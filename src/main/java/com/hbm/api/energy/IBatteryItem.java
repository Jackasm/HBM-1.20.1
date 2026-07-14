package com.hbm.api.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IBatteryItem {

    void chargeBattery(ItemStack stack, long i);

    void setCharge(ItemStack stack, long i);

    void dischargeBattery(ItemStack stack, long i);

    long getCharge(ItemStack stack);

    long getMaxCharge(ItemStack stack);

    long getChargeRate();

    long getDischargeRate();

    /**
     * Returns a string for the NBT tag name of the long storing power
     */
    default String getChargeTagName() {
        return "charge";
    }

    /**
     * Returns a string for the NBT tag name of the long storing power
     */
    static String getChargeTagName(ItemStack stack) {
        if (stack.getItem() instanceof IBatteryItem battery) {
            return battery.getChargeTagName();
        }
        return "charge";
    }

    /**
     * Returns an empty battery stack from the passed ItemStack, the original won't be modified
     */
    static @Nullable ItemStack emptyBattery(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof IBatteryItem battery) {
            String keyName = getChargeTagName(stack);
            ItemStack stackOut = stack.copy();
            stackOut.setTag(new CompoundTag());
            stackOut.getOrCreateTag().putLong(keyName, 0);
            return stackOut.copy();
        }
        return null;
    }

    /**
     * Returns an empty battery stack from the passed Item
     */
    static @Nullable ItemStack emptyBattery(Item item) {
        return item instanceof IBatteryItem ? emptyBattery(new ItemStack(item)) : null;
    }
}