package com.hbm.items.special;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemHotDusted extends ItemHot {

    public ItemHotDusted(Properties properties, int heat) {
        super(properties, heat);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int dustLevel = stack.getDamageValue();
        tooltip.add(Component.translatable("item.hot_dusted.forged", dustLevel)
                .withStyle(ChatFormatting.GRAY));
    }

    public static int getMaxHeat(ItemStack stack) {
        if (stack.getItem() instanceof ItemHotDusted) {
            int dustLevel = stack.getDamageValue();
            return maxHeat - dustLevel * 10;
        }
        return 0;
    }

    // Метод для создания запылённого слитка с определённым уровнем
    public static ItemStack createDusted(ItemStack stack, int dustLevel) {
        ItemStack result = stack.copy();
        result.setDamageValue(dustLevel);
        return result;
    }
}