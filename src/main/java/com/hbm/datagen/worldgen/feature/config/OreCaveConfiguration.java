package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record OreCaveConfiguration(
        BlockState ore,
        BlockState fluid,           // ORE_OIL или AIR (если не нужен)
        BlockState stalactite,
        BlockState stalagmite,
        int yLevel,
        int range,
        int threshold,
        double scale
) implements FeatureConfiguration {
    public static final Codec<OreCaveConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("ore").forGetter(OreCaveConfiguration::ore),
                    BlockState.CODEC.optionalFieldOf("fluid",
                                    net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                            .forGetter(OreCaveConfiguration::fluid),
                    BlockState.CODEC.fieldOf("stalactite").forGetter(OreCaveConfiguration::stalactite),
                    BlockState.CODEC.fieldOf("stalagmite").forGetter(OreCaveConfiguration::stalagmite),
                    Codec.INT.fieldOf("y_level").forGetter(OreCaveConfiguration::yLevel),
                    Codec.INT.fieldOf("range").forGetter(OreCaveConfiguration::range),
                    Codec.INT.fieldOf("threshold").forGetter(OreCaveConfiguration::threshold),
                    Codec.DOUBLE.optionalFieldOf("scale", 0.01).forGetter(OreCaveConfiguration::scale)
            ).apply(instance, OreCaveConfiguration::new)
    );
}