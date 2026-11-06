package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsComponent;
import com.hbm.itempool.ItemPoolsSingle;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SiloBlueControlRoom {

    public SiloBlueControlRoom() { }

    public static void build(SiloComponent c, WorldGenLevel level, RandomSource rand, BoundingBox box) {
        BlockState concreteSmooth = ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState();
        BlockState colored11 = ModBlocks.CONCRETE_COLORED_BLUE.get().defaultBlockState();
        BlockState decoSteel = ModBlocks.DECO_STEEL.get().defaultBlockState();

        var bricks = new NTMComponent.ConcreteBricks();
        var stairs = new SiloComponent.ConcreteStairs();

        // Air
        c.placeBlockAtCurrentPosition(level, Blocks.AIR.defaultBlockState(), 36, 23, 17, box);
        c.fillWithAir(level, box, 34, 21, 13, 35, 23, 19);
        c.fillWithAir(level, box, 33, 21, 13, 33, 23, 15);
        c.fillWithAir(level, box, 29, 21, 16, 31, 23, 19);
        c.fillWithAir(level, box, 29, 21, 12, 32, 23, 15);
        c.fillWithAir(level, box, 28, 21, 10, 32, 23, 11);
        c.fillWithAir(level, box, 27, 21, 7, 31, 23, 9);
        c.fillWithAir(level, box, 27, 21, 5, 30, 23, 6);
        c.fillWithAir(level, box, 27, 21, 4, 29, 23, 4);
        c.fillWithAir(level, box, 27, 21, 3, 28, 23, 3);
        c.fillWithAir(level, box, 26, 22, 7, 26, 23, 8);
        c.fillWithAir(level, box, 25, 22, 7, 25, 23, 7);
        c.fillWithAir(level, box, 24, 21, 2, 26, 23, 6);
        c.fillWithAir(level, box, 22, 21, 5, 23, 23, 5);
        c.fillWithAir(level, box, 16, 21, 1, 23, 23, 4);

        // Floor + Ceiling
        for (int i = 20; i <= 24; i += 4) {
            c.fillWithBlocks(level, box, 15, i, 0, 23, i, 5, concreteSmooth);
            c.fillWithBlocks(level, box, 24, i, 1, 26, i, 6, concreteSmooth);
            c.fillWithBlocks(level, box, 25, i, 7, 26, i, 7, concreteSmooth);
            c.placeBlockAtCurrentPosition(level, concreteSmooth, 26, i, 8, box);
            c.fillWithBlocks(level, box, 27, i, 2, 28, i, 6, concreteSmooth);
            c.placeBlockAtCurrentPosition(level, concreteSmooth, 29, i, 3, box);
            c.fillWithBlocks(level, box, 29, i, 4, 30, i, 4, concreteSmooth);
            c.fillWithBlocks(level, box, 29, i, 5, 31, i, 6, concreteSmooth);
            c.fillWithBlocks(level, box, 27, i, 7, 32, i, 9, concreteSmooth);
            c.fillWithBlocks(level, box, 28, i, 10, 33, i, 20, concreteSmooth);
            c.fillWithBlocks(level, box, 34, i, 12, 35, i, 15, concreteSmooth);
            c.fillWithBlocks(level, box, 34, i, 16, 37, i, 20, concreteSmooth);
        }

        // Walls (N curve)
        for (int i = 21; i <= 23; i += 2) {
            c.fillWithRandomizedBlocks(level, box, 15, i, 0, 23, i, 0, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 24, i, 1, 26, i, 1, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 27, i, 2, 28, i, 2, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 29, i, 3, 29, i, 3, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 30, i, 4, 30, i, 4, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 31, i, 5, 31, i, 6, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 32, i, 7, 32, i, 9, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 33, i, 10, 33, i, 12, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 34, i, 12, 35, i, 12, rand, bricks);
        }

        c.fillWithBlocks(level, box, 15, 22, 0, 23, 22, 0, colored11);
        c.fillWithBlocks(level, box, 24, 22, 1, 26, 22, 1, colored11);
        c.fillWithBlocks(level, box, 27, 22, 2, 28, 22, 2, colored11);
        c.placeBlockAtCurrentPosition(level, colored11, 29, 22, 3, box);
        c.placeBlockAtCurrentPosition(level, colored11, 30, 22, 4, box);
        c.fillWithBlocks(level, box, 31, 22, 5, 31, 22, 6, colored11);
        c.fillWithBlocks(level, box, 32, 22, 7, 32, 22, 9, colored11);
        c.fillWithBlocks(level, box, 33, 22, 10, 33, 22, 12, colored11);
        c.fillWithBlocks(level, box, 34, 22, 12, 35, 22, 12, colored11);

        c.fillWithRandomizedBlocks(level, box, 15, 21, 1, 15, 21, 4, rand, bricks);
        c.fillWithBlocks(level, box, 15, 22, 1, 15, 22, 4, colored11);
        c.fillWithRandomizedBlocks(level, box, 15, 23, 1, 15, 23, 4, rand, bricks);

        for (int i = 20; i <= 23; i += 3) {
            c.fillWithRandomizedBlocks(level, box, 15, i, 6, 16, i + 1, 6, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 22, i, 6, 23, i + 1, 6, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 24, i, 7, 24, i + 1, 7, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 25, i, 8, 25, i + 1, 8, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 26, i, 9, 26, i + 1, 9, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 27, i, 10, 27, i + 1, 11, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 27, i, 17, 27, i + 1, 18, rand, bricks);
        }
        c.fillWithRandomizedBlocks(level, box, 15, 21, 5, 18, 21, 5, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 20, 21, 5, 21, 21, 5, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 15, 23, 5, 21, 23, 5, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 28, 21, 12, 28, 21, 13, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 28, 21, 15, 28, 21, 20, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 28, 23, 12, 28, 23, 20, rand, bricks);

        c.fillWithBlocks(level, box, 15, 22, 6, 16, 22, 6, colored11);
        c.placeBlockAtCurrentPosition(level, colored11, 22, 22, 6, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_GLASS.get().defaultBlockState(), 23, 22, 6, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_GLASS.get().defaultBlockState(), 24, 22, 7, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_GLASS.get().defaultBlockState(), 25, 22, 8, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_GLASS.get().defaultBlockState(), 26, 22, 9, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_GLASS.get().defaultBlockState(), 27, 22, 10, box);
        c.placeBlockAtCurrentPosition(level, colored11, 27, 22, 11, box);
        c.fillWithBlocks(level, box, 27, 22, 17, 27, 22, 18, colored11);
        c.fillWithBlocks(level, box, 15, 22, 5, 18, 22, 5, colored11);
        c.fillWithBlocks(level, box, 20, 22, 5, 21, 22, 5, colored11);
        c.fillWithBlocks(level, box, 28, 22, 12, 28, 22, 13, colored11);
        c.fillWithBlocks(level, box, 28, 22, 15, 28, 22, 20, colored11);

        c.fillWithRandomizedBlocks(level, box, 29, 21, 20, 36, 21, 20, rand, bricks);
        c.fillWithBlocks(level, box, 29, 22, 20, 36, 22, 20, colored11);
        c.fillWithRandomizedBlocks(level, box, 29, 23, 20, 36, 23, 20, rand, bricks);

        c.fillWithRandomizedBlocks(level, box, 37, 21, 15, 37, 21, 20, rand, bricks);
        c.fillWithBlocks(level, box, 37, 22, 15, 37, 22, 20, colored11);
        c.fillWithRandomizedBlocks(level, box, 37, 23, 15, 37, 23, 20, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 37, 24, 15, 37, 24, 15, rand, bricks);

        c.fillWithRandomizedBlocks(level, box, 32, 21, 16, 32, 21, 19, rand, bricks);
        c.fillWithBlocks(level, box, 32, 22, 16, 32, 22, 19, colored11);
        c.fillWithRandomizedBlocks(level, box, 32, 23, 16, 32, 23, 19, rand, bricks);

        // Arches
        stairs.setFacing(c.rotate(Direction.SOUTH));
        stairs.setTop(true);
        c.fillWithRandomizedBlocks(level, box, 24, 23, 2, 26, 23, 2, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 27, 23, 3, 28, 23, 3, rand, stairs);
        stairs.setFacing(c.rotate(Direction.WEST));
        c.fillWithRandomizedBlocks(level, box, 30, 23, 5, 30, 23, 6, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 31, 23, 7, 31, 23, 9, rand, stairs);

        // Doors
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, true, rand.nextBoolean(), 19, 21, 5);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.NORTH, false, rand.nextBoolean(), 28, 21, 14);

        // Computer area
        c.fillWithBlocks(level, box, 33, 21, 19, 33, 23, 19, decoSteel);
        c.fillWithBlocks(level, box, 33, 21, 17, 33, 23, 17, decoSteel);
        c.placeDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.WEST, 33, 21, 18);
        c.placeCRT(level, box, ModBlocks.DECO_CRT.get(), 1, Direction.WEST, 33, 22, 18);
        c.placeDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.WEST, 33, 23, 18);
        c.fillWithDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.WEST, 33, 21, 16, 33, 23, 16);
        c.placeStairs(level, box, ModBlocks.REINFORCED_STONE_STAIRS.get(), Direction.EAST, true, 34, 21, 19);
        c.placeBlockAtCurrentPosition(level, ModBlocks.BRICK_SLAB_REINFORCED_STONE.get().defaultBlockState(), 34, 21, 18, box);
        c.placeBlockAtCurrentPosition(level, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 34, 22, 18, box);
        c.placeDeco(level, box, ModBlocks.CAPACITOR_COPPER.get(), Direction.EAST, 36, 21, 16);
        c.placeBlockAtCurrentPosition(level, decoSteel, 36, 21, 17, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.HADRON_COIL_ALLOY.get().defaultBlockState(), 36, 21, 19, box);
        c.fillWithDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.EAST, 36, 22, 16, 36, 23, 16);
        c.placeDeco(level, box, ModBlocks.DECO_COMPUTER.get(), Direction.EAST, 36, 22, 17);
        c.fillWithDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.EAST, 36, 21, 18, 36, 23, 18);
        c.placeCRT(level, box, ModBlocks.DECO_CRT.get(), 1, Direction.EAST, 36, 22, 19);
        c.placeCRT(level, box, ModBlocks.DECO_CRT.get(), 0, Direction.EAST, 36, 23, 19);

        // Cabinets + pipe
        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_FRAMED_GREEN_RUSTED.get(), Direction.Axis.Y, 32, 21, 11, 32, 22, 11);
        c.placePillar(level, box, ModBlocks.DECO_PIPE_FRAMED_GREEN_RUSTED.get(), Direction.Axis.Z, 32, 23, 10);
        c.placeBlockAtCurrentPosition(level, decoSteel, 32, 23, 11, box);
        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_FRAMED_GREEN_RUSTED.get(), Direction.Axis.Z, 32, 23, 12, 32, 23, 15);

        c.placeBlockAtCurrentPosition(level, ModBlocks.TURRET_SENTRY_DAMAGED.get().defaultBlockState(), 30, 21, 16, box);

        // Desk area
        c.fillWithBlocks(level, box, 27, 21, 9, 28, 21, 9, decoSteel);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_BERYLLIUM.get().defaultBlockState(), 26, 21, 8, box);
        c.fillWithBlocks(level, box, 25, 21, 7, 26, 21, 7, decoSteel);
        c.fillWithBlocks(level, box, 24, 21, 5, 24, 21, 6, decoSteel);
        c.placeDeco(level, box, ModBlocks.DECO_COMPUTER.get(), Direction.SOUTH, 28, 22, 9);
        c.placeBlockAtCurrentPosition(level, Blocks.LEVER.defaultBlockState(), 26, 22, 8, box);
        c.placeBlockAtCurrentPosition(level, Blocks.LEVER.defaultBlockState(), 25, 22, 7, box);
        c.placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, 28, 21, 7);
        c.placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 27, 21, 5);
        c.placeDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.EAST, 30, 21, 5);
        c.placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 27, 21, 3, box);
        c.placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 25, 22, 2, box);

        c.placeCore(level, box, ModBlocks.RADIO_TELEX.get(), Direction.WEST, 25, 21, 5);
        c.fillSpace(level, box, 25, 21, 5, new int[] { 0, 0, 0, 0, 1, 0 },
                ModBlocks.RADIO_TELEX.get(), Direction.WEST);

        c.placeBlockAtCurrentPosition(level, ModBlocks.RADIO_TORCH_SENDER.get().defaultBlockState(), 26, 20, 8, box);
        c.setRTTYFreq(level, box, 26, 20, 8, c.freq);
        c.placeBlockAtCurrentPosition(level, ModBlocks.RADIO_TORCH_SENDER.get().defaultBlockState(), 25, 20, 7, box);
        c.setRTTYFreq(level, box, 25, 20, 7, c.freqHatch);

        // Machine / small desk
        c.placePillar(level, box, ModBlocks.DECO_PIPE_FRAMED_GREEN_RUSTED.get(), Direction.Axis.X, 23, 23, 1);
        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_FRAMED_GREEN_RUSTED.get(), Direction.Axis.X, 16, 23, 1, 19, 23, 1);
        c.placeBlockAtCurrentPosition(level, decoSteel, 20, 21, 1, box);
        c.placeDeco(level, box, ModBlocks.CAPACITOR_COPPER.get(), Direction.NORTH, 20, 22, 1);
        c.placeStairs(level, box, ModBlocks.REINFORCED_STONE_STAIRS.get(), Direction.SOUTH, true, 21, 21, 1);
        c.placeCRT(level, box, ModBlocks.DECO_CRT.get(), 1, Direction.NORTH, 21, 22, 1);
        c.placeBlockAtCurrentPosition(level, decoSteel, 22, 21, 1, box);
        c.placeDeco(level, box, ModBlocks.CAPACITOR_COPPER.get(), Direction.NORTH, 22, 22, 1);
        c.fillWithBlocks(level, box, 20, 23, 1, 22, 23, 1, decoSteel);
        c.placeBlockAtCurrentPosition(level, ModBlocks.HEV_BATTERY.get().defaultBlockState(), 23, 21, 1, box);
        c.placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 18, 21, 2);
        c.fillWithBlocks(level, box, 16, 21, 1, 16, 21, 3, decoSteel);
        c.placeDeco(level, box, ModBlocks.DECO_COMPUTER.get(), Direction.WEST, 16, 22, 2);
        c.placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 16, 22, 3, box);
        c.placeRandomBobble(level, box, rand, 16, 22, 4);

        // Containers
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.EAST, 31, 21, 17, ItemPoolsComponent.POOL_FILING_CABINET, 4);
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.EAST, 31, 21, 18, ItemPoolsComponent.POOL_VAULT_LAB, 6);
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.EAST, 31, 21, 19, ItemPoolsComponent.POOL_FILING_CABINET, 4);
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.EAST, 31, 22, 17, ItemPoolsComponent.POOL_FILING_CABINET, 4);
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.EAST, 31, 22, 19, ItemPoolsComponent.POOL_FILING_CABINET, 4);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 29, 21, 19, ItemPoolsComponent.POOL_OFFICE_TRASH, 8);
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.WEST, 29, 21, 18, ItemPoolsComponent.POOL_FILING_CABINET, 4);
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.WEST, 29, 21, 17, ItemPoolsComponent.POOL_FILING_CABINET, 4);
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.EAST, 31, 21, 8, ItemPoolsComponent.POOL_FILING_CABINET, 5);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 25, 21, 2, ItemPoolsComponent.POOL_MACHINE_PARTS, 4);
        c.generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.SOUTH, 23, 21, 5, ItemPoolsComponent.POOL_FILING_CABINET, 5);
        c.generateLockableContents(level, box, rand, ModBlocks.SAFE.get(), Direction.WEST, 16, 21, 4, ItemPoolsSingle.POOL_VAULT_RUSTY, 3, 1.0D);
    }
        }
