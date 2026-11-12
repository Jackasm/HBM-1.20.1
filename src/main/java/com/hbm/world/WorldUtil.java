package com.hbm.world;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;



public class WorldUtil {


    public static void loadAndSpawnEntityInWorld(Entity entity) {
        Level level = entity.level();
        if (level.isClientSide) return;

        int chunkX = entity.chunkPosition().x;
        int chunkZ = entity.chunkPosition().z;
        int loadRadius = 2;

        // Загружаем чанки вокруг
        for (int dx = -loadRadius; dx <= loadRadius; dx++) {
            for (int dz = -loadRadius; dz <= loadRadius; dz++) {
                level.getChunk(chunkX + dx, chunkZ + dz);
            }
        }

        // Проверяем, не добавлена ли уже сущность
        if (!entity.isAddedToWorld()) {
            if (!MinecraftForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, level))) {
                // Добавляем сущность через уровень
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.addFreshEntity(entity);
                } else {
                    // Fallback для клиента (обычно не используется)
                    level.addFreshEntity(entity);
                }
            }
        }
    }

    /**
     * Apply crater biomes to every vertical biome section of a whole chunk.
     * MC 1.20.1 stores biomes in 4x4x4 cells; writing only the Y=0 section
     * leaves the player's surface climate unchanged.
     *
     * The vanilla ChunkMap sends the full biome data to tracking clients
     * once per changed chunk, not 256 no-op per-column custom packets.
     */
    public static boolean applyCraterBiomes(ServerLevel server, net.minecraft.world.level.ChunkPos pos,
                                           double centerX, double centerZ, int scale) {
        if (scale < 25) return false;

        LevelChunk chunk = server.getChunk(pos.x, pos.z);
        var registry = server.registryAccess().registryOrThrow(Registries.BIOME);
        boolean[] changed = {false};

        chunk.fillBiomesFromNoise((quartX, quartY, quartZ, sampler) -> {
            Holder<Biome> oldBiome = chunk.getNoiseBiome(quartX, quartY, quartZ);
            double distance = Math.hypot(quartX * 4D + 2D - centerX, quartZ * 4D + 2D - centerZ);
            var target = com.hbm.explosion.CraterFalloutRules.targetBiome(
                    distance * 100D / scale, scale, oldBiome.unwrapKey().orElse(null));
            if (target == null) return oldBiome;
            Holder<Biome> replacement = registry.getHolder(target).orElse(null);
            if (replacement == null || replacement.equals(oldBiome)) return oldBiome;
            changed[0] = true;
            return replacement;
        }, server.getChunkSource().randomState().sampler());

        if (changed[0]) {
            chunk.setUnsaved(true);
            server.getChunkSource().chunkMap.resendBiomesForChunks(java.util.List.of(chunk));
        }
        return changed[0];
    }
}
