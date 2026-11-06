package com.hbm_addons.main;

import com.hbm.api.quest.QuestHooks;
import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.entity.mob.EntityEncounterVillager;
import com.hbm_addons.items.ItemQuestBook;
import com.hbm_addons.network.AddonNetwork;
import com.hbm_addons.quests.AddonQuestHooks;
import com.hbm_addons.quests.QuestManager;
import com.hbm_addons.util.EncounterPools;
import com.hbm.util.RefStrings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class AddonRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RefStrings.MODID);

    /** Предметы аддона: книга квестов. */
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RefStrings.MODID);

    public static final RegistryObject<Item> QUEST_BOOK =
            ITEMS.register("quest_book", ItemQuestBook::new);

    public static final RegistryObject<EntityType<EntityEncounterVillager>> ENCOUNTER_VILLAGER =
            ENTITIES.register("encounter_villager",
                    () -> EntityType.Builder
                            .of(EntityEncounterVillager::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(10)
                            .build("encounter_villager"));

    private AddonRegistry() {}

    /** Единственная точка входа для MainRegistry: остальное аддон подписывает сам. */
    public static void init(IEventBus modEventBus) {
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                AddonConfig.register(),
                "hbm-addons.toml");

        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        AddonMenus.MENUS.register(modEventBus);
        modEventBus.addListener(AddonRegistry::onAttributes);
        modEventBus.addListener(AddonRegistry::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AddonConfig.load();

            if (!AddonConfig.enableAddons) {
                return;
            }

            EncounterPools.init();

            AddonNetwork.registerPackets();

            // Шов для ядра: гайд-бук спрашивает про квесты только через него.
            QuestHooks.set(new AddonQuestHooks());

            if (AddonConfig.enableQuests) {
                QuestManager.init();
            }
        });
    }

    private static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ENCOUNTER_VILLAGER.get(),
                EntityEncounterVillager.createAttributes().build());
    }
}
