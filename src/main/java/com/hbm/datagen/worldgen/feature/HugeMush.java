package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
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

        // Проверка: под структурой solid
        if (!level.getBlockState(origin.below()).isSolid()) return false;

        // Проверка: 11 блоков вверх свободно
        for (int dy = 0; dy <= 10; dy++) {
            BlockState s = level.getBlockState(origin.above(dy));
            if (!s.isAir() && !s.canBeReplaced()) return false;
        }

        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        // Ствол — ПЕРВЫМ (чтобы слои шляпки его не перекрыли)
        for (int i = 0; i <= 10; i++) {
            BlockPos pos = new BlockPos(x, y + i, z);
            BlockState existing = level.getBlockState(pos);
            if (existing.isAir() || existing.canBeReplaced()) {
                level.setBlock(pos, MUSH_STEM, 2);
            }
        }

        // Слой 0 (основание) - 3x3
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                setBlock(level, x + i, y, z + j, MUSH_BLOCK);

        // Слой 3 - 3x3
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                setBlock(level, x + i, y + 3, z + j, MUSH_BLOCK);

        // Слой 5 - 5x5
        for (int i = -2; i <= 2; i++)
            for (int j = -2; j <= 2; j++)
                setBlock(level, x + i, y + 5, z + j, MUSH_BLOCK);

        // Слои 6-8 - 9x9
        for (int i = -4; i <= 4; i++)
            for (int j = -4; j <= 4; j++)
                for (int k = 0; k <= 2; k++)
                    setBlock(level, x + i, y + 6 + k, z + j, MUSH_BLOCK);

        // Слой 9 - 7x7
        for (int i = -3; i <= 3; i++)
            for (int j = -3; j <= 3; j++)
                setBlock(level, x + i, y + 9, z + j, MUSH_BLOCK);

        // Слой 10 - 3x3
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                setBlock(level, x + i, y + 10, z + j, MUSH_BLOCK);

        return true;
    }

    private void setBlock(WorldGenLevel level, int x, int y, int z, BlockState state) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState existing = level.getBlockState(pos);
        if (existing.isAir() || existing.canBeReplaced()) {
            level.setBlock(pos, state, 2);
        }
    }
}