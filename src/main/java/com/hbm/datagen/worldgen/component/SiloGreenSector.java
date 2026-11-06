package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsComponent;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SiloGreenSector {

    public SiloGreenSector() { }

    public static void build(SiloComponent c, WorldGenLevel level, RandomSource rand, BoundingBox box) {
        BlockState concreteSmooth = ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState();
        BlockState colored13 = ModBlocks.CONCRETE_COLORED_GREEN.get().defaultBlockState();
        var bricks = new NTMComponent.ConcreteBricks();
        var stairs = new SiloComponent.ConcreteStairs();

        // Air
        c.fillWithAir(level, box, 1, 9, 13, 11, 10, 15);
        c.fillWithAir(level, box, 1, 11, 14, 8, 11, 14);
        c.fillWithAir(level, box, 1, 9, 7, 6, 10, 11);
        c.fillWithAir(level, box, 1, 11, 8, 6, 11, 10);
        c.fillWithAir(level, box, 7, 9, 7, 11, 10, 7);
        c.fillWithAir(level, box, 7, 9, 11, 11, 10, 11);
        c.fillWithAir(level, box, 2, 9, 17, 4, 11, 23);
        c.fillWithAir(level, box, 5, 9, 17, 5, 9, 18);
        c.fillWithAir(level, box, 5, 9, 22, 5, 9, 23);
        c.fillWithAir(level, box, 1, 9, 17, 1, 9, 18);
        c.fillWithAir(level, box, 1, 9, 22, 1, 9, 23);
        c.fillWithAir(level, box, 7, 9, 17, 11, 10, 23);
        c.fillWithAir(level, box, 8, 11, 17, 10, 11, 23);

        // Floor + ceiling
        c.placeBlockAtCurrentPosition(level, concreteSmooth, 12, 8, 14, box);
        for (int i = 8; i <= 12; i += 4) {
            c.fillWithBlocks(level, box, 0, i, 6, 12, i, 11, concreteSmooth);
            c.fillWithBlocks(level, box, 0, i, 12, 11, i, 16, concreteSmooth);
            c.fillWithBlocks(level, box, 0, i, 17, 12, i, 24, concreteSmooth);
        }

        // Walls
        c.fillWithBlocks(level, box, 0, 10, 6, 12, 10, 6, colored13);
        c.fillWithBlocks(level, box, 0, 10, 7, 0, 10, 23, colored13);
        c.fillWithBlocks(level, box, 0, 10, 24, 12, 10, 24, colored13);
        c.fillWithBlocks(level, box, 12, 10, 7, 12, 10, 23, colored13);
        c.fillWithBlocks(level, box, 13, 10, 17, 13, 10, 18, colored13);
        c.fillWithBlocks(level, box, 13, 10, 10, 13, 10, 11, colored13);
        c.fillWithBlocks(level, box, 1, 10, 12, 11, 10, 12, colored13);
        c.fillWithBlocks(level, box, 1, 10, 16, 11, 10, 16, colored13);
        c.fillWithBlocks(level, box, 6, 10, 17, 6, 10, 23, colored13);

        for (int i = 9; i <= 11; i += 2) {
            c.fillWithRandomizedBlocks(level, box, 0, i, 6, 12, i, 6, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 0, i, 7, 0, i, 23, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 0, i, 24, 12, i, 24, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 12, i, 17, 12, i, 23, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 12, i, 7, 12, i, 11, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 1, i, 12, 11, i, 12, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 1, i, 16, 11, i, 16, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, 6, i, 17, 6, i, 23, rand, bricks);
        }

        // Arches
        stairs.setFacing(c.rotate(Direction.SOUTH));
        stairs.setTop(true);
        c.fillWithRandomizedBlocks(level, box, 1, 11, 7, 11, 11, 7, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 1, 11, 13, 11, 11, 13, rand, stairs);
        stairs.setFacing(c.rotate(Direction.NORTH));
        c.fillWithRandomizedBlocks(level, box, 1, 11, 11, 11, 11, 11, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 1, 11, 15, 11, 11, 15, rand, stairs);
        stairs.setFacing(c.rotate(Direction.WEST));
        c.fillWithRandomizedBlocks(level, box, 11, 11, 17, 11, 11, 23, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 5, 11, 17, 5, 11, 18, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 5, 11, 22, 5, 11, 23, rand, stairs);
        stairs.setFacing(c.rotate(Direction.EAST));
        c.fillWithRandomizedBlocks(level, box, 7, 11, 17, 7, 11, 23, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 1, 11, 17, 1, 11, 18, rand, stairs);
        c.fillWithRandomizedBlocks(level, box, 1, 11, 22, 1, 11, 23, rand, stairs);

        // Doors
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.NORTH, false, rand.nextBoolean(), 12, 9, 14);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 9, 9, 16);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 3, 9, 16);
        c.placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.EAST, false, rand.nextBoolean(), 3, 9, 12);

        // Fuel pipes (все пиллар-ы оси X)
        Direction.Axis axisX = Direction.Axis.X;
        Direction.Axis axisZ = Direction.Axis.Z;

        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), axisX, 17, 11, 14, 18, 11, 14);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 16, 11, 14, box);
        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), axisX, 13, 11, 14, 15, 11, 14);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 12, 11, 14, box);
        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), axisX, 10, 11, 14, 11, 11, 14);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 9, 11, 14, box);
        c.placePillar(level, box, ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), axisZ, 9, 11, 15);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 9, 11, 16, box);
        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), axisZ, 9, 11, 17, 9, 11, 19);
        c.placeDeco(level, box, ModBlocks.FLUID_DUCT_GAUGE.get(), Direction.DOWN, 9, 11, 20);
        c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), axisZ, 9, 11, 21, 9, 11, 22);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 9, 11, 23, box);
        c.fillWithBlocks(level, box, 9, 9, 23, 9, 10, 23, ModBlocks.DECO_PIPE_FRAMED_RUSTED.get().defaultBlockState());
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 9, 8, 23, box);
        c.placePillar(level, box, ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), axisX, 10, 11, 20);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 11, 11, 20, box);
        c.fillWithBlocks(level, box, 11, 9, 20, 11, 10, 20, ModBlocks.DECO_PIPE_FRAMED_RUSTED.get().defaultBlockState());
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 11, 8, 20, box);
        c.placePillar(level, box, ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), axisX, 8, 11, 20);
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 7, 11, 20, box);
        c.fillWithBlocks(level, box, 7, 9, 20, 7, 10, 20, ModBlocks.DECO_PIPE_FRAMED_RUSTED.get().defaultBlockState());
        c.placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 7, 8, 20, box);
        c.fillWithBlocks(level, box, 8, 8, 18, 10, 8, 22, ModBlocks.DECO_LEAD.get().defaultBlockState());

        // Barrels
        c.placeBlockAtCurrentPosition(level, ModBlocks.LOX_BARREL.get().defaultBlockState(), 7, 9, 17, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.PINK_BARREL.get().defaultBlockState(), 11, 9, 19, box);
        c.placeBlockAtCurrentPosition(level, ModBlocks.PINK_BARREL.get().defaultBlockState(), 11, 9, 22, box);
        c.fillWithBlocks(level, box, 11, 9, 23, 11, 10, 23, ModBlocks.PINK_BARREL.get().defaultBlockState());
        c.placeBlockAtCurrentPosition(level, ModBlocks.PINK_BARREL.get().defaultBlockState(), 10, 9, 23, box);
        c.fillWithBlocks(level, box, 7, 9, 23, 8, 9, 23, ModBlocks.LOX_BARREL.get().defaultBlockState());
        c.fillWithBlocks(level, box, 7, 9, 21, 7, 9, 22, ModBlocks.LOX_BARREL.get().defaultBlockState());

        // Capacitors
        for (int i = 1; i <= 5; i += 4) {
            c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_QUAD_RED.get(), axisZ, i, 10, 17, i, 10, 18);
            c.fillWithPillars(level, box, ModBlocks.DECO_PIPE_QUAD_RED.get(), axisZ, i, 10, 22, i, 10, 23);
            c.fillWithBlocks(level, box, i, 9, 19, i, 9, 21, ModBlocks.DECO_LEAD.get().defaultBlockState());
            c.placeDeco(level, box, ModBlocks.CAPACITOR_COPPER.get(), i == 1 ? Direction.WEST : Direction.EAST,
                    i, 10, 19);
            c.fillWithBlocks(level, box, i, 10, 20, i, 10, 21, ModBlocks.CAPACITOR_COPPER.get().defaultBlockState());
            c.fillWithBlocks(level, box, i, 11, 19, i, 11, 21, ModBlocks.DECO_LEAD.get().defaultBlockState());
        }

        // Generator room
        c.placeBlockAtCurrentPosition(level, ModBlocks.BARREL_CORRODED.get().defaultBlockState(), 1, 9, 11, box);
        c.fillWithBlocks(level, box, 1, 9, 8, 1, 9, 9, ModBlocks.BARREL_CORRODED.get().defaultBlockState());
        c.fillWithBlocks(level, box, 1, 9, 7, 1, 10, 7, ModBlocks.BARREL_CORRODED.get().defaultBlockState());
        c.placeBlockAtCurrentPosition(level, ModBlocks.BARREL_CORRODED.get().defaultBlockState(), 2, 9, 7, box);

        c.fillWithBlocks(level, box, 7, 9, 10, 11, 9, 10, ModBlocks.DECO_LEAD.get().defaultBlockState());
        c.fillWithBlocks(level, box, 7, 10, 10, 11, 10, 10, ModBlocks.HADRON_COIL_ALLOY.get().defaultBlockState());
        c.fillWithBlocks(level, box, 7, 11, 10, 11, 11, 10, ModBlocks.DECO_LEAD.get().defaultBlockState());
        c.fillWithBlocks(level, box, 7, 9, 9, 11, 9, 9, ModBlocks.HADRON_COIL_ALLOY.get().defaultBlockState());
        c.fillWithBlocks(level, box, 8, 10, 9, 11, 10, 9, ModBlocks.DECO_RED_COPPER.get().defaultBlockState());
        c.placeDeco(level, box, ModBlocks.RED_CABLE_GAUGE.get(), Direction.EAST, 7, 10, 9);
        c.fillWithBlocks(level, box, 7, 11, 9, 11, 11, 9, ModBlocks.HADRON_COIL_ALLOY.get().defaultBlockState());
        c.fillWithBlocks(level, box, 7, 9, 8, 11, 9, 8, ModBlocks.DECO_LEAD.get().defaultBlockState());
        c.fillWithBlocks(level, box, 7, 10, 8, 11, 10, 8, ModBlocks.HADRON_COIL_ALLOY.get().defaultBlockState());
        c.fillWithBlocks(level, box, 7, 11, 8, 11, 11, 8, ModBlocks.DECO_LEAD.get().defaultBlockState());

        // Containers
        c.generateInvContents(level, box, rand, ModBlocks.CRATE_STEEL.get(), Direction.NORTH, 4, 9, 7, ItemPoolsComponent.POOL_NUKE_FUEL, 5);

        // Mines
        c.fillWithMines(level, box, rand, 1, 9, 7, 6, 9, 11);
        c.fillWithMines(level, box, rand, 8, 9, 17, 10, 9, 22);
    }
                                       }
