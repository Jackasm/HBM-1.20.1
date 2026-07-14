package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.block.Block;

public record LandmineConfiguration(Block mineBlock) implements FeatureConfiguration {
    public static final Codec<LandmineConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("mine_block").forGetter(LandmineConfiguration::mineBlock)
            ).apply(instance, LandmineConfiguration::new)
    );
}
