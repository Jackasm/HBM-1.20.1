package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsLegacy;

import com.hbm.itempool.LootGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Порт NTMLab1 из CivilianFeatures 1.7.10.
 */
public class NTMLab1 extends NTMComponent {

    private final ConcreteBricks bricks = new ConcreteBricks();
    private final LabTiles tiles = new LabTiles();
    private final boolean[] hasPlacedLoot = new boolean[2];

    public NTMLab1() { }

    public NTMLab1(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = CivilianFeatures.boxFromSize(minX, 64, minZ, 9, 4, 7, this.coordBaseMode);
    }

    @Override
    public String getTypeName() { return "NTMLab1"; }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putBoolean("hasLoot1", hasPlacedLoot[0]);
        nbt.putBoolean("hasLoot2", hasPlacedLoot[1]);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        hasPlacedLoot[0] = nbt.getBoolean("hasLoot1");
        hasPlacedLoot[1] = nbt.getBoolean("hasLoot2");
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        if (!heightCalculated) {
            if (!setAverageHeight(level, box, this.boundingBox.minY())) return false;
            heightCalculated = true;
        }

        placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, 9, 5, -1, box);
        placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 3, 6, 9, 7, -1, box);

        BlockState belowFront = level.getBlockState(new BlockPos(
                getXWithOffset(2, 6), getYWithOffset(0), getZWithOffset(2, 6)));
        if (belowFront.isAir() || belowFront.canBeReplaced()) {
            placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 2, 6, -1, box);
            placeStairs(level, box, Blocks.STONE_BRICK_STAIRS, Direction.EAST, false, 2, 0, 6);
        }

        fillWithAir(level, box, 1, 0, 1, 8, 4, 4);
        fillWithAir(level, box, 4, 0, 4, 8, 4, 6);
        fillWithAir(level, box, 3, 1, 6, 3, 2, 6);

        // Pillars
        fillWithBlocks(level, box, 0, 0, 0, 0, 3, 0, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        fillWithBlocks(level, box, 9, 0, 0, 9, 3, 0, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        fillWithBlocks(level, box, 0, 0, 1, 0, 0, 4, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        fillWithBlocks(level, box, 9, 0, 1, 9, 0, 6, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        fillWithBlocks(level, box, 0, 0, 5, 0, 3, 5, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        fillWithBlocks(level, box, 3, 0, 5, 3, 3, 5, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        fillWithBlocks(level, box, 3, 0, 7, 3, 3, 7, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        fillWithBlocks(level, box, 9, 0, 7, 9, 3, 7, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());

        // Walls
        fillWithRandomizedBlocks(level, box, 1, 0, 0, 8, 3, 0, rand, bricks);
        fillWithRandomizedBlocks(level, box, 0, 4, 0, 9, 4, 0, rand, bricks);
        fillWithRandomizedBlocks(level, box, 0, 1, 1, 0, 3, 4, rand, bricks);
        fillWithRandomizedBlocks(level, box, 0, 4, 0, 0, 4, 5, rand, bricks);
        fillWithRandomizedBlocks(level, box, 1, 0, 5, 2, 4, 5, rand, bricks);
        placeBlockAtCurrentPosition(level, ModBlocks.BRICK_CONCRETE_BROKEN.get().defaultBlockState(), 3, 4, 5, box);
        fillWithRandomizedBlocks(level, box, 3, 3, 6, 3, 4, 6, rand, bricks);
        fillWithRandomizedBlocks(level, box, 4, 0, 7, 8, 1, 7, rand, bricks);
        fillWithRandomizedBlocks(level, box, 4, 2, 7, 4, 3, 7, rand, bricks);
        fillWithRandomizedBlocks(level, box, 8, 2, 7, 8, 3, 7, rand, bricks);
        randomlyFillWithBlocks(level, box, rand, 0.75F,
                5, 2, 7, 7, 3, 7, Blocks.GLASS_PANE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        fillWithRandomizedBlocks(level, box, 3, 4, 7, 9, 4, 7, rand, bricks);
        fillWithRandomizedBlocks(level, box, 9, 1, 1, 9, 4, 6, rand, bricks);

        // Floor & Ceiling
        fillWithRandomizedBlocks(level, box, 1, 0, 1, 8, 0, 4, rand, tiles);
        fillWithRandomizedBlocks(level, box, 4, 0, 5, 8, 0, 6, rand, tiles);
        placeBlockAtCurrentPosition(level, ModBlocks.TILE_LAB_CRACKED.get().defaultBlockState(), 3, 0, 6, box);

        fillWithBlocks(level, box, 1, 3, 1, 1, 4, 4, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        fillWithBlocks(level, box, 2, 4, 1, 8, 4, 4, ModBlocks.BRICK_LIGHT.get().defaultBlockState());
        fillWithBlocks(level, box, 4, 4, 5, 8, 4, 6, ModBlocks.BRICK_LIGHT.get().defaultBlockState());

        // Decorations
        fillWithBlocks(level, box, 1, 1, 1, 1, 1, 4, Blocks.DIRT.defaultBlockState());

        fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.EAST, 2, 1, 1, 2, 1, 4);
        fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.EAST, 2, 3, 1, 2, 3, 4);
        placeBlockAtCurrentPosition(level, ModBlocks.PLANT_FLOWER_FOXGLOVE.get().defaultBlockState(), 1, 2, 1, box);
        placeBlockAtCurrentPosition(level, ModBlocks.PLANT_FLOWER_TOBACCO.get().defaultBlockState(),   1, 2, 2, box);
        placeBlockAtCurrentPosition(level, ModBlocks.PLANT_FLOWER_NIGHTSHADE.get().defaultBlockState(), 1, 2, 3, box);
        placeBlockAtCurrentPosition(level, ModBlocks.PLANT_FLOWER_WEED.get().defaultBlockState(),      1, 2, 4, box);

        placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, false, false, 3, 1, 6);

        fillWithScaffold(level, box, ModBlocks.STEEL_SCAFFOLD_STEEL.get(), Direction.NORTH, false, 5, 3, 1, 8, 3, 1);
        fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.SOUTH, 5, 3, 2, 8, 3, 2);

        placeBlockAtCurrentPosition(level,
                ModBlocks.MACHINE_ELECTRIC_FURNACE.get().defaultBlockState()
                        .setValue(com.hbm.blocks.machine.MachineElectricFurnace.FACING, rotate(Direction.SOUTH)),
                5, 1, 1, box);
        placeBlockAtCurrentPosition(level,
                ModBlocks.MACHINE_MICROWAVE.get().defaultBlockState()
                        .setValue(com.hbm.blocks.machine.MachineMicrowave.FACING, rotate(Direction.SOUTH)),
                5, 2, 1, box);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_TITANIUM.get().defaultBlockState(), 6, 1, 1, box);
        placeBlockAtCurrentPosition(level, ModBlocks.MACHINE_SHREDDER.get().defaultBlockState(), 7, 1, 1, box);
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_TITANIUM.get().defaultBlockState(), 8, 1, 1, box);
        fillWithBlocks(level, box, 5, 1, 3, 8, 1, 3, ModBlocks.DECO_TITANIUM.get().defaultBlockState());

        if (!hasPlacedLoot[0]) {
            placeBlockAtCurrentPosition(level, ModBlocks.DECO_LOOT.get().defaultBlockState(), 6, 2, 3, box);
            applyLoot(level, box, 6, 2, 3, LootGenerator.LOOT_MEDICINE);
            hasPlacedLoot[0] = true;
        }

        placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 8, 1, 5, box);
        if (!hasPlacedLoot[1]) {
            hasPlacedLoot[1] = generateInvContents(level, box, rand, ModBlocks.CRATE_IRON.get(),
                    Direction.NORTH, 8, 1, 6, ItemPoolsLegacy.POOL_GENERIC, 8);
        }

        updateConnectedBlocks(level, box);
        return true;
    }
                                    }
