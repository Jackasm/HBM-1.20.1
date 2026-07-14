package com.hbm.config;

import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

public class VersatileConfig {

    // Конфигурационные значения
    public static ForgeConfigSpec.BooleanValue enableLBSM;
    public static ForgeConfigSpec.BooleanValue enableLBSMFullSchrab;
    public static ForgeConfigSpec.BooleanValue enableLBSMShorterDecay;
    public static ForgeConfigSpec.IntValue schrabRate;
    public static ForgeConfigSpec.IntValue potionSickness;
    public static ForgeConfigSpec.BooleanValue enable528;
    public static ForgeConfigSpec.BooleanValue doRTGsDecay;
    public static ForgeConfigSpec.BooleanValue scaleRTGPower;

    // Публичные переменные для доступа (с значениями по умолчанию)
    public static boolean ENABLE_LBSM = false;
    public static boolean ENABLE_LBSM_FULL_SCHRAB = false;
    public static boolean ENABLE_LBSM_SHORTER_DECAY = false;
    public static int SCHRAB_RATE = 100;
    public static int POTION_SICKNESS = 1;
    public static boolean ENABLE_528 = false;
    public static boolean DO_RTGS_DECAY = true;
    public static boolean SCALE_RTG_POWER = true;

    // Константы времени
    private static final int MINUTE = 60 * 20;
    private static final int HOUR = 60 * MINUTE;

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("General configuration settings")
                .push("general");

        enableLBSM = BUILDER
                .comment("Enable LBSM (Large Build Server Mode) features")
                .define("enableLBSM", false);

        enableLBSMFullSchrab = BUILDER
                .comment("In LBSM mode, allow full schrabidium generation instead of schraranium")
                .define("enableLBSMFullSchrab", false);

        enableLBSMShorterDecay = BUILDER
                .comment("In LBSM mode, use shorter decay times")
                .define("enableLBSMShorterDecay", false);

        schrabRate = BUILDER
                .comment("Schrabidium ore generation chance (1/X)")
                .defineInRange("schrabRate", 100, 1, 10000);

        enable528 = BUILDER
                .comment("Enable 5.28 mode (special configuration)")
                .define("enable528", false);

        BUILDER.pop();

        BUILDER.comment("Machine configuration settings")
                .push("machines");

        doRTGsDecay = BUILDER
                .comment("Enable RTG decay over time")
                .define("doRTGsDecay", true);

        scaleRTGPower = BUILDER
                .comment("Scale RTG power output")
                .define("scaleRTGPower", true);

        BUILDER.pop();

        BUILDER.comment("Potion configuration settings")
                .push("potions");

        potionSickness = BUILDER
                .comment("Potion sickness mode: 0 = disabled, 1 = normal, 2 = extended")
                .defineInRange("potionSickness", 1, 0, 2);

        BUILDER.pop();

        return BUILDER.build();
    }

    public static void load() {
        // Загружаем значения из конфигурации в статические поля
        ENABLE_LBSM = enableLBSM.get();
        ENABLE_LBSM_FULL_SCHRAB = enableLBSMFullSchrab.get();
        ENABLE_LBSM_SHORTER_DECAY = enableLBSMShorterDecay.get();
        SCHRAB_RATE = schrabRate.get();
        ENABLE_528 = enable528.get();
        DO_RTGS_DECAY = doRTGsDecay.get();
        SCALE_RTG_POWER = scaleRTGPower.get();
        POTION_SICKNESS = potionSickness.get();
    }

    public static Item getTransmutatorItem() {
        if (ENABLE_LBSM && ENABLE_LBSM_FULL_SCHRAB)
            return ModItems.INGOT_SCHRABIDIUM.get();

        return ModItems.INGOT_SCHRARANIUM.get();
    }

    public static int getSchrabOreChance() {
        if (ENABLE_LBSM)
            return SCHRAB_RATE;

        return 100;
    }

    public static void applyPotionSickness(LivingEntity entity, int duration) {
        if (POTION_SICKNESS == 0)
            return;

        if (POTION_SICKNESS == 2)
            duration *= 12;

        MobEffectInstance effect = new MobEffectInstance(HbmPotion.POTIONSICKNESS.get(), duration * 20);
        effect.setCurativeItems(new ArrayList<>());
        entity.addEffect(effect);
    }

    public static boolean hasPotionSickness(LivingEntity entity) {
        return entity.hasEffect(HbmPotion.POTIONSICKNESS.get());
    }

    public static boolean rtgDecay() {
        return ENABLE_528 || DO_RTGS_DECAY;
    }

    public static boolean scaleRTGPower() {
        return ENABLE_528 || SCALE_RTG_POWER;
    }

    public static int getLongDecayChance() {
        return ENABLE_528 ? 15 * HOUR : (ENABLE_LBSM && ENABLE_LBSM_SHORTER_DECAY) ? 15 * MINUTE : 3 * HOUR;
    }

    public static int getShortDecayChance() {
        return ENABLE_528 ? 3 * HOUR : (ENABLE_LBSM && ENABLE_LBSM_SHORTER_DECAY) ? 3 * MINUTE : 15 * MINUTE;
    }
}