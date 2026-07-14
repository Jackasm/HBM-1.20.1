package com.hbm.api.energy;

import net.minecraft.core.Direction;

/**
 * Интерфейс для всех объектов, которые могут соединяться с энергосетью.
 */
public interface IEnergyConnector {

    /**
     * Можно ли подключиться к указанной стороне.
     * @param dir сторона этого блока
     * @return true, если подключение разрешено
     */
    default boolean canConnect(Direction dir) {
        return dir != null;
    }
}