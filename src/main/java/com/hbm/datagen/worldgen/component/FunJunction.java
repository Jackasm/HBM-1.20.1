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

public class FunJunction extends NTMComponent implements ProceduralStructure.ProceduralComponent {

    private final boolean[] paths = new boolean[2];

    public FunJunction() { }

    public FunJunction(int componentType, BoundingBox box, int coordMode) {
        this.componentType = componentType;
        this.boundingBox = box;
        this.coordBaseMode = coordMode;
    }

    public FunJunction(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = ProceduralStructure.getComponentToAddBoundingBox(
                minX, 64, minZ, -5, -1, 0, 8, 6, 12, coordBaseMode);
    }

    @Override
    public String getTypeName() { return "FunJunction"; }

    public static NTMComponent findValidPlacement(List<NTMComponent> components, RandomSource rand,
                                                  int x, int y, int z, int coordMode, int type) {
        BoundingBox box = ProceduralStructure.getComponentToAddBoundingBox(x, y, z, -5, -1, 0, 8, 6, 12, coordMode);

        return box.minY() > 10 && !ProceduralStructure.findIntersecting(components, box)
                ? new FunJunction(type, box, coordMode) : null;
    }

    @Override
    public void buildComponent(ProceduralStructure start, RandomSource rand) {
        paths[0] = this.getNextComponentEast(start, this, coordBaseMode, rand, 6, 1) != null;
        paths[1] = this.getNextComponentNormal(start, this, coordBaseMode, rand, 5, 1) != null;
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        fillWithAir(level, box, 1, 1, 1, 6, 3, 10);
        fillWithBlocks(level, box, 1, 0, 1, 6, 0, 10, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 4, 1, 6, 4, 10, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 0, 5, 0, 7, 5, 11, ModBlocks.REINFORCED_STONE.get().defaultBlockState());

        ConcreteBricks bricks = new ConcreteBricks();
        fillWithRandomizedBlocks(level, box, 0, 0, 0, 0, 4, 11, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 11, 6, 4, 11, rand, bricks);
        fillWithRandomizedBlocks(level, box, 7, 0, 0, 7, 4, 11, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 0, 6, 4, 0, rand, bricks);

        // Лампы
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 2, 5, 3, box);
        fillWithBlocks(level, box, 5, 5, 5, 5, 5, 6, ModBlocks.REINFORCED_LAMP.get().defaultBlockState());
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 2, 5, 8, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 2, 4, 3, box);
        fillWithBlocks(level, box, 5, 4, 5, 5, 4, 6, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN));
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 2, 4, 8, box);

        // Диваны со столиками
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 1, 1, 1);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, false, 2, 1, 1);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, false, 3, 1, 1);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, false, 1, 1, 4);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.WEST, false, 1, 1, 5);
        fillWithBlocks(level, box, 1, 1, 6, 2, 1, 6,
                Blocks.OAK_STAIRS.defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.SOUTH)));
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, false, 3, 1, 6);

        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 1, 1, 3, box);
        placeBlockAtCurrentPosition(level, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 1, 2, 3, box);
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 3, 1, 4, box);
        placeBlockAtCurrentPosition(level, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 3, 2, 4, box);

        // Столик + сундук
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 6, 1, 2, box);
        placeBlockAtCurrentPosition(level, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 6, 2, 2, box);
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.WEST, 6, 1, 3, "POOL_VAULT_LOCKERS", 8);

        // Стол с компьютером
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, 1, 1, 8);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.WEST, true, 1, 1, 9);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, true, 1, 1, 10);
        placeStairs(level, box, Blocks.OAK_STAIRS, Direction.EAST, false, 2, 1, 9);
        placeDecoComputer(level, box, 1, 2, 9, Direction.WEST);
        if (rand.nextBoolean()) placeRandomBobble(level, box, rand, 1, 2, 8);

        // Музыкальный автомат
        fillWithBlocks(level, box, 6, 1, 8, 6, 2, 8, Blocks.NOTE_BLOCK.defaultBlockState());
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState(), 6, 1, 9, box);
        placeBlockAtCurrentPosition(level,
                ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.WEST)),
                6, 2, 9, box);
        fillWithBlocks(level, box, 6, 3, 8, 6, 3, 9, ModBlocks.CONCRETE_SLAB_CONCRETE_SMOOTH.get().defaultBlockState());

        placeLever(level, box, Direction.WEST, net.minecraft.world.level.block.state.properties.AttachFace.WALL, rand.nextBoolean(), 5, 1, 9);

        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, true, rand.nextBoolean(), 4, 1, 0);
        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 5, 1, 0);

        if (paths[0]) fillWithAir(level, box, 7, 1, 5, 7, 2, 6);
        if (paths[1]) fillWithAir(level, box, 4, 1, 11, 5, 2, 11);

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
        for (int i = 0; i < paths.length; i++) nbt.putBoolean("path" + i, paths[i]);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        for (int i = 0; i < paths.length; i++) paths[i] = nbt.getBoolean("path" + i);
    }
}