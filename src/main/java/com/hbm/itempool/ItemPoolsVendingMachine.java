package com.hbm.itempool;

import com.hbm.items.ModItems;
import com.hbm.items.food.ItemConserve.EnumFoodType;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class ItemPoolsVendingMachine {

    public static final String POOL_SODA = "POOL_SODA";
    public static final String POOL_SNACKS = "POOL_SNACKS";

    public static void init() {
        new ItemPool(POOL_SODA)
                .add(weighted(ModItems.BOTTLE_NUKA.get(), 0, 1, 1, 10))
                .add(weighted(ModItems.BOTTLE_CHERRY.get(), 0, 1, 1, 5))
                .add(weighted(ModItems.BOTTLE_QUANTUM.get(), 0, 1, 1, 1))
                .add(weighted(ModItems.CAN_BEPIS.get(), 0, 1, 1, 10))
                .add(weighted(ModItems.CAN_LUNA.get(), 0, 1, 1, 10))
                .add(weighted(ModItems.CAN_MUG.get(), 0, 1, 1, 10))
                .add(weighted(ModItems.CAN_BREEN.get(), 0, 1, 1, 1));

        new ItemPool(POOL_SNACKS)
                .add(weighted(ModItems.DEFINITELYFOOD.get(), 0, 1, 1, 10))
                .add(weighted(ModItems.CANNED_CONSERVE.get(), EnumFoodType.BEEF.ordinal(), 1, 1, 5))
                .add(weighted(ModItems.CANNED_CONSERVE.get(), EnumFoodType.TUBE.ordinal(), 1, 1, 5))
                .add(weighted(ModItems.TWINKIE.get(), 0, 1, 1, 10))
                .add(weighted(ModItems.CHOCOLATE.get(), 0, 1, 1, 10));
    }
}