package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

/**
 * 1.7.10: крупное месторождение колтана вокруг одной случайной точки мира.
 * Центр зависит от seed (gaussian * 1500), радиус 750, 2 прохода по 5 колец.
 */
public class ColtanDepositFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MAX_RANGE = 750;

    public ColtanDepositFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!GeneralConfig.enable528ColtanDepositValue) return false;

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        Random seedRand = new Random(level.getLevel().getSeed() + 5);
        int centerX = (int) (seedRand.nextGaussian() * 1500);
        int centerZ = (int) (seedRand.nextGaussian() * 1500);

        if (Math.abs(origin.getX() - centerX) > MAX_RANGE) return false;
        if (Math.abs(origin.getZ() - centerZ) > MAX_RANGE) return false;

        BlockState ore = ModBlocks.ORE_COLTAN.get().defaultBlockState();
        boolean placed = false;

        for (int pass = 0; pass < 2; pass++) {
            for (int ring = 1; ring <= 5; ring++) {
                int range = MAX_RANGE / ring;
                int x = origin.getX() + rand.nextInt(16);
                int y = 15 + rand.nextInt(25);
                int z = origin.getZ() + rand.nextInt(16);

                if (Math.abs(x - centerX) > range || Math.abs(z - centerZ) > range) continue;
                AustraliumFeature.placeBlob(level, rand, new BlockPos(x, y, z), ore, 4);
                placed = true;
            }
        }

        return placed;
    }
}
