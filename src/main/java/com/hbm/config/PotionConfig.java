package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PotionConfig {


    public static ForgeConfigSpec.EnumValue<PotionSicknessMode> potionSicknessMode;

    public static PotionSicknessMode POTION_SICKNESS_MODE = PotionSicknessMode.OFF;

    // Режимы тошноты от зелий
    public enum PotionSicknessMode {
        OFF,
        NORMAL,
        TERRARIA
    }

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("HBM Nuclear Tech Mod Potion Configuration")
                .push("potion");

        BUILDER.comment("Potion effect IDs (Note: In 1.20.1+, IDs are managed automatically by Forge registry)")
                .push("ids");



        BUILDER.pop();

        BUILDER.comment("Potion system settings")
                .push("settings");

        potionSicknessMode = BUILDER
                .comment("Potion sickness mode. Valid values: OFF, NORMAL, TERRARIA")
                .defineEnum("potionSicknessMode", PotionSicknessMode.OFF);

        BUILDER.pop();

        BUILDER.pop();

        return BUILDER.build();
    }

    public static void load() {
        // Загружаем значения из конфигурации в статические поля

        POTION_SICKNESS_MODE = potionSicknessMode.get();
    }

    public static int getPotionSickness() {
        return switch (POTION_SICKNESS_MODE) {
            case NORMAL -> 1;
            case TERRARIA -> 2;
            default -> 0;
        };
    }

    // Метод для удобного получения режима тошноты от зелий
    public static boolean isPotionSicknessEnabled() {
        return POTION_SICKNESS_MODE != PotionSicknessMode.OFF;
    }

    public static boolean isTerrariaStylePotionSickness() {
        return POTION_SICKNESS_MODE == PotionSicknessMode.TERRARIA;
    }
}