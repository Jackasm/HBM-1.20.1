package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsComponent;
import com.hbm.itempool.ItemPoolsLegacy;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SiloRedSector {

    public SiloRedSector() { }

    public static void build(SiloComponent c, WorldGenLevel level, RandomSource rand, BoundingBox box) {
        BlockState concreteSmooth = ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState();
        BlockState colored14 = ModBlocks.CONCRETE_COLORED_RED.get().defaultBlockState();
        var bricks = new NTMComponent.ConcreteBricks();
        var stairs = new SiloComponent.ConcreteStairs();

        // Air
        c.fillWithAir(level, box, 2, 17, 9, 11, 18, 11);
        c.fillWithAir(level, box, 2, 19, 10, 11, 19, 10);
        c.fillWithAir(level, box, 2, 17, 13, 11, 18, 15);
        c.fillWithAir(level, box, 2, 19, 14, 11, 19, 14);
        c.fillWithAir(level, box, 8, 17, 17, 12, 18, 25);
        c.fillWithAir(level, box, 9, 19, 17, 11, 19, 25);
        c.fillWithAir(level, box, 2, 17, 17, 6, 18, 21);
        c.fillWithAir(level, box, 3, 19, 17, 5, 19, 21);
        c.fillWithAir(level, box, 2, 17, 22, 3, 18, 25);
        c.fillWithAir(level, box, 3, 19, 22, 3, 19, 25);
        c.fillWithAir(level, box, 5, 17, 23, 6, 19, 25);

        // Ceiling
        c.fillWithBlocks(level, box, 1, 20, 8, 12, 20, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 1, 20, 17, 13, 20, 26, concreteSmooth);

        // Floor
        c.fillWithBlocks(level, box, 2, 16, 7, 11, 16, 7, concreteSmooth);
        c.fillWithBlocks(level, box, 1, 16, 8, 12, 16, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 1, 16, 12, 11, 16, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 1, 16, 17, 12, 16, 18, concreteSmooth);
        c.fillWithBlocks(level, box, 1, 16, 19, 13, 16, 26, concreteSmooth);
        c.placeBlockAtCurrentPosition(level, concreteSmooth, 12, 16, 14, box);

        // Walls N
        c.fillWithRandomizedBlocks(level, box, 2, 17, 7, 11, 19, 7, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 11, 17, 8, 12, 17, 8, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 8, 17, 8, 8, 17, 8, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 5, 17, 8, 5, 17, 8, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 1, 17, 8, 2, 17, 8, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 1, 19, 8, 12, 19, 8, rand, bricks);

        c.fillWithBlocks(level, box, 1, 18, 8, 2, 18, 8, colored14);
        c.placeBlockAtCurrentPosition(level, colored14, 5, 18, 8, box);
        c.placeBlockAtCurrentPosition(level, colored14, 8, 18, 8, box);
        c.fillWithBlocks(level, box, 11, 18, 8, 12, 18, 8, colored14);

        c.fillWithBlocks(level, box, 1, 18, 9, 1, 18, 25, colored14);
        c.fillWithBlocks(level, box, 1, 18, 26, 13, 18, 26, colored14);
        c.fillWithBlocks(level, box, 13, 18, 17, 13, 18, 25, colored14);
        c.fillWithBlocks(level, box, 12, 18, 9, 12, 18, 16, colored14);
        c.fillWithBlocks(level, box, 13, 18, 10, 13, 18, 11, colored14);
        c.fillWithBlocks(level, box, 2, 18, 12, 11, 18, 12, colored14);
        c.fillWithBlocks(level, box, 2, 18, 16, 11, 18, 16, colored14);
        c.fillWithBlocks(level, box, 7, 18, 17, 7, 18, 25, colored14);
        c.fillWithBlocks(level, box, 4, 18, 22, 6, 18, 22, colored14);
        c.fillWithBlocks(level, box, 4, 18, 23, 4, 18, 25, colored14);

        for (int i = 17; i <= 19; i += 2) {
            c.fillWithRandomizedBlocks(level, box, 1, i, 9, 1, i, 25, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 1, i, 26, 13, i, 26, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 13, i, 19, 13, i, 25, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 12, i, 9, 12, i, 11, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 2, i, 12, 11, i, 12, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 2, i, 16, 11, i, 16, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 7, i, 17, 7, i, 25, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 4, i, 22, 6, i, 22, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 4, i, 23, 4, i, 25, rand, bricks);
        }

        // Arches
        stairs.setFacing(c.rotate(Direction.SOUTH));
        stairs.setTop(true);
        c.fillWithRandomizedBlocks(level, box, 2, 19, 9, 11, 19, 9, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 2, 19, 13, 11, 19, 13, rand, stairs);
        stairs.setFacing(c.rotate(Direction.NORTH));
        c.fillWithRandomizedBlocks(level, box, 2, 19, 11, 11, 19, 11, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 2, 19, 15, 11, 19, 15, rand, stairs);
        stairs.setFacing(c.rotate(Direction.WEST));
        c.fillWithRandomizedBlocks(level, box, 12, 19, 17, 12, 19, 25, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 6, 19, 17, 6, 19, 21, rand, stairs);
        stairs.setFacing(c.rotate(Direction.EAST));
        c.fillWithRandomizedBlocks(level, box, 8, 19, 17, 8, 19, 25, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 2, 19, 17, 2, 19, 25, rand, stairs);

        // Doors
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.NORTH, false, rand.nextBoolean(), 12, 17, 14);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.EAST, false, rand.nextBoolean(), 10, 17, 12);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 10, 17, 16);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 4, 17, 16);
        c.placeDoor(level, box, ModBlocks.DOOR_METAL.get(), Direction.SOUTH, false, rand.nextBoolean(), 4, 17, 24);

        // Living room
        c.placeStairs(level, box, ModBlocks.REINFORCED_STONE_STAIRS.get(), Direction.WEST, true, 12, 17, 17);
        c.placeBlockAtCurrentPosition(level, Blocks.CAULDRON.defaultBlockState(), 12, 17, 18, box);
        c.fillWithStairs(level, box, ModBlocks.REINFORCED_STONE_STAIRS.get(), Direction.WEST, true, 12, 17, 19, 12, 17, 20);
        c.placeDeco(level, box, ModBlocks.MACHINE_ELECTRIC_FURNACE.get(), Direction.EAST, 12, 17, 21);
        c.placeBlockAtCurrentPosition(level,
                ModBlocks.DECO_TOASTER_IRON.get().defaultBlockState()
                        .setValue(com.hbm.blocks.generic.BlockDecoToaster.FACING, c.rotate(Direction.NORTH)),
                12, 18, 17, box);
        c.placeLever(level, box, Direction.EAST, AttachFace.WALL, true, 12, 18, 18);
        c.placeDeco(level, box, ModBlocks.MACHINE_MICROWAVE.get(), Direction.EAST, 12, 18, 19);
        c.placeBlockAtCurrentPosition(level, ModBlocks.HEV_BATTERY.get().defaultBlockState(), 12, 18, 20, box);
        c.placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, 8, 17, 17);
        c.fillWithStairs(level, box, ModBlocks.REINFORCED_STONE_STAIRS.get(), Direction.SOUTH, true, 8, 17, 19, 9, 17, 19);
        c.fillWithStairs(level, box, ModBlocks.REINFORCED_STONE_STAIRS.get(), Direction.NORTH, true, 8, 17, 20, 9, 17, 20);
        c.placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, false, 8, 17, 22);
        c.placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, false, 10, 17, 23);
        c.placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, 11, 17, 23);
        c.placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 12, 17, 23);
        c.fillWithStairs(level, box, ModBlocks.REINFORCED_STONE_STAIRS.get(), Direction.NORTH, true, 10, 17, 25, 12, 17, 25);
        c.placeDeco(level, box, ModBlocks.DECO_CRT.get(), Direction.SOUTH, 11, 18, 25);

        // Bathroom
        c.placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_STONE.get().defaultBlockState(), 6, 17, 17, box);
        c.fillWithBlocks(level, box, 6, 17, 18, 6, 17, 20, Blocks.CAULDRON.defaultBlockState());
        c.placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_STONE.get().defaultBlockState(), 6, 17, 21, box);
        for (int i = 0; i < 3; i++) {
            c.placeLever(level, box, Direction.EAST, AttachFace.WALL, true, 6, 18, 18 + i);
        }
        c.placeBlockAtCurrentPosition(level, Blocks.HOPPER.defaultBlockState(), 6, 17, 24, box);
        c.placeTrapdoor(level, box, Blocks.OAK_TRAPDOOR, Direction.EAST, false, true, 6, 18, 24);

        // Bedroom
        for (int i = 3; i <= 7; i += 2) {
            c.placeStairs(level, box, ModBlocks.REINFORCED_STONE_STAIRS.get(), Direction.NORTH, true, i, 17, 11);
        }
        for (int i = 4; i <= 10; i += 3) {
            for (int j = 17; j <= 18; j++) {
                c.placeBed(level, box, Direction.WEST, i, j, 8);
            }
        }

        // Containers
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 8, 17, 25, ItemPoolsComponent.POOL_VAULT_LOCKERS, 6);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 2, 17, 11, ItemPoolsComponent.POOL_VAULT_LOCKERS, 6);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 4, 17, 11, ItemPoolsLegacy.POOL_EXPENSIVE, 2);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 6, 17, 11, ItemPoolsComponent.POOL_VAULT_LOCKERS, 6);
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 8, 17, 11, ItemPoolsComponent.POOL_VAULT_LOCKERS, 6);

        // Mines
        c.fillWithMines(level, box, rand, 2, 17, 9, 11, 17, 11);
        c.fillWithMines(level, box, rand, 9, 17, 17, 11, 17, 24);
        c.fillWithMines(level, box, rand, 5, 17, 23, 6, 17, 25);
    }
}
