package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * 1.7.10: австралиум спавнился только в квадрате X/Z [-450..-350], Y 15..29,
 * по 0..3 жилы на чанк, размер жилы 50.
 */
public class AustraliumFeature extends Feature<NoneFeatureConfiguration> {

    public AustraliumFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        if (origin.getX() < -450 || origin.getX() > -350) return false;
        if (origin.getZ() < -450 || origin.getZ() > -350) return false;

        int veins = rand.nextInt(4);
        if (veins == 0) return false;

        BlockState ore = com.hbm.blocks.ModBlocks.ORE_AUSTRALIUM.get().defaultBlockState();
        for (int i = 0; i < veins; i++) {
            int x = origin.getX() + rand.nextInt(16);
            int y = 15 + rand.nextInt(15);
            int z = origin.getZ() + rand.nextInt(16);
            placeBlob(level, rand, new BlockPos(x, y, z), ore, 50);
        }
        return true;
    }

    static void placeBlob(WorldGenLevel level, RandomSource rand, BlockPos center, BlockState ore, int size) {
        for (int i = 0; i < size; i++) {
            BlockPos pos = center.offset(rand.nextInt(3) - 1, rand.nextInt(3) - 1, rand.nextInt(3) - 1);
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE) || state.is(Blocks.TUFF)) {
                level.setBlock(pos, ore, 2);
            }
        }
    }
}
