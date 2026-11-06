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

public class Corridor extends NTMComponent implements ProceduralStructure.ProceduralComponent {

    private boolean path;
    private final int[] decorations = new int[2];

    public Corridor() { }

    /** Конструктор для chain — из Weight::findValidPlacement. */
    public Corridor(int componentType, BoundingBox box, int coordMode, RandomSource rand) {
        this.componentType = componentType;
        this.boundingBox = box;
        this.coordBaseMode = coordMode;
        this.decorations[0] = rand.nextInt(6);
        this.decorations[1] = rand.nextInt(6);
    }

    @Override public String getTypeName() { return "Corridor"; }

    @Override
    public void buildComponent(ProceduralStructure start, RandomSource rand) {
        path = this.getNextComponentNormal(start, this, coordBaseMode, rand, 3, 1) != null;
    }

    /** Вызывается из ProceduralStructure.Weight для создания компонента. */
    public static NTMComponent findValidPlacement(List<NTMComponent> components, RandomSource rand,
                                                  int x, int y, int z, int coordMode, int type) {
        BoundingBox box = ProceduralStructure.getComponentToAddBoundingBox(x, y, z, -3, -1, 0, 6, 6, 7, coordMode);

        return box.minY() > 10 && !ProceduralStructure.findIntersecting(components, box)
                ? new Corridor(type, box, coordMode, rand)
                : null;
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        fillWithAir(level, box, 1, 1, 1, 4, 3, 5);

        fillWithBlocks(level, box, 1, 0, 1, 4, 0, 5, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 4, 1, 4, 4, 5, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());

        fillWithBlocks(level, box, 0, 5, 0, 5, 5, 6, ModBlocks.REINFORCED_STONE.get().defaultBlockState());

        ConcreteBricks bricks = new ConcreteBricks();
        fillWithRandomizedBlocks(level, box, 0, 0, 0, 0, 4, 6, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 6, 4, 4, 6, rand, bricks);
        fillWithRandomizedBlocks(level, box, 5, 0, 0, 5, 4, 6, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 0, 4, 4, 0, rand, bricks);

        // Лампы + вентиляторы
        fillWithBlocks(level, box, 2, 5, 3, 3, 5, 3, ModBlocks.REINFORCED_LAMP.get().defaultBlockState());
        fillWithBlocks(level, box, 2, 4, 3, 3, 4, 3, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN));

        // Декорации в двух нишах
        for (int i = 0; i <= 1; i++) {
            int x = 1 + i * 3;
            switch (decorations[i]) {
                case 1 -> { // desk w/ computer
                    placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, x, 1, 2);
                    placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, x, 1, 4);
                    placeDecoComputer(level, box, x, 2, 2, Direction.SOUTH);
                }
                case 2 -> { // couch
                    placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, false, x, 1, 2);
                    placeStairs(level, box, Blocks.OAK_STAIRS, i < 1 ? Direction.WEST : Direction.EAST, false, x, 1, 3);
                    placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, x, 1, 4);
                }
                case 3 -> {
                    placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, x, 1, 2);
                    placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), i < 1 ? Direction.WEST : Direction.EAST, true, x, 1, 3);
                    placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, true, x, 1, 4);
                    placeBlockAtCurrentPosition(level, Blocks.FLOWER_POT.defaultBlockState(), x, 2, 2, box);
                }
                case 4 -> {
                    fillWithBlocks(level, box, x, 1, 1, x, 3, 1, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState());
                    placeBlockAtCurrentPosition(level, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState(), x, 1, 3, box);
                    fillWithBlocks(level, box, x, 3, 2, x, 3, 4,
                            ModBlocks.CONCRETE_SMOOTH_STAIRS.get().defaultBlockState()
                                    .setValue(net.minecraft.world.level.block.StairBlock.FACING,
                                            rotate(i < 1 ? Direction.EAST : Direction.WEST))
                                    .setValue(net.minecraft.world.level.block.StairBlock.HALF, Half.TOP));
                    fillWithBlocks(level, box, x, 1, 5, x, 3, 5, ModBlocks.DECO_TUNGSTEN.get().defaultBlockState());
                    placeDecoTape(level, box, x, 1, 2, i < 1 ? Direction.EAST : Direction.WEST);
                    placeDecoTape(level, box, x, 1, 4, i < 1 ? Direction.EAST : Direction.WEST);
                    placeDecoComputer(level, box, x, 2, 3, i < 1 ? Direction.WEST : Direction.EAST);
                }
                case 5 -> {
                    placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), x, 1, 1, box);
                    placeBlockAtCurrentPosition(level, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), x, 2, 1, box);
                    placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, true, x, 1, 3);
                    placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, true, x, 1, 4);
                    placeDeco(level, box, ModBlocks.RADIOREC.get(), i < 1 ? Direction.EAST : Direction.WEST, x, 2, 3);
                }
                default -> { // table w/ chairs
                    placeStairs(level, box, Blocks.OAK_STAIRS, Direction.NORTH, false, x, 1, 2);
                    placeStairs(level, box, Blocks.OAK_STAIRS, Direction.SOUTH, false, x, 1, 4);
                    placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), x, 1, 3, box);
                    placeBlockAtCurrentPosition(level, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), x, 2, 3, box);
                }
            }
        }

        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, true, rand.nextBoolean(), 2, 1, 0);
        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 3, 1, 0);

        if (path) fillWithAir(level, box, 2, 1, 6, 3, 2, 6);

        updateConnectedBlocks(level, box);
        return true;
    }

    /** Обёртка над placeDeco для DecoComputer — FACING = "спина", инвертируем. */
    private void placeDecoComputer(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction localFacing) {
        Direction facing = rotate(localFacing).getOpposite();
        BlockState state = ModBlocks.DECO_COMPUTER.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.DecoComputer.FACING, facing);
        placeBlockAtCurrentPosition(level, state, x, y, z, box);
    }

    /** Обёртка над placeDeco для DecoTapeRecorder. */
    private void placeDecoTape(WorldGenLevel level, BoundingBox box, int x, int y, int z, Direction localFacing) {
        placeDeco(level, box, ModBlocks.TAPE_RECORDER.get(), localFacing, x, y, z);
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putInt("Deco0", decorations[0]);
        nbt.putInt("Deco1", decorations[1]);
        nbt.putBoolean("Path", path);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        decorations[0] = nbt.getInt("Deco0");
        decorations[1] = nbt.getInt("Deco1");
        path = nbt.getBoolean("Path");
    }
}