package com.hbm.datagen.worldgen.structure;

import com.hbm.config.StructureConfig;
import com.hbm.datagen.worldgen.structure.pieces.ConformingNBTStructurePiece;
import com.hbm.datagen.worldgen.structure.pieces.NBTStructurePiece;
import com.hbm.world.generator.WorldgenStats;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static com.hbm.util.ResLocation.ResLocation;

public class NBTStructure extends Structure {
    public final StructureSettings settings;

    public static final Codec<NBTStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    StructureSettings.CODEC.fieldOf("settings").forGetter(s -> s.settings),
                    Codec.STRING.fieldOf("nbt_file").forGetter(s -> s.nbtFile),
                    Codec.INT.optionalFieldOf("height_offset", 0).forGetter(s -> s.heightOffset),
                    Codec.INT.optionalFieldOf("min_height", 1).forGetter(s -> s.minHeight),
                    Codec.INT.optionalFieldOf("max_height", 128).forGetter(s -> s.maxHeight),
                    // -1 — случайный поворот, 0..3 — фиксированный
                    Codec.INT.optionalFieldOf("rotation", -1).forGetter(s -> s.rotation),
                    // true — руины повторяют рельеф по колонкам (порт conformToTerrain 1.7.10)
                    Codec.BOOL.optionalFieldOf("conform", false).forGetter(s -> s.conform)
            ).apply(instance, NBTStructure::new)
    );

    private final String nbtFile;
    private final int heightOffset;
    private final int minHeight;
    private final int maxHeight;
    private final int rotation;
    private final boolean conform;

    /** Возврат {@link #queryHeight}, когда высоту получить не удалось. */
    private static final int NO_HEIGHT = Integer.MIN_VALUE;

    /** Сетка проб по подошве: не больше 5×5, стоимость не зависит от шаблона (#134). */
    private static final int GRID_MAX = 5;

    private static final Set<String> WATER_STRUCTURES = Set.of(
            "beached_patrol",
            "lighthouse",
            "aircraft_carrier",
            "oil_rig",
            "submarine"
    );

    /**
     * Структуры, которым проверка ровности подошвы не указ: у них «висящие»
     * колонки — часть формы, а не зазор под низким краем. Разрешение то же,
     * что было у всех крупных структур до #159.
     */
    private static final Set<String> SLOPE_EXEMPT = Set.of(
            "spaceship"
    );

    public NBTStructure(StructureSettings settings,
                        String nbtFile,
                        int heightOffset,
                        int minHeight,
                        int maxHeight,
                        int rotation,
                        boolean conform) {
        super(settings);
        this.settings = settings;
        this.nbtFile = nbtFile;
        this.heightOffset = heightOffset;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.rotation = rotation;
        this.conform = conform;
    }

    private boolean isWaterStructure() {
        return WATER_STRUCTURES.contains(nbtFile);
    }

    /** Шаблон структуры: стенд связывает с ним счётчики вызовов высоты. */
    public String nbtFile() {
        return nbtFile;
    }

    /** {@code conform} — руины ставятся по колонкам (ADR-0015), а не от одной высоты. */
    public boolean conformsToTerrain() {
        return conform;
    }

    /**
     * Стенд меряет поиск структуры целиком: время и число вызовов высоты рельефа.
     * Вне стенда счётчики выключены и {@code Optional} считается напрямую.
     */
    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        if (!WorldgenStats.isEnabled()) {
            return findGenerationPointMeasured(context);
        }
        long start = System.nanoTime();
        long heightCallsBefore = WorldgenStats.calls(WorldgenStats.BASE_HEIGHT);
        try {
            return findGenerationPointMeasured(context);
        } finally {
            WorldgenStats.add("find.NBTStructure", System.nanoTime() - start);
            WorldgenStats.recordFind(nbtFile,
                    WorldgenStats.calls(WorldgenStats.BASE_HEIGHT) - heightCallsBefore);
        }
    }

    private @NotNull Optional<GenerationStub> findGenerationPointMeasured(@NotNull GenerationContext context) {
        // Выключатели из hbm-structures.toml: в 1.20.1 вес структуры зашит в
        // structure_set, поэтому «вес 0» из эталона здесь — пустой Optional.
        if (!StructureConfig.isGenerationAllowed(nbtFile)) {
            return Optional.empty();
        }

        // Дешёвый отсев до выборки подошвы (#157): биом проверяется одним запросом
        // высоты, а не 9–25 пробами по сетке. Приёмка не меняется — не тот биом,
        // значит структура и так не встала бы; но при `locate` на редкой структуре
        // (вес 1 в наборе) кандидатов тысячи, и каждый отсеянный экономит ~30–50
        // запросов рельефа.
        Heightmap.Types biomeHeightmap = isWaterStructure()
                ? Heightmap.Types.OCEAN_FLOOR_WG
                : Heightmap.Types.WORLD_SURFACE_WG;
        int biomeX = context.chunkPos().getMiddleBlockX();
        int biomeZ = context.chunkPos().getMiddleBlockZ();
        int biomeY = context.chunkGenerator().getBaseHeight(
                biomeX, biomeZ, biomeHeightmap, context.heightAccessor(), context.randomState());
        Holder<Biome> chunkBiome = context.chunkGenerator().getBiomeSource().getNoiseBiome(
                QuartPos.fromBlock(biomeX), QuartPos.fromBlock(biomeY), QuartPos.fromBlock(biomeZ),
                context.randomState().sampler());
        if (!settings.biomes().contains(chunkBiome)) {
            return Optional.empty();
        }

        StructureTemplateManager manager = context.structureTemplateManager();
        StructureTemplate template = manager.getOrCreate(ResLocation(nbtFile));

        if (template.getSize().equals(BlockPos.ZERO)) {
            System.err.println("[HBM][Find] FAILED template: " + nbtFile);
            return Optional.empty();
        }

        int sizeX = template.getSize().getX();
        int sizeZ = template.getSize().getZ();

        // Оригинал: руины не спавнились в сухих биомах (canSpawnLightningBolt == false)
        if (nbtFile.startsWith("ntmruins")) {
            Holder<Biome> biomeHolder = context.chunkGenerator().getBiomeSource().getNoiseBiome(
                    QuartPos.fromBlock(context.chunkPos().getMinBlockX()),
                    QuartPos.fromBlock(64),
                    QuartPos.fromBlock(context.chunkPos().getMinBlockZ()),
                    context.randomState().sampler());
            if (!biomeHolder.value().hasPrecipitation()) {
                return Optional.empty();
            }
        }

        ChunkPos chunkPos = context.chunkPos();
        WorldgenRandom random = context.random();

        // Для конформных руин поворот выбирается до проверки площадки: нечётный
        // поворот меняет местами X и Z подошвы.
        int fixedRotation = this.rotation;
        if (fixedRotation < 0 && this.conform) fixedRotation = random.nextInt(4);
        boolean swapped = fixedRotation >= 0 && fixedRotation % 2 == 1;
        int placedX = swapped ? sizeZ : sizeX;
        int placedZ = swapped ? sizeX : sizeZ;

        boolean water = isWaterStructure();
        Heightmap.Types heightmap = water
                ? Heightmap.Types.OCEAN_FLOOR_WG
                : Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        for (int attempt = 0; attempt < 2; attempt++) {
            int x = chunkPos.getMinBlockX() + random.nextInt(16);
            int z = chunkPos.getMinBlockZ() + random.nextInt(16);

            // Одна выборка на попытку: сетка 9…25 проб, а не обход всей подошвы.
            // Из неё же берутся средняя, минимум, твёрдость, вода и перепад —
            // раньше это были три отдельных прохода по подошве (#134).
            Footprint footprint = sampleFootprint(context, x, z, placedX, placedZ, heightmap, water);
            if (footprint == null) continue;

            // Высота постановки — средняя по подошве, как getAverageHeight в эталоне
            // (ref:world/gen/nbt/NBTStructure.java:977-992): одна колонка давала на
            // склоне случайное заглубление. Если под структуру подсыпается грунт
            // (terrain_adaptation != none), берём минимум: Beardifier поднимет землю
            // до основания, и на высокой стороне склона структура не будет засыпана.
            int baseY = settings.terrainAdaptation() != TerrainAdjustment.NONE
                    ? footprint.min
                    : footprint.average;

            int y = baseY + heightOffset;
            y = Math.min(Math.max(y, minHeight), maxHeight);

            BlockPos pos = new BlockPos(x, y, z);

            if (isSpawnable(pos, placedX, placedZ, footprint, water, this.conform)) {
                if (this.conform) {
                    int[] heights = footprintHeights(context, x, z, placedX, placedZ, heightmap, baseY);
                    final int rotation = fixedRotation;
                    return Optional.of(new GenerationStub(pos, (builder) ->
                            builder.addPiece(new ConformingNBTStructurePiece(
                                    manager, nbtFile, pos, rotation, heightOffset, placedX, placedZ, heights))
                    ));
                }
                final int rotation = this.rotation >= 0 ? this.rotation : random.nextInt(4);
                return Optional.of(new GenerationStub(pos, (builder) ->
                        builder.addPiece(new NBTStructurePiece(manager, nbtFile, pos, rotation))
                ));
            }
        }

        return Optional.empty();
    }

    /**
     * Одна точка высоты рельефа. Единственное место, где структура спрашивает
     * {@code getBaseHeight}: стенд считает вызовы здесь, а не в пяти местах.
     */
    private static int queryHeight(GenerationContext context, int x, int z, Heightmap.Types heightmap) {
        if (!WorldgenStats.isEnabled()) {
            try {
                return context.chunkGenerator().getBaseHeight(
                        x, z, heightmap, context.heightAccessor(), context.randomState());
            } catch (Exception ignored) {
                return NO_HEIGHT;
            }
        }
        long start = System.nanoTime();
        try {
            return context.chunkGenerator().getBaseHeight(
                    x, z, heightmap, context.heightAccessor(), context.randomState());
        } catch (Exception ignored) {
            return NO_HEIGHT;
        } finally {
            WorldgenStats.add(WorldgenStats.BASE_HEIGHT, System.nanoTime() - start);
        }
    }

    /**
     * Выборка рельефа по подошве: всё, что поиску нужно знать о площадке.
     *
     * <p>Считается один раз на попытку. Раньше средняя, минимум, твёрдость и
     * перепад считались тремя отдельными проходами, а средняя — по каждой
     * колонке подошвы: до 4096 вызовов {@code getBaseHeight} на попытку,
     * и каждый вызов пересчитывает полный шумовой столб (~1,5 мс). Это было
     * 80–97% времени генерации чанка (#134).</p>
     */
    private static final class Footprint {
        /** Высота рельефа в опорной колонке (x, z) — от неё считаются проверки. */
        final int surfaceY;
        final int average;
        final int min;
        final int max;
        /** Сколько проб стоит на грунте и сколько их всего. */
        final int solid;
        final int solidTotal;
        /** В опорной колонке над грунтом жидкость (наземным структурам нельзя). */
        final boolean waterAtSurface;

        Footprint(int surfaceY, int average, int min, int max,
                  int solid, int solidTotal, boolean waterAtSurface) {
            this.surfaceY = surfaceY;
            this.average = average;
            this.min = min;
            this.max = max;
            this.solid = solid;
            this.solidTotal = solidTotal;
            this.waterAtSurface = waterAtSurface;
        }
    }

    /** Шагов сетки по оси: от 3 до 5 — углы, середины сторон и нутро в выборке. */
    private static int gridSteps(int size) {
        return Math.max(3, Math.min(GRID_MAX, Math.round(size / 8.0f)));
    }

    /** Смещение пробы от начала подошвы: первая на краю, последняя на дальнем краю. */
    private static int gridOffset(int size, int index, int steps) {
        if (steps <= 1 || size <= 1) return 0;
        return (size - 1) * index / (steps - 1);
    }

    /**
     * Одна выборка рельефа на попытку постановки: сетка 9…25 проб.
     * Средняя, минимум, максимум, твёрдость и вода берутся из этой же выборки,
     * поэтому стоимость перестаёт зависеть от размера шаблона (#134).
     *
     * <p>Возвращает {@code null}, если не удалось получить ни одной высоты.</p>
     */
    private Footprint sampleFootprint(GenerationContext context, int x, int z,
                                      int sizeX, int sizeZ, Heightmap.Types heightmap,
                                      boolean water) {
        int stepsX = gridSteps(sizeX);
        int stepsZ = gridSteps(sizeZ);
        int[] heights = new int[stepsX * stepsZ];
        Arrays.fill(heights, NO_HEIGHT);

        long total = 0;
        int count = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < stepsX; i++) {
            int px = x + gridOffset(sizeX, i, stepsX);
            for (int j = 0; j < stepsZ; j++) {
                int pz = z + gridOffset(sizeZ, j, stepsZ);
                int h = queryHeight(context, px, pz, heightmap);
                heights[i * stepsZ + j] = h;
                if (h == NO_HEIGHT) continue;
                total += h;
                count++;
                if (h < min) min = h;
                if (h > max) max = h;
            }
        }
        if (count == 0) return null;

        int average = (int) (total / count);
        int surfaceY = heights[0] == NO_HEIGHT ? average : heights[0];

        // Твёрдость — по всей выборке: точка опирается, если рельеф в ней не ниже
        // плоскости постановки (то же, что «блок на surfaceY − 1 твёрдый», но без
        // getBaseColumn). Углы в одиночку врали: на дюнах они ниже плоскости,
        // а вся подошва при этом стоит на грунте.
        int solid = 0;
        int solidTotal = 0;
        for (int h : heights) {
            if (h == NO_HEIGHT) continue;
            solidTotal++;
            if (h >= surfaceY) solid++;
        }

        // Жидкость в опорной колонке — разница высот рельефа и грунта без воды.
        // Этой проверке нужен ровно один лишний вызов на попытку.
        boolean waterAtSurface = false;
        if (!water) {
            int ground = queryHeight(context, x, z, Heightmap.Types.OCEAN_FLOOR_WG);
            waterAtSurface = ground != NO_HEIGHT && surfaceY > ground;
        }

        return new Footprint(surfaceY, average, min, max, solid, solidTotal, waterAtSurface);
    }

    /**
     * Высоты рельефа по каждой колонке подошвы — для конформного куска: он
     * ставит колонку на её собственную высоту. Массив идёт по X, внутри по Z.
     * Заодно задаёт границы куска, чтобы сдвинутые блоки не выпали из бокса.
     */
    private int[] footprintHeights(GenerationContext context, int x, int z,
                                   int sizeX, int sizeZ, Heightmap.Types heightmap, int fallback) {
        int[] heights = new int[sizeX * sizeZ];
        for (int px = 0; px < sizeX; px++) {
            for (int pz = 0; pz < sizeZ; pz++) {
                int h = queryHeight(context, x + px, z + pz, heightmap);
                heights[px * sizeZ + pz] = h == NO_HEIGHT ? fallback : h;
            }
        }
        return heights;
    }

    /**
     * Проверка, что структура поместится:
     *   1) в опорной колонке над грунтом не жидкость (для наземных);
     *   2) опора есть в достаточной доле проб выборки;
     *   3) перепад высот по выборке не больше допустимого.
     *
     * <p>Все три проверки идут по одной выборке {@link Footprint}: столбцов
     * через {@code getBaseColumn} больше нет, как и второго прохода за высотами.</p>
     */
    private boolean isSpawnable(BlockPos pos, int sizeX, int sizeZ,
                                Footprint footprint, boolean water, boolean conform) {
        // 1) в опорной колонке над грунтом не жидкость (для наземных)
        if (!water && footprint.waterAtSurface) return false;

        boolean large = sizeX > 32 || sizeZ > 32;

        // 2) опора под подошвой: та же шкала порогов, но считается по выборке
        double requiredSolid = large ? 0.5
                : (sizeX < 16 && sizeZ < 16 ? 0.8 : (sizeX < 32 && sizeZ < 32 ? 0.78 : 0.75));
        if (footprint.solid < footprint.solidTotal * requiredSolid) return false;

        // 3) перепад высот по выборке. Конформные руины ставятся по колонкам,
        // поэтому склон им не помеха — проверка для них не нужна.
        if (!water && !conform) {
            if (footprint.max - footprint.min > maxSlopeFor(nbtFile, sizeX, sizeZ)) return false;
        }

        return true;
    }

    /**
     * Допустимый перепад рельефа под подошвой плоскодонной постройки.
     *
     * <p>Раньше структура шире 32 блоков допускала перепад 32 блока: это исключение
     * вводилось ради плоских платформ атомки и корабля, но под него попали и обычные
     * корпуса. Фабрика (38×19) на склоне проходит такую проверку, встаёт по средней
     * высоте подошвы и висит над низким краем на 5–6 блоков (#159).</p>
     *
     * <p>Для плоскодонной постройки перепад — это и есть зазор под низким краем
     * (плюс половина перепада под высоким), поэтому предел растёт вместе с подошвой
     * медленно: восьмая часть большей стороны, от 4 до 8 блоков. Пороги мелких
     * структур (3/4/5) не меняются: там подошва мала и проверка уже строгая.</p>
     */
    private static int maxSlopeFor(String nbtFile, int sizeX, int sizeZ) {
        // Корабль — не здание с плоской подошвой: корпус уходит ниже поверхности
        // (height_offset −3), «висящие» колонки у него — обводы корпуса, а не зазор
        // под низким краем. В эталоне он тоже ставился без проверки ровности.
        if (SLOPE_EXEMPT.contains(nbtFile)) {
            return 32;
        }
        if (sizeX > 32 || sizeZ > 32) {
            return Math.max(4, Math.min(8, Math.max(sizeX, sizeZ) / 8));
        }
        return sizeX < 16 && sizeZ < 16 ? 3 : (sizeX < 32 && sizeZ < 32 ? 4 : 5);
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.SIMPLE_NBT.get();
    }
}
