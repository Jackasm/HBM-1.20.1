package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.world.item.ItemStack;


public interface IFillableItem {

    /** Whether this stack can be filled with this type. Not particularly useful for normal operations */
    boolean acceptsFluid(FluidTypeHBM type, ItemStack stack);
    /** Tries to fill the stack, returns the remainder that couldn't be added */
    int tryFill(FluidTypeHBM type, int amount, ItemStack stack);
    /** Whether this stack can fill tiles with this type. Not particularly useful for normal operations */
    boolean providesFluid(FluidTypeHBM type, ItemStack stack);
    /** Provides fluid with the maximum being the requested amount */
    int tryEmpty(FluidTypeHBM type, int amount, ItemStack stack);
    /** Returns the first (or only) corrently held type, may return null. Currently only used for setting bedrock ores */
    FluidTypeHBM getFirstFluidType(ItemStack stack);
    /** Returns the fillstate for the specified fluid. Currently only used for setting bedrock ores */
    int getFill(ItemStack stack);

    boolean isFull(ItemStack stack);
}

