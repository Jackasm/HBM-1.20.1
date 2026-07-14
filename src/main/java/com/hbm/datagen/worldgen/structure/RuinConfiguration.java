package com.hbm.datagen.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class RuinConfiguration implements FeatureConfiguration {
    public static final Codec<RuinConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("index").forGetter(config -> config.index)
            ).apply(instance, RuinConfiguration::new)
    );

    public final int index;

    public RuinConfiguration(int index) {
        this.index = index;
    }
}