package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Интерфейс для мутации блоков при взрывах
 * Позволяет изменять поведение блоков до и после взрыва
 */
public interface IBlockMutator {

    /**
     * Вызывается перед разрушением блока взрывом
     *
     * @param explosion экземпляр взрыва VNT
     * @param block блок, который будет разрушен
     * @param meta метаданные блока (для совместимости, может быть проигнорировано)
     * @param pos - координаты
     */
    void mutatePre(ExplosionVNT explosion, Block block, int meta, BlockPos pos);

    /**
     * Вызывается после разрушения блока взрывом
     *
     * @param explosion экземпляр взрыва VNT
     * @param pos - координаты
     */
    void mutatePost(ExplosionVNT explosion, BlockPos pos);

    /**
     * Современная версия метода mutatePre с использованием BlockState
     * (необязательная реализация)
     *
     * @param explosion экземпляр взрыва VNT
     * @param state состояние блока, который будет разрушен
     * @param pos - координаты
     */
    default void mutatePre(ExplosionVNT explosion, BlockState state, BlockPos pos) {
        mutatePre(explosion, state.getBlock(), 0, pos);
    }
}