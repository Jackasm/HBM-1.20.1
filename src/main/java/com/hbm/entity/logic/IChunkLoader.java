package com.hbm.entity.logic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public interface IChunkLoader {

    /**
     * Инициализирует загрузку чанков для сущности
     * @param level уровень, в котором находится сущность
     * @param modId ID мода для регистрации чанков
     */
    void init(ServerLevel level, String modId);

    /**
     * Загружает соседние чанки вокруг указанной позиции
     * @param level уровень
     * @param centerChunk центральный чанк
     * @param modId ID мода
     */
    void loadNeighboringChunks(ServerLevel level, ChunkPos centerChunk, String modId);

    /**
     * Очищает загрузчик чанков
     */
    void clearChunkLoader();
}