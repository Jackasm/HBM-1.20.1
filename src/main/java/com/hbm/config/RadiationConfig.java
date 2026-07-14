package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RadiationConfig {

    // Конфигурационные значения
    public static ForgeConfigSpec.BooleanValue disableAsbestos;
    public static ForgeConfigSpec.BooleanValue disableCoal;
    public static ForgeConfigSpec.BooleanValue disableHot;
    public static ForgeConfigSpec.BooleanValue disableExplosive;
    public static ForgeConfigSpec.BooleanValue disableHydro;
    public static ForgeConfigSpec.BooleanValue disableBlinding;
    public static ForgeConfigSpec.BooleanValue disableFibrosis;

    public static ForgeConfigSpec.BooleanValue enableContamination;
    public static ForgeConfigSpec.BooleanValue enableChunkRads;
    public static ForgeConfigSpec.BooleanValue enablePRISM;

    public static ForgeConfigSpec.IntValue fogRad;
    public static ForgeConfigSpec.IntValue fogCh;
    public static ForgeConfigSpec.DoubleValue hellRad;
    public static ForgeConfigSpec.IntValue worldRad;
    public static ForgeConfigSpec.IntValue worldRadThreshold;
    public static ForgeConfigSpec.BooleanValue worldRadEffects;
    public static ForgeConfigSpec.BooleanValue cleanupDeadDirt;

    public static ForgeConfigSpec.BooleanValue enablePollution;
    public static ForgeConfigSpec.BooleanValue enableLeadFromBlocks;
    public static ForgeConfigSpec.BooleanValue enableLeadPoisoning;
    public static ForgeConfigSpec.BooleanValue enableSootFog;
    public static ForgeConfigSpec.BooleanValue enablePoison;
    public static ForgeConfigSpec.DoubleValue buffMobThreshold;
    public static ForgeConfigSpec.DoubleValue sootFogThreshold;
    public static ForgeConfigSpec.DoubleValue sootFogDivisor;
    public static ForgeConfigSpec.DoubleValue smokeStackSootMult;

    // Публичные переменные для доступа
    public static boolean DISABLE_ASBESTOS = false;
    public static boolean DISABLE_COAL = false;
    public static boolean DISABLE_HOT = false;
    public static boolean DISABLE_EXPLOSIVE = false;
    public static boolean DISABLE_HYDRO = false;
    public static boolean DISABLE_BLINDING = false;
    public static boolean DISABLE_FIBROSIS = false;

    public static boolean ENABLE_CONTAMINATION = true;
    public static boolean ENABLE_CHUNK_RADS = true;
    public static boolean ENABLE_PRISM = false;

    public static int FOG_RAD = 100;
    public static int FOG_CH = 20;
    public static double HELL_RAD = 0.1;
    public static int WORLD_RAD = 10;
    public static int WORLD_RAD_THRESHOLD = 20;
    public static boolean WORLD_RAD_EFFECTS = true;
    public static boolean CLEANUP_DEAD_DIRT = false;

    public static boolean ENABLE_POLLUTION = true;
    public static boolean ENABLE_LEAD_FROM_BLOCKS = true;
    public static boolean ENABLE_LEAD_POISONING = true;
    public static boolean ENABLE_SOOT_FOG = true;
    public static boolean ENABLE_POISON = true;
    public static double BUFF_MOB_THRESHOLD = 15.0;
    public static double SOOT_FOG_THRESHOLD = 35.0;
    public static double SOOT_FOG_DIVISOR = 120.0;
    public static double SMOKE_STACK_SOOT_MULT = 0.8;

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("Radiation configuration settings")
                .push("radiation");

        BUILDER.comment("Radiation effects settings")
                .push("effects");

        fogRad = BUILDER
                .comment("Radiation in RADs required for fog to spawn")
                .defineInRange("fogRad", 100, 0, Integer.MAX_VALUE);

        fogCh = BUILDER
                .comment("1:n chance of fog spawning every second")
                .defineInRange("fogCh", 20, 1, Integer.MAX_VALUE);

        hellRad = BUILDER
                .comment("RAD/s in the nether")
                .defineInRange("hellRad", 0.1, 0.0, Double.MAX_VALUE);

        worldRad = BUILDER
                .comment("How many block operations radiation can perform per tick")
                .defineInRange("worldRad", 10, 0, 1000);

        worldRadThreshold = BUILDER
                .comment("The least amount of RADs required for block modification to happen")
                .defineInRange("worldRadThreshold", 20, 0, 1000);

        worldRadEffects = BUILDER
                .comment("Whether high radiation levels should perform changes in the world")
                .define("worldRadEffects", true);

        cleanupDeadDirt = BUILDER
                .comment("Whether dead grass and mycelium should decay into dirt")
                .define("cleanupDeadDirt", false);

        BUILDER.pop();

        BUILDER.comment("Radiation system settings")
                .push("system");

        enableContamination = BUILDER
                .comment("Toggles player contamination (and negative effects from radiation poisoning)")
                .define("enableContamination", true);

        enableChunkRads = BUILDER
                .comment("Toggles the world radiation system (chunk radiation only, some block use an AoE!)")
                .define("enableChunkRads", true);

        enablePRISM = BUILDER
                .comment("Enables the new 3D resistance-aware PRISM radiation system")
                .define("enablePRISM", false);

        BUILDER.pop();

        BUILDER.comment("Hazard configuration")
                .push("hazards");

        disableAsbestos = BUILDER
                .comment("When turned on, all asbestos hazards are disabled")
                .define("disableAsbestos", false);

        disableCoal = BUILDER
                .comment("When turned on, all coal dust hazards are disabled")
                .define("disableCoal", false);

        disableHot = BUILDER
                .comment("When turned on, all hot hazards are disabled")
                .define("disableHot", false);

        disableExplosive = BUILDER
                .comment("When turned on, all explosive hazards are disabled")
                .define("disableExplosive", false);

        disableHydro = BUILDER
                .comment("When turned on, all hydroactive hazards are disabled")
                .define("disableHydro", false);

        disableBlinding = BUILDER
                .comment("When turned on, all blinding hazards are disabled")
                .define("disableBlinding", false);

        disableFibrosis = BUILDER
                .comment("When turned on, all fibrosis hazards are disabled")
                .define("disableFibrosis", false);

        BUILDER.pop();

        BUILDER.comment("Pollution settings")
                .push("pollution");

        enablePollution = BUILDER
                .comment("If disabled, none of the pollution related things will work")
                .define("enablePollution", true);

        enableLeadFromBlocks = BUILDER
                .comment("Whether breaking block in heavy metal polluted areas will poison the player")
                .define("enableLeadFromBlocks", true);

        enableLeadPoisoning = BUILDER
                .comment("Whether being in a heavy metal polluted area will poison the player")
                .define("enableLeadPoisoning", true);

        enableSootFog = BUILDER
                .comment("Whether smog should be visible")
                .define("enableSootFog", true);

        enablePoison = BUILDER
                .comment("Whether being in a poisoned area will affect the player")
                .define("enablePoison", true);

        buffMobThreshold = BUILDER
                .comment("The amount of soot required to buff naturally spawning mobs")
                .defineInRange("buffMobThreshold", 15.0, 0.0, Double.MAX_VALUE);

        sootFogThreshold = BUILDER
                .comment("How much soot is required for smog to become visible")
                .defineInRange("sootFogThreshold", 35.0, 0.0, Double.MAX_VALUE);

        sootFogDivisor = BUILDER
                .comment("The divisor for smog, higher numbers will require more soot for the same smog density")
                .defineInRange("sootFogDivisor", 120.0, 1.0, Double.MAX_VALUE);

        smokeStackSootMult = BUILDER
                .comment("How much does smokestack multiply soot by, with decimal values reducing the soot")
                .defineInRange("smokeStackSootMult", 0.8, 0.0, Double.MAX_VALUE);

        BUILDER.pop();
        BUILDER.pop();

        return BUILDER.build();
    }

    public static void load() {
        // Загружаем значения из конфигурации в статические поля

        DISABLE_ASBESTOS = disableAsbestos.get();
        DISABLE_COAL = disableCoal.get();
        DISABLE_HOT = disableHot.get();
        DISABLE_EXPLOSIVE = disableExplosive.get();
        DISABLE_HYDRO = disableHydro.get();
        DISABLE_BLINDING = disableBlinding.get();
        DISABLE_FIBROSIS = disableFibrosis.get();

        ENABLE_CONTAMINATION = enableContamination.get();
        ENABLE_CHUNK_RADS = enableChunkRads.get();
        ENABLE_PRISM = enablePRISM.get();

        FOG_RAD = fogRad.get();
        FOG_CH = fogCh.get();
        HELL_RAD = hellRad.get();
        WORLD_RAD = worldRad.get();
        WORLD_RAD_THRESHOLD = worldRadThreshold.get();
        WORLD_RAD_EFFECTS = worldRadEffects.get();
        CLEANUP_DEAD_DIRT = cleanupDeadDirt.get();

        ENABLE_POLLUTION = enablePollution.get();
        ENABLE_LEAD_FROM_BLOCKS = enableLeadFromBlocks.get();
        ENABLE_LEAD_POISONING = enableLeadPoisoning.get();
        ENABLE_SOOT_FOG = enableSootFog.get();
        ENABLE_POISON = enablePoison.get();
        BUFF_MOB_THRESHOLD = buffMobThreshold.get();
        SOOT_FOG_THRESHOLD = sootFogThreshold.get();
        SOOT_FOG_DIVISOR = sootFogDivisor.get();
        SMOKE_STACK_SOOT_MULT = smokeStackSootMult.get();
    }
}