package com.hbm.itempool;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPoolHelper {

    public static ItemPool.PoolEntry weighted(ItemStack stack, int minCount, int maxCount, int weight) {
        return new ItemPool.PoolEntry(stack, weight, minCount, maxCount);
    }

    public static ItemPool.PoolEntry weighted(Item item, int minCount, int maxCount, int weight) {
        return weighted(new ItemStack(item, 1), minCount, maxCount, weight);
    }

    public static ItemPool.PoolEntry weighted(Item item, int meta, int minCount, int maxCount, int weight) {
        ItemStack stack = new ItemStack(item, 1);
        if (meta != 0) {
            stack.getOrCreateTag().putInt("ammo_type", meta);
        }
        return weighted(stack, minCount, maxCount, weight);
    }

    public static ItemPool.PoolEntry weighted(ItemStack stack, int count, int weight) {
        return weighted(stack, count, count, weight);
    }

    public static ItemPool.PoolEntry weighted(net.minecraft.world.item.Item item, int count, int weight) {
        return weighted(item, count, count, weight);
    }

    public static ItemPool.PoolEntry empty(int weight) {
        return new ItemPool.PoolEntry(ItemStack.EMPTY, weight, 0, 0);
    }
}