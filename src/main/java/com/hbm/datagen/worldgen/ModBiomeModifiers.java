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
    public static final ResourceKey<BiomeModifier> ADD_ZINC_ORE = registerKey("add_zinc_ore");
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
    public static final ResourceKey<BiomeModifier> ADD_ZINC_DEEPSLATE_ORE = registerKey("add_zinc_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_THORIUM_DEEPSLATE_ORE = registerKey("add_thorium_deepslate_ore");
    public static final ResourceKey<BiomeModifier> ADD_URANIUM_DEEPSLATE_ORE = registerKey("add_uranium_deepslate_ore");

    public static final ResourceKey<BiomeModifier> ADD_BEDROCK_ORE = registerKey("add_bedrock_ore");

    public static final ResourceKey<BiomeModifier> ADD_IRON_CLUSTER = registerKey("add_iron_cluster");
    public static final ResourceKey<BiomeModifier> ADD_TITANIUM_CLUSTER = registerKey("add_titanium_cluster");
    public static final ResourceKey<BiomeModifier> ADD_ALUMINIUM_CLUSTER = registerKey("add_aluminium_cluster");
    public static final ResourceKey<BiomeModifier> ADD_COPPER_CLUSTER = registerKey("add_copper_cluster");

    public static final ResourceKey<BiomeModifier> ADD_OIL_BUBBLE = registerKey("add_oil_bubble");

    public static final ResourceKey<BiomeModifier> ADD_GNEISS_GAS_ORE = registerKey("add_gneiss_gas_ore");
    public static final ResourceKey<BiomeModifier> ADD_GAS_BUBBLE = registerKey("add_gas_bubble");
    public static final ResourceKey<BiomeModifier> ADD_EXPLOSIVE_BUBBLE = registerKey("add_explosive_bubble");

    public static final ResourceKey<BiomeModifier> ADD_BROADCASTER = registerKey("add_broadcaster");
    public static final ResourceKey<BiomeModifier> ADD_LANDMINE = registerKey("add_landmine");
    public static final ResourceKey<BiomeModifier> ADD_CRATER = registerKey("add_crater");
    public static final ResourceKey<BiomeModifier> ADD_ANTENNA = registerKey("add_antenna");
    public static final ResourceKey<BiomeModifier> ADD_RUIN = registerKey("add_ruin");


    public static final ResourceKey<BiomeModifier> ADD_CREEPER_PHOSGENE = registerKey("add_creeper_phosgene");
    public static final ResourceKey<BiomeModifier> ADD_CREEPER_VOLATILE = registerKey("add_creeper_volatile");
    public static final ResourceKey<BiomeModifier> ADD_CREEPER_GOLD = registerKey("add_creeper_gold");

    public static final ResourceKey<BiomeModifier> ADD_FLOWER_FOXGLOVE = registerKey("add_flower_foxglove");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_NIGHTSHADE = registerKey("add_flower_nightshade");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_TOBACCO = registerKey("add_flower_tobacco");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_WEED = registerKey("add_flower_weed");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_CD0 = registerKey("add_flower_cd0");
    public static final ResourceKey<BiomeModifier> ADD_TALL_WEED = registerKey("add_tall_weed");
    public static final ResourceKey<BiomeModifier> ADD_TALL_CD2 = registerKey("add_tall_cd2");
    public static final ResourceKey<BiomeModifier> ADD_DEAD_PLANT = registerKey("add_dead_plant");

    public static final ResourceKey<BiomeModifier> ADD_NETHER_URANIUM_ORE = registerKey("add_nether_uranium_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_TUNGSTEN_ORE = registerKey("add_nether_tungsten_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_SULFUR_ORE = registerKey("add_nether_sulfur_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_PHOSPHORUS_ORE = registerKey("add_nether_phosphorus_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_COAL_ORE = registerKey("add_nether_coal_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_COBALT_ORE = registerKey("add_nether_cobalt_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_PLUTONIUM_ORE = registerKey("add_nether_plutonium_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_SMOLDERING_ORE = registerKey("add_nether_smoldering_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_GEYSER = registerKey("add_nether_geyser");

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

        context.register(ADD_ZINC_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ZINC_ORE_PLACED)),
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

        context.register(ADD_ZINC_DEEPSLATE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ZINC_DEEPSLATE_ORE_PLACED)),
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


        context.register(ADD_OIL_BUBBLE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OIL_BUBBLE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));


        context.register(ADD_GNEISS_GAS_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GNEISS_GAS_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_GAS_BUBBLE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GAS_BUBBLE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_EXPLOSIVE_BUBBLE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.EXPLOSIVE_BUBBLE_PLACED)),
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

        context.register(ADD_RUIN, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.RUIN_PLACED)),
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

        context.register(ADD_NETHER_GEYSER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_GEYSER_PLACED)),
                GenerationStep.Decoration.SURFACE_STRUCTURES));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResLocation(MODID, name));
    }
}