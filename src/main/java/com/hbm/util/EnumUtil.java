package com.hbm.util;

import java.util.Arrays;
import java.util.Locale;

public class EnumUtil {

    public static <T extends Enum<T>> T grabEnumSafely(Class<T> theEnum, int index) {
        T[] values = theEnum.getEnumConstants();
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Enum class has no constants");
        }
        index = Math.abs(index % values.length);
        return values[index];
    }

    public static String[] getEnumNames(Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> e.name().toLowerCase(Locale.US))
                .toArray(String[]::new);
    }
}