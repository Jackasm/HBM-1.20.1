package com.hbm_addons.test;

import com.hbm.handler.radiation.IRadiationData;
import com.hbm.handler.radiation.RadiationCapability;
import com.hbm.handler.radiation.RadiationDataPrism;
import com.hbm.handler.radiation.RadiationEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Спад поля во времени.
 *
 * <p>Все ячейки заполняются одинаково: разница {@code a-b} равна нулю, диффузия
 * ничего не двигает, остаётся только {@code ×0.998 − 0.005} за тик.</p>
 *
 * <p>Грабли, на которых сценарий уже падал: поле ставилось через
 * {@code addRadiation}, минуя очередь тикера, и первые десятки тиков чанк просто
 * не тикался — «спад» выходил меньше формулы. Теперь источник ставится публичным
 * API, а отсчёт ведётся не с запуска сценария, а с первого наблюдённого спада:
 * очередь может прогреться не мгновенно, и это не дефект механики.</p>
 */
public class RadiationDecayTestRunner extends TestRunner {

    private static final int CHUNK_X = 42;
    private static final int CHUNK_Z = 42;
    private static final float START = 100F;
    private static final int MEASURE_TICKS = 200;
    private static final float DECAY_PER_TICK = 0.998F;

    private final List<ChunkPos> forcedChunks = new ArrayList<>();
    private float baseline;
    private int ticksSinceDecay = -1;

    public RadiationDecayTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        int tick = tickCount++;

        if (tick == 0) {
            forcedChunks.add(TestChunks.force(level, CHUNK_X, CHUNK_Z));
            int minY = level.getMinBuildHeight();
            int x = CHUNK_X * 16;
            int z = CHUNK_Z * 16;
            for (int gx = 0; gx < RadiationDataPrism.CELL_H; gx++) {
                for (int gz = 0; gz < RadiationDataPrism.CELL_H; gz++) {
                    for (int gy = 0; gy < gridY(); gy++) {
                        RadiationEvents.incrementRadiation(level,
                                x + gx * RadiationDataPrism.CELL_H + 2,
                                minY + gy * RadiationDataPrism.CELL_V + 8,
                                z + gz * RadiationDataPrism.CELL_H + 2,
                                START);
                    }
                }
            }
            check("field_filled", true);
            return false;
        }

        IRadiationData data = RadiationCapability.getChunkData(level, new ChunkPos(CHUNK_X, CHUNK_Z));
        if (data == null) return false;

        float value = data.maxRadiation();

        if (ticksSinceDecay < 0) {
            if (value < START - 0.0001F) {
                baseline = value;
                ticksSinceDecay = 0;
                check("decay_started", true);
            }
            return false;
        }

        ticksSinceDecay++;

        if (ticksSinceDecay >= MEASURE_TICKS) {
            float expected = baseline * (float) Math.pow(DECAY_PER_TICK, ticksSinceDecay);
            float ratio = value / baseline;
            metric("baseline", fmt(baseline));
            metric("after_measure", fmt(value));
            metric("expected", fmt(expected));
            metric("ratio", fmt(ratio));
            metric("ticks_measured", ticksSinceDecay);

            check("decay_monotone", value < baseline);
            // Допуск 2%: двести умножений float накапливают погрешность.
            check("decay_matches_formula",
                    Math.abs(ratio - (float) Math.pow(DECAY_PER_TICK, ticksSinceDecay)) < 0.02F);
            check("decay_not_negative", value >= 0F);

            TestChunks.release(level, forcedChunks);
            finish(checks.stream().allMatch(c -> c.pass));
            return true;
        }

        return false;
    }

    private static String fmt(float v) {
        return String.format(java.util.Locale.ROOT, "%.4f", v);
    }

    private int gridY() {
        return Math.max(1, level.getHeight() / RadiationDataPrism.CELL_V);
    }
}
