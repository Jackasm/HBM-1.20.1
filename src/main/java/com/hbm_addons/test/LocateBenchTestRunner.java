package com.hbm_addons.test;

import com.hbm.world.generator.WorldgenStats;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Замер пути {@code /locate structure} (#157): время команды и число вызовов высоты рельефа.
 *
 * <p>Команда запускается тем же путём, что и с консоли сервера, — это тот случай,
 * на котором живой стенд ушёл на 43,6 с и 873 тика назад. Радиус команды —
 * по умолчанию, 100 чанков.</p>
 */
public class LocateBenchTestRunner extends TestRunner {

    /** Первая цель — прогрев JVM, её число не показатель; остальные — замер. */
    private static final String[] TARGETS = {"hbm:factory", "hbm:lighthouse", "hbm:military_base"};

    private final Map<String, Long> measuredMs = new LinkedHashMap<>();
    private final Map<String, Long> measuredHeights = new LinkedHashMap<>();
    private int index = 0;
    private boolean ok = true;

    public LocateBenchTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (index >= TARGETS.length) {
            report();
            finish(ok);
            return true;
        }

        String target = TARGETS[index];
        boolean warmup = index == 0;
        index++;

        CommandSourceStack source = server.createCommandSourceStack();
        WorldgenStats.enable();
        WorldgenStats.reset();
        long start = System.nanoTime();
        int result = server.getCommands().performPrefixedCommand(source, "locate structure " + target);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000L;
        long heights = WorldgenStats.calls(WorldgenStats.BASE_HEIGHT);
        long finds = WorldgenStats.findInvocations();
        WorldgenStats.disable();

        String key = target.substring(target.indexOf(':') + 1);
        info("warmup_" + key, warmup);
        info("result_" + key, result);
        if (!warmup) {
            measuredMs.put(key, elapsedMs);
            measuredHeights.put(key, heights);
            metric("ms_" + key, elapsedMs);
            metric("heights_" + key, heights);
            metric("finds_" + key, finds);
        }
        return false;
    }

    /**
     * Стражи от регресса поиска (#157): считаем вызовы {@code getBaseHeight},
     * а не время — они не зависят от железа и загрузки машины.
     *
     * <p>Замер после отсева по биому: 605 и 12 181. До отсева было 5 453 и 143 393.
     * Пороги взяты посередине с запасом на другой мир: полный откат оптимизации
     * их пробивает, а колебания по сиду — нет.</p>
     */
    private void report() {
        for (Map.Entry<String, Long> e : measuredMs.entrySet()) {
            info("time_" + e.getKey() + "_ms", e.getValue());
        }
        for (Map.Entry<String, Long> e : measuredHeights.entrySet()) {
            info("height_calls_" + e.getKey(), e.getValue());
        }
        Long lighthouse = measuredHeights.get("lighthouse");
        Long military = measuredHeights.get("military_base");
        boolean lighthouseOk = lighthouse != null && lighthouse <= 2000;
        boolean militaryOk = military != null && military <= 20000;
        check("lighthouse_height_budget", lighthouseOk);
        check("military_base_height_budget", militaryOk);
        ok = lighthouseOk && militaryOk;
    }
}
