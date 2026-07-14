package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModShield extends ItemArmorMod {

    public final float shield;

    public ItemModShield(Properties properties, float shield) {
        super(ArmorModHandler.KEVLAR, false, true, false, false, properties);
        this.shield = shield;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        String color = (System.currentTimeMillis() % 1000 < 500) ?
                ChatFormatting.YELLOW.toString() : ChatFormatting.GOLD.toString();
        tooltip.add(Component.literal(color + "+" + (Math.round(shield * 10) * 0.1) + " shield"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        String color = (System.currentTimeMillis() % 1000 < 500) ?
                ChatFormatting.YELLOW.toString() : ChatFormatting.GOLD.toString();
        list.add(Component.literal(color + "  " + stack.getHoverName().getString() +
                " (+" + (Math.round(shield * 10) * 0.1) + " health)"));
    }

    // Фабричный метод для создания свойств
    public static Properties createProperties() {
        return new Properties()
                .stacksTo(1);
    }
}