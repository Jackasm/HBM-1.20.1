package com.hbm_addons.test;

import com.hbm.datagen.worldgen.structure.NBTStructure;
import com.hbm.datagen.worldgen.structure.pieces.ConformingNBTStructurePiece;
import com.hbm.datagen.worldgen.structure.pieces.NBTStructurePiece;
import com.hbm.world.generator.WorldgenStats;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Замер генерации чанков: свежий регион {@code SIZE×SIZE}, по одному чанку за тик.
 *
 * <p>Проверяет не только время, но и главное утверждение #134: стоимость поиска
 * NBT-структуры не зависит от размера шаблона. Бюджет — 64 вызова
 * {@code getBaseHeight} на один поиск; конформные руины вне бюджета, потому что
 * их массив высот по колонкам — данные постановки (ADR-0015), а не поиска,
 * и он считается и проверяется отдельно.</p>
 *
 * <p>Пороги — из #193: медиана ≤ 40 мс, p95 ≤ 150 мс, максимум ≤ 1500 мс,
 * чанков дольше 500 мс ≤ 2 %, доля поиска структур ≤ 40 % общего времени.</p>
 *
 * <p>Регион задаётся константами и меняется каждый прогон, как у
 * {@code structures_scan}: на уже сгенерированных чанках мерить нечего.
 * Прогрев намеренно лежит в том же регионе-файле, что и замер, — тогда удаление
 * файла региона сбрасывает и прогрев, и замер, и прогоны остаются сравнимыми.</p>
 */
public class WorldgenBenchTestRunner extends TestRunner {

    private static final int SIZE = 12;
    private static final int BASE_CX = 48000;
    private static final int BASE_CZ = 48000;

    /**
     * Прогрев: 8 чанков холодной JVM. Первый прогон после старта в 5–6 раз
     * медленнее, поэтому без прогрева сравнивались бы не фиксы, а разгон.
     * Чанки намеренно в том же регионе-файле, что и замер (cz = BASE_CZ + SIZE).
     */
    private static final int WARMUP = 8;
    private static final int WARMUP_CX = BASE_CX;
    private static final int WARMUP_CZ = BASE_CZ + SIZE;

    /** Бюджет вызовов высоты на один поиск структуры — критерий #134. */
    private static final int HEIGHT_CALLS_BUDGET = 64;

    /** Пороги из #193: за их пределами сервер уже замечает игрок. */
    private static final double P50_BUDGET_MS = 40.0;
    private static final double P95_BUDGET_MS = 150.0;
    private static final double MAX_BUDGET_MS = 1_500.0;
    private static final double OVER_500_BUDGET_PCT = 2.0;
    private static final double FIND_SHARE_BUDGET_PCT = 40.0;

    private int stage = 0;
    private int cursor = 0;
    private int warmupCursor = 0;

    private final long[] samples = new long[SIZE * SIZE];
    private long totalNanos = 0L;
    private int chunksGenerated = 0;

    public WorldgenBenchTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
        // Счётчики включаются только на стенде: в обычной игре они выключены.
        WorldgenStats.enable();
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            if (warmupCursor < WARMUP) {
                level.getChunk(WARMUP_CX + warmupCursor, WARMUP_CZ);
                warmupCursor++;
                return false;
            }
            WorldgenStats.reset();
            progress("measure_start");
            stage = 1;
            return false;
        }

        if (stage == 1) {
            if (cursor < samples.length) {
                int cx = BASE_CX + cursor % SIZE;
                int cz = BASE_CZ + cursor / SIZE;
                long start = System.nanoTime();
                ChunkAccess chunk = level.getChunk(cx, cz);
                long elapsed = System.nanoTime() - start;
                samples[cursor] = elapsed;
                totalNanos += elapsed;
                chunksGenerated++;
                if (chunk == null) info("null_chunk", cx + ":" + cz);
                cursor++;
                return false;
            }
            progress("measure_done");
            stage = 2;
            return false;
        }

        report();
        return true;
    }

    private void report() {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        long p50 = sorted[sorted.length / 2];
        long p95 = sorted[(int) Math.min(sorted.length - 1, Math.round(sorted.length * 0.95))];
        long max = sorted[sorted.length - 1];
        int over500 = 0;
        for (long sample : samples) {
            if (sample > 500_000_000L) over500++;
        }

        Map<String, Long> calls = WorldgenStats.callsSnapshot();
        Map<String, Long> nanos = WorldgenStats.nanosSnapshot();
        long findCalls = calls.getOrDefault("find.NBTStructure", 0L);
        long heightCalls = calls.getOrDefault(WorldgenStats.BASE_HEIGHT, 0L);
        long heightNanos = nanos.getOrDefault(WorldgenStats.BASE_HEIGHT, 0L);
        long findNanos = nanos.getOrDefault("find.NBTStructure", 0L);

        double totalMs = totalNanos / 1_000_000.0;
        double findSharePct = totalMs > 0 ? findNanos / 1_000_000.0 * 100.0 / totalMs : 0.0;
        double over500Pct = over500 * 100.0 / samples.length;

        Map<String, Long> byTemplate = WorldgenStats.maxHeightCallsByTemplate();
        long worstSearch = 0L;
        long worstConform = 0L;
        for (Map.Entry<String, Long> entry : byTemplate.entrySet()) {
            if (isConforming(entry.getKey())) {
                worstConform = Math.max(worstConform, entry.getValue());
            } else {
                worstSearch = Math.max(worstSearch, entry.getValue());
            }
        }

        String structuresAt = snapshotStructures();

        metric("avg_ms", round(totalNanos / 1_000_000.0 / samples.length));
        metric("p50_ms", ms(p50));
        metric("p95_ms", ms(p95));
        metric("max_ms", ms(max));
        metric("over_500ms", over500);
        metric("over_500ms_pct", round(over500Pct));
        metric("find_calls", findCalls);
        metric("find_ms", ms(findNanos));
        metric("find_share_pct", round(findSharePct));
        metric("getbaseheight_calls", heightCalls);
        metric("getbaseheight_ms", ms(heightNanos));
        metric("max_height_calls_per_find", WorldgenStats.maxHeightCallsPerFind());
        metric("max_height_calls_template", WorldgenStats.maxHeightCallsTemplate());
        metric("search_height_calls_worst", worstSearch);
        metric("conform_height_calls_worst", worstConform);
        metric("height_calls_by_template", describeByTemplate(byTemplate));
        metric("structures", structuresAt.isEmpty() ? 0 : structuresAt.split(";").length);
        metric("structures_at", structuresAt);

        info("region", BASE_CX + "," + BASE_CZ);

        boolean ok = true;
        ok &= expect("region_generated", chunksGenerated == SIZE * SIZE);
        ok &= expect("search_ran", findCalls > 0);
        // Пороги #193: время генерации и доля поиска структур.
        ok &= expect("p50_within_40ms", p50 / 1_000_000.0 <= P50_BUDGET_MS);
        ok &= expect("p95_within_150ms", p95 / 1_000_000.0 <= P95_BUDGET_MS);
        ok &= expect("max_within_1500ms", max / 1_000_000.0 <= MAX_BUDGET_MS);
        ok &= expect("over_500ms_within_2pct", over500Pct <= OVER_500_BUDGET_PCT);
        ok &= expect("find_share_within_40pct", findSharePct <= FIND_SHARE_BUDGET_PCT);
        // Критерий #134: поиск не зависит от размера шаблона.
        ok &= expect("search_height_calls_within_budget", worstSearch <= HEIGHT_CALLS_BUDGET);

        finish(ok);
    }

    /**
     * Снимок поставленных NBT-структур: имя и точка. По нему сравниваются
     * прогоны до и после правки на одном регионе — критерий «высота сдвинулась
     * не больше чем на 2 блока». Подземные руды, жилы и шахты в снимок не идут:
     * они не проходят через {@code findGenerationPoint} NBT-структур.
     */
    private String snapshotStructures() {
        Map<String, Integer> found = new TreeMap<>();
        for (int index = 0; index < SIZE * SIZE; index++) {
            int cx = BASE_CX + index % SIZE;
            int cz = BASE_CZ + index / SIZE;
            ChunkAccess chunk = level.getChunk(cx, cz);
            if (chunk == null) continue;
            for (StructureStart start : chunk.getAllStarts().values()) {
                if (!start.isValid()) continue;
                if (!hasNbtPiece(start)) continue;
                var box = start.getBoundingBox();
                String key = structureName(start.getStructure()) + "@" + box.minX() + "," + box.minY() + "," + box.minZ();
                found.merge(key, 1, Integer::sum);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String key : found.keySet()) {
            if (sb.length() > 0) sb.append(';');
            sb.append(key);
        }
        return sb.toString();
    }

    private boolean hasNbtPiece(StructureStart start) {
        return start.getPieces().stream()
                .anyMatch(p -> p instanceof NBTStructurePiece || p instanceof ConformingNBTStructurePiece);
    }

    private String describeByTemplate(Map<String, Long> byTemplate) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Long> entry : byTemplate.entrySet()) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return sb.toString();
    }

    /** Конформные руины ставятся по колонкам: их высоты — данные постановки. */
    private boolean isConforming(String nbtFile) {
        List<NBTStructure> all = new ArrayList<>();
        level.registryAccess().registryOrThrow(Registries.STRUCTURE).forEach(structure -> {
            if (structure instanceof NBTStructure nbt) all.add(nbt);
        });
        for (NBTStructure nbt : all) {
            if (nbt.nbtFile().equals(nbtFile)) return nbt.conformsToTerrain();
        }
        return false;
    }

    private String structureName(Structure structure) {
        ResourceKey<Structure> key = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getResourceKey(structure)
                .orElse(null);
        if (key == null) return "unknown";
        ResourceLocation id = key.location();
        return id.getNamespace() + ":" + id.getPath();
    }

    private static double ms(long nanos) {
        return round(nanos / 1_000_000.0);
    }

    private static double round(double value) {
        return Double.parseDouble(String.format(Locale.ROOT, "%.2f", value));
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }
}
