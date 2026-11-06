package com.hbm.datagen.worldgen;

import com.hbm.config.GeneralConfig;
import com.hbm.entity.ModEntities;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class ModBiomeModifiers {
    // === Ключи для ОБЫЧНЫХ РУД ===
    public static final ResourceKey<BiomeModifier> ADD_ALUMINIUM_ORE = registerKey("add_aluminium_ore");
    public static final ResourceKey<BiomeModifier> ADD_ASBESTOS_ORE = registerKey("add_asbestos_ore");
    public static final ResourceKey<BiomeModifier> ADD_BERYLLIUM_ORE = registerKey("add_beryllium_ore");
    public static final ResourceKey<BiomeModifier> ADD_CINNABAR_ORE = registerKey("add_cinnabar_ore");
    public static final ResourceKey<BiomeModifier> ADD_COBALT_ORE = registerKey("add_cobalt_ore");
    public static final ResourceKey<BiomeModifier> ADD_FLUORITE_ORE = registerKey("add_fluorite_ore");
    public static final ResourceKey<BiomeModifier> ADD_LEAD_ORE = registerKey("add_lead_ore");
    public static final ResourceKey<BiomeModifier> ADD_LIGNITE_ORE = registerKey("add_lignite_ore");
    public static final ResourceKey<BiomeModifier> ADD_TITANIUM_ORE = registerKey("add_titanium_ore");
    public static final ResourceKey<BiomeModifier> ADD_NITER_ORE = registerKey("add_niter_ore");
    public static final ResourceKey<BiomeModifier> ADD_RARE_ORE = registerKey("add_rare_ore");
    public static final ResourceKey<BiomeModifier> ADD_SULFUR_ORE = registerKey("add_sulfur_ore");
    public static final ResourceKey<BiomeModifier> ADD_TUNGSTEN_ORE = registerKey("add_tungsten_ore");
    public static final ResourceKey<BiomeModifier> ADD_THORIUM_ORE = registerKey("add_thorium_ore");
    public static final ResourceKey<BiomeModifier> ADD_URANIUM_ORE = registerKey("add_uranium_ore");
    public static final ResourceKey<BiomeModifier> ADD_STONE_RESOURCE_LIMESTONE = registerKey("add_stone_resource_limestone");
    public static final ResourceKey<BiomeModifier> ADD_ALEXANDRITE_ORE = registerKey("add_alexandrite_ore");

    // === Ключи для DEEPSLATE РУД ===
    public static final ResourceKey<BiomeModifier> ADD_ALUMINIUM_DEEPSLATE_ORE = registerKey("add_aluminium_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_ASBESTOS_DEEPSLATE_ORE = registerKey("add_asbestos_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_BERYLLIUM_DEEPSLATE_ORE = registerKey("add_beryllium_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_CINNABAR_DEEPSLATE_ORE = registerKey("add_cinnabar_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_COBALT_DEEPSLATE_ORE = registerKey("add_cobalt_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_FLUORITE_DEEPSLATE_ORE = registerKey("add_fluorite_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_LEAD_DEEPSLATE_ORE = registerKey("add_lead_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_LIGNITE_DEEPSLATE_ORE = registerKey("add_lignite_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_TITANIUM_DEEPSLATE_ORE = registerKey("add_titanium_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_NITER_DEEPSLATE_ORE = registerKey("add_niter_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_RARE_DEEPSLATE_ORE = registerKey("add_rare_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_SULFUR_DEEPSLATE_ORE = registerKey("add_sulfur_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_TUNGSTEN_DEEPSLATE_ORE = registerKey("add_tungsten_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_THORIUM_DEEPSLATE_ORE = registerKey("add_thorium_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_URANIUM_DEEPSLATE_ORE = registerKey("add_uranium_deepslate_ore");



    public static final ResourceKey<BiomeModifier> ADD_IRON_CLUSTER = registerKey("add_iron_cluster");
    public static final ResourceKey<BiomeModifier> ADD_TITANIUM_CLUSTER = registerKey("add_titanium_cluster");
    public static final ResourceKey<BiomeModifier> ADD_ALUMINIUM_CLUSTER = registerKey("add_aluminium_cluster");
    public static final ResourceKey<BiomeModifier> ADD_COPPER_CLUSTER = registerKey("add_copper_cluster");

    public static final ResourceKey<BiomeModifier> ADD_GNEISS_GAS_ORE = registerKey("add_gneiss_gas_ore");
    public static final ResourceKey<BiomeModifier> ADD_GNEISS_IRON_ORE = registerKey("add_gneiss_iron_ore");
    public static final ResourceKey<BiomeModifier> ADD_GNEISS_GOLD_ORE = registerKey("add_gneiss_gold_ore");
    public static final ResourceKey<BiomeModifier> ADD_GNEISS_URANIUM_ORE = registerKey("add_gneiss_uranium_ore");
    public static final ResourceKey<BiomeModifier> ADD_GNEISS_COPPER_ORE = registerKey("add_gneiss_copper_ore");
    public static final ResourceKey<BiomeModifier> ADD_GNEISS_ASBESTOS_ORE = registerKey("add_gneiss_asbestos_ore");
    public static final ResourceKey<BiomeModifier> ADD_GNEISS_LITHIUM_ORE = registerKey("add_gneiss_lithium_ore");
    public static final ResourceKey<BiomeModifier> ADD_GNEISS_RARE_ORE = registerKey("add_gneiss_rare_ore");
    public static final ResourceKey<BiomeModifier> ADD_GAS_FLAMMABLE_ORE = registerKey("add_gas_flammable_ore");
    public static final ResourceKey<BiomeModifier> ADD_GAS_EXPLOSIVE_ORE = registerKey("add_gas_explosive_ore");

    public static final ResourceKey<BiomeModifier> ADD_BROADCASTER = registerKey("add_broadcaster");
    public static final ResourceKey<BiomeModifier> ADD_LANDMINE = registerKey("add_landmine");
    public static final ResourceKey<BiomeModifier> ADD_MISC_FEATURES = registerKey("add_misc_features");
    public static final ResourceKey<BiomeModifier> ADD_SOYUZ_CAPSULE = registerKey("add_soyuz_capsule");

    /** Чистка декора внутри NBT-структур (#102) — шаг после деревьев и травы. */
    public static final ResourceKey<BiomeModifier> ADD_STRUCTURE_CLEANUP = registerKey("add_structure_cleanup");


    public static final ResourceKey<BiomeModifier> ADD_CRATER = registerKey("add_crater");
    public static final ResourceKey<BiomeModifier> ADD_ANTENNA = registerKey("add_antenna");
    public static final ResourceKey<BiomeModifier> ADD_BARREL = registerKey("add_barrel");
    public static final ResourceKey<BiomeModifier> ADD_ARCTIC_VAULT = registerKey("add_arctic_vault");
    public static final ResourceKey<BiomeModifier> ADD_ANCIENT_TOMB = registerKey("add_ancient_tomb");
    public static final ResourceKey<BiomeModifier> ADD_LIBRARY_DUNGEON = registerKey("add_library_dungeon");


    public static final ResourceKey<BiomeModifier> ADD_DUD = registerKey("add_dud");
    public static final ResourceKey<BiomeModifier> ADD_OIL_SPOT = registerKey("add_oil_spot");
    public static final ResourceKey<BiomeModifier> ADD_GEYSER_WATER = registerKey("add_geyser_water");
    public static final ResourceKey<BiomeModifier> ADD_GEYSER_CHLORINE = registerKey("add_geyser_chlorine");
    public static final ResourceKey<BiomeModifier> ADD_GEYSER_VAPOR = registerKey("add_geyser_vapor");
    public static final ResourceKey<BiomeModifier> ADD_GEYSER_NETHER = registerKey("add_geyser_nether");
    public static final ResourceKey<BiomeModifier> ADD_GEYSER_LARGE_WATER = registerKey("add_geyser_large_water");
    public static final ResourceKey<BiomeModifier> ADD_GEYSER_LARGE_CHLORINE = registerKey("add_geyser_large_chlorine");
    public static final ResourceKey<BiomeModifier> ADD_GEYSER_LARGE_VAPOR = registerKey("add_geyser_large_vapor");
    public static final ResourceKey<BiomeModifier> ADD_GEYSER_LARGE_NETHER = registerKey("add_geyser_large_nether");
    public static final ResourceKey<BiomeModifier> ADD_SURFACE_SPOT = registerKey("add_surface_spot");
    public static final ResourceKey<BiomeModifier> ADD_METEORITE = registerKey("add_meteorite");
    public static final ResourceKey<BiomeModifier> ADD_BEDROCK_ORE = registerKey("add_bedrock_ore");
    public static final ResourceKey<BiomeModifier> ADD_BEDROCK_ORE_NETHER = registerKey("add_bedrock_ore_nether");
    public static final ResourceKey<BiomeModifier> ADD_DEPTH_DEPOSITS = registerKey("add_depth_deposits");
    public static final ResourceKey<BiomeModifier> ADD_DEPTH_DEPOSITS_NETHER = registerKey("add_depth_deposits_nether");
    public static final ResourceKey<BiomeModifier> ADD_BIOME_CAVE = registerKey("add_biome_cave");
    public static final ResourceKey<BiomeModifier> ADD_GLYPHID_HIVE = registerKey("add_glyphid_hive");

    public static final ResourceKey<BiomeModifier> ADD_CREEPER_PHOSGENE = registerKey("add_creeper_phosgene");
    public static final ResourceKey<BiomeModifier> ADD_CREEPER_VOLATILE = registerKey("add_creeper_volatile");
    public static final ResourceKey<BiomeModifier> ADD_CREEPER_GOLD = registerKey("add_creeper_gold");
    public static final ResourceKey<BiomeModifier> ADD_PIGEON = registerKey("add_pigeon");
    public static final ResourceKey<BiomeModifier> ADD_PLASTIC_BAG = registerKey("add_plastic_bag");

    public static final ResourceKey<BiomeModifier> ADD_FLOWER_FOXGLOVE = registerKey("add_flower_foxglove");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_NIGHTSHADE = registerKey("add_flower_nightshade");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_TOBACCO = registerKey("add_flower_tobacco");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_WEED = registerKey("add_flower_weed");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_CD0 = registerKey("add_flower_cd0");
    public static final ResourceKey<BiomeModifier> ADD_TALL_WEED = registerKey("add_tall_weed");
    public static final ResourceKey<BiomeModifier> ADD_TALL_CD2 = registerKey("add_tall_cd2");
    public static final ResourceKey<BiomeModifier> ADD_DEAD_PLANT = registerKey("add_dead_plant");
    public static final ResourceKey<BiomeModifier> ADD_REEDS_RIVER = registerKey("add_reeds_river");
    public static final ResourceKey<BiomeModifier> ADD_REEDS_BEACH = registerKey("add_reeds_beach");
    public static final ResourceKey<BiomeModifier> ADD_TIKITE_ORE = registerKey("add_tikite_ore");
    public static final ResourceKey<BiomeModifier> ADD_AUSTRALIUM_ORE = registerKey("add_australium_ore");
    public static final ResourceKey<BiomeModifier> ADD_COLTAN_DEPOSIT = registerKey("add_coltan_deposit");
    public static final ResourceKey<BiomeModifier> ADD_HEMATITE_LAYER = registerKey("add_hematite_layer");
    public static final ResourceKey<BiomeModifier> ADD_BAUXITE_LAYER = registerKey("add_bauxite_layer");
    public static final ResourceKey<BiomeModifier> ADD_MALACHITE_LAYER = registerKey("add_malachite_layer");

    public static final ResourceKey<BiomeModifier> ADD_NETHER_URANIUM_ORE = registerKey("add_nether_uranium_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_TUNGSTEN_ORE = registerKey("add_nether_tungsten_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_SULFUR_ORE = registerKey("add_nether_sulfur_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_PHOSPHORUS_ORE = registerKey("add_nether_phosphorus_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_COAL_ORE = registerKey("add_nether_coal_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_COBALT_ORE = registerKey("add_nether_cobalt_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_PLUTONIUM_ORE = registerKey("add_nether_plutonium_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_SMOLDERING_ORE = registerKey("add_nether_smoldering_ore");




    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        // Получаем HolderSet для оверворлд биомов
        HolderSet<Biome> overworldBiomes = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderSet<Biome> netherBiomes = biomes.getOrThrow(BiomeTags.IS_NETHER);

        HolderSet<Biome> foxgloveBiomes = HolderSet.direct(
                biomes.getOrThrow(Biomes.FOREST),
                biomes.getOrThrow(Biomes.FLOWER_FOREST),
                biomes.getOrThrow(Biomes.BIRCH_FOREST),
                biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST),
                biomes.getOrThrow(Biomes.WINDSWEPT_FOREST)
        );

        HolderSet<Biome> nightshadeBiomes = HolderSet.direct(
                biomes.getOrThrow(Biomes.DARK_FOREST)
        );

        HolderSet<Biome> tobaccoBiomes = HolderSet.direct(
                biomes.getOrThrow(Biomes.JUNGLE),
                biomes.getOrThrow(Biomes.BAMBOO_JUNGLE),
                biomes.getOrThrow(Biomes.SPARSE_JUNGLE)
        );

        HolderSet<Biome> cd0Biomes = HolderSet.direct(
                biomes.getOrThrow(Biomes.RIVER),
                biomes.getOrThrow(Biomes.BEACH)
        );

        HolderSet<Biome> weedBiomes = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

        // === РЕГИСТРАЦИЯ ОБЫЧНЫХ РУД ===
        context.register(ADD_ALUMINIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ALUMINIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ASBESTOS_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ASBESTOS_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_BERYLLIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BERYLLIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_CINNABAR_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.CINNABAR_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_COBALT_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.COBALT_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_FLUORITE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FLUORITE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_LEAD_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LEAD_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_LIGNITE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LIGNITE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_TITANIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TITANIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NITER_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NITER_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_RARE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.RARE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_SULFUR_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SULFUR_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_TUNGSTEN_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TUNGSTEN_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_THORIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.THORIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_URANIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.URANIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_STONE_RESOURCE_LIMESTONE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.STONE_RESOURCE_LIMESTONE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ALEXANDRITE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ALEXANDRITE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        // === РЕГИСТРАЦИЯ DEEPSLATE РУД ===
        context.register(ADD_ALUMINIUM_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ALUMINIUM_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ASBESTOS_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ASBESTOS_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_BERYLLIUM_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BERYLLIUM_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_CINNABAR_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.CINNABAR_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_COBALT_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.COBALT_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_FLUORITE_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FLUORITE_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_LEAD_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LEAD_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_LIGNITE_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LIGNITE_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_TITANIUM_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TITANIUM_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NITER_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NITER_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_RARE_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.RARE_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_SULFUR_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SULFUR_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_TUNGSTEN_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TUNGSTEN_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_THORIUM_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.THORIUM_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_URANIUM_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.URANIUM_DEEPSLATE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_BEDROCK_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BEDROCK_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_BEDROCK_ORE_NETHER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BEDROCK_ORE_NETHER_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_IRON_CLUSTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.IRON_CLUSTER_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_TITANIUM_CLUSTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TITANIUM_CLUSTER_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ALUMINIUM_CLUSTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ALUMINIUM_CLUSTER_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_COPPER_CLUSTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.COPPER_CLUSTER_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));


        context.register(ADD_GNEISS_GAS_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_GAS_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_GNEISS_IRON_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_IRON_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_GNEISS_GOLD_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_GOLD_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_GNEISS_URANIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_URANIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_GNEISS_COPPER_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_COPPER_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_GNEISS_ASBESTOS_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_ASBESTOS_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_GNEISS_LITHIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_LITHIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_GNEISS_RARE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes, HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_RARE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_GAS_FLAMMABLE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GAS_FLAMMABLE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_GAS_EXPLOSIVE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GAS_EXPLOSIVE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_BROADCASTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BROADCASTER_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_LANDMINE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LANDMINE_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_MISC_FEATURES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(
                        placedFeatures.getOrThrow(ModPlacedFeatures.SAFE_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.PINK_LOG_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.STONE_KEYHOLE_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.LANTERN_BEHEMOTH_PLACED)
                ),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_SOYUZ_CAPSULE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BEACH),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SOYUZ_CAPSULE_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_STRUCTURE_CLEANUP, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.STRUCTURE_CLEANUP_PLACED)),
                GenerationStep.Decoration.TOP_LAYER_MODIFICATION
        ));



        context.register(ADD_CRATER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.CRATER_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_ANTENNA, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ANTENNA_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_BARREL, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BADLANDS),   // пустыни, или свой тег
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BARREL_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_ARCTIC_VAULT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),  // или свой тег для холодных биомов
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ARCTIC_VAULT_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_STRUCTURES
        ));

        context.register(ADD_ANCIENT_TOMB, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BADLANDS),   // или свой тег для пустынь
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ANCIENT_TOMB_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_LIBRARY_DUNGEON, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LIBRARY_DUNGEON_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_STRUCTURES
        ));

        context.register(ADD_DUD, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.DUD_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_OIL_SPOT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BADLANDS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OIL_SPOT_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_SURFACE_SPOT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SURFACE_SPOT_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_METEORITE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.METEORITE_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_DEPTH_DEPOSITS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(
                        placedFeatures.getOrThrow(ModPlacedFeatures.DEPTH_DEPOSIT_IRON_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.DEPTH_DEPOSIT_TITANIUM_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.DEPTH_DEPOSIT_TUNGSTEN_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.DEPTH_DEPOSIT_CINNABAR_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.DEPTH_DEPOSIT_ZIRCONIUM_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.DEPTH_DEPOSIT_BORAX_PLACED)
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ADD_DEPTH_DEPOSITS_NETHER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(
                        placedFeatures.getOrThrow(ModPlacedFeatures.DEPTH_DEPOSIT_NETHER_NEODYMIUM_PLACED),
                        placedFeatures.getOrThrow(ModPlacedFeatures.DEPTH_DEPOSIT_NETHER_NEODYMIUM_TOP_PLACED)
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ADD_BIOME_CAVE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BIOME_CAVE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_DECORATION
        ));

        context.register(ADD_GLYPHID_HIVE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(
                        placedFeatures.getOrThrow(ModPlacedFeatures.GLYPHID_HIVE_PLACED)
                ),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));




        // Получаем все биомы (или определённые)
        HolderSet<Biome> allBiomes = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

        context.register(ADD_CREEPER_PHOSGENE, new MobSpawnBiomeModifier(
                allBiomes,
                ModEntities.CREEPER_PHOSGENE.get(),
                50,   // вес (вероятность)
                1,   // мин. кол-во в группе
                1,   // макс. кол-во в группе
                MobCategory.MONSTER
        ));

        context.register(ADD_CREEPER_VOLATILE, new MobSpawnBiomeModifier(
                allBiomes,
                ModEntities.CREEPER_VOLATILE.get(),
                10, 1, 1, MobCategory.MONSTER
        ));

        context.register(ADD_CREEPER_GOLD, new MobSpawnBiomeModifier(
                allBiomes,
                ModEntities.CREEPER_GOLD.get(),
                1, 1, 1, MobCategory.MONSTER
        ));

        // Голубь — как в 1.7.10: BiomeDictionary.PLAINS, вес 1, группами 5-10.
        context.register(ADD_PIGEON, new MobSpawnBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.PLAINS),
                        biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS)
                ),
                ModEntities.PIGEON.get(),
                1, 5, 10, MobCategory.CREATURE
        ));

        // Мусорный пакет — BiomeDictionary.OCEAN, вес 1, по 1-3.
        context.register(ADD_PLASTIC_BAG, new MobSpawnBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OCEAN),
                ModEntities.PLASTIC_BAG.get(),
                1, 1, 3, MobCategory.WATER_CREATURE
        ));

        context.register(ADD_FLOWER_FOXGLOVE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                foxgloveBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FLOWER_FOXGLOVE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_FLOWER_NIGHTSHADE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                nightshadeBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FLOWER_NIGHTSHADE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_FLOWER_TOBACCO, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tobaccoBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FLOWER_TOBACCO_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_FLOWER_WEED, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                weedBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FLOWER_WEED_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_FLOWER_CD0, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                cd0Biomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FLOWER_CD0_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_TALL_WEED, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                weedBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TALL_WEED_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_TALL_CD2, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                cd0Biomes, // CD2 растёт из CD1, который у воды
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TALL_CD2_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_DEAD_PLANT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                weedBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.DEAD_PLANT_GENERIC_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_REEDS_RIVER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_RIVER),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.REEDS_RIVER_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_REEDS_BEACH, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BEACH),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.REEDS_BEACH_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_TIKITE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TIKITE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_AUSTRALIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.AUSTRALIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_COLTAN_DEPOSIT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.COLTAN_DEPOSIT_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_HEMATITE_LAYER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.HEMATITE_LAYER_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_BAUXITE_LAYER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BAUXITE_LAYER_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_MALACHITE_LAYER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.MALACHITE_LAYER_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        // === НЕТЕР РУДЫ ===
        context.register(ADD_NETHER_URANIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_URANIUM_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NETHER_TUNGSTEN_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_TUNGSTEN_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NETHER_SULFUR_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_SULFUR_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NETHER_PHOSPHORUS_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_PHOSPHORUS_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NETHER_COAL_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_COAL_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NETHER_COBALT_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_COBALT_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        if (GeneralConfig.ENABLE_PLUTONIUM_ORE) {
            context.register(ADD_NETHER_PLUTONIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                    netherBiomes,
                    HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_PLUTONIUM_ORE_PLACED)),
                    GenerationStep.Decoration.UNDERGROUND_ORES));
        }

        context.register(ADD_NETHER_SMOLDERING_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_SMOLDERING_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

// ВОДНЫЕ — болота, океаны, реки
        context.register(ADD_GEYSER_WATER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GEYSER_WATER_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_GEYSER_LARGE_WATER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GEYSER_LARGE_WATER_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

// ХЛОРНЫЕ — пустоши, саванна, бэдленды, горы
        context.register(ADD_GEYSER_CHLORINE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BADLANDS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GEYSER_CHLORINE_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_GEYSER_LARGE_CHLORINE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BADLANDS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GEYSER_LARGE_CHLORINE_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

// ПАРОВЫЕ — тайга, горы, снежные
        context.register(ADD_GEYSER_VAPOR, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_MOUNTAIN),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GEYSER_VAPOR_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_GEYSER_LARGE_VAPOR, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_MOUNTAIN),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GEYSER_LARGE_VAPOR_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

// АДСКИЕ — только Нижний мир
        context.register(ADD_GEYSER_NETHER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GEYSER_NETHER_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));

        context.register(ADD_GEYSER_LARGE_NETHER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GEYSER_LARGE_NETHER_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));



    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResLocation(MODID, name));
    }
}