package com.hbm.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

/**
 * Для блоков, которые должны визуально соединяться с кабелями,
 * но не имеют собственного TileEntity (например, энергетические стены).
 */
public interface IEnergyConnectorBlock {

    /**
     * Проверяет, можно ли визуально подсоединить кабель к данному блоку с указанной стороны.
     */
    boolean canConnect(BlockGetter world, BlockPos pos, Direction dir);
}