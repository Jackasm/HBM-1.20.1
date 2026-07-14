package com.hbm.datagen.worldgen;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHelper {

    public static int getSafeInt(ForgeConfigSpec.IntValue configValue, int defaultValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }

    public static boolean getSafeBool(ForgeConfigSpec.BooleanValue configValue, boolean defaultValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }
}