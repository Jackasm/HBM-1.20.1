package com.hbm.handler;

import com.hbm.inventory.gui.CraftingOverviewScreen;
import com.hbm.inventory.gui.GUICalculator;
import com.hbm.inventory.gui.QuestBookScreen;
import com.hbm.items.IKeybindReceiver;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.network.PacketDispatcher;
import com.hbm.render.overlay.QuestOverlay;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HbmKeybinds {

    public static final String CATEGORY = "key.hbm.category";

    // Keybind definitions
    public static KeyMapping calculatorKey;
    public static KeyMapping helmetModeKey;
    public static KeyMapping jetpackKey;
    public static KeyMapping magnetKey;
    public static KeyMapping hudKey;
    public static KeyMapping dashKey;
    public static KeyMapping trainKey;
    public static KeyMapping qmaw;
    public static KeyMapping abilityCycle;
    public static KeyMapping abilityAlt;
    public static KeyMapping copyToolAlt;
    public static KeyMapping copyToolCtrl;
    public static KeyMapping reloadKey;
    public static KeyMapping gunPrimaryKey;
    public static KeyMapping gunSecondaryKey;
    public static KeyMapping gunTertiaryKey;
    public static KeyMapping gunAimKey;
    public static KeyMapping craneUpKey;
    public static KeyMapping craneDownKey;
    public static KeyMapping craneLeftKey;
    public static KeyMapping craneRightKey;
    public static KeyMapping craneLoadKey;
    public static KeyMapping questBookKey;
    public static KeyMapping craftingOverviewKey;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        calculatorKey = createKey("calculator", GLFW.GLFW_KEY_N);
        event.register(calculatorKey);

        trainKey = createKey("trainInv", GLFW.GLFW_KEY_N);
        event.register(trainKey);

        helmetModeKey = createKey("helmetModeKey", GLFW.GLFW_KEY_H);
        event.register(helmetModeKey);

        dashKey = createKey("dash", GLFW.GLFW_KEY_LEFT_SHIFT);
        event.register(dashKey);

        jetpackKey = createKey("toggleBack", GLFW.GLFW_KEY_C);
        event.register(jetpackKey);

        magnetKey = createKey("toggleMagnet", GLFW.GLFW_KEY_Z);
        event.register(magnetKey);

        hudKey = createKey("toggleHUD", GLFW.GLFW_KEY_V);
        event.register(hudKey);

        qmaw = createKey("qmaw", GLFW.GLFW_KEY_F1);
        event.register(qmaw);

        abilityCycle = createKey("ability", GLFW.GLFW_KEY_UNKNOWN);
        event.register(abilityCycle);

        abilityAlt = createKey("abilityAlt", GLFW.GLFW_KEY_LEFT_ALT);
        event.register(abilityAlt);

        copyToolAlt = createKey("copyToolAlt", GLFW.GLFW_KEY_LEFT_ALT);
        event.register(copyToolAlt);

        copyToolCtrl = createKey("copyToolCtrl", GLFW.GLFW_KEY_LEFT_CONTROL);
        event.register(copyToolCtrl);

        reloadKey = createKey("reload", GLFW.GLFW_KEY_R);
        event.register(reloadKey);

        gunPrimaryKey = createKey("gunPrimary", GLFW.GLFW_KEY_UNKNOWN);
        event.register(gunPrimaryKey);

        gunSecondaryKey = createKey("gunSecondary", GLFW.GLFW_KEY_UNKNOWN);
        event.register(gunSecondaryKey);

        gunTertiaryKey = createKey("gunTertitary", GLFW.GLFW_KEY_UNKNOWN);
        event.register(gunTertiaryKey);

        gunAimKey = createKey("gunAim", GLFW.GLFW_KEY_UNKNOWN);
        event.register(gunAimKey);

        craneUpKey = createKey("craneMoveUp", GLFW.GLFW_KEY_UP);
        event.register(craneUpKey);

        craneDownKey = createKey("craneMoveDown", GLFW.GLFW_KEY_DOWN);
        event.register(craneDownKey);

        craneLeftKey = createKey("craneMoveLeft", GLFW.GLFW_KEY_LEFT);
        event.register(craneLeftKey);

        craneRightKey = createKey("craneMoveRight", GLFW.GLFW_KEY_RIGHT);
        event.register(craneRightKey);

        craneLoadKey = createKey("craneLoad", GLFW.GLFW_KEY_ENTER);
        event.register(craneLoadKey);

        // Quests
        questBookKey = createKey("quest_book", GLFW.GLFW_KEY_TAB);
        event.register(questBookKey);

        craftingOverviewKey = createKey("crafting_overview", GLFW.GLFW_KEY_O);
        event.register(craftingOverviewKey);
    }

    private static KeyMapping createKey(String name, int keyCode) {
        return new KeyMapping(
                "key.hbm." + name,
                KeyConflictContext.IN_GAME,
                KeyModifier.NONE,
                InputConstants.Type.KEYSYM.getOrCreate(keyCode),
                HbmKeybinds.CATEGORY
        );
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientForgeEvents {

        private static long tabPressStart = 0;
        private static boolean tabPressed = false;
        private static boolean longPressActivated = false;
        private static final long LONG_PRESS_MS = 500;

        public static void resetTabState() {
            tabPressed = false;
            longPressActivated = false;
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            // Долгое нажатие Tab
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

            int keyCode = event.getKey();
            int action = event.getAction();

            // Обработка Tab (код 258)
            if (keyCode == 258) {
                // Если книга открыта, игнорируем Tab
                if (mc.screen instanceof QuestBookScreen) {
                    return;
                }

                if (action == GLFW.GLFW_PRESS) {
                    tabPressed = true;
                    tabPressStart = System.currentTimeMillis();
                    longPressActivated = false;
                    return;
                } else if (action == GLFW.GLFW_RELEASE && tabPressed) {
                    if (!longPressActivated) {
                        long duration = System.currentTimeMillis() - tabPressStart;
                        if (duration < LONG_PRESS_MS) {
                            QuestOverlay.showTemporary(5000);
                        }
                    }
                    tabPressed = false;
                    longPressActivated = false;
                    return;
                }
            }
            if (action == GLFW.GLFW_RELEASE && tabPressed) {
                if (!longPressActivated) {
                    long duration = System.currentTimeMillis() - tabPressStart;
                    if (duration < LONG_PRESS_MS) {
                        QuestOverlay.showTemporary(5000);
                    }
                }
                tabPressed = false;
                longPressActivated = false;
                return;
            }

            if (craftingOverviewKey.consumeClick()) {
                if (!(mc.screen instanceof CraftingOverviewScreen)) {
                    mc.setScreen(new CraftingOverviewScreen(mc.player));
                }
                return;
            }

            // Остальные клавиши передаём в стандартную обработку
            handleKeyInput(mc);
        }

        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            handleMouseInput(event, mc);
        }
    }

    private static void handleKeyInput(Minecraft mc) {
        // Calculator key
        if (calculatorKey.consumeClick()) {
            Objects.requireNonNull(mc.player).closeContainer();
            mc.setScreen(new GUICalculator());
        }

        // Handle keybind props
        handleProps(mc);
    }

    private static void handleMouseInput(InputEvent.MouseButton event, Minecraft mc) {
        Player player = mc.player;
        if (player == null) return;

        if (mc.screen != null) {
            return;
        }

        ItemStack held = player.getMainHandItem();

        // Обработка стрельбы из оружия
        if (!held.isEmpty() && held.getItem() instanceof GunItem) {
            // Левая кнопка мыши - стрельба
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                boolean pressed = event.getAction() == GLFW.GLFW_PRESS;

                event.setCanceled(true);

                HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
                boolean last = props.getKeyPressed(EnumKeybind.GUN_PRIMARY);
                if (last != pressed) {
                    props.setKeyPressed(EnumKeybind.GUN_PRIMARY, pressed);
                    PacketDispatcher.sendKeybind(EnumKeybind.GUN_PRIMARY, pressed);
                    onPressedClient(player, EnumKeybind.GUN_PRIMARY, pressed);
                }
                return;
            }

            // Правая кнопка мыши - прицеливание
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                boolean pressed = event.getAction() == GLFW.GLFW_PRESS;

                if (pressed) {
                    event.setCanceled(true);
                }

                HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
                boolean last = props.getKeyPressed(EnumKeybind.GUN_SECONDARY);
                if (last != pressed) {
                    props.setKeyPressed(EnumKeybind.GUN_SECONDARY, pressed);
                    PacketDispatcher.sendKeybind(EnumKeybind.GUN_SECONDARY, pressed);
                    onPressedClient(player, EnumKeybind.GUN_SECONDARY, pressed);
                }
                return;
            }

            // Средняя кнопка мыши
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                boolean pressed = event.getAction() == GLFW.GLFW_PRESS;

                if (pressed) {
                    event.setCanceled(true);

                    if (held.getItem() instanceof GunItem gun) {
                        GunConfig config = gun.getConfig(held, 0);

                        if (config.getPressTertiary(held) != null) {
                            PacketDispatcher.sendKeybind(EnumKeybind.GUN_AIM, true);
                            onPressedClient(player, EnumKeybind.GUN_AIM, true);
                        }
                    }
                }
                return;
            }
        }

        handleProps(mc);
    }

    public static void handleProps(Minecraft mc) {
        Player player = mc.player;
        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(Objects.requireNonNull(player));
        if (mc.screen != null) {
            return;
        }

        for (EnumKeybind key : EnumKeybind.values()) {
            boolean last = props.getKeyPressed(key);
            boolean current = getIsKeyPressed(key, mc);

            if (last != current) {
                if (key == EnumKeybind.ABILITY_CYCLE && mc.options.keyUse.isDown() &&
                        isMouseButton(abilityCycle)) {
                    continue;
                }

                props.setKeyPressed(key, current);
                PacketDispatcher.sendKeybind(key, current);
                onPressedClient(player, key, current);
            }
        }
    }

    public static void onPressedClient(Player player, EnumKeybind key, boolean state) {
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && held.getItem() instanceof IKeybindReceiver rec) {
            if (rec.canHandleKeybind(player, held, key)) {
                rec.handleKeybindClient(player, held, key, state);
            }
        }
    }

    private static boolean isMouseButton(KeyMapping key) {
        return key.getKey().getType() == InputConstants.Type.MOUSE;
    }

    public static boolean getIsKeyPressed(EnumKeybind key, Minecraft mc) {
        if (mc.player == null) return false;

        return switch (key) {
            case HELMET_MODE -> helmetModeKey.consumeClick();
            case JETPACK -> mc.options.keyJump.isDown();
            case TOGGLE_JETPACK -> jetpackKey.consumeClick();
            case TOGGLE_MAGNET -> magnetKey.consumeClick();
            case TOGGLE_HEAD -> hudKey.consumeClick();
            case DASH -> dashKey.isDown();
            case TRAIN -> trainKey.consumeClick();
            case CRANE_UP -> craneUpKey.isDown();
            case CRANE_DOWN -> craneDownKey.isDown();
            case CRANE_LEFT -> craneLeftKey.isDown();
            case CRANE_RIGHT -> craneRightKey.isDown();
            case CRANE_LOAD -> craneLoadKey.consumeClick();
            case ABILITY_CYCLE -> abilityCycle.isDown();
            case ABILITY_ALT -> abilityAlt.isDown();
            case TOOL_ALT -> copyToolAlt.isDown();
            case TOOL_CTRL -> copyToolCtrl.isDown();
            case GUN_PRIMARY -> gunPrimaryKey.isDown();
            case GUN_SECONDARY -> gunSecondaryKey.isDown();
            case GUN_TERTIARY -> gunTertiaryKey.isDown();
            case GUN_AIM -> gunAimKey.isDown();
            case RELOAD -> reloadKey.isDown();
        };
    }

    public enum EnumKeybind {
        HELMET_MODE,
        JETPACK,
        TOGGLE_JETPACK,
        TOGGLE_MAGNET,
        TOGGLE_HEAD,
        DASH,
        TRAIN,
        CRANE_UP,
        CRANE_DOWN,
        CRANE_LEFT,
        CRANE_RIGHT,
        CRANE_LOAD,
        ABILITY_CYCLE,
        ABILITY_ALT,
        TOOL_ALT,
        TOOL_CTRL,
        GUN_PRIMARY,
        GUN_SECONDARY,
        GUN_TERTIARY,
        GUN_AIM,
        RELOAD
    }
}