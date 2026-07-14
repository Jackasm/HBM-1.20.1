package com.hbm.itempool;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class ItemPoolsLegacy {

    private static final ItemPool POOL_ANTENNA = new ItemPool()
            .add(weighted(ModItems.TWINKIE.get(), 1, 3, 4))
            .add(weighted(ModItems.INGOT_STEEL.get(), 1, 2, 7))
            .add(weighted(ModItems.INGOT_RED_COPPER.get(), 1, 1, 4))
            .add(weighted(ModItems.INGOT_TITANIUM.get(), 1, 3, 5))
            .add(weighted(ModItems.WIRE_ADVANCED_ALLOY.get(), 2, 3, 7))
            .add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 1, 1, 4))
            .add(weighted(ModItems.CIRCUIT_CAPACITOR.get(), 1, 1, 2))
            .add(weighted(ModItems.BATTERY_GENERIC.get(), 1, 1, 4))
            .add(weighted(ModItems.BATTERY_ADVANCED.get(), 1, 1, 3))
            .add(weighted(ModItems.POWDER_IODINE.get(), 1, 1, 1))
            .add(weighted(ModItems.POWDER_BROMINE.get(), 1, 1, 1))
            .add(weighted(ModBlocks.STEEL_POLES.get().asItem(), 1, 4, 8))
            .add(weighted(ModBlocks.STEEL_SCAFFOLD.get().asItem(), 1, 3, 8))
            .add(weighted(ModBlocks.POLE_TOP.get().asItem(), 1, 1, 4))
            .add(weighted(ModBlocks.POLE_SATELLITE_RECEIVER.get().asItem(), 1, 1, 7))
            .add(weighted(ModItems.SCRAP.get(), 1, 3, 10))
            .add(weighted(ModItems.DUST.get(), 2, 4, 9))
            .add(weighted(ModToolItems.BOTTLE_OPENER.get(), 1, 1, 2))
            .add(weighted(ModItems.BOTTLE_NUKA.get(), 1, 3, 4))
            .add(weighted(ModItems.BOTTLE_CHERRY.get(), 1, 1, 2))
            .add(weighted(ModItems.STEALTH_BOY.get(), 1, 1, 1))
            .add(weighted(ModItems.CAP_NUKA.get(), 1, 15, 7))
            .add(weighted(ModItems.BOMB_CALLER_CARPET.get(), 1, 1, 1))
            .add(weighted(ModItems.GAS_MASK_FILTER.get(), 1, 1, 2));

    public static final ItemPool POOL_METEORITE_TREASURE = new ItemPool()
                .add(weighted(ModToolItems.COBALT_PICKAXE.get(), 0, 1, 1, 10))
                .add(weighted(ModItems.INGOT_ZIRCONIUM.get(), 0, 1, 16, 10))
                .add(weighted(ModItems.INGOT_NIOBIUM.get(), 0, 1, 16, 10))
                .add(weighted(ModItems.INGOT_COBALT.get(), 0, 1, 16, 10))
                .add(weighted(ModItems.INGOT_BORON.get(), 0, 1, 16, 10))
                .add(weighted(ModItems.INGOT_STARMETAL.get(), 0, 1, 1, 5))
                .add(weighted(ModItems.CRYSTAL_GOLD.get(), 0, 1, 4, 10))
                .add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 4, 8, 10))
                .add(weighted(ModItems.CIRCUIT_CHIP.get(), 2, 4, 10))
                .add(weighted(ModItems.DEFINITELYFOOD.get(), 0, 16, 32, 25))
                .add(weighted(ModItems.PILL_HERBAL.get(), 0, 1, 2, 10))
                .add(weighted(ModItems.EGG_GLYPHID.get(), 0, 1, 1, 5))
                .add(weighted(ModItems.GEM_ALEXANDRITE.get(), 0, 1, 1, 1))
                .add(weighted(ModItems.SERUM.get(), 0, 1, 1, 5))
                .add(weighted(ModItems.HEART_PIECE.get(), 0, 1, 1, 5))
                .add(weighted(ModItems.SCRUMPY.get(), 0, 1, 1, 5))
                .add(weighted(ModItems.LAUNCH_CODE_PIECE.get(), 0, 1, 1, 5))
                .add(weighted(ModBlocks.CRATE_CAN.get().asItem(), 0, 1, 3, 10))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 1));

    public static ItemPool getAntennaPool() {
        return POOL_ANTENNA;
    }
}