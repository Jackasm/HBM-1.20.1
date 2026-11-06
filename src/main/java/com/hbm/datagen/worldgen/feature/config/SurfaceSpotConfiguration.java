package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SurfaceSpotConfiguration(Block block, int radius, float chance) implements FeatureConfiguration {
    public static final Codec<SurfaceSpotConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(SurfaceSpotConfiguration::block),
                    Codec.INT.fieldOf("radius").forGetter(SurfaceSpotConfiguration::radius),
                    Codec.FLOAT.fieldOf("chance").forGetter(SurfaceSpotConfiguration::chance)
            ).apply(instance, SurfaceSpotConfiguration::new)
    );
}