
package com.hbm.creativetabs;

import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MachineTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.machine_tab"))
                .icon(() -> {
                    var items = com.hbm.items.ItemRegistryHelper.getItemsForTab(HBMEnums.CreativeTabRegistry.MACHINE_TAB);
                    if (!items.isEmpty()) {
                        return new ItemStack(items.values().iterator().next().get());
                    }
                    return TabFallback.getFallbackIcon();
                })
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.MACHINE_TAB, output);


                })
                .build();
    }
}