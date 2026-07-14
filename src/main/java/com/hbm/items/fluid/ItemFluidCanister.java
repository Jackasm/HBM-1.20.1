package com.hbm.items.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.items.ModItems;
import net.minecraft.world.item.ItemStack;

public class ItemFluidCanister extends ItemFluidContainer {

    public static final int CAPACITY = 8000;

    public ItemFluidCanister(Properties properties) {
        super(properties, CAPACITY);
    }

    public static ItemStack createEmpty() {
        return createEmpty(ModItems.FLUID_CANISTER.get());
    }

    public static ItemStack createForFluid(FluidTypeHBM fluid) {
        return createForFluid(ModItems.FLUID_CANISTER.get(), fluid);
    }

    public static FluidTypeHBM getFluidType(ItemStack stack) {
        return FluidContainerHelper.getFluid(stack);
    }

    public static boolean isEmpty(ItemStack stack) {
        return FluidContainerHelper.isEmpty(stack);
    }

    public static int getFluidColor(ItemStack stack) {
        return FluidContainerHelper.getFluidColor(stack);
    }
}