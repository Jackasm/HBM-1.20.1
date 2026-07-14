package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record CraterConfiguration(int frequency, int minSize, int maxSize, Block regolith, Block rock, ResourceKey<Biome> targetBiome) implements FeatureConfiguration {
    public static final Codec<CraterConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("frequency").forGetter(CraterConfiguration::frequency),
                    Codec.INT.fieldOf("minSize").forGetter(CraterConfiguration::minSize),
                    Codec.INT.fieldOf("maxSize").forGetter(CraterConfiguration::maxSize),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("regolith").forGetter(CraterConfiguration::regolith),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("rock").forGetter(CraterConfiguration::rock),
                    ResourceKey.codec(Registries.BIOME).fieldOf("targetBiome").forGetter(CraterConfiguration::targetBiome)
            ).apply(instance, CraterConfiguration::new)
    );
}
