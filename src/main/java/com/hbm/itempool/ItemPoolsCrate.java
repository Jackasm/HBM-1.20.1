package com.hbm.itempool;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class ItemPoolsCrate {

    public static final ItemPool SUPPLY = new ItemPool()
            .add(weighted(ModItems.SYRINGE_METAL_STIMPAK.get(), 1, 3, 10))
            .add(weighted(ModItems.SYRINGE_ANTIDOTE.get(), 1, 2, 5))
            .add(weighted(ModItems.GRENADE_GENERIC.get(), 1, 2, 8))
            .add(weighted(ModItems.GRENADE_STRONG.get(), 1, 2, 6))
            .add(weighted(ModItems.GRENADE_MK2.get(), 1, 1, 4))
            .add(weighted(ModItems.GRENADE_FLARE.get(), 1, 2, 4));

    public static final ItemPool WEAPON = new ItemPool()
            .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 1, 10))
            .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 1, 7))
            .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 1, 5))
            .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 1, 5))
            .add(weighted(ModGunItems.GUN_LIBERATOR.get(), 1, 1, 2))
            .add(weighted(ModGunItems.GUN_FLAREGUN.get(), 1, 1, 8))
            .add(weighted(ModGunItems.GUN_PANZERSCHRECK.get(), 1, 1, 1));

    public static final ItemPool LEAD = new ItemPool()
            .add(weighted(ModItems.INGOT_URANIUM.get(), 1, 3, 10))
            .add(weighted(ModItems.INGOT_U238.get(), 1, 3, 8))
            .add(weighted(ModItems.INGOT_PLUTONIUM.get(), 1, 2, 7))
            .add(weighted(ModItems.INGOT_PU240.get(), 1, 2, 6))
            .add(weighted(ModItems.INGOT_NEPTUNIUM.get(), 1, 2, 7))
            .add(weighted(ModItems.INGOT_URANIUM_FUEL.get(), 1, 2, 8))
            .add(weighted(ModItems.INGOT_PLUTONIUM_FUEL.get(), 1, 2, 7))
            .add(weighted(ModItems.INGOT_MOX_FUEL.get(), 1, 2, 6))
            .add(weighted(ModItems.NUGGET_URANIUM.get(), 2, 5, 10))
            .add(weighted(ModItems.NUGGET_U238.get(), 2, 5, 8))
            .add(weighted(ModItems.NUGGET_PLUTONIUM.get(), 2, 4, 7))
            .add(weighted(ModItems.NUGGET_PU240.get(), 2, 4, 6))
            .add(weighted(ModItems.NUGGET_NEPTUNIUM.get(), 2, 4, 7))
            .add(weighted(ModItems.NUGGET_URANIUM_FUEL.get(), 2, 4, 8))
            .add(weighted(ModItems.NUGGET_PLUTONIUM_FUEL.get(), 2, 4, 7))
            .add(weighted(ModItems.NUGGET_MOX_FUEL.get(), 2, 4, 6))
            .add(weighted(ModItems.CELL_DEUTERIUM.get(), 1, 2, 8))
            .add(weighted(ModItems.CELL_TRITIUM.get(), 1, 2, 8))
            .add(weighted(ModItems.CELL_UF6.get(), 1, 2, 8))
            .add(weighted(ModItems.CELL_PUF6.get(), 1, 2, 8))
            .add(weighted(ModItems.PELLET_RTG.get(), 1, 1, 6))
            .add(weighted(ModItems.PELLET_RTG_WEAK.get(), 1, 2, 7))
            .add(weighted(ModItems.POWDER_YELLOWCAKE.get(), 2, 4, 10));

    public static final ItemPool METAL = new ItemPool()
            .add(weighted(ModBlocks.MACHINE_PRESS.get().asItem(), 1, 1, 10))
            .add(weighted(ModBlocks.MACHINE_BLAST_FURNACE.get().asItem(), 1, 1, 9))
            .add(weighted(ModItems.MOTOR.get(), 1, 2, 8))
            .add(weighted(ModItems.COIL_TUNGSTEN.get(), 1, 2, 7))
            .add(weighted(ModBlocks.MACHINE_DIESEL.get().asItem(), 1, 1, 8))
            .add(weighted(ModItems.COIL_COPPER.get(), 2, 4, 10))
            .add(weighted(ModItems.BLADE_TITANIUM.get(), 1, 1, 3))
            .add(weighted(ModBlocks.MACHINE_FLUIDTANK.get().asItem(), 1, 1, 7))
            /* TODO: Добавить все эти блоки и предметы
            .add(weighted(ModBlocks.MACHINE_RTG_GREY.get().asItem(), 1, 1, 4))
            .add(weighted(ModBlocks.RED_PYLON.get().asItem(), 1, 1, 9))
            .add(weighted(ModBlocks.MACHINE_BATTERY.get().asItem(), 1, 1, 8))
            .add(weighted(ModBlocks.MACHINE_LITHIUM_BATTERY.get().asItem(), 1, 1, 5))
            .add(weighted(ModBlocks.MACHINE_ELECTRIC_FURNACE_OFF.get().asItem(), 1, 1, 8))
            .add(weighted(ModBlocks.MACHINE_ASSEMBLY_MACHINE.get().asItem(), 1, 1, 10))

            .add(weighted(ModItems.CENTRIFUGE_ELEMENT.get(), 1, 2, 6))
            .add(weighted(ModBlocks.MACHINE_REACTOR_BREEDING.get().asItem(), 1, 1, 6))
            .add(weighted(ModBlocks.MACHINE_WOOD_BURNER.get().asItem(), 1, 1, 10))
            .add(weighted(ModItems.PHOTO_PANEL.get(), 1, 1, 3))
            .add(weighted(ModItems.PISTON_SELENIUM.get(), 1, 1, 6))
             */
            ;

    public static final ItemPool RED = new ItemPool()
            .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER_LILMAC.get(), 1, 1, 1))
            .add(weighted(ModGunItems.GUN_AUTOSHOTGUN_SEXY.get(), 1, 1, 1))
            .add(weighted(ModGunItems.GUN_MARESLEG_BROKEN.get(), 1, 1, 1))
            .add(weighted(new ItemStack(ModAmmoItems.AMMO_M44_EQUESTRIAN.get(), 1), 1, 1, 1))
            .add(weighted(new ItemStack(ModAmmoItems.AMMO_G12_EQUESTRIAN.get(), 1), 1, 1, 1))
            .add(weighted(new ItemStack(ModAmmoItems.AMMO_BMG50_EQUESTRIAN.get(), 1), 1, 1, 1))
            .add(weighted(ModBlocks.BROADCASTER_PC.get().asItem(), 1, 1, 1))
            .add(weighted(ModItems.FLAME_PONY.get(), 1, 1, 1))
            .add(weighted(ModItems.BATTERY_SPARK.get(), 1, 1, 1))
            .add(weighted(ModItems.BOTTLE_SPARKLE.get(), 1, 1, 1))
            .add(weighted(ModItems.BOTTLE_RAD.get(), 1, 1, 1))
            .add(weighted(ModItems.RING_STARMETAL.get(), 1, 1, 1))
            .add(weighted(ModBlocks.NTM_DIRT.get().asItem(), 1, 1, 1))
            .add(weighted(ModItems.MYSTERYSHOVEL.get(), 1, 1, 1));

    public static final ItemPool JUNGLE = new ItemPool()
            .add(weighted(Items.GOLD_INGOT, 4, 8, 10))
            .add(weighted(Items.GOLD_NUGGET, 8, 18, 10))
            .add(weighted(ModItems.POWDER_GOLD.get(), 2, 5, 10))
            .add(weighted(ModItems.WIRE_GOLD.get(), 4, 9, 10))
            .add(weighted(ModItems.WIRE_DENSE_GOLD.get(), 1, 3, 10))
            .add(weighted(ModItems.PLATE_GOLD.get(), 1, 3, 5)) // шанс 5 из 10 = 50%
            .add(weighted(ModItems.CRYSTAL_GOLD.get(), 1, 1, 3)); // шанс 3 из 10 ≈ 33%

    public static ItemPool getSupplyPool() { return SUPPLY; }
    public static ItemPool getWeaponPool() { return WEAPON; }
    public static ItemPool getLeadPool()   { return LEAD; }
    public static ItemPool getMetalPool()  { return METAL; }
    public static ItemPool getRedPool()    { return RED; }
    public static ItemPool getJunglePool()    { return JUNGLE; }
}