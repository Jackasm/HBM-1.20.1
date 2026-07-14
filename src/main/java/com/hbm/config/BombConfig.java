package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BombConfig {

    // Радиусы взрывов
    public static ForgeConfigSpec.IntValue gadgetRadius;
    public static ForgeConfigSpec.IntValue boyRadius;
    public static ForgeConfigSpec.IntValue manRadius;
    public static ForgeConfigSpec.IntValue mikeRadius;
    public static ForgeConfigSpec.IntValue tsarRadius;
    public static ForgeConfigSpec.IntValue prototypeRadius;
    public static ForgeConfigSpec.IntValue fleijaRadius;
    public static ForgeConfigSpec.IntValue soliniumRadius;
    public static ForgeConfigSpec.IntValue n2Radius;
    public static ForgeConfigSpec.IntValue missileRadius;
    public static ForgeConfigSpec.IntValue mirvRadius;
    public static ForgeConfigSpec.IntValue fatmanRadius;
    public static ForgeConfigSpec.IntValue nukaRadius;
    public static ForgeConfigSpec.IntValue aSchrabRadius;

    // Настройки взрывов
    public static ForgeConfigSpec.IntValue mk5;
    public static ForgeConfigSpec.IntValue blastSpeed;
    public static ForgeConfigSpec.IntValue falloutRange;
    public static ForgeConfigSpec.IntValue fDelay;
    public static ForgeConfigSpec.IntValue limitExplosionLifespan;
    public static ForgeConfigSpec.BooleanValue chunkloading;
    public static ForgeConfigSpec.IntValue explosionAlgorithm;

    // Публичные переменные для доступа (с значениями по умолчанию)
    public static int GADGET_RADIUS = 150;
    public static int BOY_RADIUS = 120;
    public static int MAN_RADIUS = 175;
    public static int MIKE_RADIUS = 250;
    public static int TSAR_RADIUS = 500;
    public static int PROTOTYPE_RADIUS = 150;
    public static int FLEIJA_RADIUS = 50;
    public static int SOLINIUM_RADIUS = 150;
    public static int N2_RADIUS = 200;
    public static int MISSILE_RADIUS = 100;
    public static int MIRV_RADIUS = 100;
    public static int FATMAN_RADIUS = 35;
    public static int NUKA_RADIUS = 25;
    public static int A_SCHRAB_RADIUS = 20;

    public static int MK5 = 50;
    public static int BLAST_SPEED = 1024;
    public static int FALLOUT_RANGE = 100;
    public static int F_DELAY = 4;
    public static int LIMIT_EXPLOSION_LIFESPAN = 0;
    public static boolean CHUNKLOADING = true;
    public static int EXPLOSION_ALGORITHM = 2;

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("Nuclear bomb settings")
                .push("nukes");

        gadgetRadius = BUILDER
                .comment("Radius of the Gadget")
                .defineInRange("gadgetRadius", 150, 1, 10000);

        boyRadius = BUILDER
                .comment("Radius of Little Boy")
                .defineInRange("boyRadius", 120, 1, 10000);

        manRadius = BUILDER
                .comment("Radius of Fat Man")
                .defineInRange("manRadius", 175, 1, 10000);

        mikeRadius = BUILDER
                .comment("Radius of Ivy Mike")
                .defineInRange("mikeRadius", 250, 1, 10000);

        tsarRadius = BUILDER
                .comment("Radius of the Tsar Bomba")
                .defineInRange("tsarRadius", 500, 1, 10000);

        prototypeRadius = BUILDER
                .comment("Radius of the Prototype")
                .defineInRange("prototypeRadius", 150, 1, 10000);

        fleijaRadius = BUILDER
                .comment("Radius of F.L.E.I.J.A.")
                .defineInRange("fleijaRadius", 50, 1, 10000);

        soliniumRadius = BUILDER
                .comment("Radius of the blue rinse")
                .defineInRange("soliniumRadius", 150, 1, 10000);

        n2Radius = BUILDER
                .comment("Radius of the N2 mine")
                .defineInRange("n2Radius", 200, 1, 10000);

        missileRadius = BUILDER
                .comment("Radius of the nuclear missile")
                .defineInRange("missileRadius", 100, 1, 10000);

        mirvRadius = BUILDER
                .comment("Radius of a MIRV")
                .defineInRange("mirvRadius", 100, 1, 10000);

        fatmanRadius = BUILDER
                .comment("Radius of the Fatman Launcher")
                .defineInRange("fatmanRadius", 35, 1, 10000);

        nukaRadius = BUILDER
                .comment("Radius of the nuka grenade")
                .defineInRange("nukaRadius", 25, 1, 10000);

        aSchrabRadius = BUILDER
                .comment("Radius of dropped anti schrabidium")
                .defineInRange("aSchrabRadius", 20, 1, 10000);

        BUILDER.pop();

        BUILDER.comment("Explosion system settings")
                .push("explosions");

        limitExplosionLifespan = BUILDER
                .comment("How long an explosion can be unloaded until it dies in seconds. Based of system time. 0 disables the effect")
                .defineInRange("limitExplosionLifespan", 0, 0, 3600);

        blastSpeed = BUILDER
                .comment("Base speed of MK3 system (old and schrabidium) detonations (Blocks / tick)")
                .defineInRange("blastSpeed", 1024, 1, 100000);

        mk5 = BUILDER
                .comment("Minimum amount of milliseconds per tick allocated for mk5 chunk processing")
                .defineInRange("mk5", 50, 1, 10000);

        falloutRange = BUILDER
                .comment("Radius of fallout area (base radius * value in percent)")
                .defineInRange("falloutRange", 100, 0, 1000);

        fDelay = BUILDER
                .comment("How many ticks to wait for the next fallout chunk computation")
                .defineInRange("fDelay", 4, 1, 1000);

        chunkloading = BUILDER
                .comment("Allows all types of procedural explosions to keep the central chunk loaded and to generate new chunks.")
                .define("chunkloading", true);

        explosionAlgorithm = BUILDER
                .comment("Configures the algorithm of mk5 explosion. \n0 = Legacy, 1 = Threaded DDA, 2 = Threaded DDA with damage accumulation.")
                .defineInRange("explosionAlgorithm", 2, 0, 2);

        BUILDER.pop();

        return BUILDER.build();
    }

    public static void load() {
        GADGET_RADIUS = gadgetRadius.get();
        BOY_RADIUS = boyRadius.get();
        MAN_RADIUS = manRadius.get();
        MIKE_RADIUS = mikeRadius.get();
        TSAR_RADIUS = tsarRadius.get();
        PROTOTYPE_RADIUS = prototypeRadius.get();
        FLEIJA_RADIUS = fleijaRadius.get();
        SOLINIUM_RADIUS = soliniumRadius.get();
        N2_RADIUS = n2Radius.get();
        MISSILE_RADIUS = missileRadius.get();
        MIRV_RADIUS = mirvRadius.get();
        FATMAN_RADIUS = fatmanRadius.get();
        NUKA_RADIUS = nukaRadius.get();
        A_SCHRAB_RADIUS = aSchrabRadius.get();

        MK5 = mk5.get();
        BLAST_SPEED = blastSpeed.get();
        FALLOUT_RANGE = falloutRange.get();
        F_DELAY = fDelay.get();
        LIMIT_EXPLOSION_LIFESPAN = limitExplosionLifespan.get();
        CHUNKLOADING = chunkloading.get();
        EXPLOSION_ALGORITHM = explosionAlgorithm.get();
    }
}