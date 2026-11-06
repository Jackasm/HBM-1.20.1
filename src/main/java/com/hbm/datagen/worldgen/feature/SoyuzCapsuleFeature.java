package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.tileentity.storage.TileEntitySoyuzCapsule;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SoyuzCapsuleFeature extends Feature<NoneFeatureConfiguration> {

    public SoyuzCapsuleFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        if (!level.getBiome(origin).is(BiomeTags.IS_BEACH)) return false;

        int x = origin.getX();
        int z = origin.getZ();
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 4;

        if (y < level.getMinBuildHeight() + 1) return false;

        BlockPos pos = new BlockPos(x, y, z);

        if (!level.getBlockState(pos.below()).isSolid()) return false;

        level.setBlock(pos, ModBlocks.SOYUZ_CAPSULE.get().defaultBlockState(), 2);

        if (level.getBlockEntity(pos) instanceof TileEntitySoyuzCapsule capsule) {
            int slot = rand.nextInt(capsule.getContainerSize());
            capsule.setItem(slot, new ItemStack(ModItems.RECORD_GLASS.get()));
            capsule.setChanged();
        }

        return true;
    }
}