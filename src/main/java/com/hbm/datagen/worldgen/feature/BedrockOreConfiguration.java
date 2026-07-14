package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record BedrockOreConfiguration(int chance) implements FeatureConfiguration {
    public static final Codec<BedrockOreConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("chance").forGetter(BedrockOreConfiguration::chance)
            ).apply(instance, BedrockOreConfiguration::new)
    );
}