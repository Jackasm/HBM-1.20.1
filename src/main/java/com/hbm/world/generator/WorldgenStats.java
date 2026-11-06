package com.hbm.world.generator;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Счётчики генерации мира для стенда {@code worldgen_bench} (#134).
 *
 * <p>По умолчанию выключены: пока стенд не позвал {@link #enable()}, каждая
 * точка замера — это {@code if (!enabled) return;}, без аллокаций и без
 * {@code nanoTime}. Включает и сбрасывает их только стенд.</p>
 *
 * <p>Счётчик высот отдельный от прочих: именно {@code getBaseHeight} стоил
 * 1,5 мс на колонку и вытягивал генерацию чанка в секунды, поэтому стенд
 * проверяет не только время, но и число вызовов на один поиск структуры.</p>
 */
public final class WorldgenStats {

    /** Имя счётчика высот рельефа: его число ограничивает критерий #134. */
    public static final String BASE_HEIGHT = "getBaseHeight";

    private static volatile boolean enabled = false;

    private static final ConcurrentHashMap<String, LongAdder> CALLS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, LongAdder> NANOS = new ConcurrentHashMap<>();

    private static final Object FIND_LOCK = new Object();
    private static long maxHeightCallsPerFind = 0L;
    private static String maxHeightCallsTemplate = "";
    private static long findInvocations = 0L;
    private static final Map<String, Long> maxHeightCallsByTemplate = new TreeMap<>();

    private WorldgenStats() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    /** Обнуляет накопленное; включённость не трогает. */
    public static void reset() {
        CALLS.clear();
        NANOS.clear();
        synchronized (FIND_LOCK) {
            maxHeightCallsPerFind = 0L;
            maxHeightCallsTemplate = "";
            findInvocations = 0L;
            maxHeightCallsByTemplate.clear();
        }
    }

    /** Один вызов с замером: +1 к числу и +{@code nanosTaken} ко времени. */
    public static void add(String name, long nanosTaken) {
        if (!enabled) return;
        CALLS.computeIfAbsent(name, key -> new LongAdder()).increment();
        NANOS.computeIfAbsent(name, key -> new LongAdder()).add(nanosTaken);
    }

    /** Один вызов без замера времени — когда интересно только число. */
    public static void count(String name) {
        if (!enabled) return;
        CALLS.computeIfAbsent(name, key -> new LongAdder()).increment();
    }

    public static long calls(String name) {
        LongAdder adder = CALLS.get(name);
        return adder == null ? 0L : adder.sum();
    }

    public static long nanos(String name) {
        LongAdder adder = NANOS.get(name);
        return adder == null ? 0L : adder.sum();
    }

    /**
     * Запоминает один поиск структуры: сколько он стоил вызовов высоты.
     * Максимум по всем поискам и максимум по каждому шаблону — это и есть
     * проверка «стоимость не зависит от размера шаблона».
     */
    public static void recordFind(String template, long heightCalls) {
        if (!enabled) return;
        synchronized (FIND_LOCK) {
            findInvocations++;
            if (heightCalls > maxHeightCallsPerFind) {
                maxHeightCallsPerFind = heightCalls;
                maxHeightCallsTemplate = template;
            }
            maxHeightCallsByTemplate.merge(template, heightCalls, Math::max);
        }
    }

    public static long findInvocations() {
        synchronized (FIND_LOCK) {
            return findInvocations;
        }
    }

    public static long maxHeightCallsPerFind() {
        synchronized (FIND_LOCK) {
            return maxHeightCallsPerFind;
        }
    }

    public static String maxHeightCallsTemplate() {
        synchronized (FIND_LOCK) {
            return maxHeightCallsTemplate;
        }
    }

    public static Map<String, Long> maxHeightCallsByTemplate() {
        synchronized (FIND_LOCK) {
            return new TreeMap<>(maxHeightCallsByTemplate);
        }
    }

    public static Map<String, Long> callsSnapshot() {
        Map<String, Long> snapshot = new TreeMap<>();
        CALLS.forEach((name, adder) -> snapshot.put(name, adder.sum()));
        return snapshot;
    }

    public static Map<String, Long> nanosSnapshot() {
        Map<String, Long> snapshot = new TreeMap<>();
        NANOS.forEach((name, adder) -> snapshot.put(name, adder.sum()));
        return snapshot;
    }
}
