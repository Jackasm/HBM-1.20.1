package com.hbm_addons.test;

import com.hbm.handler.radiation.IRadiationData;
import com.hbm.handler.radiation.RadiationCapability;
import com.hbm.handler.radiation.RadiationDataPrism;
import com.hbm.handler.radiation.RadiationTicker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Regression for RadiationTicker same-server-tick reprocessing (P3).
 *
 * <p>The older scheduler processed a three-entry self-requeueing FIFO 64
 * times, rotating [A, B, C] into [B, C, A]. The fixed scheduler processes
 * only the three entries present at tick entry, preserving [A, B, C].
 * All reflection and test radiation are confined to this scenario.
 *
 * <p>This is an observable FIFO contract test, not a production instrumentation
 * counter of every individual call to IRadiationData.tick().</p>
 */
public final class RadiationSchedulerOnceRunner extends TestRunner {

    private static final List<ChunkPos> POSITIONS = List.of(
            new ChunkPos(400, 400),
            new ChunkPos(401, 400),
            new ChunkPos(402, 400));

    private final List<ChunkPos> forcedChunks = new ArrayList<>();
    private final List<ChunkPos> testSources = new ArrayList<>();

    public RadiationSchedulerOnceRunner(net.minecraft.server.level.ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean tick(MinecraftServer server) {
        tickCount++;

        Map<ResourceKey<Level>, Object> buckets = null;
        Map<ResourceKey<Level>, Set<ChunkPos>> hot = null;
        Object bucket = null;
        ArrayDeque<ChunkPos> queue = null;
        Set<ChunkPos> queued = null;
        Set<ChunkPos> haloed = null;
        List<ChunkPos> originalQueue = null;
        Set<ChunkPos> originalQueued = null;
        Set<ChunkPos> originalHaloed = null;
        Set<ChunkPos> originalHot = null;
        boolean hadBucket = false;
        boolean hadHot = false;

        try {
            long gameTime = level.getGameTime();
            Field lastTick = RadiationDataPrism.class.getDeclaredField("lastTick");
            Field lastSpread = RadiationDataPrism.class.getDeclaredField("lastSpread");
            lastTick.setAccessible(true);
            lastSpread.setAccessible(true);

            // Force the exact test chunks only; do not depend on nearby players,
            // worldgen radiation, or the asynchronous timing of chunk tickets.
            for (ChunkPos pos : POSITIONS) {
                forcedChunks.add(TestChunks.force(level, pos.x, pos.z));
                level.getChunk(pos.x, pos.z);
                IRadiationData data = RadiationCapability.getChunkData(level, pos);
                if (!(data instanceof RadiationDataPrism prism)) {
                    check("scheduler_once_prism_available", false);
                    return finishTest();
                }
                // Refuse to overwrite a non-empty test region.
                if (data.maxRadiation() > 0F) {
                    info("scheduler_once_precondition", "nonempty radiation at " + pos);
                    check("scheduler_once_clean_world", false);
                    return finishTest();
                }
                data.clear();
                data.addRadiation(pos.getMinBlockX() + 8,
                        level.getMinBuildHeight() + 8,
                        pos.getMinBlockZ() + 8, 100F);
                testSources.add(pos);
                // Prevent the test from invoking unrelated decay/spread.
                lastTick.setLong(prism, gameTime);
                lastSpread.setLong(prism, gameTime);
            }

            Field bucketField = RadiationTicker.class.getDeclaredField("BUCKETS");
            Field hotField = RadiationTicker.class.getDeclaredField("HOT");
            bucketField.setAccessible(true);
            hotField.setAccessible(true);
            buckets = (Map<ResourceKey<Level>, Object>) bucketField.get(null);
            hot = (Map<ResourceKey<Level>, Set<ChunkPos>>) hotField.get(null);

            hadBucket = buckets.containsKey(level.dimension());
            if (!hadBucket) {
                RadiationTicker.markActive(level, POSITIONS.get(0));
            }
            bucket = buckets.get(level.dimension());

            Field queueField = bucket.getClass().getDeclaredField("queue");
            Field queuedField = bucket.getClass().getDeclaredField("queued");
            Field haloedField = bucket.getClass().getDeclaredField("haloed");
            queueField.setAccessible(true);
            queuedField.setAccessible(true);
            haloedField.setAccessible(true);
            queue = (ArrayDeque<ChunkPos>) queueField.get(bucket);
            queued = (Set<ChunkPos>) queuedField.get(bucket);
            haloed = (Set<ChunkPos>) haloedField.get(bucket);

            synchronized (bucket) {
                originalQueue = new ArrayList<>(queue);
                originalQueued = new HashSet<>(queued);
                originalHaloed = new HashSet<>(haloed);
                queue.clear();
                queued.clear();
                haloed.clear();
                for (ChunkPos pos : POSITIONS) {
                    queue.addLast(pos);
                    queued.add(pos);
                    haloed.add(pos);
                }
            }

            hadHot = hot.containsKey(level.dimension());
            if (hadHot) {
                originalHot = new HashSet<>(hot.get(level.dimension()));
            }

            info("scheduler_once_before", new ArrayList<>(queue));
            RadiationTicker.tick(level);
            List<ChunkPos> after;
            Set<ChunkPos> afterQueued;
            Set<ChunkPos> afterHaloed;
            synchronized (bucket) {
                after = new ArrayList<>(queue);
                afterQueued = new HashSet<>(queued);
                afterHaloed = new HashSet<>(haloed);
            }
            info("scheduler_once_after", after);

            // All three entries must be requeued and remain distinct.
            check("scheduler_once_three_queued",
                    after.size() == 3 && afterQueued.size() == 3
                            && afterQueued.containsAll(POSITIONS)
                            && afterHaloed.containsAll(POSITIONS));
            // This is the crucial regression assertion: the OLD 64-iteration
            // algorithm rotates the queue by 64 mod 3, failing the check.
            check("scheduler_once_fifo_preserved", after.equals(POSITIONS));
        } catch (ReflectiveOperationException | ClassCastException | IllegalStateException e) {
            info("scheduler_once_exception", e.getClass().getSimpleName() + ": " + e.getMessage());
            check("scheduler_once_fixture", false);
        } finally {
            // Do not destroy any preexisting scheduler state in this dimension.
            if (bucket != null && originalQueue != null) {
                synchronized (bucket) {
                    queue.clear();
                    queue.addAll(originalQueue);
                    queued.clear();
                    queued.addAll(originalQueued);
                    haloed.clear();
                    haloed.addAll(originalHaloed);
                }
            }
            if (buckets != null && !hadBucket) {
                buckets.remove(level.dimension());
            }
            if (hot != null) {
                if (hadHot && originalHot != null) {
                    Set<ChunkPos> currentHot = hot.get(level.dimension());
                    if (currentHot != null) {
                        synchronized (currentHot) {
                            currentHot.clear();
                            currentHot.addAll(originalHot);
                        }
                    }
                } else {
                    hot.remove(level.dimension());
                }
            }
            // Remove only the three sources created by this scenario, not
            // other chunks or data that might already exist in the test world.
            for (ChunkPos pos : testSources) {
                IRadiationData data = RadiationCapability.getChunkData(level, pos);
                if (data != null) data.clear();
            }
            TestChunks.release(level, forcedChunks);
            info("scheduler_once_cleanup",
                    "released=" + forcedChunks.size() + " sources_cleared=" + testSources.size());
        }

        return finishTest();
    }

    private boolean finishTest() {
        finish(!checks.isEmpty() && checks.stream().allMatch(c -> c.pass));
        return true;
    }
}
