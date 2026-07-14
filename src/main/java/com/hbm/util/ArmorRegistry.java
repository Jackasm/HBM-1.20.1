package com.hbm.util;

import com.hbm.api.item.IGasMask;
import com.hbm.handler.ArmorModHandler;

import com.hbm.items.ModArmorItems;
import com.hbm.items.armor.MaskItem;
import com.hbm.items.armor.RespiratorItem;
import com.hbm.items.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ArmorRegistry {

    public enum HazardClass {
        PARTICLE_COARSE,    // Крупная пыль (уголь)
        PARTICLE_FINE,      // Мелкая пыль
        GAS_LUNG,           // Газы, поражающие легкие
        GAS_BLISTERING,     // Кожно-нарывные газы
        GAS_MONOXIDE,       // Угарный газ
        GAS,                // Общий класс газов (устаревший, но для совместимости)
        BACTERIA,           // Бактериологические угрозы
        SAND,               // Песок, пыльные бури
        LIGHT,              // Яркий свет, вспышки
        RADIATION,          // Радиация
        HEAT,               // Высокая температура
        COLD,                // Низкая температура
        CHEMICAL
    }

    // Регистр защиты предметов
    public static final Map<Item, Set<HazardClass>> PROTECTION_REGISTRY = new HashMap<>();

    // Регистр защиты по модификаторам
    private static final Map<Item, Map<HazardClass, Float>> MODIFIER_REGISTRY = new HashMap<>();

    // Инициализация регистра
    public static void registerHazard(Item item, HazardClass... classes) {
        Set<HazardClass> set = PROTECTION_REGISTRY.getOrDefault(item, new HashSet<>());
        Collections.addAll(set, classes);
        PROTECTION_REGISTRY.put(item, set);
    }

    public static void registerHazardWithModifier(Item item, HazardClass hazard, float modifier) {
        Map<HazardClass, Float> modifiers = MODIFIER_REGISTRY.getOrDefault(item, new HashMap<>());
        modifiers.put(hazard, modifier);
        MODIFIER_REGISTRY.put(item, modifiers);

        // Также добавляем в основной регистр
        registerHazard(item, hazard);
    }

    // Проверка защиты
    public static boolean hasProtection(LivingEntity entity, int slot, HazardClass hazard) {
        if (slot == 3) { // Слот головы
            ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
            return hasProtection(helmet, hazard);
        } else if (slot == 2) { // Слот груди
            ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
            return hasProtection(chestplate, hazard);
        } else if (slot == 1) { // Слот ног
            ItemStack leggings = entity.getItemBySlot(EquipmentSlot.LEGS);
            return hasProtection(leggings, hazard);
        } else if (slot == 0) { // Слот ног
            ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
            return hasProtection(boots, hazard);
        }
        return false;
    }
    public static boolean hasAnyProtection(LivingEntity entity, int slot, HazardClass... clazz) {
        // Получаем предмет в указанном слоте брони
        ItemStack stack = getArmorStack(entity, slot);

        if (stack.isEmpty()) return false;

        List<HazardClass> list = getProtectionFromItem(stack, entity);

        if (list == null) return false;

        for (HazardClass haz : clazz) {
            if (list.contains(haz)) return true;
        }

        return false;
    }

    private static ItemStack getArmorStack(LivingEntity entity, int slot) {
        return switch (slot) {
            case 0 -> entity.getItemBySlot(EquipmentSlot.FEET);
            case 1 -> entity.getItemBySlot(EquipmentSlot.LEGS);
            case 2 -> entity.getItemBySlot(EquipmentSlot.CHEST);
            case 3 -> entity.getItemBySlot(EquipmentSlot.HEAD);
            default -> ItemStack.EMPTY;
        };
    }

    public static List<HazardClass> getProtectionFromItem(ItemStack stack, LivingEntity entity) {
        List<HazardClass> prot = new ArrayList<>();

        Item item = stack.getItem();

        // if the item has HazardClasses assigned to it, add those
        if (PROTECTION_REGISTRY.containsKey(item)) {
            prot.addAll(PROTECTION_REGISTRY.get(item));
        }

        if (item instanceof IGasMask mask) {
            ItemStack filter = mask.getFilter(stack, entity);

            if (!filter.isEmpty()) {
                // add the HazardClasses from the filter, then remove the ones blacklisted by the mask
                List<HazardClass> filProt = new ArrayList<>(PROTECTION_REGISTRY.get(filter.getItem()));

                for (HazardClass c : mask.getBlacklist(stack, entity)) {
                    filProt.remove(c);
                }

                prot.addAll(filProt);
            }
        }

        if (ArmorModHandler.hasMods(stack)) {
            ItemStack[] mods = ArmorModHandler.pryMods(stack);

            for (ItemStack mod : mods) {
                // recursion! run the exact same procedure on every mod, in case future mods will have filter support
                if (!mod.isEmpty()) {
                    prot.addAll(getProtectionFromItem(mod, entity));
                }
            }
        }

        return prot;
    }

    public static boolean hasProtection(ItemStack stack, HazardClass hazard) {
        if (stack.isEmpty()) return false;

        Item item = stack.getItem();

        // Проверка в регистре
        if (PROTECTION_REGISTRY.containsKey(item)) {
            return PROTECTION_REGISTRY.get(item).contains(hazard);
        }

        // Проверка специальных предметов
        if (item instanceof MaskItem mask) {
            return mask.protectsFrom(hazard);
        }

        if (item instanceof RespiratorItem respirator) {
            return respirator.protectsFrom(hazard);
        }

        return false;
    }

    // Получение модификатора защиты
    public static float getProtectionModifier(ItemStack stack, HazardClass hazard) {
        if (stack.isEmpty()) return 0.0f;

        Item item = stack.getItem();

        if (MODIFIER_REGISTRY.containsKey(item)) {
            Map<HazardClass, Float> modifiers = MODIFIER_REGISTRY.get(item);
            return modifiers.getOrDefault(hazard, 1.0f);
        }

        // Значения по умолчанию
        return hasProtection(stack, hazard) ? 1.0f : 0.0f;
    }

    // Полная защита от всех угроз в слоте
    public static boolean hasFullBodyProtection(LivingEntity entity, HazardClass hazard) {
        for (int i = 0; i < 4; i++) {
            if (!hasProtection(entity, i, hazard)) {
                return false;
            }
        }
        return true;
    }

    // Получение эффективности защиты (сумма модификаторов)
    public static float getTotalProtection(LivingEntity entity, HazardClass hazard) {
        float total = 0.0f;
        for (int i = 0; i < 4; i++) {
            ItemStack stack = getArmorInSlot(entity, i);
            total += getProtectionModifier(stack, hazard);
        }
        return Math.min(total, 1.0f); // Максимум 100% защиты
    }

    private static ItemStack getArmorInSlot(LivingEntity entity, int slot) {
        return switch (slot) {
            case 0 -> entity.getItemBySlot(EquipmentSlot.FEET);
            case 1 -> entity.getItemBySlot(EquipmentSlot.LEGS);
            case 2 -> entity.getItemBySlot(EquipmentSlot.CHEST);
            case 3 -> entity.getItemBySlot(EquipmentSlot.HEAD);
            default -> ItemStack.EMPTY;
        };
    }

    // Получение всех классов опасности, от которых защищает предмет
    public static Set<HazardClass> getProtectedHazards(ItemStack stack) {
        if (stack.isEmpty()) return Collections.emptySet();

        Item item = stack.getItem();
        if (PROTECTION_REGISTRY.containsKey(item)) {
            return new HashSet<>(PROTECTION_REGISTRY.get(item));
        }

        // Для специальных предметов
        Set<HazardClass> hazards = new HashSet<>();
        if (item instanceof MaskItem mask) {
            for (HazardClass hazard : HazardClass.values()) {
                if (mask.protectsFrom(hazard)) {
                    hazards.add(hazard);
                }
            }
        }

        if (item instanceof RespiratorItem respirator) {
            for (HazardClass hazard : HazardClass.values()) {
                if (respirator.protectsFrom(hazard)) {
                    hazards.add(hazard);
                }
            }
        }

        return hazards;
    }

    public static void registerAll() {
        
        registerHazard(ModItems.GAS_MASK_FILTER.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.GAS_BLISTERING, HazardClass.BACTERIA);

        registerHazard(ModItems.GAS_MASK_FILTER_MONO.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.GAS_MONOXIDE);

        registerHazard(ModItems.GAS_MASK_FILTER_COMBO.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.GAS_BLISTERING,
                HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE);

        registerHazard(ModItems.GAS_MASK_FILTER_RAG.get(),
                HazardClass.PARTICLE_COARSE);

        registerHazard(ModItems.GAS_MASK_FILTER_PISS.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.GAS_LUNG);

        // Маски и респираторы
        registerHazard(ModArmorItems.GAS_MASK.get(),
                HazardClass.SAND, HazardClass.LIGHT);

        registerHazard(ModArmorItems.GAS_MASK_M65.get(),
                HazardClass.SAND);
        
        registerHazard(ModArmorItems.MASK_WET.get(),
                HazardClass.PARTICLE_COARSE);

        registerHazard(ModArmorItems.MASK_PISS.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.GAS_LUNG);
        
        registerHazard(ModArmorItems.GAS_MASK.get(),
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.ASHGLASSES.get(),
                HazardClass.LIGHT, HazardClass.SAND);

        // Аттачменты
        registerHazard(ModArmorItems.ATTACHMENT_MASK.get(),
                HazardClass.SAND);

        // Шлемы и броня
        registerHazard(ModArmorItems.ASBESTOS_HELMET.get(),
                HazardClass.SAND, HazardClass.LIGHT);

        registerHazard(ModArmorItems.HAZMAT_HELMET.get(),
                HazardClass.SAND);

        registerHazard(ModArmorItems.HAZMAT_HELMET_RED.get(),
                HazardClass.SAND);

        registerHazard(ModArmorItems.HAZMAT_HELMET_GREY.get(),
                HazardClass.SAND);

        registerHazard(ModArmorItems.HAZMAT_PAA_HELMET.get(),
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.LIQUIDATOR_HELMET.get(),
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.T45_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE, HazardClass.SAND);

        registerHazard(ModArmorItems.T51_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE, HazardClass.SAND);

        registerHazard(ModArmorItems.AJR_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.AJRO_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.STEAMSUIT_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.HEV_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.FAU_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.DNT_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.SCHRABIDIUM_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.EUPHEMIUM_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.RPA_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.ENVSUIT_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.TRENCHMASTER_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);


    }

    // Регистрация предметов из других модов
    public static void registerIfExists(String domain, String name, HazardClass... classes) {
        ResourceLocation key = ResLocation.ResLocation(domain, name);
        Item item = ForgeRegistries.ITEMS.getValue(key);
        if (item != null) {
            registerHazard(item, classes);
        }
    }

    // Утилиты для совместимости
    public static boolean checkForFullHazmat(LivingEntity entity) {
        return hasFullBodyProtection(entity, HazardClass.RADIATION) &&
                hasFullBodyProtection(entity, HazardClass.GAS) &&
                hasFullBodyProtection(entity, HazardClass.PARTICLE_FINE);
    }

    public static boolean checkForFullAsbestos(LivingEntity entity) {
        return hasFullBodyProtection(entity, HazardClass.HEAT) &&
                hasFullBodyProtection(entity, HazardClass.PARTICLE_COARSE);
    }

    public static boolean hasAllProtection(LivingEntity entity, int slotMask, HazardClass hazard) {
        // Проверяем каждый слот в зависимости от маски
        if ((slotMask & 1) != 0 && !hasProtection(entity, 3, hazard)) { // Голова (3)
            return false;
        }
        if ((slotMask & 2) != 0 && !hasProtection(entity, 2, hazard)) { // Грудь (2)
            return false;
        }
        if ((slotMask & 4) != 0 && !hasProtection(entity, 1, hazard)) { // Ноги (1)
            return false;
        }
        if ((slotMask & 8) != 0 && !hasProtection(entity, 0, hazard)) { // Ботинки (0)
            return false;
        }
        return true;
    }
}