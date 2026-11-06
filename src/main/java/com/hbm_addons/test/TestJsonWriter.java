package com.hbm_addons.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Минимальный writer JSON-отчёта. Без внешних зависимостей.
 * Пишет в run/test-results/<scenario>.json и run/test-results/<scenario>.log
 */
public class TestJsonWriter {

    private static final String RESULTS_DIR = "test-results";

    public static void write(String scenario, boolean pass, int ticks,
                             Map<String, Object> metrics,
                             List<TestRunner.CheckResult> checks,
                             List<String> logLines) {
        ensureDir();
        writeJson(scenario, pass, ticks, metrics, checks);
        writeLog(scenario, logLines);
    }

    public static void writeFailure(String scenario, String reason, String details) {
        ensureDir();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"scenario\": \"").append(escape(scenario)).append("\",\n");
        sb.append("  \"status\": \"FAIL\",\n");
        sb.append("  \"reason\": \"").append(escape(reason)).append("\",\n");
        sb.append("  \"details\": \"").append(escape(details)).append("\",\n");
        sb.append("  \"durationMs\": 0,\n");
        sb.append("  \"metrics\": {},\n");
        sb.append("  \"checks\": []\n");
        sb.append("}\n");
        writeFile(Paths.get(RESULTS_DIR, scenario + ".json"), sb.toString());
    }

    private static void writeJson(String scenario, boolean pass, int ticks,
                                  Map<String, Object> metrics,
                                  List<TestRunner.CheckResult> checks) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"scenario\": \"").append(escape(scenario)).append("\",\n");
        sb.append("  \"status\": \"").append(pass ? "PASS" : "FAIL").append("\",\n");
        sb.append("  \"ticks\": ").append(ticks).append(",\n");
        sb.append("  \"durationMs\": ").append(ticks * 50).append(",\n");

        sb.append("  \"metrics\": {");
        boolean first = true;
        for (Map.Entry<String, Object> e : metrics.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\n    \"").append(escape(e.getKey())).append("\": ").append(valueToJson(e.getValue()));
            first = false;
        }
        if (!metrics.isEmpty()) sb.append("\n  ");
        sb.append("},\n");

        sb.append("  \"checks\": [");
        first = true;
        for (TestRunner.CheckResult c : checks) {
            if (!first) sb.append(",");
            sb.append("\n    {\"name\": \"").append(escape(c.name))
              .append("\", \"pass\": ").append(c.pass).append("}");
            first = false;
        }
        if (!checks.isEmpty()) sb.append("\n  ");
        sb.append("]\n");

        sb.append("}\n");
        writeFile(Paths.get(RESULTS_DIR, scenario + ".json"), sb.toString());
    }

    private static void writeLog(String scenario, List<String> logLines) {
        StringBuilder sb = new StringBuilder();
        for (String line : logLines) sb.append(line).append("\n");
        writeFile(Paths.get(RESULTS_DIR, scenario + ".log"), sb.toString());
    }

    private static void ensureDir() {
        try {
            Path abs = Paths.get(RESULTS_DIR).toAbsolutePath();
            System.out.println("HBM_TEST_INFO: results_dir=" + abs);
            Files.createDirectories(abs);
        } catch (IOException e) {
            System.err.println("HBM_TEST_INFO: failed_to_create_results_dir=" + e.getMessage());
        }
    }

    private static void writeFile(Path path, String content) {
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("HBM_TEST_INFO: failed_to_write_" + path + "=" + e.getMessage());
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String valueToJson(Object value) {
        if (value == null) return "null";
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        return "\"" + escape(value.toString()) + "\"";
    }
}
