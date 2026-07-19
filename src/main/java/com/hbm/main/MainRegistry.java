package com.hbm.main;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.fluid.ModFluids;
import com.hbm.config.*;
import com.hbm.creativetabs.HbmCreativeTabs;
import com.hbm.datagen.worldgen.MobSpawnBiomeModifier;
import com.hbm.datagen.worldgen.ModFeatures;

import com.hbm.datagen.worldgen.feature.BedrockOre;
import com.hbm.entity.ModEntities;
import com.hbm.handler.ArmorResistanceHandler;
import com.hbm.event.MiningEventHandler;

import com.hbm.handler.neutron.NeutronHandler;
import com.hbm.handler.pollution.PollutionCapability;
import com.hbm.handler.pollution.PollutionHandler;

import com.hbm.handler.radiation.RadiationCapability;
import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.hazard.HazardRegistration;
import com.hbm.hazard.HazardRegistry;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.container.ModContainers;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.recipes.*;
import com.hbm.items.*;
import com.hbm.items.tool.ItemTooling;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.loot.ModLootModifiers;
import com.hbm.network.PacketHandler;
import com.hbm.particle.ModParticles;
import com.hbm.potion.HbmPotion;
import com.hbm.quests.QuestManager;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.RefStrings;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static com.hbm.util.RefStrings.MODID;

@Mod(MODID)
public class MainRegistry {

    public static final Logger logger = LogManager.getLogger(MODID);

    public static CommonProxy proxy;
    public static int generalOverride = 0;
    public static int polaroidID = 1;

    public static File configHbmDir;
    public static File configDir;


    public MainRegistry(FMLJavaModLoadingContext context) {

        logger.info("HBM Nuclear Tech Mod loading...");

        proxy = DistExecutor.safeRunForDist(
                () -> ClientProxy::new,
                () -> ServerProxy::new
        );

        // === РЕГИСТРАЦИЯ CALLBACK ДЛЯ ЧАНКОВ ===
        ForgeChunkManager.setForcedChunkLoadingCallback(MODID, (level, ticket) -> {
            // Callback вызывается когда чанки форсятся/анфорсятся
            // Можно оставить пустым, если не нужна специальная логика
            logger.debug("Chunk loading callback triggered for mod: {}", MODID);
        });

        if(generalOverride > 0 && generalOverride < 19) {
            polaroidID = generalOverride;
        } else {
            RandomSource rand = RandomSource.create();
            do polaroidID = rand.nextInt(18) + 1;
            while (polaroidID == 4 || polaroidID == 9);
        }

        configDir = FMLPaths.CONFIGDIR.get().toFile();

        configHbmDir = new File(FMLPaths.CONFIGDIR.get().toFile(), RefStrings.MODID);
        if (!configHbmDir.exists() && !configHbmDir.mkdirs()) {
            logger.error("Failed to create config directory: {}", configHbmDir.getAbsolutePath());
        }

        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, GeneralConfig.register(), "hbm-general.toml");
        context.registerConfig(ModConfig.Type.COMMON, WorldConfig.register(), "hbm-world.toml");
        context.registerConfig(ModConfig.Type.COMMON, RadiationConfig.register(), "hbm-radiation.toml");
        context.registerConfig(ModConfig.Type.COMMON, ToolConfig.register(), "hbm-tool.toml");
        context.registerConfig(ModConfig.Type.COMMON, VersatileConfig.register(), "hbm-versatile.toml");
        context.registerConfig(ModConfig.Type.COMMON, BombConfig.register(), "hbm-bomb.toml");
        context.registerConfig(ModConfig.Type.COMMON, PotionConfig.register(), "hbm-potion.toml");
        context.registerConfig(ModConfig.Type.COMMON, MobConfig.register(), "hbm-mob.toml");
        context.registerConfig(ModConfig.Type.SERVER, ServerConfig.register(), "hbm-server.toml");
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.register(), "hbm-client.toml");

        modEventBus.addListener(this::registerBiomeModifierSerializers);

        ModFeatures.register(modEventBus);

        Fluids.HBM_FLUIDS.register(modEventBus);


        ModItems.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);

        ModAmmoItems.register(modEventBus);

        ModGunItems.register(modEventBus);
        ModArmorItems.register(modEventBus);
        ModToolItems.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);

        ModTileEntity.register(modEventBus);
        ModParticles.register(modEventBus);

        HbmPotion.EFFECTS.register(modEventBus);


        ModEntities.ENTITIES.register(modEventBus);

        ModContainers.MENUS.register(modEventBus);

        ModLootModifiers.register(modEventBus);

        HbmCreativeTabs.CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(new NeutronHandler());

        HazardRegistry.register();

        MinecraftForge.EVENT_BUS.register(RadiationCapability.class);
        MinecraftForge.EVENT_BUS.register(new RadiationEvents());

        MinecraftForge.EVENT_BUS.register(PollutionCapability.class);
        MinecraftForge.EVENT_BUS.register(new PollutionHandler());

        ModSounds.SOUNDS.register(modEventBus);


        // MiningEventHandler нужно зарегистрировать вручную,
        // так как он использует статический метод с @SubscribeEvent
        MinecraftForge.EVENT_BUS.addListener(MiningEventHandler::onBlockBroken);

        AnvilRecipes.updateSmithingTiersFromConfig();

        ModCriteriaTriggers.register();

        logger.info("HBM Nuclear Tech Mod loaded!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            GeneralConfig.load();
            ToolConfig.load();
            WorldConfig.load();
            RadiationConfig.load();

            BulletConfigRegistry.initLate();
            BulletConfigRegistry.linkAllItems();
            FalloutConfigJSON.initialize();
            FluidContainerRegistry.register();
            ItemTooling.register();
            ArmorResistanceHandler.init();
            PacketHandler.register();

            AnvilRecipes.register();
            CentrifugeRecipes.register();
            GasCentrifugeRecipes.register();
            CombinationRecipes.register();
            CrucibleRecipes.register();
            Mats.registerMaterialEntries();
            RotaryFurnaceRecipes.register();
            SolderingRecipes.register();
            ArcWelderRecipes.register();
            ShredderRecipes.register();
            CrystallizerRecipes.register();

            Fluids.init();
            HazardRegistration.registerAll();
            ArmorRegistry.registerAll();
            TileEntityNukeCustom.registerBombItems();

            BedrockOre.init();

            //Quests
            logger.info("Initializing quest system...");
            QuestManager.init();
            logger.info("Quest system initialized");

        });
    }

    private void registerBiomeModifierSerializers(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, helper -> helper.register("mob_spawn_biome_modifier", MobSpawnBiomeModifier.CODEC));
    }
}