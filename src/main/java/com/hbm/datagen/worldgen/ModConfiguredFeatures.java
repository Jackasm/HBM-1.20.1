package com.hbm.datagen.worldgen;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockDeadPlant;
import com.hbm.blocks.generic.BlockNTMFlower;
import com.hbm.blocks.generic.BlockTallPlant;
import com.hbm.config.GeneralConfig;
import com.hbm.config.WorldConfig;
import com.hbm.datagen.worldgen.feature.*;
import com.hbm.datagen.worldgen.structure.RuinConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class ModConfiguredFeatures {
    private static final RuleTest STONE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    private static final RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

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
    public static final ResourceKey<ConfiguredFeature<?, ?>> ZINC_ORE = registerKey("zinc_ore");
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
    public static final ResourceKey<ConfiguredFeature<?, ?>> ZINC_DEEPSLATE_ORE = registerKey("zinc_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> THORIUM_DEEPSLATE_ORE = registerKey("thorium_deepslate_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> URANIUM_DEEPSLATE_ORE = registerKey("uranium_deepslate_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> BEDROCK_ORE = registerKey("bedrock_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> IRON_CLUSTER = registerKey("iron_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_CLUSTER = registerKey("titanium_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALUMINIUM_CLUSTER = registerKey("aluminium_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COPPER_CLUSTER = registerKey("copper_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OIL_BUBBLE = registerKey("oil_bubble");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GNEISS_GAS_ORE = registerKey("gneiss_gas_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GAS_BUBBLE = registerKey("gas_bubble");
    public static final ResourceKey<ConfiguredFeature<?, ?>> EXPLOSIVE_BUBBLE = registerKey("explosive_bubble");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BROADCASTER = registerKey("broadcaster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LANDMINE = registerKey("landmine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRATER = registerKey("crater");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANTENNA = registerKey("antenna");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUIN = registerKey("ruin");

    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_FOXGLOVE = registerKey("flower_foxglove");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_NIGHTSHADE = registerKey("flower_nightshade");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_TOBACCO = registerKey("flower_tobacco");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_WEED = registerKey("flower_weed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_CD0 = registerKey("flower_cd0");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_WEED = registerKey("tall_weed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_CD2 = registerKey("tall_cd2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_PLANT_GENERIC = registerKey("dead_plant_generic");

    // === НЕТЕР РУДЫ ===
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_URANIUM_ORE = registerKey("nether_uranium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_TUNGSTEN_ORE = registerKey("nether_tungsten_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_SULFUR_ORE = registerKey("nether_sulfur_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_PHOSPHORUS_ORE = registerKey("nether_phosphorus_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_COAL_ORE = registerKey("nether_coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_COBALT_ORE = registerKey("nether_cobalt_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_PLUTONIUM_ORE = registerKey("nether_plutonium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_SMOLDERING_ORE = registerKey("nether_smoldering_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_GEYSER = registerKey("nether_geyser");


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

        register(context, ZINC_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_ZINC.get().defaultBlockState())
                ), getVeinSize(WorldConfig.zincVeinSize, WorldConfig.ZINC_VEIN_SIZE)));

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

        register(context, ZINC_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_ZINC_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.zincDeepslateVeinSize, WorldConfig.ZINC_DEEPSLATE_VEIN_SIZE)));

        register(context, THORIUM_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_THORIUM_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.thoriumDeepslateVeinSize, WorldConfig.THORIUM_DEEPSLATE_VEIN_SIZE)));

        register(context, URANIUM_DEEPSLATE_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, ModBlocks.ORE_URANIUM_DEEPSLATE.get().defaultBlockState())
                ), getVeinSize(WorldConfig.uraniumDeepslateVeinSize, WorldConfig.URANIUM_DEEPSLATE_VEIN_SIZE)));

        register(context, BEDROCK_ORE, ModFeatures.BEDROCK_ORE.get(),
                new BedrockOreConfiguration(WorldConfig.BEDROCK_ORE_CHANCE));

        register(context, IRON_CLUSTER, Feature.ORE,
                new OreConfiguration(STONE_ORE_REPLACEABLES, ModBlocks.CLUSTER_IRON.get().defaultBlockState(), 6));

        register(context, TITANIUM_CLUSTER, Feature.ORE,
                new OreConfiguration(STONE_ORE_REPLACEABLES, ModBlocks.CLUSTER_TITANIUM.get().defaultBlockState(), 6));

        register(context, ALUMINIUM_CLUSTER, Feature.ORE,
                new OreConfiguration(STONE_ORE_REPLACEABLES, ModBlocks.CLUSTER_ALUMINIUM.get().defaultBlockState(), 6));

        register(context, COPPER_CLUSTER, Feature.ORE,
                new OreConfiguration(STONE_ORE_REPLACEABLES, ModBlocks.CLUSTER_COPPER.get().defaultBlockState(), 6));


        register(context, OIL_BUBBLE, ModFeatures.OIL_BUBBLE.get(), new OilBubbleConfiguration(
                new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                ModBlocks.ORE_OIL.get().defaultBlockState(),
                8, 16, false
        ));

        // Газ в сланце (обычная руда)
        register(context, GNEISS_GAS_ORE, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(STONE_ORE_REPLACEABLES, ModBlocks.ORE_GNEISS_GAS.get().defaultBlockState())
                ), 10)); // vein size 10

        // Газовые пузыри
        register(context, GAS_BUBBLE, ModFeatures.GAS_BUBBLE.get(),
                new OilBubbleConfiguration(
                        new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                        ModBlocks.GAS_FLAMMABLE.get().defaultBlockState(),
                        8, 16, false
                ));

        register(context, EXPLOSIVE_BUBBLE, ModFeatures.EXPLOSIVE_BUBBLE.get(),
                new OilBubbleConfiguration(
                        new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                        ModBlocks.GAS_EXPLOSIVE.get().defaultBlockState(),
                        8, 16, false
                ));

        register(context, BROADCASTER, ModFeatures.BROADCASTER.get(),
                new BroadcasterConfiguration(ModBlocks.BROADCASTER_PC.get()));

        register(context, LANDMINE, ModFeatures.LANDMINE.get(),
                new LandmineConfiguration(ModBlocks.MINE_AP.get()));

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

        register(context, RUIN, ModFeatures.RUIN.get(),
                new RuinConfiguration(WorldConfig.RUIN_STRUCTURE));

        registerFlower(context, FLOWER_FOXGLOVE, BlockNTMFlower.EnumFlowerType.FOXGLOVE.ordinal());
        registerFlower(context, FLOWER_NIGHTSHADE, BlockNTMFlower.EnumFlowerType.NIGHTSHADE.ordinal());
        registerFlower(context, FLOWER_TOBACCO, BlockNTMFlower.EnumFlowerType.TOBACCO.ordinal());
        registerFlower(context, FLOWER_WEED, BlockNTMFlower.EnumFlowerType.WEED.ordinal());
        registerFlower(context, FLOWER_CD0, BlockNTMFlower.EnumFlowerType.CD0.ordinal());

        registerTallPlant(context, TALL_WEED, BlockTallPlant.EnumTallFlower.WEED);
        registerTallPlant(context, TALL_CD2, BlockTallPlant.EnumTallFlower.CD2);

        registerDeadPlant(context, DEAD_PLANT_GENERIC, BlockDeadPlant.EnumDeadPlantType.GENERIC.ordinal());

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
// Smoldering ore (30 блоков на чанк)
        registerSmoldering(context, NETHER_SMOLDERING_ORE);
// Geyser
        registerGeyser(context, NETHER_GEYSER);

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

    private static void registerSmoldering(BootstapContext<ConfiguredFeature<?, ?>> context,
                                           ResourceKey<ConfiguredFeature<?, ?>> key) {
        // Заменяем REPLACE_SINGLE на ORE с veinSize = 1
        // CountPlacement будет управлять количеством блоков
        context.register(key, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(
                        List.of(OreConfiguration.target(
                                new TagMatchTest(BlockTags.NETHER_CARVER_REPLACEABLES),
                                ModBlocks.ORE_NETHER_SMOLDERING.get().defaultBlockState()
                        )),
                        1 // размер жилы 1
                )
        ));
    }

    private static void registerGeyser(BootstapContext<ConfiguredFeature<?, ?>> context,
                                       ResourceKey<ConfiguredFeature<?, ?>> key) {
        // Geyser можно заменить на Feature.SIMPLE_BLOCK
        // В 1.20.1 SIMPLE_BLOCK существует
        context.register(key, new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(ModBlocks.GEYSIR_NETHER.get().defaultBlockState())
                )
        ));
    }

    private static void registerFlower(BootstapContext<ConfiguredFeature<?, ?>> context,
                                       ResourceKey<ConfiguredFeature<?, ?>> key,
                                       int meta) {
        BlockState state = ModBlocks.PLANT_FLOWER.get().defaultBlockState()
                .setValue(BlockNTMFlower.META, meta);

        context.register(key, new ConfiguredFeature<>(
                ModFeatures.PLANT.get(),
                new SimpleBlockConfiguration(BlockStateProvider.simple(state))
        ));
    }

    private static void registerTallPlant(BootstapContext<ConfiguredFeature<?, ?>> context,
                                          ResourceKey<ConfiguredFeature<?, ?>> key,
                                          BlockTallPlant.EnumTallFlower type) {
        BlockState state = ModBlocks.PLANT_TALL.get().defaultBlockState()
                .setValue(BlockTallPlant.HALF, DoubleBlockHalf.LOWER)
                .setValue(BlockTallPlant.TYPE, type);

        context.register(key, new ConfiguredFeature<>(
                ModFeatures.PLANT.get(),
                new SimpleBlockConfiguration(BlockStateProvider.simple(state))
        ));
    }

    private static void registerDeadPlant(BootstapContext<ConfiguredFeature<?, ?>> context,
                                          ResourceKey<ConfiguredFeature<?, ?>> key,
                                          int meta) {
        BlockState state = ModBlocks.PLANT_DEAD.get().defaultBlockState()
                .setValue(BlockDeadPlant.META, meta);

        context.register(key, new ConfiguredFeature<>(
                ModFeatures.PLANT.get(),
                new SimpleBlockConfiguration(BlockStateProvider.simple(state))
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