package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class StoneKeyholeFeature extends Feature<NoneFeatureConfiguration> {

    public StoneKeyholeFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        int x = origin.getX();
        int z = origin.getZ();
        int y = 6 + rand.nextInt(13);

        BlockPos pos = new BlockPos(x, y, z);

        if (level.getBlockState(pos).is(Blocks.STONE)) {
            level.setBlock(pos, ModBlocks.STONE_KEYHOLE.get().defaultBlockState(), 2);
            return true;
        }

        return false;
    }
}