package com.hbm.datagen.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record MeteoriteConfiguration(
        boolean safe,
        boolean allowSpecials,
        boolean damagingImpact,
        boolean findSurface
) implements FeatureConfiguration {
    public static final Codec<MeteoriteConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("safe").forGetter(MeteoriteConfiguration::safe),
                    Codec.BOOL.fieldOf("allow_specials").forGetter(MeteoriteConfiguration::allowSpecials),
                    Codec.BOOL.fieldOf("damaging_impact").forGetter(MeteoriteConfiguration::damagingImpact),
                    Codec.BOOL.fieldOf("find_surface").forGetter(MeteoriteConfiguration::findSurface)
            ).apply(instance, MeteoriteConfiguration::new)
    );

    public static MeteoriteConfiguration staticMeteorite() {
        return new MeteoriteConfiguration(false, false, false, true);
    }

    public static MeteoriteConfiguration impact() {
        return new MeteoriteConfiguration(false, true, true, false);
    }
}