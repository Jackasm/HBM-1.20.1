package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;

public interface IFluidContainer {

    /**
     * Возвращает максимальную вместимость контейнера для указанного типа жидкости
     */
    long getCapacity(FluidTypeHBM type);

    /**
     * Возвращает текущее количество жидкости в контейнере
     */
    long getFluidAmount(FluidTypeHBM type);

    /**
     * Проверяет, пуст ли контейнер
     */
    default boolean isEmpty(FluidTypeHBM type) {
        return getFluidAmount(type) <= 0;
    }

    FluidTypeHBM getFluidType();

}