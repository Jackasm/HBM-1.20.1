package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    // Конфигурационные значения
    public static ForgeConfigSpec.BooleanValue damageCompatibilityMode;
    public static ForgeConfigSpec.DoubleValue mineApDamage;
    public static ForgeConfigSpec.DoubleValue mineHeDamage;
    public static ForgeConfigSpec.DoubleValue mineShrapDamage;
    public static ForgeConfigSpec.DoubleValue mineNukeDamage;
    public static ForgeConfigSpec.DoubleValue mineNavalDamage;
    public static ForgeConfigSpec.BooleanValue taintTrails;
    public static ForgeConfigSpec.BooleanValue crateOpenHeld;
    public static ForgeConfigSpec.BooleanValue crateKeepContents;
    public static ForgeConfigSpec.IntValue itemHazardDropTickrate;

    // Публичные переменные для доступа
    public static boolean DAMAGE_COMPATIBILITY_MODE = false;
    public static float MINE_AP_DAMAGE = 10F;
    public static float MINE_HE_DAMAGE = 35F;
    public static float MINE_SHRAP_DAMAGE = 7.5F;
    public static float MINE_NUKE_DAMAGE = 100F;
    public static float MINE_NAVAL_DAMAGE = 60F;
    public static boolean TAINT_TRAILS = true; //по умолчанию false
    public static boolean CRATE_OPEN_HELD = true;
    public static boolean CRATE_KEEP_CONTENTS = true;
    public static int ITEM_HAZARD_DROP_TICKRATE = 2;

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("HBM Nuclear Tech Mod Server Configuration")
                .push("server");

        BUILDER.comment("Damage system settings")
                .push("damage");

        damageCompatibilityMode = BUILDER
                .comment("Use compatibility mode for damage calculation (MK2 SEDNA system)")
                .define("damageCompatibilityMode", false);

        BUILDER.pop();

        BUILDER.comment("Mine damage settings")
                .push("mines");

        mineApDamage = BUILDER
                .comment("AP (Armor Piercing) mine damage")
                .defineInRange("mineApDamage", 10.0, 0.0, 1000.0);

        mineHeDamage = BUILDER
                .comment("HE (High Explosive) mine damage")
                .defineInRange("mineHeDamage", 35.0, 0.0, 1000.0);

        mineShrapDamage = BUILDER
                .comment("Shrapnel mine damage")
                .defineInRange("mineShrapDamage", 7.5, 0.0, 1000.0);

        mineNukeDamage = BUILDER
                .comment("Nuclear mine damage")
                .defineInRange("mineNukeDamage", 100.0, 0.0, 10000.0);

        mineNavalDamage = BUILDER
                .comment("Naval mine damage")
                .defineInRange("mineNavalDamage", 60.0, 0.0, 1000.0);

        BUILDER.pop();

        BUILDER.comment("Miscellaneous settings")
                .push("misc");

        taintTrails = BUILDER
                .comment("Enable taint trails from contaminated entities")
                .define("taintTrails", false);

        crateOpenHeld = BUILDER
                .comment("Allow opening crates while holding them")
                .define("crateOpenHeld", true);

        crateKeepContents = BUILDER
                .comment("Keep crate contents when broken")
                .define("crateKeepContents", true);

        BUILDER.pop();

        BUILDER.comment("Item settings")
                .push("items");

        itemHazardDropTickrate = BUILDER
                .comment("Tick rate for dropping hazardous items (lower = more frequent)")
                .defineInRange("itemHazardDropTickrate", 2, 1, 20);

        BUILDER.pop();

        BUILDER.pop();

        return BUILDER.build();
    }

    public static void load() {
        // Загружаем значения из конфигурации в статические поля
        DAMAGE_COMPATIBILITY_MODE = damageCompatibilityMode.get();
        MINE_AP_DAMAGE = mineApDamage.get().floatValue();
        MINE_HE_DAMAGE = mineHeDamage.get().floatValue();
        MINE_SHRAP_DAMAGE = mineShrapDamage.get().floatValue();
        MINE_NUKE_DAMAGE = mineNukeDamage.get().floatValue();
        MINE_NAVAL_DAMAGE = mineNavalDamage.get().floatValue();
        TAINT_TRAILS = taintTrails.get();
        CRATE_OPEN_HELD = crateOpenHeld.get();
        CRATE_KEEP_CONTENTS = crateKeepContents.get();
        ITEM_HAZARD_DROP_TICKRATE = itemHazardDropTickrate.get();
    }

    // Методы для обратной совместимости со старым кодом
    public static boolean getDamageCompatibilityMode() {
        return DAMAGE_COMPATIBILITY_MODE;
    }

    public static float getMineApDamage() {
        return MINE_AP_DAMAGE;
    }

    public static float getMineHeDamage() {
        return MINE_HE_DAMAGE;
    }

    public static float getMineShrapDamage() {
        return MINE_SHRAP_DAMAGE;
    }

    public static float getMineNukeDamage() {
        return MINE_NUKE_DAMAGE;
    }

    public static float getMineNavalDamage() {
        return MINE_NAVAL_DAMAGE;
    }

    public static boolean getTaintTrails() {
        return TAINT_TRAILS;
    }

    public static boolean getCrateOpenHeld() {
        return CRATE_OPEN_HELD;
    }

    public static boolean getCrateKeepContents() {
        return CRATE_KEEP_CONTENTS;
    }

    public static int getItemHazardDropTickrate() {
        return ITEM_HAZARD_DROP_TICKRATE;
    }
}