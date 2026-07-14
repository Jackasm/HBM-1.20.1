package com.hbm.creativetabs;

import com.hbm.items.ItemEnumMulti;
import com.hbm.items.ModItems;
import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ConsumableTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.consumable_tab"))
                .icon(() -> {
                    var items = com.hbm.items.ItemRegistryHelper.getItemsForTab(HBMEnums.CreativeTabRegistry.CONSUMABLE_TAB);
                    if (!items.isEmpty()) {
                        return new ItemStack(items.values().iterator().next().get());
                    }
                    return TabFallback.getFallbackIcon();
                })
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.CONSUMABLE_TAB, output);
                    ItemEnumMulti conserve = (ItemEnumMulti) ModItems.CANNED_CONSERVE.get();
                    for (Object enumConstant : conserve.theEnum.getEnumConstants()) {
                        output.accept(conserve.stackFromEnum((Enum) enumConstant));
                    }
                })
                .build();
    }
}