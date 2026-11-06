package com.hbm.datagen.worldgen;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockResourceStone;
import com.hbm.config.GeneralConfig;
import com.hbm.config.WorldConfig;
import com.hbm.datagen.worldgen.feature.config.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class ModConfiguredFeatures {

    private static final RuleTest STONE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    private static final RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
    private static final RuleTest STONE_GNEISS_REPLACEABLES = new BlockMatchTest(ModBlocks.STONE_GNEISS.get());

    // === ОБЫЧНЫЕ РУДЫ ===
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALUMINIUM_ORE = registerKey("aluminium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASBESTOS_ORE = registerKey("asbestos_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BERYLLIUM_ORE = registerKey("beryllium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CINNABAR_ORE = registerKey("cinnabar_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COBALT_ORE = registerKey("cobalt_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLUORITE_ORE = registerKey("fluorite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEAD_ORE = registerKey("lead_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LIGNITE_ORE = registerKey("lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_ORE = registerKey("titanium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NITER_ORE = registerKey("niter_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RARE_ORE = registerKey("rare_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SULFUR_ORE = registerKey("sulfur_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TUNGSTEN_ORE = registerKey("tungsten_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> THORIUM_ORE = registerKey("thorium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> URANIUM_ORE = registerKey("uranium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_RESOURCE_LIMESTONE = registerKey("stone_resource_limestone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALEXANDRITE_ORE = registerKey("alexandrite_ore");

    // === DEEPSLATE РУДЫ ===
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALUMINIUM_DEEPSLATE_ORE = registerKey("aluminium_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASBESTOS_DEEPSLATE_ORE = registerKey("asbestos_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BERYLLIUM_DEEPSLATE_ORE = registerKey("beryllium_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CINNABAR_DEEPSLATE_ORE = registerKey("cinnabar_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COBALT_DEEPSLATE_ORE = registerKey("cobalt_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLUORITE_DEEPSLATE_ORE = registerKey("fluorite_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEAD_DEEPSLATE_ORE = registerKey("lead_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LIGNITE_DEEPSLATE_ORE = registerKey("lignite_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_DEEPSLATE_ORE = registerKey("titanium_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NITER_DEEPSLATE_ORE = registerKey("niter_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RARE_DEEPSLATE_ORE = registerKey("rare_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SULFUR_DEEPSLATE_ORE = registerKey("sulfur_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TUNGSTEN_DEEPSLATE_ORE = registerKey("tungsten_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> THORIUM_DEEPSLATE_ORE = registerKey("thorium_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> URANIUM_DEEPSLATE_ORE = registerKey("uranium_deepslate_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> BEDROCK_ORE = registerKey("bedrock_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BEDROCK_ORE_NETHER = registerKey("bedrock_ore_nether");

    public static final ResourceKey<ConfiguredFeature<?, ?>> IRON_CLUSTER = registerKey("iron_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_CLUSTER = registerKey("titanium_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALUMINIUM_CLUSTER = registerKey("aluminium_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COPPER_CLUSTER = registerKey("copper_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_GAS_ORE = registerKey("gneiss_gas_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_IRON_ORE = registerKey("gneiss_iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_GOLD_ORE = registerKey("gneiss_gold_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_URANIUM_ORE = registerKey("gneiss_uranium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_COPPER_ORE = registerKey("gneiss_copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_ASBESTOS_ORE = registerKey("gneiss_asbestos_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_LITHIUM_ORE = registerKey("gneiss_lithium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_RARE_ORE = registerKey("gneiss_rare_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TIKITE_ORE = registerKey("tikite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUSTRALIUM_ORE = registerKey("australium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COLTAN_DEPOSIT = registerKey("coltan_deposit");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEMATITE_LAYER = registerKey("hematite_layer");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BAUXITE_LAYER = registerKey("bauxite_layer");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MALACHITE_LAYER = registerKey("malachite_layer");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GAS_FLAMMABLE_ORE = registerKey("gas_flammable_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GAS_EXPLOSIVE_ORE = registerKey("gas_explosive_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BROADCASTER = registerKey("broadcaster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LANDMINE = registerKey("landmine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAFE = registerKey("safe");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOYUZ_CAPSULE = registerKey("soyuz_capsule");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PINK_LOG = registerKey("pink_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_KEYHOLE = registerKey("stone_keyhole");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LANTERN_BEHEMOTH = registerKey("lantern_behemoth");

    /** Чистка габарита NBT-структур после декорации (#102). */
    public static final ResourceKey<ConfiguredFeature<?, ?>> STRUCTURE_CLEANUP = registerKey("structure_cleanup");

    public static final ResourceKey<ConfiguredFeature<?, ?>> CRATER = registerKey("crater");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ANTENNA = registerKey("antenna");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BARREL = registerKey("barrel");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARCTIC_VAULT = registerKey("arctic_vault");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_TOMB = registerKey("ancient_tomb");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LIBRARY_DUNGEON = registerKey("library_dungeon");


    public static final ResourceKey<ConfiguredFeature<?, ?>> DUD = registerKey("dud");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OIL_SPOT = registerKey("oil_spot");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEYSER_WATER = registerKey("geyser_water");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEYSER_CHLORINE = registerKey("geyser_chlorine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEYSER_VAPOR = registerKey("geyser_vapor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEYSER_NETHER = registerKey("geyser_nether");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEYSER_LARGE_WATER = registerKey("geyser_large_water");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEYSER_LARGE_CHLORINE = registerKey("geyser_large_chlorine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEYSER_LARGE_VAPOR = registerKey("geyser_large_vapor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEYSER_LARGE_NETHER = registerKey("geyser_large_nether");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SURFACE_SPOT = registerKey("surface_spot");
    public static final ResourceKey<ConfiguredFeature<?, ?>> METEORITE = registerKey("meteorite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEPTH_DEPOSIT_IRON = registerKey("depth_deposit_iron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEPTH_DEPOSIT_TITANIUM = registerKey("depth_deposit_titanium");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEPTH_DEPOSIT_TUNGSTEN = registerKey("depth_deposit_tungsten");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEPTH_DEPOSIT_CINNABAR = registerKey("depth_deposit_cinnabar");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEPTH_DEPOSIT_ZIRCONIUM = registerKey("depth_deposit_zirconium");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEPTH_DEPOSIT_BORAX = registerKey("depth_deposit_borax");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEPTH_DEPOSIT_NETHER_NEODYMIUM = registerKey("depth_deposit_nether_neodymium");

    public static final ResourceKey<ConfiguredFeature<?, ?>> BIOME_CAVE = registerKey("biome_cave");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLYPHID_HIVE = registerKey("glyphid_hive");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLYPHID_HIVE_INFECTED = registerKey("glyphid_hive_infected");


    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_FOXGLOVE = registerKey("flower_foxglove");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_NIGHTSHADE = registerKey("flower_nightshade");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_TOBACCO = registerKey("flower_tobacco");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_WEED = registerKey("flower_weed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_CD0 = registerKey("flower_cd0");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_WEED = registerKey("tall_weed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_CD2 = registerKey("tall_cd2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_PLANT_GENERIC = registerKey("dead_plant_generic");
    public static final ResourceKey<ConfiguredFeature<?, ?>> REEDS = registerKey("reeds");

    // === НЕТЕР РУДЫ ===
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_URANIUM_ORE = registerKey("nether_uranium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_TUNGSTEN_ORE = registerKey("nether_tungsten_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_SULFUR_ORE = registerKey("nether_sulfur_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_PHOSPHORUS_ORE = registerKey("nether_phosphorus_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_COAL_ORE = registerKey("nether_coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_COBALT_ORE = registerKey("nether_cobalt_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_PLUTONIUM_ORE = registerKey("nether_plutonium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_SMOLDERING_ORE = registerKey("nether_smoldering_ore");








    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {

        // === РЕГИСТРАЦИЯ ОБЫЧНЫХ РУД ===
        register(context, ALUMINIUM_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_ALUMINIUM.get().defaultBlockState())
                ), getVeinSize(WorldConfig.aluminiumVeinSize, WorldConfig.ALUMINIUM_VEIN_SIZE)));

        register(context, ASBESTOS_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_ASBESTOS.get().defaultBlockState())
                ), getVeinSize(WorldConfig.asbestosVeinSize, WorldConfig.ASBESTOS_VEIN_SIZE)));

        register(context, BERYLLIUM_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_BERYLLIUM.get().defaultBlockState())
                ), getVeinSize(WorldConfig.berylliumVeinSize, WorldConfig.BERYLLIUM_VEIN_SIZE)));

        register(context, CINNABAR_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_CINNABAR.get().defaultBlockState())
                ), getVeinSize(WorldConfig.cinnabarVeinSize, WorldConfig.CINNABAR_VEIN_SIZE)));

        register(context, COBALT_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_COBALT.get().defaultBlockState())
                ), getVeinSize(WorldConfig.cobaltVeinSize, WorldConfig.COBALT_VEIN_SIZE)));

        register(context, FLUORITE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_FLUORITE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.fluoriteVeinSize, WorldConfig.FLUORITE_VEIN_SIZE)));

        register(context, LEAD_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_LEAD.get().defaultBlockState())
                ), getVeinSize(WorldConfig.leadVeinSize, WorldConfig.LEAD_VEIN_SIZE)));

        register(context, LIGNITE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_LIGNITE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.ligniteVeinSize, WorldConfig.LIGNITE_VEIN_SIZE)));

        register(context, TITANIUM_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_TITANIUM.get().defaultBlockState())
                ), getVeinSize(WorldConfig.titaniumVeinSize, WorldConfig.TITANIUM_VEIN_SIZE)));

        register(context, NITER_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_NITER.get().defaultBlockState())
                ), getVeinSize(WorldConfig.niterVeinSize, WorldConfig.NITER_VEIN_SIZE)));

        register(context, RARE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_RARE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.rareVeinSize, WorldConfig.RARE_VEIN_SIZE)));

        register(context, SULFUR_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_SULFUR.get().defaultBlockState())
                ), getVeinSize(WorldConfig.sulfurVeinSize, WorldConfig.SULFUR_VEIN_SIZE)));

        register(context, TUNGSTEN_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_TUNGSTEN.get().defaultBlockState())
                ), getVeinSize(WorldConfig.tungstenVeinSize, WorldConfig.TUNGSTEN_VEIN_SIZE)));

        register(context, THORIUM_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_THORIUM.get().defaultBlockState())
                ), getVeinSize(WorldConfig.thoriumVeinSize, WorldConfig.THORIUM_VEIN_SIZE)));

        register(context, URANIUM_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_URANIUM.get().defaultBlockState())
                ), getVeinSize(WorldConfig.uraniumVeinSize, WorldConfig.URANIUM_VEIN_SIZE)));

        register(context, STONE_RESOURCE_LIMESTONE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.STONE_RESOURCE_LIMESTONE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.limestoneVeinSize, WorldConfig.LIMESTONE_VEIN_SIZE)));

        register(context, ALEXANDRITE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_ALEXANDRITE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.alexandriteVeinSize, WorldConfig.ALEXANDRITE_VEIN_SIZE)));

        // === РЕГИСТРАЦИЯ DEEPSLATE РУД ===
        register(context, ALUMINIUM_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_ALUMINIUM_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.aluminiumDeepslateVeinSize, WorldConfig.ALUMINIUM_DEEPSLATE_VEIN_SIZE)));

        register(context, ASBESTOS_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_ASBESTOS_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.asbestosDeepslateVeinSize, WorldConfig.ASBESTOS_DEEPSLATE_VEIN_SIZE)));

        register(context, BERYLLIUM_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_BERYLLIUM_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.berylliumDeepslateVeinSize, WorldConfig.BERYLLIUM_DEEPSLATE_VEIN_SIZE)));

        register(context, CINNABAR_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_CINNABAR_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.cinnabarDeepslateVeinSize, WorldConfig.CINNABAR_DEEPSLATE_VEIN_SIZE)));

        register(context, COBALT_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_COBALT_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.cobaltDeepslateVeinSize, WorldConfig.COBALT_DEEPSLATE_VEIN_SIZE)));

        register(context, FLUORITE_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_FLUORITE_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.fluoriteDeepslateVeinSize, WorldConfig.FLUORITE_DEEPSLATE_VEIN_SIZE)));

        register(context, LEAD_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_LEAD_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.leadDeepslateVeinSize, WorldConfig.LEAD_DEEPSLATE_VEIN_SIZE)));

        register(context, LIGNITE_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_LIGNITE_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.ligniteDeepslateVeinSize, WorldConfig.LIGNITE_DEEPSLATE_VEIN_SIZE)));

        register(context, TITANIUM_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_TITANIUM_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.titaniumDeepslateVeinSize, WorldConfig.TITANIUM_DEEPSLATE_VEIN_SIZE)));

        register(context, NITER_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_NITER_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.niterDeepslateVeinSize, WorldConfig.NITER_DEEPSLATE_VEIN_SIZE)));

        register(context, RARE_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_RARE_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.rareDeepslateVeinSize, WorldConfig.RARE_DEEPSLATE_VEIN_SIZE)));

        register(context, SULFUR_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_SULFUR_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.sulfurDeepslateVeinSize, WorldConfig.SULFUR_DEEPSLATE_VEIN_SIZE)));

        register(context, TUNGSTEN_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_TUNGSTEN_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.tungstenDeepslateVeinSize, WorldConfig.TUNGSTEN_DEEPSLATE_VEIN_SIZE)));


        register(context, THORIUM_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_THORIUM_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.thoriumDeepslateVeinSize, WorldConfig.THORIUM_DEEPSLATE_VEIN_SIZE)));

        register(context, URANIUM_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_URANIUM_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.uraniumDeepslateVeinSize, WorldConfig.URANIUM_DEEPSLATE_VEIN_SIZE)));

        register(context, BEDROCK_ORE, ModFeatures.BEDROCK_ORE.get(),
                new BedrockOreConfiguration(WorldConfig.BEDROCK_ORE_CHANCE, false));
        register(context, BEDROCK_ORE_NETHER, ModFeatures.BEDROCK_ORE.get(),
                new BedrockOreConfiguration(WorldConfig.BEDROCK_ORE_CHANCE, true));

        register(context, IRON_CLUSTER, Feature.ORE,
                new OreConfiguration(STONE_ORE_REPLACEABLES, ModBlocks.CLUSTER_IRON.get().defaultBlockState(), 6));

        register(context, TITANIUM_CLUSTER, Feature.ORE,
                new OreConfiguration(STONE_ORE_REPLACEABLES, ModBlocks.CLUSTER_TITANIUM.get().defaultBlockState(), 6));

        register(context, ALUMINIUM_CLUSTER, Feature.ORE,
                new OreConfiguration(STONE_ORE_REPLACEABLES, ModBlocks.CLUSTER_ALUMINIUM.get().defaultBlockState(), 6));

        register(context, COPPER_CLUSTER, Feature.ORE,
                new OreConfiguration(STONE_ORE_REPLACEABLES, ModBlocks.CLUSTER_COPPER.get().defaultBlockState(), 6));


        // Руды в гнейсе (1.7.10: target stone_gneiss, size 6, y 30..40)
        register(context, GNEISS_GAS_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_GNEISS_REPLACEABLES, ModBlocks.ORE_GNEISS_GAS.get().defaultBlockState())
                ), 10));
        register(context, GNEISS_IRON_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_GNEISS_REPLACEABLES, ModBlocks.ORE_GNEISS_IRON.get().defaultBlockState())
                ), 6));
        register(context, GNEISS_GOLD_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_GNEISS_REPLACEABLES, ModBlocks.ORE_GNEISS_GOLD.get().defaultBlockState())
                ), 6));
        register(context, GNEISS_URANIUM_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_GNEISS_REPLACEABLES, ModBlocks.ORE_GNEISS_URANIUM.get().defaultBlockState())
                ), 6));
        register(context, GNEISS_COPPER_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_GNEISS_REPLACEABLES, ModBlocks.ORE_GNEISS_COPPER.get().defaultBlockState())
                ), 6));
        register(context, GNEISS_ASBESTOS_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_GNEISS_REPLACEABLES, ModBlocks.ORE_GNEISS_ASBESTOS.get().defaultBlockState())
                ), 6));
        register(context, GNEISS_LITHIUM_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_GNEISS_REPLACEABLES, ModBlocks.ORE_GNEISS_LITHIUM.get().defaultBlockState())
                ), 6));
        register(context, GNEISS_RARE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_GNEISS_REPLACEABLES, ModBlocks.ORE_GNEISS_RARE.get().defaultBlockState())
                ), 6));

        // Газ — снова руда (как в 1.7.10: 1 газовый пузырь на GASBUBBLE_SPAWN чанков, y 30..40, size 32)
        register(context, GAS_FLAMMABLE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.GAS_FLAMMABLE.get().defaultBlockState())
                ), 32));

        // Взрывной газ — тоже руда (как в 1.7.10: 1 на EXPLOSIVEBUBBLE_SPAWN чанков, y 30..40, size 32)
        register(context, GAS_EXPLOSIVE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.GAS_EXPLOSIVE.get().defaultBlockState())
                ), 32));

        register(context, BROADCASTER, ModFeatures.BROADCASTER.get(),
                new BroadcasterConfiguration(ModBlocks.BROADCASTER_PC.get()));

        register(context, LANDMINE, ModFeatures.LANDMINE.get(),
                new LandmineConfiguration(ModBlocks.MINE_AP.get()));

        register(context, SAFE, ModFeatures.SAFE.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, SOYUZ_CAPSULE, ModFeatures.SOYUZ_CAPSULE.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, PINK_LOG, ModFeatures.PINK_LOG.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, STONE_KEYHOLE, ModFeatures.STONE_KEYHOLE.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, LANTERN_BEHEMOTH, ModFeatures.LANTERN_BEHEMOTH.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, STRUCTURE_CLEANUP, ModFeatures.STRUCTURE_CLEANUP.get(), NoneFeatureConfiguration.INSTANCE);



        register(context, CRATER, ModFeatures.CRATER.get(),
                new CraterConfiguration(
                        WorldConfig.RAD_FREQ, // частота
                        8,  // minSize
                        64, // maxSize
                        ModBlocks.SELLAFIELD_SLAKED.get(), // regolith
                        ModBlocks.SELLAFIELD_SLAKED.get(), // rock
                        ResourceKey.create(Registries.BIOME, ResLocation("desert")) // targetBiome
                ));

        register(context, ANTENNA, ModFeatures.ANTENNA.get(),
                new AntennaConfiguration(WorldConfig.ANTENNA_STRUCTURE));
        register(context, BARREL, ModFeatures.BARREL.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, ARCTIC_VAULT, ModFeatures.ARCTIC_VAULT.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, ANCIENT_TOMB, ModFeatures.ANCIENT_TOMB.get(),
                NoneFeatureConfiguration.INSTANCE);
        register(context, LIBRARY_DUNGEON, ModFeatures.LIBRARY_DUNGEON.get(), NoneFeatureConfiguration.INSTANCE);


        register(context, DUD, ModFeatures.DUD.get(),
                NoneFeatureConfiguration.INSTANCE);
        register(context, OIL_SPOT, ModFeatures.OIL_SPOT.get(),
                new OilSpotConfiguration(6, 30, true));
        register(context, GEYSER_WATER, ModFeatures.GEYSER.get(), GeyserConfiguration.water());
        register(context, GEYSER_CHLORINE, ModFeatures.GEYSER.get(), GeyserConfiguration.chlorine());
        register(context, GEYSER_VAPOR, ModFeatures.GEYSER.get(), GeyserConfiguration.vapor());
        register(context, GEYSER_NETHER, ModFeatures.GEYSER.get(), GeyserConfiguration.nether());
        register(context, GEYSER_LARGE_WATER, ModFeatures.GEYSER_LARGE.get(), GeyserConfiguration.water());
        register(context, GEYSER_LARGE_CHLORINE, ModFeatures.GEYSER_LARGE.get(), GeyserConfiguration.chlorine());
        register(context, GEYSER_LARGE_VAPOR, ModFeatures.GEYSER_LARGE.get(), GeyserConfiguration.vapor());
        register(context, GEYSER_LARGE_NETHER, ModFeatures.GEYSER_LARGE.get(), GeyserConfiguration.nether());

        register(context, METEORITE, ModFeatures.METEORITE.get(),
                MeteoriteConfiguration.staticMeteorite());
        register(context, SURFACE_SPOT, ModFeatures.SURFACE_SPOT.get(),
                new SurfaceSpotConfiguration(Blocks.SANDSTONE, 6, 0.4f));
        register(context, DEPTH_DEPOSIT_IRON, ModFeatures.DEPTH_DEPOSIT.get(),
                DepthDepositConfiguration.overworld(
                        ModBlocks.CLUSTER_DEPTH_IRON.get().defaultBlockState(),
                        ModBlocks.STONE_DEPTH.get().defaultBlockState(),
                        5, 0.6));

        register(context, DEPTH_DEPOSIT_TITANIUM, ModFeatures.DEPTH_DEPOSIT.get(),
                DepthDepositConfiguration.overworld(
                        ModBlocks.CLUSTER_DEPTH_TITANIUM.get().defaultBlockState(),
                        ModBlocks.STONE_DEPTH.get().defaultBlockState(),
                        5, 0.6));

        register(context, DEPTH_DEPOSIT_TUNGSTEN, ModFeatures.DEPTH_DEPOSIT.get(),
                DepthDepositConfiguration.overworld(
                        ModBlocks.CLUSTER_DEPTH_TUNGSTEN.get().defaultBlockState(),
                        ModBlocks.STONE_DEPTH.get().defaultBlockState(),
                        5, 0.6));

        register(context, DEPTH_DEPOSIT_CINNABAR, ModFeatures.DEPTH_DEPOSIT.get(),
                DepthDepositConfiguration.overworld(
                        ModBlocks.ORE_DEPTH_CINNEBAR.get().defaultBlockState(),
                        ModBlocks.STONE_DEPTH.get().defaultBlockState(),
                        5, 0.8));

        register(context, DEPTH_DEPOSIT_ZIRCONIUM, ModFeatures.DEPTH_DEPOSIT.get(),
                DepthDepositConfiguration.overworld(
                        ModBlocks.ORE_DEPTH_ZIRCONIUM.get().defaultBlockState(),
                        ModBlocks.STONE_DEPTH.get().defaultBlockState(),
                        5, 0.8));

        register(context, DEPTH_DEPOSIT_BORAX, ModFeatures.DEPTH_DEPOSIT.get(),
                DepthDepositConfiguration.overworld(
                        ModBlocks.ORE_DEPTH_BORAX.get().defaultBlockState(),
                        ModBlocks.STONE_DEPTH.get().defaultBlockState(),
                        5, 0.8));

// Nether
        register(context, DEPTH_DEPOSIT_NETHER_NEODYMIUM, ModFeatures.DEPTH_DEPOSIT.get(),
                DepthDepositConfiguration.nether(
                        ModBlocks.ORE_DEPTH_NETHER_NEODYMIUM.get().defaultBlockState(),
                        ModBlocks.STONE_DEPTH_NETHER.get().defaultBlockState(),
                        7, 0.6));



        register(context, BIOME_CAVE, ModFeatures.BIOME_CAVE.get(),
                BiomeCaveConfiguration.defaults());
        register(context, GLYPHID_HIVE, ModFeatures.GLYPHID_HIVE.get(),
                GlyphidHiveConfiguration.small(false));
        register(context, GLYPHID_HIVE_INFECTED, ModFeatures.GLYPHID_HIVE.get(),
                GlyphidHiveConfiguration.small(true));


        registerPlant(context, FLOWER_FOXGLOVE, ModBlocks.PLANT_FLOWER_FOXGLOVE.get());
        registerPlant(context, FLOWER_NIGHTSHADE, ModBlocks.PLANT_FLOWER_NIGHTSHADE.get());
        registerPlant(context, FLOWER_TOBACCO, ModBlocks.PLANT_FLOWER_TOBACCO.get());
        registerPlant(context, FLOWER_WEED, ModBlocks.PLANT_FLOWER_WEED.get());
        registerPlant(context, FLOWER_CD0, ModBlocks.PLANT_FLOWER_CD0.get());

        registerPlant(context, TALL_WEED, ModBlocks.PLANT_TALL_WEED.get());
        registerPlant(context, TALL_CD2, ModBlocks.PLANT_TALL_CD2.get());

        registerPlant(context, DEAD_PLANT_GENERIC, ModBlocks.PLANT_DEAD_GENERIC.get());
        register(context, REEDS, ModFeatures.REEDS.get(), NoneFeatureConfiguration.INSTANCE);

        // === НЕТЕР РУДЫ ===
        registerOre(context, NETHER_URANIUM_ORE, ModBlocks.ORE_NETHER_URANIUM.get().defaultBlockState(), Blocks.NETHERRACK.defaultBlockState(), 6, 0, 127);
        registerOre(context, NETHER_TUNGSTEN_ORE, ModBlocks.ORE_NETHER_TUNGSTEN.get().defaultBlockState(), Blocks.NETHERRACK.defaultBlockState(), 10, 0, 127);
        registerOre(context, NETHER_SULFUR_ORE, ModBlocks.ORE_NETHER_SULFUR.get().defaultBlockState(), Blocks.NETHERRACK.defaultBlockState(), 12, 0, 127);
        registerOre(context, NETHER_PHOSPHORUS_ORE, ModBlocks.ORE_NETHER_FIRE.get().defaultBlockState(), Blocks.NETHERRACK.defaultBlockState(), 6, 0, 127);
        registerOre(context, NETHER_COAL_ORE, ModBlocks.ORE_NETHER_COAL.get().defaultBlockState(), Blocks.NETHERRACK.defaultBlockState(), 32, 16, 96);
        registerOre(context, NETHER_COBALT_ORE, ModBlocks.ORE_NETHER_COBALT.get().defaultBlockState(), Blocks.NETHERRACK.defaultBlockState(), 6, 100, 26);
        if (GeneralConfig.ENABLE_PLUTONIUM_ORE) {
            registerOre(context, NETHER_PLUTONIUM_ORE, ModBlocks.ORE_NETHER_PLUTONIUM.get().defaultBlockState(), Blocks.NETHERRACK.defaultBlockState(), 4, 0, 127);
        }
        // Smoldering ore: только на незераке с воздухом сверху (как в 1.7.10)
        register(context, NETHER_SMOLDERING_ORE, ModFeatures.SMOLDERING_ORE.get(),
                NoneFeatureConfiguration.INSTANCE);

        // Тикит — руда в энде
        register(context, TIKITE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(new BlockMatchTest(Blocks.END_STONE), ModBlocks.ORE_TIKITE.get().defaultBlockState())
                ), 6));

        // Австралиум — только зона X/Z [-450..-350]
        register(context, AUSTRALIUM_ORE, ModFeatures.AUSTRALIUM.get(),
                NoneFeatureConfiguration.INSTANCE);

        // Колтан — крупное месторождение вокруг seed-зависимой точки
        register(context, COLTAN_DEPOSIT, ModFeatures.COLTAN_DEPOSIT.get(),
                NoneFeatureConfiguration.INSTANCE);

        // OreLayer3D: гематит/боксит/малахит
        register(context, HEMATITE_LAYER, ModFeatures.ORE_LAYER_3D.get(),
                new OreLayer3DConfiguration(
                        ModBlocks.STONE_RESOURCE.get().defaultBlockState()
                                .setValue(BlockResourceStone.TYPE, BlockResourceStone.EnumStoneType.HEMATITE),
                        0.04D, 0.25D, 230.0D, 0));
        register(context, BAUXITE_LAYER, ModFeatures.ORE_LAYER_3D.get(),
                new OreLayer3DConfiguration(
                        ModBlocks.STONE_RESOURCE.get().defaultBlockState()
                                .setValue(BlockResourceStone.TYPE, BlockResourceStone.EnumStoneType.BAUXITE),
                        0.03D, 0.15D, 300.0D, 1));
        register(context, MALACHITE_LAYER, ModFeatures.ORE_LAYER_3D.get(),
                new OreLayer3DConfiguration(
                        ModBlocks.STONE_RESOURCE.get().defaultBlockState()
                                .setValue(BlockResourceStone.TYPE, BlockResourceStone.EnumStoneType.MALACHITE),
                        0.1D, 0.15D, 275.0D, 2));




    }

    private static void registerOre(BootstapContext<ConfiguredFeature<?, ?>> context,
                                    ResourceKey<ConfiguredFeature<?, ?>> key,
                                    BlockState oreState,
                                    BlockState baseState,
                                    int veinSize,
                                    int minHeight,
                                    int maxHeight) {
        // Для ада используем тег NETHER_CARVER_REPLACEABLES или BASE_STONE_NETHER
        RuleTest ruleTest = new TagMatchTest(BlockTags.NETHER_CARVER_REPLACEABLES);

        context.register(key, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(
                        List.of(OreConfiguration.target(ruleTest, oreState)),
                        veinSize
                )
        ));
    }

    private static void registerPlant(BootstapContext<ConfiguredFeature<?, ?>> context,
                                      ResourceKey<ConfiguredFeature<?, ?>> key,
                                      Block block) {
        context.register(key, new ConfiguredFeature<>(
                ModFeatures.PLANT.get(),
                new SimpleBlockConfiguration(BlockStateProvider.simple(block.defaultBlockState()))
        ));
    }

    private static int getVeinSize(ForgeConfigSpec.IntValue config, int defaultValue) {
        return ConfigHelper.getSafeInt(config, defaultValue);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResLocation(MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}