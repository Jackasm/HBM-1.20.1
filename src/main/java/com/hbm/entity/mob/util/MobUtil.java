package com.hbm.entity.mob.util;

import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolHelper;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import com.hbm.util.ArmorUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class MobUtil {

    /* ============================================================
     *  ПУЛЫ ДЛЯ ЛОГИЧЕСКИХ БЛОКОВ (структуры)
     *  Мобы гарантированно экипированы — empty-веса минимальны.
     * ============================================================ */
    public static final Map<Integer, ItemPool> COMMON_ARMOR_POOLS = new HashMap<>();
    public static final Map<Integer, ItemPool> RANGED_ARMOR_POOLS = new HashMap<>();
    public static final Map<Integer, ItemPool> ADVANCED_ARMOR_POOLS = new HashMap<>();
    public static Map<Integer, ItemPool> ADVANCED_RANGED_ARMOR_POOLS;

    /* ============================================================
     *  ПУЛЫ ДЛЯ SOOT-МОБОВ (натуральный спавн + сажа)
     *  Мобы часто без экипировки — empty-веса большие.
     * ============================================================ */
    public static final Map<Integer, ItemPool> COMMON_ARMOR_POOLS_SOOT = new HashMap<>();
    public static final Map<Integer, ItemPool> RANGED_ARMOR_POOLS_SOOT = new HashMap<>();
    public static final Map<Integer, ItemPool> ADVANCED_ARMOR_POOLS_SOOT = new HashMap<>();
    public static Map<Integer, ItemPool> ADVANCED_RANGED_ARMOR_POOLS_SOOT;

    /* ============================================================
     *  ОРУЖИЕ
     * ============================================================ */
    /** Оружие по уровням сажи (для soot-мобов). */
    public static final Map<Double, ItemPool> GUN_POOLS = new HashMap<>();

    /** Оружие для скелетов логических блоков. */
    public static ItemPool SKELETON_GUN_POOLS_T1;
    public static ItemPool SKELETON_GUN_POOLS_T2;
    public static ItemPool SKELETON_GUN_POOLS_T3;

    /* ============================================================
     *  ИНИЦИАЛИЗАЦИЯ
     * ============================================================ */
    static {
        // ---------- ЛОГИЧЕСКИЕ БЛОКИ ----------
        COMMON_ARMOR_POOLS.put(4, makeCommonHelmet(0));
        COMMON_ARMOR_POOLS.put(3, makeCommonChestplate(10));
        COMMON_ARMOR_POOLS.put(2, makeCommonLeggings(20));
        COMMON_ARMOR_POOLS.put(1, makeCommonBoots(10));
        COMMON_ARMOR_POOLS.put(0, makeCommonWeapon(1000));

        ADVANCED_ARMOR_POOLS.put(4, makeAdvancedHelmet(0));
        ADVANCED_ARMOR_POOLS.put(3, makeAdvancedChestplate(10));
        ADVANCED_ARMOR_POOLS.put(2, makeAdvancedLeggings(20));
        ADVANCED_ARMOR_POOLS.put(1, makeAdvancedBoots(10));
        ADVANCED_ARMOR_POOLS.put(0, makeAdvancedWeapon(500));

        RANGED_ARMOR_POOLS.put(4, makeRangedHelmet(0));
        RANGED_ARMOR_POOLS.put(3, makeRangedChestplate(10));
        RANGED_ARMOR_POOLS.put(2, makeRangedLeggings(10));
        RANGED_ARMOR_POOLS.put(1, makeRangedBoots(10));

        // ---------- SOOT ----------
        // Тут твои веса — мобы чаще одеты, чем в 1.7.10
        COMMON_ARMOR_POOLS_SOOT.put(4, makeCommonHelmet(800));
        COMMON_ARMOR_POOLS_SOOT.put(3, makeCommonChestplate(800));
        COMMON_ARMOR_POOLS_SOOT.put(2, makeCommonLeggings(700));
        COMMON_ARMOR_POOLS_SOOT.put(1, makeCommonBoots(700));
        COMMON_ARMOR_POOLS_SOOT.put(0, makeCommonWeapon(1000));

        ADVANCED_ARMOR_POOLS_SOOT.put(4, makeAdvancedHelmet(20));
        ADVANCED_ARMOR_POOLS_SOOT.put(3, makeAdvancedChestplate(20));
        ADVANCED_ARMOR_POOLS_SOOT.put(2, makeAdvancedLeggings(20));
        ADVANCED_ARMOR_POOLS_SOOT.put(1, makeAdvancedBoots(10));
        ADVANCED_ARMOR_POOLS_SOOT.put(0, makeAdvancedWeapon(500));

        RANGED_ARMOR_POOLS_SOOT.put(4, makeRangedHelmet(1200));
        RANGED_ARMOR_POOLS_SOOT.put(3, makeRangedChestplate(1000));
        RANGED_ARMOR_POOLS_SOOT.put(2, makeRangedLeggings(1000));
        RANGED_ARMOR_POOLS_SOOT.put(1, makeRangedBoots(1000));

        // Ranged-варианты без слота 0 (оружие выдаётся отдельно)
        ADVANCED_RANGED_ARMOR_POOLS = new HashMap<>(ADVANCED_ARMOR_POOLS);
        ADVANCED_RANGED_ARMOR_POOLS.remove(0);

        ADVANCED_RANGED_ARMOR_POOLS_SOOT = new HashMap<>(ADVANCED_ARMOR_POOLS_SOOT);
        ADVANCED_RANGED_ARMOR_POOLS_SOOT.remove(0);

        // ---------- ОРУЖИЕ ПО УРОВНЮ САЖИ ----------
        GUN_POOLS.put(0.3, new ItemPool()
                .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 16))
                .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 2)));

        GUN_POOLS.put(1.0, new ItemPool()
                .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 6))
                .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 4))
                .add(weighted(ModGunItems.GUN_HENRY.get(), 1, 6)));

        GUN_POOLS.put(3.0, new ItemPool()
                .add(weighted(ModGunItems.GUN_UZI.get(), 1, 10))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_HENRY.get(), 1, 12))
                .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 4))
                .add(weighted(ModGunItems.GUN_FLAREGUN.get(), 1, 2)));

        GUN_POOLS.put(5.0, new ItemPool()
                .add(weighted(ModGunItems.GUN_AM180.get(), 1, 6))
                .add(weighted(ModGunItems.GUN_UZI.get(), 1, 10))
                .add(weighted(ModGunItems.GUN_SPAS12.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_HENRY_LINCOLN.get(), 1, 2))
                .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 12))
                .add(weighted(ModGunItems.GUN_FLAREGUN.get(), 1, 4))
                .add(weighted(ModGunItems.GUN_FLAMER.get(), 1, 2)));

        // ---------- ОРУЖИЕ ДЛЯ ЛОГИЧЕСКИХ БЛОКОВ ----------
        SKELETON_GUN_POOLS_T1 = new ItemPool()
                .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 16))
                .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 2))
                .add(weighted(ModGunItems.GUN_FLAREGUN.get(), 1, 1));

        SKELETON_GUN_POOLS_T2 = new ItemPool()
                .add(weighted(ModGunItems.GUN_UZI.get(), 1, 12))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_HENRY.get(), 1, 12))
                .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_FLAREGUN.get(), 1, 4));
        // .add(weighted(ModGunItems.GUN_STAR_F.get(), 1, 8));  // если есть

        SKELETON_GUN_POOLS_T3 = new ItemPool()
                .add(weighted(ModGunItems.GUN_G3.get(), 1, 25))
                .add(weighted(ModGunItems.GUN_SPAS12.get(), 1, 20))
                .add(weighted(ModGunItems.GUN_CARBINE.get(), 1, 15))
                .add(weighted(ModGunItems.GUN_AM180.get(), 1, 6))
                .add(weighted(ModGunItems.GUN_AMAT.get(), 1, 5));
        // .add(weighted(ModGunItems.GUN_STAR_F.get(), 1, 20)); // если есть
    }

    /* ============================================================
     *  ФАБРИЧНЫЕ МЕТОДЫ — чтобы не дублировать списки
     *  (один набор предметов, разные empty-веса)
     * ============================================================ */

    // ----- COMMON: зомби ближнего боя -----
    private static ItemPool makeCommonHelmet(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.GAS_MASK_M65.get(), 1, 16))
                .add(weighted(ModArmorItems.GAS_MASK_OLDE.get(), 1, 12))
                .add(weighted(ModArmorItems.MASK_OF_INFAMY.get(), 1, 8))
                .add(weighted(ModArmorItems.GAS_MASK_MONO.get(), 1, 8))
                .add(weighted(ModArmorItems.ROBES_HELMET.get(), 1, 32))
                .add(weighted(ModArmorItems.NO9.get(), 1, 16))
                .add(weighted(ModArmorItems.COBALT_HELMET.get(), 1, 2))
                .add(weighted(ModArmorItems.MASK_PISS.get(), 1, 1))
                .add(weighted(ModArmorItems.HAT.get(), 1, 1))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_HELMET.get(), 1, 2))
                .add(weighted(ModArmorItems.TITANIUM_HELMET.get(), 1, 4))
                .add(weighted(ModArmorItems.STEEL_HELMET.get(), 1, 8))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeCommonChestplate(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.STARMETAL_CHESTPLATE.get(), 1, 1))
                .add(weighted(ModArmorItems.COBALT_CHESTPLATE.get(), 1, 2))
                .add(weighted(ModArmorItems.ROBES_CHESTPLATE.get(), 1, 32))
                .add(weighted(ModArmorItems.JACKET.get(), 1, 32))
                .add(weighted(ModArmorItems.JACKET_2.get(), 1, 32))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_CHESTPLATE.get(), 1, 2))
                .add(weighted(ModArmorItems.STEEL_CHESTPLATE.get(), 1, 2))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeCommonLeggings(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.ZIRCONIUM_LEGGINGS.get(), 1, 1))
                .add(weighted(ModArmorItems.COBALT_LEGGINGS.get(), 1, 2))
                .add(weighted(ModArmorItems.STEEL_LEGGINGS.get(), 1, 16))
                .add(weighted(ModArmorItems.TITANIUM_LEGGINGS.get(), 1, 8))
                .add(weighted(ModArmorItems.ROBES_LEGGINGS.get(), 1, 32))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_LEGGINGS.get(), 1, 2))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeCommonBoots(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.ROBES_BOOTS.get(), 1, 32))
                .add(weighted(ModArmorItems.STEEL_BOOTS.get(), 1, 16))
                .add(weighted(ModArmorItems.COBALT_BOOTS.get(), 1, 2))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_BOOTS.get(), 1, 2))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeCommonWeapon(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModToolItems.PIPE_LEAD.get(), 1, 30))
                .add(weighted(ModToolItems.CROWBAR.get(), 1, 25))
                .add(weighted(ModItems.GEIGER_COUNTER.get(), 1, 20))
                .add(weighted(ModToolItems.REER_GRAAR.get(), 1, 16))
                .add(weighted(ModToolItems.STEEL_PICKAXE.get(), 1, 12))
                .add(weighted(ModToolItems.STOPSIGN.get(), 1, 10))
                .add(weighted(ModToolItems.SOPSIGN.get(), 1, 8))
                .add(weighted(ModToolItems.CHERNOBYLSIGN.get(), 1, 6))
                .add(weighted(ModToolItems.STEEL_SWORD.get(), 1, 15))
                .add(weighted(ModToolItems.TITANIUM_SWORD.get(), 1, 8))
                .add(weighted(ModToolItems.LEAD_GAVEL.get(), 1, 4))
                .add(weighted(ModToolItems.WRENCH_FLIPPED.get(), 1, 2))
                .add(weighted(ModToolItems.WRENCH.get(), 1, 20))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    // ----- ADVANCED -----
    private static ItemPool makeAdvancedHelmet(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.SECURITY_HELMET.get(), 1, 10))
                .add(weighted(ModArmorItems.T51_HELMET.get(), 1, 4))
                .add(weighted(ModArmorItems.ASBESTOS_HELMET.get(), 1, 12))
                .add(weighted(ModArmorItems.LIQUIDATOR_HELMET.get(), 1, 4))
                .add(weighted(ModArmorItems.NO9.get(), 1, 12))
                .add(weighted(ModArmorItems.HAZMAT_HELMET.get(), 1, 6))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeAdvancedChestplate(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.LIQUIDATOR_CHESTPLATE.get(), 1, 4))
                .add(weighted(ModArmorItems.SECURITY_CHESTPLATE.get(), 1, 8))
                .add(weighted(ModArmorItems.ASBESTOS_CHESTPLATE.get(), 1, 12))
                .add(weighted(ModArmorItems.T51_CHESTPLATE.get(), 1, 4))
                .add(weighted(ModArmorItems.HAZMAT_CHESTPLATE.get(), 1, 6))
                .add(weighted(ModArmorItems.STEEL_CHESTPLATE.get(), 1, 8))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeAdvancedLeggings(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.LIQUIDATOR_LEGGINGS.get(), 1, 4))
                .add(weighted(ModArmorItems.SECURITY_LEGGINGS.get(), 1, 8))
                .add(weighted(ModArmorItems.ASBESTOS_LEGGINGS.get(), 1, 12))
                .add(weighted(ModArmorItems.T51_LEGGINGS.get(), 1, 4))
                .add(weighted(ModArmorItems.HAZMAT_LEGGINGS.get(), 1, 6))
                .add(weighted(ModArmorItems.STEEL_LEGGINGS.get(), 1, 8))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeAdvancedBoots(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.LIQUIDATOR_BOOTS.get(), 1, 4))
                .add(weighted(ModArmorItems.SECURITY_BOOTS.get(), 1, 8))
                .add(weighted(ModArmorItems.ASBESTOS_BOOTS.get(), 1, 12))
                .add(weighted(ModArmorItems.T51_BOOTS.get(), 1, 4))
                .add(weighted(ModArmorItems.HAZMAT_BOOTS.get(), 1, 6))
                .add(weighted(ModArmorItems.ROBES_BOOTS.get(), 1, 8))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeAdvancedWeapon(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModToolItems.PIPE_LEAD.get(), 1, 20))
                .add(weighted(ModToolItems.CROWBAR.get(), 1, 10))
                .add(weighted(ModItems.GEIGER_COUNTER.get(), 1, 10))
                .add(weighted(ModToolItems.REER_GRAAR.get(), 1, 20))
                .add(weighted(ModToolItems.WRENCH_FLIPPED.get(), 1, 20))
                .add(weighted(ModToolItems.STOPSIGN.get(), 1, 16))
                .add(weighted(ModToolItems.SOPSIGN.get(), 1, 4))
                .add(weighted(ModToolItems.CHERNOBYLSIGN.get(), 1, 16))
                .add(weighted(ModToolItems.TITANIUM_SWORD.get(), 1, 18))
                .add(weighted(ModToolItems.LEAD_GAVEL.get(), 1, 8))
                .add(weighted(ModToolItems.WRENCH.get(), 1, 20))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    // ----- RANGED -----
    private static ItemPool makeRangedHelmet(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.GAS_MASK_M65.get(), 1, 16))
                .add(weighted(ModArmorItems.GAS_MASK_OLDE.get(), 1, 12))
                .add(weighted(ModArmorItems.MASK_OF_INFAMY.get(), 1, 8))
                .add(weighted(ModArmorItems.GAS_MASK_MONO.get(), 1, 8))
                .add(weighted(ModArmorItems.ROBES_HELMET.get(), 1, 32))
                .add(weighted(ModArmorItems.NO9.get(), 1, 16))
                .add(weighted(ModArmorItems.MASK_PISS.get(), 1, 1))
                .add(weighted(ModArmorItems.GOGGLES.get(), 1, 1))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_HELMET.get(), 1, 2))
                .add(weighted(ModArmorItems.TITANIUM_HELMET.get(), 1, 4))
                .add(weighted(ModArmorItems.STEEL_HELMET.get(), 1, 8))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeRangedChestplate(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.STARMETAL_CHESTPLATE.get(), 1, 1))
                .add(weighted(ModArmorItems.COBALT_CHESTPLATE.get(), 1, 2))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_CHESTPLATE.get(), 1, 2))
                .add(weighted(ModArmorItems.STEEL_CHESTPLATE.get(), 1, 8))
                .add(weighted(ModArmorItems.TITANIUM_CHESTPLATE.get(), 1, 4))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeRangedLeggings(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.ZIRCONIUM_LEGGINGS.get(), 1, 1))
                .add(weighted(ModArmorItems.COBALT_LEGGINGS.get(), 1, 2))
                .add(weighted(ModArmorItems.STEEL_LEGGINGS.get(), 1, 16))
                .add(weighted(ModArmorItems.TITANIUM_LEGGINGS.get(), 1, 8))
                .add(weighted(ModArmorItems.ROBES_LEGGINGS.get(), 1, 32))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_LEGGINGS.get(), 1, 2))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    private static ItemPool makeRangedBoots(int emptyWeight) {
        return new ItemPool()
                .add(weighted(ModArmorItems.ROBES_BOOTS.get(), 1, 32))
                .add(weighted(ModArmorItems.STEEL_BOOTS.get(), 1, 16))
                .add(weighted(ModArmorItems.COBALT_BOOTS.get(), 1, 2))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_BOOTS.get(), 1, 2))
                .add(weighted(ModArmorItems.TITANIUM_BOOTS.get(), 1, 6))
                .add(ItemPoolHelper.empty(emptyWeight));
    }

    /* ============================================================
     *  ВЫДАЧА ЭКИПИРОВКИ
     * ============================================================ */
    public static void assignItemsToEntity(LivingEntity entity, Map<Integer, ItemPool> slotPools, RandomSource rand) {
        for (Map.Entry<Integer, ItemPool> entry : slotPools.entrySet()) {
            int slot = entry.getKey();
            ItemPool pool = entry.getValue();
            ItemStack stack = pool.generateOne(rand);
            if (stack == null || stack.isEmpty()) continue;

            // Противогазы — вставляем фильтр
            if (stack.getItem() == ModArmorItems.GAS_MASK_M65.get() ||
                    stack.getItem() == ModArmorItems.GAS_MASK_OLDE.get() ||
                    stack.getItem() == ModArmorItems.GAS_MASK_MONO.get()) {
                ArmorUtil.installGasMaskFilter(stack, new ItemStack(ModItems.GAS_MASK_FILTER.get()));
            }

            EquipmentSlot equipmentSlot = switch (slot) {
                case 0 -> EquipmentSlot.MAINHAND;
                case 1 -> EquipmentSlot.FEET;
                case 2 -> EquipmentSlot.LEGS;
                case 3 -> EquipmentSlot.CHEST;
                case 4 -> EquipmentSlot.HEAD;
                default -> null;
            };
            if (equipmentSlot == null) continue;

            entity.setItemSlot(equipmentSlot, stack);
        }
    }

    public static void equipFullSet(LivingEntity entity, Item helmet, Item chest, Item legs, Item boots) {
        entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(helmet));
        entity.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        entity.setItemSlot(EquipmentSlot.LEGS, new ItemStack(legs));
        entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(boots));
    }
}