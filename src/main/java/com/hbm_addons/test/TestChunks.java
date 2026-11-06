package com.hbm_addons.test;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Принудительная загрузка чанков для сценариев стенда.
 *
 * <p>Стенд стартует у спавна, а сценарии работают в своих чанках. Без загрузки
 * `RadiationCapability.getChunkData` возвращает null — и сценарий падает на
 * `chunk_loaded`, что и случилось при первом прогоне.</p>
 */
final class TestChunks {

    private TestChunks() {
    }

    /** Загружает чанк и возвращает его позицию для последующей разгрузки. */
    static ChunkPos force(ServerLevel level, int chunkX, int chunkZ) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        level.setChunkForced(chunkX, chunkZ, true);
        return pos;
    }

    /** Загружает квадрат чанков вокруг центра. */
    static List<ChunkPos> forceSquare(ServerLevel level, int centerX, int centerZ, int radius) {
        List<ChunkPos> forced = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                forced.add(force(level, centerX + dx, centerZ + dz));
            }
        }
        return forced;
    }

    static void release(ServerLevel level, List<ChunkPos> forced) {
        for (ChunkPos pos : forced) {
            level.setChunkForced(pos.x, pos.z, false);
        }
    }
}
