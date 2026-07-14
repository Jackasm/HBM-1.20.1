package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.generic.BlockTallPlant;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class PlantFeature extends Feature<SimpleBlockConfiguration> {

    public PlantFeature(Codec<SimpleBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        BlockState state = context.config().toPlace().getState(context.random(), pos);

        // Проверяем, что под блоком НЕ вода
        BlockState below = level.getBlockState(pos.below());
        if (below.getFluidState().isSource() || below.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER)) {
            return false;
        }

        // Для высоких растений (двублочных) ставим два блока
        if (state.getBlock() instanceof BlockTallPlant) {
            // Проверяем место для двух блоков
            if (!level.getBlockState(pos).canBeReplaced() || !level.getBlockState(pos.above()).canBeReplaced()) {
                return false;
            }
            // Ставим нижнюю часть (LOWER)
            BlockState lowerState = state.setValue(BlockTallPlant.HALF, DoubleBlockHalf.LOWER);
            level.setBlock(pos, lowerState, 2);
            // Ставим верхнюю часть (UPPER)
            BlockState upperState = state.setValue(BlockTallPlant.HALF, DoubleBlockHalf.UPPER);
            level.setBlock(pos.above(), upperState, 2);
            return true;
        }

        // Обычные растения
        if (level.getBlockState(pos).canBeReplaced()) {
            level.setBlock(pos, state, 2);
            return true;
        }
        return false;
    }
}