package com.hbm.handler.radiation;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Планировщик симуляции радиации.
 *
 * <p>Тикаются только чанки с радиацией плюс их соседи, и не больше бюджета за тик.
 *
 * <p>Соседи обязательны: обмен на границе обрабатывает чанк с меньшей координатой
 * (западный и северный), поэтому если такой чанк не тикается, перенос в эти
 * стороны не происходит вообще — источник «стоит стеной» у своей западной границы.
 */
public final class RadiationTicker {

    /** Сколько чанков максимум обрабатывать за серверный тик. */
    private static final int CHUNKS_PER_TICK = 64;

    private static final Map<ResourceKey<Level>, Bucket> BUCKETS = new HashMap<>();

    /**
     * Чанки, у которых поле ещё не угасло: кандидаты на «мёртвую землю».
     * В 1.7.10 эту роль играла карта «чанк -> уровень радиации» в
     * ChunkRadiationHandlerSimple, и разрушение брало из неё случайные чанки
     * по всему миру, а не вокруг игрока.
     */
    private static final Map<ResourceKey<Level>, Set<ChunkPos>> HOT = new HashMap<>();

    private RadiationTicker() {}

    private static final class Bucket {
        final ArrayDeque<ChunkPos> queue = new ArrayDeque<>();
        final Set<ChunkPos> queued = new HashSet<>();
        /** Чанки, которым уже выдали «ореол» соседей. */
        final Set<ChunkPos> haloed = new HashSet<>();
    }

    public static void markActive(Level level, ChunkPos pos) {
        if (level.isClientSide) return;
        Bucket bucket = BUCKETS.computeIfAbsent(level.dimension(), key -> new Bucket());
        synchronized (bucket) {
            if (bucket.queued.add(pos)) {
                bucket.queue.add(pos);
            }
        }
    }

    /**
     * Радиация пришла в чанк: ставим в очередь сам чанк и его соседей,
     * чтобы перенос через границу работал во все стороны.
     */
    public static void markRadiationArrived(Level level, ChunkPos pos) {
        if (level.isClientSide) return;
        Bucket bucket = BUCKETS.computeIfAbsent(level.dimension(), key -> new Bucket());
        synchronized (bucket) {
            if (bucket.queued.add(pos)) {
                bucket.queue.add(pos);
            }
            if (!bucket.haloed.add(pos)) {
                return; // ореол уже выдан, соседей не дублируем
            }
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    ChunkPos neighbor = new ChunkPos(pos.x + dx, pos.z + dz);
                    if (bucket.queued.add(neighbor)) {
                        bucket.queue.add(neighbor);
                    }
                }
            }
        }
    }

    /** Снимок горячих чанков измерения. */
    public static List<ChunkPos> hotChunks(ServerLevel level) {
        Set<ChunkPos> set = HOT.get(level.dimension());
        if (set == null) return List.of();
        synchronized (set) {
            return new ArrayList<>(set);
        }
    }

    public static void tick(ServerLevel level) {
        Bucket bucket = BUCKETS.get(level.dimension());
        if (bucket == null) return;

        // Process only entries that existed at the start of the server tick.
        // Active chunks requeue themselves during processing. Without this
        // snapshot, a small queue can consume the entire budget by processing
        // the same chunk repeatedly within one game tick. Entries added during
        // this pass are deliberately deferred until the next server tick.
        final int workThisTick;
        synchronized (bucket) {
            workThisTick = Math.min(CHUNKS_PER_TICK, bucket.queue.size());
        }

        for (int i = 0; i < workThisTick; i++) {
            ChunkPos pos;
            synchronized (bucket) {
                pos = bucket.queue.poll();
                if (pos == null) return;
                bucket.queued.remove(pos);
            }

            IRadiationData data = RadiationCapability.getChunkData(level, pos);
            if (data == null) continue;

            data.tick(level, pos);
            boolean active = data.maxRadiation() >= RadiationDataPrism.ACTIVE_THRESHOLD;
            setHot(level, pos, active);
            if (active) {
                markRadiationArrived(level, pos);
            } else if (hasRadiatingNeighbor(bucket, pos)) {
                // Сосед светит — этот чанк нужен как принимающая сторона обмена.
                markActive(level, pos);
            } else {
                synchronized (bucket) {
                    // Поле угасло: в следующий раз ореол выдадим заново.
                    bucket.haloed.remove(pos);
                }
            }
        }
    }

    private static boolean hasRadiatingNeighbor(Bucket bucket, ChunkPos pos) {
        synchronized (bucket) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    if (bucket.haloed.contains(new ChunkPos(pos.x + dx, pos.z + dz))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void setHot(ServerLevel level, ChunkPos pos, boolean hot) {
        Set<ChunkPos> set = HOT.computeIfAbsent(level.dimension(), key -> new HashSet<>());
        synchronized (set) {
            if (hot) set.add(pos);
            else set.remove(pos);
        }
    }

    public static void clear(Level level) {
        BUCKETS.remove(level.dimension());
        Set<ChunkPos> set = HOT.remove(level.dimension());
        if (set != null) {
            synchronized (set) { set.clear(); }
        }
    }
}
