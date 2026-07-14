package com.hbm.creativetabs;


import com.hbm.items.ItemRegistryHelper;
import com.hbm.util.HBMEnums;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class TabContentProvider {

    public static void addItemsToTab(HBMEnums.CreativeTabRegistry tab, CreativeModeTab.Output output) {
        Map<String, RegistryObject<Item>> items = ItemRegistryHelper.getItemsForTab(tab);

        if (items.isEmpty()) {
            // Добавляем заглушку, если вкладка пустая
            TabFallback.addFallbackItem(output);
        } else {
            // Добавляем все предметы из этой вкладки
            items.values().forEach(itemRegistry -> output.accept(new ItemStack(itemRegistry.get())));
        }
    }
}