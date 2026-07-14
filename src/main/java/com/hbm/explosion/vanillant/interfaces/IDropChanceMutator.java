package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Интерфейс для модификации шанса выпадения блоков при взрыве
 * Позволяет динамически изменять вероятность выпадения предметов из разрушенных блоков
 */
public interface IDropChanceMutator {

    /**
     * Модифицирует шанс выпадения блока при взрыве
     * (Для обратной совместимости)
     *
     * @param explosion экземпляр взрыва VNT
     * @param block блок, который будет разрушен
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @param chance исходный шанс выпадения (0.0 - 1.0)
     * @return модифицированный шанс выпадения
     */
    float mutateDropChance(ExplosionVNT explosion, Block block, int x, int y, int z, float chance);

    /**
     * Современная версия метода с использованием BlockState
     * (Реализация по умолчанию для обратной совместимости)
     *
     * @param explosion экземпляр взрыва VNT
     * @param state состояние блока, который будет разрушен
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @param chance исходный шанс выпадения (0.0 - 1.0)
     * @return модифицированный шанс выпадения
     */
    default float mutateDropChance(ExplosionVNT explosion, BlockState state, int x, int y, int z, float chance) {
        return mutateDropChance(explosion, state.getBlock(), x, y, z, chance);
    }
}