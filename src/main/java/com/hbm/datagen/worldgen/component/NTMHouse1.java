package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsLegacy;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Порт NTMHouse1 (Sandstone Ruin 1) из CivilianFeatures 1.7.10.
 */
public class NTMHouse1 extends NTMComponent {

    private static final Sandstone RANDOM_SANDSTONE = new Sandstone();
    private boolean hasPlacedChest;

    public NTMHouse1() { }

    public NTMHouse1(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = CivilianFeatures.boxFromSize(minX, 64, minZ, 9, 4, 6, this.coordBaseMode);
        this.hasPlacedChest = false;
    }

    @Override
    public String getTypeName() { return "NTMHouse1"; }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putBoolean("hasChest", this.hasPlacedChest);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        this.hasPlacedChest = nbt.getBoolean("hasChest");
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        if (!heightCalculated) {
            if (!setAverageHeight(level, box, this.boundingBox.minY())) return false;
            heightCalculated = true;
        }

        placeFoundationUnderneath(level, Blocks.SANDSTONE.defaultBlockState(), 0, 0, 9, 6, -1, box);

        // Back Wall
        fillWithRandomizedBlocks(level, box, 0, 0, 0, 9, 0, 0, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 0, 1, 0, 1, 1, 0, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 2, 1, 0, box);
        fillWithRandomizedBlocks(level, box, 3, 1, 0, 5, 1, 0, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 6, 1, 0, box);
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 7, 1, 0, box);
        fillWithRandomizedBlocks(level, box, 8, 1, 0, 9, 1, 0, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 0, 2, 0, 7, 2, 0, rand, RANDOM_SANDSTONE);

        // Left Wall
        fillWithRandomizedBlocks(level, box, 0, 0, 0, 0, 1, 6, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, slabBottom(Blocks.SANDSTONE_SLAB), 0, 2, 1, box);
        fillWithBlocks(level, box, 0, 2, 3, 0, 2, 6, slabBottom(Blocks.SANDSTONE_SLAB));

        // Front Wall
        fillWithRandomizedBlocks(level, box, 1, 0, 6, 1, 1, 6, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 3, 0, 6, 9, 1, 6, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 1, 2, 6, 3, 2, 6, rand, RANDOM_SANDSTONE);
        fillWithBlocks(level, box, 4, 2, 6, 5, 2, 6, slabBottom(Blocks.SANDSTONE_SLAB));
        placeBlockAtCurrentPosition(level, slabBottom(Blocks.SANDSTONE_SLAB), 7, 2, 6, box);

        // Right Wall
        fillWithRandomizedBlocks(level, box, 9, 0, 0, 9, 0, 6, rand, RANDOM_SANDSTONE);
        randomlyFillWithBlocks(level, box, rand, 0.65F,
                9, 1, 1, 9, 1, 5, Blocks.SAND.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

        // Interior
        fillWithRandomizedBlocks(level, box, 4, 0, 1, 4, 1, 3, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, ModBlocks.REINFORCED_SAND.get().defaultBlockState(), 4, 0, 4, box);

        // Loot / Sand
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE_WEAPON.get().defaultBlockState(), 1, 0, 1, box);
        if (!hasPlacedChest) {
            hasPlacedChest = generateInvContents(level, box, rand, Blocks.CHEST, Direction.SOUTH,
                    3, 0, 1, ItemPoolsLegacy.POOL_GENERIC, rand.nextInt(2) + 8);
        }
        fillWithBlocks(level, box, 5, 0, 1, 6, 0, 1, ModBlocks.CRATE.get().defaultBlockState());
        placeBlockAtCurrentPosition(level, Blocks.SAND.defaultBlockState(), 7, 0, 1, box);
        if (rand.nextFloat() <= 0.25F) {
            placeBlockAtCurrentPosition(level, ModBlocks.CRATE_METAL.get().defaultBlockState(), 8, 0, 1, box);
        }
        randomlyFillWithBlocks(level, box, rand, 0.25F,
                1, 0, 2, 3, 0, 5, Blocks.SAND.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        randomlyFillWithBlocks(level, box, rand, 0.25F,
                5, 0, 2, 8, 0, 5, Blocks.SAND.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

        updateConnectedBlocks(level, box);
        return true;
    }

    static BlockState slabBottom(net.minecraft.world.level.block.Block b) {
        return b.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
    }
}
