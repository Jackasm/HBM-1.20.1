package com.hbm.itempool;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;

import static com.hbm.itempool.ItemPoolHelper.weighted;


public class ItemPoolsRedRoom {

    public static final String POOL_RED_PEDESTAL = "POOL_RED_PEDESTAL";
    public static final String POOL_BLACK_SLAB = "POOL_BLACK_SLAB";
    public static final String POOL_BLACK_PART = "POOL_BLACK_PART";

    public static void init() {

        // ==================== RED PEDESTAL ====================
        new ItemPool(POOL_RED_PEDESTAL)
                // Tier 1 (weight 10)
                .add(weighted(ModItems.BALLISTIC_GAUNTLET.get(), 1, 1, 10))
                .add(weighted(ModItems.ARMOR_POLISH.get(), 1, 1, 10))
                .add(weighted(ModItems.BANDAID.get(), 1, 1, 10))
                .add(weighted(ModItems.SERUM.get(), 1, 1, 10))
                //TODO QUARTZ_PLUTONIUM FLASK_INFUSION
                //.add(weighted(ModItems.QUARTZ_PLUTONIUM.get(), 1, 1, 10))
                .add(weighted(ModItems.MORNING_GLORY.get(), 1, 1, 10))
                .add(weighted(ModItems.SPIDER_MILK.get(), 1, 1, 10))
                .add(weighted(ModItems.INK.get(), 1, 1, 10))
                .add(weighted(ModItems.HEART_CONTAINER.get(), 1, 1, 10))
                .add(weighted(ModItems.BLACK_DIAMOND.get(), 1, 1, 10))
                .add(weighted(ModItems.SCRUMPY.get(), 1, 1, 10))
                // Tier 2 (weight 5)
                .add(weighted(ModItems.WILD_P.get(), 1, 1, 5))
                .add(weighted(ModItems.CARD_AOS.get(), 1, 1, 5))
                .add(weighted(ModItems.CARD_QOS.get(), 1, 1, 5))
                .add(weighted(ModToolItems.STARMETAL_SWORD.get(), 1, 1, 5))
                .add(weighted(ModItems.GEM_ALEXANDRITE.get(), 1, 1, 5))
                .add(weighted(ModItems.CRACKPIPE.get(), 1, 1, 5))
                //.add(weighted(ModItems.FLASK_INFUSION.get(), 1, 1, 5))
                .add(weighted(ModBlocks.BOXCAR.get().asItem(), 1, 1, 5))
                .add(weighted(ModItems.BOOK_OF_.get(), 1, 1, 5))
                // Tier 3 (weight 1)
                .add(weighted(ModGunItems.GUN_HANGMAN.get(), 1, 1, 1))
                .add(weighted(ModGunItems.GUN_MAS36.get(), 1, 1, 1))
                .add(weighted(ModItems.ITEM_SECRET_FOLLY.get(), 1, 1, 1))
                .add(weighted(ModItems.WEAPON_MOD_SPECIAL_NICKEL.get(), 1, 1, 1))
                .add(weighted(ModItems.WEAPON_MOD_SPECIAL_DOUBLOONS.get(), 1, 1, 1));

        // ==================== BLACK SLAB ====================
        new ItemPool(POOL_BLACK_SLAB)
                .add(weighted(ModItems.CLAY_TABLET.get(), 1, 1, 10));

        // ==================== BLACK PART ====================
        new ItemPool(POOL_BLACK_PART)
                .add(weighted(ModItems.ITEM_SECRET_SELENIUM_STEEL.get(), 4, 4, 10))
                .add(weighted(ModItems.ITEM_SECRET_CONTROLLER.get(), 1, 1, 10))
                .add(weighted(ModItems.ITEM_SECRET_CANISTER.get(), 1, 1, 10))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1));
    }
}