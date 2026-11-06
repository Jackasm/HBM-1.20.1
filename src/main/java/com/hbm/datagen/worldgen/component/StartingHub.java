package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.BlockMachineFan;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StartingHub extends NTMComponent implements ProceduralStructure.ProceduralComponent {

    private final boolean[] paths = new boolean[3];

    public StartingHub() { }

    public StartingHub(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = ProceduralStructure.getComponentToAddBoundingBox(
                minX, 64, minZ, 0, 0, 0, 8, 7, 8, coordBaseMode);
    }

    public StartingHub(int componentType, BoundingBox box, int coordMode) {
        this.componentType = componentType;
        this.boundingBox = box;
        this.coordBaseMode = coordMode;
    }

    @Override
    public String getTypeName() { return "StartingHub"; }

    @Override
    public void buildComponent(ProceduralStructure start, RandomSource rand) {
        paths[0] = this.getNextComponentEast(start, this, coordBaseMode, rand, 5, 1) != null;
        paths[1] = this.getNextComponentAntiNormal(start, this, coordBaseMode, rand, 4, 1) != null;
        paths[2] = this.getNextComponentWest(start, this, coordBaseMode, rand, 3, 1) != null;
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        if (!heightCalculated) {
            int surfaceY = getAverageHeight(level, boundingBox);

            this.hpos = surfaceY - boundingBox.minY();
            heightCalculated = true;
        }

        fillWithAir(level, box, 1, 1, 1, 6, 3, 6);

        // Floor
        fillWithBlocks(level, box, 1, 0, 1, 6, 0, 6, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        // Ceiling
        fillWithBlocks(level, box, 1, 4, 1, 6, 4, 6, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        // Upper shield
        fillWithBlocks(level, box, 1, 4, 4, 3, 4, 6, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 0, 5, 0, 7, 5, 7, ModBlocks.REINFORCED_STONE.get().defaultBlockState());

        // Walls
        ConcreteBricks bricks = new ConcreteBricks();
        fillWithRandomizedBlocks(level, box, 0, 0, 0, 0, 4, 7, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 7, 6, 4, 7, rand, bricks);
        fillWithRandomizedBlocks(level, box, 7, 0, 0, 7, 4, 7, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 0, 6, 4, 0, rand, bricks);

        // Верхний люк
        int hpos = this.hpos;

        placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_SLAB_CONCRETE.get().defaultBlockState(), 1, hpos, 5, box);
        fillWithBlocks(level, box, 1, hpos, 4, 1, hpos, 6,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.EAST))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, Half.BOTTOM));
        placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_SLAB_CONCRETE.get().defaultBlockState(), 2, hpos, 3, box);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, false, 2, hpos, 4);
        placeBlockAtCurrentPosition(level, ModBlocks.TRAPDOOR_STEEL.get().defaultBlockState(), 2, hpos, 5, box);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, false, 2, hpos, 6);
        placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_SLAB_CONCRETE.get().defaultBlockState(), 2, hpos, 7, box);
        fillWithBlocks(level, box, 3, hpos, 4, 3, hpos, 6,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.WEST))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, Half.BOTTOM));
        placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_SLAB_CONCRETE.get().defaultBlockState(), 4, hpos, 5, box);

        // Тоннель вниз
        fillWithBlocks(level, box, 1, 6, 4, 1, hpos - 1, 6, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 2, 1, 6, 2, hpos - 1, 6, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 3, 6, 4, 3, hpos - 1, 6, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 2, 6, 4, 2, hpos - 1, 4, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithLadders(level, box, ModBlocks.LADDER_STURDY.get(), Direction.NORTH, 2, 1, 5, 2, hpos - 1, 5);

        // Лампы и вентиляторы
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 2, 5, 2, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 5, 5, 2, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 5, 5, 5, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(BlockMachineFan.FACING, Direction.DOWN), 2, 4, 2, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(BlockMachineFan.FACING, Direction.DOWN), 5, 4, 2, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(BlockMachineFan.FACING, Direction.DOWN), 5, 4, 5, box);

        // Машина + сундук
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState(), 3, 1, 6, box);
        generateInvContents(level, box, rand, Blocks.CHEST, Direction.NORTH, 4, 1, 6, "POOL_ANTENNA", 5);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState(), 5, 1, 6, box);

        // Столешница из smooth stairs (top)
        fillWithBlocks(level, box, 3, 2, 6, 5, 2, 6,
                ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, rotate(Direction.SOUTH))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, Half.TOP));
        fillWithBlocks(level, box, 3, 3, 6, 5, 3, 6,
                ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                        .setValue(com.hbm.blocks.deco.DecoTapeRecorder.FACING, rotate(Direction.NORTH)));

        // Стол
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.WEST, true, 3, 1, 4);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, 4, 1, 4);
        placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.EAST, true, 5, 1, 4);
        placeDecoComputer(level, box, 4, 2, 4, Direction.SOUTH);

        // Проходы
        if (paths[0]) fillWithAir(level, box, 7, 1, 2, 7, 2, 3);
        if (paths[1]) fillWithAir(level, box, 3, 1, 0, 4, 2, 0);
        if (paths[2]) fillWithAir(level, box, 0, 1, 2, 0, 2, 3);

        updateConnectedBlocks(level, box);
        return true;
    }

    private void placeDecoComputer(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction localFacing) {
        Direction facing = rotate(localFacing);
        BlockState state = ModBlocks.DECO_COMPUTER.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.DecoComputer.FACING, facing);
        placeBlockAtCurrentPosition(level, state, x, y, z, box);
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putBoolean("heightCalculated", heightCalculated);
        for (int i = 0; i < paths.length; i++) {
            nbt.putBoolean("path" + i, paths[i]);
        }
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        this.heightCalculated = nbt.getBoolean("heightCalculated");
        for (int i = 0; i < paths.length; i++) {
            paths[i] = nbt.getBoolean("path" + i);
        }
    }
}