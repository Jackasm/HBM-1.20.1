package com.hbm.blocks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public interface ITooltipProvider {

    default String[] getTooltipKeys(ItemStack stack) {
        return new String[]{stack.getItem().getDescriptionId() + ".desc"};
    }

    /**
     * Полный тултип. Переопределяй, если нужно больше, чем просто список ключей.
     * @param showDetails Shift нажат
     */
    default void addTooltip(ItemStack stack, List<Component> tooltip, boolean showDetails) {
        String[] keys = getTooltipKeys(stack);
        if (keys == null || keys.length == 0) return;

        if (showDetails) {
            for (String key : keys) tooltip.add(Component.translatable(key).withStyle(net.minecraft.ChatFormatting.YELLOW));
        } else {
            tooltip.add(Component.translatable("tooltip.hold.shift")
                    .withStyle(net.minecraft.ChatFormatting.GOLD, net.minecraft.ChatFormatting.ITALIC));
        }
    }

    default Rarity getRarity(ItemStack stack) {
        return Rarity.COMMON;
    }
}