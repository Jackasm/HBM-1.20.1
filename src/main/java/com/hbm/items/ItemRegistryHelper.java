package com.hbm.items;

import com.hbm.util.HBMEnums;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemRegistryHelper {
    private static final Map<HBMEnums.CreativeTabRegistry, Map<String, RegistryObject<Item>>> ITEMS_BY_TAB = new LinkedHashMap<>();
    private static final Map<String, RegistryObject<Item>> WEAPON_ITEMS = new LinkedHashMap<>();
    static {
        // Инициализируем карты для каждой вкладки
        for (HBMEnums.CreativeTabRegistry tab : HBMEnums.CreativeTabRegistry.values()) {
            ITEMS_BY_TAB.put(tab, new LinkedHashMap<>());
        }
    }

    public static RegistryObject<Item> registerTools(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier) {
        RegistryObject<Item> item = ModToolItems.TOOLS.register(name, supplier);
        ITEMS_BY_TAB.get(tab).put(name, item);
        return item;
    }

    public static RegistryObject<Item> registerArmor(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier) {
        RegistryObject<Item> item = ModArmorItems.ARMOR.register(name, supplier);
        ITEMS_BY_TAB.get(tab).put(name, item);
        return item;
    }

    public static RegistryObject<Item> registerAmmo(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier) {
        RegistryObject<Item> item = ModAmmoItems.AMMO.register(name, supplier);
        ITEMS_BY_TAB.get(tab).put(name, item);
        return item;
    }

    public static RegistryObject<Item> register(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier) {
        RegistryObject<Item> item = ModItems.ITEMS.register(name, supplier);
        ITEMS_BY_TAB.get(tab).put(name, item);
        return item;
    }

    public static void addWeaponToTab(String name, RegistryObject<Item> item) {
        ITEMS_BY_TAB.get(HBMEnums.CreativeTabRegistry.WEAPON_TAB).put(name, item);
    }

    public static Map<String, RegistryObject<Item>> getItemsForTab(HBMEnums.CreativeTabRegistry tab) {
        return ITEMS_BY_TAB.get(tab);
    }

}