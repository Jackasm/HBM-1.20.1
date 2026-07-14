package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.entity.Entity;

/**
 * Интерфейс для обработки кастомного урона от взрывов
 * Позволяет настраивать особые эффекты урона для разных типов взрывов
 */
public interface ICustomDamageHandler {

    /**
     * Обрабатывает атаку на сущность от взрыва
     *
     * @param explosion экземпляр взрыва VNT
     * @param entity сущность, получившая урон
     * @param distanceScaled нормализованное расстояние до центра взрыва (0-1)
     */
    void handleAttack(ExplosionVNT explosion, Entity entity, double distanceScaled);
}