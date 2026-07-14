package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class WeaponConfig {

    // Missile settings
    public static ForgeConfigSpec.IntValue ciwsHitrate;

    // Drop settings
    public static ForgeConfigSpec.BooleanValue dropCell;
    public static ForgeConfigSpec.BooleanValue dropSing;
    public static ForgeConfigSpec.BooleanValue dropStar;
    public static ForgeConfigSpec.BooleanValue dropCrys;
    public static ForgeConfigSpec.BooleanValue dropDead;

    // Weapon animations
    public static ForgeConfigSpec.BooleanValue linearAnimations;

    public static int CIWS_HITRATE = 50;
    public static boolean DROP_CELL = true;
    public static boolean DROP_SING = true;
    public static boolean DROP_STAR = true;
    public static boolean DROP_CRYS = true;
    public static boolean DROP_DEAD = true;
    public static boolean LINEAR_ANIMATION = false;

    public static void load() {
        CIWS_HITRATE = ciwsHitrate.get();
        DROP_CELL = dropCell.get();
        DROP_SING = dropSing.get();
        DROP_STAR = dropStar.get();
        DROP_CRYS = dropCrys.get();
        DROP_DEAD = dropDead.get();
        LINEAR_ANIMATION = linearAnimations.get();
    }



    public static void init(ForgeConfigSpec.Builder builder) {

        builder.comment("Missile Settings").push("missile");

        ciwsHitrate = builder
                .comment("Additional modifier for CIWS accuracy")
                .defineInRange("ciwsHitrate", 50, 0, Integer.MAX_VALUE);

        builder.pop();

        builder.comment("Drop Settings").push("drops");

        dropCell = builder
                .comment("Whether antimatter cells should explode when dropped")
                .define("dropCell", true);

        dropSing = builder
                .comment("Whether singularities and black holes should spawn when dropped")
                .define("dropSing", true);

        dropStar = builder
                .comment("Whether rigged star blaster cells should explode when dropped")
                .define("dropStar", true);

        dropCrys = builder
                .comment("Whether xen crystals should move blocks when dropped")
                .define("dropCrys", true);

        dropDead = builder
                .comment("Whether dead man's explosives should explode when dropped")
                .define("dropDead", true);

        builder.pop();

        builder.comment("Weapon Settings").push("weapons");

        linearAnimations = builder
                .comment("Should heavily stylised weapon animations be replaced with more conventional ones?")
                .define("linearAnimations", false);

        builder.pop();
    }
}