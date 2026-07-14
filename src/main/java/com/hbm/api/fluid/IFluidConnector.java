package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.core.Direction;

public interface IFluidConnector {

    /**
     * Проверка возможности подключения к указанной стороне с заданным типом жидкости
     */
    boolean canConnect(FluidTypeHBM type, Direction dir);
    /**
     * Получение типа жидкости коннектора
     */
    default FluidTypeHBM getFluidType() {
        return null;
    }

    /**
     * Установка типа жидкости коннектора
     */
    default boolean setFluidType(FluidTypeHBM type){return true;}

}