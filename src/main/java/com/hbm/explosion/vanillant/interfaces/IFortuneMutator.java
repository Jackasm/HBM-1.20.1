package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Интерфейс для модификации уровня удачи (Fortune) при выпадении предметов из блоков
 * Позволяет динамически изменять уровень удачи для дропа при взрывах
 */
public interface IFortuneMutator {

    /**
     * Модифицирует уровень удачи для выпадения блока при взрыве
     * (Для обратной совместимости)
     *
     * @param explosion экземпляр взрыва VNT
     * @param block блок, который будет разрушен
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @return модифицированный уровень удачи (0 = нет удачи)
     */
    int mutateFortune(ExplosionVNT explosion, Block block, int x, int y, int z);

    /**
     * Современная версия метода с использованием BlockState
     * (Реализация по умолчанию для обратной совместимости)
     *
     * @param explosion экземпляр взрыва VNT
     * @param state состояние блока, который будет разрушен
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @return модифицированный уровень удачи (0 = нет удачи)
     */
    default int mutateFortune(ExplosionVNT explosion, BlockState state, int x, int y, int z) {
        return mutateFortune(explosion, state.getBlock(), x, y, z);
    }
}