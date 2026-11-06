package com.hbm.items.machine;

import com.hbm.api.energy.IBatteryItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemBatteryCreative extends Item implements IBatteryItem {

    public ItemBatteryCreative(Properties properties) {
        super(properties);
    }

    @Override
    public void chargeBattery(ItemStack stack, long i) {
        // Креативная батарея всегда заряжена
    }

    @Override
    public void setCharge(ItemStack stack, long i) {
        // Игнорируем
    }

    @Override
    public void dischargeBattery(ItemStack stack, long i) {
        // Не разряжается
    }

    @Override
    public long getCharge(ItemStack stack) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxCharge(ItemStack stack) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getChargeRate() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getDischargeRate() {
        return Long.MAX_VALUE;
    }
}