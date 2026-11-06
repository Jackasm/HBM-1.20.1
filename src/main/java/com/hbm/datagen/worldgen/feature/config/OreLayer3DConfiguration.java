package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record OreLayer3DConfiguration(BlockState state, double scaleH, double scaleV, double threshold, int id)
        implements FeatureConfiguration {

    public static final Codec<OreLayer3DConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("state").forGetter(OreLayer3DConfiguration::state),
                    Codec.DOUBLE.fieldOf("scale_h").forGetter(OreLayer3DConfiguration::scaleH),
                    Codec.DOUBLE.fieldOf("scale_v").forGetter(OreLayer3DConfiguration::scaleV),
                    Codec.DOUBLE.fieldOf("threshold").forGetter(OreLayer3DConfiguration::threshold),
                    Codec.INT.fieldOf("id").forGetter(OreLayer3DConfiguration::id)
            ).apply(instance, OreLayer3DConfiguration::new)
    );
}
