package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;


public class ToolConfig {

    // Конфигурационные значения
    public static ForgeConfigSpec.IntValue recursionDepth;
    public static ForgeConfigSpec.BooleanValue recursiveStone;
    public static ForgeConfigSpec.BooleanValue recursiveNetherrack;

    public static ForgeConfigSpec.BooleanValue abilityHammer;
    public static ForgeConfigSpec.BooleanValue abilityVein;
    public static ForgeConfigSpec.BooleanValue abilityLuck;
    public static ForgeConfigSpec.BooleanValue abilitySilk;
    public static ForgeConfigSpec.BooleanValue abilitySmelter;
    public static ForgeConfigSpec.BooleanValue abilityShredder;
    public static ForgeConfigSpec.BooleanValue abilityCentrifuge;
    public static ForgeConfigSpec.BooleanValue abilityCrystallizer;
    public static ForgeConfigSpec.BooleanValue abilityMercury;
    public static ForgeConfigSpec.BooleanValue abilityExplosion;

    // Публичные переменные для доступа
    public static int RECURSION_DEPTH = 500;
    public static boolean RECURSIVE_STONE = false;
    public static boolean RECURSIVE_NETHERRACK = false;

    public static boolean ABILITY_HAMMER = true;
    public static boolean ABILITY_VEIN = true;
    public static boolean ABILITY_LUCK = true;
    public static boolean ABILITY_SILK = true;
    public static boolean ABILITY_SMELTER = true;
    public static boolean ABILITY_SHREDDER = true;
    public static boolean ABILITY_CENTRIFUGE = true;
    public static boolean ABILITY_CRYSTALLIZER = true;
    public static boolean ABILITY_MERCURY = true;
    public static boolean ABILITY_EXPLOSION = true;



    public static ForgeConfigSpec register(){
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("Tool configuration settings")
                .push("tools");

        BUILDER.comment("Vein miner settings")
                .push("vein_miner");

        recursionDepth = BUILDER
                .comment("Limits veinminer's recursive function. Usually not an issue, unless you're using bukkit which is especially sensitive for some reason.")
                .defineInRange("recursionDepth", 500, 100, 10000);

        recursiveStone = BUILDER
                .comment("Determines whether veinminer can break stone")
                .define("recursionStone", false);

        recursiveNetherrack = BUILDER
                .comment("Determines whether veinminer can break netherrack")
                .define("recursionNetherrack", false);

        BUILDER.pop();

        BUILDER.comment("Tool ability settings")
                .push("abilities");

        abilityHammer = BUILDER
                .comment("Allows AoE hammer ability")
                .define("hammerAbility", true);

        abilityVein = BUILDER
                .comment("Allows veinminer ability")
                .define("abilityVein", true);

        abilityLuck = BUILDER
                .comment("Allow luck (fortune) ability")
                .define("abilityLuck", true);

        abilitySilk = BUILDER
                .comment("Allow silk touch ability")
                .define("abilitySilk", true);

        abilitySmelter = BUILDER
                .comment("Allow auto-smelter ability")
                .define("abilitySmelter", true);

        abilityShredder = BUILDER
                .comment("Allow auto-shredder ability")
                .define("abilityShredder", true);

        abilityCentrifuge = BUILDER
                .comment("Allow auto-centrifuge ability")
                .define("abilityCentrifuge", true);

        abilityCrystallizer = BUILDER
                .comment("Allow auto-crystallizer ability")
                .define("abilityCrystallizer", true);

        abilityMercury = BUILDER
                .comment("Allow mercury touch ability (digging redstone gives mercury)")
                .define("abilityMercury", true);

        abilityExplosion = BUILDER
                .comment("Allow explosion ability")
                .define("abilityExplosion", true);

        BUILDER.pop();
        BUILDER.pop();

        return BUILDER.build();

    }


    public static void load() {
        // Загружаем значения из конфигурации в статические поля
        RECURSION_DEPTH = recursionDepth.get();
        RECURSIVE_STONE = recursiveStone.get();
        RECURSIVE_NETHERRACK = recursiveNetherrack.get();

        ABILITY_HAMMER = abilityHammer.get();
        ABILITY_VEIN = abilityVein.get();
        ABILITY_LUCK = abilityLuck.get();
        ABILITY_SILK = abilitySilk.get();
        ABILITY_SMELTER = abilitySmelter.get();
        ABILITY_SHREDDER = abilityShredder.get();
        ABILITY_CENTRIFUGE = abilityCentrifuge.get();
        ABILITY_CRYSTALLIZER = abilityCrystallizer.get();
        ABILITY_MERCURY = abilityMercury.get();
        ABILITY_EXPLOSION = abilityExplosion.get();
    }

}