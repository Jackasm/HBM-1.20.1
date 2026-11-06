package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Хелперы и BlockSelector-ы для Civilian-структур.
 */
public class CivilianFeatures {

    /** Размер box как в Component(Random, minX, 64, minZ, sizeX, sizeY, sizeZ). */
    public static BoundingBox boxFromSize(int minX, int minY, int minZ,
                                          int sizeX, int sizeY, int sizeZ, int coordMode) {
        int sx = (coordMode == 1 || coordMode == 3) ? sizeZ : sizeX;
        int sz = (coordMode == 1 || coordMode == 3) ? sizeX : sizeZ;
        return BoundingBox.fromCorners(
                new BlockPos(minX, minY, minZ),
                new BlockPos(minX + sx, minY + sizeY, minZ + sz));
    }

    public static class Sandstone extends NTMComponent.BlockSelector {
        @Override
        public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c > 0.6F) state = Blocks.SANDSTONE.defaultBlockState();
            else if (c < 0.5F) state = ModBlocks.REINFORCED_SAND.get().defaultBlockState();
            else state = Blocks.SAND.defaultBlockState();
        }
    }

    public static class BrokenStairs extends NTMComponent.BlockSelector {
        private int meta = 0;
        private final int coordBaseMode;

        public BrokenStairs(int coordBaseMode) { this.coordBaseMode = coordBaseMode; }

        public void setMetadata(int meta) { this.meta = meta; }

        @Override
        public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c < 0.7F) {
                state = Blocks.OAK_STAIRS.defaultBlockState()
                        .setValue(StairBlock.FACING, rotate(metaToDirection(meta), coordBaseMode));
            } else if (c < 0.97F) {
                state = Blocks.OAK_SLAB.defaultBlockState();
            } else {
                state = Blocks.AIR.defaultBlockState();
            }
        }

        private static Direction rotate(Direction local, int coordMode) {
            return switch (coordMode) {
                default -> local;                      // South
                case 1 -> local.getClockWise();        // West
                case 2 -> local.getOpposite();         // North
                case 3 -> local.getCounterClockWise(); // East
            };
        }

        private Direction metaToDirection(int m) {
            return switch (m & 3) {
                case 0 -> Direction.EAST;   // было WEST
                case 1 -> Direction.WEST;   // было EAST
                case 2 -> Direction.SOUTH;  // было NORTH
                default -> Direction.NORTH; // было SOUTH
            };
        }
    }

    public static class BrokenBlocks extends NTMComponent.BlockSelector {
        @Override
        public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c < 0.6F) {
                state = Blocks.OAK_PLANKS.defaultBlockState();
            } else if (c < 0.8F) {
                Direction d = switch (rand.nextInt(4)) {
                    case 0 -> Direction.WEST;
                    case 1 -> Direction.EAST;
                    case 2 -> Direction.NORTH;
                    default -> Direction.SOUTH;
                };
                state = Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, d);
            } else {
                state = Blocks.OAK_SLAB.defaultBlockState();
            }
        }
    }
}
