package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class Laboratory extends NTMComponent implements ProceduralStructure.ProceduralComponent {

    private final boolean[] paths = new boolean[2];

    public Laboratory() { }

    public Laboratory(int componentType, BoundingBox box, int coordMode) {
        this.componentType = componentType;
        this.boundingBox = box;
        this.coordBaseMode = coordMode;
    }

    public Laboratory(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = ProceduralStructure.getComponentToAddBoundingBox(
                minX, 64, minZ, -6, -1, 0, 9, 6, 12, coordBaseMode);
    }

    @Override
    public String getTypeName() { return "Laboratory"; }

    public static NTMComponent findValidPlacement(List<NTMComponent> components, RandomSource rand,
                                                  int x, int y, int z, int coordMode, int type) {
        BoundingBox box = ProceduralStructure.getComponentToAddBoundingBox(x, y, z, -6, -1, 0, 9, 6, 12, coordMode);
        return box.minY() > 10 && !ProceduralStructure.findIntersecting(components, box)
                ? new Laboratory(type, box, coordMode) : null;
    }

    @Override
    public void buildComponent(ProceduralStructure start, RandomSource rand) {
        paths[0] = this.getNextComponentWest(start, this, coordBaseMode, rand, 3, 1) != null;
        paths[1] = this.getNextComponentNormal(start, this, coordBaseMode, rand, 6, 1) != null;
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        fillWithAir(level, box, 1, 1, 1, 7, 3, 11);
        fillWithBlocks(level, box, 1, 0, 1, 7, 0, 11, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 4, 1, 7, 4, 11, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 0, 5, 0, 8, 5, 12, ModBlocks.REINFORCED_STONE.get().defaultBlockState());

        // Стены из brick_concrete
        BlockState brickConcrete = ModBlocks.BRICK_CONCRETE.get().defaultBlockState();
        fillWithBlocks(level, box, 0, 0, 0, 0, 4, 12, brickConcrete);
        fillWithBlocks(level, box, 1, 0, 12, 7, 4, 12, brickConcrete);
        fillWithBlocks(level, box, 8, 0, 0, 8, 4, 12, brickConcrete);
        fillWithBlocks(level, box, 1, 0, 0, 7, 4, 0, brickConcrete);

        // Лампы
        for (int x = 3; x <= 5; x += 2) {
            for (int z = 3; z <= 9; z += 3) {
                placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), x, 5, z, box);
                placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), x, 4, z, box);
            }
        }

        // Диван со столиком
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 1, 1, 1);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, false, 2, 1, 1);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, false, 3, 1, 1);
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 4, 1, 1, box);
        placeBlockAtCurrentPosition(level, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 4, 2, 1, box);

        // Настенная машина
        fillWithBlocks(level, box, 1, 1, 5, 1, 3, 5, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState());
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 1, 1, 6, box);
        placeDecoComputer(level, box, 1, 2, 6, Direction.WEST);
        placeDecoTape(level, box, 1, 3, 6, Direction.EAST);
        fillWithBlocks(level, box, 1, 1, 7, 1, 3, 7,
                ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.EAST)));
        fillWithBlocks(level, box, 1, 1, 8, 1, 3, 8, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 1, 9, 1, 1, 10,
                ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.EAST)));
        fillWithBlocks(level, box, 1, 2, 9, 1, 2, 10,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.WEST))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, net.minecraft.world.level.block.state.properties.Half.TOP));
        fillWithBlocks(level, box, 1, 3, 9, 1, 3, 10,
                ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.EAST)));
        fillWithBlocks(level, box, 1, 1, 11, 1, 3, 11, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState());

        // Столы с компьютерами
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.NORTH, 3, 1, 4, "POOL_MACHINE_PARTS", 6);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, 3, 1, 5);
        fillWithBlocks(level, box, 4, 1, 5, 4, 1, 7,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.EAST))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, net.minecraft.world.level.block.state.properties.Half.TOP));
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 3, 1, 6);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, 3, 1, 9);
        fillWithBlocks(level, box, 4, 1, 9, 4, 1, 11,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.EAST))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, net.minecraft.world.level.block.state.properties.Half.TOP));
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 3, 1, 10);
        placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), 3, 2, 5, box);
        placeDecoComputer(level, box, 4, 2, 6, Direction.EAST);
        placeDecoComputer(level, box, 4, 2, 10, Direction.EAST);

        // Рычаг-машина у стены
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.EAST, true, 7, 1, 3);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_RED_COPPER.get().defaultBlockState(), 7, 2, 3, box);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.EAST, false, 7, 3, 3);
        placeLever(level, box, Direction.WEST, net.minecraft.world.level.block.state.properties.AttachFace.WALL, rand.nextBoolean(), 6, 2, 3);

        fillWithBlocks(level, box, 7, 1, 4, 7, 2, 4,
                ModBlocks.STEEL_POLES.get().defaultBlockState());
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_STEEL.get().defaultBlockState(), 7, 3, 4, box);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState(), 7, 1, 5, box);
        placeBlockAtCurrentPosition(level,
                ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.WEST)),
                7, 1, 6, box);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState(), 7, 1, 7, box);
        fillWithBlocks(level, box, 7, 2, 5, 7, 2, 7,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.EAST))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, net.minecraft.world.level.block.state.properties.Half.TOP));
        fillWithBlocks(level, box, 7, 3, 5, 7, 3, 7,
                ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.WEST)));

        // Стол с сундуком
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 7, 1, 9, box);
        placeBlockAtCurrentPosition(level, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 7, 2, 9, box);
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.WEST, 7, 1, 10, "POOL_VAULT_LAB", 8);

        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, true, rand.nextBoolean(), 5, 1, 0);
        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 6, 1, 0);

        if (paths[0]) fillWithAir(level, box, 0, 1, 2, 0, 2, 3);
        if (paths[1]) fillWithAir(level, box, 5, 1, 12, 6, 2, 12);

        updateConnectedBlocks(level, box);
        return true;
    }

    private void placeDecoComputer(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction localFacing) {
        Direction facing = rotate(localFacing).getOpposite();
        BlockState state = ModBlocks.DECO_COMPUTER.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.DecoComputer.FACING, facing);
        placeBlockAtCurrentPosition(level, state, x, y, z, box);
    }

    private void placeDecoTape(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction localFacing) {
        placeDeco(level, box, ModBlocks.TAPE_RECORDER.get(), localFacing, x, y, z);
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        for (int i = 0; i < paths.length; i++) nbt.putBoolean("path" + i, paths[i]);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        for (int i = 0; i < paths.length; i++) paths[i] = nbt.getBoolean("path" + i);
    }
}