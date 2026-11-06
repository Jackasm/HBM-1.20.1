package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.deco.DecoComputer;
import com.hbm.itempool.ItemPoolsComponent;
import com.hbm.itempool.ItemPoolsLegacy;
import com.hbm.itempool.LootGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class OfficeFeatures {

    public static class LargeOffice extends NTMComponent {

        private static final ConcreteBricks CONCRETE_BRICKS = new ConcreteBricks();

        private boolean[] hasPlacedLoot = new boolean[2];

        public LargeOffice() { }

        public LargeOffice(RandomSource rand, int minX, int minZ) {
            this.coordBaseMode = rand.nextInt(4);
            this.boundingBox = createBoundingBox(minX, 64, minZ, 14, 5, 12);
            this.hasPlacedLoot[0] = false;
            this.hasPlacedLoot[1] = false;
        }

        @Override
        public String getTypeName() { return "LargeOffice"; }

        private BoundingBox createBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return switch (this.coordBaseMode) {
                case 1, 3 -> BoundingBox.fromCorners(
                        new BlockPos(minX, minY, minZ),
                        new BlockPos(minX + maxZ, minY + maxY, minZ + maxX));
                default -> BoundingBox.fromCorners(
                        new BlockPos(minX, minY, minZ),
                        new BlockPos(minX + maxX, minY + maxY, minZ + maxZ));
            };
        }

        @Override
        public void writeNBT(CompoundTag nbt) {
            super.writeNBT(nbt);
            nbt.putBoolean("hasLoot1", this.hasPlacedLoot[0]);
            nbt.putBoolean("hasLoot2", this.hasPlacedLoot[1]);
        }

        @Override
        public void readNBT(CompoundTag nbt) {
            super.readNBT(nbt);
            this.hasPlacedLoot[0] = nbt.getBoolean("hasLoot1");
            this.hasPlacedLoot[1] = nbt.getBoolean("hasLoot2");
        }

        @Override
        public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

            if (!heightCalculated) {
                if (!this.setAverageHeight(level, box, this.boundingBox.minY())) return false;
                this.boundingBox = this.boundingBox.move(0, -1, 0);
                heightCalculated = true;
            }

            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 5, 0, 14, 1, -1, box);
            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 2, 14, 7, -1, box);
            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 8, 8, 12, 0, box);
            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 9, 8, 14, 12, -1, box);

            fillWithAir(level, box, 1, 1, 3, 4, 3, 6);
            fillWithAir(level, box, 6, 1, 1, 13, 3, 6);
            fillWithAir(level, box, 10, 1, 7, 13, 3, 11);

            // Pillars - Back
            fillPillar(level, box, 0, 0, 2, 0, 4, 2);
            fillPillar(level, box, 5, 0, 0, 5, 4, 0);
            fillPillar(level, box, 14, 0, 0, 14, 4, 0);
            // Pillars - Front
            fillPillar(level, box, 0, 0, 7, 0, 3, 7);
            fillPillar(level, box, 0, 0, 12, 0, 3, 12);
            fillPillar(level, box, 3, 0, 12, 3, 3, 12);
            fillPillar(level, box, 6, 0, 12, 6, 3, 12);
            fillPillar(level, box, 9, 0, 12, 9, 3, 12);
            fillPillar(level, box, 9, 0, 7, 9, 3, 7);
            fillPillar(level, box, 14, 0, 12, 14, 4, 12);

            // Walls - Back
            fillWithRandomizedBlocks(level, box, 1, 0, 2, 5, 4, 2, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 5, 0, 1, 5, 4, 1, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 6, 0, 0, 13, 1, 0, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 6, 2, 0, 6, 2, 0, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 9, 2, 0, 10, 2, 0, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 13, 2, 0, 13, 2, 0, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 6, 3, 0, 13, 4, 0, rand, CONCRETE_BRICKS);
            // Walls - Right
            fillWithRandomizedBlocks(level, box, 14, 0, 1, 14, 1, 11, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 14, 2, 1, 14, 2, 2, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 14, 2, 5, 14, 2, 7, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 14, 2, 10, 14, 2, 11, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 14, 3, 1, 14, 4, 11, rand, CONCRETE_BRICKS);
            // Walls - Front
            fillWithRandomizedBlocks(level, box, 0, 4, 12, 13, 4, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 10, 0, 12, 13, 1, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 10, 2, 12, 10, 2, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 13, 2, 12, 13, 2, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 10, 3, 12, 13, 3, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 9, 0, 8, 9, 3, 11, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 0, 7, 8, 0, 7, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 1, 7, 1, 2, 7, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 1, 7, 8, 3, 7, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 3, 7, 3, 3, 7, rand, CONCRETE_BRICKS);
            // Walls - Left
            fillWithRandomizedBlocks(level, box, 0, 4, 3, 0, 4, 11, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 0, 0, 3, 0, 1, 6, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 0, 2, 3, 0, 3, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 0, 2, 6, 0, 3, 6, rand, CONCRETE_BRICKS);
            // Walls - Interior
            fillWithRandomizedBlocks(level, box, 5, 1, 3, 5, 3, 5, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 5, 3, 6, 5, 3, 6, rand, CONCRETE_BRICKS);

            // Trim
            randomlyFillWithBlocks(level, box, rand, 0.85F, 0, 5, 2, 5, 5, 2, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 5, 5, 1, 5, 5, 1, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 5, 5, 0, 14, 5, 0, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 14, 5, 1, 14, 5, 12, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 0, 5, 12, 13, 5, 12, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 0, 5, 3, 0, 5, 11, Blocks.STONE_SLAB.defaultBlockState());

            // Floor
            fillWithBlocks(level, box, 1, 0, 3, 4, 0, 6, Blocks.GREEN_WOOL.defaultBlockState());
            fillWithBlocks(level, box, 5, 0, 3, 5, 0, 6, ModBlocks.BRICK_LIGHT.get().defaultBlockState());
            fillWithBlocks(level, box, 6, 0, 1, 13, 0, 6, ModBlocks.BRICK_LIGHT.get().defaultBlockState());
            fillWithBlocks(level, box, 10, 0, 7, 13, 0, 11, ModBlocks.BRICK_LIGHT.get().defaultBlockState());

            // Ceiling
            fillWithBlocks(level, box, 6, 4, 1, 13, 4, 2, ModBlocks.BRICK_LIGHT.get().defaultBlockState());
            fillWithBlocks(level, box, 1, 4, 3, 13, 4, 11, ModBlocks.BRICK_LIGHT.get().defaultBlockState());

            // Carpet
            fillWithBlocks(level, box, 9, 1, 3, 11, 1, 6, Blocks.LIGHT_GRAY_CARPET.defaultBlockState());

            // Windows
            randomlyFillWithBlocks(level, box, rand, 0.75F, 0, 2, 4, 0, 3, 5, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 7, 2, 0, 8, 2, 0, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 11, 2, 0, 12, 2, 0, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 14, 2, 3, 14, 2, 4, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 14, 2, 8, 14, 2, 9, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 11, 2, 12, 12, 2, 12, Blocks.GLASS_PANE.defaultBlockState());

            // === Furniture ===
            // Desk 1
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST,  true,  1, 1, 4);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.NORTH, true,  2, 1, 4);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST,  true,  3, 1, 4);
            placeStairs(level, box, Blocks.OAK_STAIRS,    Direction.NORTH, false, 2, 1, 3);
            placeComputer(level, box, 1, 2, 4, Direction.SOUTH);
            // Desk 2
            placeStairs(level, box, Blocks.OAK_STAIRS,    Direction.NORTH, false, 7, 1, 3);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST,  true,  6, 1, 4);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST,  true,  7, 1, 4);
            placeBlockAtCurrentPosition(level, Blocks.SPRUCE_PLANKS.defaultBlockState(), 8, 1, 4, box);
            placeComputer(level, box, 7, 2, 4, Direction.SOUTH);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 8, 2, 4, box);
            // Desk 3
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST,  true,  10, 1, 1);
            fillWithStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.SOUTH, true, 11, 1, 1, 13, 1, 1);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.NORTH, true,  13, 1, 2);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.SOUTH, true,  13, 1, 3);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST,  true,  13, 1, 4);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.NORTH, true,  13, 1, 5);
            placeStairs(level, box, Blocks.OAK_STAIRS,    Direction.SOUTH, false, 11, 1, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS,    Direction.WEST,  false, 12, 1, 4);
            placeComputer(level, box, 11, 2, 1, Direction.NORTH);
            placeComputer(level, box, 13, 2, 5, Direction.EAST);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 13, 2, 3, box);
            placeDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.WEST, 13, 2, 2);
            // Desk 4
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST,  true,  10, 1, 8);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST,  true,  11, 1, 8);
            placeStairs(level, box, Blocks.OAK_STAIRS,    Direction.SOUTH, false, 10, 1, 9);
            placeComputer(level, box, 10, 2, 8, Direction.NORTH);
            // Desk 5
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.NORTH, true,  13, 1, 9);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST,  true,  13, 1, 10);
            placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.SOUTH, true,  13, 1, 11);
            placeStairs(level, box, Blocks.OAK_STAIRS,    Direction.WEST,  false, 11, 1, 11);
            placeComputer(level, box, 13, 2, 11, Direction.EAST);

            // Cobwebs
            randomlyFillWithBlocks(level, box, rand, 0.25F, 1, 3, 3, 4, 3, 6, Blocks.COBWEB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.25F, 6, 3, 1, 13, 3, 6, Blocks.COBWEB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.25F, 10, 3, 7, 13, 3, 11, Blocks.COBWEB.defaultBlockState());

            // Doors
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, false, rand.nextBoolean(), 2, 1, 7);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, true,  rand.nextBoolean(), 3, 1, 7);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.EAST,  false, rand.nextBoolean(), 5, 1, 6);

            // Loot
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 10, 1, 11, ItemPoolsComponent.POOL_OFFICE_TRASH, 8);
            generateLockableContents(level, box, rand, ModBlocks.SAFE.get(), Direction.SOUTH, 6, 1, 1, ItemPoolsComponent.POOL_MACHINE_PARTS, 10, 0.5D);

            updateConnectedBlocks(level, box);
            return true;
        }

        private void placeComputer(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction localFacing) {
            Direction facing = rotate(localFacing).getOpposite();
            placeBlockAtCurrentPosition(level,
                    ModBlocks.DECO_COMPUTER.get().defaultBlockState().setValue(DecoComputer.FACING, facing),
                    x, y, z, box);
        }

        protected void fillWithStairs(WorldGenLevel level, BoundingBox box, Block block,
                                    Direction localFacing, boolean top,
                                    int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            Direction facing = rotate(localFacing);
            BlockState state = block.defaultBlockState()
                    .setValue(StairBlock.FACING, facing)
                    .setValue(StairBlock.HALF, top ? Half.TOP : Half.BOTTOM);
            fillWithBlocks(level, box, minX, minY, minZ, maxX, maxY, maxZ, state);
        }

        private Direction.Axis pillarAxis(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            boolean xLen = minX != maxX;
            boolean yLen = minY != maxY;
            boolean zLen = minZ != maxZ;
            Direction.Axis axis;
            if (xLen && !yLen && !zLen) axis = Direction.Axis.X;
            else if (!xLen && yLen && !zLen) axis = Direction.Axis.Y;
            else if (!xLen && !yLen && zLen) axis = Direction.Axis.Z;
            else axis = Direction.Axis.Y;
            if (coordBaseMode % 2 != 0) {
                if (axis == Direction.Axis.X) axis = Direction.Axis.Z;
                else if (axis == Direction.Axis.Z) axis = Direction.Axis.X;
            }
            return axis;
        }

        private void fillPillar(WorldGenLevel level, BoundingBox box,
                                int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            Direction.Axis axis = pillarAxis(minX, minY, minZ, maxX, maxY, maxZ);
            BlockState state = ModBlocks.CONCRETE_PILLAR.get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, axis);
            fillWithBlocks(level, box, minX, minY, minZ, maxX, maxY, maxZ, state);
        }
    }

    // ==================== LargeOfficeCorner ====================

    public static class LargeOfficeCorner extends NTMComponent {

        private static final ConcreteBricks CONCRETE_BRICKS = new ConcreteBricks();

        public LargeOfficeCorner() { }

        public LargeOfficeCorner(RandomSource rand, int minX, int minZ) {
            this.coordBaseMode = rand.nextInt(4);
            this.boundingBox = createBoundingBox(minX, 64, minZ, 11, 15, 14);
        }

        @Override
        public String getTypeName() { return "LargeOfficeCorner"; }

        private BoundingBox createBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return switch (this.coordBaseMode) {
                case 1, 3 -> BoundingBox.fromCorners(
                        new BlockPos(minX, minY, minZ),
                        new BlockPos(minX + maxZ, minY + maxY, minZ + maxX));
                default -> BoundingBox.fromCorners(
                        new BlockPos(minX, minY, minZ),
                        new BlockPos(minX + maxX, minY + maxY, minZ + maxZ));
            };
        }

        @Override
        public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

            if (!heightCalculated) {
                if (!this.setAverageHeight(level, box, this.boundingBox.minY())) return false;
                this.boundingBox = this.boundingBox.move(0, -1, 0);
                heightCalculated = true;
            }

            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, 11, 2, -1, box);
            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 3, 11, 13, -1, box);
            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 14, 4, 14, -1, box);
            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 10, 0, 13, -1, box);

            fillWithAir(level, box, 1, 1, 11, 3, 12, 13);
            fillWithAir(level, box, 4, 1, 4, 10, 12, 12);
            fillWithAir(level, box, 2, 1, 4, 3, 12, 10);
            fillWithAir(level, box, 5, 1, 1, 10, 12, 2);
            fillWithAir(level, box, 5, 13, 1, 8, 14, 2);

            // Back Wall
            fillPillar(level, box, 1, 0, 3, 5, 0, 3);
            placePillar(level, box, 6, 0, 3, Direction.Axis.X);
            fillPillar(level, box, 7, 0, 3, 10, 0, 3);
            fillPillar(level, box, 4, 0, 0, 4, 0, 2);
            fillPillar(level, box, 5, 0, 0, 11, 0, 0);
            fillPillar(level, box, 11, 1, 0, 11, 12, 0);
            fillPillar(level, box, 1, 1, 3, 1, 12, 3);

            fillWithRandomizedBlocks(level, box, 4, 1, 0, 10, 12, 0, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 13, 0, 9, 13, 0, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 14, 0, 8, 14, 0, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 15, 0, 7, 15, 0, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 2, 1, 3, 5, 2, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 7, 1, 3, 10, 2, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 2, 3, 3, 10, 7, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 2, 8, 3, 9, 10, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 2, 11, 3, 10, 12, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 13, 3, 4, 14, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 6, 13, 3, 9, 13, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 6, 14, 3, 8, 14, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 15, 3, 7, 15, 3, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 1, 1, 4, 15, 2, rand, CONCRETE_BRICKS);

            // Right Wall
            fillPillar(level, box, 11, 0, 1, 11, 0, 12);
            fillPillar(level, box, 11, 0, 13, 11, 12, 13);
            fillPillar(level, box, 11, 1, 3, 11, 7, 3);
            fillPillar(level, box, 11, 4, 1, 11, 4, 2);
            fillPillar(level, box, 11, 8, 1, 11, 8, 12);
            fillPillar(level, box, 11, 9, 3, 11, 11, 3);
            fillPillar(level, box, 11, 12, 1, 11, 12, 12);

            fillWithRandomizedBlocks(level, box, 11, 9, 1, 11, 11, 2, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 11, 5, 1, 11, 7, 2, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 11, 1, 1, 11, 3, 2, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 11, 1, 4, 11, 7, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 11, 9, 4, 11, 9, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 11, 10, 4, 11, 10, 4, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 11, 10, 8, 11, 10, 8, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 11, 10, 12, 11, 10, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 11, 11, 4, 11, 11, 12, rand, CONCRETE_BRICKS);

            // Front Wall
            fillPillar(level, box, 4, 0, 13, 10, 0, 13);
            fillPillar(level, box, 4, 0, 14, 4, 12, 14);
            placePillar(level, box, 3, 0, 14, Direction.Axis.X);
            fillPillar(level, box, 1, 0, 14, 2, 0, 14);
            fillPillar(level, box, 0, 0, 14, 0, 12, 14);

            fillWithRandomizedBlocks(level, box, 4, 1, 13, 10, 1, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 10, 2, 13, 10, 3, 13, rand, CONCRETE_BRICKS);
            fillPillar(level, box, 9, 2, 13, 9, 3, 13);
            fillPillar(level, box, 6, 2, 13, 6, 3, 13);
            fillWithRandomizedBlocks(level, box, 4, 2, 13, 5, 3, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 4, 13, 10, 5, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 10, 6, 13, 10, 7, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 6, 13, 5, 7, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 8, 13, 10, 9, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 10, 10, 13, 10, 11, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 10, 13, 5, 11, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 12, 13, 10, 12, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 3, 1, 14, 3, 2, 14, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 3, 14, 3, 5, 14, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 8, 14, 3, 9, 14, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 12, 14, 3, 12, 14, rand, CONCRETE_BRICKS);

            // Left Wall
            fillPillar(level, box, 0, 0, 12, 0, 0, 13);
            placePillar(level, box, 0, 0, 11, Direction.Axis.Y);
            fillPillar(level, box, 0, 0, 10, 0, 12, 10);
            fillPillar(level, box, 1, 0, 4, 1, 0, 10);
            fillPillar(level, box, 1, 0, 3, 1, 12, 3);

            fillWithRandomizedBlocks(level, box, 0, 1, 11, 0, 2, 11, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 0, 3, 11, 0, 5, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 0, 8, 11, 0, 9, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 0, 12, 11, 0, 12, 13, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 1, 4, 1, 1, 10, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 2, 9, 1, 3, 10, rand, CONCRETE_BRICKS);
            fillPillar(level, box, 1, 2, 8, 1, 3, 8);
            fillPillar(level, box, 1, 2, 5, 1, 3, 5);
            fillWithRandomizedBlocks(level, box, 1, 2, 4, 1, 3, 4, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 4, 4, 1, 5, 10, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 6, 9, 1, 7, 10, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 6, 4, 1, 7, 4, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 8, 4, 1, 9, 10, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 10, 9, 1, 11, 10, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 10, 4, 1, 11, 4, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 1, 12, 4, 1, 12, 10, rand, CONCRETE_BRICKS);

            // Floor 1
            fillPillar(level, box, 5, 0, 1, 10, 0, 2);
            placePillar(level, box, 6, 0, 3, Direction.Axis.Z);
            fillWithBlocks(level, box, 2, 0, 4, 10, 0, 10, Blocks.OAK_PLANKS.defaultBlockState());
            fillWithBlocks(level, box, 3, 0, 11, 10, 0, 11, Blocks.OAK_PLANKS.defaultBlockState());
            fillWithBlocks(level, box, 4, 0, 12, 10, 0, 12, Blocks.OAK_PLANKS.defaultBlockState());
            fillWithBlocks(level, box, 1, 0, 11, 2, 0, 13, Blocks.GRAY_WOOL.defaultBlockState());
            fillWithBlocks(level, box, 3, 0, 12, 3, 0, 13, Blocks.GRAY_WOOL.defaultBlockState());

            // Floor 2
            fillPillar(level, box, 5, 4, 1, 5, 4, 3);
            fillWithBlocks(level, box, 2, 4, 4, 10, 4, 12, Blocks.OAK_PLANKS.defaultBlockState());
            fillWithBlocks(level, box, 1, 4, 11, 1, 4, 13, Blocks.OAK_PLANKS.defaultBlockState());
            fillWithBlocks(level, box, 2, 4, 13, 3, 4, 13, Blocks.OAK_PLANKS.defaultBlockState());

            // Floor 3
            fillPillar(level, box, 10, 8, 1, 10, 8, 3);
            fillWithBlocks(level, box, 2, 8, 4, 10, 8, 12, Blocks.OAK_PLANKS.defaultBlockState());
            fillWithBlocks(level, box, 1, 8, 11, 1, 8, 13, Blocks.OAK_PLANKS.defaultBlockState());
            fillWithBlocks(level, box, 2, 8, 13, 3, 8, 13, Blocks.OAK_PLANKS.defaultBlockState());

            // Ceiling
            fillPillar(level, box, 5, 12, 1, 5, 12, 2);
            fillWithBlocks(level, box, 10, 12, 1, 10, 12, 2, ModBlocks.ASPHALT.get().defaultBlockState());
            fillWithBlocks(level, box, 9, 13, 1, 9, 13, 2, ModBlocks.ASPHALT.get().defaultBlockState());
            fillWithBlocks(level, box, 8, 14, 1, 8, 14, 2, ModBlocks.ASPHALT.get().defaultBlockState());
            fillWithBlocks(level, box, 5, 15, 1, 7, 15, 2, ModBlocks.ASPHALT.get().defaultBlockState());
            fillWithBlocks(level, box, 2, 12, 4, 10, 12, 12, ModBlocks.BRICK_LIGHT.get().defaultBlockState());
            fillWithBlocks(level, box, 1, 12, 11, 1, 12, 13, ModBlocks.BRICK_LIGHT.get().defaultBlockState());
            fillWithBlocks(level, box, 2, 12, 13, 3, 12, 13, ModBlocks.BRICK_LIGHT.get().defaultBlockState());

            // Staircase
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 9, 1, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  true,  8, 1, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 8, 2, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  true,  7, 2, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 7, 3, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  true,  6, 3, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 6, 4, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  true,  6, 4, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  false, 6, 5, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  true,  7, 5, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  false, 7, 6, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  true,  8, 6, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  false, 8, 7, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  true,  9, 7, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  false, 9, 8, 2);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  true,  9, 8, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 9, 9, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  true,  8, 9, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 8, 10, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  true,  7, 10, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 7, 11, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  true,  6, 11, 1);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 6, 12, 1);

            // Windows
            randomlyFillWithBlocks(level, box, rand, 0.75F, 11, 10, 5, 11, 10, 7, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 11, 10, 9, 11, 10, 11, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 7, 2, 13, 8, 3, 13, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 6, 6, 13, 9, 7, 13, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 6, 10, 13, 9, 11, 13, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 1, 6, 14, 3, 7, 14, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 1, 10, 14, 3, 11, 14, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 0, 6, 11, 0, 7, 13, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 0, 10, 11, 0, 11, 13, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 1, 2, 6, 1, 3, 7, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 1, 6, 5, 1, 7, 8, Blocks.GLASS_PANE.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.75F, 1, 10, 5, 1, 11, 8, Blocks.GLASS_PANE.defaultBlockState());

            // Trim
            randomlyFillWithBlocks(level, box, rand, 0.85F, 10, 13, 0, 10, 13, 0, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 11, 13, 0, 11, 13, 13, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 4, 13, 13, 10, 13, 13, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 0, 13, 14, 4, 13, 14, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 0, 13, 10, 0, 13, 13, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 1, 13, 3, 1, 13, 10, Blocks.STONE_SLAB.defaultBlockState());
            randomlyFillWithBlocks(level, box, rand, 0.85F, 2, 13, 3, 3, 13, 3, Blocks.STONE_SLAB.defaultBlockState());

            // Interior Walls
            fillWithRandomizedBlocks(level, box, 4, 5, 12, 4, 6, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 5, 10, 4, 6, 10, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 7, 10, 4, 7, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 2, 5, 10, 3, 7, 10, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 4, 9, 10, 4, 11, 12, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 2, 11, 10, 3, 11, 10, rand, CONCRETE_BRICKS);
            fillWithRandomizedBlocks(level, box, 2, 9, 10, 2, 10, 10, rand, CONCRETE_BRICKS);

            // Doors
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, false, rand.nextBoolean(), 1, 1, 14);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, true,  rand.nextBoolean(), 2, 1, 14);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.EAST,  false, rand.nextBoolean(), 0, 1, 12);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.EAST,  true,  rand.nextBoolean(), 0, 1, 13);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, false, rand.nextBoolean(), 6, 1, 3);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, false, rand.nextBoolean(), 5, 5, 3);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.WEST,  false, rand.nextBoolean(), 4, 5, 11);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, false, rand.nextBoolean(), 10, 9, 3);
            placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.SOUTH, false, rand.nextBoolean(), 3, 9, 10);
            placeDoor(level, box, ModBlocks.DOOR_METAL.get(),  Direction.NORTH, false, rand.nextBoolean(), 5, 13, 3);

            // Furniture - Floor 1
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 2, 1, 5);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, false, 2, 1, 7);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 2, 1, 8);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, 2, 1, 9);

            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 8, 1, 4);
            fillWithBlocks(level, box, 8, 1, 5, 8, 1, 7, Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.SOUTH, true, 8, 1, 8);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST,  true, 9, 1, 8);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.EAST,  false, 9, 1, 5);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 9, 1, 4, box);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 8, 2, 7, box);

            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST,  false, 6, 1, 12);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, 7, 1, 12);
            placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST,  false, 8, 1, 12);

            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 10, 1, 11);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.SOUTH, true, 10, 1, 12);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 10, 2, 11, box);

            // Furniture - Floor 2
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST,  true, 4, 5, 4);
            placeBlockAtCurrentPosition(level, Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), 3, 5, 4, box);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST,  true, 2, 5, 4);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.SOUTH, false, 3, 5, 5);
            placeComputer(level, box, 3, 6, 4, Direction.NORTH);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST,  true, 3, 5, 7);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 4, 5, 7);
            placeBlockAtCurrentPosition(level, Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), 4, 5, 8, box);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.SOUTH, true, 4, 5, 9);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.WEST,  false, 2, 5, 9);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 10, 5, 4);
            placeBlockAtCurrentPosition(level, Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), 10, 5, 5, box);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.SOUTH, true, 10, 5, 6);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.SOUTH, false, 8, 5, 6);
            placeComputer(level, box, 10, 6, 6, Direction.EAST);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.SOUTH, true, 8, 5, 11);
            placeBlockAtCurrentPosition(level, Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), 8, 5, 10, box);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST,  true, 8, 5, 9);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 9, 5, 9);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST,  true, 10, 5, 9);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.EAST,  false, 10, 5, 10);
            placeComputer(level, box, 9, 6, 9, Direction.NORTH);

            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.SOUTH, true, 1, 5, 13);
            placeBlockAtCurrentPosition(level, Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), 1, 5, 12, box);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 1, 5, 11);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.SOUTH, false, 3, 5, 13);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 1, 6, 12, box);
            placeBlockAtCurrentPosition(level,
                    ModBlocks.MACHINE_MICROWAVE.get().defaultBlockState()
                            .setValue(com.hbm.blocks.machine.MachineMicrowave.FACING, rotate(Direction.EAST)),
                    1, 6, 11, box);

            // Furniture - Floor 3
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST,  true, 8, 9, 4);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST,  true, 7, 9, 4);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 9, 9, 4, box);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 5, 9, 5);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST,  true, 5, 9, 6);
            fillWithBlocks(level, box, 3, 9, 6, 4, 9, 6, Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST,  true, 2, 9, 6);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.NORTH, false, 3, 9, 5);
            placeComputer(level, box, 3, 10, 6, Direction.SOUTH);

            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST,  true, 9, 9, 10);
            fillWithBlocks(level, box, 7, 9, 10, 8, 9, 10, Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 6, 9, 10);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST,  true, 5, 9, 10);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST,  true, 5, 9, 11);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.SOUTH, true, 5, 9, 12);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.SOUTH, false, 8, 9, 11);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.WEST,  false, 7, 9, 8);
            placeStairs(level, box, Blocks.OAK_STAIRS,      Direction.NORTH, false, 9, 9, 8);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 6, 10, 10, box);
            placeComputer(level, box, 7, 10, 10, Direction.NORTH);
            placeBlockAtCurrentPosition(level,
                    ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                            .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.EAST)),
                    6, 9, 11, box);
            placeRandomBobble(level, box, rand, 5, 10, 11);

            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST,  true, 2, 9, 11);
            placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST,  true, 1, 9, 11);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 2, 10, 11, box);

            // Roof
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 5, 13, 9, box);
            placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 7, 13, 11, box);

            // Loot
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 9, 1, 7, ItemPoolsComponent.POOL_FILING_CABINET, 4);
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.SOUTH, 7, 5, 4, ItemPoolsComponent.POOL_FILING_CABINET, 4);
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.SOUTH, 7, 6, 4, ItemPoolsComponent.POOL_FILING_CABINET, 4);
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.WEST,  10, 5, 7, ItemPoolsComponent.POOL_FILING_CABINET, 4);
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 10, 5, 12, ItemPoolsComponent.POOL_FILING_CABINET, 4);
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 10, 6, 12, ItemPoolsComponent.POOL_FILING_CABINET, 4);
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 2, 9, 5, ItemPoolsComponent.POOL_FILING_CABINET, 4);

            generateLockableContents(level, box, rand, ModBlocks.SAFE.get(), Direction.NORTH, 1, 9, 13, ItemPoolsComponent.POOL_OFFICE_TRASH, 10, 1.0D);

            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 2, 9, 13, ItemPoolsComponent.POOL_FILING_CABINET, 4);
            generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 3, 9, 13, ItemPoolsComponent.POOL_FILING_CABINET, 4);
            generateLockableContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 3, 10, 13, ItemPoolsLegacy.POOL_EXPENSIVE, 8, 0.1D);

            placeBlockAtCurrentPosition(level, ModBlocks.DECO_LOOT.get().defaultBlockState(), 6, 13, 11, box);
            applyLoot(level, box, 6, 13, 11, LootGenerator.LOOT_CAPSTASH);
            placeBlockAtCurrentPosition(level, ModBlocks.DECO_LOOT.get().defaultBlockState(), 1, 10, 11, box);
            applyLoot(level, box, 1, 10, 11, LootGenerator.LOOT_MEDICINE);

            updateConnectedBlocks(level, box);
            return true;
        }

        private void placeComputer(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction localFacing) {
            Direction facing = rotate(localFacing).getOpposite();
            placeBlockAtCurrentPosition(level,
                    ModBlocks.DECO_COMPUTER.get().defaultBlockState().setValue(DecoComputer.FACING, facing),
                    x, y, z, box);
        }

        private Direction.Axis pillarAxis(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            boolean xLen = minX != maxX;
            boolean yLen = minY != maxY;
            boolean zLen = minZ != maxZ;
            Direction.Axis axis;
            if (xLen && !yLen && !zLen) axis = Direction.Axis.X;
            else if (!xLen && yLen && !zLen) axis = Direction.Axis.Y;
            else if (!xLen && !yLen && zLen) axis = Direction.Axis.Z;
            else axis = Direction.Axis.Y;
            if (coordBaseMode % 2 != 0) {
                if (axis == Direction.Axis.X) axis = Direction.Axis.Z;
                else if (axis == Direction.Axis.Z) axis = Direction.Axis.X;
            }
            return axis;
        }

        private void fillPillar(WorldGenLevel level, BoundingBox box,
                                int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            Direction.Axis axis = pillarAxis(minX, minY, minZ, maxX, maxY, maxZ);
            BlockState state = ModBlocks.CONCRETE_PILLAR.get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, axis);
            fillWithBlocks(level, box, minX, minY, minZ, maxX, maxY, maxZ, state);
        }

        private void placePillar(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction.Axis axis) {
            if (coordBaseMode % 2 != 0) {
                if (axis == Direction.Axis.X) axis = Direction.Axis.Z;
                else if (axis == Direction.Axis.Z) axis = Direction.Axis.X;
            }
            BlockState state = ModBlocks.CONCRETE_PILLAR.get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, axis);
            placeBlockAtCurrentPosition(level, state, x, y, z, box);
        }

        protected void applyLoot(WorldGenLevel level, BoundingBox box, int x, int y, int z, String lootName) {
            int posX = getXWithOffset(x, z);
            int posY = getYWithOffset(y);
            int posZ = getZWithOffset(x, z);
            if (!box.isInside(posX, posY, posZ)) return;
            BlockPos pos = new BlockPos(posX, posY, posZ);
            LootGenerator.applyLoot(level, pos, lootName);
        }
    }
}