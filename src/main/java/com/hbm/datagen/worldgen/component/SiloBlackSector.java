package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsComponent;
import com.hbm.itempool.ItemPoolsLegacy;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SiloBlackSector {

    public SiloBlackSector() { }

    public static void build(SiloComponent c, WorldGenLevel level, RandomSource rand, BoundingBox box) {
        BlockState concreteSmooth = ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState();
        BlockState colored15 = ModBlocks.CONCRETE_COLORED_BLACK.get().defaultBlockState();
        var bricks = new NTMComponent.ConcreteBricks();
        var stairs = new SiloComponent.ConcreteStairs();

        // Air
        c.fillWithAir(level, box, 27, 5, 13, 31, 6, 15);
        c.fillWithAir(level, box, 27, 7, 14, 31, 7, 14);
        c.fillWithAir(level, box, 28, 2, 11, 31, 3, 15);

        // Floor + ceiling
        c.fillWithBlocks(level, box, 28, 0, 11, 31, 0, 15, Blocks.DIRT.defaultBlockState());
        c.randomlyFillWithBlocks(level, box, rand, 0.5F, 28, 0, 11, 31, 0, 15, Blocks.DIRT.defaultBlockState());
        c.fillWithBlocks(level, box, 27, 4, 11, 31, 4, 15, concreteSmooth);
        c.fillWithBlocks(level, box, 27, 8, 12, 32, 8, 16, concreteSmooth);

        // Walls
        c.fillWithRandomizedBlocks(level, box, 27, 0, 10, 32, 4, 10, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 27, 5, 12, 32, 5, 12, rand, bricks);
        c.fillWithBlocks(level, box, 27, 6, 12, 32, 6, 12, colored15);
        c.fillWithRandomizedBlocks(level, box, 27, 7, 12, 32, 7, 12, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 32, 0, 11, 32, 4, 12, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 32, 0, 13, 32, 5, 15, rand, bricks);
        c.fillWithBlocks(level, box, 32, 6, 13, 32, 6, 15, colored15);
        c.fillWithRandomizedBlocks(level, box, 32, 7, 13, 32, 7, 15, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 27, 0, 16, 32, 5, 16, rand, bricks);
        c.fillWithBlocks(level, box, 27, 6, 16, 32, 6, 16, colored15);
        c.fillWithRandomizedBlocks(level, box, 27, 7, 16, 32, 7, 16, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 27, 0, 11, 27, 3, 15, rand, bricks);

        // Arches
        stairs.setFacing(c.rotate(Direction.NORTH));
        stairs.setTop(true);
        c.fillWithRandomizedBlocks(level, box, 27, 7, 15, 31, 7, 15, rand, stairs);
        stairs.setFacing(c.rotate(Direction.SOUTH));
        c.fillWithRandomizedBlocks(level, box, 27, 7, 13, 31, 7, 13, rand, stairs);

        // Water
        c.fillWithBlocks(level, box, 28, 1, 11, 31, 1, 15, Blocks.WATER.defaultBlockState());

        // Doorway + ladder
        c.fillWithBlocks(level, box, 26, 5, 14, 26, 6, 14, concreteSmooth);
        c.fillWithLadders(level, box, ModBlocks.LADDER_STEEL.get(), Direction.EAST, 31, 2, 15, 31, 4, 15);

        // Webs
        c.randomlyFillWithBlocks(level, box, rand, 0.15F, 27, 5, 13, 30, 6, 15, Blocks.COBWEB.defaultBlockState());
        c.randomlyFillWithBlocks(level, box, rand, 0.15F, 31, 6, 13, 31, 6, 15, Blocks.COBWEB.defaultBlockState());
        c.randomlyFillWithBlocks(level, box, rand, 0.15F, 27, 7, 14, 31, 7, 14, Blocks.COBWEB.defaultBlockState());

        // Reeds
        c.randomlyFillWithBlocks(level, box, rand, 0.15F, 28, 2, 11, 31, 2, 15, ModBlocks.REEDS_BOTTOM.get().defaultBlockState());

        // Pipes
        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_FRAMED_GREEN_RUSTED.get(), Direction.Axis.Z,
                28, 3, 12, 28, 3, 15);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 28, 3, 11, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_PIPE_RIM_GREEN_RUSTED.get().defaultBlockState(), 28, 2, 11, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 28, 0, 11, box);
        c.fillWithBlocks(level, box, 31, 1, 11, 31, 1, 12, ModBlocks.DECO_BERYLLIUM.get().defaultBlockState());
        c.fillWithDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.EAST, 31, 2, 11, 31, 2, 12);

        c.placeBlockAtCurrentPosition(level, ModBlocks.HEV_BATTERY.get().defaultBlockState(), 30, 2, 11, box);

        // Containers
        c.generateLockableContents(level, box, rand, ModBlocks.SAFE.get(), Direction.EAST,
                31, 5, 13, ItemPoolsComponent.POOL_FILING_CABINET, 5, 0.1D);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH,
                31, 5, 14, ItemPoolsLegacy.POOL_NUKE_TRASH, 5);
        c.generateInvContents(level, box, rand, ModBlocks.SAFE.get(), Direction.EAST,
                31, 5, 15, ItemPoolsComponent.POOL_FILING_CABINET, 5);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_IRON.get(), Direction.NORTH,
                30, 1, 11, ItemPoolsLegacy.POOL_EXPENSIVE, 7);

        // Mines
        c.fillWithMines(level, box, rand, 27, 5, 13, 30, 5, 15);
    }
                                 }
