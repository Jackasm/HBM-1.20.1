package com.hbm.creativetabs;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.*;
import com.hbm.items.ModItems;
import com.hbm.items.block.*;
import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class BlockTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.block_tab"))
                .icon(() -> ModItems.ORE_URANIUM.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.BLOCK_TAB, output);

                    for (BlockOreBasalt.EnumBasaltOreType type : BlockOreBasalt.EnumBasaltOreType.values()) {
                        int ordinal = type.ordinal();
                        ItemStack stack = new ItemStack(ModItems.ORE_BASALT.get(), 1);
                        stack.getOrCreateTag().putInt("CustomModelData", ordinal);
                        output.accept(stack);
                    }

                    for (BlockResourceStone.EnumStoneType type : BlockResourceStone.EnumStoneType.values()) {
                        output.accept(ItemBlockResourceStone.withType(type));
                    }

                    for (BlockGlyphid.GlyphidType type : BlockGlyphid.GlyphidType.values()) {
                        output.accept(ItemGlyphid.withType(type));
                    }

                    for (int i = 0; i < 3; i++) {
                        if (i == 0) continue;
                        output.accept(ItemBlockGlyphidSpawner.withType(i));
                    }

                    for (int i = 0; i < BlockSellafield.SELLAFITE_LEVELS; i++) {
                        output.accept(ItemBlockSellafield.withLevel(ModBlocks.SELLAFIELD.get(), i));
                    }

                    for (BlockSnowglobe.SnowglobeType type : BlockSnowglobe.SnowglobeType.values()) {
                        if (type != BlockSnowglobe.SnowglobeType.NONE) {
                            output.accept(ItemSnowglobe.withType(type));
                        }
                    }

                    for (BlockPlushie.PlushieType type : BlockPlushie.PlushieType.values()) {
                        if (type != BlockPlushie.PlushieType.NONE) {
                            output.accept(ItemPlushie.withType(type));
                        }
                    }

                })
                .build();
    }
}