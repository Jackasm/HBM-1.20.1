package com.hbm_addons.test;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Arrays;
import java.util.Locale;

/**
 * Прилёт в неисследованную точку: за сколько сервер отдаёт чанки радиуса 8
 * и насколько при этом залипает тик (#193).
 *
 * <p>Сценарий повторяет то, что делает телепорт: серверу называют чанки вокруг
 * точки прилёта, и он обязан их отдать. Выдача идёт потоком, как клиенту: за тик
 * берётся столько чанков, сколько успевается за бюджет {@code hbm.chunk.budget_ms}
 * (400 мс), но не меньше одного. Пока чанк не готов,
 * {@link net.minecraft.server.level.ServerLevel#getChunk(int, int)} держит тик,
 * поэтому задержка тика — это и есть залипание сервера.</p>
 *
 * <p>Меряется: время до полной загрузки радиуса, время генерации по чанкам
 * (медиана, p95, максимум — сравнимо с {@code worldgen_bench}) и максимальная
 * задержка тика. Пороги из #193: радиус ≤ 12 с, тик ≤ 1 с.</p>
 *
 * <p>Регион задаётся свойствами {@code -Phbm.chunk.cx=} и {@code -Phbm.chunk.cz=}
 * (центр в чанках, по умолчанию 48200). Регион нужно менять каждый прогон:
 * на уже сгенерированных чанках мерить нечего. Прогрев — 8 чанков на
 * расстоянии 16 от центра, в том же регион-файле, чтобы удаление файла
 * сбрасывало и прогрев, и замер.</p>
 */
public class ChunkLoadBenchTestRunner extends TestRunner {

    private static final int RADIUS = 8;
    private static final int SIDE = 2 * RADIUS + 1;
    private static final int CHUNK_COUNT = SIDE * SIDE;
    private static final int WARMUP = 8;
    private static final int WARMUP_OFFSET = RADIUS + 8;
    private static final int TIMEOUT_TICKS = 600;

    /** Сколько тик готов отдавать чанки; больше — уже залипание (порог #193). */
    private static final long DELIVERY_BUDGET_MS = 400L;

    /**
     * Пороги из #193: после этого играть можно, а сервер не выкидывает.
     * Загрузка — 12 с, а не 10: замер 05.11.2026 дал 10,0–10,3 с, и порог
     * в 10 с не держался ни с каким запасом. Решение владельца 05.11.2026:
     * порог с запасом, оптимизация генерации — отдельной карточкой.
     */
    private static final double LOAD_BUDGET_MS = 12_000.0;
    private static final double TICK_BUDGET_MS = 1_000.0;

    private final int baseCx;
    private final int baseCz;

    private final long[] samples = new long[CHUNK_COUNT];

    private int stage = 0;
    private int warmupCursor = 0;
    private int cursor = 0;
    private int windowTicks = 0;
    private int maxTickAt = 0;
    private int nullChunks = 0;
    private long startNanos = 0L;
    private long previousNanos = 0L;
    private long maxTickNanos = 0L;
    private long totalGenNanos = 0L;
    private long loadNanos = -1L;
    private boolean timedOut = false;

    public ChunkLoadBenchTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
        this.baseCx = Integer.getInteger("hbm.chunk.cx", 48200);
        this.baseCz = Integer.getInteger("hbm.chunk.cz", 48200);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) return warmup();
        if (stage == 1) return load();
        report();
        return true;
    }

    private boolean warmup() {
        if (warmupCursor < WARMUP) {
            level.getChunk(baseCx, baseCz + WARMUP_OFFSET + warmupCursor);
            warmupCursor++;
            return false;
        }
        progress("arrival cx=" + baseCx + " cz=" + baseCz + " radius=" + RADIUS);
        startNanos = System.nanoTime();
        previousNanos = startNanos;
        stage = 1;
        return false;
    }

    private boolean load() {
        long now = System.nanoTime();
        long delta = now - previousNanos;
        previousNanos = now;

        windowTicks++;
        if (delta > maxTickNanos) {
            maxTickNanos = delta;
            maxTickAt = windowTicks;
        }

        long budget = Long.getLong("hbm.chunk.budget_ms", DELIVERY_BUDGET_MS) * 1_000_000L;
        long tickStart = System.nanoTime();
        do {
            int dx = cursor % SIDE - RADIUS;
            int dz = cursor / SIDE - RADIUS;
            long start = System.nanoTime();
            LevelChunk chunk = level.getChunk(baseCx + dx, baseCz + dz);
            samples[cursor] = System.nanoTime() - start;
            totalGenNanos += samples[cursor];
            if (chunk == null) nullChunks++;
            cursor++;
        } while (cursor < CHUNK_COUNT && System.nanoTime() - tickStart < budget);

        if (cursor >= CHUNK_COUNT) {
            loadNanos = System.nanoTime() - startNanos;
            progress("loaded_all ticks=" + windowTicks);
            stage = 2;
            return false;
        }
        if (windowTicks >= TIMEOUT_TICKS) {
            timedOut = true;
            progress("timeout ticks=" + windowTicks + " loaded=" + cursor);
            stage = 2;
            return false;
        }
        return false;
    }

    private void report() {
        long[] sorted = samples.clone();
        Arrays.sort(sorted, 0, cursor);
        int count = Math.max(1, cursor);

        double loadMs = loadNanos < 0 ? -1.0 : loadNanos / 1_000_000.0;
        double maxTickMs = maxTickNanos / 1_000_000.0;

        metric("region", baseCx + "," + baseCz);
        metric("radius", RADIUS);
        metric("chunks", CHUNK_COUNT);
        metric("loaded", cursor);
        metric("load_ms", round(loadMs));
        metric("max_tick_ms", round(maxTickMs));
        metric("max_tick_at", maxTickAt);
        metric("gen_ms", round(totalGenNanos / 1_000_000.0));
        metric("chunk_p50_ms", round(sorted[count / 2] / 1_000_000.0));
        metric("chunk_p95_ms", round(sorted[(int) Math.min(count - 1, Math.round(count * 0.95))] / 1_000_000.0));
        metric("chunk_max_ms", round(sorted[count - 1] / 1_000_000.0));
        metric("ticks", windowTicks);
        metric("null_chunks", nullChunks);
        metric("timed_out", timedOut ? 1 : 0);

        boolean ok = true;
        ok &= expect("all_chunks_loaded", cursor >= CHUNK_COUNT && nullChunks == 0);
        ok &= expect("load_within_12s", loadMs >= 0 && loadMs <= LOAD_BUDGET_MS);
        ok &= expect("tick_lag_within_1s", maxTickMs <= TICK_BUDGET_MS);

        finish(ok);
    }

    private static double round(double value) {
        return Double.parseDouble(String.format(Locale.ROOT, "%.2f", value));
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }
}
