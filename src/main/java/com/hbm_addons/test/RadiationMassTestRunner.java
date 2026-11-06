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
 * Сохранение величины поля при переносе.
 *
 * <p>Инвариант: диффузия перераспределяет поле между ячейками, но не создаёт и не
 * уничтожает его. Единственная законная убыль — собственный спад {@code 0.998^n}.</p>
 *
 * <p>Две грабли, на которых этот сценарий уже падал (обе — в тесте, не в моде):</p>
 * <ul>
 *   <li>источник ставился через {@code addRadiation} напрямую, минуя очередь тикера,
 *       поэтому чанк мог не тикаться вообще;</li>
 *   <li>окно 3×3 чанка слишком узкое: за 40 тиков поле уходит дальше, и «убыль»
 *       оказывалась утечкой за границу замера, а не дефектом.</li>
 * </ul>
 * <p>Поэтому источник ставится публичным API, а сумма считается по 5×5 чанкам,
 * которые предварительно загружены и очищены целиком.</p>
 */
public class RadiationMassTestRunner extends TestRunner {

    private static final int CHUNK_X = 70;
    private static final int CHUNK_Z = 70;
    private static final int RADIUS = 2;
    private static final float SOURCE = 10_000F;
    private static final int MEASURE_TICKS = 40;

    private final List<ChunkPos> forcedChunks = new ArrayList<>();
    private float totalBefore;
    private float baselineBefore;

    public RadiationMassTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        int tick = tickCount++;

        if (tick == 0) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    forcedChunks.add(TestChunks.force(level, CHUNK_X + dx, CHUNK_Z + dz));
                }
            }
            // Чистим ВСЁ окно: в сохранённом мире могло остаться поле от прошлых
            // прогонов, и загруженный позже чанк вернул бы его обратно в сумму.
            for (ChunkPos pos : forcedChunks) {
                IRadiationData data = RadiationCapability.getChunkData(level, pos);
                if (data != null) data.clear();
            }

            int x = CHUNK_X * 16 + 8;
            int y = level.getMinBuildHeight() + 12 * RadiationDataPrism.CELL_V + 8;
            int z = CHUNK_Z * 16 + 8;
            RadiationEvents.incrementRadiation(level, x, y, z, SOURCE);

            baselineBefore = sumArea();
            totalBefore = sumArea();
            metric("source", SOURCE);
            metric("total_before", fmt(totalBefore));
            metric("window_chunks", forcedChunks.size());
            metric("area_baseline", fmt(baselineBefore));
            check("source_placed", Math.abs(totalBefore - SOURCE) < 1F);
            return false;
        }

        if (tick >= MEASURE_TICKS) {
            float totalAfter = sumArea();
            float expected = SOURCE * (float) Math.pow(0.998, MEASURE_TICKS);
            metric("total_after", fmt(totalAfter));
            metric("expected_after_decay", fmt(expected));

            check("mass_not_created", totalAfter <= totalBefore + 1F);
            check("mass_loss_only_decay",
                    Math.abs(totalAfter - expected) <= Math.max(1F, expected * 0.05F));

            TestChunks.release(level, forcedChunks);
            finish(checks.stream().allMatch(c -> c.pass));
            return true;
        }

        return false;
    }

    private float sumArea() {
        float total = 0;
        for (ChunkPos pos : forcedChunks) {
            IRadiationData d = RadiationCapability.getChunkData(level, pos);
            if (d == null) continue;
            for (int gx = 0; gx < RadiationDataPrism.CELL_H; gx++) {
                for (int gz = 0; gz < RadiationDataPrism.CELL_H; gz++) {
                    for (int gy = 0; gy < gridY(); gy++) {
                        total += d.getRawCell(gx, gy, gz);
                    }
                }
            }
        }
        return total;
    }

    private static String fmt(float v) {
        return String.format(java.util.Locale.ROOT, "%.2f", v);
    }

    private int gridY() {
        return Math.max(1, level.getHeight() / RadiationDataPrism.CELL_V);
    }
}
