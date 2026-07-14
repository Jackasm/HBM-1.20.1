package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.block.state.BlockState;

public record OilBubbleConfiguration(RuleTest target, BlockState toPlace, int minRadius, int maxRadius, boolean fuzzy) implements FeatureConfiguration {
    public static final Codec<OilBubbleConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RuleTest.CODEC.fieldOf("target").forGetter(OilBubbleConfiguration::target),
            BlockState.CODEC.fieldOf("toPlace").forGetter(OilBubbleConfiguration::toPlace),
            Codec.INT.fieldOf("minRadius").forGetter(OilBubbleConfiguration::minRadius),
            Codec.INT.fieldOf("maxRadius").forGetter(OilBubbleConfiguration::maxRadius),
            Codec.BOOL.fieldOf("fuzzy").forGetter(OilBubbleConfiguration::fuzzy)
    ).apply(instance, OilBubbleConfiguration::new));
}
