package com.hbm.world;

import com.hbm.network.PacketDispatcher;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import java.lang.reflect.Field;


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

    public static void setBiomeAndSync(Level level, int x, int z, Holder<Biome> biome) {
        if (level.isClientSide) return;

        LevelChunk chunk = level.getChunk(x >> 4, z >> 4);
        int biomeX = (x & 15) >> 2;
        int biomeY = 0; // для поверхности
        int biomeZ = (z & 15) >> 2;

        try {
            int sectionIndex = chunk.getSectionIndex(0);
            LevelChunkSection section = chunk.getSections()[sectionIndex];

            // Получаем текущий (только для чтения) контейнер биомов
            PalettedContainerRO<Holder<Biome>> oldBiomes = section.getBiomes();
            // Создаём редактируемую копию
            PalettedContainer<Holder<Biome>> newBiomes = oldBiomes.recreate();
            // Устанавливаем биом
            newBiomes.set(biomeX, biomeY, biomeZ, biome);

            // Заменяем поле через отражение
            Field biomesField = LevelChunkSection.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);
            biomesField.set(section, newBiomes);

            chunk.setUnsaved(true);

            if (level instanceof ServerLevel serverLevel) {
                int biomeId = serverLevel.registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getId(biome.value());
                PacketDispatcher.sendBiomeSyncToAllAround(serverLevel, x, z, (short) biomeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}