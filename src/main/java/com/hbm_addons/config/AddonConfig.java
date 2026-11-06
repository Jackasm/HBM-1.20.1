package com.hbm_addons.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Конфиг аддонов порта (hbm-addons.toml).
 *
 * <p>Сверху — общий рубильник, ниже — по секции на каждое дополнение.
 * Чтобы добавить новое дополнение: завести секцию, флаг {@code enabled}
 * и поле в {@link #load()}, которое скрещивает его с рубильником.</p>
 */
public class AddonConfig {

    // --- рубильник ---
    public static ForgeConfigSpec.BooleanValue ENABLE_ADDONS;

    // --- encounters ---
    public static ForgeConfigSpec.BooleanValue ENABLE_ENCOUNTERS;
    public static ForgeConfigSpec.IntValue ENCOUNTER_DELAY;
    public static ForgeConfigSpec.IntValue ENCOUNTER_CHANCE;
    public static ForgeConfigSpec.IntValue ENCOUNTER_MIN_DIST;
    public static ForgeConfigSpec.IntValue ENCOUNTER_MAX_DIST;
    public static ForgeConfigSpec.IntValue ENCOUNTER_GROUP_MIN;
    public static ForgeConfigSpec.IntValue ENCOUNTER_GROUP_MAX;
    public static ForgeConfigSpec.DoubleValue ENCOUNTER_FRIENDLY_BASE_CHANCE;
    public static ForgeConfigSpec.DoubleValue ENCOUNTER_HOSTILE_PER_KILL;
    public static ForgeConfigSpec.IntValue ENCOUNTER_HOSTILE_DECAY_TICKS;
    public static ForgeConfigSpec.BooleanValue ENCOUNTER_DAY_ONLY;
    public static ForgeConfigSpec.BooleanValue ENCOUNTER_SURFACE_ONLY;
    public static ForgeConfigSpec.DoubleValue ENCOUNTER_HOSTILE_MAX;
    public static ForgeConfigSpec.IntValue ENCOUNTER_VIEW_RADIUS;
    public static ForgeConfigSpec.IntValue ENCOUNTER_MAX_GROUPS;
    public static ForgeConfigSpec.IntValue ENCOUNTER_DESPAWN_TICKS;

    // --- quests ---
    public static ForgeConfigSpec.BooleanValue ENABLE_QUESTS;
    public static ForgeConfigSpec.BooleanValue ENABLE_QUEST_OVERLAY;
    public static ForgeConfigSpec.IntValue MAX_ACTIVE_QUESTS;

    // --- публичные значения (заполняются в load()) ---
    /** Общий рубильник. false — аддон не делает вообще ничего. */
    public static boolean enableAddons = true;

    /** Уже с учётом рубильника: false, если выключен мастер или сама секция. */
    public static boolean enableEncounters = true;
    public static boolean enableQuests = true;
    public static boolean enableQuestOverlay = true;

    public static int maxActiveQuests = 10;

    public static int encounterDelay = 60 * 20 * 5;
    public static int encounterChance = 4;
    public static int encounterMinDist = 12;
    public static int encounterMaxDist = 28;
    public static int encounterGroupMin = 1;
    public static int encounterGroupMax = 3;
    public static double encounterFriendlyBaseChance = 0.7D;
    public static double encounterHostilePerKill = 0.08D;
    public static int encounterHostileDecayTicks = 20 * 60 * 10;
    public static boolean encounterDayOnly = false;
    public static boolean encounterSurfaceOnly = true;
    public static double encounterHostileMax = 0.95D;
    public static int encounterViewRadius = 96;
    public static int encounterMaxGroups = 3;
    public static int encounterDespawnTicks = 100;

    /**
     * Дефолт рубильника. Задаётся при сборке: релизная сборка передаёт
     * -Paddons_default_enabled=false, чтобы необкатанные аддоны не уехали игрокам.
     */
    private static boolean defaultEnabled() {
        try (InputStream in = AddonConfig.class.getResourceAsStream("/hbm-addons-defaults.properties")) {
            if (in == null) return true;
            Properties props = new Properties();
            props.load(in);
            return Boolean.parseBoolean(props.getProperty("addons_default_enabled", "true"));
        } catch (IOException e) {
            return true;
        }
    }

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.comment("HBM Port addons").push("addons");

        ENABLE_ADDONS = b.comment("Master switch: false turns off every addon below")
                .define("enableAddons", defaultEnabled());

        b.comment("Random encounters: wandering traders with their own goals and reputation")
                .push("encounters");

        ENABLE_ENCOUNTERS = b.comment("Enable this addon")
                .define("enabled", true);

        ENCOUNTER_DELAY = b.comment("World ticks between encounter checks")
                .defineInRange("encounterDelay", 60 * 20 * 5, 1, Integer.MAX_VALUE);

        ENCOUNTER_CHANCE = b.comment("1:x chance per check to spawn an encounter")
                .defineInRange("encounterChance", 4, 1, Integer.MAX_VALUE);

        ENCOUNTER_MIN_DIST = b.comment("Minimum spawn distance from player")
                .defineInRange("encounterMinDist", 12, 4, 128);

        ENCOUNTER_MAX_DIST = b.comment("Maximum spawn distance from player")
                .defineInRange("encounterMaxDist", 28, 4, 256);

        ENCOUNTER_GROUP_MIN = b.comment("Minimum group size")
                .defineInRange("encounterGroupMin", 1, 1, 16);

        ENCOUNTER_GROUP_MAX = b.comment("Maximum group size")
                .defineInRange("encounterGroupMax", 3, 1, 16);

        ENCOUNTER_FRIENDLY_BASE_CHANCE = b.comment("Base chance for friendly (0.0-1.0)")
                .defineInRange("encounterFriendlyBaseChance", 0.7D, 0.0D, 1.0D);

        ENCOUNTER_HOSTILE_PER_KILL = b.comment("Hostile chance added per friendly kill")
                .defineInRange("encounterHostilePerKill", 0.08D, 0.0D, 1.0D);

        ENCOUNTER_HOSTILE_DECAY_TICKS = b.comment("Ticks before one kill is forgiven")
                .defineInRange("encounterHostileDecayTicks", 20 * 60 * 10, 1, Integer.MAX_VALUE);

        ENCOUNTER_DAY_ONLY = b.comment("Only spawn during day")
                .define("encounterDayOnly", false);

        ENCOUNTER_SURFACE_ONLY = b.comment("Only spawn on surface")
                .define("encounterSurfaceOnly", true);

        ENCOUNTER_HOSTILE_MAX = b.comment("Upper cap for hostile chance")
                .defineInRange("encounterHostileMax", 0.95D, 0.0D, 1.0D);

        ENCOUNTER_VIEW_RADIUS = b.comment("View radius used for spawn/despawn (blocks)")
                .defineInRange("encounterViewRadius", 96, 32, 256);

        ENCOUNTER_MAX_GROUPS = b.comment("Max concurrent encounter groups per level")
                .defineInRange("encounterMaxGroups", 3, 1, 16);

        ENCOUNTER_DESPAWN_TICKS = b.comment("Ticks out of all players' view before despawn")
                .defineInRange("encounterDespawnTicks", 100, 20, 1200);

        b.pop();

        b.comment("Quests: the quest book, HUD and the guide-book integration")
                .push("quests");

        ENABLE_QUESTS = b.comment("Enable this addon")
                .define("enabled", true);

        ENABLE_QUEST_OVERLAY = b.comment("Show the quest HUD")
                .define("enableOverlay", true);

        MAX_ACTIVE_QUESTS = b.comment("Maximum number of active quests a player can have")
                .defineInRange("maxActiveQuests", 10, 1, 50);

        b.pop();

        b.pop();

        return b.build();
    }

    public static void load() {
        enableAddons = ENABLE_ADDONS.get();

        enableEncounters = enableAddons && ENABLE_ENCOUNTERS.get();
        enableQuests = enableAddons && ENABLE_QUESTS.get();
        enableQuestOverlay = enableQuests && ENABLE_QUEST_OVERLAY.get();

        maxActiveQuests = MAX_ACTIVE_QUESTS.get();

        encounterDelay = ENCOUNTER_DELAY.get();
        encounterChance = ENCOUNTER_CHANCE.get();
        encounterMinDist = ENCOUNTER_MIN_DIST.get();
        encounterMaxDist = ENCOUNTER_MAX_DIST.get();
        encounterGroupMin = ENCOUNTER_GROUP_MIN.get();
        encounterGroupMax = ENCOUNTER_GROUP_MAX.get();
        encounterFriendlyBaseChance = ENCOUNTER_FRIENDLY_BASE_CHANCE.get();
        encounterHostilePerKill = ENCOUNTER_HOSTILE_PER_KILL.get();
        encounterHostileDecayTicks = ENCOUNTER_HOSTILE_DECAY_TICKS.get();
        encounterDayOnly = ENCOUNTER_DAY_ONLY.get();
        encounterSurfaceOnly = ENCOUNTER_SURFACE_ONLY.get();
        encounterHostileMax = ENCOUNTER_HOSTILE_MAX.get();
        encounterViewRadius = ENCOUNTER_VIEW_RADIUS.get();
        encounterMaxGroups = ENCOUNTER_MAX_GROUPS.get();
        encounterDespawnTicks = ENCOUNTER_DESPAWN_TICKS.get();
    }
}
