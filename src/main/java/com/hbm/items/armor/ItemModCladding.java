package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModCladding extends ItemArmorMod {

    private final double rad;

    public ItemModCladding(double rad) {
        super(ArmorModHandler.CLADDING, true, true, true, true, new Item.Properties());
        this.rad = rad;
    }

    public double getRadResistance() {
        return this.rad;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {

        tooltip.add(Component.literal("§e+" + String.format("%.2f", rad) + " rad-resistance"));
        tooltip.add(Component.empty());

        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack modStack, ItemStack armorStack) {
        String displayName = modStack.getHoverName().getString();
        list.add(Component.literal("  §e" + displayName + " (+" + String.format("%.2f", rad) + " radiation resistance)"));
    }
}