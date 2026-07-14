package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;

public interface IFluidProvider extends IFluidUser{

    /**
     * Использование жидкости из провайдера
     */
    void useUpFluid(FluidTypeHBM type, int pressure, long amount);


    /**
     * Получение доступного количества жидкости
     */
    long getFluidAvailable(FluidTypeHBM type, int pressure);

    default long getProviderSpeed(FluidTypeHBM type, int pressure) { return 1_000_000_000; }

    default int[] getProvidingPressureRange(FluidTypeHBM type) { return DEFAULT_PRESSURE_RANGE; }

}