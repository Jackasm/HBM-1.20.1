package com.hbm.itempool;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.*;
import net.minecraft.world.item.Items;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class ItemPoolsSingle {

    public static final String POOL_VAULT_RUSTY = "POOL_VAULT_RUSTY";
    public static final String POOL_VAULT_STANDARD = "POOL_VAULT_STANDARD";
    public static final String POOL_VAULT_REINFORCED = "POOL_VAULT_REINFORCED";
    public static final String POOL_VAULT_UNBREAKABLE = "POOL_VAULT_UNBREAKABLE";
    public static final String POOL_METEORITE_TREASURE = "POOL_METEORITE_TREASURE";
    public static final String POOL_BLUEPRINTS = "POOL_BLUEPRINTS";

    public static void init() {

        // ==================== VAULT RUSTY ====================
        new ItemPool(POOL_VAULT_RUSTY)
                .add(weighted(Items.GOLD_INGOT, 3, 14, 1))
                .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 1, 2))
                .add(weighted(ModItems.PIN.get(), 8, 8, 1))
                .add(weighted(ModGunItems.GUN_AM180.get(), 1, 1, 1))
                .add(weighted(ModItems.BOTTLE_QUANTUM.get(), 1, 3, 1))
                .add(weighted(ModItems.INGOT_COBALT.get(), 4, 12, 1))
                .add(weighted(ModAmmoItems.AMMO_BMG50_FMJ.get(), 24, 48, 1))
                .add(weighted(ModAmmoItems.AMMO_P9_JHP.get(), 48, 64, 2))
                .add(weighted(ModItems.CIRCUIT_CHIP.get(), 3, 6, 1))
                .add(weighted(ModArmorItems.GAS_MASK_M65.get(), 1, 1, 1))
                .add(weighted(ModItems.GRENADE_FRAG.get(), 1, 1, 1))
                .add(weighted(ModItems.GRENADE_IF_INCENDIARY.get(), 1, 1, 1))
                .add(weighted(Items.DIAMOND, 1, 2, 1));

        // ==================== VAULT STANDARD ====================
        new ItemPool(POOL_VAULT_STANDARD)
                .add(weighted(ModItems.INGOT_DESH.get(), 2, 6, 1))
                .add(weighted(ModItems.POWDER_DESH_MIX.get(), 1, 5, 1))
                .add(weighted(Items.DIAMOND, 3, 6, 1))
                .add(weighted(ModAmmoItems.AMMO_NUKE_STANDARD.get(), 1, 1, 1))
                .add(weighted(ModItems.AMMO_CONTAINER.get(), 1, 1, 1))
                .add(weighted(ModItems.GRENADE_NUKE.get(), 1, 1, 1))
                .add(weighted(ModItems.GRENADE_ELECTRIC.get(), 1, 6, 1))
                .add(weighted(ModItems.POWDER_YELLOWCAKE.get(), 16, 24, 1))
                .add(weighted(ModGunItems.GUN_UZI.get(), 1, 1, 1))
                .add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 12, 16, 1))
                .add(weighted(ModItems.CIRCUIT_CHIP.get(), 2, 6, 1));

        // ==================== VAULT REINFORCED ====================
        new ItemPool(POOL_VAULT_REINFORCED)
                .add(weighted(ModItems.INGOT_DESH.get(), 6, 16, 1))
                .add(weighted(ModItems.POWDER_POWER.get(), 1, 5, 1))
                .add(weighted(ModItems.SAT_CHIP.get(), 1, 1, 1))
                .add(weighted(Items.DIAMOND, 5, 9, 1))
                .add(weighted(ModAmmoItems.AMMO_NUKE_STANDARD.get(), 1, 3, 1))
                .add(weighted(ModItems.AMMO_CONTAINER.get(), 1, 4, 1))
                .add(weighted(ModItems.GRENADE_NUKE.get(), 1, 2, 1))
                .add(weighted(ModItems.GRENADE_IF_STICKY.get(), 1, 1, 1))
                .add(weighted(ModItems.POWDER_YELLOWCAKE.get(), 26, 42, 1))
                .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 1, 1))
                .add(weighted(ModItems.CIRCUIT_CHIP.get(), 18, 32, 1))
                .add(weighted(ModItems.CIRCUIT_ANALOG.get(), 6, 12, 1));

        // ==================== VAULT UNBREAKABLE ====================
        new ItemPool(POOL_VAULT_UNBREAKABLE)
                .add(weighted(ModItems.AMMO_CONTAINER.get(), 3, 6, 1))
                .add(weighted(ModAmmoItems.AMMO_NUKE_DEMO.get(), 2, 3, 1))
                .add(weighted(ModGunItems.GUN_CARBINE.get(), 1, 1, 1))
                .add(weighted(ModAmmoItems.AMMO_R762_DU.get(), 16, 32, 1))
                .add(weighted(ModGunItems.GUN_CONGOLAKE.get(), 1, 1, 1))
                .add(weighted(ModItems.CIRCUIT_ADVANCED.get(), 6, 12, 1));

        // ==================== METEORITE TREASURE ====================
        new ItemPool(POOL_METEORITE_TREASURE)
                .add(weighted(ModToolItems.COBALT_PICKAXE.get(), 1, 1, 10))
                .add(weighted(ModItems.INGOT_ZIRCONIUM.get(), 1, 16, 10))
                .add(weighted(ModItems.INGOT_NIOBIUM.get(), 1, 16, 10))
                .add(weighted(ModItems.INGOT_COBALT.get(), 1, 16, 10))
                .add(weighted(ModItems.INGOT_BORON.get(), 1, 16, 10))
                .add(weighted(ModItems.INGOT_STARMETAL.get(), 1, 1, 5))
                .add(weighted(ModItems.CRYSTAL_GOLD.get(), 1, 4, 10))
                .add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 4, 8, 10))
                .add(weighted(ModItems.CIRCUIT_CHIP.get(), 2, 4, 10))
                .add(weighted(ModItems.DEFINITELYFOOD.get(), 16, 32, 25))
                .add(weighted(ModBlocks.CRATE_CAN.get().asItem(), 1, 3, 10))
                .add(weighted(ModItems.PILL_HERBAL.get(), 1, 2, 10))
                .add(weighted(ModItems.SERUM.get(), 1, 1, 5))
                .add(weighted(ModItems.HEART_PIECE.get(), 1, 1, 5))
                .add(weighted(ModItems.SCRUMPY.get(), 1, 1, 5))
                .add(weighted(ModItems.LAUNCH_CODE_PIECE.get(), 1, 1, 5))
                .add(weighted(ModItems.EGG_GLYPHID.get(), 1, 1, 5))
                .add(weighted(ModItems.GEM_ALEXANDRITE.get(), 1, 1, 1))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 1));

        // ==================== BLUEPRINTS ====================
        new ItemPool(POOL_BLUEPRINTS)
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 10))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 5))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 1));
    }
}