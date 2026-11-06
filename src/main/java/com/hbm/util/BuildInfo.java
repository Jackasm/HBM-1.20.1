package com.hbm.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Идентичность сборки (#168): версия, номер сборки, коммит, ветка и время коммита.
 *
 * <p>Значения записывает Gradle в {@code hbm-build.properties} при сборке: они
 * детерминированы для одного коммита, поэтому не ломают воспроизводимость jar.
 * Читается один раз при первом обращении и больше не перечитывается.</p>
 *
 * <p>Зачем: до этого две сборки с одинаковым {@code mod_version} могли отличаться
 * на два мержа, и «какая версия стоит на стенде» выяснялось сравнением sha256.</p>
 */
public final class BuildInfo {

    private static final Properties PROPS = load();

    private BuildInfo() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = BuildInfo.class.getResourceAsStream("/hbm-build.properties")) {
            if (in != null) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception ignored) {
            // Файла нет — работаем как есть, summary() честно покажет unknown.
        }
        return props;
    }

    private static String get(String key, String fallback) {
        String value = PROPS.getProperty(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    /** Версия сборки: {@code 0.0.15-dev.a1b2c3d} у dev, ровно {@code mod_version} у релиза. */
    public static String version() {
        return get("version", "unknown");
    }

    /** Номер сборки: {@code git rev-list --count HEAD}. */
    public static String number() {
        return get("number", "0");
    }

    public static String commit() {
        return get("commit", "unknown");
    }

    public static String branch() {
        return get("branch", "unknown");
    }

    public static String dirty() {
        return get("dirty", "false");
    }

    /** Время коммита, а не сборки: время сборки сломало бы воспроизводимость jar. */
    public static String time() {
        return get("time", "unknown");
    }

    public static boolean isRelease() {
        return Boolean.parseBoolean(get("release", "false"));
    }

    /** Одна строка для лога и команды {@code /hbm version}. */
    public static String summary() {
        return "hbm: build " + version() + " #" + number()
                + " (branch " + branch() + ", commit " + commit()
                + ", dirty=" + dirty() + ", committed " + time() + ")";
    }
}
