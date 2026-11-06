package com.hbm_addons.client;

import com.hbm.handler.HbmKeybinds;
import com.hbm.util.RefStrings;
import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.inventory.gui.QuestBookScreen;
import com.hbm_addons.render.overlay.QuestOverlay;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Хоткей квест-книги: долгое нажатие Tab открывает книгу, короткое — плашку квестов.
 * Раньше жил в HbmKeybinds ядра, теперь целиком на стороне аддона.
 */
public final class AddonInput {

    private static final long LONG_PRESS_MS = 500;

    private static KeyMapping questBookKey;
    private static long tabPressStart;
    private static boolean tabPressed;
    private static boolean longPressActivated;

    private AddonInput() {
    }

    public static void resetTabState() {
        tabPressed = false;
        longPressActivated = false;
    }

    @Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModEvents {

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            questBookKey = new KeyMapping(
                    "key.hbm.quest_book",
                    KeyConflictContext.IN_GAME,
                    KeyModifier.NONE,
                    InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_TAB),
                    HbmKeybinds.CATEGORY
            );
            event.register(questBookKey);
        }
    }

    @Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeEvents {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (!AddonConfig.enableQuests) return;

            if (tabPressed && !longPressActivated && !(mc.screen instanceof QuestBookScreen)) {
                if (System.currentTimeMillis() - tabPressStart >= LONG_PRESS_MS) {
                    longPressActivated = true;
                    mc.setScreen(new QuestBookScreen(mc.player));
                    tabPressed = false;
                }
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (!AddonConfig.enableQuests) return;

            if (event.getKey() != GLFW.GLFW_KEY_TAB) return;

            if (mc.screen instanceof QuestBookScreen) return;

            int action = event.getAction();

            if (action == GLFW.GLFW_PRESS) {
                tabPressed = true;
                tabPressStart = System.currentTimeMillis();
                longPressActivated = false;
            } else if (action == GLFW.GLFW_RELEASE && tabPressed) {
                if (!longPressActivated) {
                    long duration = System.currentTimeMillis() - tabPressStart;
                    if (duration < LONG_PRESS_MS) {
                        QuestOverlay.showTemporary(5000);
                    }
                }
                tabPressed = false;
                longPressActivated = false;
            }
        }
    }
}
