package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record BiomeCaveConfiguration(
        int yLevel,
        int threshold,
        int rangeMult,
        int maxRange
) implements FeatureConfiguration {
    public static final Codec<BiomeCaveConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("y_level").forGetter(BiomeCaveConfiguration::yLevel),
                    Codec.INT.fieldOf("threshold").forGetter(BiomeCaveConfiguration::threshold),
                    Codec.INT.fieldOf("range_mult").forGetter(BiomeCaveConfiguration::rangeMult),
                    Codec.INT.fieldOf("max_range").forGetter(BiomeCaveConfiguration::maxRange)
            ).apply(instance, BiomeCaveConfiguration::new)
    );

    public static BiomeCaveConfiguration defaults() {
        return new BiomeCaveConfiguration(30, 2, 3, 4);
    }
}