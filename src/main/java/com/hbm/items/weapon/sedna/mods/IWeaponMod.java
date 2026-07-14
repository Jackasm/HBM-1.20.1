package com.hbm.items.weapon.sedna.mods;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IWeaponMod {

    /**
     * Lower numbers get installed and therefore evaluated first.
     * Important when multiplicative and additive bonuses are supposed to stack.
     */
    int getModPriority();

    /**
     * The slots this mods occupies. Used to prevent conflicting mods.
     */
    String[] getSlots();

    /**
     * The core evaluation method. Modifies the base value based on the mods's effect.
     *
     * @param base The base value to modify
     * @param gun The gun item stack
     * @param key The value identifier (e.g., "damage", "fire_rate")
     * @param parent The parent object (e.g., Receiver, GunConfig)
     * @return The modified value
     */
    <T> T eval(T base, @NotNull ItemStack gun, @NotNull String key, @NotNull Object parent);

    /**
     * Special evaluation method for SoundEvents.
     * This allows mods to modify weapon sounds (e.g., silencers).
     *
     * @param base The base sound event
     * @param gun The gun item stack
     * @param parent The parent object (Receiver or GunConfig)
     * @return The modified sound event
     */
    @Nullable
    default SoundEvent evalSound(@Nullable SoundEvent base, @NotNull ItemStack gun, @NotNull Object parent) {
        // Default implementation returns the same sound
        // Mods like silencers should override this method
        return base;
    }

    /**
     * Called when the mods is installed on a gun.
     */
    default void onInstall(@NotNull ItemStack gun, @NotNull ItemStack mod, int index) { }

    /**
     * Called when the mods is uninstalled from a gun.
     */
    default void onUninstall(@NotNull ItemStack gun, @NotNull ItemStack mod, int index) { }

    /**
     * Get the display name for this mods.
     */
    default String getDisplayName() {
        return "Weapon Mod";
    }

    /**
     * Get the description for this mods.
     */
    default String getDescription() {
        return "A weapon modification.";
    }

    /**
     * Check if this mods can be applied to the given gun.
     */
    default boolean canApplyTo(@NotNull ItemStack gun) {
        return true;
    }
}