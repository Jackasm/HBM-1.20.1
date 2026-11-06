package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record GlyphidHiveConfiguration(boolean infected, boolean loot)
        implements FeatureConfiguration {
    public static final Codec<GlyphidHiveConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("infected").forGetter(GlyphidHiveConfiguration::infected),
                    Codec.BOOL.fieldOf("loot").forGetter(GlyphidHiveConfiguration::loot)
            ).apply(instance, GlyphidHiveConfiguration::new)
    );

    public static GlyphidHiveConfiguration small(boolean infected) {
        return new GlyphidHiveConfiguration(infected, true);
    }
}