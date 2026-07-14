package com.hbm.itempool;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemPool {
    private final List<PoolEntry> entries = new ArrayList<>();
    private int totalWeight = 0;

    public ItemPool add(PoolEntry entry) {
        entries.add(entry);
        totalWeight += entry.weight;
        return this;
    }

    public List<ItemStack> generate(RandomSource random, int count) {
        List<ItemStack> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ItemStack stack = generateOne(random);
            if (!stack.isEmpty()) {
                results.add(stack);
            }
        }
        return results;
    }

    public ItemStack generateOne(RandomSource random) {
        if (entries.isEmpty() || totalWeight <= 0) return ItemStack.EMPTY;

        int choice = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (PoolEntry entry : entries) {
            currentWeight += entry.weight;
            if (choice < currentWeight) {
                return entry.createStack(random);
            }
        }
        return ItemStack.EMPTY;
    }

    public static class PoolEntry {
        public final ItemStack stack;
        public final int weight;
        public final int minCount;
        public final int maxCount;

        public PoolEntry(ItemStack stack, int weight, int minCount, int maxCount) {
            this.stack = stack;
            this.weight = weight;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }

        public ItemStack createStack(RandomSource random) {
            ItemStack result = stack.copy();
            int count = minCount;
            if (maxCount > minCount) {
                count = minCount + random.nextInt(maxCount - minCount + 1);
            }
            result.setCount(count);
            return result;
        }
    }
}