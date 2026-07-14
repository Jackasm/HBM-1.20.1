package com.hbm.items.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.items.ModItems;
import net.minecraft.world.item.ItemStack;

public class ItemDisperser extends ItemFluidContainer {

    public static final int CAPACITY = 2000;

    public ItemDisperser(Properties properties) {
        super(properties, CAPACITY);
    }

    public static ItemStack createEmpty() {
        return createEmpty(ModItems.DISPERSER_CANISTER.get());
    }

    public static ItemStack createForFluid(FluidTypeHBM fluid) {
        return createForFluid(ModItems.DISPERSER_CANISTER.get(), fluid);
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