package com.hbm.itempool;

import com.hbm.items.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemPool {

    private final List<PoolEntry> entries = new ArrayList<>();
    public static final Map<String, ItemPool> pools = new HashMap<>();
    private static final Set<String> REPORTED_MISSING = new HashSet<>();
    private int totalWeight = 0;
    private final String name;

    private static ItemPool BACKUP_POOL;

    public ItemPool() {
        this.name = "anonymous_" + System.identityHashCode(this);
    }

    public ItemPool(String name) {
        this.name = name;
        pools.put(name, this);
    }

    public static void initAll() {
        ItemPoolsPile.init();
        ItemPoolsCrate.init();
        ItemPoolsC130.init();
        ItemPoolsLegacy.init();
        ItemPoolsSatellite.init();
        ItemPoolsComponent.init();
        ItemPoolsSingle.init();
        ItemPoolsRedRoom.init();
        ItemPoolsVendingMachine.init();
    }

    public static ItemPool getPool(String name) {
        ItemPool pool = pools.get(name);
        if (pool == null) {
            if (!REPORTED_MISSING.contains(name)) {
                REPORTED_MISSING.add(name);
                System.err.println("[HBM] Missing ItemPool: " + name + " — returning BACKUP_POOL");
            }
            return getBackupPool();
        }
        return pool;
    }

    public static ItemStack getStack(String poolName, RandomSource rand) {
        ItemPool pool = getPool(poolName);
        return pool.generateOne(rand);
    }

    private static ItemPool getBackupPool() {
        if (BACKUP_POOL == null) {
            BACKUP_POOL = new ItemPool("BACKUP_POOL");
            BACKUP_POOL.add(ItemPoolHelper.weighted(Items.BREAD, 1, 3, 10));
            BACKUP_POOL.add(ItemPoolHelper.weighted(Items.STICK, 2, 5, 10));
            BACKUP_POOL.add(ItemPoolHelper.weighted(ModItems.SCRAP.get(), 1, 3, 10));
            BACKUP_POOL.add(ItemPoolHelper.weighted(ModItems.DUST.get(), 2, 5, 5));
        }
        return BACKUP_POOL;
    }

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

    public String getName() {
        return name;
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