package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.deco.BlockSteelGrate;
import com.hbm.blocks.deco.BlockSteelScaffold;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class PowerRoom extends NTMComponent implements ProceduralStructure.ProceduralComponent {

    private boolean path;
    private int powerType;

    public PowerRoom() {
        this.powerType = 0;
    }

    public PowerRoom(int componentType, BoundingBox box, int coordMode, RandomSource rand) {
        this.componentType = componentType;
        this.boundingBox = box;
        this.coordBaseMode = coordMode;

        float chance = rand.nextFloat();
        this.powerType = chance < 0.2F ? 2 : chance < 0.6F ? 1 : 0;
    }

    public PowerRoom(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = ProceduralStructure.getComponentToAddBoundingBox(
                minX, 64, minZ, -4, -1, 0, 12, 6, 12, coordBaseMode);

        float chance = rand.nextFloat();
        this.powerType = chance < 0.2F ? 2 : chance < 0.6F ? 1 : 0;
    }

    @Override
    public String getTypeName() { return "PowerRoom"; }

    public static NTMComponent findValidPlacement(List<NTMComponent> components, RandomSource rand,
                                                  int x, int y, int z, int coordMode, int type) {
        BoundingBox box = ProceduralStructure.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 12, 6, 12, coordMode);

        return box.minY() > 10 && !ProceduralStructure.findIntersecting(components, box)
                ? new PowerRoom(type, box, coordMode, rand) : null;
    }

    @Override
    public void buildComponent(ProceduralStructure start, RandomSource rand) {
        path = this.getNextComponentEast(start, this, coordBaseMode, rand, 4, 1) != null;
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        fillWithAir(level, box, 1, 1, 1, 10, 3, 10);
        fillWithBlocks(level, box, 1, 0, 1, 10, 0, 10, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 4, 1, 10, 4, 10, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 0, 5, 0, 11, 5, 11, ModBlocks.REINFORCED_STONE.get().defaultBlockState());

        ConcreteBricks bricks = new ConcreteBricks();
        fillWithRandomizedBlocks(level, box, 0, 0, 0, 11, 4, 0, rand, bricks);
        fillWithRandomizedBlocks(level, box, 0, 0, 1, 0, 4, 10, rand, bricks);
        fillWithRandomizedBlocks(level, box, 0, 0, 11, 11, 4, 11, rand, bricks);
        fillWithRandomizedBlocks(level, box, 11, 0, 1, 11, 4, 10, rand, bricks);
        fillWithRandomizedBlocks(level, box, 5, 1, 1, 5, 3, 6, rand, bricks);
        fillWithRandomizedBlocks(level, box, 6, 1, 6, 10, 3, 6, rand, bricks);

        // Лампы и вентиляторы
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 3, 5, 2, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 3, 5, 5, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 3, 5, 8, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 6, 5, 8, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 9, 5, 8, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 3, 4, 2, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 3, 4, 5, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 3, 4, 8, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 6, 4, 8, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 9, 4, 8, box);

        // Окно
        fillWithBlocks(level, box, 7, 2, 6, 9, 2, 6, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());

        switch (powerType) {
            case 1 -> {
                placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_COLORED_EXT_HAZARD.get().defaultBlockState(), 6, 1, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.CABLE_DETECTOR.get().defaultBlockState(), 6, 2, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_COLORED_EXT_HAZARD.get().defaultBlockState(), 6, 3, 1, box);
                placeLever(level, box, Direction.SOUTH, net.minecraft.world.level.block.state.properties.AttachFace.WALL, false, 6, 2, 2);

                for (int i = 7; i <= 9; i += 2) {
                    BlockState scaffold = ModBlocks.STEEL_SCAFFOLD_STEEL.get().defaultBlockState()
                            .setValue(BlockSteelScaffold.FACING, rotate(Direction.NORTH))
                            .setValue(BlockSteelScaffold.VERTICAL, false);
                    placeBlockAtCurrentPosition(level, scaffold, i, 1, 1, box);
                    placeDeco(level, box, ModBlocks.MACHINE_DIESEL.get(), Direction.EAST, i, 2, 1);
                }
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_PIPE_RIM_RUSTED.get().defaultBlockState(), 8, 2, 1, box);
                fillWithBlocks(level, box, 7, 3, 1, 9, 3, 1,
                        ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                                .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.NORTH)));
                fillWithBlocks(level, box, 10, 1, 1, 10, 1, 3, ModBlocks.DECO_STEEL.get().defaultBlockState());
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_RED_COPPER.get().defaultBlockState(), 10, 2, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 10, 3, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.STEEL_GRATE.get().defaultBlockState().setValue(com.hbm.blocks.deco.BlockSteelGrate.LAYER, 7), 10, 2, 2, box);
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_COMPUTER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoComputer.FACING, rotate(Direction.WEST)), 10, 3, 2, box);
                fillWithBlocks(level, box, 10, 2, 3, 10, 3, 3,
                        ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                                .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.WEST)));
                fillWithBlocks(level, box, 9, 1, 2, 9, 1, 3, ModBlocks.STEEL_GRATE.get().defaultBlockState().setValue(com.hbm.blocks.deco.BlockSteelGrate.LAYER, 7));
                fillWithBlocks(level, box, 9, 1, 5, 10, 1, 5, ModBlocks.BARREL_CORRODED.get().defaultBlockState());
                placeBlockAtCurrentPosition(level, ModBlocks.BARREL_CORRODED.get().defaultBlockState(), 10, 2, 5, box);
                fillWithBlocks(level, box, 6, 1, 5, 6, 2, 5, ModBlocks.BARREL_CORRODED.get().defaultBlockState());
                placeBlockAtCurrentPosition(level, ModBlocks.BARREL_CORRODED.get().defaultBlockState(), 6, 1, 2, box);
            }
            case 2 -> {
                for (int i = 7; i <= 9; i += 2) {
                    fillWithBlocks(level, box, i, 1, 2, i, 1, 4, ModBlocks.DECO_LEAD.get().defaultBlockState());
                    fillWithBlocks(level, box, i, 2, 2, i, 2, 4, ModBlocks.BLOCK_LEAD.get().defaultBlockState());
                    fillWithBlocks(level, box, i, 3, 2, i, 3, 4, ModBlocks.DECO_LEAD.get().defaultBlockState());
                }
                placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_COLORED_EXT_HAZARD.get().defaultBlockState(), 8, 1, 4, box);
                placeBlockAtCurrentPosition(level, Blocks.REDSTONE_LAMP.defaultBlockState(), 8, 2, 4, box);
                placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_COLORED_EXT_HAZARD.get().defaultBlockState(), 8, 3, 4, box);
                placeLever(level, box, Direction.SOUTH, net.minecraft.world.level.block.state.properties.AttachFace.WALL, rand.nextBoolean(), 8, 2, 5);
                placeBlockAtCurrentPosition(level, ModBlocks.PWR_FUEL.get().defaultBlockState(), 8, 1, 3, box);
                placeBlockAtCurrentPosition(level, ModBlocks.PWR_CONTROL.get().defaultBlockState(), 8, 2, 3, box);
                placeBlockAtCurrentPosition(level, ModBlocks.PWR_FUEL.get().defaultBlockState(), 8, 3, 3, box);
                placeBlockAtCurrentPosition(level, Blocks.COPPER_BLOCK.defaultBlockState(), 8, 1, 2, box);
                placeBlockAtCurrentPosition(level, ModBlocks.BLOCK_LEAD.get().defaultBlockState(), 8, 2, 2, box);
                placeBlockAtCurrentPosition(level, Blocks.COPPER_BLOCK.defaultBlockState(), 8, 3, 2, box);
                placeBlockAtCurrentPosition(level, ModBlocks.PWR_CHANNEL.get().defaultBlockState(), 8, 1, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.MACHINE_CONDENSER.get().defaultBlockState(), 8, 2, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.PWR_CHANNEL.get().defaultBlockState(), 8, 3, 1, box);
                fillWithBlocks(level, box, 9, 1, 1, 9, 3, 1, ModBlocks.DECO_STEEL.get().defaultBlockState());
                placeBlockAtCurrentPosition(level, ModBlocks.STEEL_GRATE.get().defaultBlockState().setValue(BlockSteelGrate.LAYER, 7), 10, 1, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_COMPUTER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoComputer.FACING, rotate(Direction.SOUTH)), 10, 2, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.SOUTH)), 10, 3, 1, box);
                fillWithBlocks(level, box, 6, 1, 1, 7, 1, 1, ModBlocks.DECO_PIPE_QUAD_RUSTED.get().defaultBlockState());
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_PIPE_QUAD_RUSTED.get().defaultBlockState(), 7, 3, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.FLUID_DUCT_GAUGE.get().defaultBlockState(), 6, 3, 1, box);

                generateInvContents(level, box, rand, Blocks.CHEST, Direction.SOUTH, 6, 1, 2, "POOL_NUKE_FUEL", 8);
            }
            default -> { // 0
                fillWithBlocks(level, box, 6, 1, 1, 6, 3, 1, ModBlocks.DECO_PIPE_FRAMED_RUSTED.get().defaultBlockState());
                for (int i = 7; i <= 9; i += 2) {
                    placeBlockAtCurrentPosition(level,
                            ModBlocks.MACHINE_ELECTRIC_FURNACE.get().defaultBlockState()
                                    .setValue(com.hbm.blocks.machine.MachineElectricFurnace.FACING, rotate(Direction.SOUTH)),
                            i, 1, 1, box);
                    placeBlockAtCurrentPosition(level, ModBlocks.STEEL_BEAM.get().defaultBlockState(), i, 2, 1, box);
                    placeBlockAtCurrentPosition(level,
                            ModBlocks.MACHINE_ELECTRIC_FURNACE.get().defaultBlockState()
                                    .setValue(com.hbm.blocks.machine.MachineElectricFurnace.FACING, rotate(Direction.SOUTH)),
                            i, 3, 1, box);
                }
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_RED_COPPER.get().defaultBlockState(), 8, 1, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_COLORED_EXT_HAZARD.get().defaultBlockState(), 8, 2, 1, box);
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_RED_COPPER.get().defaultBlockState(), 8, 3, 1, box);
                placeLever(level, box, Direction.SOUTH, net.minecraft.world.level.block.state.properties.AttachFace.WALL, rand.nextBoolean(), 8, 2, 2);

                for (int i = 1; i <= 3; i += 2) {
                    placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 10, i, 1, box);
                    fillWithBlocks(level, box, 10, i, 2, 10, i, 4, ModBlocks.DECO_PIPE_QUAD_RUSTED.get().defaultBlockState());
                    placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 10, i, 5, box);
                }
                placeBlockAtCurrentPosition(level, ModBlocks.DECO_PIPE_FRAMED_RUSTED.get().defaultBlockState(), 10, 2, 1, box);
                placeDeco(level, box, ModBlocks.FLUID_DUCT_GAUGE.get(), Direction.WEST, 10, 2, 5);
                placeBlockAtCurrentPosition(level, ModBlocks.BARREL_PLASTIC.get().defaultBlockState(), 6, 1, 5, box);

                generateInvContents(level, box, rand, Blocks.CHEST, Direction.NORTH, 7, 1, 5, "POOL_SOLID_FUEL", 5);
                generateInvContents(level, box, rand, Blocks.CHEST, Direction.NORTH, 9, 1, 5, "POOL_SOLID_FUEL", 6);
            }
        }

        // Трансформатор
        fillWithBlocks(level, box, 1, 1, 1, 1, 1, 5,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.EAST))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, net.minecraft.world.level.block.state.properties.Half.TOP));
        fillWithBlocks(level, box, 1, 1, 6, 1, 3, 6, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 3, 1, 1, 3, 5,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.EAST))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, net.minecraft.world.level.block.state.properties.Half.BOTTOM));
        placeBlockAtCurrentPosition(level, ModBlocks.MACHINE_TRANSFORMER.get().defaultBlockState(), 1, 2, 1, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CABLE_DIODE.get().defaultBlockState(), 1, 2, 2, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CAPACITOR_COPPER.get().defaultBlockState(), 1, 2, 3, box);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_RED_COPPER.get().defaultBlockState(), 1, 2, 4, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CABLE_SWITCH.get().defaultBlockState(), 1, 2, 5, box);

        // Машина
        for (int i = 1; i <= 5; i += 4) {
            placeBlockAtCurrentPosition(level, ModBlocks.DECO_BERYLLIUM.get().defaultBlockState(), i, 1, 10, box);

            BlockState scaffold = ModBlocks.STEEL_SCAFFOLD_STEEL.get().defaultBlockState()
                    .setValue(BlockSteelScaffold.FACING, rotate(Direction.NORTH))
                    .setValue(BlockSteelScaffold.VERTICAL, false);
            placeBlockAtCurrentPosition(level, scaffold, i, 2, 10, box);
            placeBlockAtCurrentPosition(level, ModBlocks.DECO_BERYLLIUM.get().defaultBlockState(), i, 3, 10, box);
        }

        BlockState scaffold = ModBlocks.STEEL_SCAFFOLD_STEEL.get().defaultBlockState()
                .setValue(BlockSteelScaffold.FACING, rotate(Direction.NORTH))
                .setValue(BlockSteelScaffold.VERTICAL, false);
        placeBlockAtCurrentPosition(level, scaffold, 2, 1, 10, box);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState(), 3, 1, 10, box);

        scaffold = ModBlocks.STEEL_SCAFFOLD_STEEL.get().defaultBlockState()
                .setValue(BlockSteelScaffold.FACING, rotate(Direction.NORTH))
                .setValue(BlockSteelScaffold.VERTICAL, false);
        placeBlockAtCurrentPosition(level, scaffold, 4, 1, 10, box);
        placeBlockAtCurrentPosition(level, ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.NORTH)), 2, 2, 10, box);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_COMPUTER.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.DecoComputer.FACING, rotate(Direction.NORTH)), 3, 2, 10, box);
        placeBlockAtCurrentPosition(level, ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.NORTH)), 4, 2, 10, box);
        fillWithBlocks(level, box, 2, 3, 10, 4, 3, 10,
                ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.NORTH)));

        // Стол
        fillWithBlocks(level, box, 8, 1, 10, 10, 1, 10,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.SOUTH))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, net.minecraft.world.level.block.state.properties.Half.TOP));
        placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.WEST))
                .setValue(net.minecraft.world.level.block.StairBlock.HALF, net.minecraft.world.level.block.state.properties.Half.TOP), 10, 1, 9, box);
        placeBlockAtCurrentPosition(level, Blocks.OAK_STAIRS.defaultBlockState()
                .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.NORTH)), 9, 1, 9, box);
        placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 8, 2, 10, box);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_COMPUTER.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.DecoComputer.FACING, rotate(Direction.NORTH)), 9, 2, 10, box);

        generateInvContents(level, box, rand, Blocks.CHEST, Direction.EAST, 1, 1, 7, "POOL_MACHINE_PARTS", 6);
        generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 7, 1, 10, "POOL_FILING_CABINET", 4);

        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, true, rand.nextBoolean(), 3, 1, 0);
        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 4, 1, 0);
        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.EAST, false, false, 5, 1, 3);

        if (path) fillWithAir(level, box, 11, 1, 7, 11, 2, 8);

        updateConnectedBlocks(level, box);
        return true;
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putBoolean("Path", path);
        nbt.putInt("PowerType", powerType);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        path = nbt.getBoolean("Path");
        powerType = nbt.getInt("PowerType");
    }
}