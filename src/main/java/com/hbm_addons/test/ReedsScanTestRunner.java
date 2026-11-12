package com.hbm_addons.test;

import com.hbm.blocks.generic.BlockReeds;
import com.hbm.datagen.worldgen.feature.ReedsFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.TreeMap;

/**
 * Стенд генерации камыша (#137): глубина, близость берега, высота столбика и куртина.
 *
 * <p>Регион генерируется целиком — фичи живут только на генерации, чанк за тик.
 * {@code BASE_CX}/{@code BASE_CZ} меняются каждый прогон: на уже сгенерированных чанках
 * камыша не появится, и стенд честно покажет ноль. Регион должен содержать биомы рек
 * или пляжей, иначе проверять нечего — {@code shore_biomes_present} скажет об этом прямо.</p>
 *
 * <p>Что стенд <b>не</b> проверяет и почему: «каждый камыш стоит на воде» — неверный
 * инвариант. Фича ставит камыш только на воду, но после декорации мир дописывают
 * структуры: у деревни вода фермы и пол из дубовых досок появляются поверх, и камыш
 * остаётся на досках. Такие случаи считаются метрикой {@code off_water} с позицией
 * и составом блоков вокруг, а не провалом проверки.</p>
 */
public class ReedsScanTestRunner extends TestRunner {

    /** Регион сканирования. Менять каждый прогон — иначе чанки уже сгенерированы. */
    private static final int CHUNKS = 32;
    private static final int BASE_CX = 39000;
    private static final int BASE_CZ = 39000;

    /** Насколько широко смотрим вокруг поверхности колонки: над водой один блок, берём с запасом. */
    private static final int SCAN_BELOW = 4;
    private static final int SCAN_ABOVE = 4;
    /** Радиус поиска биома реки/пляжа вокруг растения (метрика, не проверка). */
    private static final int BIOME_SEARCH_RADIUS = 16;
    /** Сколько случаев «камыш не на воде» расписывать в лог: остальные только в счётчике. */
    private static final int OFF_WATER_DUMPS = 5;

    private int cursor = 0;
    private int stage = 0;
    private int chunksGenerated = 0;
    private int riverChunks = 0;
    private int beachChunks = 0;

    private final List<BlockPos> plants = new ArrayList<>();
    private final TreeMap<String, Integer> reedsByBiome = new TreeMap<>();

    private int deepWater = 0;
    private int stemsMissing = 0;
    private int stemsNotToBed = 0;
    private int stemsWaterlogged = 0;
    private int stemsTooDeep = 0;
    private int stemTotal = 0;
    private int stemMin = Integer.MAX_VALUE;
    private int stemMax = 0;
    private int offWater = 0;
    private int noShore = 0;
    private int shortStacks = 0;
    private int lonelyPlants = 0;
    private int clusters = 0;
    private int maxDepth = 0;
    private int biomeFar = 0;
    private int biomeDistanceMax = 0;
    private int biomeDistanceSum = 0;

    public ReedsScanTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
        ReedsFeature.Stats.reset();
        ReedsFeature.Stats.enabled = true;
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            int total = CHUNKS * CHUNKS;
            if (cursor < total) {
                int cx = BASE_CX + cursor % CHUNKS;
                int cz = BASE_CZ + cursor / CHUNKS;
                // Полная генерация: без неё фичи не выполняются и камыша в чанке нет.
                level.getChunk(cx, cz);
                chunksGenerated++;
                scanChunk(cx, cz);
                cursor++;
                if (cursor % 64 == 0) {
                    progress("generated " + cursor + "/" + total);
                }
                return false;
            }
            progress("scan_done");
            info("river_chunks", riverChunks);
            info("beach_chunks", beachChunks);
            info("reeds_found", plants.size());
            stage = 1;
            return false;
        }

        progress("checking");
        ReedsFeature.Stats.enabled = false;
        for (BlockPos pos : plants) {
            checkPlant(pos);
        }
        checkClusters();

        int biomeDistanceAvg = plants.isEmpty() ? 0 : biomeDistanceSum / plants.size();
        info("plants", plants.size());
        info("clusters", clusters);
        info("max_depth", maxDepth);
        info("deep_water", deepWater);
        info("off_water", offWater);
        info("no_shore", noShore);
        info("short_stacks", shortStacks);
        info("stems_total", stemTotal);
        info("stems_min", stemMin == Integer.MAX_VALUE ? 0 : stemMin);
        info("stems_max", stemMax);
        info("stems_missing", stemsMissing);
        info("stems_not_to_bed", stemsNotToBed);
        info("stems_not_waterlogged", stemsWaterlogged);
        info("stems_too_deep", stemsTooDeep);
        info("lonely_in_world", lonelyPlants);
        info("events", ReedsFeature.Stats.events);
        info("events_with_stand", ReedsFeature.Stats.eventsWithStand);
        info("min_plants_per_event", ReedsFeature.Stats.minPlantsPerEvent);
        info("plants_placed", ReedsFeature.Stats.plantsPlaced);
        info("max_depth_at_placement", ReedsFeature.Stats.maxDepthSeen);
        info("reeds_by_biome", reedsByBiome);
        info("biome_distance_max", biomeDistanceMax);
        info("biome_distance_avg", biomeDistanceAvg);
        info("biome_far", biomeFar);

        boolean ok = true;
        ok &= expect("region_generated", chunksGenerated == CHUNKS * CHUNKS);
        ok &= expect("shore_biomes_present", riverChunks + beachChunks > 0);
        ok &= expect("reeds_found", !plants.isEmpty());
        ok &= expect("no_deep_water", deepWater == 0);
        ok &= expect("all_near_shore", noShore == 0);
        ok &= expect("stacks_full_height", shortStacks == 0);
        ok &= expect("stems_present", stemsMissing == 0);
        ok &= expect("stems_to_bed", stemsNotToBed == 0);
        ok &= expect("stems_waterlogged", stemsWaterlogged == 0);
        ok &= expect("stems_bounded", stemsTooDeep == 0);
        // Куртина проверяется на генераторе, а не в готовом мире: одиночка в мире может
        // остаться и по честной причине — партнёра убрала структура после декорации.
        ok &= expect("events_have_stand", ReedsFeature.Stats.eventsWithStand > 0);
        ok &= expect("no_single_plant_events",
                ReedsFeature.Stats.minPlantsPerEvent >= ReedsFeature.MIN_PLANTS);
        ok &= expect("placement_depth_bounded",
                ReedsFeature.Stats.maxDepthSeen <= ReedsFeature.MAX_DEPTH);

        metric("chunks", chunksGenerated);
        metric("river", riverChunks);
        metric("beach", beachChunks);
        metric("plants", plants.size());
        metric("clusters", clusters);
        metric("deep", deepWater);
        metric("off_water", offWater);
        metric("no_shore", noShore);
        metric("short", shortStacks);
        metric("stems", stemTotal);
        metric("stem_min", stemMin == Integer.MAX_VALUE ? 0 : stemMin);
        metric("stem_max", stemMax);
        metric("stems_not_to_bed", stemsNotToBed);
        metric("stems_not_waterlogged", stemsWaterlogged);
        metric("lonely_in_world", lonelyPlants);
        metric("events", ReedsFeature.Stats.events);
        metric("stands", ReedsFeature.Stats.eventsWithStand);
        metric("min_per_event", ReedsFeature.Stats.minPlantsPerEvent);
        metric("placed", ReedsFeature.Stats.plantsPlaced);
        metric("max_depth", maxDepth);

        finish(ok);
        return true;
    }

    /** Ищем низ камыша в полосе вокруг поверхности колонки. */
    private void scanChunk(int cx, int cz) {
        int baseX = cx << 4;
        int baseZ = cz << 4;
        int centerY = level.getHeight(Heightmap.Types.WORLD_SURFACE, baseX + 8, baseZ + 8);
        Holder<Biome> centerBiome = level.getBiome(new BlockPos(baseX + 8, centerY, baseZ + 8));
        if (centerBiome.is(BiomeTags.IS_RIVER)) riverChunks++;
        if (centerBiome.is(BiomeTags.IS_BEACH)) beachChunks++;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = baseX + dx;
                int z = baseZ + dz;
                int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                for (int y = surface - SCAN_BELOW; y <= surface + SCAN_ABOVE; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof BlockReeds reeds
                            && reeds.getType() == BlockReeds.TYPE_TOP) {
                        plants.add(pos);
                    }
                }
            }
        }
    }

    private void checkPlant(BlockPos pos) {
        int depth = ReedsFeature.waterDepth(level, pos);
        maxDepth = Math.max(maxDepth, depth);
        boolean onWater = level.getBlockState(pos.below()).getFluidState().is(FluidTags.WATER);

        if (!onWater) {
            // Камыш стоял на воде при генерации, но мир дописали: деревня, чистка декора,
            // другая фича. Это не правило фичи, поэтому метрика с разбором, а не провал.
            offWater++;
            if (offWater <= OFF_WATER_DUMPS) {
                info("off_water_at", pos + " below=" + level.getBlockState(pos.below()));
                info("off_water_context", contextAround(pos));
            }
        } else if (depth > ReedsFeature.MAX_DEPTH) {
            deepWater++;
            info("violation_deep", pos + " depth=" + depth);
        }

        if (!ReedsFeature.hasShoreNearby(level, pos)) {
            noShore++;
            info("violation_no_shore", pos.toString());
        }

        // Подводная часть: стебли водонаполнены и доходят до грунта (ADR-0021).
        int stems = 0;
        BlockState stemState = level.getBlockState(pos.below());
        while (stemState.getBlock() instanceof BlockReeds) {
            stems++;
            if (!stemState.getValue(BlockReeds.WATERLOGGED)) stemsWaterlogged++;
            stemState = level.getBlockState(pos.below(stems + 1));
        }
        if (stems == 0) stemsMissing++;
        if (!stemState.isSolid()) stemsNotToBed++;
        if (stems > ReedsFeature.MAX_DEPTH) stemsTooDeep++;
        stemTotal += stems;
        stemMin = Math.min(stemMin, stems);
        stemMax = Math.max(stemMax, stems);

        int height = 0;
        for (int i = 0; i < ReedsFeature.HEIGHT; i++) {
            if (level.getBlockState(pos.above(i)).getBlock() instanceof BlockReeds) {
                height++;
            } else {
                break;
            }
        }
        if (height < ReedsFeature.HEIGHT) shortStacks++;

        // Биом самой колонки над водой не показателен: в 1.20 биом сэмплится по 4×4, и у кромки
        // воды это уже соседний лес или саванна. Биом — повод запустить фичу, поэтому расстояние
        // до биома реки/пляжа пишем метрикой, а не проверкой.
        Holder<Biome> biome = level.getBiome(pos);
        reedsByBiome.merge(biome.unwrapKey().map(k -> k.location().toString()).orElse("unknown"), 1, Integer::sum);
        int biomeDistance = shoreBiomeDistance(pos);
        biomeDistanceSum += biomeDistance;
        biomeDistanceMax = Math.max(biomeDistanceMax, biomeDistance);
        if (biomeDistance > ReedsFeature.SHORE_RADIUS) biomeFar++;
    }

    /** Что стоит вокруг позиции: по составу блоков видно, чья это постройка. */
    private String contextAround(BlockPos pos) {
        TreeMap<String, Integer> counts = new TreeMap<>();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -5; dz <= 5; dz++) {
                    BlockState state = level.getBlockState(pos.offset(dx, dy, dz));
                    if (state.isAir()) continue;
                    String name = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
                    counts.merge(name, 1, Integer::sum);
                }
            }
        }
        return counts.toString();
    }

    /** Расстояние Чебышёва до ближайшей колонки с биомом реки или пляжа. */
    private int shoreBiomeDistance(BlockPos pos) {
        for (int radius = 0; radius <= BIOME_SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                    int x = pos.getX() + dx;
                    int z = pos.getZ() + dz;
                    int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                    Holder<Biome> biome = level.getBiome(new BlockPos(x, y, z));
                    if (biome.is(BiomeTags.IS_RIVER) || biome.is(BiomeTags.IS_BEACH)) return radius;
                }
            }
        }
        return BIOME_SEARCH_RADIUS;
    }

    /** Куртины: связные группы растений; пара из одного события разведена не больше чем на диаметр. */
    private void checkClusters() {
        List<BlockPos> unvisited = new ArrayList<>(plants);
        while (!unvisited.isEmpty()) {
            BlockPos seed = unvisited.remove(0);
            Deque<BlockPos> queue = new ArrayDeque<>();
            queue.add(seed);
            int size = 0;
            while (!queue.isEmpty()) {
                BlockPos current = queue.poll();
                size++;
                for (int i = unvisited.size() - 1; i >= 0; i--) {
                    if (near(current, unvisited.get(i))) {
                        queue.add(unvisited.remove(i));
                    }
                }
            }
            clusters++;
            if (size < ReedsFeature.MIN_PLANTS) lonelyPlants++;
        }
    }

    private boolean near(BlockPos a, BlockPos b) {
        // Попытки берутся в радиусе CLUSTER_RADIUS от общего центра, значит два растения одного
        // события могут стоять на 2 × CLUSTER_RADIUS. Меньший радиус склеивал бы половинки
        // одной куртины и объявлял их одиночками.
        return Math.abs(a.getX() - b.getX()) <= ReedsFeature.CLUSTER_RADIUS * 2
                && Math.abs(a.getZ() - b.getZ()) <= ReedsFeature.CLUSTER_RADIUS * 2;
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }
}
