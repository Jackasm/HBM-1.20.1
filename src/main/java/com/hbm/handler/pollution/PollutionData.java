package com.hbm.handler.pollution;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class PollutionData implements IPollutionData {
    private static final float MAX_POLLUTION = 10_000F;
    private static final int SPREAD_INTERVAL = 100; // тиков между распространением (5 сек)

    public final float[] pollution = new float[PollutionType.values().length];
    private long lastUpdateTick;
    private boolean dirty;

    // Коэффициенты для естественного спада (за тик)
    private static final float DECAY_SOOT = 0.9995f;
    private static final float DECAY_POISON = 0.998f;
    private static final float DECAY_HEAVYMETAL = 0.9999f;
    private static final float DECAY_FALLOUT = 0.999f;

    // Порог для дополнительного спада (при высоких значениях)
    private static final float HIGH_THRESHOLD = 10f;
    private static final float HIGH_DECAY_SOOT = 0.99f;
    private static final float HIGH_DECAY_POISON = 0.995f;

    public PollutionData() {
        this.lastUpdateTick = 0;
    }

    @Override
    public float getPollution(PollutionType type) {
        return pollution[type.ordinal()];
    }

    @Override
    public void setPollution(PollutionType type, float amount) {
        int idx = type.ordinal();
        pollution[idx] = Math.max(0, Math.min(MAX_POLLUTION, amount));
        markDirty();
    }

    @Override
    public void addPollution(PollutionType type, float amount) {
        setPollution(type, getPollution(type) + amount);
    }

    @Override
    public void tick(Level level, ChunkPos pos) {
        long currentTick = level.getGameTime();
        long ticksPassed = currentTick - lastUpdateTick;
        if (ticksPassed <= 0) return;

        // Применяем естественный спад за прошедшие тики
        applyDecay(ticksPassed);

        // Если прошло достаточно тиков, делаем распространение на соседей
        if (ticksPassed >= SPREAD_INTERVAL) {
            spreadToNeighbors(level, pos);
        }

        this.lastUpdateTick = currentTick;
        markDirty();
    }

    private void applyDecay(long ticks) {
        int S = PollutionType.SOOT.ordinal();
        int P = PollutionType.POISON.ordinal();
        int H = PollutionType.HEAVYMETAL.ordinal();
        int F = PollutionType.FALLOUT.ordinal();

        for (int i = 0; i < ticks; i++) {
            // SOOT
            if (pollution[S] > HIGH_THRESHOLD) pollution[S] *= HIGH_DECAY_SOOT;
            pollution[S] *= DECAY_SOOT;

            // POISON
            if (pollution[P] > HIGH_THRESHOLD) pollution[P] *= HIGH_DECAY_POISON;
            pollution[P] *= DECAY_POISON;

            // HEAVYMETAL
            pollution[H] *= DECAY_HEAVYMETAL;

            // FALLOUT
            pollution[F] *= DECAY_FALLOUT;
        }

        // Обрезаем по максимуму
        for (int i = 0; i < pollution.length; i++) {
            pollution[i] = Math.min(MAX_POLLUTION, pollution[i]);
        }
    }

    private void spreadToNeighbors(Level level, ChunkPos pos) {
        int S = PollutionType.SOOT.ordinal();
        int P = PollutionType.POISON.ordinal();

        float sootSpread = 0;
        float poisonSpread = 0;

        if (pollution[S] > HIGH_THRESHOLD) {
            sootSpread = pollution[S] * 0.05f;
            pollution[S] *= 0.8f;
        }
        if (pollution[P] > HIGH_THRESHOLD) {
            poisonSpread = pollution[P] * 0.025f;
            pollution[P] *= 0.9f;
        }

        if (sootSpread <= 0.001f && poisonSpread <= 0.001f) return;

        int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] offset : offsets) {
            ChunkPos neighborPos = new ChunkPos(pos.x + offset[0], pos.z + offset[1]);
            // Проверяем, что чанк загружен
            if (!level.hasChunk(neighborPos.x, neighborPos.z)) continue;
            if (level instanceof ServerLevel sl && !sl.isPositionEntityTicking(neighborPos.getWorldPosition())) continue;

            IPollutionData neighbor = PollutionCapability.getChunkData(level, neighborPos);
            if (neighbor != null) {
                if (sootSpread > 0) neighbor.addPollution(PollutionType.SOOT, sootSpread);
                if (poisonSpread > 0) neighbor.addPollution(PollutionType.POISON, poisonSpread);
            }
        }
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    @Override
    public long getLastUpdateTick() {
        return lastUpdateTick;
    }

    public void setLastUpdateTick(long tick) {
        this.lastUpdateTick = tick;
    }

    // ======== NBT ========
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (PollutionType type : PollutionType.values()) {
            tag.putFloat(type.name().toLowerCase(), getPollution(type));
        }
        tag.putLong("lastUpdate", lastUpdateTick);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        for (PollutionType type : PollutionType.values()) {
            setPollution(type, tag.getFloat(type.name().toLowerCase()));
        }
        this.lastUpdateTick = tag.getLong("lastUpdate");
    }
}