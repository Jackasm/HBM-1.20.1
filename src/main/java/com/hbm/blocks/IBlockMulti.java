package com.hbm.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.chat.Component;

public interface IBlockMulti {

    int getSubCount();

    default Component getDisplayName(ItemStack stack) {
        // Возвращаем имя блока как компонент
        return ((Block) this).getName();
    }

    default Component getOverrideDisplayName(ItemStack stack) {
        return null;
    }

    default int rectify(int meta) {
        return Math.abs(meta % getSubCount());
    }
}