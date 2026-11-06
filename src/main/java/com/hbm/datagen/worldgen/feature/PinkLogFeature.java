package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PinkLogFeature extends Feature<NoneFeatureConfiguration> {

    public PinkLogFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        int x = origin.getX();
        int z = origin.getZ();
        boolean changed = false;

        // В 1.7.10 проходили по всем y от 0 до 256
        for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);

            // oak_log → pink_log
            if (state.is(Blocks.OAK_LOG)) {
                level.setBlock(pos, ModBlocks.PINK_LOG.get().defaultBlockState(), 2);
                changed = true;
            }
        }

        return changed;
    }
}