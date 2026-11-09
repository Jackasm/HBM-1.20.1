package com.hbm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сравнение версий мода без внешних зависимостей.
 *
 * <p>Версия dev-сборки несёт коммит: {@code 0.1.1-dev.a88e7fc}. Для сравнения
 * важна только числовая база {@code major.minor.patch}: суффиксы {@code -dev},
 * {@code -beta} и прочие не делают версию старше или младше — иначе dev-сборка
 * 0.1.1 «старше» релиза 0.1.0 только по строке и проверка обновлений врёт.</p>
 */
public final class VersionUtil {

    private static final Pattern CORE = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?");

    private VersionUtil() {
    }

    /** Убирает ведущий {@code v} у тега релиза, не трогая буквы внутри версии. */
    public static String stripTagPrefix(String tag) {
        return tag != null && tag.startsWith("v") ? tag.substring(1) : tag;
    }

    /**
     * {@code true}, только если {@code latest} старше {@code current} по числовым
     * компонентам. Равные базы (в том числе релиз против dev-сборки той же базы)
     * новее не считаются. Неразобранную версию не считаем новее — лучше промолчать,
     * чем показать ложное обновление.
     */
    public static boolean isNewer(String latest, String current) {
        int[] a = parse(stripTagPrefix(latest));
        int[] b = parse(stripTagPrefix(current));
        if (a == null || b == null) return false;
        for (int i = 0; i < 3; i++) {
            if (a[i] != b[i]) return a[i] > b[i];
        }
        return false;
    }

    private static int[] parse(String version) {
        if (version == null) return null;
        Matcher matcher = CORE.matcher(version.trim());
        if (!matcher.find()) return null;
        int[] parts = new int[3];
        try {
            for (int i = 0; i < 3; i++) {
                String group = matcher.group(i + 1);
                if (group != null) parts[i] = Integer.parseInt(group);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return parts;
    }
}
