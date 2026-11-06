package com.hbm.datagen.worldgen.structure.cleanup;

import com.hbm.config.StructureConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Реестр кусков структур и их чистка после декорации (#102).
 *
 * <p>Когда чанк генерируется, структура ставится на шаге {@code surface_structures},
 * а деревья и трава — позже, на {@code vegetal_decoration}. Поэтому декор успевает
 * вырасти внутри габарита. Чистка запускается фичей на {@code top_layer_modification},
 * то есть после декорации, и работает в пределах района генерации — радиус один чанк,
 * ровно столько, насколько дерево может перекинуть листву в соседний чанк.</p>
 *
 * <p>Соседей чистим только в полосе три блока от границы и только если их собственная
 * чистка уже прошла. Иначе сосед ещё не сгенерирован, и его блоки перезапишет
 * шум/поверхность; его собственная чистка разберётся с этим позже.</p>
 *
 * <p>Индекс живёт в памяти сессии и не сохраняется. Чистка идемпотентна, поэтому
 * отсутствие индекса после перезапуска ничего не портит: старые чанки остаются
 * как есть, а новые чистятся при генерации. Чанки, созданные до включения фичи,
 * не перечищаются — это вне ТЗ.</p>
 */
public final class StructureCleanup {

    /**
     * Радиус, на который крона дерева из соседнего чанка залетает в чанк.
     * Ванильные кроны — до 3–4 блоков (большой дуб, ель, джунгли), берём с запасом:
     * прогон стенда поймал листву в четвёртом блоке от границы — полосы в три не хватило.
     */
    private static final int STRAY_RADIUS = 6;

    private static final Map<ResourceKey<Level>, DimensionState> DIMENSIONS = new ConcurrentHashMap<>();

    static {
        MinecraftForge.EVENT_BUS.addListener(StructureCleanup::onLevelUnload);
    }

    private StructureCleanup() {
    }

    /** Запомнить кусок для чанка, в который он ставится. */
    public static void register(WorldGenLevel level, ChunkPos chunk, CleanablePiece piece) {
        if (!StructureConfig.isCleanDecor()) return;
        List<CleanablePiece> pieces = state(level).byChunk
                .computeIfAbsent(chunk.toLong(), key -> new CopyOnWriteArrayList<>());
        if (!pieces.contains(piece)) {
            pieces.add(piece);
        }
    }

    /** Почистить чанк и залётный декор в полосе соседей. */
    public static void clean(WorldGenLevel level, ChunkPos center) {
        if (!StructureConfig.isCleanDecor()) return;
        DimensionState state = state(level);
        long key = center.toLong();

        List<CleanablePiece> own = state.byChunk.get(key);
        if (own != null && !own.isEmpty()) {
            state.cleaned.add(key);
            BoundingBox box = chunkBox(level, center);
            for (CleanablePiece piece : own) {
                piece.clean(level, box);
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                ChunkPos neighbor = new ChunkPos(center.x + dx, center.z + dz);
                long neighborKey = neighbor.toLong();
                List<CleanablePiece> pieces = state.byChunk.get(neighborKey);
                if (pieces == null || pieces.isEmpty() || !state.cleaned.contains(neighborKey)) continue;
                BoundingBox strip = strayStrip(level, center, neighbor);
                for (CleanablePiece piece : pieces) {
                    piece.clean(level, strip);
                }
            }
        }
    }

    /** Декор, который нарос поверх структуры: деревья, листва, трава, цветы. */
    public static boolean isDecor(BlockState state) {
        if (state.isAir()) return false;
        return state.is(BlockTags.LEAVES)
                || state.is(BlockTags.LOGS)
                || state.is(BlockTags.SAPLINGS)
                || state.is(BlockTags.FLOWERS)
                || state.is(Blocks.GRASS)
                || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.FERN)
                || state.is(Blocks.LARGE_FERN)
                || state.is(Blocks.DEAD_BUSH)
                || state.is(Blocks.VINE)
                || state.is(Blocks.GLOW_LICHEN)
                || state.is(Blocks.HANGING_ROOTS)
                || state.is(Blocks.SUGAR_CANE);
    }

    private static BoundingBox chunkBox(WorldGenLevel level, ChunkPos chunk) {
        return new BoundingBox(
                chunk.getMinBlockX(), level.getMinBuildHeight(), chunk.getMinBlockZ(),
                chunk.getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getMaxBlockZ());
    }

    /** Полоса соседнего чанка, куда может дотянуться декор из центрального. */
    private static BoundingBox strayStrip(WorldGenLevel level, ChunkPos center, ChunkPos neighbor) {
        int minX = Math.max(neighbor.getMinBlockX(), center.getMinBlockX() - STRAY_RADIUS);
        int maxX = Math.min(neighbor.getMaxBlockX(), center.getMaxBlockX() + STRAY_RADIUS);
        int minZ = Math.max(neighbor.getMinBlockZ(), center.getMinBlockZ() - STRAY_RADIUS);
        int maxZ = Math.min(neighbor.getMaxBlockZ(), center.getMaxBlockZ() + STRAY_RADIUS);
        return new BoundingBox(minX, level.getMinBuildHeight(), minZ,
                maxX, level.getMaxBuildHeight() - 1, maxZ);
    }

    private static DimensionState state(WorldGenLevel level) {
        return DIMENSIONS.computeIfAbsent(level.getLevel().dimension(), key -> new DimensionState());
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            DIMENSIONS.remove(serverLevel.dimension());
        }
    }

    private static final class DimensionState {
        final Map<Long, List<CleanablePiece>> byChunk = new ConcurrentHashMap<>();
        final Set<Long> cleaned = ConcurrentHashMap.newKeySet();
    }
}
