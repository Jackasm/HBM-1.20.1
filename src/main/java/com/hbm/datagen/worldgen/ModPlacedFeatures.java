package com.hbm.datagen.worldgen;


import com.hbm.config.GeneralConfig;
import com.hbm.config.MobConfig;
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
    public static final ResourceKey<PlacedFeature> THORIUM_DEEPSLATE_ORE_PLACED = registerKey("thorium_deepslate_ore_placed");
    public static final ResourceKey<PlacedFeature> URANIUM_DEEPSLATE_ORE_PLACED = registerKey("uranium_deepslate_ore_placed");

    public static final ResourceKey<PlacedFeature> BEDROCK_ORE_PLACED = registerKey("bedrock_ore_placed");
    public static final ResourceKey<PlacedFeature> BEDROCK_ORE_NETHER_PLACED = registerKey("bedrock_ore_nether_placed");

    public static final ResourceKey<PlacedFeature> IRON_CLUSTER_PLACED = registerKey("iron_cluster_placed");
    public static final ResourceKey<PlacedFeature> TITANIUM_CLUSTER_PLACED = registerKey("titanium_cluster_placed");
    public static final ResourceKey<PlacedFeature> ALUMINIUM_CLUSTER_PLACED = registerKey("aluminium_cluster_placed");
    public static final ResourceKey<PlacedFeature> COPPER_CLUSTER_PLACED = registerKey("copper_cluster_placed");

    public static final ResourceKey<PlacedFeature> GNEISS_GAS_ORE_PLACED = registerKey("gneiss_gas_ore_placed");
    public static final ResourceKey<PlacedFeature> GNEISS_IRON_ORE_PLACED = registerKey("gneiss_iron_ore_placed");
    public static final ResourceKey<PlacedFeature> GNEISS_GOLD_ORE_PLACED = registerKey("gneiss_gold_ore_placed");
    public static final ResourceKey<PlacedFeature> GNEISS_URANIUM_ORE_PLACED = registerKey("gneiss_uranium_ore_placed");
    public static final ResourceKey<PlacedFeature> GNEISS_COPPER_ORE_PLACED = registerKey("gneiss_copper_ore_placed");
    public static final ResourceKey<PlacedFeature> GNEISS_ASBESTOS_ORE_PLACED = registerKey("gneiss_asbestos_ore_placed");
    public static final ResourceKey<PlacedFeature> GNEISS_LITHIUM_ORE_PLACED = registerKey("gneiss_lithium_ore_placed");
    public static final ResourceKey<PlacedFeature> GNEISS_RARE_ORE_PLACED = registerKey("gneiss_rare_ore_placed");
    public static final ResourceKey<PlacedFeature> GAS_FLAMMABLE_ORE_PLACED = registerKey("gas_flammable_ore_placed");
    public static final ResourceKey<PlacedFeature> GAS_EXPLOSIVE_ORE_PLACED = registerKey("gas_explosive_ore_placed");

    public static final ResourceKey<PlacedFeature> BROADCASTER_PLACED = registerKey("broadcaster_placed");
    public static final ResourceKey<PlacedFeature> LANDMINE_PLACED = registerKey("landmine_placed");
    public static final ResourceKey<PlacedFeature> SAFE_PLACED = registerKey("safe_placed");
    public static final ResourceKey<PlacedFeature> SOYUZ_CAPSULE_PLACED = registerKey("soyuz_capsule_placed");
    public static final ResourceKey<PlacedFeature> PINK_LOG_PLACED = registerKey("pink_log_placed");
    public static final ResourceKey<PlacedFeature> STONE_KEYHOLE_PLACED = registerKey("stone_keyhole_placed");
    public static final ResourceKey<PlacedFeature> LANTERN_BEHEMOTH_PLACED = registerKey("lantern_behemoth_placed");

    /** Чистка габарита NBT-структур после декорации (#102). */
    public static final ResourceKey<PlacedFeature> STRUCTURE_CLEANUP_PLACED = registerKey("structure_cleanup_placed");


    public static final ResourceKey<PlacedFeature> CRATER_PLACED = registerKey("crater_placed");

    public static final ResourceKey<PlacedFeature> ANTENNA_PLACED = registerKey("antenna_placed");
    public static final ResourceKey<PlacedFeature> BARREL_PLACED = registerKey("barrel_placed");
    public static final ResourceKey<PlacedFeature> ARCTIC_VAULT_PLACED = registerKey("arctic_vault_placed");
    public static final ResourceKey<PlacedFeature> ANCIENT_TOMB_PLACED = registerKey("ancient_tomb_placed");
    public static final ResourceKey<PlacedFeature> LIBRARY_DUNGEON_PLACED = registerKey("library_dungeon_placed");



    public static final ResourceKey<PlacedFeature> DUD_PLACED = registerKey("dud_placed");
    public static final ResourceKey<PlacedFeature> OIL_SPOT_PLACED = registerKey("oil_spot_placed");
    public static final ResourceKey<PlacedFeature> GEYSER_WATER_PLACED = registerKey("geyser_water_placed");
    public static final ResourceKey<PlacedFeature> GEYSER_CHLORINE_PLACED = registerKey("geyser_chlorine_placed");
    public static final ResourceKey<PlacedFeature> GEYSER_VAPOR_PLACED = registerKey("geyser_vapor_placed");
    public static final ResourceKey<PlacedFeature> GEYSER_NETHER_PLACED = registerKey("geyser_nether_placed");

    public static final ResourceKey<PlacedFeature> GEYSER_LARGE_WATER_PLACED = registerKey("geyser_large_water_placed");
    public static final ResourceKey<PlacedFeature> GEYSER_LARGE_CHLORINE_PLACED = registerKey("geyser_large_chlorine_placed");
    public static final ResourceKey<PlacedFeature> GEYSER_LARGE_VAPOR_PLACED = registerKey("geyser_large_vapor_placed");
    public static final ResourceKey<PlacedFeature> GEYSER_LARGE_NETHER_PLACED = registerKey("geyser_large_nether_placed");

    public static final ResourceKey<PlacedFeature> SURFACE_SPOT_PLACED = registerKey("surface_spot_placed");
    public static final ResourceKey<PlacedFeature> METEORITE_PLACED = registerKey("meteorite_placed");
    public static final ResourceKey<PlacedFeature> DEPTH_DEPOSIT_IRON_PLACED = registerKey("depth_deposit_iron_placed");
    public static final ResourceKey<PlacedFeature> DEPTH_DEPOSIT_TITANIUM_PLACED = registerKey("depth_deposit_titanium_placed");
    public static final ResourceKey<PlacedFeature> DEPTH_DEPOSIT_TUNGSTEN_PLACED = registerKey("depth_deposit_tungsten_placed");
    public static final ResourceKey<PlacedFeature> DEPTH_DEPOSIT_CINNABAR_PLACED = registerKey("depth_deposit_cinnabar_placed");
    public static final ResourceKey<PlacedFeature> DEPTH_DEPOSIT_ZIRCONIUM_PLACED = registerKey("depth_deposit_zirconium_placed");
    public static final ResourceKey<PlacedFeature> DEPTH_DEPOSIT_BORAX_PLACED = registerKey("depth_deposit_borax_placed");
    public static final ResourceKey<PlacedFeature> DEPTH_DEPOSIT_NETHER_NEODYMIUM_PLACED = registerKey("depth_deposit_nether_neodymium_placed");
    public static final ResourceKey<PlacedFeature> DEPTH_DEPOSIT_NETHER_NEODYMIUM_TOP_PLACED = registerKey("depth_deposit_nether_neodymium_top_placed");

    public static final ResourceKey<PlacedFeature> BIOME_CAVE_PLACED = registerKey("biome_cave_placed");
    public static final ResourceKey<PlacedFeature> GLYPHID_HIVE_PLACED = registerKey("glyphid_hive_placed");


    public static final ResourceKey<PlacedFeature> FLOWER_FOXGLOVE_PLACED = registerKey("flower_foxglove_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_NIGHTSHADE_PLACED = registerKey("flower_nightshade_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_TOBACCO_PLACED = registerKey("flower_tobacco_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_WEED_PLACED = registerKey("flower_weed_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_CD0_PLACED = registerKey("flower_cd0_placed");
    public static final ResourceKey<PlacedFeature> TALL_WEED_PLACED = registerKey("tall_weed_placed");
    public static final ResourceKey<PlacedFeature> TALL_CD2_PLACED = registerKey("tall_cd2_placed");
    public static final ResourceKey<PlacedFeature> DEAD_PLANT_GENERIC_PLACED = registerKey("dead_plant_generic_placed");
    public static final ResourceKey<PlacedFeature> REEDS_RIVER_PLACED = registerKey("reeds_river_placed");
    public static final ResourceKey<PlacedFeature> REEDS_BEACH_PLACED = registerKey("reeds_beach_placed");
    public static final ResourceKey<PlacedFeature> TIKITE_ORE_PLACED = registerKey("tikite_ore_placed");
    public static final ResourceKey<PlacedFeature> AUSTRALIUM_ORE_PLACED = registerKey("australium_ore_placed");
    public static final ResourceKey<PlacedFeature> COLTAN_DEPOSIT_PLACED = registerKey("coltan_deposit_placed");
    public static final ResourceKey<PlacedFeature> HEMATITE_LAYER_PLACED = registerKey("hematite_layer_placed");
    public static final ResourceKey<PlacedFeature> BAUXITE_LAYER_PLACED = registerKey("bauxite_layer_placed");
    public static final ResourceKey<PlacedFeature> MALACHITE_LAYER_PLACED = registerKey("malachite_layer_placed");

    // === НЕТЕР РУДЫ ===
    public static final ResourceKey<PlacedFeature> NETHER_URANIUM_ORE_PLACED = registerKey("nether_uranium_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_TUNGSTEN_ORE_PLACED = registerKey("nether_tungsten_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_SULFUR_ORE_PLACED = registerKey("nether_sulfur_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_PHOSPHORUS_ORE_PLACED = registerKey("nether_phosphorus_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_COAL_ORE_PLACED = registerKey("nether_coal_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_COBALT_ORE_PLACED = registerKey("nether_cobalt_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_PLUTONIUM_ORE_PLACED = registerKey("nether_plutonium_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_SMOLDERING_ORE_PLACED = registerKey("nether_smoldering_ore_placed");





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

        registerOre(context, configuredFeatures, THORIUM_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.THORIUM_DEEPSLATE_ORE,
                WorldConfig.thoriumDeepslateSpawn, WorldConfig.thoriumDeepslateMinHeight, WorldConfig.thoriumDeepslateMaxHeight,
                WorldConfig.THORIUM_DEEPSLATE_SPAWN, WorldConfig.THORIUM_DEEPSLATE_MIN, WorldConfig.THORIUM_DEEPSLATE_MAX);

        registerOre(context, configuredFeatures, URANIUM_DEEPSLATE_ORE_PLACED, ModConfiguredFeatures.URANIUM_DEEPSLATE_ORE,
                WorldConfig.uraniumDeepslateSpawn, WorldConfig.uraniumDeepslateMinHeight, WorldConfig.uraniumDeepslateMaxHeight,
                WorldConfig.URANIUM_DEEPSLATE_SPAWN, WorldConfig.URANIUM_DEEPSLATE_MIN, WorldConfig.URANIUM_DEEPSLATE_MAX);


        register(context, BEDROCK_ORE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.BEDROCK_ORE),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(1))                        
                ));

        register(context, BEDROCK_ORE_NETHER_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.BEDROCK_ORE_NETHER),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(1))
                ));

        register(context, IRON_CLUSTER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.IRON_CLUSTER),
                ModOrePlacement.commonOrePlacement(
                        WorldConfig.IRON_CLUSTER_SPAWN,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(15), VerticalAnchor.absolute(30))
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


        // Гнейс: 1.7.10 — target stone_gneiss, y 30..40, size 6 (газ — size 10)
        register(context, GNEISS_GAS_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_GAS_ORE),
                List.of(
                        CountPlacement.of(15),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()
                ));
        register(context, GNEISS_IRON_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_IRON_ORE),
                List.of(CountPlacement.of(25), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()));
        register(context, GNEISS_GOLD_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_GOLD_ORE),
                List.of(CountPlacement.of(10), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()));
        register(context, GNEISS_URANIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_URANIUM_ORE),
                List.of(CountPlacement.of(18), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()));
        register(context, GNEISS_COPPER_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_COPPER_ORE),
                List.of(CountPlacement.of(36), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()));
        register(context, GNEISS_ASBESTOS_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_ASBESTOS_ORE),
                List.of(CountPlacement.of(12), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()));
        register(context, GNEISS_LITHIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_LITHIUM_ORE),
                List.of(CountPlacement.of(6), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()));
        register(context, GNEISS_RARE_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.GNEISS_RARE_ORE),
                List.of(CountPlacement.of(6), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()));

        // Газ — снова руда (1/12 чанков, y 30..40, как в 1.7.10)
        register(context, GAS_FLAMMABLE_ORE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.GAS_FLAMMABLE_ORE),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.GASBUBBLE_SPAWN),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()
                ));

        // Взрывной газ — тоже руда (1/24 чанков, y 30..40, как в 1.7.10)
        register(context, GAS_EXPLOSIVE_ORE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.GAS_EXPLOSIVE_ORE),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.EXPLOSIVEBUBBLE_SPAWN),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(40)),
                        BiomeFilter.biome()
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

        register(context, SAFE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.SAFE),
                List.of(
                        RarityFilter.onAverageOnceEvery(2500),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

        register(context, SOYUZ_CAPSULE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.SOYUZ_CAPSULE),
                List.of(
                        RarityFilter.onAverageOnceEvery(100),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

        register(context, PINK_LOG_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.PINK_LOG),
                List.of(
                        RarityFilter.onAverageOnceEvery(1000),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

        register(context, STONE_KEYHOLE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.STONE_KEYHOLE),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(6), VerticalAnchor.absolute(19))
                ));

        register(context, LANTERN_BEHEMOTH_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.LANTERN_BEHEMOTH),
                List.of(
                        RarityFilter.onAverageOnceEvery(2000),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

        // #102: одна попытка на чанк на шаге после декорации; работу делает фича по своим данным.
        register(context, STRUCTURE_CLEANUP_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.STRUCTURE_CLEANUP),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        BiomeFilter.biome()
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

        register(context, BARREL_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.BARREL),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.BARREL_STRUCTURE),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

        register(context, ARCTIC_VAULT_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.ARCTIC_VAULT),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.ARCTIC_STRUCTURE),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(16),
                                VerticalAnchor.absolute(48))
                ));

        register(context, ANCIENT_TOMB_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.ANCIENT_TOMB),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.PYRAMID_STRUCTURE),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

        register(context, LIBRARY_DUNGEON_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.LIBRARY_DUNGEON),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.DUNGEON_STRUCTURE),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(255))
                ));

// Dud — редко, на поверхности
        register(context, DUD_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DUD),
                List.of(
                        RarityFilter.onAverageOnceEvery(500),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

// OilSpot — нечасто, в пустынях и саваннах
        register(context, OIL_SPOT_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OIL_SPOT),
                List.of(
                        RarityFilter.onAverageOnceEvery(80),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));


// SurfaceSpot
        register(context, SURFACE_SPOT_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.SURFACE_SPOT),
                List.of(
                        RarityFilter.onAverageOnceEvery(40),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

        register(context, METEORITE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.METEORITE),
                List.of(
                        RarityFilter.onAverageOnceEvery(WorldConfig.METEORITE_SPAWN),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));

        // ==================== DEPTH DEPOSITS — OVERWORLD ====================

        // Iron (chance 24, yMin 0, yDev 3)
        register(context, DEPTH_DEPOSIT_IRON_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DEPTH_DEPOSIT_IRON),
                List.of(
                        RarityFilter.onAverageOnceEvery(24),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(3))
                ));

        // Titanium (chance 32, yMin 0, yDev 3)
        register(context, DEPTH_DEPOSIT_TITANIUM_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DEPTH_DEPOSIT_TITANIUM),
                List.of(
                        RarityFilter.onAverageOnceEvery(32),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(3))
                ));

        // Tungsten (chance 32, yMin 0, yDev 3)
        register(context, DEPTH_DEPOSIT_TUNGSTEN_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DEPTH_DEPOSIT_TUNGSTEN),
                List.of(
                        RarityFilter.onAverageOnceEvery(32),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(3))
                ));

        // Cinnabar (chance 16, yMin 0, yDev 3)
        register(context, DEPTH_DEPOSIT_CINNABAR_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DEPTH_DEPOSIT_CINNABAR),
                List.of(
                        RarityFilter.onAverageOnceEvery(16),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(3))
                ));

        // Zirconium (chance 16, yMin 0, yDev 3)
        register(context, DEPTH_DEPOSIT_ZIRCONIUM_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DEPTH_DEPOSIT_ZIRCONIUM),
                List.of(
                        RarityFilter.onAverageOnceEvery(16),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(3))
                ));

        // Borax (chance 16, yMin 0, yDev 3)
        register(context, DEPTH_DEPOSIT_BORAX_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DEPTH_DEPOSIT_BORAX),
                List.of(
                        RarityFilter.onAverageOnceEvery(16),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(3))
                ));

        // ==================== DEPTH DEPOSITS — NETHER ====================

        // Neodymium nether (chance 16, два диапазона: 0-3 и 122-125)
        register(context, DEPTH_DEPOSIT_NETHER_NEODYMIUM_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DEPTH_DEPOSIT_NETHER_NEODYMIUM),
                List.of(
                        RarityFilter.onAverageOnceEvery(16),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(3))
                ));

        register(context, DEPTH_DEPOSIT_NETHER_NEODYMIUM_TOP_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.DEPTH_DEPOSIT_NETHER_NEODYMIUM),
                List.of(
                        RarityFilter.onAverageOnceEvery(16),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(122), VerticalAnchor.absolute(125))
                ));



        register(context, BIOME_CAVE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.BIOME_CAVE),
                List.of(
                        RarityFilter.onAverageOnceEvery(1),   // 1 попытка на чанк
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(20), VerticalAnchor.absolute(40))
                ));

        register(context, GLYPHID_HIVE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.GLYPHID_HIVE),
                List.of(
                        CountPlacement.of(1),
                        RarityFilter.onAverageOnceEvery(MobConfig.hiveSpawn),  // 256 по умолчанию
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                ));



        register(context, FLOWER_FOXGLOVE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_FOXGLOVE), 16);
        register(context, FLOWER_NIGHTSHADE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_NIGHTSHADE), 8);
        register(context, FLOWER_TOBACCO_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_TOBACCO), 8);
        register(context, FLOWER_WEED_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_WEED), 64);
        register(context, FLOWER_CD0_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FLOWER_CD0), 4);
        register(context, TALL_WEED_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.TALL_WEED), 64);
        register(context, TALL_CD2_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.TALL_CD2), 4);
        register(context, DEAD_PLANT_GENERIC_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.DEAD_PLANT_GENERIC), 32);

        // Камыш: вдвое реже эталона — реки 1/8, пляжи 1/16. В 1.7.10 было 1/4 и 1/8, и порт это
        // переносил верно; владелец после осмотра решил, что куртин слишком много (ADR-0021, #139).
        register(context, REEDS_RIVER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.REEDS),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));
        register(context, REEDS_BEACH_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.REEDS),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));

        // Тикит: 8 жил на чанк, size 6, y 0..126, только энд
        register(context, TIKITE_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.TIKITE_ORE),
                List.of(CountPlacement.of(8), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(126)),
                        BiomeFilter.biome()));

        // Австралиум и колтан сами проверяют координаты/seed
        register(context, AUSTRALIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.AUSTRALIUM_ORE),
                List.of(CountPlacement.of(1), InSquarePlacement.spread()));
        register(context, COLTAN_DEPOSIT_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.COLTAN_DEPOSIT),
                List.of(CountPlacement.of(1), InSquarePlacement.spread()));

        // OreLayer3D: одна попытка на чанк, фича сама обходит 16x16x(64..6)
        register(context, HEMATITE_LAYER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.HEMATITE_LAYER),
                List.of(CountPlacement.of(1), InSquarePlacement.spread()));
        register(context, BAUXITE_LAYER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.BAUXITE_LAYER),
                List.of(CountPlacement.of(1), InSquarePlacement.spread()));
        register(context, MALACHITE_LAYER_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.MALACHITE_LAYER),
                List.of(CountPlacement.of(1), InSquarePlacement.spread()));

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
                WorldConfig.DEFAULT_NETHER_COBALT_SPAWN, 100, 126);
        if (GeneralConfig.ENABLE_PLUTONIUM_ORE) {
            registerNetherOre(context, NETHER_PLUTONIUM_ORE_PLACED, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_PLUTONIUM_ORE),
                    WorldConfig.DEFAULT_NETHER_PLUTONIUM_SPAWN, 0, 127);
        }

        register(context, NETHER_SMOLDERING_ORE_PLACED,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_SMOLDERING_ORE),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread()
                ));

        // Гейзеры — маленькие
        registerGeyser(context, configuredFeatures, GEYSER_WATER_PLACED,
                ModConfiguredFeatures.GEYSER_WATER, 100);
        registerGeyser(context, configuredFeatures, GEYSER_CHLORINE_PLACED,
                ModConfiguredFeatures.GEYSER_CHLORINE, 100);
        registerGeyser(context, configuredFeatures, GEYSER_VAPOR_PLACED,
                ModConfiguredFeatures.GEYSER_VAPOR, 150);
        registerGeyser(context, configuredFeatures, GEYSER_NETHER_PLACED,
                ModConfiguredFeatures.GEYSER_NETHER, 80);

        // Гейзеры — большие
        registerGeyser(context, configuredFeatures, GEYSER_LARGE_WATER_PLACED,
                ModConfiguredFeatures.GEYSER_LARGE_WATER, 300);
        registerGeyser(context, configuredFeatures, GEYSER_LARGE_CHLORINE_PLACED,
                ModConfiguredFeatures.GEYSER_LARGE_CHLORINE, 300);
        registerGeyser(context, configuredFeatures, GEYSER_LARGE_VAPOR_PLACED,
                ModConfiguredFeatures.GEYSER_LARGE_VAPOR, 400);
        registerGeyser(context, configuredFeatures, GEYSER_LARGE_NETHER_PLACED,
                ModConfiguredFeatures.GEYSER_LARGE_NETHER, 250);


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
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(minHeight), VerticalAnchor.absolute(maxHeight))
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
                        // MOTION_BLOCKING, а не WORLD_SURFACE: трава и цветы не считаются
                        // поверхностью, поэтому растение садится на грунт и заменяет траву,
                        // как ванильный цветок. Вода и листва грунтом не становятся —
                        // их отсекает canSurvive у блока.
                        PlacementUtils.HEIGHTMAP
                )
        ));
    }

    private static void registerGeyser(
            BootstapContext<PlacedFeature> context,
            HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
            ResourceKey<PlacedFeature> key,
            ResourceKey<ConfiguredFeature<?, ?>> configured,
            int rarity) {
        register(context, key, configuredFeatures.getOrThrow(configured),
                List.of(
                        RarityFilter.onAverageOnceEvery(rarity),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE
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