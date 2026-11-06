package com.hbm.handler.radiation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Радиационное поле одного чанка.
 *
 * <p>API работает в мировых координатах блоков: вызывающему не нужно знать,
 * по какой сетке внутри разложены значения. Это позволяет менять разрешение
 * и добавлять интерполяцию, не трогая потребителей (гейгер, эффекты, блоки).
 */
public interface IRadiationData {

    float getRadiation(int x, int y, int z);

    /** Значение ячейки без интерполяции, индексы локальны для чанка. Для соседей при интерполяции. */
    float getRawCell(int localX, int localY, int localZ);

    void addRadiation(int x, int y, int z, float amount);

    /** Блок в этой точке изменился: сбросить кеш непрозрачности ячейки. */
    void invalidateCell(int x, int y, int z);

    /** Максимум по чанку — для проверок «фонтан/разрушение мира» без обхода сетки снаружи. */
    float maxRadiation();

    /** Обнулить поле чанка (сброс мира, тесты). */
    void clear();

    /** Один шаг симуляции для этого чанка. */
    void tick(Level level, ChunkPos pos);

    void markDirty();
    boolean isDirty();
    void clearDirty();

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
