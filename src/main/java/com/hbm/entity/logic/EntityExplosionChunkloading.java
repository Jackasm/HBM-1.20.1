package com.hbm.entity.logic;

import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.jetbrains.annotations.NotNull;

public abstract class EntityExplosionChunkloading extends Entity implements IChunkLoader {

    private ChunkPos loadedChunk;
    private String modId = RefStrings.MODID;
    private boolean chunksForced = false;

    public EntityExplosionChunkloading(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        // Нет синхронизируемых данных
    }

    @Override
    public void init(ServerLevel level, String modId) {
        this.modId = modId;
        if (!level.isClientSide && !chunksForced) {
            ChunkPos chunkPos = level.getChunkAt(blockPosition()).getPos();
            forceChunk(level, chunkPos, true, false);
            chunksForced = true;
        }
    }

    protected void forceChunk(ServerLevel level, ChunkPos chunkPos, boolean add, boolean ticking) {
        ForgeChunkManager.forceChunk(level, modId, this, chunkPos.x, chunkPos.z, add, ticking);
    }

    public void loadChunk(BlockPos pos) {
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            ChunkPos newChunk = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);

            if (this.loadedChunk != null && !this.loadedChunk.equals(newChunk)) {
                // Освобождаем старый чанк
                ForgeChunkManager.forceChunk(serverLevel, modId, this, loadedChunk.x, loadedChunk.z, false, false);
            }

            if (this.loadedChunk == null || !this.loadedChunk.equals(newChunk)) {
                this.loadedChunk = newChunk;
                ForgeChunkManager.forceChunk(serverLevel, modId, this, loadedChunk.x, loadedChunk.z, true, false);
            }
        }
    }

    public void clearChunkLoader() {
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            if (loadedChunk != null) {
                ForgeChunkManager.forceChunk(serverLevel, modId, this, loadedChunk.x, loadedChunk.z, false, false);
                loadedChunk = null;
            }
            chunksForced = false;
        }
    }

    @Override
    public void loadNeighboringChunks(ServerLevel level, ChunkPos centerChunk, String modId) {
        // Для взрывов обычно не нужно загружать соседние чанки, только центральный
        if (!level.isClientSide) {
            // Освобождаем старый чанк если он есть
            if (loadedChunk != null && !loadedChunk.equals(centerChunk)) {
                ForgeChunkManager.forceChunk(level, modId, this, loadedChunk.x, loadedChunk.z, false, false);
            }

            // Загружаем новый центральный чанк
            if (loadedChunk == null || !loadedChunk.equals(centerChunk)) {
                loadedChunk = centerChunk;
                ForgeChunkManager.forceChunk(level, modId, this, centerChunk.x, centerChunk.z, true, false);
            }
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        this.clearChunkLoader();
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        // Нет данных для загрузки
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        // Нет данных для сохранения
    }
}