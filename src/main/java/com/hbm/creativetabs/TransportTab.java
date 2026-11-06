package com.hbm.creativetabs;

import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class TransportTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.transport_tab"))
                .icon(() -> new ItemStack(com.hbm.blocks.ModBlocks.RAIL_HIGHSPEED.get()))
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.TRANSPORT_TAB, output);
                })
                .build();
    }
}