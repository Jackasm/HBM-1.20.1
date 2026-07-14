package com.hbm.items.weapon.sedna;

import com.hbm.config.GeneralConfig;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.HbmKeybinds;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.IKeybindReceiver;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.items.weapon.sedna.hud.AmmoCounterHUD;
import com.hbm.items.weapon.sedna.hud.DurabilityBarHUD;
import com.hbm.items.weapon.sedna.hud.IHUDComponent;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.items.weapon.sedna.mags.MagazineInfinite;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.GunAnimationPacket;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.item.GunItemRenderer;
import com.hbm.util.BobMathUtil;
import com.hbm.util.EnumUtil;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber
public class GunItem extends Item implements IKeybindReceiver {

    // NBT Keys
    public static final String KEY_DRAWN = "drawn";
    public static final String KEY_AIMING = "aiming";
    public static final String KEY_MODE = "mode_";
    public static final String KEY_WEAR = "wear_";
    public static final String KEY_TIMER = "timer_";
    public static final String KEY_STATE = "state_";
    public static final String KEY_PRIMARY = "mouse1_";
    public static final String KEY_SECONDARY = "mouse2_";
    public static final String KEY_TERTIARY = "mouse3_";
    public static final String KEY_RELOAD = "reload_";
    public static final String KEY_LASTANIM = "lastanim_";
    public static final String KEY_ANIMTIMER = "animtimer_";
    public static final String KEY_LOCKONTARGET = "lockontarget";
    public static final String KEY_LOCKEDON = "lockedon";
    public static final String KEY_CANCELRELOAD = "cancel";
    public static final String KEY_EQUIPPED = "equipped";

    // Recoil system (client-side)
    public static float recoilVertical = 0;
    public static float recoilHorizontal = 0;
    public static float recoilDecay = 0.75F;
    public static float recoilRebound = 0.25F;
    public static float offsetVertical = 0;
    public static float offsetHorizontal = 0;

    // Sound tracking
    public static ConcurrentHashMap<LivingEntity, Object> loopedSounds = new ConcurrentHashMap<>();

    // Aiming interpolation
    public static float prevAimingProgress;
    public static float aimingProgress;

    // Formatting
    public static final DecimalFormatSymbols SYMBOLS_US = new DecimalFormatSymbols(Locale.US);
    public static final DecimalFormat FORMAT_DMG = new DecimalFormat("#.##", SYMBOLS_US);

    // Class members
    protected final GunConfig[] configs;
    public final WeaponQuality quality;
    public Function<ItemStack, String> nameMutator;
    public final List<ComparableStack> recognizedMods = new ArrayList<>();
    public final long[] lastShot;
    public double shotRand = 0D;
    public ItemStack defaultAmmo;

    public static final ResourceLocation overlay_misc = ResLocation.ResLocation(RefStrings.MODID, "textures/misc/overlay_misc.png");

    public interface IGunWithAdvancedHUD {
        @OnlyIn(Dist.CLIENT)
        void renderAdvancedHUD(RenderGuiOverlayEvent.Pre event, VanillaGuiOverlay type, Player player, ItemStack stack);
    }

    public GunItem(WeaponQuality quality, GunConfig... configs) {
        super(new Properties()
                .stacksTo(1)
                .durability(100)
                .setNoRepair());

        this.quality = quality;
        this.configs = configs;
        this.lastShot = new long[configs.length];

        for (int i = 0; i < configs.length; i++) {
            configs[i].index = i;
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new GunItemRenderer();
            }
        });
    }


    @OnlyIn(Dist.CLIENT)
    public void renderAdvancedHUD(RenderGuiOverlayEvent.Pre event, VanillaGuiOverlay type, Player player, ItemStack stack) {
        // Базовая реализация (может быть пустой)
        // Или можно перенести логику из renderHUD сюда
    }

    public enum WeaponQuality {
        A_SIDE,
        B_SIDE,
        LEGENDARY,
        SPECIAL,
        UTILITY,
        SECRET,
        DEBUG
    }

    public enum GunState {
        DRAWING,    // Forced delay where nothing can be done
        IDLE,       // Gun is ready to fire or reload
        COOLDOWN,   // Forced delay, but with option for refire
        RELOADING,  // Forced delay after which reload happens
        JAMMED,      // Forced delay due to jamming
        CYCLING
    }

    // Getters for NBT data
    public static boolean getIsDrawn(ItemStack stack) {
        return getBoolean(stack, KEY_DRAWN);
    }

    public static void setIsDrawn(ItemStack stack, boolean value) {
        setBoolean(stack, KEY_DRAWN, value);
    }

    public static int getTimer(ItemStack stack, int index) {
        return getInt(stack, KEY_TIMER + index);
    }

    public static void setTimer(ItemStack stack, int index, int value) {
        setInt(stack, KEY_TIMER + index, value);
    }

    public static GunState getState(ItemStack stack, int index) {
        byte stateByte = getByte(stack, KEY_STATE + index);
        return EnumUtil.grabEnumSafely(GunState.class, stateByte);
    }

    public static void setState(ItemStack stack, int index, GunState value) {
        setByte(stack, KEY_STATE + index, (byte) value.ordinal());
    }

    public static int getMode(ItemStack stack, int index) {
        return getInt(stack, KEY_MODE + index);
    }

    public static void setMode(ItemStack stack, int index, int value) {
        setInt(stack, KEY_MODE + index, value);
    }

    public static boolean getIsAiming(ItemStack stack) {
        return getBoolean(stack, KEY_AIMING);
    }

    public static void setIsAiming(ItemStack stack, boolean value) {
        setBoolean(stack, KEY_AIMING, value);
    }

    public static float getWear(ItemStack stack, int index) {
        return getFloat(stack, KEY_WEAR + index);
    }

    public static void setWear(ItemStack stack, int index, float value) {
        setFloat(stack, KEY_WEAR + index, value);
    }

    public static int getLockonTarget(ItemStack stack) {
        return getInt(stack, KEY_LOCKONTARGET);
    }

    public static void setLockonTarget(ItemStack stack, int value) {
        setInt(stack, KEY_LOCKONTARGET, value);
    }

    public static boolean getIsLockedOn(ItemStack stack) {
        return getBoolean(stack, KEY_LOCKEDON);
    }

    public static void setIsLockedOn(ItemStack stack, boolean value) {
        setBoolean(stack, KEY_LOCKEDON, value);
    }

    public static AnimationEnums.GunAnimation getLastAnim(ItemStack stack, int index) {
        int animId = getInt(stack, KEY_LASTANIM + index);
        return EnumUtil.grabEnumSafely(AnimationEnums.GunAnimation.class, animId);
    }

    public static void setLastAnim(ItemStack stack, int index, AnimationEnums.GunAnimation value) {
        setInt(stack, KEY_LASTANIM + index, value.ordinal());
    }

    public static int getAnimTimer(ItemStack stack, int index) {
        return getInt(stack, KEY_ANIMTIMER + index);
    }

    public static void setAnimTimer(ItemStack stack, int index, int value) {
        setInt(stack, KEY_ANIMTIMER + index, value);
    }

    public static boolean getPrimary(ItemStack stack, int index) {
        return getBoolean(stack, KEY_PRIMARY + index);
    }

    public static void setPrimary(ItemStack stack, int index, boolean value) {
        setBoolean(stack, KEY_PRIMARY + index, value);
    }

    public static boolean getSecondary(ItemStack stack, int index) {
        return getBoolean(stack, KEY_SECONDARY + index);
    }

    public static void setSecondary(ItemStack stack, int index, boolean value) {
        setBoolean(stack, KEY_SECONDARY + index, value);
    }

    public static boolean getTertiary(ItemStack stack, int index) {
        return getBoolean(stack, KEY_TERTIARY + index);
    }

    public static void setTertiary(ItemStack stack, int index, boolean value) {
        setBoolean(stack, KEY_TERTIARY + index, value);
    }

    public static boolean getReloadKey(ItemStack stack, int index) {
        return getBoolean(stack, KEY_RELOAD + index);
    }

    public static void setReloadKey(ItemStack stack, int index, boolean value) {
        setBoolean(stack, KEY_RELOAD + index, value);
    }

    public static boolean getReloadCancel(ItemStack stack) {
        return getBoolean(stack, KEY_CANCELRELOAD);
    }

    public static void setReloadCancel(ItemStack stack, boolean value) {
        setBoolean(stack, KEY_CANCELRELOAD, value);
    }

    public static boolean getIsEquipped(ItemStack stack) {
        return getBoolean(stack, KEY_EQUIPPED);
    }

    public static void setIsEquipped(ItemStack stack, boolean value) {
        setBoolean(stack, KEY_EQUIPPED, value);
    }

    // NBT helper methods
    public static int getInt(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key) ? tag.getInt(key) : 0;
    }

    public static void setInt(ItemStack stack, String key, int value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(key, value);
    }

    public static float getFloat(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key) ? tag.getFloat(key) : 0;
    }

    public static void setFloat(ItemStack stack, String key, float value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat(key, value);
    }

    public static byte getByte(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key) ? tag.getByte(key) : 0;
    }

    public static void setByte(ItemStack stack, String key, byte value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putByte(key, value);
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key) && tag.getBoolean(key);
    }

    public static void setBoolean(ItemStack stack, String key, boolean value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(key, value);
    }

    public GunItem setDefaultAmmo(BulletConfig ammo, int amount) {
        this.defaultAmmo = new ItemStack(BulletConfigRegistry.ammo_standard, amount);
        this.defaultAmmo.setDamageValue(ammo.id);
        return this;
    }

    public static String getString(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key) ? tag.getString(key) : "";
    }

    public static void setString(ItemStack stack, String key, String value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(key, value);
    }

    // Guncfg getter
    public GunConfig getConfig(ItemStack stack, int index) {
        GunConfig cfg = configs[index];
        if (stack == null) return cfg;
        return WeaponModManager.eval(cfg, stack, "O_GUNCONFIG_" + index, this, index);
    }

    public int getConfigCount() {
        return configs.length;
    }

    // Name mutator
    public GunItem setNameMutator(Function<ItemStack, String> mutator) {
        this.nameMutator = mutator;
        return this;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (this.nameMutator != null) {
            String unloc = this.nameMutator.apply(stack);
            if (unloc != null) {
                return Component.translatable(unloc);
            }
        }
        return super.getName(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        Minecraft mc = null;
        Player localPlayer = null;
        Container playerContainer = null;
        if (level != null && level.isClientSide()) {
            mc = Minecraft.getInstance();
            if (mc.player != null) {
                localPlayer = mc.player;
                playerContainer = mc.player.getInventory();
            }
        }

        for (int i = 0; i < configs.length; i++) {
            GunConfig config = getConfig(stack, i);
            for (Receiver rec : config.getReceivers(stack)) {
                IMagazine mag = Objects.requireNonNull(rec).getMagazine(stack);
                if (!(mag instanceof MagazineInfinite)) {
                    ItemStack icon = ItemStack.EMPTY;
                    String ammoState = "N/A";
                    // Передаем localPlayer вместо null
                    if (localPlayer != null) {
                        icon = Objects.requireNonNull(mag).getIconForHUD(stack, localPlayer);
                        ammoState = mag.reportAmmoStateForHUD(stack, localPlayer);
                    }
                    if (!icon.isEmpty()) {
                        tooltip.add(Component.translatable("gui.weapon.ammo")
                                .append(": ")
                                .append(icon.getHoverName())
                                .append(" ")
                                .append(Component.literal(ammoState)));
                    }
                }

                float dmg = rec.getBaseDamage(stack);
                tooltip.add(Component.translatable("gui.weapon.baseDamage")
                        .append(": ")
                        .append(Component.literal(FORMAT_DMG.format(dmg))));


                if (Objects.requireNonNull(mag).getType(stack, playerContainer) instanceof BulletConfig) {
                    BulletConfig bullet = (BulletConfig) mag.getType(stack, playerContainer);
                    int min = (int) (bullet.projectilesMin * rec.getSplitProjectiles(stack));
                    int max = (int) (bullet.projectilesMax * rec.getSplitProjectiles(stack));

                    MutableComponent damageText = Component.translatable("gui.weapon.damageWithAmmo")
                            .append(": ")
                            .append(Component.literal(FORMAT_DMG.format(dmg * bullet.damageMult)));

                    if (min > 1) {
                        if (min != max) {
                            damageText = damageText.append(Component.literal(" x" + min + "-" + max));
                        } else {
                            damageText = damageText.append(Component.literal(" x" + min));
                        }
                    }

                    tooltip.add(damageText);
                }
            }

            float maxDura = config.getDurability(stack);
            if (maxDura > 0) {
                int dura = (int) (((maxDura - getWear(stack, i)) * 100) / maxDura);
                dura = Math.max(0, Math.min(100, dura));
                tooltip.add(Component.translatable("gui.weapon.condition")
                        .append(": " + dura + "%"));
            }

            // Show installed mods
            ListTag modList = stack.getOrCreateTag().getList("mods", Tag.TAG_STRING);
            for (int j = 0; j < modList.size(); j++) {
                ResourceLocation modId = ResLocation.ResLocation(modList.getString(j));
                tooltip.add(Component.literal("§e" + modId));
            }
        }

        // Quality indicator
        ChatFormatting color;
        String qualityKey;
        switch (this.quality) {
            case A_SIDE -> { color = ChatFormatting.YELLOW; qualityKey = "gui.weapon.quality.aside"; }
            case B_SIDE -> { color = ChatFormatting.GOLD; qualityKey = "gui.weapon.quality.bside"; }
            case LEGENDARY -> { color = ChatFormatting.RED; qualityKey = "gui.weapon.quality.legendary"; }
            case SPECIAL -> { color = ChatFormatting.AQUA; qualityKey = "gui.weapon.quality.special"; }
            case UTILITY -> { color = ChatFormatting.GREEN; qualityKey = "gui.weapon.quality.utility"; }
            case SECRET -> {
                color = BobMathUtil.getBlink() ? ChatFormatting.DARK_RED : ChatFormatting.RED;
                qualityKey = "gui.weapon.quality.secret";
            }
            case DEBUG -> {
                color = BobMathUtil.getBlink() ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
                qualityKey = "gui.weapon.quality.debug";
            }
            default -> { color = ChatFormatting.WHITE; qualityKey = ""; }
        }

        if (!qualityKey.isEmpty()) {
            tooltip.add(Component.translatable(qualityKey).withStyle(color));
        }


        if (mc != null && mc.screen != null && mc.screen.getTitle().getString().contains("Weapon Table") &&
                !recognizedMods.isEmpty()) {
            tooltip.add(Component.translatable("gui.weapon.accepts").withStyle(ChatFormatting.RED));
            for (ComparableStack comp : recognizedMods) {
                tooltip.add(Component.literal("  " + comp.toStack().getHoverName().getString())
                        .withStyle(ChatFormatting.RED));
            }
        }
    }

    // Key handling
    public boolean canHandleKeybind(KeybindType keybind) {
        return keybind == KeybindType.GUN_PRIMARY || keybind == KeybindType.GUN_SECONDARY ||
                keybind == KeybindType.GUN_TERTIARY || keybind == KeybindType.GUN_AIM || keybind == KeybindType.RELOAD;
    }

    @Override
    public boolean canHandleKeybind(Player player, ItemStack stack, HbmKeybinds.EnumKeybind key) {
        // Конвертируем EnumKeybind в KeybindType
        KeybindType gunKeybind = convertToKeybindType(key);
        return gunKeybind != null && canHandleKeybind(gunKeybind);
    }

    public static KeybindType convertToKeybindType(HbmKeybinds.EnumKeybind key) {
        return switch (key) {
            case RELOAD -> KeybindType.RELOAD;
            case GUN_PRIMARY -> KeybindType.GUN_PRIMARY;
            case GUN_SECONDARY -> KeybindType.GUN_SECONDARY;
            case GUN_TERTIARY -> KeybindType.GUN_TERTIARY;
            case GUN_AIM -> KeybindType.GUN_AIM;
            default -> null;
        };
    }

    @Override
    public void handleKeybind(Player player, ItemStack stack, HbmKeybinds.EnumKeybind key, boolean newState) {
        KeybindType gunKeybind = convertToKeybindType(key);
        if (gunKeybind != null) {
            handleKeybind(player, stack, gunKeybind, newState);
        }
    }

    public void handleKeybind(Player player, ItemStack stack, KeybindType keybind, boolean newState) {
        ItemStack mainHand = player.getMainHandItem();

        if (!mainHand.isEmpty() && mainHand.getItem() instanceof GunItem)
            handleKeybind(player, player.getInventory(), stack, keybind, newState);
    }

    public void handleKeybind(LivingEntity entity, Container inventory,
                              ItemStack stack, KeybindType keybind, boolean newState) {
        if (!GeneralConfig.enableGuns.get()) return;

        for (int i = 0; i < configs.length; i++) {
            GunConfig config = getConfig(stack, i);
            LambdaContext ctx = new LambdaContext(config, entity, inventory, i);

            if (keybind == KeybindType.GUN_PRIMARY) {
                boolean currentState = getPrimary(stack, i);

                if (newState != currentState) {
                    if (newState) {
                        if (config.getPressPrimary(stack) != null) {
                            Objects.requireNonNull(config.getPressPrimary(stack)).accept(stack, ctx);
                        }
                    } else {
                        if (config.getReleasePrimary(stack) != null) {
                            Objects.requireNonNull(config.getReleasePrimary(stack)).accept(stack, ctx);
                        }
                    }
                    setPrimary(stack, i, newState);
                }
                continue;
            }

            if (keybind == KeybindType.GUN_SECONDARY && newState && !getSecondary(stack, i)) {
                if (config.getPressSecondary(stack) != null) {
                    Objects.requireNonNull(config.getPressSecondary(stack)).accept(stack, ctx);
                }
                setSecondary(stack, i, true);
                continue;
            }

            if (keybind == KeybindType.GUN_SECONDARY && !newState && getSecondary(stack, i)) {
                if (config.getReleaseSecondary(stack) != null) {
                    Objects.requireNonNull(config.getReleaseSecondary(stack)).accept(stack, ctx);
                }
                setSecondary(stack, i, false);
                continue;
            }

            if (keybind == KeybindType.GUN_TERTIARY && newState && !getTertiary(stack, i)) {
                if (config.getPressTertiary(stack) != null) {
                    Objects.requireNonNull(config.getPressTertiary(stack)).accept(stack, ctx);
                }
                setTertiary(stack, i, true);
                continue;
            }

            if (keybind == KeybindType.GUN_TERTIARY && !newState && getTertiary(stack, i)) {
                if (config.getReleaseTertiary(stack) != null) {
                    Objects.requireNonNull(config.getReleaseTertiary(stack)).accept(stack, ctx);
                }
                setTertiary(stack, i, false);
                continue;
            }

            if (keybind == KeybindType.RELOAD && newState && !getReloadKey(stack, i)) {
                if (config.getPressReload(stack) != null) {
                    Objects.requireNonNull(config.getPressReload(stack)).accept(stack, ctx);
                }
                setReloadKey(stack, i, true);
                continue;
            }

            if (keybind == KeybindType.RELOAD && !newState && getReloadKey(stack, i)) {
                if (config.getReleaseReload(stack) != null) {
                    Objects.requireNonNull(config.getReleaseReload(stack)).accept(stack, ctx);
                }
                setReloadKey(stack, i, false);
                continue;
            }

            if (keybind == KeybindType.GUN_AIM && newState) {
                // Toggle режим: меняем состояние на противоположное
                boolean currentAiming = getIsAiming(stack);
                setIsAiming(stack, !currentAiming);

                continue;
            }
        }
    }

    public enum KeybindType {
        GUN_PRIMARY,
        GUN_SECONDARY,
        GUN_TERTIARY,
        RELOAD,
        GUN_AIM
    }

    // Equipment handling
    public void onEquip(Player player, ItemStack stack) {
        for (int i = 0; i < configs.length; i++) {
            if (getLastAnim(stack, i) == AnimationEnums.GunAnimation.EQUIP && getAnimTimer(stack, i) < 5) {
                continue;
            }

            playAnimation(player, stack, AnimationEnums.GunAnimation.EQUIP, i);
            setPrimary(stack, i, false);
            setSecondary(stack, i, false);
            setTertiary(stack, i, false);
            setReloadKey(stack, i, false);
        }
    }

    public static void playAnimation(Player player, ItemStack stack,
                                     AnimationEnums.GunAnimation type, int index) {
        playAnimation(player, stack, type, index, false, 0L);
    }

    public static void playAnimation(Player player, ItemStack stack,
                                     AnimationEnums.GunAnimation type, int index,
                                     boolean isFireAnimation, long shotTime) {
        if (!player.level().isClientSide) {
            // Если это анимация выстрела - передаем время
            long timeToSend = isFireAnimation ? shotTime : 0L;

            PacketDispatcher.sendGunAnimation(
                    new GunAnimationPacket(type.ordinal(), 0, index, timeToSend),
                    player
            );

            setLastAnim(stack, index, type);
            setAnimTimer(stack, index, 0);

        }
    }

    // Update tick
    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level,
                              @NotNull Entity entity, int slot, boolean isSelected) {
        if (!(entity instanceof LivingEntity living)) return;

        if (level.isClientSide && isSelected && entity instanceof Player player) {
            syncShootingStateOnClient(player, stack);
        }

        if(getBoolean(stack, "shouldBreak")) {
            destroyGun(stack, entity);
            setBoolean(stack, "shouldBreak", false);
            return;
        }

        Player player = entity instanceof Player ? (Player) entity : null;
        GunConfig[] configs = new GunConfig[this.configs.length];
        LambdaContext[] contexts = new LambdaContext[configs.length];

        for (int i = 0; i < configs.length; i++) {
            configs[i] = getConfig(stack, i);
            contexts[i] = new LambdaContext(configs[i], living,
                    player != null ? player.getInventory() : null, i);
        }

        if (level.isClientSide) {
            if (isSelected && player != null && player == Minecraft.getInstance().player) {
                // Aiming interpolation
                prevAimingProgress = aimingProgress;
                boolean aiming = getIsAiming(stack);
                float aimSpeed = 0.25F;

                if (aiming && aimingProgress < 1F) aimingProgress += aimSpeed;
                if (!aiming && aimingProgress > 0F) aimingProgress -= aimSpeed;
                aimingProgress = Math.max(0F, Math.min(1F, aimingProgress));

                // Smoke nodes
                for (int i = 0; i < configs.length; i++) {
                    if (configs[i].getSmokeHandler(stack) != null) {
                        Objects.requireNonNull(configs[i].getSmokeHandler(stack)).accept(stack, contexts[i]);
                    }
                }

                // Orchestra (reload sounds)
                for (int i = 0; i < configs.length; i++) {
                    BiConsumer<ItemStack, LambdaContext> orchestra = configs[i].getOrchestra(stack);
                    if (orchestra != null) {
                        orchestra.accept(stack, contexts[i]);
                    }
                }
            }
            return;
        }

        // Server-side logic
        if (player != null) {
            boolean wasHeld = getIsEquipped(stack);

            if (!wasHeld && isSelected) {
                onEquip(player, stack);
            }
        }

        setIsEquipped(stack, isSelected);

        // Reset when not equipped
        if (!isSelected) {
            for (int i = 0; i < configs.length; i++) {
                GunState current = getState(stack, i);
                if (current != GunState.JAMMED) {
                    setState(stack, i, GunState.DRAWING);
                    setTimer(stack, i, configs[i].getDrawDuration(stack));
                }
                setLastAnim(stack, i, AnimationEnums.GunAnimation.CYCLE);
            }
            setIsAiming(stack, false);
            setReloadCancel(stack, false);
            return;
        }

        // State machine update
        for (int i = 0; i < configs.length; i++) {
            // Orchestra
            BiConsumer<ItemStack, LambdaContext> orchestra = configs[i].getOrchestra(stack);
            if (orchestra != null) {
                orchestra.accept(stack, contexts[i]);
            }

            // Animation timer
            setAnimTimer(stack, i, getAnimTimer(stack, i) + 1);

            // State machine
            int timer = getTimer(stack, i);
            if (timer > 0) {
                setTimer(stack, i, timer - 1);
            }

            if (timer <= 1) {
                BiConsumer<ItemStack, LambdaContext> decider = configs[i].getDecider(stack);
                if (decider != null) {
                    decider.accept(stack, contexts[i]);
                }
            }
        }
    }

    private void syncShootingStateOnClient(Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        boolean mouseLeftPressed = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(),
                GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
        boolean propsState = props.getKeyPressed(HbmKeybinds.EnumKeybind.GUN_PRIMARY);

        if (propsState != mouseLeftPressed) {
            props.setKeyPressed(HbmKeybinds.EnumKeybind.GUN_PRIMARY, mouseLeftPressed);
            PacketDispatcher.sendKeybind(HbmKeybinds.EnumKeybind.GUN_PRIMARY, mouseLeftPressed);

            for (int i = 0; i < configs.length; i++) {
                setPrimary(stack, i, mouseLeftPressed);
            }
        }
    }

    /**
     * Уничтожает оружие при достижении нулевой прочности (простой вариант)
     */
    private void destroyGun(ItemStack stack, Entity entity) {
        Level level = entity.level();

        if(!level.isClientSide()) {
            // Проигрываем звук ломания
            level.playSound(null, entity.blockPosition(),
                    SoundEvents.ITEM_BREAK, SoundSource.PLAYERS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);

            if(entity instanceof Player player) {
                // Сообщение игроку
                player.displayClientMessage(
                        Component.translatable("message.weapon.broken")
                                .withStyle(ChatFormatting.RED),
                        true
                );

                // Просто уменьшаем количество предметов на 1
                stack.shrink(1);

                // Частицы
                if(level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            player.getX(), player.getY() + 1.5, player.getZ(),
                            15, 0.3, 0.3, 0.3, 0.05);
                }
            }
        }
    }

    // Interaction
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            // Handle primary fire on right click
            for (int i = 0; i < configs.length; i++) {
                GunConfig config = getConfig(stack, i);
                LambdaContext ctx = new LambdaContext(config, player, player.getInventory(), i);

                if (config.getPressPrimary(stack) != null) {
                    Objects.requireNonNull(config.getPressPrimary(stack)).accept(stack, ctx);
                }
            }
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    // HUD rendering
    @OnlyIn(Dist.CLIENT)
    public void renderHUD(GuiGraphics guiGraphics, float partialTick, Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        // Crosshair rendering (существующий код)
        GunConfig config = getConfig(stack, 0);
        if (!(config.getHideCrosshair(stack) && aimingProgress >= 1F)) {
            Crosshair crosshair = config.getCrosshair(stack);
            if (crosshair != Crosshair.NONE) {
                renderCrosshair(guiGraphics, Objects.requireNonNull(crosshair), width / 2, height / 2);
            }
        }

        // Получаем HUD компоненты из конфига
        IHUDComponent[] configComponents = null;
        for (int i = 0; i < configs.length; i++) {
            configComponents = getConfig(stack, i).getHUDComponents(stack);
            if (configComponents != null) {
                break;
            }
        }

        // Если в конфиге нет HUD компонентов, используем дефолтные
        List<IHUDComponent> allComponents = new ArrayList<>();

        if (configComponents != null) {
            allComponents.addAll(Arrays.asList(configComponents));
        } else {
            // Добавляем дефолтные HUD компоненты
            allComponents.add(new AmmoCounterHUD(0));
            allComponents.add(new DurabilityBarHUD());
        }

        // Рендерим все компоненты
        int bottomOffset = 0;
        for (IHUDComponent component : allComponents) {
            component.renderHUDComponent(guiGraphics, player, stack, bottomOffset, 0);
            bottomOffset += component.getComponentHeight(player, stack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void renderCrosshair(GuiGraphics guiGraphics, Crosshair crosshair, int x, int y) {
        if (crosshair == Crosshair.NONE) {
            return;
        }

        // Получаем текущий PoseStack
        PoseStack poseStack = guiGraphics.pose();

        // Сохраняем матрицу
        poseStack.pushPose();

        // Включаем и настраиваем смешивание
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        // Рисуем текстуру с указанием размеров текстуры
        guiGraphics.blit(overlay_misc,
                x - crosshair.size / 2,
                y - crosshair.size / 2,
                crosshair.x,
                crosshair.y,
                crosshair.size,
                crosshair.size,
                256,  // ширина текстуры (textureWidth)
                256); // высота текстуры (textureHeight)

        // Восстанавливаем стандартные настройки
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    // Recoil setup
    public static void setupRecoil(float vertical, float horizontal, float decay, float rebound) {
        recoilVertical += vertical;
        recoilHorizontal += horizontal;
        recoilDecay = decay;
        recoilRebound = rebound;
    }

    public static void setupRecoil(float vertical, float horizontal) {
        setupRecoil(vertical, horizontal, 0.75F, 0.25F);
    }

    // Lambda Context
    public static class LambdaContext {
        public final GunConfig config;
        public final LivingEntity entity;
        public final Container inventory;
        public final int configIndex;

        public LambdaContext(GunConfig config, LivingEntity entity,
                             Container inventory, int configIndex) {
            this.config = config;
            this.entity = entity;
            this.inventory = inventory;
            this.configIndex = configIndex;
        }

        @Nullable
        public Player getPlayer() {
            return entity instanceof Player ? (Player) entity : null;
        }
    }

    // Smoke node (for particle effects)
    public static class SmokeNode {
        public double forward = 0D;
        public double side = 0D;
        public double lift = 0D;
        public double alpha;
        public double width = 1D;

        public SmokeNode(double alpha) {
            this.alpha = alpha;
        }
    }

    // Equipment change event
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        // Handle when gun is equipped/unequipped
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        // Показываем полоску прочности, если есть износ
        for (int i = 0; i < configs.length; i++) {
            GunConfig config = getConfig(stack, i);
            float maxDura = config.getDurability(stack);
            if (maxDura > 0) {
                float wear = getWear(stack, i);
                if (wear > 0) {
                    return true;
                }
            }
        }
        return super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        // Возвращаем ширину полоски прочности (0-13)
        for (int i = 0; i < configs.length; i++) {
            GunConfig config = getConfig(stack, i);
            float maxDura = config.getDurability(stack);
            if (maxDura > 0) {
                float wear = getWear(stack, i);
                float condition = Math.max(0, Math.min(1, (maxDura - wear) / maxDura));
                return Math.round(13.0F * condition);
            }
        }
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        // Цвет полоски прочности (зелёный -> жёлтый -> красный)
        for (int i = 0; i < configs.length; i++) {
            GunConfig config = getConfig(stack, i);
            float maxDura = config.getDurability(stack);
            if (maxDura > 0) {
                float wear = getWear(stack, i);
                float condition = (maxDura - wear) / maxDura;

                if (condition > 0.66f) {
                    return 0x00FF00; // Зелёный
                } else if (condition > 0.33f) {
                    return 0xFFAA00; // Жёлтый/оранжевый
                } else {
                    return 0xFF0000; // Красный
                }
            }
        }
        return super.getBarColor(stack);
    }

}