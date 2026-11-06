package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsComponent;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SiloYellowSector {

    public SiloYellowSector() { }

    public static void build(SiloComponent c, WorldGenLevel level, RandomSource rand, BoundingBox box) {
        BlockState concreteSmooth = ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState();
        BlockState colored4 = ModBlocks.CONCRETE_COLORED_YELLOW.get().defaultBlockState();
        var bricks = new NTMComponent.ConcreteBricks();
        var stairs = new SiloComponent.ConcreteStairs();
        var supplies = new SiloComponent.SiloSupplies();

        // Air
        c.fillWithAir(level, box, 27, 13, 13, 33, 14, 15);
        c.fillWithAir(level, box, 27, 15, 14, 33, 15, 14);
        c.fillWithAir(level, box, 27, 13, 17, 33, 14, 21);
        c.fillWithAir(level, box, 27, 15, 18, 33, 15, 20);
        c.fillWithAir(level, box, 27, 13, 9, 29, 14, 11);
        c.fillWithAir(level, box, 28, 15, 9, 28, 15, 11);
        c.fillWithAir(level, box, 31, 13, 9, 33, 14, 11);
        c.fillWithAir(level, box, 32, 15, 9, 32, 15, 11);

        // Ceiling + Floor
        for (int i = 12; i <= 16; i += 4) {
            c.fillWithBlocks(level, box, 26, i, 17, 26, i, 22, concreteSmooth);
            c.fillWithBlocks(level, box, 27, i, 8, 34, i, 22, concreteSmooth);
            c.fillWithBlocks(level, box, 26, i, 8, 26, i, 11, concreteSmooth);
        }
        c.placeBlockAtCurrentPosition(level, concreteSmooth, 26, 12, 14, box);

        // Walls
        c.fillWithBlocks(level, box, 26, 14, 8, 34, 14, 8, colored4);
        c.fillWithBlocks(level, box, 34, 14, 9, 34, 14, 21, colored4);
        c.fillWithBlocks(level, box, 26, 14, 22, 34, 14, 22, colored4);
        c.fillWithBlocks(level, box, 26, 14, 9, 26, 14, 21, colored4);
        c.fillWithBlocks(level, box, 25, 14, 17, 25, 14, 18, colored4);
        c.fillWithBlocks(level, box, 25, 14, 10, 25, 14, 11, colored4);
        c.fillWithBlocks(level, box, 27, 14, 16, 33, 14, 16, colored4);
        c.fillWithBlocks(level, box, 27, 14, 12, 33, 14, 12, colored4);
        c.fillWithBlocks(level, box, 30, 14, 9, 30, 14, 11, colored4);

        for (int i = 13; i <= 15; i += 2) {
            c.fillWithRandomizedBlocks(level, box, 26, i, 8, 34, i, 8, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 34, i, 9, 34, i, 21, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 26, i, 22, 34, i, 22, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 26, i, 15, 26, i, 21, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 26, i, 9, 26, i, 13, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 27, i, 16, 33, i, 16, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 27, i, 12, 33, i, 12, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 30, i, 9, 30, i, 11, rand, bricks);
        }

        // Arches
        stairs.setFacing(c.rotate(Direction.NORTH));
        stairs.setTop(true);
        c.fillWithRandomizedBlocks(level, box, 27, 15, 21, 33, 15, 21, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 27, 15, 15, 33, 15, 15, rand, stairs);
        stairs.setFacing(c.rotate(Direction.SOUTH));
        c.fillWithRandomizedBlocks(level, box, 27, 15, 17, 33, 15, 17, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 27, 15, 13, 33, 15, 13, rand, stairs);
        stairs.setFacing(c.rotate(Direction.WEST));
        c.fillWithRandomizedBlocks(level, box, 33, 15, 9, 33, 15, 11, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 29, 15, 9, 29, 15, 11, rand, stairs);
        stairs.setFacing(c.rotate(Direction.EAST));
        c.fillWithRandomizedBlocks(level, box, 31, 15, 9, 31, 15, 11, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 27, 15, 9, 27, 15, 11, rand, stairs);

        // Doors
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 26, 13, 14);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.EAST, false, rand.nextBoolean(), 28, 13, 12);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.EAST, false, rand.nextBoolean(), 32, 13, 12);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 32, 13, 16);

        // Room 1
        c.placeBlockAtCurrentPosition(level, ModBlocks.CRATE_AMMO.get().defaultBlockState(), 27, 13, 9, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 27, 13, 10, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 27, 14, 9, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 28, 13, 9, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.BARREL_CORRODED.get().defaultBlockState(), 29, 13, 9, box);

        // Room 2
        c.placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 31, 13, 9, box);
        c.placeDeco(level, box, ModBlocks.DECO_COMPUTER.get(), Direction.EAST, 31, 13, 11);
        c.placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 33, 13, 11, box);

        // Workshop
        c.placeBlockAtCurrentPosition(level, ModBlocks.MACHINE_TRANSFORMER.get().defaultBlockState(), 33, 13, 17, box);
        c.fillWithRandomizedBlocks(level, box, 33, 13, 18, 33, 13, 20, rand, supplies);
        c.placeDeco(level, box, ModBlocks.ANVIL_IRON.get(), Direction.NORTH, 31, 13, 21);
        c.fillWithBlocks(level, box, 28, 13, 18, 29, 13, 20, Blocks.OAK_PLANKS.defaultBlockState());
        c.placeBlockAtCurrentPosition(level, Blocks.CRAFTING_TABLE.defaultBlockState(), 29, 13, 19, box);
        c.placeDeco(level, box, ModBlocks.RADIOREC.get(), Direction.WEST, 28, 14, 19);
        c.placeBlockAtCurrentPosition(level,
                ModBlocks.DECO_TOASTER_IRON.get().defaultBlockState()
                        .setValue(com.hbm.blocks.generic.BlockDecoToaster.FACING, c.rotate(Direction.EAST)),
                28, 13, 17, box);

        // Containers
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 32, 13, 9, ItemPoolsComponent.POOL_SILO, 6);
        c.generateInvContents(level, box, rand, ModBlocks.SAFE.get(), Direction.NORTH, 33, 13, 9, ItemPoolsComponent.POOL_MACHINE_PARTS, 6);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 33, 13, 21, ItemPoolsComponent.POOL_VAULT_LAB, 8);

        // Mines
        c.fillWithMines(level, box, rand, 27, 13, 13, 33, 13, 15);
    }
                    }
