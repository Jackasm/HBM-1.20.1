package com.hbm.datagen.worldgen.feature;

import com.hbm.datagen.worldgen.feature.config.DepthDepositConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class DepthDepositFeature extends Feature<DepthDepositConfiguration> {

    public DepthDepositFeature(com.mojang.serialization.Codec<DepthDepositConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<DepthDepositConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        DepthDepositConfiguration cfg = context.config();

        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();
        int size = cfg.size();
        double fill = cfg.fill();

        BlockState ore = cfg.ore();
        BlockState filler = cfg.filler();
        BlockState target = cfg.target();

        for (int i = x - size; i <= x + size; i++) {
            for (int j = y - size; j <= y + size; j++) {
                for (int k = z - size; k <= z + size; k++) {
                    if (j < level.getMinBuildHeight() || j >= level.getMaxBuildHeight()) continue;

                    BlockPos pos = new BlockPos(i, j, k);
                    BlockState current = level.getBlockState(pos);

                    // Проверка: заменяем target или bedrock (если разрешено)
                    boolean canReplace = current.is(target.getBlock())
                            || (cfg.replaceBedrock() && current.is(Blocks.BEDROCK));
                    if (!canReplace) continue;

                    // Расстояние от центра
                    double dx = x - i;
                    double dy = y - j;
                    double dz = z - k;
                    double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    if (len + rand.nextInt(2) < size * fill) {
                        level.setBlock(pos, ore, 2);
                    } else if (len + rand.nextInt(2) <= size) {
                        level.setBlock(pos, filler, 2);
                    }
                }
            }
        }
        return true;
    }
}