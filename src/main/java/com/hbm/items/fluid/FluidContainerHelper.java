package com.hbm.items.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FluidContainerHelper {

    public static final String FLUID_KEY = "FluidType";

    public static void setFluid(ItemStack stack, FluidTypeHBM fluid) {
        if (fluid == Fluids.NONE.get()) {
            stack.getOrCreateTag().putString(FLUID_KEY, Fluids.NONE.get().getName());
        } else {
            stack.getOrCreateTag().putString(FLUID_KEY, fluid.getName());
        }
    }

    public static FluidTypeHBM getFluid(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(FLUID_KEY)) {
            return Fluids.fromName(tag.getString(FLUID_KEY));
        }
        return Fluids.NONE.get();
    }

    public static int getFluidColor(ItemStack stack) {
        FluidTypeHBM fluid = getFluid(stack);
        return fluid != Fluids.NONE.get() ? fluid.getColor() : 0x888888;
    }

    public static boolean isEmpty(ItemStack stack) {
        return getFluid(stack) == Fluids.NONE.get();
    }

    public static ItemStack createEmpty(Item containerItem) {
        ItemStack stack = new ItemStack(containerItem);
        setFluid(stack, Fluids.NONE.get());
        return stack;
    }

    public static ItemStack createFilled(Item containerItem, FluidTypeHBM fluid) {
        ItemStack stack = new ItemStack(containerItem);
        setFluid(stack, fluid);
        return stack;
    }
}