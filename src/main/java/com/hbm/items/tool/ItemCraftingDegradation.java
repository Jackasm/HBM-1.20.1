package com.hbm.items.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemCraftingDegradation extends Item {

    public ItemCraftingDegradation(int durability) {
        super(new Item.Properties()
                .stacksTo(1)
                .durability(durability)
                .setNoRepair());
    }

    @Override
    public boolean hasCraftingRemainingItem(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemStack getCraftingRemainingItem(@NotNull ItemStack stack) {
        if (this.getMaxDamage(stack) > 0) {
            ItemStack copy = stack.copy();
            copy.setDamageValue(stack.getDamageValue() + 1);
            return copy;
        } else {
            return stack.copy();
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack));
    }
}