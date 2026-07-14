package com.hbm.pawn;

import com.hbm.entity.mob.PawnEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Интерфейс для блоков, которые могут обслуживаться пешкой.
 */
public interface IPawnServicable extends IOwnable {

    ServicableType getServicableType();

    /**
     * Проверяет, нуждается ли блок в обслуживании прямо сейчас.
     */
    boolean needsService();

    /**
     * Приоритет обслуживания (0 – наивысший).
     */
    default int getServicePriority() {
        return 10;
    }

    BlockPos getPosition();

    Level getServiceLevel();

    /**
     * Выполнить операцию обслуживания.
     * @param pawn пешка, которая выполняет работу
     * @return true, если операция выполнена успешно
     */
    boolean executeService(PawnEntity pawn);

    default boolean isInterruptible() {
        return true;
    }

    default String getServiceId() {
        Level level = getServiceLevel();
        if (level == null) return "unknown:" + getPosition();
        return level.dimension().location() + ":" + getPosition();
    }
}