package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record DepthDepositConfiguration(
        BlockState ore,
        BlockState filler,
        BlockState target,
        int size,
        double fill,
        boolean replaceBedrock
) implements FeatureConfiguration {
    public static final Codec<DepthDepositConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("ore").forGetter(DepthDepositConfiguration::ore),
                    BlockState.CODEC.fieldOf("filler").forGetter(DepthDepositConfiguration::filler),
                    BlockState.CODEC.fieldOf("target").forGetter(DepthDepositConfiguration::target),
                    Codec.INT.fieldOf("size").forGetter(DepthDepositConfiguration::size),
                    Codec.DOUBLE.fieldOf("fill").forGetter(DepthDepositConfiguration::fill),
                    Codec.BOOL.optionalFieldOf("replace_bedrock", false)
                            .forGetter(DepthDepositConfiguration::replaceBedrock)
            ).apply(instance, DepthDepositConfiguration::new)
    );

    public static DepthDepositConfiguration overworld(BlockState ore, BlockState filler, int size, double fill) {
        return new DepthDepositConfiguration(ore, filler,
                net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
                size, fill, true);
    }

    public static DepthDepositConfiguration nether(BlockState ore, BlockState filler, int size, double fill) {
        return new DepthDepositConfiguration(ore, filler,
                net.minecraft.world.level.block.Blocks.NETHERRACK.defaultBlockState(),
                size, fill, true);
    }
}