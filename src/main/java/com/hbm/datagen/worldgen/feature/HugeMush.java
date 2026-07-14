package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class HugeMush extends Feature<NoneFeatureConfiguration> {

    private static final BlockState MUSH_BLOCK = ModBlocks.MUSH_BLOCK.get().defaultBlockState();
    private static final BlockState MUSH_STEM = ModBlocks.MUSH_BLOCK_STEM.get().defaultBlockState();

    public HugeMush() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        // Слой 0 (основание) - 3x3
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                setBlock(level, x + i, y, z + j, MUSH_BLOCK);
            }
        }

        // Слой 3 - 3x3
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                setBlock(level, x + i, y + 3, z + j, MUSH_BLOCK);
            }
        }

        // Слой 5 - 5x5
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                setBlock(level, x + i, y + 5, z + j, MUSH_BLOCK);
            }
        }

        // Слои 6-8 - 9x9
        for (int i = -4; i <= 4; i++) {
            for (int j = -4; j <= 4; j++) {
                for (int k = 0; k <= 2; k++) {
                    setBlock(level, x + i, y + 6 + k, z + j, MUSH_BLOCK);
                }
            }
        }

        // Слой 9 - 7x7
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                setBlock(level, x + i, y + 9, z + j, MUSH_BLOCK);
            }
        }

        // Слой 10 - 3x3
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                setBlock(level, x + i, y + 10, z + j, MUSH_BLOCK);
            }
        }

        // Ствол - от y до y+7
        for (int i = 0; i <= 7; i++) {
            setBlock(level, x, y + i, z, MUSH_STEM);
        }

        return true;
    }

    private void setBlock(WorldGenLevel level, int x, int y, int z, BlockState state) {
        BlockPos pos = new BlockPos(x, y, z);
        if (level.getBlockState(pos).isAir()) {
            level.setBlock(pos, state, 2);
        }
    }
}
