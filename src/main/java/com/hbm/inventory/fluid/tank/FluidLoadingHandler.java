package com.hbm.inventory.fluid.tank;

import net.minecraftforge.items.ItemStackHandler;

public abstract class FluidLoadingHandler {

    public abstract boolean fillItem(ItemStackHandler handler, int in, int out, FluidTankHBM tank);
    public abstract boolean emptyItem(ItemStackHandler handler, int in, int out, FluidTankHBM tank);
}

