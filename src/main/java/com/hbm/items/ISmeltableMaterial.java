package com.hbm.items;

import com.hbm.inventory.material.Mats.MaterialStack;
import net.minecraft.world.item.ItemStack;

public interface ISmeltableMaterial {
    MaterialStack getMaterialStack(ItemStack stack);
}