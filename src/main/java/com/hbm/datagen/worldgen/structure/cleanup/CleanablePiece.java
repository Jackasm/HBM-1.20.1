package com.hbm.datagen.worldgen.structure.cleanup;

import com.hbm.blocks.generic.BarbedWire;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Кусок структуры, который умеет чистить свой габарит после декорации (#102, #190).
 *
 * <p>Три правила, все — после декорации ({@code top_layer_modification}), чтобы
 * видеть уже наросшее:</p>
 * <ol>
 *   <li>декор выше подошвы, которого нет в палитре шаблона, удаляется (#102);</li>
 *   <li>блоки структуры, которые декор всё-таки заменил, восстанавливаются;</li>
 *   <li>чужое внутри объёма, накрытого шаблоном, удаляется (#190): выше подошвы
 *       и только в колонках, где над клеткой есть блок шаблона. Так уходит дерево
 *       под породой островка, а гора вокруг структуры остаётся.</li>
 * </ol>
 *
 * <p>Блоки с блок-энтити не трогаются — сундук с лутом дороже ровной стены;
 * проволока тоже, её {@code facing} меняет пост-обработка. Жидкости не трогаются:
 * у воды свои правила, а подводная часть структуры должна оставаться под водой.</p>
 *
 * <p>Отдельно — чужие структуры: если в наш габарит влез кусок другой структуры
 * (колодец на палубе катера), его блоки выше нашей подошвы убираются. Это
 * осознанное отклонение от 1.7.10, где объём не чистился вовсе (ADR-0026).</p>
 */
public interface CleanablePiece {

    /** Полный габарит куска в мире. */
    BoundingBox cleanupBox();

    /** Низ структуры в колонке; {@code -1}, если колонки нет (руины вне подошвы). */
    int cleanupBaseY(int x, int z);

    /** Локальная позиция шаблона → мировая. {@code null}, если колонка вне подошвы. */
    @Nullable
    BlockPos localToWorld(BlockPos local);

    /** Поворот состояния блока так же, как его ставит шаблон. */
    BlockState transformState(BlockState local);

    TemplateLayout layout(WorldGenLevel level);

    default void clean(WorldGenLevel level, BoundingBox region) {
        BoundingBox box = cleanupBox();
        int minX = Math.max(region.minX(), box.minX());
        int minY = Math.max(region.minY(), box.minY());
        int minZ = Math.max(region.minZ(), box.minZ());
        int maxX = Math.min(region.maxX(), box.maxX());
        int maxY = Math.min(region.maxY(), box.maxY());
        int maxZ = Math.min(region.maxZ(), box.maxZ());
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return;
        }
        BoundingBox area = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

        Map<BlockPos, BlockState> expected = new HashMap<>();
        Map<Long, Integer> topByColumn = new HashMap<>();
        Map<Long, Integer> bottomByColumn = new HashMap<>();
        for (Map.Entry<BlockPos, BlockState> entry : layout(level).blocks().entrySet()) {
            BlockPos world = localToWorld(entry.getKey());
            if (world == null) continue;
            long column = ChunkPos.asLong(world.getX(), world.getZ());
            topByColumn.merge(column, world.getY(), Math::max);
            bottomByColumn.merge(column, world.getY(), Math::min);
            if (!area.isInside(world)) continue;
            expected.put(world, transformState(entry.getValue()));
        }

        clearDecor(level, area, expected);
        restoreExpected(level, expected);
        clearCovered(level, area, expected, topByColumn, bottomByColumn);

        // Чужой кусок структуры внутри габарита чистим в пределах своего региона.
        // Раньше сюда передавался полный `box`: габарит структуры лежит в десятках
        // чанков, обход шёл по всем им (включая `level.getChunk` на чужие чанки) и
        // давал сотни тысяч setBlock в «far chunk» — сервер стоял десятки секунд, а
        // записи ваниль всё равно не применяла (#219). Теперь каждый чанк чистит
        // свой срез; работа идемпотентна, повтор не страшен.
        clearForeignPieces(level, area);
    }

    /** #102: декор выше подошвы, которого нет в палитре. */
    private void clearDecor(WorldGenLevel level, BoundingBox area, Map<BlockPos, BlockState> expected) {
        for (int x = area.minX(); x <= area.maxX(); x++) {
            for (int z = area.minZ(); z <= area.maxZ(); z++) {
                int base = cleanupBaseY(x, z);
                if (base < 0) continue;
                int from = Math.max(area.minY(), base + 1);
                for (int y = from; y <= area.maxY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!StructureCleanup.isDecor(state) || expected.containsKey(pos)) continue;
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    /** Блоки шаблона возвращаются на место, если декор их заменил. */
    private void restoreExpected(WorldGenLevel level, Map<BlockPos, BlockState> expected) {
        for (Map.Entry<BlockPos, BlockState> entry : expected.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState wanted = entry.getValue();
            BlockState current = level.getBlockState(pos);
            if (current.equals(wanted)) continue;
            // Блок-энтити не трогаем: сундук с лутом, табличка, блок с NBT.
            if (current.hasBlockEntity()) continue;
            // Проволока после пост-обработки намеренно смотрит иначе, чем в шаблоне.
            if (current.getBlock() instanceof BarbedWire || wanted.getBlock() instanceof BarbedWire) continue;
            level.setBlock(pos, wanted, 2);
        }
    }

    /**
     * #190: чужое внутри объёма, накрытого шаблоном. Колонка участвует, только
     * если в ней есть блок шаблона; чистится от подошвы до верхнего блока
     * шаблона в этой колонке, кроме самих блоков шаблона.
     */
    private void clearCovered(WorldGenLevel level, BoundingBox area,
                              Map<BlockPos, BlockState> expected, Map<Long, Integer> topByColumn,
                              Map<Long, Integer> bottomByColumn) {
        for (Map.Entry<Long, Integer> entry : topByColumn.entrySet()) {
            int x = ChunkPos.getX(entry.getKey());
            int z = ChunkPos.getZ(entry.getKey());
            if (x < area.minX() || x > area.maxX() || z < area.minZ() || z > area.maxZ()) continue;
            int base = cleanupBaseY(x, z);
            if (base < 0) continue;
            int bottom = bottomByColumn.getOrDefault(entry.getKey(), base);
            // Начинаем с первого блока шаблона в колонке, а не с подошвы:
            // у крана под стрелой нет стен, и старый обход выкапывал землю
            // от подошвы до стрелы — в поверхности появлялась щель.
            int from = Math.max(area.minY(), Math.max(base + 1, bottom));
            int to = Math.min(area.maxY(), entry.getValue() - 1);
            for (int y = from; y <= to; y++) {
                BlockPos pos = new BlockPos(x, y, z);
                if (expected.containsKey(pos)) continue;
                BlockState state = level.getBlockState(pos);
                if (!isClearable(state)) continue;
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    /** Блоки чужих структур выше нашей подошвы внутри нашего габарита. */
    private void clearForeignPieces(WorldGenLevel level, BoundingBox box) {
        Map<BlockPos, BlockState> expected = new HashMap<>();
        for (Map.Entry<BlockPos, BlockState> entry : layout(level).blocks().entrySet()) {
            BlockPos world = localToWorld(entry.getKey());
            if (world != null) expected.put(world, transformState(entry.getValue()));
        }

        for (int cx = box.minX() >> 4; cx <= box.maxX() >> 4; cx++) {
            for (int cz = box.minZ() >> 4; cz <= box.maxZ() >> 4; cz++) {
                ChunkAccess chunk = level.getChunk(cx, cz);
                for (StructureStart start : chunk.getAllStarts().values()) {
                    if (!start.isValid()) continue;
                    if (start.getPieces().stream().anyMatch(piece -> piece == this)) continue;
                    for (StructurePiece other : start.getPieces()) {
                        clearOtherPiece(level, box, expected, other.getBoundingBox());
                    }
                }
            }
        }
    }

    private void clearOtherPiece(WorldGenLevel level, BoundingBox box,
                                 Map<BlockPos, BlockState> expected, BoundingBox other) {
        int minX = Math.max(other.minX(), box.minX());
        int minY = Math.max(other.minY(), box.minY());
        int minZ = Math.max(other.minZ(), box.minZ());
        int maxX = Math.min(other.maxX(), box.maxX());
        int maxY = Math.min(other.maxY(), box.maxY());
        int maxZ = Math.min(other.maxZ(), box.maxZ());
        if (minX > maxX || minY > maxY || minZ > maxZ) return;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int base = cleanupBaseY(x, z);
                if (base < 0) continue;
                int from = Math.max(minY, base + 1);
                for (int y = from; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (expected.containsKey(pos)) continue;
                    BlockState state = level.getBlockState(pos);
                    if (!isClearable(state)) continue;
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    /** Что можно сносить: не воздух, не жидкость, не блок-энтити, не проволока. */
    private static boolean isClearable(BlockState state) {
        if (state.isAir()) return false;
        if (!state.getFluidState().isEmpty()) return false;
        if (state.hasBlockEntity()) return false;
        return !(state.getBlock() instanceof BarbedWire);
    }
}
