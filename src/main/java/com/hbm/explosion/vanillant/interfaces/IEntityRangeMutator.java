package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

/**
 * Интерфейс для модификации радиуса воздействия на сущности
 * Позволяет динамически изменять радиус взрыва для сущностей
 */
public interface IEntityRangeMutator {

    /**
     * Модифицирует радиус воздействия взрыва на сущности
     *
     * @param explosion экземпляр взрыва VNT
     * @param range исходный радиус взрыва
     * @return модифицированный радиус воздействия на сущности
     */
    float mutateRange(ExplosionVNT explosion, float range);
}