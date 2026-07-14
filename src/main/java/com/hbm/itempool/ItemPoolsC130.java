package com.hbm.itempool;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.ItemFluidCanister;
import com.hbm.items.weapon.sedna.factory.GunFactory.EnumAmmo;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class ItemPoolsC130 {

    private static final ItemPool POOL_SUPPLIES = new ItemPool()
            .add(weighted(ModItems.DEFINITELYFOOD.get(), 1, 3, 10, 25))
            .add(weighted(ModItems.SYRINGE_METAL_STIMPAK.get(), 1, 3, 10))
            .add(weighted(ModItems.PILL_IODINE.get(), 1, 2, 2))
            .add(weighted(ItemFluidCanister.createForFluid(Fluids.DIESEL.get()), 1, 4, 5))
            .add(weighted(ModBlocks.MACHINE_DIESEL.get().asItem(), 1, 1, 1))
            .add(weighted(ModItems.GEIGER_COUNTER.get(), 1, 1, 2))
            .add(weighted(ModItems.MED_BAG.get(), 1, 1, 3))
            .add(weighted(ModItems.RADAWAY.get(), 1, 5, 10));

    private static final ItemPool POOL_WEAPONS = new ItemPool()
            .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 1, 100))
            .add(weighted(ModGunItems.GUN_HENRY.get(), 1, 1, 100))
            .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 1, 100))
            .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 1, 100))
            .add(weighted(ModGunItems.GUN_CARBINE.get(), 1, 1, 50))
            .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 1, 50))
            .add(weighted(ModGunItems.GUN_PANZERSCHRECK.get(), 1, 1, 20))
            .add(weighted(ModGunItems.GUN_DOUBLE_BARREL.get(), 1, 1, 10))
            .add(weighted(ModGunItems.GUN_N_I_4_N_I.get(), 1, 1, 1));

    private static final ItemPool POOL_AMMO = new ItemPool()
            .add(weighted(ModAmmoItems.AMMO_M357_SP.get(), EnumAmmo.M357_SP.ordinal(), 12, 12, 10))
            .add(weighted(ModAmmoItems.AMMO_M357_FMJ.get(), EnumAmmo.M357_FMJ.ordinal(), 6, 6, 10))
            .add(weighted(ModAmmoItems.AMMO_M44_SP.get(), EnumAmmo.M44_SP.ordinal(), 12, 12, 5))
            .add(weighted(ModAmmoItems.AMMO_M44_FMJ.get(), EnumAmmo.M44_FMJ.ordinal(), 6, 6, 5))
            .add(weighted(ModAmmoItems.AMMO_P9_SP.get(), EnumAmmo.P9_SP.ordinal(), 12, 12, 10))
            .add(weighted(ModAmmoItems.AMMO_P9_FMJ.get(), EnumAmmo.P9_FMJ.ordinal(), 6, 6, 10))
            .add(weighted(ModAmmoItems.AMMO_R762_SP.get(), EnumAmmo.R762_SP.ordinal(), 6, 6, 5))
            .add(weighted(ModAmmoItems.AMMO_G12_BP.get(), EnumAmmo.G12_BP.ordinal(), 6, 6, 10))
            .add(weighted(ModAmmoItems.AMMO_ROCKET_HE.get(), EnumAmmo.ROCKET_HE.ordinal(), 1, 1, 3));

    public static ItemPool getSuppliesPool() {
        return POOL_SUPPLIES;
    }

    public static ItemPool getWeaponsPool() {
        return POOL_WEAPONS;
    }

    public static ItemPool getAmmoPool() {
        return POOL_AMMO;
    }
}