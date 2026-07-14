package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.level.Level;

public interface IExplosionSFX {

    /**
     * Выполняет звуковые и визуальные эффекты взрыва
     *
     * @param explosion экземпляр взрыва
     * @param world мир
     * @param x координата X центра взрыва
     * @param y координата Y центра взрыва
     * @param z координата Z центра взрыва
     * @param size радиус взрыва
     */
    void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size);
}