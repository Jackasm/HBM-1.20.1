package com.hbm.explosion.vanillant.interfaces;

import java.util.HashSet;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBlockProcessor {

    /**
     * Обрабатывает блоки, затронутые взрывом
     *
     * @param explosion экземпляр взрыва
     * @param world мир
     * @param x координата X центра взрыва
     * @param y координата Y центра взрыва
     * @param z координата Z центра взрыва
     * @param affectedBlocks набор позиций блоков для обработки
     */
    void process(ExplosionVNT explosion, Level world, double x, double y, double z, HashSet<BlockPos> affectedBlocks);
}