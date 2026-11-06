package com.hbm_addons.util;

import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolHelper;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public final class EncounterPools {

    public static final Map<Integer, ItemPool> ENCOUNTER_FRIENDLY_ARMOR_POOLS = new HashMap<>();
    public static final Map<Integer, ItemPool> ENCOUNTER_HOSTILE_ARMOR_POOLS = new HashMap<>();
    public static ItemPool ENCOUNTER_FRIENDLY_GUN_POOLS;
    public static ItemPool ENCOUNTER_HOSTILE_GUN_POOLS;
    public static ItemPool ENCOUNTER_LOOT_POOLS;

    private EncounterPools() {}

    public static void init() {
        // Friendly armor
        ENCOUNTER_FRIENDLY_ARMOR_POOLS.put(4, makeHelmet(200));
        ENCOUNTER_FRIENDLY_ARMOR_POOLS.put(3, makeChestplate(200));
        ENCOUNTER_FRIENDLY_ARMOR_POOLS.put(2, makeLeggings(150));
        ENCOUNTER_FRIENDLY_ARMOR_POOLS.put(1, makeBoots(150));
        ENCOUNTER_FRIENDLY_ARMOR_POOLS.put(0, makeWeapon(400));

        // Hostile armor
        ENCOUNTER_HOSTILE_ARMOR_POOLS.put(4, makeAdvancedHelmet(50));
        ENCOUNTER_HOSTILE_ARMOR_POOLS.put(3, makeAdvancedChestplate(50));
        ENCOUNTER_HOSTILE_ARMOR_POOLS.put(2, makeAdvancedLeggings(50));
        ENCOUNTER_HOSTILE_ARMOR_POOLS.put(1, makeAdvancedBoots(50));
        ENCOUNTER_HOSTILE_ARMOR_POOLS.put(0, makeAdvancedWeapon(100));

        // Guns
        ENCOUNTER_FRIENDLY_GUN_POOLS = new ItemPool()
                .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 6))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 4))
                .add(weighted(ModGunItems.GUN_HENRY.get(), 1, 4))
                .add(ItemPoolHelper.empty(40));

        ENCOUNTER_HOSTILE_GUN_POOLS = new ItemPool()
                .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 10))
                .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 10))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_HENRY.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_UZI.get(), 1, 6))
                .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 4))
                .add(weighted(ModGunItems.GUN_SPAS12.get(), 1, 3))
                .add(ItemPoolHelper.empty(15));

        // Loot — все имена сверены с ModItems.java
        ENCOUNTER_LOOT_POOLS = new ItemPool()
                .add(weighted(ModItems.CAP_NUKA.get(), 1, 20))
                .add(weighted(ModItems.BOTTLE_EMPTY.get(), 1, 10))
                .add(weighted(ModItems.CAN_EMPTY.get(), 1, 10))
                .add(weighted(ModItems.SCRAP.get(), 1, 15))
                .add(weighted(ModItems.INGOT_STEEL.get(), 1, 8))
                .add(weighted(ModItems.BANDAID.get(), 1, 6))
                .add(weighted(ModItems.RADAWAY.get(), 1, 4))
                .add(weighted(ModItems.GEIGER_COUNTER.get(), 1, 2))
                .add(ItemPoolHelper.empty(50));
    }

    public static void assignItemsToEntity(LivingEntity entity, Map<Integer, ItemPool> slotPools, RandomSource rand) {
        for (Map.Entry<Integer, ItemPool> entry : slotPools.entrySet()) {
            ItemStack stack = entry.getValue().generateOne(rand);
            if (stack == null || stack.isEmpty()) continue;
            EquipmentSlot slot = switch (entry.getKey()) {
                case 0 -> EquipmentSlot.MAINHAND;
                case 1 -> EquipmentSlot.FEET;
                case 2 -> EquipmentSlot.LEGS;
                case 3 -> EquipmentSlot.CHEST;
                case 4 -> EquipmentSlot.HEAD;
                default -> null;
            };
            if (slot != null) entity.setItemSlot(slot, stack);
        }
    }

    // --- Фабрики ---
    private static ItemPool makeHelmet(int empty) {
        return new ItemPool()
                .add(weighted(ModArmorItems.GAS_MASK_M65.get(), 1, 16))
                .add(weighted(ModArmorItems.GAS_MASK_OLDE.get(), 1, 12))
                .add(weighted(ModArmorItems.NO9.get(), 1, 16))
                .add(weighted(ModArmorItems.STEEL_HELMET.get(), 1, 8))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeChestplate(int empty) {
        return new ItemPool()
                .add(weighted(ModArmorItems.JACKET.get(), 1, 32))
                .add(weighted(ModArmorItems.STEEL_CHESTPLATE.get(), 1, 2))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeLeggings(int empty) {
        return new ItemPool()
                .add(weighted(ModArmorItems.STEEL_LEGGINGS.get(), 1, 16))
                .add(weighted(ModArmorItems.ROBES_LEGGINGS.get(), 1, 32))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeBoots(int empty) {
        return new ItemPool()
                .add(weighted(ModArmorItems.ROBES_BOOTS.get(), 1, 32))
                .add(weighted(ModArmorItems.STEEL_BOOTS.get(), 1, 16))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeWeapon(int empty) {
        return new ItemPool()
                .add(weighted(ModToolItems.PIPE_LEAD.get(), 1, 30))
                .add(weighted(ModToolItems.CROWBAR.get(), 1, 25))
                .add(weighted(ModToolItems.STEEL_SWORD.get(), 1, 15))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeAdvancedHelmet(int empty) {
        return new ItemPool()
                .add(weighted(ModArmorItems.SECURITY_HELMET.get(), 1, 10))
                .add(weighted(ModArmorItems.HAZMAT_HELMET.get(), 1, 6))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeAdvancedChestplate(int empty) {
        return new ItemPool()
                .add(weighted(ModArmorItems.SECURITY_CHESTPLATE.get(), 1, 8))
                .add(weighted(ModArmorItems.HAZMAT_CHESTPLATE.get(), 1, 6))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeAdvancedLeggings(int empty) {
        return new ItemPool()
                .add(weighted(ModArmorItems.SECURITY_LEGGINGS.get(), 1, 8))
                .add(weighted(ModArmorItems.HAZMAT_LEGGINGS.get(), 1, 6))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeAdvancedBoots(int empty) {
        return new ItemPool()
                .add(weighted(ModArmorItems.SECURITY_BOOTS.get(), 1, 8))
                .add(weighted(ModArmorItems.HAZMAT_BOOTS.get(), 1, 6))
                .add(ItemPoolHelper.empty(empty));
    }

    private static ItemPool makeAdvancedWeapon(int empty) {
        return new ItemPool()
                .add(weighted(ModToolItems.REER_GRAAR.get(), 1, 20))
                .add(weighted(ModToolItems.TITANIUM_SWORD.get(), 1, 18))
                .add(weighted(ModToolItems.LEAD_GAVEL.get(), 1, 8))
                .add(ItemPoolHelper.empty(empty));
    }
}
