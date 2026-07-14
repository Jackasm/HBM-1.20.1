package com.hbm.explosion.vanillant.interfaces;

import java.util.HashSet;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBlockAllocator {

    /**
     * Выделяет блоки, которые будут затронуты взрывом
     *
     * @param explosion экземпляр взрыва
     * @param world мир
     * @param x координата X центра взрыва
     * @param y координата Y центра взрыва
     * @param z координата Z центра взрыва
     * @param size радиус взрыва
     * @return набор позиций блоков, которые будут затронуты
     */
    HashSet<BlockPos> allocate(ExplosionVNT explosion, Level world, double x, double y, double z, float size);
}