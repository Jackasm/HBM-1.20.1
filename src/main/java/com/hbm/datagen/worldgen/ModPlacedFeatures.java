package com.hbm.datagen.worldgen;


import com.hbm.config.GeneralConfig;
import com.hbm.config.WorldConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class ModPlacedFeatures {
    // === ОБЫЧНЫЕ РУДЫ ===
    public static final ResourceKey<PlacedFeature> ALUMINIUM_ORE_PLACED = registerKey("aluminium_ore_placed");
    public static final ResourceKey<PlacedFeature> ASBESTOS_ORE_PLACED = registerKey("asbestos_ore_placed");
    public static final ResourceKey<PlacedFeature> BERYLLIUM_ORE_PLACED = registerKey("beryllium_ore_placed");
    public static final ResourceKey<PlacedFeature> CINNABAR_ORE_PLACED = registerKey("cinnabar_ore_placed");
    public static final ResourceKey<PlacedFeature> COBALT_ORE_PLACED = registerKey("cobalt_ore_placed");
    public static final ResourceKey<PlacedFeature> FLUORITE_ORE_PLACED = registerKey("fluorite_ore_placed");
    public static final ResourceKey<PlacedFeature> LEAD_ORE_PLACED = registerKey("lead_ore_placed");
    public static final ResourceKey<PlacedFeature> LIGNITE_ORE_PLACED = registerKey("lignite_ore_placed");
    public static final ResourceKey<PlacedFeature> TITANIUM_ORE_PLACED = registerKey("titanium_ore_placed");
    public static final ResourceKey<PlacedFeature> NITER_ORE_PLACED = registerKey("niter_ore_placed");
    public static final ResourceKey<PlacedFeature> RARE_ORE_PLACED = registerKey("rare_ore_placed");
    public static final ResourceKey<PlacedFeature> SULFUR_ORE_PLACED = registerKey("sulfur_ore_placed");
    public static final ResourceKey<PlacedFeature> TUNGSTEN_ORE_PLACED = registerKey("tungsten_ore_placed");
    public static final ResourceKey<PlacedFeature> ZINC_ORE_PLACED = registerKey("zinc_ore_placed");
    public static final ResourceKey<PlacedFeature> THORIUM_ORE_PLACED = registerKey("thorium_ore_placed");
    public static final ResourceKey<PlacedFeature> URANIUM_ORE_PLACED = registerKey("uranium_ore_placed");
    public static final ResourceKey<PlacedFeature> STONE_RESOURCE_LIMESTONE_PLACED = registerKey("stone_resource_limestone");
    public static final ResourceKey<PlacedFeature> ALEXANDRITE_ORE_PLACED = registerKey("alexandrite_ore_placed");

    // === DEEPSLATE РУДЫ ===
    public static final ResourceKey<PlacedFeature> ALUMINIUM_DEEPSLATE_ORE_PLACED = registerKey("aluminium_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> ASBESTOS_DEEPSLATE_ORE_PLACED = registerKey("asbestos_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> BERYLLIUM_DEEPSLATE_ORE_PLACED = registerKey("beryllium_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> CINNABAR_DEEPSLATE_ORE_PLACED = registerKey("cinnabar_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> COBALT_DEEPSLATE_ORE_PLACED = registerKey("cobalt_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> FLUORITE_DEEPSLATE_ORE_PLACED = registerKey("fluorite_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> LEAD_DEEPSLATE_ORE_PLACED = registerKey("lead_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> LIGNITE_DEEPSLATE_ORE_PLACED = registerKey("lignite_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> TITANIUM_DEEPSLATE_ORE_PLACED = registerKey("titanium_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> NITER_DEEPSLATE_ORE_PLACED = registerKey("niter_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> RARE_DEEPSLATE_ORE_PLACED = registerKey("rare_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> SULFUR_DEEPSLATE_ORE_PLACED = registerKey("sulfur_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> TUNGSTEN_DEEPSLATE_ORE_PLACED = registerKey("tungsten_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> ZINC_DEEPSLATE_ORE_PLACED = registerKey("zinc_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> THORIUM_DEEPSLATE_ORE_PLACED = registerKey("thorium_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> URANIUM_DEEPSLATE_ORE_PLACED = registerKey("uranium_deepslate_ore_placed");

    public static final ResourceKey<PlacedFeature> BEDROCK_ORE_PLACED = registerKey("bedrock_ore_placed");

    public static final ResourceKey<PlacedFeature> IRON_CLUSTER_PLACED = registerKey("iron_cluster_placed");
    public static final ResourceKey<PlacedFeature> TITANIUM_CLUSTER_PLACED = registerKey("titanium_cluster_placed");
    public static final ResourceKey<PlacedFeature> ALUMINIUM_CLUSTER_PLACED = registerKey("aluminium_cluster_placed");
    public static final ResourceKey<PlacedFeature> COPPER_CLUSTER_PLACED = registerKey("copper_cluster_placed");

    public static final ResourceKey<PlacedFeature> OIL_BUBBLE_PLACED = registerKey("oil_bubble_placed");

    public static final ResourceKey<PlacedFeature> GNEISS_GAS_ORE_PLACED = registerKey("gneiss_gas_ore_placed");
    public static final ResourceKey<PlacedFeature> GAS_BUBBLE_PLACED = registerKey("gas_bubble_placed");
    public static final ResourceKey<PlacedFeature> EXPLOSIVE_BUBBLE_PLACED = registerKey("explosive_bubble_placed");

    public static final ResourceKey<PlacedFeature> BROADCASTER_PLACED = registerKey("broadcaster_placed");
    public static final ResourceKey<PlacedFeature> LANDMINE_PLACED = registerKey("landmine_placed");
    public static final ResourceKey<PlacedFeature> CRATER_PLACED = registerKey("crater_placed");
    public static final ResourceKey<PlacedFeature> ANTENNA_PLACED = registerKey("antenna_placed");
    public static final ResourceKey<PlacedFeature> RUIN_PLACED = registerKey("ruin_placed");


    public static final ResourceKey<PlacedFeature> FLOWER_FOXGLOVE_PLACED = registerKey("flower_foxglove_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_NIGHTSHADE_PLACED = registerKey("flower_nightshade_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_TOBACCO_PLACED = registerKey("flower_tobacco_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_WEED_PLACED = registerKey("flower_weed_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_CD0_PLACED = registerKey("flower_cd0_placed");
    public static final ResourceKey<PlacedFeature> TALL_WEED_PLACED = registerKey("tall_weed_placed");
    public static final ResourceKey<PlacedFeature> TALL_CD2_PLACED = registerKey("tall_cd2_placed");
    public static final ResourceKey<PlacedFeature> DEAD_PLANT_GENERIC_PLACED = registerKey("dead_plant_generic_placed");

    // === НЕТЕР РУДЫ ===
    public static final ResourceKey<PlacedFeature> NETHER_URANIUM_ORE_PLACED = registerKey("nether_uranium_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_TUNGSTEN_ORE_PLACED = registerKey("nether_tungsten_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_SULFUR_ORE_PLACED = registerKey("nether_sulfur_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_PHOSPHORUS_ORE_PLACED = registerKey("nether_phosphorus_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_COAL_ORE_PLACED = registerKey("nether_coal_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_COBALT_ORE_PLACED = registerKey("nether_cobalt_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_PLUTONIUM_ORE_PLACED = registerKey("nether_plutonium_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_SMOLDERING_ORE_PLACED = registerKey("nether_smoldering_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_GEYSER_PLACED = registerKey("nether_geyser_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // === РЕГИСТРАЦИЯ ОБЫЧНЫХ РУД ===
        registerOre(context, configuredFeatures, ALUMINIUM_ORE_PLACED, ModConfiguredFeatures.ALUMINIUM_ORE,
                WorldConfig.aluminiumSpawn, WorldConfig.aluminiumMinHeight, WorldConfig.aluminiumMaxHeight,
                WorldConfig.ALUMINIUM_SPAWN, WorldConfig.ALUMINIUM_MIN, WorldConfig.ALUMINIUM_MAX);

        registerOre(context, configuredFeatures, ASBESTOS_ORE_PLACED, ModConfiguredFeatures.ASBESTOS_ORE,
                WorldConfig.asbestosSpawn, WorldConfig.asbestosMinHeight, WorldConfig.asbestosMaxHeight,
                WorldConfig.ASBESTOS_SPAWN, WorldConfig.ASBESTOS_MIN, WorldConfig.ASBESTOS_MAX);

        registerOre(context, configuredFeatures, BERYLLIUM_ORE_PLACED, ModConfiguredFeatures.BERYLLIUM_ORE,
                WorldConfig.berylliumSpawn, WorldConfig.berylliumMinHeight, WorldConfig.berylliumMaxHeight,
                WorldConfig.BERYLLIUM_SPAWN, WorldConfig.BERYLLIUM_MIN, WorldConfig.BERYLLIUM_MAX);

        registerOre(context, configuredFeatures, CINNABAR_ORE_PLACED, ModConfiguredFeatures.CINNABAR_ORE,
                WorldConfig.cinnabarSpawn, WorldConfig.cinnabarMinHeight, WorldConfig.cinnabarMaxHeight,
                WorldConfig.CINNABAR_SPAWN, WorldConfig.CINNABAR_MIN, WorldConfig.CINNABAR_MAX);

        registerOre(context, configuredFeatures, COBALT_ORE_PLACED, ModConfiguredFeatures.COBALT_ORE,
                WorldConfig.cobaltSpawn, WorldConfig.cobaltMinHeight, WorldConfig.cobaltMaxHeight,
                WorldConfig.COBALT_SPAWN, WorldConfig.COBALT_MIN, WorldConfig.COBALT_MAX);

        registerOre(context, configuredFeatures, FLUORITE_ORE_PLACED, ModConfiguredFeatures.FLUORITE_ORE,
                WorldConfig.fluoriteSpawn, WorldConfig.fluoriteMinHeight, WorldConfig.fluoriteMaxHeight,
                WorldConfig.FLUORITE_SPAWN, WorldConfig.FLUORITE_MIN, WorldConfig.FLUORITE_MAX);

        registerOre(context, configuredFeatures, LEAD_ORE_PLACED, ModConfiguredFeatures.LEAD_ORE,
                WorldConfig.leadSpawn, WorldConfig.leadMinHeight, WorldConfig.leadMaxHeight,
                WorldConfig.LEAD_SPAWN, WorldConfig.LEAD_MIN, WorldConfig.LEAD_MAX);

        registerOre(context, configuredFeatures, LIGNITE_ORE_PLACED, ModConfiguredFeatures.LIGNITE_ORE,
                WorldConfig.ligniteSpawn, WorldConfig.ligniteMinHeight, WorldConfig.ligniteMaxHeight,
                WorldConfig.LIGNITE_SPAWN, WorldConfig.LIGNITE_MIN, WorldConfig.LIGNITE_MAX);

        registerOre(context, configuredFeatures, TITANIUM_ORE_PLACED, ModConfiguredFeatures.TITANIUM_ORE,
                WorldConfig.titaniumSpawn, WorldConfig.titaniumMinHeight, WorldConfig.titaniumMaxHeight,
                WorldConfig.TITANIUM_SPAWN, WorldConfig.TITANIUM_MIN, WorldConfig.TITANIUM_MAX);

        registerOre(context, configuredFeatures, NITER_ORE_PLACED, ModConfiguredFeatures.NITER_ORE,
                WorldConfig.niterSpawn, WorldConfig.niterMinHeight, WorldConfig.niterMaxHeight,
                WorldConfig.NITER_SPAWN, WorldConfig.NITER_MIN, WorldConfig.NITER_MAX);

        registerOre(context, configuredFeatures, RARE_ORE_PLACED, ModConfiguredFeatures.RARE_ORE,
                WorldConfig.rareSpawn, WorldConfig.rareMinHeight, WorldConfig.rareMaxHeight,
                WorldConfig.RARE_SPAWN, WorldConfig.RARE_MIN, WorldConfig.RARE_MAX);

        registerOre(context, configuredFeatures, SULFUR_ORE_PLACED, ModConfiguredFeatures.SULFUR_ORE,
                WorldConfig.sulfurSpawn, WorldConfig.sulfurMinHeight, WorldConfig.sulfurMaxHeight,
                WorldConfig.SULFUR_SPAWN, WorldConfig.SULFUR_MIN, WorldConfig.SULFUR_MAX);

        registerOre(context, configuredFeatures, TUNGSTEN_ORE_PLACED, ModConfiguredFeatures.TUNGSTEN_ORE,
                WorldConfig.tungstenSpawn, WorldConfig.tungstenMinHeight, WorldConfig.tungstenMaxHeight,
                WorldConfig.TUNGSTEN_SPAWN, WorldConfig.TUNGSTEN_MIN, WorldConfig.TUNGSTEN_MAX);

        registerOre(context, configuredFeatures, ZINC_ORE_PLACED, ModConfiguredFeatures.ZINC_ORE,
                WorldConfig.zincSpawn, WorldConfig.zincMinHeight, WorldConfig.zincMaxHeight,
                WorldConfig.ZINC_SPAWN, WorldConfig.ZINC_MIN, WorldConfig.ZINC_MAX);

        registerOre(context, configuredFeatures, THORIUM_ORE_PLACED, ModConfiguredFeatures.THORIUM_ORE,
                WorldConfig.thoriumSpawn, WorldConfig.thoriumMinHeight, WorldConfig.thoriumMaxHeight,
                WorldConfig.THORIUM_SPAWN, WorldConfig.THORIUM_MIN, WorldConfig.THORIUM_MAX);

        registerOre(context, configuredFeatures, URANIUM_ORE_PLACED, ModConfiguredFeatures.URANIUM_ORE,
                WorldConfig.uraniumSpawn, WorldConfig.uraniumMinHeight, WorldConfig.uraniumMaxHeight,
                WorldConfig.URANIUM_SPAWN, WorldConfig.URANIUM_MIN, WorldConfig.URANIUM_MAX);

        registerOre(context, configuredFeatures, STONE_RESOURCE_LIMESTONE_PLACED, ModConfiguredFeatures.STONE_RESOURCE_LIMESTONE,
                WorldConfig.limestoneSpawn, WorldConfig.limestoneMinHeight, WorldConfig.limestoneMaxHeight,
                WorldConfig.LIMESTONE_SPAWN, WorldConfig.LIMESTONE_MIN, WorldConfig.LIMESTONE_MAX);

        registerOre(context, configuredFeatures, ALEXANDRITE_ORE_PLACED, ModConfiguredFeatures.ALEXANDRITE_ORE,
                WorldConfig.alexandriteSpawn, WorldConfig.alexandriteMinHeight, WorldConfig.alexandriteMaxHeight,
                WorldConfig.ALEXANDRITE_SPAWN, WorldConfig.ALEXANDRITE_MIN, WorldConfig.ALEXANDRITE_MAX);

        // === РЕГИСТРАЦИЯ DEEPSLATE РУД ===
        registerOre(context, configuredFeatures, ALUMINIUM_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.ALUMINIUM_DEEPSLATE_ORE,
                WorldConfig.aluminiumDeepslateSpawn, WorldConfig.aluminiumDeepslateMinHeight, WorldConfig.aluminiumDeepslateMaxHeight,
                WorldConfig.ALUMINIUM_DEEPSLATE_SPAWN, WorldConfig.ALUMINIUM_DEEPSLATE_MIN, WorldConfig.ALUMINIUM_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, ASBESTOS_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.ASBESTOS_DEEPSLATE_ORE,
                WorldConfig.asbestosDeepslateSpawn, WorldConfig.asbestosDeepslateMinHeight, WorldConfig.asbestosDeepslateMaxHeight,
                WorldConfig.ASBESTOS_DEEPSLATE_SPAWN, WorldConfig.ASBESTOS_DEEPSLATE_MIN, WorldConfig.ASBESTOS_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, BERYLLIUM_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.BERYLLIUM_DEEPSLATE_ORE,
                WorldConfig.berylliumDeepslateSpawn, WorldConfig.berylliumDeepslateMinHeight, WorldConfig.berylliumDeepslateMaxHeight,
                WorldConfig.BERYLLIUM_DEEPSLATE_SPAWN, WorldConfig.BERYLLIUM_DEEPSLATE_MIN, WorldConfig.BERYLLIUM_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, CINNABAR_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.CINNABAR_DEEPSLATE_ORE,
                WorldConfig.cinnabarDeepslateSpawn, WorldConfig.cinnabarDeepslateMinHeight, WorldConfig.cinnabarDeepslateMaxHeight,
                WorldConfig.CINNABAR_DEEPSLATE_SPAWN, WorldConfig.CINNABAR_DEEPSLATE_MIN, WorldConfig.CINNABAR_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, COBALT_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.COBALT_DEEPSLATE_ORE,
                WorldConfig.cobaltDeepslateSpawn, WorldConfig.cobaltDeepslateMinHeight, WorldConfig.cobaltDeepslateMaxHeight,
                WorldConfig.COBALT_DEEPSLATE_SPAWN, WorldConfig.COBALT_DEEPSLATE_MIN, WorldConfig.COBALT_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, FLUORITE_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.FLUORITE_DEEPSLATE_ORE,
                WorldConfig.fluoriteDeepslateSpawn, WorldConfig.fluoriteDeepslateMinHeight, WorldConfig.fluoriteDeepslateMaxHeight,
                WorldConfig.FLUORITE_DEEPSLATE_SPAWN, WorldConfig.FLUORITE_DEEPSLATE_MIN, WorldConfig.FLUORITE_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, LEAD_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.LEAD_DEEPSLATE_ORE,
                WorldConfig.leadDeepslateSpawn, WorldConfig.leadDeepslateMinHeight, WorldConfig.leadDeepslateMaxHeight,
                WorldConfig.LEAD_DEEPSLATE_SPAWN, WorldConfig.LEAD_DEEPSLATE_MIN, WorldConfig.LEAD_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, LIGNITE_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.LIGNITE_DEEPSLATE_ORE,
                WorldConfig.ligniteDeepslateSpawn, WorldConfig.ligniteDeepslateMinHeight, WorldConfig.ligniteDeepslateMaxHeight,
                WorldConfig.LIGNITE_DEEPSLATE_SPAWN, WorldConfig.LIGNITE_DEEPSLATE_MIN, WorldConfig.LIGNITE_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, TITANIUM_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.TITANIUM_DEEPSLATE_ORE,
                WorldConfig.titaniumDeepslateSpawn, WorldConfig.titaniumDeepslateMinHeight, WorldConfig.titaniumDeepslateMaxHeight,
                WorldConfig.TITANIUM_DEEPSLATE_SPAWN, WorldConfig.TITANIUM_DEEPSLATE_MIN, WorldConfig.TITANIUM_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, NITER_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.NITER_DEEPSLATE_ORE,
                WorldConfig.niterDeepslateSpawn, WorldConfig.niterDeepslateMinHeight, WorldConfig.niterDeepslateMaxHeight,
                WorldConfig.NITER_DEEPSLATE_SPAWN, WorldConfig.NITER_DEEPSLATE_MIN, WorldConfig.NITER_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, RARE_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.RARE_DEEPSLATE_ORE,
                WorldConfig.rareDeepslateSpawn, WorldConfig.rareDeepslateMinHeight, WorldConfig.rareDeepslateMaxHeight,
                WorldConfig.RARE_DEEPSLATE_SPAWN, WorldConfig.RARE_DEEPSLATE_MIN, WorldConfig.RARE_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, SULFUR_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.SULFUR_DEEPSLATE_ORE,
                WorldConfig.sulfurDeepslateSpawn, WorldConfig.sulfurDeepslateMinHeight, WorldConfig.sulfurDeepslateMaxHeight,
                WorldConfig.SULFUR_DEEPSLATE_SPAWN, WorldConfig.SULFUR_DEEPSLATE_MIN, WorldConfig.SULFUR_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, TUNGSTEN_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.TUNGSTEN_DEEPSLATE_ORE,
                WorldConfig.tungstenDeepslateSpawn, WorldConfig.tungstenDeepslateMinHeight, WorldConfig.tungstenDeepslateMaxHeight,
                WorldConfig.TUNGSTEN_DEEPSLATE_SPAWN, WorldConfig.TUNGSTEN_DEEPSLATE_MIN, WorldConfig.TUNGSTEN_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, ZINC_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.ZINC_DEEPSLATE_ORE,
                WorldConfig.zincDeepslateSpawn, WorldConfig.zincDeepslateMinHeight, WorldConfig.zincDeepslateMaxHeight,
                WorldConfig.ZINC_DEEPSLATE_SPAWN, WorldConfig.ZINC_DEEPSLATE_MIN, WorldConfig.ZINC_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, THORIUM_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.THORIUM_DEEPSLATE_ORE,
                WorldConfig.thoriumDeepslateSpawn, WorldConfig.thoriumDeepslateMinHeight, WorldConfig.thoriumDeepslateMaxHeight,
                WorldConfig.THORIUM_DEEPSLATE_SPAWN, WorldConfig.THORIUM_DEEPSLATE_MIN, WorldConfig.THORIUM_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, URANIUM_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.URANIUM_DEEPSLATE_ORE,
                WorldConfig.uraniumDeepslateSpawn, WorldConfig.uraniumDeepslateMinHeight, WorldConfig.uraniumDeepslateMaxHeight,
                WorldConfig.URANIUM_DEEPSLATE_SPAWN, WorldConfig.URANIUM_DEEPSLATE_MIN, WorldConfig.URANIUM_DEEPSLATE_MAX);

        register(context, BEDROCK_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.BEDROCK_ORE),
                List.of(RarityFilter.onAverageOnceEvery(1), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0))));

        register(context, IRON_CLUSTER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.IRON_CLUSTER),
                ModOrePlacement.commonOrePlacement(
                        WorldConfig.IRON_CLUSTER_SPAWN,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(15), VerticalAnchor.absolute(45))
                ));

        register(context, TITANIUM_CLUSTER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.TITANIUM_CLUSTER),
                ModOrePlacement.commonOrePlacement(
                        WorldConfig.TITANIUM_CLUSTER_SPAWN,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(15), VerticalAnchor.absolute(30))
                ));

        register(context, ALUMINIUM_CLUSTER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ALUMINIUM_CLUSTER),
                ModOrePlacement.commonOrePlacement(
                        WorldConfig.ALUMINIUM_CLUSTER_SPAWN,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(15), VerticalAnchor.absolute(35))
                ));

        register(context, COPPER_CLUSTER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.COPPER_CLUSTER),
                ModOrePlacement.commonOrePlacement(
                        WorldConfig.COPPER_CLUSTER_SPAWN,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(15), VerticalAnchor.absolute(20))
                ));


        register(context, OIL_BUBBLE_PLACED, context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.OIL_BUBBLE),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.OIL_SPAWN),   // шанс 1/100 на чанк
                        InSquarePlacement.spread(),              // случайные X,Z внутри чанка
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(25)) // Y от 0 до 24
                )
        );

        // Газ в сланце
        register(context, GNEISS_GAS_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_GAS_ORE),
                ModOrePlacement.commonOrePlacement(
                        WorldConfig.GASSHALE_SPAWN,
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(10),
                                VerticalAnchor.absolute(30)
                        )));

        // Газовые пузыри
        register(context, GAS_BUBBLE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GAS_BUBBLE),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.GASBUBBLE_SPAWN),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(30))
                ));

        register(context, EXPLOSIVE_BUBBLE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.EXPLOSIVE_BUBBLE),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.EXPLOSIVEBUBBLE_SPAWN),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(30))
                ));

        register(context, BROADCASTER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.BROADCASTER),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.BROADCASTER),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(255))
                ));

        register(context, LANDMINE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.LANDMINE),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.MINE_FREQ),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(255))
                ));

        register(context, CRATER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.CRATER),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.RAD_FREQ),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(255))
                ));

        register(context, ANTENNA_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.ANTENNA),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.ANTENNA_STRUCTURE),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(255))
                ));

        register(context, RUIN_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUIN),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.RUIN_STRUCTURE),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(255))
                ));

        register(context, FLOWER_FOXGLOVE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_FOXGLOVE), 16);
        register(context, FLOWER_NIGHTSHADE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_NIGHTSHADE), 8);
        register(context, FLOWER_TOBACCO_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_TOBACCO), 8);
        register(context, FLOWER_WEED_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_WEED), 64);
        register(context, FLOWER_CD0_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_CD0), 4);
        register(context, TALL_WEED_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.TALL_WEED), 64);
        register(context, TALL_CD2_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.TALL_CD2), 4);
        register(context, DEAD_PLANT_GENERIC_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.DEAD_PLANT_GENERIC), 32);

        registerNetherOre(context, NETHER_URANIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_URANIUM_ORE),
                WorldConfig.DEFAULT_NETHER_URANIUM_SPAWN, 0, 127);
        registerNetherOre(context, NETHER_TUNGSTEN_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_TUNGSTEN_ORE),
                WorldConfig.DEFAULT_NETHER_TUNGSTEN_SPAWN, 0, 127);
        registerNetherOre(context, NETHER_SULFUR_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_SULFUR_ORE),
                WorldConfig.DEFAULT_NETHER_SULFUR_SPAWN, 0, 127);
        registerNetherOre(context, NETHER_PHOSPHORUS_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_PHOSPHORUS_ORE),
                WorldConfig.DEFAULT_NETHER_PHOSPHORUS_SPAWN, 0, 127);
        registerNetherOre(context, NETHER_COAL_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_COAL_ORE),
                WorldConfig.DEFAULT_NETHER_COAL_SPAWN, 16, 96);
        registerNetherOre(context, NETHER_COBALT_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_COBALT_ORE),
                WorldConfig.DEFAULT_NETHER_COBALT_SPAWN, 100, 26);
        if (GeneralConfig.ENABLE_PLUTONIUM_ORE) {
            registerNetherOre(context, NETHER_PLUTONIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_PLUTONIUM_ORE),
                    WorldConfig.DEFAULT_NETHER_PLUTONIUM_SPAWN, 0, 127);
        }

        register(context, NETHER_SMOLDERING_ORE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_SMOLDERING_ORE),
                List.of(
                        CountPlacement.of(30),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(112)),
                        BiomeFilter.biome()
                ));

        register(context, NETHER_GEYSER_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_GEYSER),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE),
                        BiomeFilter.biome()
                ));
    }

    private static void registerNetherOre(BootstapContext<PlacedFeature> context,
                                          ResourceKey<PlacedFeature> key,
                                          Holder<ConfiguredFeature<?, ?>> feature,
                                          int spawnCount, int minHeight, int maxHeight) {
        context.register(key, new PlacedFeature(
                feature,
                List.of(
                        CountPlacement.of(spawnCount),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(minHeight), VerticalAnchor.absolute(maxHeight)),
                        BiomeFilter.biome()
                )
        ));
    }

    private static void register(BootstapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> feature,
                                 int rarity) {
        context.register(key, new PlacedFeature(
                feature,
                List.of(
                        RarityFilter.onAverageOnceEvery(rarity),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        ));
    }

    private static void registerOre(BootstapContext<PlacedFeature> context,
                                    HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                    ResourceKey<PlacedFeature> placedKey,
                                    ResourceKey<ConfiguredFeature<?, ?>> configuredKey,
                                    ForgeConfigSpec.IntValue spawnConfig,
                                    ForgeConfigSpec.IntValue minHeightConfig,
                                    ForgeConfigSpec.IntValue maxHeightConfig,
                                    int defaultSpawn, int defaultMin, int defaultMax) {

        register(context, placedKey, configuredFeatures.getOrThrow(configuredKey),
                ModOrePlacement.commonOrePlacement(
                        getSpawnCount(spawnConfig, defaultSpawn),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(getHeight(minHeightConfig, defaultMin)),
                                VerticalAnchor.absolute(getHeight(maxHeightConfig, defaultMax))
                        )));
    }

    private static int getSpawnCount(ForgeConfigSpec.IntValue config, int defaultValue) {
        return ConfigHelper.getSafeInt(config, defaultValue);
    }

    private static int getHeight(ForgeConfigSpec.IntValue config, int defaultValue) {
        return ConfigHelper.getSafeInt(config, defaultValue);
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResLocation(MODID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}