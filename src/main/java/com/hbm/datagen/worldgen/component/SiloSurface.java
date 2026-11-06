package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsLegacy;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SiloSurface {

    public SiloSurface() { }

    public static void build(SiloComponent c, WorldGenLevel level, RandomSource rand, BoundingBox box) {
        BlockState asphalt = ModBlocks.ASPHALT.get().defaultBlockState();
        BlockState concreteExt = ModBlocks.CONCRETE_COLORED_EXT_HAZARD.get().defaultBlockState();
        BlockState concreteSmooth = ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState();
        BlockState fenceMetal = ModBlocks.FENCE_METAL.get().defaultBlockState();
        BlockState barbed = ModBlocks.BARBED_WIRE.get().defaultBlockState();
        BlockState concretePillar = ModBlocks.CONCRETE_PILLAR.get().defaultBlockState();
        BlockState concreteSlab = ModBlocks.CONCRETE_SLAB_CONCRETE.get().defaultBlockState();
        BlockState decoSteel = ModBlocks.DECO_STEEL.get().defaultBlockState();
        var bricks = new NTMComponent.ConcreteBricks();
        var stairs = new SiloComponent.ConcreteStairs();

        c.fillWithAir(level, box, 13, 26, 2, 42, 36, 20);
        c.placeFoundationUnderneath(level, concreteExt, 13, 2, 42, 20, 24, box);

        // Floor
        c.fillWithBlocks(level, box, 13, 25, 2, 42, 25, 4, asphalt);
        c.fillWithBlocks(level, box, 13, 25, 5, 34, 25, 9, asphalt);
        c.fillWithBlocks(level, box, 13, 25, 10, 14, 25, 18, asphalt);
        c.fillWithBlocks(level, box, 24, 25, 10, 35, 25, 12, asphalt);
        c.fillWithBlocks(level, box, 24, 25, 13, 26, 25, 18, asphalt);
        c.fillWithBlocks(level, box, 13, 25, 19, 42, 25, 20, asphalt);
        c.fillWithBlocks(level, box, 40, 25, 5, 42, 25, 18, asphalt);
        c.fillWithBlocks(level, box, 39, 25, 10, 39, 25, 12, asphalt);

        c.fillWithBlocks(level, box, 15, 25, 10, 23, 25, 10, concreteExt);
        c.fillWithBlocks(level, box, 15, 25, 11, 15, 25, 17, concreteExt);
        c.fillWithBlocks(level, box, 15, 25, 18, 23, 25, 18, concreteExt);
        c.fillWithBlocks(level, box, 23, 25, 11, 23, 25, 17, concreteExt);
        c.placeBlockAtCurrentPosition(level, concreteExt, 16, 25, 11, box);
        c.placeBlockAtCurrentPosition(level, concreteExt, 22, 25, 11, box);
        c.placeBlockAtCurrentPosition(level, concreteExt, 22, 25, 17, box);

        c.fillWithRandomizedBlocks(level, box, 27, 25, 13, 39, 25, 18, rand, bricks);
        c.fillWithBlocks(level, box, 36, 25, 4, 38, 25, 4, concreteSmooth);
        c.fillWithBlocks(level, box, 35, 25, 5, 39, 25, 9, concreteSmooth);

        // Fences (N)
        c.fillWithBlocks(level, box, 13, 26, 2, 13, 28, 2, decoSteel);
        c.fillWithBlocks(level, box, 42, 26, 2, 42, 28, 2, decoSteel);
        c.fillWithBlocks(level, box, 13, 26, 20, 13, 28, 20, decoSteel);
        c.fillWithBlocks(level, box, 42, 26, 20, 42, 28, 20, decoSteel);

        c.fillWithBlocks(level, box, 38, 26, 2, 41, 27, 2, fenceMetal);
        c.fillWithBlocks(level, box, 34, 26, 2, 36, 27, 2, fenceMetal);
        c.fillWithBlocks(level, box, 30, 26, 2, 31, 27, 2, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 28, 27, 2, box);
        c.fillWithBlocks(level, box, 22, 26, 2, 28, 26, 2, fenceMetal);
        c.fillWithBlocks(level, box, 23, 27, 2, 26, 27, 2, fenceMetal);
        c.fillWithBlocks(level, box, 18, 26, 2, 20, 26, 2, fenceMetal);
        c.fillWithBlocks(level, box, 14, 26, 2, 16, 26, 2, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 14, 27, 2, box);
        BlockState barbedN = c.applyFacing(barbed, c.rotate(Direction.NORTH));
        c.fillWithBlocks(level, box, 38, 28, 2, 41, 28, 2, barbedN);
        c.fillWithBlocks(level, box, 35, 28, 2, 36, 28, 2, barbedN);
        c.fillWithBlocks(level, box, 23, 28, 2, 25, 28, 2, barbedN);
        c.placeBlockAtCurrentPosition(level, barbedN, 14, 28, 2, box);

        // W
        c.fillWithBlocks(level, box, 13, 26, 3, 13, 27, 4, fenceMetal);
        c.fillWithBlocks(level, box, 13, 26, 5, 13, 26, 6, fenceMetal);
        c.fillWithBlocks(level, box, 13, 26, 9, 13, 27, 9, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 13, 26, 11, box);
        c.fillWithBlocks(level, box, 13, 26, 12, 13, 27, 19, fenceMetal);
        BlockState barbedW = c.applyFacing(barbed, c.rotate(Direction.WEST));
        c.fillWithBlocks(level, box, 13, 28, 3, 13, 28, 4, barbedW);
        c.fillWithBlocks(level, box, 13, 28, 15, 13, 28, 19, barbedW);

        // E
        c.fillWithBlocks(level, box, 42, 26, 3, 42, 27, 4, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 42, 26, 7, box);
        c.fillWithBlocks(level, box, 42, 26, 9, 42, 26, 12, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 42, 26, 14, box);
        c.fillWithBlocks(level, box, 42, 26, 15, 42, 27, 19, fenceMetal);
        BlockState barbedE = c.applyFacing(barbed, c.rotate(Direction.EAST));
        c.fillWithBlocks(level, box, 42, 28, 3, 42, 28, 4, barbedE);
        c.fillWithBlocks(level, box, 42, 28, 15, 42, 28, 19, barbedE);

        // S
        c.fillWithBlocks(level, box, 14, 26, 20, 17, 27, 20, fenceMetal);
        c.fillWithBlocks(level, box, 18, 26, 20, 22, 26, 20, fenceMetal);
        c.fillWithBlocks(level, box, 20, 27, 20, 21, 27, 20, fenceMetal);
        c.fillWithBlocks(level, box, 24, 26, 20, 25, 26, 20, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 27, 26, 20, box);
        c.fillWithBlocks(level, box, 29, 26, 20, 32, 27, 20, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 33, 26, 20, box);
        c.fillWithBlocks(level, box, 35, 26, 20, 37, 26, 20, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 36, 27, 20, box);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 39, 26, 20, box);
        c.fillWithBlocks(level, box, 40, 26, 20, 41, 27, 20, fenceMetal);
        BlockState barbedS = c.applyFacing(barbed, c.rotate(Direction.SOUTH));
        c.fillWithBlocks(level, box, 14, 28, 20, 17, 28, 20, barbedS);
        c.fillWithBlocks(level, box, 29, 28, 20, 32, 28, 20, barbedS);
        c.fillWithBlocks(level, box, 40, 28, 20, 41, 28, 20, barbedS);

        // Defense platforms
        c.placeBlockAtCurrentPosition(level, concretePillar, 27, 26, 13, box);
        c.placeBlockAtCurrentPosition(level, concretePillar, 32, 26, 13, box);
        c.placeBlockAtCurrentPosition(level, concretePillar, 27, 26, 18, box);
        c.placeBlockAtCurrentPosition(level, concretePillar, 32, 26, 18, box);
        c.fillWithRandomizedBlocks(level, box, 28, 26, 14, 31, 26, 17, rand, bricks);

        stairs.setFacing(c.rotate(Direction.NORTH));
        c.fillWithRandomizedBlocks(level, box, 28, 26, 13, 31, 26, 13, rand, stairs);
        stairs.setFacing(c.rotate(Direction.WEST));
        c.fillWithRandomizedBlocks(level, box, 27, 26, 14, 27, 26, 17, rand, stairs);
        stairs.setFacing(c.rotate(Direction.SOUTH));
        c.fillWithRandomizedBlocks(level, box, 28, 26, 18, 31, 26, 18, rand, stairs);

        c.fillWithBlocks(level, box, 27, 27, 13, 32, 27, 13, concreteSlab);
        c.fillWithBlocks(level, box, 27, 27, 14, 27, 27, 17, concreteSlab);
        c.fillWithBlocks(level, box, 27, 27, 18, 32, 27, 18, concreteSlab);
        c.fillWithBlocks(level, box, 32, 27, 14, 32, 27, 17, concreteSlab);

        // Methusalem
        c.placeCore(level, box, ModBlocks.TURRET_HOWARD_DAMAGED.get(), Direction.SOUTH, 29, 27, 15);
        c.fillSpace(level, box, 29, 27, 15, new int[] { 0, 0, 1, 0, 1, 0 },
                ModBlocks.TURRET_HOWARD_DAMAGED.get(), Direction.SOUTH);

        // Destroyed platform
        c.placeBlockAtCurrentPosition(level, concretePillar, 34, 26, 13, box);
        c.placeBlockAtCurrentPosition(level, concretePillar, 39, 26, 13, box);
        c.placeBlockAtCurrentPosition(level, concretePillar, 34, 26, 18, box);
        c.placeBlockAtCurrentPosition(level, concretePillar, 39, 26, 18, box);
        c.fillWithRandomizedBlocks(level, box, 35, 26, 13, 38, 26, 13, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 32, 26, 15, 34, 26, 17, rand, bricks);
        stairs.setFacing(c.rotate(Direction.SOUTH));
        c.fillWithRandomizedBlocks(level, box, 35, 26, 18, 38, 26, 18, rand, stairs);
        stairs.setFacing(c.rotate(Direction.EAST));
        c.fillWithRandomizedBlocks(level, box, 39, 26, 14, 39, 26, 15, rand, stairs);

        var destroyed = new SiloComponent.DestroyedBricks();
        c.fillWithRandomizedBlocks(level, box, 35, 26, 14, 38, 26, 17, rand, destroyed);
        c.fillWithBlocks(level, box, 33, 27, 15, 33, 27, 17, concreteSlab);
        c.placeBlockAtCurrentPosition(level, concreteSlab, 34, 27, 17, box);
        c.fillWithBlocks(level, box, 34, 27, 18, 36, 27, 18, concreteSlab);
        c.fillWithBlocks(level, box, 37, 27, 13, 39, 27, 13, concreteSlab);
        c.placeBlockAtCurrentPosition(level, concreteSlab, 39, 27, 14, box);
        c.placeBlockAtCurrentPosition(level, decoSteel, 37, 25, 15, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_PIPE_RIM_RUSTED.get().defaultBlockState(), 37, 26, 15, box);
        c.placeBlockAtCurrentPosition(level, decoSteel, 36, 25, 16, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_PIPE_QUAD_RUSTED.get().defaultBlockState(), 36, 26, 16, box);

        // Access building
        c.fillWithRandomizedBlocks(level, box, 35, 26, 5, 39, 28, 5, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 35, 26, 6, 35, 28, 9, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 39, 26, 6, 39, 28, 9, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 36, 26, 9, 38, 28, 10, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 36, 27, 11, 38, 27, 11, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 36, 26, 12, 38, 26, 12, rand, bricks);
        stairs.setFacing(c.rotate(Direction.SOUTH));
        c.fillWithRandomizedBlocks(level, box, 36, 28, 11, 38, 28, 11, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 36, 27, 12, 38, 27, 12, rand, stairs);

        c.fillWithBlocks(level, box, 36, 29, 5, 38, 29, 9, ModBlocks.CONCRETE.get().defaultBlockState());
        c.fillWithStairs(level, box, ModBlocks.CONCRETE_STAIRS.get(), Direction.WEST, false, 35, 29, 5, 35, 29, 9);
        c.fillWithStairs(level, box, ModBlocks.CONCRETE_STAIRS.get(), Direction.SOUTH, false, 36, 29, 10, 38, 29, 10);
        c.fillWithStairs(level, box, ModBlocks.CONCRETE_STAIRS.get(), Direction.EAST, false, 39, 29, 5, 39, 29, 9);

        c.placeBlockAtCurrentPosition(level, Blocks.IRON_BARS.defaultBlockState(), 35, 27, 7, box);
        c.placeBlockAtCurrentPosition(level, Blocks.IRON_BARS.defaultBlockState(), 39, 27, 7, box);
        c.placeDoor(level, box, ModBlocks.DOOR_METAL.get(), Direction.SOUTH, false, rand.nextBoolean(), 37, 26, 5);

        // Tent
        for (int j = 4; j <= 8; j += 2) {
            c.placeBlockAtCurrentPosition(level, ModBlocks.STEEL_BEAM.get().defaultBlockState(), 20, 26, j, box);
            c.fillWithBlocks(level, box, 16, 26, j, 16, 27, j, ModBlocks.STEEL_BEAM.get().defaultBlockState());
        }
        BlockState brickSlab = ModBlocks.BRICK_SLAB_REINFORCED_STONE.get().defaultBlockState();
        c.fillWithBlocks(level, box, 16, 28, 4, 17, 28, 8, brickSlab);
        c.fillWithBlocks(level, box, 18, 27, 4, 19, 27, 8, brickSlab);
        c.fillWithBlocks(level, box, 20, 27, 4, 20, 27, 8, brickSlab);
        c.fillWithBlocks(level, box, 16, 28, 6, 17, 28, 6, brickSlab);
        c.fillWithBlocks(level, box, 18, 27, 6, 19, 27, 6, brickSlab);
        c.placeBlockAtCurrentPosition(level, brickSlab, 20, 27, 6, box);

        var supplies = new SiloComponent.SiloSupplies();
        c.fillWithRandomizedBlocks(level, box, 27, 26, 7, 29, 26, 9, rand, supplies);
        c.fillWithRandomizedBlocks(level, box, 17, 26, 4, 19, 26, 8, rand, supplies);

        // Wreckage
        c.placeBlockAtCurrentPosition(level, ModBlocks.BARREL_CORRODED.get().defaultBlockState(), 32, 26, 5, box);
        c.fillWithRandomizedBlocks(level, box, 32, 26, 7, 32, 26, 7, rand, destroyed);
        c.placeBlockAtCurrentPosition(level, ModBlocks.BARREL_CORRODED.get().defaultBlockState(), 31, 26, 9, box);
        c.fillWithRandomizedBlocks(level, box, 31, 26, 11, 32, 26, 11, rand, destroyed);
        c.fillWithRandomizedBlocks(level, box, 34, 26, 11, 34, 26, 11, rand, destroyed);
        c.fillWithRandomizedBlocks(level, box, 41, 26, 17, 41, 26, 17, rand, destroyed);
        c.placeBlockAtCurrentPosition(level, concreteSlab, 37, 26, 19, box);

        // Silo hatch
        c.placeCore(level, box, ModBlocks.SILO_HATCH_LARGE.get(), Direction.SOUTH, 19, 26, 14);
        c.fillSpace(level, box, 19, 26, 14, new int[] { 0, 0, 3, 3, 3, 3 },
                ModBlocks.SILO_HATCH_LARGE.get(), Direction.SOUTH);
        c.placeBlockAtCurrentPosition(level, ModBlocks.RADIO_TORCH_RECEIVER.get().defaultBlockState(), 16, 25, 17, box);
        c.setRTTYFreq(level, box, 16, 25, 17, c.freqHatch);

        c.generateInvContents(level, box, rand, Blocks.CHEST, Direction.SOUTH,
                36, 26, 17, ItemPoolsLegacy.POOL_VERTIBIRD, 5);
    }
}
