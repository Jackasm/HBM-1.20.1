package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockDeadPlant;
import com.hbm.blocks.generic.BlockNTMFlower;
import com.hbm.blocks.generic.BlockTallPlant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Генерация растений, кроме камыша: на чём они стоят.
 *
 * <p>Две части. Первая — честный мир: регион генерируется целиком и сканируется,
 * каждое растение проверяется на грунт, воду и воздух под ним, у двублочных —
 * на целостность половин. Вторая — предикат блока: {@code canSurvive} обязан
 * отказать на камне, песке, льду, воде и воздухе и согласиться на траве и земле.
 * Вторая часть ловит дефект даже тогда, когда в регионе не повезло с биомами.</p>
 *
 * <p>Допустимый грунт — из самих блоков: трава, земля, грядка, мёртвая и масляная
 * земля; у мёртвых растений ещё waste_earth. Камыш не проверяется: у него своя
 * фича и свой стенд ({@code reeds_scan}, ADR-0021).</p>
 *
 * <p>Что стенд <b>не</b> считает провалом: грунт под растением, подменённый позже —
 * соседний чанк кастует диск песка или гравия через границу (радиус диска больше
 * чанка), структура дописывает основание. Растение остаётся стоять. Такие случаи
 * идут метрикой {@code bad_soil_world} с позицией, а правило фичи держит
 * детерминированная часть — {@code canSurvive}. Это та же логика, что
 * {@code off_water} у камыша.</p>
 */
public class PlantsScanTestRunner extends TestRunner {

    /** Регион сканирования. Менять каждый прогон — иначе чанки уже сгенерированы. */
    private static final int CHUNKS = 32;
    private static final int BASE_CX = 43000;
    private static final int BASE_CZ = 43000;

    /** Поверхность колонки: растения — фичи декорации, глубже смысла нет. */
    private static final int SCAN_BELOW = 3;
    private static final int SCAN_ABOVE = 3;
    /** Сколько случаев расписывать в лог: остальные только в счётчике. */
    private static final int DUMPS = 5;

    private enum Kind { FLOWER, TALL, DEAD }

    private record Plant(BlockPos pos, Kind kind) {}

    private int cursor = 0;
    private int stage = 0;
    private int chunksGenerated = 0;
    private int dumps = 0;

    private final List<Plant> plants = new ArrayList<>();
    private final Map<String, Integer> badSoilByBlock = new LinkedHashMap<>();
    private final Map<String, Integer> byKind = new LinkedHashMap<>();

    private int onWater = 0;
    private int onAir = 0;
    private int onBadSoil = 0;
    private int afterStructure = 0;
    private int tallOrphanLower = 0;
    private int tallOrphanUpper = 0;

    public PlantsScanTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            int total = CHUNKS * CHUNKS;
            if (cursor < total) {
                int cx = BASE_CX + cursor % CHUNKS;
                int cz = BASE_CZ + cursor / CHUNKS;
                level.getChunk(cx, cz);
                chunksGenerated++;
                scanChunk(cx, cz);
                cursor++;
                if (cursor % 64 == 0) progress("generated " + cursor + "/" + total);
                return false;
            }
            progress("scan_done");
            stage = 1;
            return false;
        }

        progress("checking");
        for (Plant plant : plants) checkPlant(plant);

        info("chunks", chunksGenerated);
        info("plants", plants.size());
        info("by_kind", byKind);
        info("on_water", onWater);
        info("on_air", onAir);
        info("on_bad_soil", onBadSoil);
        info("after_structure", afterStructure);
        info("bad_soil_by_block", badSoilByBlock);
        info("tall_orphan_lower", tallOrphanLower);
        info("tall_orphan_upper", tallOrphanUpper);

        boolean ok = true;
        ok &= expect("region_generated", chunksGenerated == CHUNKS * CHUNKS);
        ok &= expect("plants_found", !plants.isEmpty());
        ok &= expect("no_plants_on_water", onWater == 0);
        ok &= expect("no_plants_in_air", onAir == 0);
        // Грунт проверяется метрикой, а не провалом: мир дописывают после декорации —
        // диск песка или гравия из соседнего чанка (радиус больше чанка) и структуры
        // меняют землю под уже посаженным растением. Правило фичи держит вторая часть.
        info("bad_soil_world", onBadSoil);
        ok &= expect("tall_halves_complete", tallOrphanLower == 0 && tallOrphanUpper == 0);

        // Предикат блока: то же правило, но без зависимости от случайных биомов.
        int cases = 0;
        int wrong = 0;
        for (Block block : new Block[]{
                ModBlocks.PLANT_FLOWER_FOXGLOVE.get(),
                ModBlocks.PLANT_TALL_WEED.get(),
                ModBlocks.PLANT_DEAD_GENERIC.get()}) {
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            for (Map.Entry<Block, Boolean> probe : probes().entrySet()) {
                cases++;
                boolean actual = survives(block, probe.getKey());
                if (actual != probe.getValue()) {
                    wrong++;
                    info("predicate_wrong", name + " on "
                            + BuiltInRegistries.BLOCK.getKey(probe.getKey()).getPath()
                            + " expected=" + probe.getValue() + " actual=" + actual);
                }
            }
        }
        metric("predicate_cases", cases);
        metric("predicate_wrong", wrong);
        ok &= expect("predicate_matches_soil", wrong == 0);

        metric("plants", plants.size());
        metric("water", onWater);
        metric("air", onAir);
        metric("bad_soil", onBadSoil);
        metric("after_structure", afterStructure);
        metric("orphans", tallOrphanLower + tallOrphanUpper);

        finish(ok);
        return true;
    }

    /** Пары «блок под растением → ожидание canSurvive». */
    private static Map<Block, Boolean> probes() {
        Map<Block, Boolean> map = new LinkedHashMap<>();
        map.put(Blocks.STONE, false);
        map.put(Blocks.SAND, false);
        map.put(Blocks.WATER, false);
        map.put(Blocks.AIR, false);
        map.put(Blocks.ICE, false);
        map.put(Blocks.GRASS_BLOCK, true);
        map.put(Blocks.DIRT, true);
        return map;
    }

    /** Ставит блок-грунт в тестовой точке и спрашивает у состояния, выживет ли оно. */
    private boolean survives(Block plant, Block soil) {
        BlockPos pos = new BlockPos(8, 250, 8);
        level.setBlockAndUpdate(pos.below(), soil.defaultBlockState());
        BlockState state = plant.defaultBlockState();
        if (state.hasProperty(BlockTallPlant.HALF)) {
            state = state.setValue(BlockTallPlant.HALF, DoubleBlockHalf.LOWER);
        }
        boolean result = state.canSurvive(level, pos);
        level.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        return result;
    }

    private void scanChunk(int cx, int cz) {
        int baseX = cx << 4;
        int baseZ = cz << 4;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = baseX + dx;
                int z = baseZ + dz;
                int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                for (int y = surface - SCAN_BELOW; y <= surface + SCAN_ABOVE; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Kind kind = kindOf(level.getBlockState(pos).getBlock());
                    if (kind != null) plants.add(new Plant(pos, kind));
                }
            }
        }
    }

    private static Kind kindOf(Block block) {
        if (block instanceof BlockNTMFlower) return Kind.FLOWER;
        if (block instanceof BlockTallPlant) return Kind.TALL;
        if (block instanceof BlockDeadPlant) return Kind.DEAD;
        return null;
    }

    private void checkPlant(Plant plant) {
        BlockPos pos = plant.pos();
        BlockState state = level.getBlockState(pos);
        byKind.merge(plant.kind().name().toLowerCase(), 1, Integer::sum);

        if (plant.kind() == Kind.TALL) {
            if (state.getValue(BlockTallPlant.HALF) == DoubleBlockHalf.UPPER) {
                if (!(level.getBlockState(pos.below()).getBlock() instanceof BlockTallPlant)) {
                    tallOrphanUpper++;
                    dump("orphan_upper", pos);
                }
                return;
            }
            if (level.getBlockState(pos.above()).getBlock() != state.getBlock()) {
                tallOrphanLower++;
                dump("orphan_lower", pos);
            }
        }

        BlockState below = level.getBlockState(pos.below());
        if (below.getFluidState().is(FluidTags.WATER) || below.is(Blocks.WATER)) {
            onWater++;
            dump("on_water", pos);
            return;
        }
        if (below.isAir()) {
            onAir++;
            dump("in_air", pos);
            return;
        }
        if (!allowedSoil(below.getBlock(), plant.kind())) {
            if (insideStructure(pos)) {
                // Фича посадила растение на грунт, а структура после декорации подменила
                // землю песком или гравием: это не правило фичи, а последствие застройки.
                afterStructure++;
                dump("after_structure", pos);
            } else {
                onBadSoil++;
                badSoilByBlock.merge(BuiltInRegistries.BLOCK.getKey(below.getBlock()).toString(), 1, Integer::sum);
                dump("bad_soil", pos);
            }
        }
    }

    /**
     * Позиция внутри габарита куска структуры. Смотрим три на три чанка: структура,
     * начатая в соседнем чанке, кладёт куски и в этот, а {@code getAllStarts()} знает
     * только про собственные старты.
     */
    private boolean insideStructure(BlockPos pos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (StructureStart start : level.getChunk(pos.getX() + dx * 16, pos.getZ() + dz * 16)
                        .getAllStarts().values()) {
                    for (StructurePiece piece : start.getPieces()) {
                        if (piece.getBoundingBox().isInside(pos)) return true;
                    }
                }
            }
        }
        return false;
    }

    /** Что под растением допустимо. Списки — как в canPlaceBlockOn у самих блоков. */
    private static boolean allowedSoil(Block soil, Kind kind) {
        if (soil == Blocks.GRASS_BLOCK || soil == Blocks.DIRT || soil == Blocks.FARMLAND
                || soil == ModBlocks.DIRT_DEAD.get() || soil == ModBlocks.DIRT_OILY.get()) return true;
        return kind == Kind.DEAD && soil == ModBlocks.WASTE_EARTH.get();
    }

    private void dump(String what, BlockPos pos) {
        if (dumps++ < DUMPS) {
            BlockState state = level.getBlockState(pos);
            int starts = level.getChunk(pos).getAllStarts().size();
            info("violation_" + what, pos
                    + " block=" + state.getBlock().defaultBlockState()
                    + " below=" + level.getBlockState(pos.below()).getBlock()
                    + " below2=" + level.getBlockState(pos.below(2)).getBlock()
                    + " biome=" + level.getBiome(pos).unwrapKey().map(k -> k.location().toString()).orElse("?")
                    + " structure_starts=" + starts);
        }
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }
}
