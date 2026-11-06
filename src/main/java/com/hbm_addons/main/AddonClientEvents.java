package com.hbm_addons.main;

import com.hbm.render.overlay.OverlayManager;
import com.hbm.util.RefStrings;
import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.inventory.gui.EncounterTradeScreen;
import com.hbm_addons.render.entity.mob.EncounterVillagerRenderer;
import com.hbm_addons.render.overlay.QuestOverlay;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Клиентская регистрация контента аддона: рендерер, экран, оверлей. */
@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class AddonClientEvents {

    private AddonClientEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AddonRegistry.ENCOUNTER_VILLAGER.get(), EncounterVillagerRenderer::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            OverlayManager.registerProvider(new QuestOverlay());
            MenuScreens.register(AddonMenus.ENCOUNTER_TRADE.get(), EncounterTradeScreen::new);
        });
    }
}
