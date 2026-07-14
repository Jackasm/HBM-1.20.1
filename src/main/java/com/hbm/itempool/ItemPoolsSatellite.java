package com.hbm.itempool;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class ItemPoolsSatellite {

    public static final String POOL_SAT_MINER = "POOL_SAT_MINER";
    public static final String POOL_SAT_LUNAR = "POOL_SAT_LUNAR"; // woona

    private static final ItemPool SAT_MINER_POOL = new ItemPool()
            .add(weighted(ModItems.POWDER_ALUMINIUM.get(), 3, 3, 10))
            .add(weighted(ModItems.POWDER_IRON.get(), 3, 3, 10))
            .add(weighted(ModItems.POWDER_TITANIUM.get(), 2, 2, 8))
            .add(weighted(ModItems.CRYSTAL_TUNGSTEN.get(), 2, 2, 7))
            .add(weighted(ModItems.POWDER_COAL.get(), 4, 4, 15))
            .add(weighted(ModItems.POWDER_URANIUM.get(), 2, 2, 5))
            .add(weighted(ModItems.POWDER_PLUTONIUM.get(), 1, 1, 5))
            .add(weighted(ModItems.POWDER_THORIUM.get(), 2, 2, 7))
            .add(weighted(ModItems.POWDER_DESH_MIX.get(), 3, 3, 5))
            .add(weighted(ModItems.POWDER_DIAMOND.get(), 2, 2, 7))
            .add(weighted(Items.REDSTONE, 5, 5, 15))
            .add(weighted(ModItems.POWDER_NITAN_MIX.get(), 2, 2, 5))
            .add(weighted(ModItems.POWDER_POWER.get(), 2, 2, 5))
            .add(weighted(ModItems.POWDER_COPPER.get(), 5, 5, 15))
            .add(weighted(ModItems.POWDER_LEAD.get(), 3, 3, 10))
            .add(weighted(ModItems.FLUORITE.get(), 4, 4, 15))
            .add(weighted(ModItems.POWDER_LAPIS.get(), 4, 4, 10))
            .add(weighted(ModItems.CRYSTAL_ALUMINIUM.get(), 1, 1, 5))
            .add(weighted(ModItems.CRYSTAL_GOLD.get(), 1, 1, 5))
            .add(weighted(ModItems.CRYSTAL_PHOSPHORUS.get(), 1, 1, 10))
            .add(weighted(ModBlocks.GRAVEL_DIAMOND.get().asItem(), 1, 1, 3))
            .add(weighted(ModItems.CRYSTAL_URANIUM.get(), 1, 1, 3))
            .add(weighted(ModItems.CRYSTAL_PLUTONIUM.get(), 1, 1, 3))
            .add(weighted(ModItems.CRYSTAL_TRIXITE.get(), 1, 1, 1))
            .add(weighted(ModItems.CRYSTAL_STARMETAL.get(), 1, 1, 1))
            .add(weighted(ModItems.CRYSTAL_LITHIUM.get(), 2, 2, 4));

    private static final ItemPool SAT_LUNAR_POOL = new ItemPool()
            .add(weighted(ModBlocks.MOON_TURF.get().asItem(), 48, 48, 5))
            .add(weighted(ModBlocks.MOON_TURF.get().asItem(), 32, 32, 7))
            .add(weighted(ModBlocks.MOON_TURF.get().asItem(), 16, 16, 5))
            .add(weighted(ModItems.POWDER_LITHIUM.get(), 3, 3, 5))
            .add(weighted(ModItems.POWDER_IRON.get(), 3, 3, 5))
            .add(weighted(ModItems.CRYSTAL_IRON.get(), 1, 1, 1))
            .add(weighted(ModItems.CRYSTAL_LITHIUM.get(), 1, 1, 1));

    public static ItemPool getMinerPool() {
        return SAT_MINER_POOL;
    }

    public static ItemPool getLunarPool() {
        return SAT_LUNAR_POOL;
    }

    public static ItemStack getRandomItemFromPool(String poolName, RandomSource random) {
        ItemPool pool = switch (poolName) {
            case POOL_SAT_MINER -> SAT_MINER_POOL;
            case POOL_SAT_LUNAR -> SAT_LUNAR_POOL;
            default -> null;
        };
        return pool != null ? pool.generateOne(random) : ItemStack.EMPTY;
    }
}