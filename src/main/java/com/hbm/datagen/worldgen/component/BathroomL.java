package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class BathroomL extends NTMComponent implements ProceduralStructure.ProceduralComponent {

    private boolean path;

    public BathroomL() { }

    public BathroomL(int componentType, BoundingBox box, int coordMode) {
        this.componentType = componentType;
        this.boundingBox = box;
        this.coordBaseMode = coordMode;
    }

    public BathroomL(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = ProceduralStructure.getComponentToAddBoundingBox(
                minX, 64, minZ, -3, -1, 0, 9, 6, 11, coordBaseMode);
    }

    @Override
    public String getTypeName() { return "BathroomL"; }

    public static NTMComponent findValidPlacement(List<NTMComponent> components, RandomSource rand,
                                                  int x, int y, int z, int coordMode, int type) {
        BoundingBox box = ProceduralStructure.getComponentToAddBoundingBox(x, y, z, -3, -1, 0, 9, 6, 11, coordMode);

        return box.minY() > 10 && !ProceduralStructure.findIntersecting(components, box)
                ? new BathroomL(type, box, coordMode) : null;
    }

    @Override
    public void buildComponent(ProceduralStructure start, RandomSource rand) {
        path = this.getNextComponentEast(start, this, coordBaseMode, rand, 3, 1) != null;
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        fillWithAir(level, box, 1, 1, 1, 7, 3, 9);
        fillWithBlocks(level, box, 1, 0, 1, 7, 0, 9, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 4, 1, 7, 4, 9, ModBlocks.VINYL_TILE_SMALL.get().defaultBlockState());
        fillWithBlocks(level, box, 0, 5, 0, 8, 5, 10, ModBlocks.REINFORCED_STONE.get().defaultBlockState());

        ConcreteBricks bricks = new ConcreteBricks();
        fillWithRandomizedBlocks(level, box, 0, 0, 0, 0, 4, 10, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 10, 7, 4, 10, rand, bricks);
        fillWithRandomizedBlocks(level, box, 8, 0, 0, 8, 4, 10, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 0, 7, 4, 0, rand, bricks);

        // Лампы и вентиляторы
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 2, 5, 3, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 2, 5, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 5, 5, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_LAMP.get().defaultBlockState(), 5, 5, 3, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 2, 4, 3, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 2, 4, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 5, 4, 7, box);
        placeBlockAtCurrentPosition(level, ModBlocks.FAN.get().defaultBlockState().setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, Direction.DOWN), 5, 4, 3, box);

        // Раковины
        for (int i = 2; i <= 8; i += 2) {
            placeBlockAtCurrentPosition(level, Blocks.CAULDRON.defaultBlockState(), 1, 1, i, box);
            placeBlockAtCurrentPosition(level, ModBlocks.CONCRETE_SLAB_CONCRETE_SMOOTH.get().defaultBlockState(), 1, 1, i + 1, box);
            placeBlockAtCurrentPosition(level, Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, rotate(Direction.EAST)), 1, 2, i, box);
        }

        // Сушилки для рук
        BlockState buttonSouth = Blocks.STONE_BUTTON.defaultBlockState()
                .setValue(net.minecraft.world.level.block.ButtonBlock.FACE,
                        net.minecraft.world.level.block.state.properties.AttachFace.WALL)
                .setValue(net.minecraft.world.level.block.ButtonBlock.FACING, rotate(Direction.NORTH));
        BlockState fanSouth = ModBlocks.FAN.get().defaultBlockState()
                .setValue(com.hbm.blocks.machine.BlockMachineFan.FACING, rotate(Direction.SOUTH));
        placeBlockAtCurrentPosition(level, ModBlocks.STEEL_BEAM.get().defaultBlockState(), 4, 1, 9, box);
        placeBlockAtCurrentPosition(level,
                fanSouth,
                4, 2, 9, box);
        placeBlockAtCurrentPosition(level, buttonSouth, 3, 2, 9, box);
        placeBlockAtCurrentPosition(level, ModBlocks.STEEL_BEAM.get().defaultBlockState(), 6, 1, 9, box);
        placeBlockAtCurrentPosition(level,
                fanSouth,
                6, 2, 9, box);
        placeBlockAtCurrentPosition(level, buttonSouth, 7, 2, 9, box);

        // Кабинки с туалетами
        for (int i = 1; i <= 5; i += 2) {
            placeDoor(level, box, ModBlocks.DOOR_METAL.get(), Direction.EAST, false, rand.nextBoolean(), 5, 1, i);
            fillWithDeco(level, box, ModBlocks.STEEL_CORNER.get(), Direction.NORTH, 5, 1, i + 1, 5, 2, i + 1);
            fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.NORTH, 6, 1, i + 1, 7, 2, i + 1);
            placeBlockAtCurrentPosition(level, ModBlocks.DECO_PIPE_RIM.get().defaultBlockState(), 7, 1, i, box);
            placeBlockAtCurrentPosition(level, Blocks.OAK_TRAPDOOR.defaultBlockState(), 7, 2, i, box);
        }

        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, true, rand.nextBoolean(), 2, 1, 0);
        placeDoor(level, box, ModBlocks.DOOR_BUNKER.get(), Direction.SOUTH, false, rand.nextBoolean(), 3, 1, 0);

        if (path) fillWithAir(level, box, 8, 1, 7, 8, 2, 8);

        updateConnectedBlocks(level, box);
        return true;
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