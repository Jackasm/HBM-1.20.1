package com.hbm.handler.radiation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public interface IRadiationData {
    /** Получить радиацию в ячейке сетки (0..3 по каждой оси) */
    float getRadiation(int gridX, int gridY, int gridZ);

    /** Добавить радиацию в конкретную ячейку */
    void addRadiationAt(int gridX, int gridY, int gridZ, float amount);

    /** Вызывается при обновлении чанка */
    void tick(Level level, ChunkPos pos);

    void markDirty();
    boolean isDirty();
    void clearDirty();

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}