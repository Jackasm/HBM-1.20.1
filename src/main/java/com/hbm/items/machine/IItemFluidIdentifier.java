package com.hbm.items.machine;

import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IItemFluidIdentifier {
    FluidTypeHBM getType(Level world, BlockPos pos, ItemStack stack);
}