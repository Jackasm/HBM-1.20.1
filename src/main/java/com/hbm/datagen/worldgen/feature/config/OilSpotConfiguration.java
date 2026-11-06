package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record OilSpotConfiguration(int width, int count, boolean addWillows) implements FeatureConfiguration {
    public static final Codec<OilSpotConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("width").forGetter(OilSpotConfiguration::width),
                    Codec.INT.fieldOf("count").forGetter(OilSpotConfiguration::count),
                    Codec.BOOL.fieldOf("add_willows").forGetter(OilSpotConfiguration::addWillows)
            ).apply(instance, OilSpotConfiguration::new)
    );
}