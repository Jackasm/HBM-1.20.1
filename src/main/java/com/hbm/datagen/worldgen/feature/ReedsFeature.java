package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockReeds;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Камыш: куртина на мелководье у берега.
 *
 * <p>1.7.10 ставил камыш на любую воду (проверялось только «под блоком вода»), а нужный
 * вид давал рендер: {@code RenderReeds} считал глубину под растением и рисовал столбик
 * вниз до дна, поэтому камыш читался как заросли на мелководье. Порт когда-то перенёс
 * рендер как три блока, но потерял и высоту, и группу — остался одиночный блок, висящий
 * на воде посреди водоёма (см. карточку #137). Теперь над водой снова один блок.</p>
 *
 * <p>Здесь правило задано явно: только мелководье не глубже {@link #MAX_DEPTH}, только
 * рядом с сушей не дальше {@link #SHORE_RADIUS}, только куртиной от {@link #MIN_PLANTS}
 * растений, каждое — один надводный блок. Подводную часть 1.7.10
 * рисовал в рендере; здесь она ставится водонаполненными блоками от поверхности воды до
 * грунта (ADR-0021): вода в клетках остаётся, стебель стоит на дне. Глубину и близость
 * берега эталон не проверял вовсе — это осознанное отличие.</p>
 */
public class ReedsFeature extends Feature<NoneFeatureConfiguration> {

    /** Максимальная глубина воды под растением. Глубже — камыш не ставится. */
    public static final int MAX_DEPTH = 2;
    /** Радиус, в котором должна найтись суша: выше — уже не берег. */
    public static final int SHORE_RADIUS = 4;
    /** Разброс куртины вокруг центра. */
    public static final int CLUSTER_RADIUS = 3;
    /** Попыток на одну куртину. */
    public static final int ATTEMPTS = 8;
    /** Меньше этого числа растений куртина не ставится: одиночный камыш выглядит поломкой. */
    public static final int MIN_PLANTS = 2;
    /** Надводная часть — один блок, как в 1.7.10; подводные стебли не в счёт. */
    public static final int HEIGHT = 1;
    /** Насколько далеко от исходной точки ищем воду, если точка пришлась на сушу. */
    private static final int CENTER_SEARCH_RADIUS = 4;
    /** Насколько ниже уровня воды смотрим в окне проверки берега: дно мелководья должно попасть. */
    private static final int SHORE_DEPTH = MAX_DEPTH + 1;
    /** Насколько выше уровня воды может быть суша у берега. */
    private static final int SHORE_HEIGHT = 2;

    public ReedsFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        boolean collect = Stats.enabled;
        if (collect) Stats.events++;

        BlockPos center = findClusterCenter(level, origin);
        if (center == null) return false;

        List<BlockPos> plants = new ArrayList<>();
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            int x = center.getX() + random.nextInt(CLUSTER_RADIUS * 2 + 1) - CLUSTER_RADIUS;
            int z = center.getZ() + random.nextInt(CLUSTER_RADIUS * 2 + 1) - CLUSTER_RADIUS;
            BlockPos pos = waterSurfaceAt(level, x, z);
            if (pos == null) continue;
            if (plants.contains(pos)) continue;
            int depth = waterDepth(level, pos);
            if (depth < 1 || depth > MAX_DEPTH) continue;
            if (!level.getBlockState(pos.below(depth + 1)).isSolid()) continue;
            if (!hasShoreNearby(level, pos)) continue;
            if (!canPlantAt(level, pos)) continue;
            plants.add(pos);
            if (collect) Stats.maxDepthSeen = Math.max(Stats.maxDepthSeen, depth);
        }

        if (plants.size() < MIN_PLANTS) return false;

        for (BlockPos pos : plants) {
            placeReed(level, pos);
        }
        if (collect) {
            Stats.eventsWithStand++;
            Stats.plantsPlaced += plants.size();
            Stats.minPlantsPerEvent = Math.min(Stats.minPlantsPerEvent, plants.size());
        }
        return true;
    }

    /**
     * Счётчики для стенда: сколько событий и сколько растений в куртине получилось.
     *
     * <p>Гарантия «в куртине не меньше {@link #MIN_PLANTS} растений» проверяется здесь, в момент
     * постановки: в готовом мире одиночка может остаться и по честной причине — партнёра убрала
     * структура или другая фича после декорации. Вне прогона стенда счётчики выключены:
     * одна проверка флага на событие.</p>
     */
    public static final class Stats {
        public static boolean enabled = false;
        public static int events = 0;
        public static int eventsWithStand = 0;
        public static int plantsPlaced = 0;
        public static int minPlantsPerEvent = Integer.MAX_VALUE;
        public static int maxDepthSeen = 0;

        private Stats() {
        }

        public static void reset() {
            events = 0;
            eventsWithStand = 0;
            plantsPlaced = 0;
            minPlantsPerEvent = Integer.MAX_VALUE;
            maxDepthSeen = 0;
        }
    }

    /** Центр куртины: сама точка постановки, если она подходит, иначе ближайшая подходящая рядом. */
    private BlockPos findClusterCenter(WorldGenLevel level, BlockPos origin) {
        if (isShallowWaterNearShore(level, origin)) return origin;

        BlockPos best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (int dx = -CENTER_SEARCH_RADIUS; dx <= CENTER_SEARCH_RADIUS; dx++) {
            for (int dz = -CENTER_SEARCH_RADIUS; dz <= CENTER_SEARCH_RADIUS; dz++) {
                int distance = Math.abs(dx) + Math.abs(dz);
                if (distance >= bestDistance) continue;
                BlockPos candidate = waterSurfaceAt(level, origin.getX() + dx, origin.getZ() + dz);
                if (candidate == null) continue;
                if (!isShallow(level, candidate)) continue;
                if (!hasShoreNearby(level, candidate)) continue;
                best = candidate;
                bestDistance = distance;
            }
        }
        return best;
    }

    private boolean isShallowWaterNearShore(WorldGenLevel level, BlockPos pos) {
        BlockPos surface = waterSurfaceAt(level, pos.getX(), pos.getZ());
        if (surface == null) return false;
        return isShallow(level, surface) && hasShoreNearby(level, surface);
    }

    /** Позиция над водой в этой колонке или {@code null}, если колонка не водная. */
    private BlockPos waterSurfaceAt(LevelReader level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        if (!level.getBlockState(pos).isAir()) return null;
        if (!isWater(level, pos.below())) return null;
        return pos;
    }

    /** Вода под растением не глубже {@link #MAX_DEPTH}, и на дне твёрдый блок, а не пустота. */
    private boolean isShallow(LevelReader level, BlockPos pos) {
        int depth = waterDepth(level, pos);
        if (depth < 1 || depth > MAX_DEPTH) return false;
        return level.getBlockState(pos.below(depth + 1)).isSolid();
    }

    /** Сколько блоков воды под позицией (счёт останавливается на {@link #MAX_DEPTH} + 1). */
    public static int waterDepth(LevelReader level, BlockPos pos) {
        int depth = 0;
        while (depth <= MAX_DEPTH) {
            if (!isWater(level, pos.below(depth + 1))) break;
            depth++;
        }
        return depth;
    }

    /**
     * Суша в радиусе {@link #SHORE_RADIUS}: колонка, где в окне высот есть твёрдый блок и нет воды.
     *
     * <p>Считается по блокам, а не по карте высот: {@code WORLD_SURFACE} учитывает листву и прочий
     * непустой декор, поэтому дерево на берегу «поднимает» берег на 5–7 блоков, и проверка
     * перестаёт видеть сушу. Блоки дают одно и то же число и на генерации, и после неё.</p>
     */
    public static boolean hasShoreNearby(LevelReader level, BlockPos pos) {
        for (int dx = -SHORE_RADIUS; dx <= SHORE_RADIUS; dx++) {
            for (int dz = -SHORE_RADIUS; dz <= SHORE_RADIUS; dz++) {
                if (dx == 0 && dz == 0) continue;
                int x = pos.getX() + dx;
                int z = pos.getZ() + dz;
                boolean water = false;
                boolean solid = false;
                for (int y = pos.getY() - SHORE_DEPTH; y <= pos.getY() + SHORE_HEIGHT; y++) {
                    BlockPos probe = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(probe);
                    if (state.getFluidState().is(FluidTags.WATER)) {
                        water = true;
                    } else if (state.isSolid()) {
                        solid = true;
                    }
                }
                if (solid && !water) return true;
            }
        }
        return false;
    }

    private static boolean isWater(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).getFluidState().is(FluidTags.WATER);
    }

    /**
     * Проверка до постановки: под низом вода не глубже {@link #MAX_DEPTH} и твёрдое дно,
     * подводные клетки заняты водой, надводные — воздух, всё доступно на запись.
     */
    private boolean canPlantAt(WorldGenLevel level, BlockPos surface) {
        int depth = waterDepth(level, surface);
        if (depth < 1 || depth > MAX_DEPTH) return false;
        if (!level.getBlockState(surface.below(depth + 1)).isSolid()) return false;
        for (int i = 1; i <= depth; i++) {
            BlockPos pos = surface.below(i);
            if (!isWater(level, pos)) return false;
            if (!level.ensureCanWrite(pos)) return false;
        }
        for (int i = 0; i < HEIGHT; i++) {
            BlockPos pos = surface.above(i);
            if (!level.getBlockState(pos).isAir()) return false;
            if (!level.ensureCanWrite(pos)) return false;
        }
        return true;
    }

    /**
     * Столбик целиком: водонаполненные стебли от грунта вверх до поверхности воды,
     * затем один {@code top} над водой.
     *
     * <p>Подводная часть ставится снизу вверх, чтобы каждый следующий блок опирался
     * на уже поставленный. Все подводные стебли — {@code mid}; надводный блок —
     * {@code top}, по нему стенд и считает растения. Самый нижний стебель стоит
     * на грунте — для него у {@code mid} есть отдельное правило.</p>
     */
    private void placeReed(WorldGenLevel level, BlockPos surface) {
        int depth = waterDepth(level, surface);

        for (int i = depth; i >= 1; i--) {
            BlockPos pos = surface.below(i);
            BlockState state = ModBlocks.REEDS_MID.get().defaultBlockState()
                    .setValue(BlockReeds.WATERLOGGED, true);
            if (!level.ensureCanWrite(pos)) return;
            level.setBlock(pos, state, 2);
        }

        for (int i = 0; i < HEIGHT; i++) {
            BlockPos pos = surface.above(i);
            BlockState state = reedBlock(i).defaultBlockState();
            if (!state.canSurvive(level, pos)) return;
            if (!level.getBlockState(pos).isAir()) return;
            if (!level.ensureCanWrite(pos)) return;
            level.setBlock(pos, state, 2);
        }
    }

    private Block reedBlock(int index) {
        if (HEIGHT == 1) return ModBlocks.REEDS_TOP.get();
        if (index <= 0) return ModBlocks.REEDS_BOTTOM.get();
        if (index >= HEIGHT - 1) return ModBlocks.REEDS_TOP.get();
        return ModBlocks.REEDS_MID.get();
    }
}
