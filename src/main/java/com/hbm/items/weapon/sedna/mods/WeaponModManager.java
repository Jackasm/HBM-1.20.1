package com.hbm.items.weapon.sedna.mods;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The mod manager operates by scraping upgrades from a gun, then iterating over them
 * and evaluating the given value, passing the modified value to successive mods.
 * The way that mods stack (additive vs multiplicative) depends on the order the mod is installed in.
 */
@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WeaponModManager {

    public static final String KEY_MOD_LIST = "mods_";

    // Mapping of mods to ResourceLocations (replacing integer IDs)
    private static final BiMap<ResourceLocation, IWeaponMod> MODS = HashBiMap.create();

    // Mapping of mod items to mod definitions
    private static final Map<ComparableStack, WeaponModDefinition> STACK_TO_MOD = new HashMap<>();

    // Map for turning individual mods back into their item form
    private static final Map<IWeaponMod, ItemStack> MOD_TO_STACK = new HashMap<>();

    // Mod IDs (now using ResourceLocation)
    public static final ResourceLocation SILENCER = ResLocation.ResLocation(RefStrings.MODID, "silencer");
    public static final ResourceLocation SCOPE = ResLocation.ResLocation(RefStrings.MODID, "scope");
    public static final ResourceLocation SAWED_OFF = ResLocation.ResLocation(RefStrings.MODID, "sawed_off");
    public static final ResourceLocation NO_SHIELD = ResLocation.ResLocation(RefStrings.MODID, "no_shield");
    public static final ResourceLocation NO_STOCK = ResLocation.ResLocation(RefStrings.MODID, "no_stock");
    public static final ResourceLocation GREASEGUN_CLEAN = ResLocation.ResLocation(RefStrings.MODID, "greasegun_clean");
    public static final ResourceLocation MINIGUN_SPEED = ResLocation.ResLocation(RefStrings.MODID, "minigun_speed");
    public static final ResourceLocation FURNITURE_GREEN = ResLocation.ResLocation(RefStrings.MODID, "furniture_green");
    public static final ResourceLocation FURNITURE_BLACK = ResLocation.ResLocation(RefStrings.MODID, "furniture_black");
    public static final ResourceLocation MAS_BAYONET = ResLocation.ResLocation(RefStrings.MODID, "mas_bayonet");
    public static final ResourceLocation UZI_SATURN = ResLocation.ResLocation(RefStrings.MODID, "uzi_saturn");
    public static final ResourceLocation LAS_SHOTGUN = ResLocation.ResLocation(RefStrings.MODID, "las_shotgun");
    public static final ResourceLocation LAS_CAPACITOR = ResLocation.ResLocation(RefStrings.MODID, "las_capacitor");
    public static final ResourceLocation LAS_AUTO = ResLocation.ResLocation(RefStrings.MODID, "las_auto");
    public static final ResourceLocation CARBINE_BAYONET = ResLocation.ResLocation(RefStrings.MODID, "carbine_bayonet");
    public static final ResourceLocation NI4NI_NICKEL = ResLocation.ResLocation(RefStrings.MODID, "ni4ni_nickel");
    public static final ResourceLocation NI4NI_DOUBLOONS = ResLocation.ResLocation(RefStrings.MODID, "ni4ni_doubloons");

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Register mods here
            // Example:
            // registerMod(SILENCER, new WeaponModSilencer());
            // registerMod(SCOPE, new WeaponModScope());

            // You'll need to adapt the factory classes for 1.20.1
        });
    }

    public static void registerMod(@NotNull ResourceLocation id, @NotNull IWeaponMod mod) {
        MODS.put(id, mod);
    }

    @Nullable
    public static IWeaponMod getMod(@NotNull ResourceLocation id) {
        return MODS.get(id);
    }

    @Nullable
    public static ResourceLocation getModId(@NotNull IWeaponMod mod) {
        return MODS.inverse().get(mod);
    }

    /* MOD MANAGEMENT METHODS */

    /**
     * Get all mods installed on a gun.
     */
    @NotNull
    public static List<IWeaponMod> getInstalledMods(@NotNull ItemStack gun, int configIndex) {
        List<IWeaponMod> mods = new ArrayList<>();

        CompoundTag tag = gun.getTag();
        if (tag == null) return mods;

        String key = KEY_MOD_LIST + configIndex;
        if (!tag.contains(key, Tag.TAG_LIST)) return mods;

        ListTag modList = tag.getList(key, Tag.TAG_STRING);
        for (int i = 0; i < modList.size(); i++) {
            ResourceLocation modId = ResourceLocation.tryParse(modList.getString(i));
            if (modId != null) {
                IWeaponMod mod = MODS.get(modId);
                if (mod != null) {
                    mods.add(mod);
                }
            }
        }

        return mods;
    }

    /**
     * Get mod items installed on a gun.
     */
    @NotNull
    public static ItemStack[] getUpgradeItems(@NotNull ItemStack gun, int configIndex) {
        List<IWeaponMod> mods = getInstalledMods(gun, configIndex);
        ItemStack[] items = new ItemStack[mods.size()];

        for (int i = 0; i < mods.size(); i++) {
            items[i] = MOD_TO_STACK.getOrDefault(mods.get(i), ItemStack.EMPTY).copy();
        }

        return items;
    }

    /**
     * Check if a gun has a specific mod installed.
     */
    public static boolean hasUpgrade(@NotNull ItemStack gun, int configIndex, @NotNull ResourceLocation modId) {
        CompoundTag tag = gun.getTag();
        if (tag == null) return false;

        String key = KEY_MOD_LIST + configIndex;
        if (!tag.contains(key, Tag.TAG_LIST)) return false;

        ListTag modList = tag.getList(key, Tag.TAG_STRING);
        for (int i = 0; i < modList.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(modList.getString(i));
            if (modId.equals(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Install mods on a gun.
     */
    public static void install(@NotNull ItemStack gun, int configIndex, @NotNull ItemStack... mods) {
        List<ResourceLocation> toInstall = new ArrayList<>();
        ComparableStack gunStack = new ComparableStack(gun);

        for (ItemStack mod : mods) {
            if (mod.isEmpty()) continue;

            ComparableStack comp = new ComparableStack(mod);
            WeaponModDefinition def = STACK_TO_MOD.get(comp);
            if (def != null) {
                IWeaponMod weaponMod = def.getModForGun(gunStack);
                if (weaponMod != null) {
                    ResourceLocation modId = MODS.inverse().get(weaponMod);
                    if (modId != null) {
                        toInstall.add(modId);
                    }
                }
            }
        }

        if (toInstall.isEmpty()) return;

        // Sort by priority
        toInstall.sort(Comparator.comparingInt(id -> {
            IWeaponMod mod = MODS.get(id);
            return mod != null ? mod.getModPriority() : 0;
        }));

        CompoundTag tag = gun.getOrCreateTag();
        String key = KEY_MOD_LIST + configIndex;
        ListTag modList = new ListTag();

        // Add existing mods
        if (tag.contains(key, Tag.TAG_LIST)) {
            modList = tag.getList(key, Tag.TAG_STRING);
        }

        // Add new mods
        for (ResourceLocation modId : toInstall) {
            modList.add(StringTag.valueOf(modId.toString()));
        }

        tag.put(key, modList);
    }

    /**
     * Remove all mods from a gun.
     */
    public static void uninstall(@NotNull ItemStack gun, int configIndex) {
        CompoundTag tag = gun.getTag();
        if (tag == null) return;

        String key = KEY_MOD_LIST + configIndex;
        tag.remove(key);

        // Clean up empty tag
        if (tag.isEmpty()) {
            gun.setTag(null);
        }
    }

    /**
     * Remove a specific mod from a gun.
     */
    public static void removeMod(@NotNull ItemStack gun, int configIndex, @NotNull ResourceLocation modId) {
        CompoundTag tag = gun.getTag();
        if (tag == null) return;

        String key = KEY_MOD_LIST + configIndex;
        if (!tag.contains(key, Tag.TAG_LIST)) return;

        ListTag modList = tag.getList(key, Tag.TAG_STRING);
        ListTag newList = new ListTag();

        for (int i = 0; i < modList.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(modList.getString(i));
            if (!modId.equals(id)) {
                newList.add(modList.get(i));
            }
        }

        if (newList.isEmpty()) {
            tag.remove(key);
        } else {
            tag.put(key, newList);
        }
    }

    /* MOD APPLICATION LOGIC */

    public static void onInstallStack(@NotNull ItemStack gun, @NotNull ItemStack mod, int configIndex) {
        IWeaponMod weaponMod = getModFromStack(gun, mod, configIndex);
        if (weaponMod != null) {
            weaponMod.onInstall(gun, mod, configIndex);
        }
    }

    public static void onUninstallStack(@NotNull ItemStack gun, @NotNull ItemStack mod, int configIndex) {
        IWeaponMod weaponMod = getModFromStack(gun, mod, configIndex);
        if (weaponMod != null) {
            weaponMod.onUninstall(gun, mod, configIndex);
        }
    }

    @Nullable
    public static IWeaponMod getModFromStack(@NotNull ItemStack gun, @NotNull ItemStack mod, int configIndex) {
        if (gun.isEmpty() || mod.isEmpty()) return null;

        ComparableStack comp = new ComparableStack(mod);
        WeaponModDefinition def = STACK_TO_MOD.get(comp);
        if (def == null) return null;

        ComparableStack gunComp = new ComparableStack(gun);
        IWeaponMod weaponMod = def.getModForGun(gunComp);
        if (weaponMod == null) {
            weaponMod = def.getDefaultMod();
        }

        return weaponMod;
    }

    public static boolean isApplicable(@NotNull ItemStack gun, @NotNull ItemStack mod,
                                       int configIndex, boolean checkMutex) {
        IWeaponMod weaponMod = getModFromStack(gun, mod, configIndex);
        if (weaponMod == null) return false;

        if (!weaponMod.canApplyTo(gun)) return false;

        if (checkMutex) {
            List<IWeaponMod> installedMods = getInstalledMods(gun, configIndex);
            for (IWeaponMod installed : installedMods) {
                for (String slot1 : weaponMod.getSlots()) {
                    for (String slot2 : installed.getSlots()) {
                        if (slot1.equals(slot2)) {
                            return false; // Slot conflict
                        }
                    }
                }
            }
        }

        return true;
    }

    /* VALUE EVALUATION */

    /**
     * Evaluate a value through all installed mods.
     */
    public static <T> T eval(T base, @NotNull ItemStack gun, @NotNull String key,
                             @NotNull Object parent, int configIndex) {
        if (gun.isEmpty()) return base;

        List<IWeaponMod> mods = getInstalledMods(gun, configIndex);
        T result = base;

        for (IWeaponMod mod : mods) {
            result = mod.eval(result, gun, key, parent);
        }

        return result;
    }

    public static SoundEvent evalSound(SoundEvent base, @NotNull ItemStack gun,
                                       @NotNull Object parent, int configIndex) {
        if (gun.isEmpty()) return base;

        List<IWeaponMod> mods = getInstalledMods(gun, configIndex);
        SoundEvent result = base;

        for (IWeaponMod mod : mods) {
            result = mod.evalSound(result, gun, parent);
        }

        return result;
    }

    /* HELPER CLASSES */

    public static class WeaponModDefinition {
        private final Map<ComparableStack, IWeaponMod> modByGun = new HashMap<>();
        private final ItemStack stack;
        private IWeaponMod defaultMod;

        public WeaponModDefinition(@NotNull ItemStack stack) {
            this.stack = stack.copy();
            STACK_TO_MOD.put(new ComparableStack(stack), this);
        }

        public WeaponModDefinition addMod(@NotNull ItemStack gun, @NotNull IWeaponMod mod) {
            return addMod(new ComparableStack(gun), mod);
        }

        public WeaponModDefinition addMod(@NotNull Item gun, @NotNull IWeaponMod mod) {
            return addMod(new ComparableStack(gun), mod);
        }

        public WeaponModDefinition addMod(@NotNull Item[] guns, @NotNull IWeaponMod mod) {
            for (Item gun : guns) {
                addMod(new ComparableStack(gun), mod);
            }
            return this;
        }

        public WeaponModDefinition addMod(@Nullable ComparableStack gun, @NotNull IWeaponMod mod) {
            modByGun.put(gun, mod);
            MOD_TO_STACK.put(mod, stack.copy());

            if (gun != null) {
                // Register mod with gun item
                Item item = gun.getItem();
                if (item instanceof GunItem gunItem) {
                    ComparableStack comp = new ComparableStack(stack);
                    if (!gunItem.recognizedMods.contains(comp)) {
                        gunItem.recognizedMods.add(comp);
                    }
                }
            }

            return this;
        }

        public WeaponModDefinition addDefault(@NotNull IWeaponMod mod) {
            this.defaultMod = mod;
            return addMod((Item) null, mod);
        }

        @Nullable
        public IWeaponMod getModForGun(@NotNull ComparableStack gun) {
            return modByGun.get(gun);
        }

        @Nullable
        public IWeaponMod getDefaultMod() {
            return defaultMod;
        }

        @NotNull
        public ItemStack getModItem() {
            return stack.copy();
        }
    }

    /* EXAMPLE MOD IMPLEMENTATIONS */

    public static class WeaponModDamage implements IWeaponMod {
        private final float damageMultiplier;
        private final ResourceLocation id;

        public WeaponModDamage(@NotNull ResourceLocation id, float damageMultiplier) {
            this.id = id;
            this.damageMultiplier = damageMultiplier;
        }

        @Override
        public int getModPriority() {
            return 100;
        }

        @Override
        public String[] getSlots() {
            return new String[]{"barrel"};
        }

        @Override
        public <T> T eval(T base, @NotNull ItemStack gun, @NotNull String key, @NotNull Object parent) {
            if ("damage".equals(key) && base instanceof Float) {
                return (T) Float.valueOf((Float) base * damageMultiplier);
            }
            if ("damage".equals(key) && base instanceof Double) {
                return (T) Double.valueOf((Double) base * damageMultiplier);
            }
            return base;
        }

        @Override
        public String getDisplayName() {
            return "Damage Upgrade";
        }

        @Override
        public String getDescription() {
            return String.format("Increases damage by %.0f%%", (damageMultiplier - 1) * 100);
        }
    }

    public static class WeaponModSilencer implements IWeaponMod {
        private final ResourceLocation id;

        public WeaponModSilencer(@NotNull ResourceLocation id) {
            this.id = id;
        }

        @Override
        public int getModPriority() {
            return 50;
        }

        @Override
        public String[] getSlots() {
            return new String[]{"muzzle"};
        }

        @Override
        public <T> T eval(T base, @NotNull ItemStack gun, @NotNull String key, @NotNull Object parent) {
            if ("sound".equals(key) && base instanceof String) {
                // Modify sound to silenced version
                String sound = (String) base;
                if (sound.contains("gun")) {
                    return (T) sound.replace("gun", "silenced_gun");
                }
            }
            if ("volume".equals(key) && base instanceof Float) {
                return (T) Float.valueOf((Float) base * 0.3f);
            }
            return base;
        }

        @Override
        public String getDisplayName() {
            return "Silencer";
        }

        @Override
        public String getDescription() {
            return "Reduces weapon noise and muzzle flash";
        }

        @Override
        public void onInstall(@NotNull ItemStack gun, @NotNull ItemStack mod, int index) {
            // Play installation sound
            // Add visual effects
        }
    }

    public static class WeaponModScope implements IWeaponMod {
        private final ResourceLocation id;

        public WeaponModScope(@NotNull ResourceLocation id) {
            this.id = id;
        }

        @Override
        public int getModPriority() {
            return 75;
        }

        @Override
        public String[] getSlots() {
            return new String[]{"optic"};
        }

        @Override
        public <T> T eval(T base, @NotNull ItemStack gun, @NotNull String key, @NotNull Object parent) {
            if ("spread".equals(key) && base instanceof Float) {
                return (T) Float.valueOf((Float) base * 0.5f); // Reduce spread when aiming
            }
            if ("zoom".equals(key) && base instanceof Boolean) {
                return (T) Boolean.TRUE; // Enable zoom
            }
            return base;
        }

        @Override
        public String getDisplayName() {
            return "Scope";
        }

        @Override
        public String getDescription() {
            return "Improves accuracy and provides zoom capability";
        }
    }
}