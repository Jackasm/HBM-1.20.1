package com.hbm.creativetabs;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TabFallback {
    public static ItemStack getFallbackIcon() {
        return new ItemStack(Items.IRON_AXE);
    }

    public static void addFallbackItem(CreativeModeTab.Output output) {
        output.accept(getFallbackIcon());
    }
}