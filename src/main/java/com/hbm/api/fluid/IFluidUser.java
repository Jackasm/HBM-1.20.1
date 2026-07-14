package com.hbm.api.fluid;

import com.hbm.api.tile.ILoadedTile;
import com.hbm.inventory.fluid.tank.FluidTankHBM;

public interface IFluidUser extends IFluidConnector, ILoadedTile {

    int HIGHEST_VALID_PRESSURE = 5;
    int[] DEFAULT_PRESSURE_RANGE = new int[] {0, 0};

    boolean particleDebug = false;

    FluidTankHBM[] getAllTanks();
}