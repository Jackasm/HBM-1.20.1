package com.hbm.items.machine;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemBlades extends Item {

    private final int maxDamage;

    public ItemBlades(Properties properties, int durability) {
        super(properties.durability(durability));
        this.maxDamage = durability;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.maxDamage;
    }
}