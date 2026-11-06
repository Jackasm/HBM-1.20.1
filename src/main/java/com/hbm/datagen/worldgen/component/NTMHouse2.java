package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.deco.BlockSteelGrate;
import com.hbm.itempool.ItemPoolsComponent;
import com.hbm.itempool.ItemPoolsLegacy;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Порт NTMHouse2 из CivilianFeatures 1.7.10 (двойной дом).
 */
public class NTMHouse2 extends NTMComponent {

    private static final Sandstone RANDOM_SANDSTONE = new Sandstone();
    private final boolean[] hasPlacedLoot = new boolean[2];

    public NTMHouse2() { }

    public NTMHouse2(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = CivilianFeatures.boxFromSize(minX, 64, minZ, 15, 5, 9, this.coordBaseMode);
    }

    @Override
    public String getTypeName() { return "NTMHouse2"; }

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

        placeFoundationUnderneath(level, Blocks.SANDSTONE.defaultBlockState(), 0, 0, 6, 15, -1, box);
        placeFoundationUnderneath(level, Blocks.SANDSTONE.defaultBlockState(), 9, 0, 15, 9, -1, box);

        fillWithAir(level, box, 1, 0, 1, 5, 5, 8);

        // ===== House 1 =====
        fillWithRandomizedBlocks(level, box, 0, 0, 0, 6, 1, 0, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 0, 2, 0, 1, 2, 0, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 2, 2, 0, box);
        fillWithRandomizedBlocks(level, box, 3, 2, 0, 3, 2, 0, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 4, 2, 0, box);
        fillWithRandomizedBlocks(level, box, 5, 2, 0, 6, 2, 0, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 0, 3, 0, 6, 3, 0, rand, RANDOM_SANDSTONE);

        fillWithRandomizedBlocks(level, box, 0, 0, 1, 0, 3, 9, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 1, 0, 9, 6, 1, 9, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 1, 2, 9, 1, 2, 9, rand, RANDOM_SANDSTONE);
        fillWithBlocks(level, box, 2, 2, 9, 4, 2, 9, Blocks.OAK_FENCE.defaultBlockState());
        fillWithRandomizedBlocks(level, box, 5, 2, 9, 6, 2, 9, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 1, 3, 9, 6, 3, 9, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 6, 0, 8, 6, 3, 8, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 6, 0, 7, 6, 0, 7, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 6, 3, 7, 6, 3, 7, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 6, 0, 1, 6, 3, 6, rand, RANDOM_SANDSTONE);

        // Floor + Ceiling
        fillWithBlocks(level, box, 1, 0, 1, 5, 0, 8, Blocks.SANDSTONE.defaultBlockState());
        fillWithBlocks(level, box, 1, 4, 0, 5, 4, 9, Blocks.SANDSTONE.defaultBlockState());
        fillWithBlocks(level, box, 0, 4, 0, 0, 4, 9, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB));
        fillWithBlocks(level, box, 6, 4, 0, 6, 4, 9, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB));
        fillWithBlocks(level, box, 2, 5, 0, 4, 5, 0, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB));
        fillWithBlocks(level, box, 3, 5, 1, 3, 5, 2, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB));
        fillWithBlocks(level, box, 3, 5, 4, 3, 5, 6, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB));
        placeBlockAtCurrentPosition(level, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB), 3, 5, 8, box);
        fillWithBlocks(level, box, 2, 5, 9, 4, 5, 9, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB));

        // ===== House 2 =====
        fillWithRandomizedBlocks(level, box, 9, 0, 0, 15, 0, 0, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 9, 1, 0, 13, 1, 0, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 9, 2, 0, 9, 2, 0, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB), 9, 2, 0, box);
        placeBlockAtCurrentPosition(level, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB), 12, 2, 0, box);

        fillWithRandomizedBlocks(level, box, 9, 0, 1, 9, 3, 1, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 9, 0, 2, 9, 0, 2, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 9, 3, 2, 9, 3, 8, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB), 9, 4, 2, box);
        fillWithBlocks(level, box, 9, 4, 4, 9, 4, 7, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB));
        fillWithRandomizedBlocks(level, box, 9, 0, 3, 9, 1, 9, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 9, 2, 3, 9, 2, 3, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, Blocks.OAK_FENCE.defaultBlockState(), 9, 2, 4, box);
        fillWithRandomizedBlocks(level, box, 9, 2, 5, 9, 2, 5, rand, RANDOM_SANDSTONE);
        fillWithBlocks(level, box, 9, 2, 6, 9, 2, 7, Blocks.OAK_FENCE.defaultBlockState());
        fillWithRandomizedBlocks(level, box, 9, 2, 8, 9, 2, 9, rand, RANDOM_SANDSTONE);

        fillWithRandomizedBlocks(level, box, 10, 0, 9, 15, 1, 9, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 10, 2, 9, 10, 2, 9, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 14, 2, 9, 15, 2, 9, rand, RANDOM_SANDSTONE);

        fillWithRandomizedBlocks(level, box, 15, 0, 1, 15, 0, 8, rand, RANDOM_SANDSTONE);
        fillWithRandomizedBlocks(level, box, 15, 1, 3, 15, 1, 3, rand, RANDOM_SANDSTONE);
        fillWithBlocks(level, box, 15, 1, 4, 15, 1, 5, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB));
        fillWithRandomizedBlocks(level, box, 15, 1, 6, 15, 1, 8, rand, RANDOM_SANDSTONE);
        placeBlockAtCurrentPosition(level, NTMHouse1.slabBottom(Blocks.SANDSTONE_SLAB), 15, 1, 8, box);

        fillWithBlocks(level, box, 10, 0, 1, 14, 0, 8, Blocks.SANDSTONE.defaultBlockState());

        // House 1
        placeBlockAtCurrentPosition(level, ModBlocks.MACHINE_HEAT_BOILER.get().defaultBlockState(), 1, 1, 1, box);
        fillWithBlocks(level, box, 1, 2, 1, 1, 3, 1, ModBlocks.DECO_PIPE_QUAD_RUSTED.get().defaultBlockState());
        placeBlockAtCurrentPosition(level, ModBlocks.DECO_PIPE_RIM_RUSTED.get().defaultBlockState(), 1, 5, 1, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE.get().defaultBlockState(), 2, 1, 3, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE_CAN.get().defaultBlockState(), 1, 1, 5, box);

        if (!hasPlacedLoot[0]) {
            generateInvContents(level, box, rand, Blocks.CHEST, Direction.SOUTH,
                    1, 1, 7, ItemPoolsComponent.POOL_MACHINE_PARTS, 10);
            hasPlacedLoot[0] = true;
        }
        fillWithBlocks(level, box, 4, 1, 8, 5, 1, 8, ModBlocks.CRATE.get().defaultBlockState());
        fillWithScaffold(level, box, ModBlocks.STEEL_SCAFFOLD_STEEL.get(), Direction.NORTH, true, 5, 1, 4, 5, 3, 4);
        fillWithScaffold(level, box, ModBlocks.STEEL_SCAFFOLD_STEEL.get(), Direction.NORTH, true, 5, 1, 6, 5, 3, 6);
        placeBlockAtCurrentPosition(level, ModBlocks.STEEL_GRATE.get().defaultBlockState().setValue(BlockSteelGrate.LAYER, 7), 5, 1, 5, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE_WEAPON.get().defaultBlockState(), 5, 2, 5, box);

        // House 2
        if (!hasPlacedLoot[1]) {
            generateInvContents(level, box, rand, Blocks.CHEST, Direction.SOUTH,
                    10, 1, 1, ItemPoolsLegacy.POOL_ANTENNA, 10);
            hasPlacedLoot[1] = true;
        }
        placeRandomBobble(level, box, rand, 10, 1, 4);

        randomlyFillWithBlocks(level, box, rand, 0.25F,
                11, 1, 1, 14, 1, 8, Blocks.SAND.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

        updateConnectedBlocks(level, box);
        return true;
    }
}
