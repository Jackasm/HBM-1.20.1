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

    // Пулы для обычных зомби (slotPoolCommon) — ключ = номер слота (4,3,2,1,0)
    // В 1.20.1 слоты: 4 = голова, 3 = грудь, 2 = ноги, 1 = ботинки, 0 = основная рука
    public static final Map<Integer, ItemPool> COMMON_ARMOR_POOLS = new HashMap<>();
    public static final Map<Integer, ItemPool> RANGED_ARMOR_POOLS = new HashMap<>();
    public static final Map<Integer, ItemPool> ADVANCED_ARMOR_POOLS = new HashMap<>();

    // Пулы для оружия скелетов (по уровням сажи)
    public static final Map<Double, ItemPool> GUN_POOLS = new HashMap<>();

    // Инициализация пулов
    static {
        // ========== ОБЫЧНЫЕ ЗОМБИ ==========
        // Шлем (слот 4)
        COMMON_ARMOR_POOLS.put(4, new ItemPool()
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
                .add(ItemPoolHelper.empty(800)) // пустой вариант (ничего)
        );

        // Нагрудник (слот 3)
        COMMON_ARMOR_POOLS.put(3, new ItemPool()
                .add(weighted(ModArmorItems.STARMETAL_CHESTPLATE.get(), 1, 1))
                .add(weighted(ModArmorItems.COBALT_CHESTPLATE.get(), 1, 2))
                .add(weighted(ModArmorItems.ROBES_CHESTPLATE.get(), 1, 32))
                .add(weighted(ModArmorItems.JACKET.get(), 1, 32))
                .add(weighted(ModArmorItems.JACKET_2.get(), 1, 32))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_CHESTPLATE.get(), 1, 2))
                .add(weighted(ModArmorItems.STEEL_CHESTPLATE.get(), 1, 2))
                .add(ItemPoolHelper.empty(800))
        );

        // Поножи (слот 2)
        COMMON_ARMOR_POOLS.put(2, new ItemPool()
                .add(weighted(ModArmorItems.ZIRCONIUM_LEGGINGS.get(), 1, 1))
                .add(weighted(ModArmorItems.COBALT_LEGGINGS.get(), 1, 2))
                .add(weighted(ModArmorItems.STEEL_LEGGINGS.get(), 1, 16))
                .add(weighted(ModArmorItems.TITANIUM_LEGGINGS.get(), 1, 8))
                .add(weighted(ModArmorItems.ROBES_LEGGINGS.get(), 1, 32))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_LEGGINGS.get(), 1, 2))
                .add(ItemPoolHelper.empty(700))
        );

        // Ботинки (слот 1)
        COMMON_ARMOR_POOLS.put(1, new ItemPool()
                .add(weighted(ModArmorItems.ROBES_BOOTS.get(), 1, 32))
                .add(weighted(ModArmorItems.STEEL_BOOTS.get(), 1, 16))
                .add(weighted(ModArmorItems.COBALT_BOOTS.get(), 1, 2))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_BOOTS.get(), 1, 2))
                .add(ItemPoolHelper.empty(700))
        );

        // Оружие и инструменты (слот 0)
        COMMON_ARMOR_POOLS.put(0, new ItemPool()
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
                .add(ItemPoolHelper.empty(1000))
        );

        // ========== СКЕЛЕТЫ (RANGED) ==========
        // Шлем
        RANGED_ARMOR_POOLS.put(4, new ItemPool()
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
                .add(ItemPoolHelper.empty(1200))
        );

        // Нагрудник
        RANGED_ARMOR_POOLS.put(3, new ItemPool()
                .add(weighted(ModArmorItems.STARMETAL_CHESTPLATE.get(), 1, 1))
                .add(weighted(ModArmorItems.COBALT_CHESTPLATE.get(), 1, 2))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_CHESTPLATE.get(), 1, 2))
                .add(weighted(ModArmorItems.STEEL_CHESTPLATE.get(), 1, 8))
                .add(weighted(ModArmorItems.TITANIUM_CHESTPLATE.get(), 1, 4))
                .add(ItemPoolHelper.empty(1000))
        );

        // Поножи
        RANGED_ARMOR_POOLS.put(2, new ItemPool()
                .add(weighted(ModArmorItems.ZIRCONIUM_LEGGINGS.get(), 1, 1))
                .add(weighted(ModArmorItems.COBALT_LEGGINGS.get(), 1, 2))
                .add(weighted(ModArmorItems.STEEL_LEGGINGS.get(), 1, 16))
                .add(weighted(ModArmorItems.TITANIUM_LEGGINGS.get(), 1, 8))
                .add(weighted(ModArmorItems.ROBES_LEGGINGS.get(), 1, 32))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_LEGGINGS.get(), 1, 2))
                .add(ItemPoolHelper.empty(1000))
        );

        // Ботинки
        RANGED_ARMOR_POOLS.put(1, new ItemPool()
                .add(weighted(ModArmorItems.ROBES_BOOTS.get(), 1, 32))
                .add(weighted(ModArmorItems.STEEL_BOOTS.get(), 1, 16))
                .add(weighted(ModArmorItems.COBALT_BOOTS.get(), 1, 2))
                .add(weighted(ModArmorItems.ADVANCED_ALLOY_BOOTS.get(), 1, 2))
                .add(weighted(ModArmorItems.TITANIUM_BOOTS.get(), 1, 6))
                .add(ItemPoolHelper.empty(1000))
        );

        // ========== ОРУЖИЕ СКЕЛЕТОВ ПО УРОВНЯМ САЖИ ==========
        GUN_POOLS.put(0.3, new ItemPool()
                .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 16))
                .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 2))
                .add(ItemPoolHelper.empty(100))
        );

        GUN_POOLS.put(1.0, new ItemPool()
                .add(weighted(ModGunItems.GUN_LIGHT_REVOLVER.get(), 1, 6))
                .add(weighted(ModGunItems.GUN_GREASEGUN.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 4))
                .add(weighted(ModGunItems.GUN_HENRY.get(), 1, 6))
                .add(ItemPoolHelper.empty(100))
        );

        GUN_POOLS.put(3.0, new ItemPool()
                .add(weighted(ModGunItems.GUN_UZI.get(), 1, 10))
                .add(weighted(ModGunItems.GUN_MARESLEG.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_HENRY.get(), 1, 12))
                .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 4))
                .add(weighted(ModGunItems.GUN_FLAREGUN.get(), 1, 2))
                .add(ItemPoolHelper.empty(100))
        );

        GUN_POOLS.put(5.0, new ItemPool()
                .add(weighted(ModGunItems.GUN_AM180.get(), 1, 6))
                .add(weighted(ModGunItems.GUN_UZI.get(), 1, 10))
                .add(weighted(ModGunItems.GUN_SPAS12.get(), 1, 8))
                .add(weighted(ModGunItems.GUN_HENRY_LINCOLN.get(), 1, 2))
                .add(weighted(ModGunItems.GUN_HEAVY_REVOLVER.get(), 1, 12))
                .add(weighted(ModGunItems.GUN_FLAREGUN.get(), 1, 4))
                .add(weighted(ModGunItems.GUN_FLAMER.get(), 1, 2))
                .add(ItemPoolHelper.empty(100))
        );
    }

    // Метод для назначения предметов сущности
    public static void assignItemsToEntity(LivingEntity entity, Map<Integer, ItemPool> slotPools, RandomSource rand) {
        for (Map.Entry<Integer, ItemPool> entry : slotPools.entrySet()) {
            int slot = entry.getKey();
            ItemPool pool = entry.getValue();
            ItemStack stack = pool.generateOne(rand);
            if (stack != null && !stack.isEmpty()) {
                // Специальная обработка для противогазов (добавляем фильтр)
                if (stack.getItem() == ModArmorItems.GAS_MASK_M65.get() ||
                        stack.getItem() == ModArmorItems.GAS_MASK_OLDE.get() ||
                        stack.getItem() == ModArmorItems.GAS_MASK_MONO.get()) {
                        ArmorUtil.installGasMaskFilter(stack, new ItemStack(ModItems.GAS_MASK_FILTER.get()));                    
                }
                // Преобразуем номер слота в EquipmentSlot
                EquipmentSlot equipmentSlot;
                if (slot == 0) equipmentSlot = EquipmentSlot.MAINHAND;
                else if (slot == 1) equipmentSlot = EquipmentSlot.FEET;
                else if (slot == 2) equipmentSlot = EquipmentSlot.LEGS;
                else if (slot == 3) equipmentSlot = EquipmentSlot.CHEST;
                else if (slot == 4) equipmentSlot = EquipmentSlot.HEAD;
                else continue;
                entity.setItemSlot(equipmentSlot, stack);
            }
        }
    }

    // Установка полного комплекта брони
    public static void equipFullSet(LivingEntity entity, Item helmet, Item chest, Item legs, Item boots) {
        entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(helmet));
        entity.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        entity.setItemSlot(EquipmentSlot.LEGS, new ItemStack(legs));
        entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(boots));
    }

}