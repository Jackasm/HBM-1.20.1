package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockCrashedBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DudFeature extends Feature<NoneFeatureConfiguration> {

    public DudFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        BlockPos below = origin.below();
        BlockState belowState = level.getBlockState(below);

        // Проверка: под структурой solid
        if (!(belowState.is(BlockTags.DIRT)
                || belowState.is(BlockTags.BASE_STONE_OVERWORLD)
                || belowState.is(BlockTags.SAND))) {
            return false;
        }

        // Проверка: сам блок air
        if (!level.getBlockState(origin).isAir()) return false;

        // Случайный тип
        BlockCrashedBomb.EnumDudType[] types = BlockCrashedBomb.EnumDudType.values();
        BlockCrashedBomb.EnumDudType chosen = types[rand.nextInt(types.length)];

        BlockState state = ModBlocks.CRASHED_BALEFIRE.get()
                .defaultBlockState()
                .setValue(BlockCrashedBomb.TYPE, chosen);

        level.setBlock(origin, state, 3);

        return true;
    }
}