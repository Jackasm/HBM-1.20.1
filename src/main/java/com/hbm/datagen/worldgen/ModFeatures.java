package com.hbm.datagen.worldgen;

import com.hbm.datagen.worldgen.feature.*;
import com.hbm.datagen.worldgen.structure.RuinConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.util.RefStrings.MODID;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);

    public static final RegistryObject<OilBubbleFeature> OIL_BUBBLE =
            FEATURES.register("oil_bubble", () -> new OilBubbleFeature(OilBubbleConfiguration.CODEC));
    public static final RegistryObject<OilBubbleFeature> GAS_BUBBLE =
            FEATURES.register("gas_bubble", () -> new OilBubbleFeature(OilBubbleConfiguration.CODEC));
    public static final RegistryObject<OilBubbleFeature> EXPLOSIVE_BUBBLE =
            FEATURES.register("explosive_bubble", () -> new OilBubbleFeature(OilBubbleConfiguration.CODEC));
    public static final RegistryObject<BroadcasterFeature> BROADCASTER =
            FEATURES.register("broadcaster", () -> new BroadcasterFeature(BroadcasterConfiguration.CODEC));
    public static final RegistryObject<LandmineFeature> LANDMINE =
            FEATURES.register("landmine", () -> new LandmineFeature(LandmineConfiguration.CODEC));
    public static final RegistryObject<CraterFeature> CRATER =
            FEATURES.register("crater", () -> new CraterFeature(CraterConfiguration.CODEC));
    public static final RegistryObject<AntennaFeature> ANTENNA =
            FEATURES.register("antenna", () -> new AntennaFeature(AntennaConfiguration.CODEC));
    public static final RegistryObject<RuinFeature> RUIN =
            FEATURES.register("ruin", () -> new RuinFeature(RuinConfiguration.CODEC));

    public static final RegistryObject<PlantFeature> PLANT =
            FEATURES.register("plant", () -> new PlantFeature(SimpleBlockConfiguration.CODEC));


    public static final RegistryObject<Feature<NoneFeatureConfiguration>> HUGE_MUSH =
            FEATURES.register("huge_mush",
            HugeMush::new);

    public static final RegistryObject<Feature<BedrockOreConfiguration>> BEDROCK_ORE = FEATURES.register("bedrock_ore",
            () -> new BedrockOreFeature(BedrockOreConfiguration.CODEC));


    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}