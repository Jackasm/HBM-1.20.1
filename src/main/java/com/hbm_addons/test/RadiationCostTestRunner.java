package com.hbm_addons.test;

import com.hbm.handler.radiation.IRadiationData;
import com.hbm.handler.radiation.RadiationCapability;
import com.hbm.handler.radiation.RadiationDataPrism;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Стоимость симуляции при многих источниках.
 *
 * <p>Тикер ограничен бюджетом 64 чанка за серверный тик, но цену одного шага
 * никто не мерил. Сценарий держит {@code N} активных чанков и замеряет время
 * шага и запроса значения.</p>
 */
public class RadiationCostTestRunner extends TestRunner {

    private static final int START_X = 50;
    private static final int START_Z = 50;
    private static final int ACTIVE_CHUNKS = 32;
    private static final int MEASURE_TICKS = 100;
    private static final float SOURCE = 5_000F;

    private final List<ChunkPos> forcedChunks = new ArrayList<>();
    private long tickNanos;
    private long queryNanos;
    private int measured;

    public RadiationCostTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        int tick = tickCount++;

        if (tick == 0) {
            for (int dx = 0; dx < 8; dx++) {
                for (int dz = 0; dz < 4; dz++) {
                    forcedChunks.add(TestChunks.force(level, START_X + dx, START_Z + dz));
                }
            }
            int i = 0;
            for (ChunkPos pos : forcedChunks) {
                IRadiationData data = RadiationCapability.getChunkData(level, pos);
                if (data == null) continue;
                data.clear();
                data.addRadiation(pos.getMinBlockX() + 8,
                        level.getMinBuildHeight() + 8 * RadiationDataPrism.CELL_V + 8,
                        pos.getMinBlockZ() + 8, SOURCE);
                i++;
            }
            metric("active_chunks", i);
            check("chunks_prepared", i >= ACTIVE_CHUNKS / 2);
            return false;
        }

        // Замер одного полного прохода по активным чанкам.
        long start = System.nanoTime();
        for (ChunkPos pos : forcedChunks) {
            IRadiationData data = RadiationCapability.getChunkData(level, pos);
            if (data != null) data.tick(level, pos);
        }
        tickNanos += System.nanoTime() - start;

        // Запрос значения в горячей точке: 10 000 обращений за тик замера.
        long qStart = System.nanoTime();
        float probe = 0;
        ChunkPos first = forcedChunks.get(0);
        IRadiationData firstData = RadiationCapability.getChunkData(level, first);
        for (int k = 0; k < 10_000 && firstData != null; k++) {
            probe += firstData.getRadiation(first.getMinBlockX() + 8,
                    level.getMinBuildHeight() + 8 * RadiationDataPrism.CELL_V + 8,
                    first.getMinBlockZ() + 8);
        }
        queryNanos += System.nanoTime() - qStart;
        if (probe < 0) check("probe_sane", false);

        measured++;

        if (measured >= MEASURE_TICKS) {
            long perTick = tickNanos / measured;
            long perQuery = queryNanos / (measured * 10_000L);
            metric("tick_us_per_pass", perTick / 1000);
            metric("tick_us_per_chunk", perTick / 1000 / Math.max(1, forcedChunks.size()));
            metric("query_ns", perQuery);

            check("tick_budget_ok", perTick < 50_000_000L);   // 50 мс на проход — с большим запасом
            check("query_cost_ok", perQuery < 1_000L);        // меньше микросекунды на запрос

            TestChunks.release(level, forcedChunks);
            finish(checks.stream().allMatch(c -> c.pass));
            return true;
        }

        return false;
    }
}
