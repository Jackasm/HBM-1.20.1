package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SchistStratumConfiguration(
        double scale,
        int threshold,
        int centerY,
        int radius,
        int horizontalRadius
) implements FeatureConfiguration {
    public static final Codec<SchistStratumConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("scale").forGetter(SchistStratumConfiguration::scale),
                    Codec.INT.fieldOf("threshold").forGetter(SchistStratumConfiguration::threshold),
                    Codec.INT.fieldOf("center_y").forGetter(SchistStratumConfiguration::centerY),
                    Codec.INT.fieldOf("radius").forGetter(SchistStratumConfiguration::radius),
                    Codec.INT.fieldOf("horizontal_radius").forGetter(SchistStratumConfiguration::horizontalRadius)
            ).apply(instance, SchistStratumConfiguration::new)
    );

    public static SchistStratumConfiguration defaults() {
        return new SchistStratumConfiguration(0.01D, 5, 30, 8, 16);
    }
}