package com.hbm.datagen.worldgen.feature;

import com.hbm.datagen.worldgen.feature.config.SurfaceSpotConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class SurfaceSpotFeature extends Feature<SurfaceSpotConfiguration> {

    public SurfaceSpotFeature(com.mojang.serialization.Codec<SurfaceSpotConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SurfaceSpotConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        SurfaceSpotConfiguration cfg = context.config();

        int r = rand.nextInt(Math.max(1, cfg.radius() - 2)) + 2;

        for (int iX = origin.getX() - r; iX <= origin.getX() + r; iX++) {
            for (int iZ = origin.getZ() - r; iZ <= origin.getZ() + r; iZ++) {
                int dx = iX - origin.getX();
                int dz = iZ - origin.getZ();
                if (dx * dx + dz * dz > r * r) continue;

                for (int iY = origin.getY() - 2; iY <= origin.getY() + 2; iY++) {
                    BlockPos pos = new BlockPos(iX, iY, iZ);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) {
                        if (rand.nextFloat() < cfg.chance()) {
                            level.setBlock(pos, cfg.block().defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}