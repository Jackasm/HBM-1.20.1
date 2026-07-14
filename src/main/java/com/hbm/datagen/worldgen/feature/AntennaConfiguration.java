package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record AntennaConfiguration(int frequency) implements FeatureConfiguration {
    public static final Codec<AntennaConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("frequency").forGetter(AntennaConfiguration::frequency)
            ).apply(instance, AntennaConfiguration::new)
    );
}
