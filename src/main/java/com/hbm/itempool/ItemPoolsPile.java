package com.hbm.itempool;

import com.hbm.items.*;
import com.hbm.items.food.ItemConserve;
import net.minecraft.world.item.Items;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class ItemPoolsPile {

    public static final String POOL_PILE_HIVE = "POOL_PILE_HIVE";
    public static final String POOL_PILE_BONES = "POOL_PILE_BONES";
    public static final String POOL_PILE_CAPS = "POOL_PILE_CAPS";
    public static final String POOL_PILE_MED_SYRINGE = "POOL_PILE_MED_SYRINGE";
    public static final String POOL_PILE_MED_PILLS = "POOL_PILE_MED_PILLS";
    public static final String POOL_PILE_MAKESHIFT_GUN = "POOL_PILE_MAKESHIFT_GUN";
    public static final String POOL_PILE_MAKESHIFT_WRENCH = "POOL_PILE_MAKESHIFT_WRENCH";
    public static final String POOL_PILE_MAKESHIFT_PLATES = "POOL_PILE_MAKESHIFT_PLATES";
    public static final String POOL_PILE_MAKESHIFT_WIRE = "POOL_PILE_MAKESHIFT_WIRE";
    public static final String POOL_PILE_NUKE_STORAGE = "POOL_PILE_NUKE_STORAGE";
    public static final String POOL_PILE_OF_GARBAGE = "POOL_PILE_OF_GARBAGE";
    public static final String POOL_PILE_MECHANICAL = "POOL_PILE_MECHANICAL";
    public static final String POOL_PILE_GEAR = "POOL_PILE_GEAR";
    public static final String POOL_PILE_SUPPLIES = "POOL_PILE_SUPPLIES";

    public static void init() {

        // Items found in glyphid hives
        new ItemPool(POOL_PILE_HIVE)
                .add(weighted(Items.IRON_INGOT, 1, 3, 10))
                .add(weighted(ModItems.INGOT_STEEL.get(), 1, 2, 10))
                .add(weighted(ModItems.INGOT_ALUMINIUM.get(), 1, 2, 10))
                .add(weighted(ModItems.SCRAP.get(), 3, 6, 10))
                .add(weighted(ModArmorItems.GAS_MASK_M65.get(), 1, 1, 10))
                .add(weighted(ModArmorItems.STEEL_HELMET.get(), 1, 1, 5))
                .add(weighted(ModArmorItems.STEEL_CHESTPLATE.get(), 1, 1, 5))
                .add(weighted(ModArmorItems.STEEL_LEGGINGS.get(), 1, 1, 5))
                .add(weighted(ModArmorItems.STEEL_BOOTS.get(), 1, 1, 5))
                .add(weighted(ModToolItems.STEEL_PICKAXE.get(), 1, 1, 5))
                .add(weighted(ModToolItems.STEEL_SHOVEL.get(), 1, 1, 5))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 1, 5))
                .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 1, 1))
                .add(weighted(ModItems.GRENADE_IF_GENERIC.get(), 1, 2, 5))
                .add(weighted(ModAmmoItems.AMMO_G12.get(), 4, 4, 10))
                .add(weighted(ModAmmoItems.AMMO_M357_SP.get(), 6, 12, 10))
                .add(weighted(ModAmmoItems.AMMO_G40_HE.get(), 1, 1, 2))
                .add(weighted(ModItems.BOTTLE_NUKA.get(), 1, 2, 20))
                .add(weighted(ModItems.BOTTLE_QUANTUM.get(), 1, 2, 1))
                .add(weighted(ModItems.DEFINITELYFOOD.get(), 5, 12, 20))
                .add(weighted(ModItems.EGG_GLYPHID.get(), 1, 3, 30))
                .add(weighted(ModItems.SYRINGE_METAL_STIMPAK.get(), 1, 1, 5))
                .add(weighted(ModItems.IV_BLOOD.get(), 1, 1, 10))
                .add(weighted(Items.EXPERIENCE_BOTTLE, 1, 3, 5));

        // Items found in glyphid bone piles
        new ItemPool(POOL_PILE_BONES)
                .add(weighted(Items.BONE, 1, 1, 10))
                .add(weighted(Items.ROTTEN_FLESH, 1, 1, 5))
                .add(weighted(ModItems.BIOMASS.get(), 1, 1, 2));

        // Bottlecap stashes
        new ItemPool(POOL_PILE_CAPS)
                .add(weighted(ModItems.CAP_NUKA.get(), 4, 4, 20))
                .add(weighted(ModItems.CAP_QUANTUM.get(), 4, 4, 3))
                .add(weighted(ModItems.CAP_SPARKLE.get(), 4, 4, 1));

        // Medicine stashes - syringes
        new ItemPool(POOL_PILE_MED_SYRINGE)
                .add(weighted(ModItems.SYRINGE_METAL_STIMPAK.get(), 1, 1, 10))
                .add(weighted(ModItems.SYRINGE_METAL_MEDX.get(), 1, 1, 5))
                .add(weighted(ModItems.SYRINGE_METAL_PSYCHO.get(), 1, 1, 5));

        // Medicine stashes - pills
        new ItemPool(POOL_PILE_MED_PILLS)
                .add(weighted(ModItems.RADAWAY.get(), 1, 1, 10))
                .add(weighted(ModItems.RADX.get(), 1, 1, 10))
                .add(weighted(ModItems.IV_BLOOD.get(), 1, 1, 15))
                .add(weighted(ModItems.SIOX.get(), 1, 1, 5));

        // Makeshift gun
        new ItemPool(POOL_PILE_MAKESHIFT_GUN)
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 1, 10));

        new ItemPool(POOL_PILE_MAKESHIFT_WRENCH)
                .add(weighted(ModToolItems.WRENCH.get(), 1, 1, 10));

        new ItemPool(POOL_PILE_MAKESHIFT_PLATES)
                .add(weighted(ModItems.PLATE_STEEL.get(), 1, 1, 10));

        new ItemPool(POOL_PILE_MAKESHIFT_WIRE)
                .add(weighted(ModItems.WIRE_FINE_ALUMINIUM.get(), 1, 1, 10));

        // Nuclear storage
        new ItemPool(POOL_PILE_NUKE_STORAGE)
                .add(weighted(ModAmmoItems.AMMO_NUKE_STANDARD.get(), 1, 1, 50))
                .add(weighted(ModAmmoItems.AMMO_NUKE_HIGH.get(), 1, 1, 10))
                .add(weighted(ModAmmoItems.AMMO_NUKE_TOTS.get(), 1, 1, 10));

        // Garbage pile
        new ItemPool(POOL_PILE_OF_GARBAGE)
                .add(weighted(ModItems.PIPE_COPPER.get(), 0, 2, 20))
                .add(weighted(ModItems.SCRAP.get(), 1, 5, 20))
                .add(weighted(ModItems.DUST.get(), 1, 3, 40))
                .add(weighted(ModItems.DUST_TINY.get(), 1, 7, 40))
                .add(weighted(ModItems.POWDER_CEMENT.get(), 1, 6, 40))
                .add(weighted(ModItems.NUGGET_LEAD.get(), 0, 3, 20))
                .add(weighted(ModItems.WIRE_FINE_LEAD.get(), 1, 2, 20))
                .add(weighted(ModItems.POWDER_ASH.get(), 0, 1, 15))
                .add(weighted(ModItems.PLATE_LEAD.get(), 0, 1, 15))
                .add(weighted(Items.STRING, 0, 1, 15))
                .add(weighted(ModItems.BOLT_STEEL.get(), 0, 2, 15))
                .add(weighted(ModItems.PIN.get(), 0, 2, 15))
                .add(weighted(ModItems.CAP_NUKA.get(), 0, 8, 15))
                .add(weighted(ModItems.PLATE_IRON.get(), 0, 2, 15))
                .add(weighted(ModItems.FALLOUT.get(), 0, 2, 15))
                .add(weighted(ModItems.COIL_TUNGSTEN.get(), 0, 2, 15))
                .add(weighted(ModItems.CAN_EMPTY.get(), 0, 1, 15))
                .add(weighted(ModItems.INGOT_ASBESTOS.get(), 0, 1, 15))
                .add(weighted(ModItems.SYRINGE_METAL_EMPTY.get(), 0, 1, 15))
                .add(weighted(ModItems.SYRINGE_EMPTY.get(), 0, 1, 15))
                .add(weighted(ModItems.PIPE_LEAD.get(), 0, 1, 5))
                .add(weighted(ModItems.MOTOR.get(), 0, 1, 5))
                .add(weighted(ModItems.CANNED_CONSERVE.get(), ItemConserve.EnumFoodType.PASHTET.ordinal(), 0, 1, 5));

        // mechanical parts - found in factories and workshops
        new ItemPool(POOL_PILE_MECHANICAL) {{
            add(weighted(ModItems.DEFUSER.get(), 1, 1, 30));
            add(weighted(ModItems.SCREWDRIVER.get(), 1, 1, 30));
            add(weighted(ModItems.WIRE_FINE_COPPER.get(), 8, 12, 120));
            add(weighted(ModItems.PLATE_STEEL.get(), 3, 8, 40));
            add(weighted(ModItems.PLATE_COPPER.get(), 2, 5, 40));
            add(weighted(ModItems.COIL_COPPER.get(), 2, 5, 40));
            add(weighted(ModItems.COIL_TUNGSTEN.get(), 2, 5, 40));
        }};

        // gear - personal equipment found in military stashes
        new ItemPool(POOL_PILE_GEAR) {{
            add(weighted(ModItems.DEFUSER.get(), 1, 1, 40));
            add(weighted(ModItems.SCREWDRIVER.get(), 1, 1, 30));
            add(weighted(ModItems.CANTEEN_VODKA.get(), 1, 1, 40));
            add(weighted(ModItems.CASING_SMALL_STEEL.get(), 1, 4, 30));
            add(weighted(ModItems.CASING_SMALL.get(), 3, 8, 40));
            add(weighted(ModItems.CASING_BUCKSHOT.get(), 3, 8, 40));
            add(weighted(ModItems.CANNED_CONSERVE.get(), 2, 5, 40));
            add(weighted(ModArmorItems.TAURUN_HELMET.get(), 1, 1, 20));
            add(weighted(ModArmorItems.TAURUN_CHESTPLATE.get(), 1, 1, 20));
            add(weighted(ModArmorItems.TAURUN_LEGGINGS.get(), 1, 1, 20));
            add(weighted(ModArmorItems.TAURUN_BOOTS.get(), 1, 1, 20));
        }};

        // supplies - grenades, medical, ammo container
        new ItemPool(POOL_PILE_SUPPLIES) {{
            //TODO ItemGrenadeUniversal
            //add(weighted(ItemGrenadeUniversal.make(EnumGrenadeShell.FRAG, EnumGrenadeFilling.HE, EnumGrenadeFuze.S3, EnumGrenadeExtra.FRAG_SLEEVE), 3, 5, 10));
            //add(weighted(ItemGrenadeUniversal.make(EnumGrenadeShell.FRAG, EnumGrenadeFilling.HE, EnumGrenadeFuze.S3, EnumGrenadeExtra.FRAG_SLEEVE), 3, 5, 10));
            //add(weighted(ItemGrenadeUniversal.make(EnumGrenadeShell.FRAG, EnumGrenadeFilling.HE, EnumGrenadeFuze.S3, EnumGrenadeExtra.FRAG_SLEEVE), 3, 5, 10));
            add(weighted(ModItems.SYRINGE_METAL_STIMPAK.get(), 3, 5, 30));
            add(weighted(ModItems.SYRINGE_METAL_PSYCHO.get(), 3, 5, 30));
            add(weighted(ModItems.SYRINGE_ANTIDOTE.get(), 1, 2, 30));
            add(weighted(ModItems.AMMO_CONTAINER.get(), 2, 3, 40));
        }};
    }
}