package com.hbm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Утилитный класс для работы с мультиблоками
 */
public class MultiblockUtil {

    // when looking north
    //                                        U  D  N  S  W  E
    public static int[] uni = new int[]{3, 0, 4, 4, 4, 4};

    /**
     * Проверяет, свободно ли пространство для размещения мультиблока
     *
     * @param level    мир
     * @param corePos  позиция ядра
     * @param dim      размеры {up, down, north, south, west, east}
     * @param source   позиция, с которой ставится блок (игнорируется при проверке)
     * @param dir      направление взгляда
     * @return true если место свободно
     */
    public static boolean checkSpace(LevelAccessor level, BlockPos corePos, int[] dim, BlockPos source, Direction dir) {
        if (dim == null || dim.length != 6) return false;

        int count = 0;
        int[] rot = rotate(dim, dir);

        for (int a = corePos.getX() - rot[4]; a <= corePos.getX() + rot[5]; a++) {
            for (int b = corePos.getY() - rot[1]; b <= corePos.getY() + rot[0]; b++) {
                for (int c = corePos.getZ() - rot[2]; c <= corePos.getZ() + rot[3]; c++) {
                    BlockPos checkPos = new BlockPos(a, b, c);

                    // Пропускаем позицию, с которой ставится блок
                    if (checkPos.equals(source)) continue;

                    if (!level.getBlockState(checkPos).canBeReplaced()) {
                        return false;
                    }

                    count++;
                    if (count > 2000) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Заполняет пространство заглушками мультиблока
     *
     * @param level    мир
     * @param corePos  позиция ядра
     * @param dim      размеры
     * @param block    блок-заглушка (обычно BlockDummyable)
     * @param dir      направление
     */
    public static void fillSpace(LevelAccessor level, BlockPos corePos, int[] dim, Block block, Direction dir) {
        if (dim == null || dim.length != 6) return;

        int count = 0;
        int[] rot = rotate(dim, dir);

        BlockDummyable.setSafeRem(true);

        for (int a = corePos.getX() - rot[4]; a <= corePos.getX() + rot[5]; a++) {
            for (int b = corePos.getY() - rot[1]; b <= corePos.getY() + rot[0]; b++) {
                for (int c = corePos.getZ() - rot[2]; c <= corePos.getZ() + rot[3]; c++) {
                    BlockPos pos = new BlockPos(a, b, c);

                    // Пропускаем ядро
                    if (pos.equals(corePos)) continue;

                    Direction side = getSide(corePos, pos);

                    if (side != null) {
                        BlockState dummyState = ((BlockDummyable) block).getDummyState(side);
                        level.setBlock(pos, dummyState, 3);

                        // Заглушке своя BE не нужна, но «висячая» появляется двумя путями:
                        // 1) в живом мире setBlock не удаляет BE, если тип блока не менялся
                        //    (ядро, ставшее заглушкой);
                        // 2) при генерации WorldGenRegion.setBlock пишет на любое состояние с
                        //    hasBlockEntity() отложенный тег id=DUMMY, а newBlockEntity у
                        //    заглушек возвращает null — на загрузке чанка это давало
                        //    «Tried to load a block entity … but failed» на каждую заглушку (#204).
                        // Чистим ПОСЛЕ setBlock: до него убирать нечего, тег пишет сам setBlock.
                        level.getChunk(pos).removeBlockEntity(pos);
                    }

                    count++;
                    if (count > 2000) {
                        BlockDummyable.setSafeRem(false);
                        return;
                    }
                }
            }
        }

        BlockDummyable.setSafeRem(false);
    }

    /**
     * Определяет направление от ядра к заглушке
     */
    private static Direction getSide(BlockPos core, BlockPos part) {
        int dx = part.getX() - core.getX();
        int dy = part.getY() - core.getY();
        int dz = part.getZ() - core.getZ();

        if (dy < 0) return Direction.DOWN;
        if (dy > 0) return Direction.UP;
        if (dx < 0) return Direction.WEST;
        if (dx > 0) return Direction.EAST;
        if (dz < 0) return Direction.NORTH;
        if (dz > 0) return Direction.SOUTH;

        return null; // это ядро
    }

    /**
     * Поворачивает размеры в соответствии с направлением
     *
     * @param dim исходные размеры [up, down, north, south, west, east]
     * @param dir направление, в котором смотрит блок
     * @return повёрнутые размеры
     */
    public static int[] rotate(int[] dim, Direction dir) {
        if (dim == null) return null;

        return switch (dir) {
            case SOUTH -> dim;
            case NORTH -> new int[]{dim[0], dim[1], dim[3], dim[2], dim[5], dim[4]};
            case EAST -> new int[]{dim[0], dim[1], dim[5], dim[4], dim[2], dim[3]};
            case WEST -> new int[]{dim[0], dim[1], dim[4], dim[5], dim[3], dim[2]};
            default -> dim;
        };
    }
}
