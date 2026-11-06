package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsComponent;
import com.hbm.itempool.ItemPoolsLegacy;
import com.hbm.util.HbmChestContents;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Порт RuralHouse1 из CivilianFeatures 1.7.10.
 */
public class RuralHouse1 extends NTMComponent {

    public RuralHouse1() { }

    @Override
    public String getTypeName() { return "RuralHouse1"; }

    public RuralHouse1(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = CivilianFeatures.boxFromSize(minX, 64, minZ, 14, 8, 14, this.coordBaseMode);
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        if (!heightCalculated) {
            if (!setAverageHeight(level, box, this.boundingBox.minY())) return false;
            heightCalculated = true;
        }

        // ===== Air =====
        fillWithAir(level, box, 9, 1, 3, 12, 4, 8);
        fillWithAir(level, box, 5, 1, 2, 8, 3, 8);
        fillWithAir(level, box, 2, 1, 5, 4, 3, 8);
        fillWithAir(level, box, 2, 1, 10, 7, 3, 12);

        // ===== Foundations =====
        BlockState cc = ModBlocks.CONCRETE_COLORED_EXT_MACHINE.get().defaultBlockState();
        fillWithBlocks(level, box, 1, 0, 4, 4, 0, 4, cc);
        fillWithBlocks(level, box, 4, 0, 2, 4, 0, 3, cc);
        fillWithBlocks(level, box, 4, 0, 1, 9, 0, 1, cc);
        fillWithBlocks(level, box, 9, 0, 2, 10, 0, 2, cc);
        placeBlockAtCurrentPosition(level, cc, 12, 0, 2, box);
        fillWithBlocks(level, box, 13, 0, 2, 13, 0, 9, cc);
        fillWithBlocks(level, box, 5, 0, 9, 12, 0, 9, cc);
        fillWithBlocks(level, box, 2, 0, 9, 3, 0, 9, cc);
        placeBlockAtCurrentPosition(level, cc, 8, 0, 10, box);
        fillWithBlocks(level, box, 8, 0, 12, 8, 0, 13, cc);
        fillWithBlocks(level, box, 1, 0, 13, 7, 0, 13, cc);
        fillWithBlocks(level, box, 1, 0, 5, 1, 0, 12, cc);

        placeFoundationUnderneath(level, cc, 1, 10, 8, 13, -1, box);
        placeFoundationUnderneath(level, cc, 1, 4, 3, 9, -1, box);
        placeFoundationUnderneath(level, cc, 4, 1, 13, 9, -1, box);

        // ===== Log foundations =====
        fillLogFoundation(level, box, 2, 3, 2, 3, 0);
        fillLogFoundation(level, box, 3, 2, 3, 2, 0);
        fillLogFoundation(level, box, 3, 0, 3, 0, -1);
        fillLogFoundation(level, box, 5, 0, 5, 0, 0);
        fillLogFoundation(level, box, 8, 0, 8, 0, 0);
        fillLogFoundation(level, box, 10, 0, 10, 0, -1);
        fillLogFoundation(level, box, 14, 1, 14, 1, -1);
        fillLogFoundation(level, box, 14, 3, 14, 3, -1);
        fillLogFoundation(level, box, 14, 5, 14, 6, 0);
        fillLogFoundation(level, box, 14, 8, 14, 8, -1);
        fillLogFoundation(level, box, 14, 10, 14, 10, -1);
        fillLogFoundation(level, box, 9, 14, 9, 14, -1);
        fillLogFoundation(level, box, 7, 14, 7, 14, -1);
        fillLogFoundation(level, box, 4, 14, 5, 14, 0);
        fillLogFoundation(level, box, 2, 14, 2, 14, -1);
        fillLogFoundation(level, box, 0, 14, 0, 14, -1);
        fillLogFoundation(level, box, 0, 13, 0, 13, 0);
        fillLogFoundation(level, box, 0, 11, 0, 11, 0);
        fillLogFoundation(level, box, 0, 9, 0, 9, -1);
        fillLogFoundation(level, box, 0, 6, 0, 7, 0);
        fillLogFoundation(level, box, 0, 4, 0, 4, 0);
        fillLogFoundation(level, box, 0, 3, 0, 4, -1);

        // ===== Walls =====
        BlockState brick = Blocks.BRICKS.defaultBlockState();

        fillWithBlocks(level, box, 1, 1, 4, 4, 4, 4, brick);
        fillWithBlocks(level, box, 2, 5, 4, 7, 5, 4, brick);
        placeBlockAtCurrentPosition(level, brick, 3, 6, 4, box);
        placeBlockAtCurrentPosition(level, brick, 6, 6, 4, box);
        fillWithBlocks(level, box, 4, 7, 4, 5, 7, 4, brick);
        fillWithBlocks(level, box, 4, 1, 1, 4, 4, 3, brick);
        fillWithBlocks(level, box, 5, 1, 1, 8, 1, 1, brick);
        fillWithBlocks(level, box, 5, 4, 1, 8, 4, 1, brick);
        fillWithBlocks(level, box, 9, 1, 1, 9, 4, 2, brick);
        fillWithBlocks(level, box, 10, 1, 2, 10, 3, 2, brick);
        fillWithBlocks(level, box, 12, 1, 2, 13, 3, 2, brick);
        fillWithBlocks(level, box, 10, 4, 2, 13, 4, 2, brick);
        fillWithBlocks(level, box, 9, 5, 2, 12, 5, 2, brick);
        fillWithBlocks(level, box, 10, 6, 2, 11, 6, 2, brick);

        fillWithBlocks(level, box, 13, 1, 3, 13, 1, 8, brick);
        fillWithBlocks(level, box, 13, 3, 3, 13, 4, 8, brick);

        fillWithBlocks(level, box, 13, 1, 9, 13, 4, 9, brick);
        fillWithBlocks(level, box, 9, 1, 9, 12, 1, 9, brick);
        fillWithBlocks(level, box, 9, 4, 9, 12, 5, 9, brick);
        fillWithBlocks(level, box, 10, 6, 9, 11, 6, 9, brick);
        fillWithBlocks(level, box, 8, 1, 9, 8, 4, 10, brick);
        fillWithBlocks(level, box, 8, 1, 12, 8, 3, 13, brick);
        fillWithBlocks(level, box, 8, 4, 11, 8, 4, 13, brick);
        fillWithBlocks(level, box, 7, 1, 13, 7, 3, 13, brick);
        fillWithBlocks(level, box, 3, 1, 13, 6, 1, 13, brick);
        fillWithBlocks(level, box, 2, 4, 13, 7, 5, 13, brick);
        placeBlockAtCurrentPosition(level, brick, 6, 6, 13, box);
        placeBlockAtCurrentPosition(level, brick, 3, 6, 13, box);
        fillWithBlocks(level, box, 4, 7, 13, 5, 7, 13, brick);
        fillWithBlocks(level, box, 2, 1, 13, 2, 3, 13, brick);

        fillWithBlocks(level, box, 1, 1, 13, 1, 4, 13, brick);
        fillWithBlocks(level, box, 1, 1, 5, 1, 1, 12, brick);
        placeBlockAtCurrentPosition(level, brick, 1, 2, 9, box);
        fillWithBlocks(level, box, 1, 3, 5, 1, 3, 12, brick);

        fillWithBlocks(level, box, 2, 1, 9, 3, 3, 9, brick);
        fillWithBlocks(level, box, 5, 1, 9, 7, 3, 9, brick);

        // ===== Wood panels =====
        BlockState planks = Blocks.OAK_PLANKS.defaultBlockState();
        BlockState plankTop = Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);
        BlockState plankBottom = Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);

        fillWithBlocks(level, box, 5, 2, 1, 5, 3, 1, planks);
        fillWithBlocks(level, box, 8, 2, 1, 8, 3, 1, planks);
        placeBlockAtCurrentPosition(level, planks, 11, 3, 2, box);
        fillWithBlocks(level, box, 13, 2, 3, 13, 2, 4, planks);
        fillWithBlocks(level, box, 13, 2, 7, 13, 2, 8, planks);
        fillWithBlocks(level, box, 12, 2, 9, 12, 3, 9, planks);
        fillWithBlocks(level, box, 9, 2, 9, 9, 3, 9, planks);
        placeBlockAtCurrentPosition(level, planks, 8, 3, 11, box);
        fillWithBlocks(level, box, 6, 2, 13, 6, 3, 13, planks);
        fillWithBlocks(level, box, 3, 2, 13, 3, 3, 13, planks);
        placeBlockAtCurrentPosition(level, planks, 1, 2, 12, box);
        placeBlockAtCurrentPosition(level, planks, 1, 2, 10, box);
        placeBlockAtCurrentPosition(level, planks, 1, 2, 8, box);
        placeBlockAtCurrentPosition(level, planks, 1, 2, 5, box);
        placeBlockAtCurrentPosition(level, planks, 4, 3, 9, box);

        // ===== Log framing =====
        BlockState log = Blocks.OAK_LOG.defaultBlockState();
        fillWithBlocks(level, box, 0, 0, 3, 0, 3, 3, log);
        fillWithBlocks(level, box, 1, 4, 3, 3, 4, 3, log);
        fillWithBlocks(level, box, 3, 4, 1, 3, 4, 2, log);
        placeBlockAtCurrentPosition(level, plankTop, 1, 3, 3, box);
        placeBlockAtCurrentPosition(level, plankTop, 3, 3, 1, box);
        fillWithBlocks(level, box, 1, 1, 3, 2, 1, 3, plankBottom);
        fillWithBlocks(level, box, 3, 1, 1, 3, 1, 3, plankBottom);
        fillWithBlocks(level, box, 3, 0, 0, 3, 3, 0, log);
        fillWithBlocks(level, box, 4, 1, 0, 9, 1, 0, plankBottom);
        placeBlockAtCurrentPosition(level, plankTop, 4, 3, 0, box);
        placeBlockAtCurrentPosition(level, plankTop, 9, 3, 0, box);
        fillWithBlocks(level, box, 10, 0, 0, 10, 3, 0, log);
        fillWithBlocks(level, box, 10, 4, 1, 13, 4, 1, log);
        fillWithBlocks(level, box, 14, 0, 1, 14, 3, 1, log);

        fillWithBlocks(level, box, 14, 0, 3, 14, 3, 3, log);
        fillWithBlocks(level, box, 14, 0, 8, 14, 3, 8, log);
        fillWithBlocks(level, box, 14, 0, 10, 14, 3, 10, log);
        placeBlockAtCurrentPosition(level, plankBottom, 14, 1, 2, box);
        fillWithBlocks(level, box, 14, 1, 4, 14, 1, 7, plankBottom);
        placeBlockAtCurrentPosition(level, plankBottom, 14, 1, 9, box);
        placeBlockAtCurrentPosition(level, plankTop, 14, 3, 2, box);
        placeBlockAtCurrentPosition(level, plankTop, 14, 3, 4, box);
        placeBlockAtCurrentPosition(level, plankTop, 14, 3, 7, box);
        placeBlockAtCurrentPosition(level, plankTop, 14, 3, 9, box);

        fillWithBlocks(level, box, 9, 4, 10, 13, 4, 10, log);
        placeBlockAtCurrentPosition(level, plankTop, 13, 3, 10, box);
        fillWithBlocks(level, box, 9, 0, 14, 9, 3, 14, log);
        fillWithBlocks(level, box, 7, 0, 14, 7, 3, 14, log);
        fillWithBlocks(level, box, 2, 0, 14, 2, 3, 14, log);
        fillWithBlocks(level, box, 0, 0, 14, 0, 3, 14, log);
        fillWithBlocks(level, box, 1, 4, 14, 8, 4, 14, log);
        placeBlockAtCurrentPosition(level, plankBottom, 8, 1, 14, box);
        fillWithBlocks(level, box, 3, 1, 14, 6, 1, 14, plankBottom);
        placeBlockAtCurrentPosition(level, plankBottom, 1, 1, 14, box);
        placeBlockAtCurrentPosition(level, plankTop, 8, 3, 14, box);
        placeBlockAtCurrentPosition(level, plankTop, 1, 3, 14, box);

        fillWithBlocks(level, box, 0, 0, 9, 0, 3, 9, log);
        fillWithBlocks(level, box, 0, 1, 10, 0, 1, 13, plankBottom);
        fillWithBlocks(level, box, 0, 1, 4, 0, 1, 8, plankBottom);
        placeBlockAtCurrentPosition(level, plankTop, 0, 3, 13, box);
        placeBlockAtCurrentPosition(level, plankTop, 0, 3, 10, box);
        placeBlockAtCurrentPosition(level, plankTop, 0, 3, 8, box);
        placeBlockAtCurrentPosition(level, plankTop, 0, 3, 4, box);

        // ===== Floor =====
        placeBlockAtCurrentPosition(level, planks, 11, 0, 2, box);
        fillWithBlocks(level, box, 9, 0, 3, 12, 0, 8, planks);
        fillWithBlocks(level, box, 5, 0, 2, 8, 0, 8, planks);
        fillWithBlocks(level, box, 2, 0, 5, 4, 0, 8, planks);
        placeBlockAtCurrentPosition(level, planks, 4, 0, 9, box);
        fillWithBlocks(level, box, 2, 0, 10, 7, 0, 12, planks);
        placeBlockAtCurrentPosition(level, planks, 8, 0, 11, box);

        fillWithBlocks(level, box, 13, 1, 0, 14, 1, 0, Blocks.OAK_FENCE.defaultBlockState());

        // Porches (ступеньки инвертированы)
        fillWithBlocks(level, box, 10, 0, 1, 13, 0, 1, planks);
        fillWithStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.SOUTH, false, 11, 0, 0, 12, 0, 0); 
        fillWithBlocks(level, box, 13, 0, 0, 14, 0, 0, planks);
        fillWithBlocks(level, box, 12, 0, 10, 13, 0, 10, planks);
        fillWithBlocks(level, box, 9, 0, 10, 11, 0, 11, planks);
        fillWithBlocks(level, box, 9, 0, 12, 10, 0, 12, planks);
        placeBlockAtCurrentPosition(level, planks, 9, 0, 13, box);
        for (int i = 0; i < 3; i++) {
            fillWithBlocks(level, box, 10 + i, 0, 13 - i, 11 + i, 0, 13 - i, planks);
            fillWithBlocks(level, box, 10 + i, 1, 13 - i, 11 + i, 1, 13 - i, Blocks.OAK_FENCE.defaultBlockState());
        }

        // ===== Ceiling (ступеньки инвертированы) =====
        fillWithStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, true, 12, 4, 3, 12, 4, 8); 
        fillWithBlocks(level, box, 12, 5, 3, 12, 5, 8, planks);
        fillWithBlocks(level, box, 10, 5, 3, 11, 6, 8, planks);
        fillWithBlocks(level, box, 9, 5, 3, 9, 5, 8, planks);
        fillWithStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, true, 9, 4, 3, 9, 4, 8); 
        fillWithBlocks(level, box, 8, 4, 5, 8, 4, 8, planks);
        fillWithBlocks(level, box, 5, 4, 2, 8, 4, 4, planks);
        fillWithBlocks(level, box, 1, 4, 5, 7, 4, 12, planks);

        // ===== Roof (ступеньки инвертированы) =====
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 1, 5, 3); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 2, 6, 3); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 3, 6, 3); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 3, 7, 3); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 4, 7, 3); 
        fillWithBlocks(level, box, 4, 8, 3, 5, 8, 3, plankBottom);
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 5, 7, 3); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 6, 7, 3); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 6, 6, 3); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 7, 6, 3); 
        fillWithBlocks(level, box, 2, 5, 3, 3, 5, 3, planks);
        placeBlockAtCurrentPosition(level, planks, 3, 5, 2, box);
        placeBlockAtCurrentPosition(level, plankBottom, 3, 5, 1, box);
        fillWithStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.SOUTH, false, 3, 4, 0, 14, 4, 0); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 8, 5, 1); 
        placeBlockAtCurrentPosition(level, planks, 9, 5, 1, box);
        placeBlockAtCurrentPosition(level, plankBottom, 10, 5, 1, box);
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 9, 6, 1); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 10, 6, 1); 
        fillWithBlocks(level, box, 10, 7, 1, 11, 7, 1, plankBottom);
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 11, 6, 1); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 12, 6, 1); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 12, 5, 1); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 13, 5, 1); 
        fillWithStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 14, 4, 1, 14, 4, 10); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 13, 5, 10); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 12, 5, 10); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 12, 6, 10); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 11, 6, 10); 
        fillWithBlocks(level, box, 10, 7, 10, 11, 7, 10, plankBottom);
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 10, 6, 10); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 9, 6, 10); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 9, 5, 10); 
        fillWithStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 9, 4, 11, 9, 4, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 8, 5, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 7, 5, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 7, 6, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 6, 6, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, false, 6, 7, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 5, 7, 14); 
        fillWithBlocks(level, box, 4, 8, 14, 5, 8, 14, plankBottom);
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 4, 7, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 3, 7, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 3, 6, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 2, 6, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 2, 5, 14); 
        placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 1, 5, 14); 
        fillWithStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, false, 0, 4, 3, 0, 4, 14); 

        for (int z = 6; z <= 11; z += 5) {
            for (int i = 0; i < 3; i++) {
                placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.WEST, true, 2 + i, 5 + i, z); 
                placeStairs(level, box, Blocks.SPRUCE_STAIRS, Direction.EAST, true, 7 - i, 5 + i, z); 
            }
        }

        // ===== Main roof with breakage =====
        CivilianFeatures.BrokenStairs roofStairs = new CivilianFeatures.BrokenStairs(this.coordBaseMode);
        CivilianFeatures.BrokenBlocks roofBlocks = new CivilianFeatures.BrokenBlocks();

        roofStairs.setMetadata(0); // W
        fillWithBlocks(level, box, 4, 5, 1, 7, 5, 1, plankBottom);
        fillWithRandomizedBlocks(level, box, 4, 5, 2, 7, 5, 3, rand, roofBlocks);
        fillWithRandomizedBlocks(level, box, 8, 5, 2, 8, 5, 10, rand, roofBlocks);
        fillWithRandomizedBlocks(level, box, 9, 6, 2, 9, 6, 9, rand, roofStairs);
        randomlyFillWithBlocks(level, box, rand, 0.8F, 10, 7, 2, 11, 7, 9, plankBottom);
        roofStairs.setMetadata(1); // E
        fillWithRandomizedBlocks(level, box, 12, 6, 2, 12, 6, 9, rand, roofStairs);
        fillWithRandomizedBlocks(level, box, 13, 5, 2, 13, 5, 9, rand, roofStairs);

        fillWithRandomizedBlocks(level, box, 8, 5, 11, 8, 5, 13, rand, roofStairs);
        fillWithRandomizedBlocks(level, box, 7, 6, 4, 7, 6, 13, rand, roofStairs);
        fillWithRandomizedBlocks(level, box, 6, 7, 4, 6, 7, 7, rand, roofStairs);
        fillWithRandomizedBlocks(level, box, 6, 7, 11, 6, 7, 13, rand, roofStairs);
        roofStairs.setMetadata(0); // W
        fillWithBlocks(level, box, 4, 8, 4, 5, 8, 5, plankBottom);
        placeBlockAtCurrentPosition(level, plankBottom, 5, 8, 6, box);
        placeBlockAtCurrentPosition(level, plankBottom, 4, 8, 11, box);
        fillWithBlocks(level, box, 4, 8, 12, 5, 8, 13, plankBottom);
        fillWithRandomizedBlocks(level, box, 3, 7, 4, 3, 7, 6, rand, roofStairs);
        fillWithRandomizedBlocks(level, box, 3, 7, 10, 3, 7, 13, rand, roofStairs);
        fillWithRandomizedBlocks(level, box, 2, 6, 4, 2, 6, 13, rand, roofStairs);
        fillWithRandomizedBlocks(level, box, 1, 5, 4, 1, 5, 13, rand, roofStairs);

        // Webs
        randomlyFillWithBlocks(level, box, rand, 0.05F, 12, 3, 3, 12, 3, 8, Blocks.COBWEB.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.05F, 10, 4, 3, 11, 4, 8, Blocks.COBWEB.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.05F, 5, 3, 2, 8, 3, 2, Blocks.COBWEB.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.05F, 5, 3, 3, 9, 3, 8, Blocks.COBWEB.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.05F, 2, 3, 5, 4, 3, 8, Blocks.COBWEB.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.05F, 2, 3, 10, 7, 3, 12, Blocks.COBWEB.defaultBlockState());

        // Doors (двери инвертированы)
        placeDoor(level, box, Blocks.OAK_DOOR, Direction.NORTH, false, false, 11, 1, 2); 
        placeDoor(level, box, Blocks.OAK_DOOR, Direction.NORTH, false, false, 4, 1, 9); 
        placeDoor(level, box, Blocks.OAK_DOOR, Direction.EAST, false, false, 8, 1, 11); 

        // Windows
        randomlyFillWithBlocks(level, box, rand, 0.5F, 6, 2, 1, 7, 3, 1, Blocks.GLASS_PANE.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.5F, 13, 2, 5, 13, 2, 6, Blocks.GLASS_PANE.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.5F, 10, 2, 9, 11, 3, 9, Blocks.GLASS_PANE.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.5F, 4, 2, 13, 5, 3, 13, Blocks.GLASS_PANE.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.5F, 1, 2, 11, 1, 2, 11, Blocks.GLASS_PANE.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.5F, 1, 2, 6, 1, 2, 7, Blocks.GLASS_PANE.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.5F, 4, 6, 4, 5, 6, 4, Blocks.GLASS_PANE.defaultBlockState());
        randomlyFillWithBlocks(level, box, rand, 0.5F, 4, 6, 13, 5, 6, 13, Blocks.GLASS_PANE.defaultBlockState());

        // Attic access
        placeTrapdoor(level, box, Blocks.OAK_TRAPDOOR, Direction.SOUTH, false, true, 6, 4, 10); 
        fillWithLadders(level, box, Blocks.LADDER, Direction.SOUTH, 6, 2, 10, 6, 3, 10); 

        // ===== Furniture (ступеньки инвертированы) =====
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, true, 12, 1, 5); 
        placeBlockAtCurrentPosition(level, plankTop, 12, 1, 6, box);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, true, 12, 1, 7); 
        fillWithStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST, true, 9, 1, 4, 9, 1, 5); 
        fillWithBlocks(level, box, 8, 1, 4, 8, 1, 5, plankTop);
        fillWithStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST, true, 7, 1, 4, 7, 1, 5); 
        placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 8, 1, 2); 
        placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.EAST, false, 7, 1, 2); 
        placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, false, 6, 1, 2); 
        fillWithStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.WEST, false, 5, 1, 2, 5, 1, 3); 
        placeStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.SOUTH, false, 5, 1, 4); 
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, false, 10, 1, 5); 
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, 8, 1, 6); 
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 9, 1, 8); 
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, true, 9, 2, 8); 
        fillWithBlocks(level, box, 8, 1, 8, 8, 2, 8, Blocks.BOOKSHELF.defaultBlockState());
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, false, 7, 1, 8); 
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, true, 7, 2, 8); 
        fillWithBlocks(level, box, 7, 3, 8, 9, 3, 8, plankBottom);

        placeBlockAtCurrentPosition(level,
                Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE),
                4, 1, 5, box);
        BlockState furnaceOrMachine = rand.nextBoolean()
                ? ModBlocks.MACHINE_ELECTRIC_FURNACE.get().defaultBlockState()
                : Blocks.FURNACE.defaultBlockState();
        placeDeco(level, box, furnaceOrMachine.getBlock(), Direction.SOUTH, 3, 1, 5); 

        fillWithBlocks(level, box, 2, 1, 5, 2, 1, 6,
                Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE));
        placeBlockAtCurrentPosition(level, Blocks.CAULDRON.defaultBlockState(), 2, 1, 7, box);
        placeBlockAtCurrentPosition(level,
                Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE),
                2, 1, 8, box);
        placeBlockAtCurrentPosition(level,
                Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE),
                4, 3, 5, box);
        placeBlockAtCurrentPosition(level, Blocks.REDSTONE_LAMP.defaultBlockState(), 3, 3, 5, box);
        placeBlockAtCurrentPosition(level,
                Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE),
                2, 3, 5, box);
        placeDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.SOUTH, 3, 3, 6); 
        placeDeco(level, box, ModBlocks.RADIOREC.get(), Direction.SOUTH, 8, 2, 2); 
        placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 7, 2, 4, box);

        fillWithBlocks(level, box, 2, 1, 12, 3, 1, 12, Blocks.BOOKSHELF.defaultBlockState());
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, true, 4, 1, 12); 
        placeBlockAtCurrentPosition(level, plankTop, 5, 1, 12, box);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, true, 6, 1, 12); 
        fillWithBlocks(level, box, 7, 1, 12, 7, 2, 12, Blocks.BOOKSHELF.defaultBlockState());
        placeBlockAtCurrentPosition(level, plankTop, 5, 1, 11, box);
        placeBed(level, box, Direction.WEST, 3, 1, 10); 
        placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 4, 2, 12, box);
        placeDeco(level, box, ModBlocks.DECO_COMPUTER.get(), Direction.NORTH, 5, 2, 12);

        fillWithStairs(level, box, Blocks.DARK_OAK_STAIRS, Direction.NORTH, true, 4, 5, 5, 5, 5, 5); 
        placeBlockAtCurrentPosition(level, plankBottom, 4, 5, 6, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 7, 5, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 2, 5, 9, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 3, 5, 11, box);
        if (rand.nextBoolean()) {
            placeDeco(level, box, ModBlocks.MACHINE_DIESEL.get(), Direction.WEST, 7, 5, 9); 
        }
        BlockState crate = rand.nextBoolean()
                ? ModBlocks.CRATE_WEAPON.get().defaultBlockState()
                : ModBlocks.CRATE.get().defaultBlockState();
        placeBlockAtCurrentPosition(level, crate, 6, 5, 12, box);

        // Inventories
        generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(),
                Direction.SOUTH, 7, 1, 10, ItemPoolsComponent.POOL_OFFICE_TRASH, 4); 
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.WEST,
                7, 5, 5, ItemPoolsLegacy.POOL_GENERIC, 8); 

        // Loot
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_LOOT.get().defaultBlockState(), 3, 2, 12, box);
        applyLoreBook(level, box, 3, 2, 12, HbmChestContents.generateLabBook(rand));
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_LOOT.get().defaultBlockState(), 5, 6, 5, box);
        applyMakeshiftGun(level, box, 5, 6, 5);
        placeRandomBobble(level, box, rand, 5, 5, 12);

        updateConnectedBlocks(level, box);
        return true;
    }

    private void fillLogFoundation(WorldGenLevel level, BoundingBox box,
                                   int fMinX, int fMinZ, int fMaxX, int fMaxZ, int yOffset) {
        placeFoundationUnderneath(level, Blocks.OAK_LOG.defaultBlockState(),
                Math.min(fMinX, fMaxX), Math.min(fMinZ, fMaxZ),
                Math.max(fMinX, fMaxX), Math.max(fMinZ, fMaxZ), yOffset, box);
    }
}