package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class BedroomL extends NTMComponent implements ProceduralStructure.ProceduralComponent {

    private boolean path;

    public BedroomL() { }

    public BedroomL(int componentType, BoundingBox box, int coordMode) {
        this.componentType = componentType;
        this.boundingBox = box;
        this.coordBaseMode = coordMode;
    }

    public BedroomL(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = ProceduralStructure.getComponentToAddBoundingBox(
                minX, 64, minZ, -8, -1, 0, 10, 6, 11, coordBaseMode);
    }

    @Override
    public String getTypeName() { return "BedroomL"; }

    public static NTMComponent findValidPlacement(List<NTMComponent> components, RandomSource rand,
                                                  int x, int y, int z, int coordMode, int type) {
        BoundingBox box = ProceduralStructure.getComponentToAddBoundingBox(x, y, z, -8, -1, 0, 10, 6, 11, coordMode);

        return box.minY() > 10 && !ProceduralStructure.findIntersecting(components, box)
                ? new BedroomL(type, box, coordMode) : null;
    }

    @Override
    public void buildComponent(ProceduralStructure start, RandomSource rand) {
        path = this.getNextComponentWest(start, this, coordBaseMode, rand, 9, 1) != null;
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        fillWithAir(level, box, 4, 1, 1, 8, 3, 4);
        fillWithAir(level, box, 1, 1, 5, 8, 3, 9);

        fillWithBlocks(level, box, 4, 0, 1, 8, 0, 4, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 0, 5, 8, 0, 9, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 4, 4, 1, 8, 4, 4, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 4, 5, 8, 4, 9, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());

        fillWithBlocks(level, box, 3, 5, 0, 9, 5, 3, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 0, 5, 4, 9, 5, 10, ModBlocks.REINFORCED_STONE.get().defaultBlockState());

        ConcreteBricks bricks = new ConcreteBricks();
        fillWithRandomizedBlocks(level, box, 0, 0, 4, 0, 4, 10, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 10, 8, 4, 10, rand, bricks);
        fillWithRandomizedBlocks(level, box, 9, 0, 0, 9, 4, 10, rand, bricks);
        fillWithRandomizedBlocks(level, box, 4, 0, 0, 8, 4, 0, rand, bricks);
        fillWithRandomizedBlocks(level, box, 3, 0, 0, 3, 4, 4, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 4, 2, 4, 4, rand, bricks);

        // Лампы и вентиляторы
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 3, 5, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 6, 5, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 6, 5, 3, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 3, 4, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 6, 4, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 6, 4, 3, box);

        // Кровати со столиком
        placeBed(level, box, Direction.WEST, 5, 1, 1);
        placeBed(level, box, Direction.WEST, 5, 1, 3);
        placeBed(level, box, Direction.NORTH, 3, 1, 6);
        placeBed(level, box, Direction.NORTH, 1, 1, 6);

        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.WEST, true, 4, 1, 2);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.WEST, true, 4, 1, 4);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, 4, 1, 5);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, 2, 1, 5);

        placeDeco(level, box, ModBlocks.RADIOREC.get(), Direction.EAST, 4, 2, 4);

        // Стол с микроволновкой
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, 8, 1, 3);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, true, 8, 1, 4);
        placeBlockAtCurrentPosition(level, Blocks.NOTE_BLOCK.defaultBlockState(), 8, 1, 5, box);
        placeBlockAtCurrentPosition(level,
                ModBlocks.MACHINE_MICROWAVE.get().defaultBlockState()
                        .setValue(com.hbm.blocks.machine.MachineMicrowave.FACING, rotate(Direction.WEST)),
                8, 2, 4, box);

        // Стол с компьютером
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.EAST, true, 6, 1, 9);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, true, 5, 1, 9);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.WEST, true, 4, 1, 9);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, false, 5, 1, 8);
        placeDecoComputer(level, box, 5, 2, 9, Direction.SOUTH);

        generateInvContents(level, box, rand, ModBlocks.FILING_CABINET.get(), Direction.NORTH, 3, 1, 9, "POOL_FILING_CABINET", 5);

        // Шкафчики-локеры
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.WEST, 8, 1, 7, "POOL_VAULT_LOCKERS", 3);
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.WEST, 8, 2, 7, "POOL_VAULT_LOCKERS", 5);
        fillWithBlocks(level, box, 8, 1, 8, 8, 2, 8, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState());
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.WEST, 8, 1, 9, "POOL_VAULT_LOCKERS", 4);
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.WEST, 8, 2, 9, "POOL_VAULT_LOCKERS", 5);
        fillWithBlocks(level, box, 8, 3, 7, 8, 3, 9,
                Blocks.OAK_TRAPDOOR.defaultBlockState()
                        .setValue(net.minecraft.world.level.block.TrapDoorBlock.FACING, rotate(Direction.SOUTH))
                        .setValue(net.minecraft.world.level.block.TrapDoorBlock.HALF, Half.BOTTOM));

        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, true, rand.nextBoolean(), 7, 1, 0);
        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 8, 1, 0);

        if (path) fillWithAir(level, box, 0, 1, 8, 0, 2, 9);

        updateConnectedBlocks(level, box);
        return true;
    }

    private void placeDecoComputer(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction localFacing) {
        Direction facing = rotate(localFacing).getOpposite();
        BlockState state = ModBlocks.DECO_COMPUTER.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.DecoComputer.FACING, facing);
        placeBlockAtCurrentPosition(level, state, x, y, z, box);
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putBoolean("Path", path);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        path = nbt.getBoolean("Path");
    }
}