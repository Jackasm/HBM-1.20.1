package com.hbm_addons.test;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Базовый класс для всех сценариев тестирования.
 * Вызывается через TestBootstrap каждый серверный тик.
 */
public abstract class TestRunner {

    protected final ServerLevel level;
    protected final String scenarioName;

    protected int tickCount = 0;
    protected final Map<String, Object> metrics = new LinkedHashMap<>();
    protected final List<CheckResult> checks = new ArrayList<>();
    protected final List<String> logLines = new ArrayList<>();

    private Boolean passed = null;

    protected TestRunner(ServerLevel level, String scenarioName) {
        this.level = level;
        this.scenarioName = scenarioName;
    }

    public String getScenarioName() { return scenarioName; }
    public boolean isPassed() { return Boolean.TRUE.equals(passed); }

    /** Вызывается каждый тик. Возвращает true, когда сценарий завершён. */
    public abstract boolean tick(MinecraftServer server);

    protected void finish(boolean pass) {
        this.passed = pass;
        printResult();
        writeResult();
    }

    protected void info(String key, Object value) {
        String line = "HBM_TEST_INFO: " + key + "=" + value;
        System.out.println(line);
        logLines.add(line);
    }

    protected void progress(String step) {
        String line = "HBM_TEST_PROGRESS: " + scenarioName + " " + step;
        System.out.println(line);
        logLines.add(line);
    }

    protected void check(String name, boolean pass) {
        checks.add(new CheckResult(name, pass));
        System.out.println("HBM_TEST_INFO: check_" + name + "=" + (pass ? "PASS" : "FAIL"));
    }

    protected void metric(String key, Object value) {
        metrics.put(key, value);
    }

    private void printResult() {
        StringBuilder sb = new StringBuilder();
        sb.append("HBM_TEST_RESULT: ");
        sb.append(isPassed() ? "PASS" : "FAIL");
        sb.append(" ").append(scenarioName);

        if (!isPassed()) {
            sb.append(" ").append(firstFailedCheck());
        } else {
            for (Map.Entry<String, Object> e : metrics.entrySet()) {
                sb.append(" ").append(e.getKey()).append("=").append(e.getValue());
            }
        }

        String line = sb.toString();
        System.out.println(line);
        logLines.add(line);
    }

    private String firstFailedCheck() {
        for (CheckResult c : checks) {
            if (!c.pass) return "failed_" + c.name;
        }
        return "unknown_failure";
    }

    private void writeResult() {
        TestJsonWriter.write(scenarioName, isPassed(), tickCount, metrics, checks, logLines);
    }

    public static class CheckResult {
        public final String name;
        public final boolean pass;
        public CheckResult(String name, boolean pass) {
            this.name = name;
            this.pass = pass;
        }
    }
}
