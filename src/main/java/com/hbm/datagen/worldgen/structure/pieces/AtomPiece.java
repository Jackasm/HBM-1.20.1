package com.hbm.datagen.worldgen.structure.pieces;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BarbedWire;
import com.hbm.datagen.worldgen.feature.DesertAtomFeature;
import com.hbm.datagen.worldgen.structure.ModStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.NotNull;

/**
 * Атомная станция: кусок структуры, который рисует её кодом — так же, как это
 * делал эталон 1.7.10 ({@code DesertAtom001/002/003}).
 *
 * <p>Почему не NBT-шаблон: шаблон 0.0.15 нарезан из мира вручную (коммит
 * {@code ad7203a5}, «NBT 64x32x64 без платформы») и композицию эталона не
 * воспроизводит — 3304 блока против 3259 у эталона выше земли, совпадает
 * меньше половины клеток, части комплекса стоят со сдвигом (#160). Код
 * генератора перенесён из 1.7.10 построчно и координаты вызовов совпадают
 * с эталоном, поэтому источник правды — он.</p>
 */
public class AtomPiece extends StructurePiece {

    /** Габарит эталона по X: x 0..40. */
    public static final int SIZE_X = 41;
    /** Габарит эталона по Z: z 0..33. */
    public static final int SIZE_Z = 34;
    /** Основание: y −5..−1 (бетонная подушка), поэтому низ куска ниже точки постановки. */
    public static final int BASE_DEPTH = 5;
    /** Верх постройки: y + 19 относительно точки постановки. */
    public static final int TOP_Y = 19;
    /** Насколько глубоко ищем грунт под подушкой, прежде чем бросить колонку. */
    private static final int MAX_FILL = 24;

    private final BlockPos origin;

    public AtomPiece(int x, int y, int z) {
        super(ModStructurePieceTypes.ATOM_PIECE.get(), 0,
                new BoundingBox(x, y - BASE_DEPTH, z, x + SIZE_X - 1, y + TOP_Y, z + SIZE_Z - 1));
        this.origin = new BlockPos(x, y, z);
    }

    public AtomPiece(CompoundTag tag) {
        super(ModStructurePieceTypes.ATOM_PIECE.get(), tag);
        this.origin = new BlockPos(tag.getInt("OX"), tag.getInt("OY"), tag.getInt("OZ"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tag) {
        tag.putInt("OX", origin.getX());
        tag.putInt("OY", origin.getY());
        tag.putInt("OZ", origin.getZ());
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager manager,
                            @NotNull ChunkGenerator generator, @NotNull RandomSource random,
                            @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
        // Генератор пишет всю постройку целиком, поэтому берём в работу только
        // клетки текущего чанка: остальные достанутся своим проходам.
        WorldGenLevel clipped = ChunkBoundLevel.clip(level, chunkPos);

        // Порядок важен: сначала срезаем рельеф над площадкой (иначе дюна
        // оказывается внутри корпусов), потом ставим постройку, затем подсыпаем
        // грунт под подушку.
        carveSite(clipped, chunkPos);
        new DesertAtomFeature().generate(
                clipped,
                random,
                origin.getX(), origin.getY(), origin.getZ());
        fillUnderPlatform(clipped, chunkPos);
    }

    /**
     * Срезает рельеф над площадкой: всё, что не часть станции, выше уровня её
     * пола. Без этого дюна остаётся внутри корпусов — владелец 03.11.2026:
     * «надо очищать все блоки, которые не часть атомной станции».
     *
     * <p>Чистим от уровня пола зданий ({@code origin}) вверх до верха коробки.
     * Подушка занимает {@code origin−5…origin−1} и ставится после, поэтому её
     * не трогаем.</p>
     */
    private void carveSite(WorldGenLevel level, ChunkPos chunkPos) {
        int topY = origin.getY() + TOP_Y;
        int minX = Math.max(chunkPos.getMinBlockX(), origin.getX());
        int maxX = Math.min(chunkPos.getMaxBlockX(), origin.getX() + SIZE_X - 1);
        int minZ = Math.max(chunkPos.getMinBlockZ(), origin.getZ());
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), origin.getZ() + SIZE_Z - 1);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockState air = Blocks.AIR.defaultBlockState();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = origin.getY(); y <= topY; y++) {
                    if (!level.getBlockState(cursor.set(x, y, z)).isAir()) {
                        level.setBlock(cursor, air, 2);
                    }
                }
            }
        }
    }

    /**
     * Подсыпает грунт под подушку до земли — вместо Beardifier'а, который в
     * эталоне поднимал рельеф до низа коробки, но на дюнах не дотягивался.
     *
     * <p>Замер 03.11.2026 на живом мире: подушка висела над грунтом в среднем
     * на 2,15 блока, в 88,6% колонок, до 11 блоков в худшей. Теперь каждая
     * колонка, где есть низ подушки, получает засыпку материалом того грунта,
     * который лежит под ней. Пишем только свой чанк — обёртка уже режет.</p>
     */
    private void fillUnderPlatform(WorldGenLevel level, ChunkPos chunkPos) {
        int baseY = origin.getY() - BASE_DEPTH;
        int padTopY = origin.getY() - 1;
        int minX = Math.max(chunkPos.getMinBlockX(), origin.getX());
        int maxX = Math.min(chunkPos.getMaxBlockX(), origin.getX() + SIZE_X - 1);
        int minZ = Math.max(chunkPos.getMinBlockZ(), origin.getZ());
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), origin.getZ() + SIZE_Z - 1);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int groundY = Integer.MIN_VALUE;
                BlockState groundBlock = null;
                for (int y = baseY - 1; y >= baseY - MAX_FILL; y--) {
                    BlockState state = level.getBlockState(cursor.set(x, y, z));
                    if (!state.isAir()) {
                        groundY = y;
                        groundBlock = state;
                        break;
                    }
                }
                if (groundY == Integer.MIN_VALUE || groundBlock == null) continue;

                // Подушка: от грунта вверх до уровня площадки. Площадка с бочками
                // стоит на этажерке, и без этого под ней оставалась пустота от
                // грунта до настила (владелец 03.11.2026).
                for (int y = groundY + 1; y <= padTopY; y++) {
                    if (level.getBlockState(cursor.set(x, y, z)).isAir()) {
                        level.setBlock(cursor, groundBlock, 2);
                    }
                }
            }
        }

        fixBarbedWire(level, chunkPos, baseY);
    }

    /**
     * Разворачивает колючую проволоку по сторонам площадки.
     *
     * <p>Генератор ставит всю проволоку состоянием по умолчанию, поэтому панели
     * на всех сторонах смотрят одинаково, а на коротких сторонах их надо
     * развернуть. Правило то же, что у NBT-куска
     * ({@code NBTStructurePiece.fixBarbedWire}): вдоль X — на север, вдоль Z —
     * на запад. Сторону определяем по локальным координатам колонки: длинные
     * стороны кольца лежат на {@code z = 0} и {@code z = SIZE_Z−1}, короткие —
     * на {@code x = 0} и {@code x = SIZE_X−1}.</p>
     */
    private void fixBarbedWire(WorldGenLevel level, ChunkPos chunkPos, int baseY) {
        int minX = Math.max(chunkPos.getMinBlockX(), origin.getX());
        int maxX = Math.min(chunkPos.getMaxBlockX(), origin.getX() + SIZE_X - 1);
        int minZ = Math.max(chunkPos.getMinBlockZ(), origin.getZ());
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), origin.getZ() + SIZE_Z - 1);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int localX = x - origin.getX();
                int localZ = z - origin.getZ();

                boolean longSide = localZ == 0 || localZ == SIZE_Z - 1;
                boolean shortSide = localX == 0 || localX == SIZE_X - 1;
                if (!longSide && !shortSide) continue;

                Direction facing = longSide ? Direction.NORTH : Direction.WEST;

                for (int y = baseY; y <= origin.getY() + TOP_Y; y++) {
                    BlockState state = level.getBlockState(cursor.set(x, y, z));
                    if (state.getBlock() != ModBlocks.BARBED_WIRE.get()) continue;
                    if (!state.hasProperty(BarbedWire.FACING)) continue;
                    if (state.getValue(BarbedWire.FACING) == facing) continue;
                    level.setBlock(cursor, state.setValue(BarbedWire.FACING, facing), 2);
                }
            }
        }
    }
}
