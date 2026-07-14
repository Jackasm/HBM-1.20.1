package com.hbm.inventory.fluid.tank;

import java.util.Random;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.machine.ItemInfiniteFluid;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FluidLoaderInfinite extends FluidLoadingHandler {

    private static final Random RAND = new Random();

    @Override
    public boolean fillItem(ItemStackHandler handler, int in, int out, FluidTankHBM tank) {
        ItemStack stack = handler.getStackInSlot(in);

        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfiniteFluid item))
            return false;

        if (!item.allowPressure(tank.getPressure()))
            return false;
        if (item.getType() != null && tank.getTankType() != item.getType())
            return false;

        if (item.getChance() <= 1 || RAND.nextInt(item.getChance()) == 0) {
            tank.setFill(Math.max(tank.getFill() - item.getAmount(), 0));
        }

        return true;
    }

    @Override
    public boolean emptyItem(ItemStackHandler handler, int in, int out, FluidTankHBM tank) {
        ItemStack stack = handler.getStackInSlot(in);

        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfiniteFluid item) || tank.getTankType() == Fluids.NONE.get())
            return false;

        if (item.getType() != null && tank.getTankType() != item.getType())
            return false;

        if (item.getChance() <= 1 || RAND.nextInt(item.getChance()) == 0) {
            tank.setFill(Math.min(tank.getFill() + item.getAmount(), tank.getMaxFill()));
        }

        return true;
    }
}