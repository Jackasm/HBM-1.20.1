package com.hbm.creativetabs;

import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class WeaponTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.weapon_tab"))
                .icon(() -> ModGunItems.GUN_MARESLEG.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.WEAPON_TAB, output);
                })
                .build();
    }
}