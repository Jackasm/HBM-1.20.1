package com.hbm.handler.pollution;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public interface IPollutionData {
    float getPollution(PollutionType type);
    void setPollution(PollutionType type, float amount);
    void addPollution(PollutionType type, float amount);

    /** Вызывается, когда чанк загружен и рядом есть игроки – обновляет значения с учётом времени */
    void tick(Level level, ChunkPos pos);

    /** Принудительно пометить данные как изменённые (для сохранения) */
    void markDirty();

    /** Получить время последнего обновления (в тиках мира) */
    long getLastUpdateTick();
}