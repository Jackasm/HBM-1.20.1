package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class OilBubbleFeature extends Feature<OilBubbleConfiguration> {
    public OilBubbleFeature(Codec<OilBubbleConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<OilBubbleConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos center = context.origin();
        RandomSource random = context.random();
        OilBubbleConfiguration config = context.config();

        int radius = random.nextInt(config.maxRadius() - config.minRadius() + 1) + config.minRadius();
        double radiusSqr = (radius * radius) / 2.0;

        int xMin = center.getX() - radius;
        int xMax = center.getX() + radius;
        int zMin = center.getZ() - radius;
        int zMax = center.getZ() + radius;
        int yMin = center.getY() - radius;
        int yMax = center.getY() + radius;

        // Оптимизация: ограничиваем Y диапазон допустимыми значениями
        int yMinClamped = Math.max(level.getMinBuildHeight(), yMin);
        int yMaxClamped = Math.min(level.getMaxBuildHeight() - 1, yMax);

        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                for (int y = yMinClamped; y <= yMaxClamped; y++) {
                    double dx = x - center.getX();
                    double dz = z - center.getZ();
                    double dy = y - center.getY();
                    double rSqr = dx * dx + dz * dz + dy * dy * 3.0;

                    if (config.fuzzy()) {
                        rSqr -= random.nextDouble() * radiusSqr / 3.0;
                    }

                    if (rSqr < radiusSqr) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (config.target().test(level.getBlockState(pos), random)) {
                            level.setBlock(pos, config.toPlace(), 2);
                        }
                    }
                }
            }
        }
        return true;
    }
}
