package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.block.Block;

public record BroadcasterConfiguration(Block broadcasterBlock) implements FeatureConfiguration {
    public static final Codec<BroadcasterConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("broadcaster_block").forGetter(BroadcasterConfiguration::broadcasterBlock)
            ).apply(instance, BroadcasterConfiguration::new)
    );
}
