package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class CraterFeature extends Feature<CraterConfiguration> {

    public CraterFeature(Codec<CraterConfiguration> codec) {
        super(codec);
    }

    private double depthFunc(double x, double rad, double depth) {
        return -Math.pow(x, 2) / Math.pow(rad, 2) * depth + depth;
    }

    @Override
    public boolean place(FeaturePlaceContext<CraterConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        CraterConfiguration config = context.config();

        // Проверяем биом
        var biomeKey = level.getBiome(origin).unwrapKey().orElse(null);
        if (biomeKey != config.targetBiome()) {
            return false;
        }

        // Проверяем частоту (1 на frequency чанков)
        if (rand.nextInt(config.frequency()) != 0) {
            return false;
        }

        double radius = rand.nextInt(config.maxSize() - config.minSize()) + config.minSize();
        double depth = radius * 0.35D;

        for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
                int x = origin.getX() + bx;
                int z = origin.getZ() + bz;
                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = level.getBlockState(pos);

                // Ищем твёрдый блок или жидкость
                if (state.isSolid() || state.liquid()) {
                    double r = Math.sqrt(x * x + z * z);

                    if (r - rand.nextInt(3) <= radius) {
                        // Вырезаем до нужной глубины
                        int dep = (int) Mth.clamp(depthFunc(r, radius, depth), 0, y - 1);

                        for (int i = 0; i < dep; i++) {
                            level.setBlock(pos.below(i), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
                        }

                        // Заполняем обратно
                        dep = Math.min(3, y - 1);

                        if (r + rand.nextInt(3) <= radius / 3D) {
                            for (int i = 0; i < dep; i++) {
                                level.setBlock(pos.below(i), config.regolith().defaultBlockState(), 2);
                            }
                        } else {
                            for (int i = 0; i < dep; i++) {
                                level.setBlock(pos.below(i), config.rock().defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
