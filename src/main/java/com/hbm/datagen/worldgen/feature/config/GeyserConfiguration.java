package com.hbm.datagen.worldgen.feature.config;

import com.hbm.blocks.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record GeyserConfiguration(Block geyserBlock) implements FeatureConfiguration {
    public static final Codec<GeyserConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("geyser_block")
                            .forGetter(GeyserConfiguration::geyserBlock)
            ).apply(instance, GeyserConfiguration::new)
    );

    public static GeyserConfiguration water() {
        return new GeyserConfiguration(ModBlocks.GEYSIR_WATER.get());
    }
    public static GeyserConfiguration chlorine() {
        return new GeyserConfiguration(ModBlocks.GEYSIR_CHLORINE.get());
    }
    public static GeyserConfiguration vapor() {
        return new GeyserConfiguration(ModBlocks.GEYSIR_VAPOR.get());
    }
    public static GeyserConfiguration nether() {
        return new GeyserConfiguration(ModBlocks.GEYSIR_NETHER.get());
    }
}