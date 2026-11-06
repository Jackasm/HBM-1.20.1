package com.hbm.itempool;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.*;
import com.hbm.items.fluid.ItemFluidCanister;
import net.minecraft.world.item.Items;

import static com.hbm.itempool.ItemPoolHelper.weighted;
import static com.hbm.itempool.ItemPoolHelper.weightedDamaged;

public class ItemPoolsLegacy {

    public static final String POOL_ANTENNA = "POOL_ANTENNA";
    public static final String POOL_METEORITE_TREASURE = "POOL_METEORITE_TREASURE";
    public static final String POOL_EXPENSIVE = "POOL_EXPENSIVE";
    public static final String POOL_GENERIC = "POOL_GENERIC";
    public static final String POOL_NUKE_TRASH = "POOL_NUKE_TRASH";
    public static final String POOL_NUKE_MISC = "POOL_NUKE_MISC";
    public static final String POOL_VERTIBIRD = "POOL_VERTIBIRD";
    public static final String POOL_SPACESHIP = "POOL_SPACESHIP";

    public static void init() {
        new ItemPool(POOL_ANTENNA)
                .add(weighted(ModItems.TWINKIE.get(), 1, 3, 4))
                .add(weighted(ModItems.INGOT_STEEL.get(), 1, 2, 7))
                .add(weighted(ModItems.INGOT_RED_COPPER.get(), 1, 1, 4))
                .add(weighted(ModItems.INGOT_TITANIUM.get(), 1, 3, 5))
                .add(weighted(ModItems.WIRE_FINE_ADVANCED_ALLOY.get(), 2, 3, 7))
                .add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 1, 1, 4))
                .add(weighted(ModItems.CIRCUIT_CAPACITOR.get(), 1, 1, 2))
                .add(weighted(ModItems.BATTERY_GENERIC.get(), 1, 1, 4))
                .add(weighted(ModItems.BATTERY_ADVANCED.get(), 1, 1, 3))
                .add(weighted(ModItems.POWDER_IODINE.get(), 1, 1, 1))
                .add(weighted(ModItems.POWDER_BROMINE.get(), 1, 1, 1))
                .add(weighted(ModBlocks.STEEL_POLES.get().asItem(), 1, 4, 8))
                .add(weighted(ModBlocks.STEEL_SCAFFOLD_STEEL.get().asItem(), 1, 3, 8))
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

        new ItemPool(POOL_METEORITE_TREASURE)
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

        new ItemPool(POOL_EXPENSIVE) {{
            //add(weighted(ModItems.CHLORINE_PINWHEEL.get(), 1, 1, 1));
            add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 1, 1, 4));
            add(weighted(ModItems.CIRCUIT_ANALOG.get(), 1, 1, 3));
            add(weighted(ModItems.CIRCUIT_CHIP.get(), 1, 1, 2));
            add(weighted(ModItems.GUN_KIT_1.get(), 1, 3, 6));
            add(weighted(ModItems.GUN_KIT_2.get(), 1, 2, 3));
            add(weighted(ModGunItems.GUN_PANZERSCHRECK.get(), 1, 1, 4));
            add(weighted(ModAmmoItems.AMMO_ROCKET_HE.get(), 1, 4, 5));
            add(weighted(ModAmmoItems.AMMO_G26_FLARE_SUPPLY.get(), 1, 1, 5));
            add(weighted(ModAmmoItems.AMMO_G26_FLARE.get(), 1, 1, 3));
            add(weighted(ModItems.GRENADE_NUKE.get(), 1, 1, 2));
            add(weighted(ModItems.GRENADE_CLUSTER.get(), 1, 3, 3));
            //add(weighted(ModItems.GRENADE_IF_TRIPLEX.get(), 1, 1, 1));
            add(weighted(ModItems.STEALTH_BOY.get(), 1, 1, 2));
            add(weighted(ModItems.BATTERY_LITHIUM.get(), 1, 1, 1));
            add(weighted(ModItems.SYRINGE_AWESOME.get(), 1, 1, 1));
            add(weighted(ModItems.FUSION_CORE.get(), 1, 1, 4));
            add(weighted(ModItems.BOTTLE_NUKA.get(), 1, 3, 6));
            add(weighted(ModItems.BOTTLE_QUANTUM.get(), 1, 1, 3));
            add(weighted(ModBlocks.RED_BARREL.get().asItem(), 1, 1, 6));
            add(weighted(ItemFluidCanister.createForFluid(Fluids.DIESEL.get()), 1, 2, 2));
            add(weighted(ItemFluidCanister.createForFluid(Fluids.BIOFUEL.get()), 1, 2, 3));
            add(weightedDamaged(ModArmorItems.GAS_MASK_M65.get(), 60, 1, 1, 5));
            add(weighted(ModItems.BOMB_CALLER_CARPET.get(), 1, 1, 2));
            add(weighted(ModItems.BOMB_CALLER_ORANGE.get(), 1, 1, 1));
            add(weighted(ModItems.BOMB_CALLER_ORANGE.get(), 1, 1, 1));  // третий вариант — поправь Item
            add(weighted(ModItems.GAS_MASK_FILTER.get(), 1, 1, 4));
            add(weighted(ModItems.LAUNCH_CODE_PIECE.get(), 1, 1, 1));
            add(weighted(ModGunItems.GUN_DOUBLE_BARREL.get(), 1, 1, 1));
            add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 1));
        }};

        new ItemPool(POOL_GENERIC) {{
            add(weighted(Items.BREAD, 1, 5, 8));
            add(weighted(ModItems.TWINKIE.get(), 1, 3, 6));
            add(weighted(Items.IRON_INGOT, 2, 6, 10));
            add(weighted(ModItems.INGOT_STEEL.get(), 2, 5, 7));
            add(weighted(ModItems.INGOT_BERYLLIUM.get(), 1, 2, 4));
            add(weighted(ModItems.INGOT_TITANIUM.get(), 1, 1, 3));
            add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 1, 1, 5));
            add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 1, 3));
            add(weighted(ModAmmoItems.AMMO_M357_SP.get(), 2, 6, 4));
            add(weighted(ModAmmoItems.AMMO_G12_BP.get(), 3, 6, 3));
            add(weighted(ModAmmoItems.AMMO_G26_FLARE_SUPPLY.get(), 1, 1, 1));
            add(weighted(ModItems.GUN_KIT_1.get(), 1, 3, 4));
            add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 1, 1));
            add(weighted(ModItems.CASING_SMALL.get(), 4, 10, 3));
            add(weighted(ModItems.CASING_BUCKSHOT.get(), 4, 10, 3));
            add(weighted(ModItems.CORDITE.get(), 4, 6, 5));
            add(weighted(ModItems.BATTERY_GENERIC.get(), 1, 1, 1));
            add(weighted(ModItems.SCRAP.get(), 1, 3, 10));
            add(weighted(ModItems.DUST.get(), 2, 4, 9));
            add(weighted(ModToolItems.BOTTLE_OPENER.get(), 1, 1, 2));
            add(weighted(ModItems.BOTTLE_NUKA.get(), 1, 3, 4));
            add(weighted(ModItems.BOTTLE_CHERRY.get(), 1, 1, 2));
            add(weighted(ModItems.STEALTH_BOY.get(), 1, 1, 1));
            add(weighted(ModItems.CAP_NUKA.get(), 1, 15, 7));
            add(weighted(ItemFluidCanister.createForFluid(Fluids.DIESEL.get()), 1, 2, 2));
            add(weighted(ItemFluidCanister.createForFluid(Fluids.BIOFUEL.get()), 1, 2, 3));
            add(weightedDamaged(ModArmorItems.GAS_MASK_M65.get(), 60, 1, 1, 2));
            add(weighted(ModItems.GAS_MASK_FILTER.get(), 1, 1, 3));
            add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 1));
            add(weighted(ModItems.COIN_TOKEN.get(), 1, 1, 2));
        }};

        new ItemPool(POOL_NUKE_TRASH) {{
            add(weighted(ModItems.NUGGET_U238.get(), 3, 12, 5));
            add(weighted(ModItems.NUGGET_PU240.get(), 3, 8, 5));
            add(weighted(ModItems.NUGGET_NEPTUNIUM.get(), 1, 4, 3));
            //add(weighted(ModItems.PILE_ROD_U238.get(), 1, 1, 3));
            //add(weighted(ModItems.PILE_ROD_U238_DUAL.get(), 1, 1, 3));
            //add(weighted(ModItems.PILE_ROD_U238_QUAD.get(), 1, 1, 3));
            add(weighted(ModItems.BOTTLE_QUANTUM.get(), 1, 1, 1));
            add(weightedDamaged(ModArmorItems.GAS_MASK_M65.get(), 60, 1, 1, 5));
            add(weighted(ModItems.HAZMAT_KIT.get(), 1, 1, 1));
            add(weighted(ModItems.GAS_MASK_FILTER.get(), 1, 1, 5));
            add(weighted(ModBlocks.YELLOW_BARREL.get().asItem(), 1, 1, 2));
        }};

        new ItemPool(POOL_NUKE_MISC) {{
            add(weighted(ModItems.NUGGET_U235.get(), 3, 12, 5));
            add(weighted(ModItems.NUGGET_PU238.get(), 3, 12, 5));
            add(weighted(ModItems.NUGGET_RA226.get(), 3, 6, 5));
            //TODO стержни
            //add(weighted(ModItems.PILE_ROD_U235.get(), 1, 1, 3));
            //add(weighted(ModItems.PILE_ROD_U235_DUAL.get(), 1, 1, 3));
            //add(weighted(ModItems.PILE_ROD_U235_QUAD.get(), 1, 1, 3));
            //add(weighted(ModItems.ROD_ZIRNOX_URANIUM_FUEL.get(), 1, 1, 4));
            //add(weighted(ModItems.ROD_ZIRNOX_MOX_FUEL.get(), 1, 1, 4));
            //add(weighted(ModItems.ROD_ZIRNOX_LITHIUM.get(), 1, 1, 3));
            //add(weighted(ModItems.ROD_ZIRNOX_THORIUM_FUEL.get(), 1, 1, 3));
            //add(weighted(ModItems.PILE_ROD_THF_DUAL.get(), 1, 1, 3));
            add(weighted(ModItems.ROD_ZIRNOX_TRITIUM.get(), 1, 1, 1));
            //add(weighted(ModItems.ROD_ZIRNOX_U233_FUEL.get(), 1, 1, 1));
            //add(weighted(ModItems.ROD_ZIRNOX_U235_FUEL.get(), 1, 1, 1));
            add(weighted(ModItems.PELLET_RTG.get(), 1, 1, 3));
            add(weighted(ModItems.POWDER_THORIUM.get(), 1, 1, 1));
            add(weighted(ModItems.POWDER_NEPTUNIUM.get(), 1, 1, 1));
            add(weighted(ModItems.POWDER_STRONTIUM.get(), 1, 1, 1));
            add(weighted(ModItems.POWDER_COBALT.get(), 1, 1, 1));
            add(weighted(ModItems.BOTTLE_QUANTUM.get(), 1, 1, 1));
            add(weightedDamaged(ModArmorItems.GAS_MASK_M65.get(), 60, 1, 1, 5));
            add(weighted(ModItems.HAZMAT_KIT.get(), 1, 1, 2));
            add(weighted(ModItems.GAS_MASK_FILTER.get(), 1, 1, 5));
            add(weighted(ModBlocks.YELLOW_BARREL.get().asItem(), 1, 3, 3));
        }};

        new ItemPool(POOL_VERTIBIRD) {{
            add(weighted(ModArmorItems.T51_HELMET.get(), 1, 1, 15));
            add(weighted(ModArmorItems.T51_CHESTPLATE.get(), 1, 1, 15));
            add(weighted(ModArmorItems.T51_LEGGINGS.get(), 1, 1, 15));
            add(weighted(ModArmorItems.T51_BOOTS.get(), 1, 1, 15));
            add(weighted(ModItems.FUSION_CORE.get(), 1, 1, 10));
            add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 1, 4));
            add(weighted(ModItems.GUN_KIT_1.get(), 2, 3, 4));
            add(weighted(ModAmmoItems.AMMO_M357_FMJ.get(), 1, 24, 4));
            add(weighted(ModAmmoItems.AMMO_G40_HE.get(), 1, 6, 3));
            add(weighted(ModAmmoItems.AMMO_G26_FLARE.get(), 1, 1, 5));
            //add(weighted(ModItems.PILE_ROD_U235.get(), 1, 1, 2));
            add(weighted(ModItems.BILLET_URANIUM_FUEL.get(), 1, 1, 2));
            add(weighted(ModItems.INGOT_URANIUM_FUEL.get(), 1, 1, 2));
            add(weighted(ModItems.BOTTLE_NUKA.get(), 1, 3, 6));
            add(weighted(ModItems.BOTTLE_QUANTUM.get(), 1, 1, 3));
            add(weighted(ModItems.STEALTH_BOY.get(), 1, 1, 7));
            add(weighted(ModArmorItems.GAS_MASK_M65.get(), 1, 1, 5));
            add(weighted(ModItems.GAS_MASK_FILTER.get(), 1, 1, 5));
            add(weighted(ModItems.GRENADE_NUKE.get(), 1, 2, 2));
            add(weighted(ModItems.BOMB_CALLER_CARPET.get(), 1, 1, 1));
            add(weighted(ModItems.BOMB_CALLER_ORANGE.get(), 1, 1, 1));
            add(weighted(ModItems.BOMB_CALLER_ORANGE.get(), 1, 1, 2));  // третий вариант — поправь Item
        }};

        new ItemPool(POOL_SPACESHIP) {{
            add(weighted(ModItems.BATTERY_PACK_BATTERY_LEAD.get(), 1, 1, 2));
            add(weighted(ModItems.COIL_COPPER.get(), 2, 16, 5));
            add(weighted(ModItems.WIRE_FINE_RED_COPPER.get(), 8, 32, 5));
            add(weighted(ModItems.CELL_DEUTERIUM.get(), 1, 8, 5));
            add(weighted(ModItems.CELL_TRITIUM.get(), 1, 8, 5));
            add(weighted(ModItems.CELL_ANTIMATTER.get(), 1, 1, 1));
            add(weighted(ModItems.POWDER_NEODYMIUM.get(), 1, 1, 1));
            add(weighted(ModItems.POWDER_NIOBIUM.get(), 1, 1, 1));
            add(weighted(ModItems.WIRE_DENSE_RED_COPPER.get(), 2, 4, 5));
            add(weighted(ModItems.WIRE_DENSE_GOLD.get(), 1, 3, 5));
            //add(weighted(ModBlocks.PWR_FUEL.get().asItem(), 1, 2, 5));
            add(weighted(ModBlocks.BLOCK_TUNGSTEN.get().asItem(), 3, 8, 5));
            add(weighted(ModBlocks.RED_WIRE_COATED.get().asItem(), 4, 8, 5));
            add(weighted(ModBlocks.RED_CABLE.get().asItem(), 8, 16, 5));
        }};
    }
}