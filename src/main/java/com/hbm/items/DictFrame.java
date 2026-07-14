package com.hbm.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.hbm.inventory.material.MaterialShapes.FRAGMENT;


public class DictFrame {

    public String[] mats;

    public String fragment() {		return FRAGMENT.name()			+ mats[0]; }

    public DictFrame(String... mats) {
        this.mats = mats;
    }

    public static ItemStack fromOne(Item item, Enum<?> type) {
        return fromOne(item, type, 1);
    }

    public static ItemStack fromOne(Item item, Enum<?> type, int count) {
        ItemStack stack = new ItemStack(item, count);
        stack.getOrCreateTag().putInt("CustomModelData", type.ordinal());
        return stack;
    }
}