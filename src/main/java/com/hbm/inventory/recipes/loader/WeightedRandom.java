package com.hbm.inventory.recipes.loader;

import java.util.List;

public class WeightedRandom {

    public static class Item {
        public int itemWeight;

        public Item(int weight) {
            this.itemWeight = weight;
        }
    }

    public static int getTotalWeight(List<? extends Item> items) {
        int total = 0;
        for (Item item : items) {
            total += item.itemWeight;
        }
        return total;
    }

    public static <T extends Item> T getRandomItem(java.util.Random random, List<T> items) {
        if (items.isEmpty()) return null;
        int totalWeight = getTotalWeight(items);
        int randomWeight = random.nextInt(totalWeight);
        int cumulative = 0;
        for (T item : items) {
            cumulative += item.itemWeight;
            if (randomWeight < cumulative) {
                return item;
            }
        }
        return items.get(0);
    }
}