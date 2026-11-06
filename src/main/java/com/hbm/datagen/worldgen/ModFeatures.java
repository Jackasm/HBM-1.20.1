package com.hbm.datagen.worldgen;

import com.hbm.datagen.worldgen.feature.*;
import com.hbm.datagen.worldgen.feature.config.*;
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

    // OIL_BUBBLE, GAS_BUBBLE и EXPLOSIVE_BUBBLE удалены: нефть и песок теперь структуры,
    // газ (обычный и взрывной) снова генерируется как руда.
    public static final RegistryObject<BroadcasterFeature> BROADCASTER =
            FEATURES.register("broadcaster", () -> new BroadcasterFeature(BroadcasterConfiguration.CODEC));
    public static final RegistryObject<LandmineFeature> LANDMINE =
            FEATURES.register("landmine", () -> new LandmineFeature(LandmineConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SAFE =
            FEATURES.register("safe", () -> new SafeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SOYUZ_CAPSULE =
            FEATURES.register("soyuz_capsule", () -> new SoyuzCapsuleFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PINK_LOG =
            FEATURES.register("pink_log", () -> new PinkLogFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> STONE_KEYHOLE =
            FEATURES.register("stone_keyhole", () -> new StoneKeyholeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> LANTERN_BEHEMOTH =
            FEATURES.register("lantern_behemoth", () -> new LanternBehemothFeature(NoneFeatureConfiguration.CODEC));

    /** Чистка габарита NBT-структур после декорации (#102). */
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> STRUCTURE_CLEANUP =
            FEATURES.register("structure_cleanup", () -> new StructureCleanupFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<CraterFeature> CRATER =
            FEATURES.register("crater", () -> new CraterFeature(CraterConfiguration.CODEC));
    public static final RegistryObject<PlantFeature> PLANT =
            FEATURES.register("plant", () -> new PlantFeature(SimpleBlockConfiguration.CODEC));

    public static final RegistryObject<AntennaFeature> ANTENNA =
            FEATURES.register("antenna", () -> new AntennaFeature(AntennaConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BARREL =
            FEATURES.register("barrel", () -> new BarrelFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ARCTIC_VAULT =
            FEATURES.register("arctic_vault", () -> new ArcticVaultFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ANCIENT_TOMB =
            FEATURES.register("ancient_tomb",
                    () -> new AncientTombFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> LIBRARY_DUNGEON =
            FEATURES.register("library_dungeon", () -> new LibraryDungeonFeature(NoneFeatureConfiguration.CODEC));


    public static final RegistryObject<Feature<NoneFeatureConfiguration>> DUD =
            FEATURES.register("dud", () -> new DudFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SMOLDERING_ORE =
            FEATURES.register("smoldering_ore", () -> new SmolderingOreFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> REEDS =
            FEATURES.register("reeds", () -> new ReedsFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> AUSTRALIUM =
            FEATURES.register("australium", () -> new AustraliumFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> COLTAN_DEPOSIT =
            FEATURES.register("coltan_deposit", () -> new ColtanDepositFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<OreLayer3DConfiguration>> ORE_LAYER_3D =
            FEATURES.register("ore_layer_3d", () -> new OreLayer3DFeature(OreLayer3DConfiguration.CODEC));
    public static final RegistryObject<Feature<OilSpotConfiguration>> OIL_SPOT =
            FEATURES.register("oil_spot", () -> new OilSpotFeature(OilSpotConfiguration.CODEC));
    public static final RegistryObject<Feature<GeyserConfiguration>> GEYSER =
            FEATURES.register("geyser", () -> new GeyserFeature(GeyserConfiguration.CODEC));
    public static final RegistryObject<Feature<GeyserConfiguration>> GEYSER_LARGE =
            FEATURES.register("geyser_large", () -> new GeyserLargeFeature(GeyserConfiguration.CODEC));

    public static final RegistryObject<Feature<SurfaceSpotConfiguration>> SURFACE_SPOT =
            FEATURES.register("surface_spot", () -> new SurfaceSpotFeature(SurfaceSpotConfiguration.CODEC));
    public static final RegistryObject<Feature<MeteoriteConfiguration>> METEORITE =
            FEATURES.register("meteorite", () -> new MeteoriteFeature(MeteoriteConfiguration.CODEC));
    public static final RegistryObject<Feature<DepthDepositConfiguration>> DEPTH_DEPOSIT =
            FEATURES.register("depth_deposit",
                    () -> new DepthDepositFeature(DepthDepositConfiguration.CODEC));

    public static final RegistryObject<Feature<BiomeCaveConfiguration>> BIOME_CAVE =
            FEATURES.register("biome_cave", () -> new BiomeCaveFeature(BiomeCaveConfiguration.CODEC));
    public static final RegistryObject<Feature<GlyphidHiveConfiguration>> GLYPHID_HIVE =
            FEATURES.register("glyphid_hive",
                    () -> new GlyphidHiveFeature(GlyphidHiveConfiguration.CODEC));


    public static final RegistryObject<Feature<BedrockOreConfiguration>> BEDROCK_ORE = FEATURES.register("bedrock_ore",
            () -> new BedrockOreFeature(BedrockOreConfiguration.CODEC));






    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}