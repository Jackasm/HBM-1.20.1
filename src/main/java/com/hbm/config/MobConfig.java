package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MobConfig {

    // Конфигурационные значения
    public static ForgeConfigSpec.BooleanValue ENABLE_MASKMAN;
    public static ForgeConfigSpec.IntValue MASKMAN_DELAY;
    public static ForgeConfigSpec.IntValue MASKMAN_CHANCE;
    public static ForgeConfigSpec.IntValue MASKMAN_MIN_RAD;
    public static ForgeConfigSpec.BooleanValue MASKMAN_UNDERGROUND;

    public static ForgeConfigSpec.BooleanValue ENABLE_RAIDS;
    public static ForgeConfigSpec.IntValue RAID_DELAY;
    public static ForgeConfigSpec.IntValue RAID_CHANCE;
    public static ForgeConfigSpec.IntValue RAID_AMOUNT;
    public static ForgeConfigSpec.IntValue RAID_DRONES;
    public static ForgeConfigSpec.IntValue RAID_ATTACK_DELAY;
    public static ForgeConfigSpec.IntValue RAID_ATTACK_REACH;
    public static ForgeConfigSpec.IntValue RAID_ATTACK_DISTANCE;

    public static ForgeConfigSpec.BooleanValue ENABLE_ELEMENTALS;
    public static ForgeConfigSpec.IntValue ELEMENTAL_DELAY;
    public static ForgeConfigSpec.IntValue ELEMENTAL_CHANCE;
    public static ForgeConfigSpec.IntValue ELEMENTAL_AMOUNT;
    public static ForgeConfigSpec.IntValue ELEMENTAL_DISTANCE;

    public static ForgeConfigSpec.BooleanValue ENABLE_DUCKS;
    public static ForgeConfigSpec.BooleanValue ENABLE_MOB_GEAR;
    public static ForgeConfigSpec.BooleanValue ENABLE_MOB_WEAPONS;

    public static ForgeConfigSpec.BooleanValue ENABLE_HIVES;
    public static ForgeConfigSpec.IntValue HIVE_SPAWN;
    public static ForgeConfigSpec.DoubleValue SCOUT_THRESHOLD;
    public static ForgeConfigSpec.IntValue SCOUT_SWARM_SPAWN_CHANCE;
    public static ForgeConfigSpec.BooleanValue WAYPOINT_DEBUG;
    public static ForgeConfigSpec.IntValue LARGE_HIVE_CHANCE;
    public static ForgeConfigSpec.IntValue LARGE_HIVE_THRESHOLD;

    public static ForgeConfigSpec.IntValue SWARM_COOLDOWN;
    public static ForgeConfigSpec.IntValue BASE_SWARM_SIZE;
    public static ForgeConfigSpec.DoubleValue SWARM_SCALING_MULT;
    public static ForgeConfigSpec.IntValue SOOT_STEP;

    public static ForgeConfigSpec.DoubleValue SPAWN_MAX;
    public static ForgeConfigSpec.BooleanValue ENABLE_INFESTATION;
    public static ForgeConfigSpec.DoubleValue BASE_INFEST_CHANCE;
    public static ForgeConfigSpec.DoubleValue TARGETING_THRESHOLD;

    public static ForgeConfigSpec.BooleanValue RAMPANT_MODE;
    public static ForgeConfigSpec.BooleanValue RAMPANT_NATURAL_SCOUT_SPAWN;
    public static ForgeConfigSpec.DoubleValue RAMPANT_SCOUT_SPAWN_THRESH;
    public static ForgeConfigSpec.IntValue RAMPANT_SCOUT_SPAWN_CHANCE;
    public static ForgeConfigSpec.BooleanValue SCOUT_INITIAL_SPAWN;
    public static ForgeConfigSpec.BooleanValue RAMPANT_EXTENDED_TARGETING;
    public static ForgeConfigSpec.BooleanValue RAMPANT_DIG;
    public static ForgeConfigSpec.BooleanValue RAMPANT_GLYPHID_GUIDANCE;
    public static ForgeConfigSpec.DoubleValue RAMPANT_SMOKE_STACK_OVERRIDE;
    public static ForgeConfigSpec.DoubleValue POLLUTION_MULT;

    // Массивы для шансов глифидов (храним как строки для конфига)
    public static ForgeConfigSpec.ConfigValue<String> GLYPHID_CHANCE;
    public static ForgeConfigSpec.ConfigValue<String> BRAWLER_CHANCE;
    public static ForgeConfigSpec.ConfigValue<String> BOMBARDIER_CHANCE;
    public static ForgeConfigSpec.ConfigValue<String> BLASTER_CHANCE;
    public static ForgeConfigSpec.ConfigValue<String> DIGGER_CHANCE;
    public static ForgeConfigSpec.ConfigValue<String> BEHEMOTH_CHANCE;
    public static ForgeConfigSpec.ConfigValue<String> BRENDA_CHANCE;
    public static ForgeConfigSpec.ConfigValue<String> JOHNSON_CHANCE;

    // Публичные переменные для доступа (с значениями по умолчанию)
    public static boolean enableMaskman = true;
    public static int maskmanDelay = 60 * 60 * 60;
    public static int maskmanChance = 3;
    public static int maskmanMinRad = 50;
    public static boolean maskmanUnderground = true;

    public static boolean enableRaids = false;
    public static int raidDelay = 30 * 60 * 60;
    public static int raidChance = 3;
    public static int raidAmount = 15;
    public static int raidDrones = 5;
    public static int raidAttackDelay = 40;
    public static int raidAttackReach = 2;
    public static int raidAttackDistance = 32;

    public static boolean enableElementals = true;
    public static int elementalDelay = 30 * 60 * 60;
    public static int elementalChance = 2;
    public static int elementalAmount = 10;
    public static int elementalDistance = 32;

    public static boolean enableDucks = true;
    public static boolean enableMobGear = true;
    public static boolean enableMobWeapons = true;

    public static boolean enableHives = true;
    public static int hiveSpawn = 256;
    public static double scoutThreshold = 5;
    public static int scoutSwarmSpawnChance = 2;
    public static boolean waypointDebug = false;
    public static int largeHiveChance = 5;
    public static int largeHiveThreshold = 30;

    public static int swarmCooldown = 120 * 20;
    public static int baseSwarmSize = 5;
    public static double swarmScalingMult = 1.2;
    public static int sootStep = 50;

    public static int[] glyphidChance = {50, -45, 0};
    public static int[] brawlerChance = {10, 30, 1};
    public static int[] bombardierChance = {20, -15, 1};
    public static int[] blasterChance = {-5, 40, 5};
    public static int[] diggerChance = {-15, 25, 5};
    public static int[] behemothChance = {-30, 45, 10};
    public static int[] brendaChance = {-50, 60, 20};
    public static int[] johnsonChance = {-50, 60, 50};

    public static double spawnMax = 50;
    public static boolean enableInfestation = true;
    public static double baseInfestChance = 5;
    public static double targetingThreshold = 1;

    public static boolean rampantMode = false;
    public static boolean rampantNaturalScoutSpawn = false;
    public static double rampantScoutSpawnThresh = 14;
    public static int rampantScoutSpawnChance = 1400;
    public static boolean scoutInitialSpawn = false;
    public static boolean rampantExtendedTargeting = false;
    public static boolean rampantDig = false;
    public static boolean rampantGlyphidGuidance = false;
    public static double rampantSmokeStackOverride = 0.4;
    public static double pollutionMult = 3;

    private static final int MINUTE = 60 * 20;
    private static final int HOUR = 60 * MINUTE;

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("Mask Man settings")
                .push("maskman");

        ENABLE_MASKMAN = BUILDER
                .comment("Whether mask man should spawn")
                .define("enableMaskman", true);

        MASKMAN_DELAY = BUILDER
                .comment("How many world ticks need to pass for a check to be performed")
                .defineInRange("maskmanDelay", 60 * 60 * 60, 1, Integer.MAX_VALUE);

        MASKMAN_CHANCE = BUILDER
                .comment("1:x chance to spawn mask man, must be at least 1")
                .defineInRange("maskmanChance", 3, 1, Integer.MAX_VALUE);

        MASKMAN_MIN_RAD = BUILDER
                .comment("The amount of radiation needed for mask man to spawn")
                .defineInRange("maskmanMinRad", 50, 0, Integer.MAX_VALUE);

        MASKMAN_UNDERGROUND = BUILDER
                .comment("Whether players need to be underground for mask man to spawn")
                .define("maskmanUnderground", true);

        BUILDER.pop();

        BUILDER.comment("FBI Raid settings")
                .push("raids");

        ENABLE_RAIDS = BUILDER
                .comment("Whether there should be FBI raids")
                .define("enableRaids", false);

        RAID_DELAY = BUILDER
                .comment("How many world ticks need to pass for a check to be performed")
                .defineInRange("raidDelay", 30 * 60 * 60, 1, Integer.MAX_VALUE);

        RAID_CHANCE = BUILDER
                .comment("1:x chance to spawn a raid, must be at least 1")
                .defineInRange("raidChance", 3, 1, Integer.MAX_VALUE);

        RAID_AMOUNT = BUILDER
                .comment("How many FBI agents are spawned each raid")
                .defineInRange("raidAmount", 15, 1, Integer.MAX_VALUE);

        RAID_DRONES = BUILDER
                .comment("How many quadcopter drones are spawned each raid")
                .defineInRange("raidDrones", 5, 0, Integer.MAX_VALUE);

        RAID_ATTACK_DELAY = BUILDER
                .comment("Time between individual attempts to break machines")
                .defineInRange("raidAttackDelay", 40, 1, Integer.MAX_VALUE);

        RAID_ATTACK_REACH = BUILDER
                .comment("How far away machines can be broken")
                .defineInRange("raidAttackReach", 2, 1, Integer.MAX_VALUE);

        RAID_ATTACK_DISTANCE = BUILDER
                .comment("How far away agents will spawn from the targeted player")
                .defineInRange("raidAttackDistance", 32, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("Meltdown Elementals settings")
                .push("elementals");

        ENABLE_ELEMENTALS = BUILDER
                .comment("Whether there should be radiation elementals")
                .define("enableElementals", true);

        ELEMENTAL_DELAY = BUILDER
                .comment("How many world ticks need to pass for a check to be performed")
                .defineInRange("elementalDelay", 30 * 60 * 60, 1, Integer.MAX_VALUE);

        ELEMENTAL_CHANCE = BUILDER
                .comment("1:x chance to spawn elementals, must be at least 1")
                .defineInRange("elementalChance", 2, 1, Integer.MAX_VALUE);

        ELEMENTAL_AMOUNT = BUILDER
                .comment("How many elementals are spawned each raid")
                .defineInRange("elementalAmount", 10, 1, Integer.MAX_VALUE);

        ELEMENTAL_DISTANCE = BUILDER
                .comment("How far away elementals will spawn from the targeted player")
                .defineInRange("elementalDistance", 32, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("General mob settings")
                .push("general");

        ENABLE_DUCKS = BUILDER
                .comment("Whether pressing O should allow the player to duck")
                .define("enableDucks", true);

        ENABLE_MOB_GEAR = BUILDER
                .comment("Whether zombies and skeletons should have additional gear when spawning")
                .define("enableMobGear", true);

        ENABLE_MOB_WEAPONS = BUILDER
                .comment("Whether skeletons should have bows replaced with guns when spawning at higher soot levels")
                .define("enableMobWeapons", true);

        BUILDER.pop();

        BUILDER.comment("Glyphid Hive settings")
                .push("hives");

        ENABLE_HIVES = BUILDER
                .comment("Whether glyphid hives should spawn")
                .define("enableHives", true);

        HIVE_SPAWN = BUILDER
                .comment("The average amount of chunks per hive")
                .defineInRange("hiveSpawn", 256, 1, Integer.MAX_VALUE);

        SCOUT_THRESHOLD = BUILDER
                .comment("Minimum amount of soot for scouts to spawn")
                .defineInRange("scoutThreshold", 1.0, 0.0, Double.MAX_VALUE);

        SPAWN_MAX = BUILDER
                .comment("Maximum amount of glyphids being able to exist at once through natural spawning")
                .defineInRange("spawnMax", 50.0, 1.0, Double.MAX_VALUE);

        TARGETING_THRESHOLD = BUILDER
                .comment("Minimum amount of soot required for glyphids' extended targeting range to activate")
                .defineInRange("targetingThreshold", 1.0, 0.0, Double.MAX_VALUE);

        SCOUT_SWARM_SPAWN_CHANCE = BUILDER
                .comment("How likely are scouts to spawn in swarms, 1 in x chance format")
                .defineInRange("scoutSwarmSpawnChance", 2, 1, Integer.MAX_VALUE);

        LARGE_HIVE_CHANCE = BUILDER
                .comment("The chance for a large hive to spawn, formula: 1/x")
                .defineInRange("largeHiveChance", 5, 1, Integer.MAX_VALUE);

        LARGE_HIVE_THRESHOLD = BUILDER
                .comment("The soot threshold for a large hive to spawn")
                .defineInRange("largeHiveThreshold", 20, 0, Integer.MAX_VALUE);

        WAYPOINT_DEBUG = BUILDER
                .comment("Allows glyphid waypoints to be seen, mainly used for debugging, also useful as an aid against them")
                .define("waypointDebug", false);

        BUILDER.pop();

        BUILDER.comment("Glyphid swarm settings")
                .push("swarms");

        SWARM_COOLDOWN = BUILDER
                .comment("How often do glyphid swarms spawn, in ticks (20 ticks = 1 second)")
                .defineInRange("swarmCooldown", 120 * 20, 1, Integer.MAX_VALUE);

        BASE_SWARM_SIZE = BUILDER
                .comment("The basic, soot-less swarm size")
                .defineInRange("baseSwarmSize", 5, 1, Integer.MAX_VALUE);

        SWARM_SCALING_MULT = BUILDER
                .comment("By how much should swarm size scale by per soot amount determined below")
                .defineInRange("swarmScalingMult", 1.2, 0.0, Double.MAX_VALUE);

        SOOT_STEP = BUILDER
                .comment("The soot amount the above multiplier applies to the swarm size")
                .defineInRange("sootStep", 50, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("Infested structures settings")
                .push("infestation");

        ENABLE_INFESTATION = BUILDER
                .comment("Whether structures infested with glyphids should spawn")
                .define("enableInfestation", true);

        BASE_INFEST_CHANCE = BUILDER
                .comment("The chance for infested structures to spawn")
                .defineInRange("baseInfestChance", 5.0, 0.0, Double.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("Rampant Mode settings")
                .push("rampant");

        RAMPANT_MODE = BUILDER
                .comment("The main rampant mode toggle, enables all other features associated with it")
                .define("rampantMode", false);

        RAMPANT_NATURAL_SCOUT_SPAWN = BUILDER
                .comment("Whether scouts should spawn naturally in highly polluted chunks")
                .define("rampantNaturalScoutSpawn", false);

        RAMPANT_SCOUT_SPAWN_THRESH = BUILDER
                .comment("How much soot is needed for scouts to naturally spawn")
                .defineInRange("rampantScoutSpawnThresh", 13.0, 0.0, Double.MAX_VALUE);

        RAMPANT_SCOUT_SPAWN_CHANCE = BUILDER
                .comment("How often scouts naturally spawn per mob population, 1/x format")
                .defineInRange("rampantScoutSpawnChance", 1400, 1, Integer.MAX_VALUE);

        RAMPANT_EXTENDED_TARGETING = BUILDER
                .comment("Whether Glyphids should have the extended targeting always enabled")
                .define("rampantExtendedTargeting", false);

        RAMPANT_DIG = BUILDER
                .comment("Whether Glyphids should be able to dig to waypoints")
                .define("rampantDig", false);

        RAMPANT_GLYPHID_GUIDANCE = BUILDER
                .comment("Whether Glyphids should always expand toward a player's spawnpoint")
                .define("rampantGlyphidGuidance", false);

        RAMPANT_SMOKE_STACK_OVERRIDE = BUILDER
                .comment("How much should the smokestack multiply soot by when on rampant mode")
                .defineInRange("rampantSmokeStackOverride", 0.4, 0.0, 1.0);

        POLLUTION_MULT = BUILDER
                .comment("A multiplier for soot emitted, whether you want to increase or decrease it")
                .defineInRange("pollutionMult", 3.0, 0.0, Double.MAX_VALUE);

        SCOUT_INITIAL_SPAWN = BUILDER
                .comment("Whether glyphid scouts should be able to spawn on the first swarm of a hive")
                .define("scoutInitialSpawn", false);

        BUILDER.pop();

        BUILDER.comment("Glyphid spawn chances")
                .push("glyphid_chances");

        GLYPHID_CHANCE = BUILDER
                .comment("Base Spawn chance and soot modifier for a glyphid grunt (base,modifier,minSoot)")
                .define("glyphidChance", "50,-45,0");

        BRAWLER_CHANCE = BUILDER
                .comment("Base Spawn chance and soot modifier for a glyphid brawler (base,modifier,minSoot)")
                .define("brawlerChance", "10,30,1");

        BOMBARDIER_CHANCE = BUILDER
                .comment("Base Spawn chance and soot modifier for a glyphid bombardier (base,modifier,minSoot)")
                .define("bombardierChance", "20,-15,1");

        BLASTER_CHANCE = BUILDER
                .comment("Base Spawn chance and soot modifier for a glyphid blaster (base,modifier,minSoot)")
                .define("blasterChance", "-5,40,5");

        DIGGER_CHANCE = BUILDER
                .comment("Base Spawn chance and soot modifier for a glyphid digger (base,modifier,minSoot)")
                .define("diggerChance", "-15,25,5");

        BEHEMOTH_CHANCE = BUILDER
                .comment("Base Spawn chance and soot modifier for a glyphid behemoth (base,modifier,minSoot)")
                .define("behemothChance", "-30,45,10");

        BRENDA_CHANCE = BUILDER
                .comment("Base Spawn chance and soot modifier for a glyphid brenda (base,modifier,minSoot)")
                .define("brendaChance", "-50,60,20");

        JOHNSON_CHANCE = BUILDER
                .comment("Base Spawn chance and soot modifier for Big Man Johnson (base,modifier,minSoot)")
                .define("johnsonChance", "-50,60,50");

        BUILDER.pop();

        return BUILDER.build();
    }

    private static int[] parseChanceString(String value) {
        String[] parts = value.split(",");
        int[] result = new int[3];
        try {
            result[0] = Integer.parseInt(parts[0].trim());
            result[1] = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
            result[2] = parts.length > 2 ? Integer.parseInt(parts[2].trim()) : 0;
        } catch (Exception e) {
            result[0] = 0;
            result[1] = 0;
            result[2] = 0;
        }
        return result;
    }

    public static void load() {
        enableMaskman = ENABLE_MASKMAN.get();
        maskmanDelay = MASKMAN_DELAY.get();
        maskmanChance = MASKMAN_CHANCE.get();
        maskmanMinRad = MASKMAN_MIN_RAD.get();
        maskmanUnderground = MASKMAN_UNDERGROUND.get();

        enableRaids = ENABLE_RAIDS.get();
        raidDelay = RAID_DELAY.get();
        raidChance = RAID_CHANCE.get();
        raidAmount = RAID_AMOUNT.get();
        raidDrones = RAID_DRONES.get();
        raidAttackDelay = RAID_ATTACK_DELAY.get();
        raidAttackReach = RAID_ATTACK_REACH.get();
        raidAttackDistance = RAID_ATTACK_DISTANCE.get();

        enableElementals = ENABLE_ELEMENTALS.get();
        elementalDelay = ELEMENTAL_DELAY.get();
        elementalChance = ELEMENTAL_CHANCE.get();
        elementalAmount = ELEMENTAL_AMOUNT.get();
        elementalDistance = ELEMENTAL_DISTANCE.get();

        enableDucks = ENABLE_DUCKS.get();
        enableMobGear = ENABLE_MOB_GEAR.get();
        enableMobWeapons = ENABLE_MOB_WEAPONS.get();

        enableHives = ENABLE_HIVES.get();
        hiveSpawn = HIVE_SPAWN.get();
        scoutThreshold = SCOUT_THRESHOLD.get();
        scoutSwarmSpawnChance = SCOUT_SWARM_SPAWN_CHANCE.get();
        waypointDebug = WAYPOINT_DEBUG.get();
        largeHiveChance = LARGE_HIVE_CHANCE.get();
        largeHiveThreshold = LARGE_HIVE_THRESHOLD.get();

        swarmCooldown = SWARM_COOLDOWN.get();
        baseSwarmSize = BASE_SWARM_SIZE.get();
        swarmScalingMult = SWARM_SCALING_MULT.get();
        sootStep = SOOT_STEP.get();

        spawnMax = SPAWN_MAX.get();
        enableInfestation = ENABLE_INFESTATION.get();
        baseInfestChance = BASE_INFEST_CHANCE.get();
        targetingThreshold = TARGETING_THRESHOLD.get();

        rampantMode = RAMPANT_MODE.get();
        rampantNaturalScoutSpawn = RAMPANT_NATURAL_SCOUT_SPAWN.get();
        rampantScoutSpawnThresh = RAMPANT_SCOUT_SPAWN_THRESH.get();
        rampantScoutSpawnChance = RAMPANT_SCOUT_SPAWN_CHANCE.get();
        rampantExtendedTargeting = RAMPANT_EXTENDED_TARGETING.get();
        rampantDig = RAMPANT_DIG.get();
        rampantGlyphidGuidance = RAMPANT_GLYPHID_GUIDANCE.get();
        rampantSmokeStackOverride = RAMPANT_SMOKE_STACK_OVERRIDE.get();
        pollutionMult = POLLUTION_MULT.get();
        scoutInitialSpawn = SCOUT_INITIAL_SPAWN.get();

        glyphidChance = parseChanceString(GLYPHID_CHANCE.get());
        brawlerChance = parseChanceString(BRAWLER_CHANCE.get());
        bombardierChance = parseChanceString(BOMBARDIER_CHANCE.get());
        blasterChance = parseChanceString(BLASTER_CHANCE.get());
        diggerChance = parseChanceString(DIGGER_CHANCE.get());
        behemothChance = parseChanceString(BEHEMOTH_CHANCE.get());
        brendaChance = parseChanceString(BRENDA_CHANCE.get());
        johnsonChance = parseChanceString(JOHNSON_CHANCE.get());

        if (rampantMode) {
            rampantNaturalScoutSpawn = true;
            rampantExtendedTargeting = true;
            rampantDig = true;
            rampantGlyphidGuidance = true;
            scoutSwarmSpawnChance = 1;
            scoutThreshold = 0.1;
            if (pollutionMult == 3.0) {
                pollutionMult = 3.0;
            }
            if (bombardierChance[2] == 1) {
                bombardierChance[2] = 0;
            }
            // Note: RadiationConfig.sootFogThreshold *= pollutionMult should be handled in RadiationConfig
        }
    }
}