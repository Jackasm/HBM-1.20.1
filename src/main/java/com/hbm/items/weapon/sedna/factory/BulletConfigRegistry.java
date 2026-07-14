package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.effect.EntityFireLingering;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.particle.SpentCasing;
import com.hbm.util.HBMEnums;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.hbm.items.weapon.sedna.BulletConfig.*;
import static com.hbm.items.weapon.sedna.BulletConfig.BOLT_LASER;
import static com.hbm.items.weapon.sedna.BulletConfig.BOLT_NIGHTMARE;
import static com.hbm.items.weapon.sedna.BulletConfig.BOLT_WORM;
import static com.hbm.items.weapon.sedna.BulletConfig.STYLE_BLADE;
import static com.hbm.items.weapon.sedna.BulletConfig.STYLE_BOLT;
import static com.hbm.items.weapon.sedna.BulletConfig.STYLE_FLECHETTE;
import static com.hbm.items.weapon.sedna.BulletConfig.STYLE_METEOR;
import static com.hbm.items.weapon.sedna.BulletConfig.STYLE_ROCKET;
import static com.hbm.items.weapon.sedna.factory.XFactory10ga.LAMBDA_TINY_EXPLODE;
import static com.hbm.items.weapon.sedna.factory.XFactory12ga.*;
import static com.hbm.items.weapon.sedna.factory.XFactory556mm.LAMBDA_INCENDIARY;
import static com.hbm.items.weapon.sedna.factory.XFactoryCatapult.*;
import static com.hbm.items.weapon.sedna.factory.XFactoryEnergy.LAMBDA_IR_HIT;
import static com.hbm.items.weapon.sedna.factory.XFactoryRocket.*;
import static com.hbm.util.HBMEnums.EnumCasingType.*;

public class BulletConfigRegistry {

    static float buckshotSpread = 0.035F;
    static float magnumSpread = 0.015F;
    static float pistolSpread = 0.01F;
    static float rifleSpread = 0.005F;

    public static Item ammo_standard;

    public static final BulletConfig STONE = new BulletConfig()
            .setBlackPowder(true)
            .setHeadshot(1F)
            .setSpread(0.025F)
            .setRicochetAngle(15)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setRenderRotations(true);

    public static final BulletConfig FLINT = new BulletConfig()
            .setBlackPowder(true)
            .setHeadshot(1F)
            .setSpread(0.01F)
            .setRicochetAngle(5)
            .setDoesPenetrate(true)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setRenderRotations(true);

    public static BulletConfig IRON = new BulletConfig()
            .setBlackPowder(true).setHeadshot(1F)
            .setSpread(0F)
            .setRicochetAngle(90)
            .setRicochetCount(5)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setRenderRotations(true);

    public static BulletConfig SHOT = new BulletConfig()
            .setBlackPowder(true).setHeadshot(1F)
            .setSpread(0.1F).setRicochetAngle(45)
            .setProjectiles(6, 6)
            .setDamage(1F/6F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setRenderRotations(true);

    public static BulletConfig g12_bp = new BulletConfig()
        .setCasing(SHOTSHELL, 12)
        .setBlackPowder(true)
        .setProjectiles(8)
        .setDamage(0.75F/8F)
        .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
        .setSpread(buckshotSpread)
        .setRicochetAngle(15)
        .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
                .setColor(SpentCasing.COLOR_CASE_BRASS, SpentCasing.COLOR_CASE_BRASS)
                .setScale(0.75F)
                .register("12GA_BP"));

    public static BulletConfig g12_bp_magnum = new BulletConfig()
        .setCasing(SHOTSHELL, 12)
        .setBlackPowder(true)
        .setProjectiles(4)
        .setDamage(0.75F/4F)
        .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
        .setSpread(buckshotSpread)
        .setRicochetAngle(25)
        .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
                .setColor(SpentCasing.COLOR_CASE_BRASS, SpentCasing.COLOR_CASE_BRASS)
                .setScale(0.75F)
                .register("12GA_BP_MAGNUM"));

    public static BulletConfig g12_bp_slug = new BulletConfig()
        .setCasing(SHOTSHELL, 12)
        .setBlackPowder(true)
        .setDamage(0.75F)
        .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
        .setSpread(0.01F)
        .setRicochetAngle(5)
        .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
                .setColor(SpentCasing.COLOR_CASE_BRASS, SpentCasing.COLOR_CASE_BRASS)
                .setScale(0.75F)
                .register("12GA_BP_SLUG"));

    public static BulletConfig g12 = new BulletConfig()
        .setCasing(BUCKSHOT, 6)
        .setProjectiles(8)
        .setDamage(1F/8F)
        .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
        .setSpread(buckshotSpread)
        .setRicochetAngle(15)
        .setThresholdNegation(2F)
        .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
                .setColor(0xB52B2B, SpentCasing.COLOR_CASE_BRASS)
                .setScale(0.75F)
                .register("12GA"));

    public static BulletConfig g12_slug = new BulletConfig()
            .setCasing(BUCKSHOT, 6)
            .setHeadshot(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(0.0F)
            .setRicochetAngle(25)
            .setThresholdNegation(4F)
            .setArmorPiercing(0.15F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0x393939, SpentCasing.COLOR_CASE_BRASS)
            .setScale(0.75F)
            .register("12GA_SLUG"));

    public static BulletConfig g12_flechette = new BulletConfig()
            .setCasing(BUCKSHOT, 6)
            .setProjectiles(8)
            .setDamage(1F/8F)
            .setRenderer(LegoClient.RENDER_FLECHETTE_BULLET)
            .setThresholdNegation(5F)
            .setArmorPiercing(0.2F)
            .setSpread(0.025F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0x3C80F0, SpentCasing.COLOR_CASE_BRASS)
            .setScale(0.75F)
            .register("12GA_FLECHETTE"));

    public static BulletConfig g12_magnum = new BulletConfig()
            .setCasing(BUCKSHOT_ADVANCED, 6)
            .setProjectiles(4)
            .setDamage(2F/4F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(magnumSpread)
            .setRicochetAngle(15)
            .setThresholdNegation(4F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0x278400, SpentCasing.COLOR_CASE_12GA)
            .setScale(0.75F)
            .register("12GA_MAGNUM"));

    public static BulletConfig g12_explosive = new BulletConfig()
            .setCasing(BUCKSHOT_ADVANCED, 6)
            .setDamage(2.5F)
            .setRenderer(LegoClient.RENDER_EXPRESS_BULLET)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE)
            .setSpread(0F)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0xDA4127, SpentCasing.COLOR_CASE_12GA)
            .setScale(0.75F)
            .register("12GA_EXPLOSIVE"));

    public static BulletConfig g12_phosphorus = new BulletConfig()
            .setCasing(BUCKSHOT_ADVANCED, 6)
            .setProjectiles(8)
            .setDamage(1F/8F)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setSpread(magnumSpread)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0x910001, SpentCasing.COLOR_CASE_12GA)
            .setScale(0.75F)
            .register("12GA_PHOSPHORUS"))
            .setOnImpact(XFactory12ga.LAMBDA_INC);

    public static BulletConfig g12_equestrian_bj = new BulletConfig()
            .setDamage(0F)
            .setOnImpact(LAMBDA_BOAT)
            .setRenderer(LegoClient.RENDER_LEGENDARY_BULLET)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0xB52B2B, SpentCasing.COLOR_CASE_EQUESTRIAN)
            .setScale(0.75F)
            .register("12gaEquestrianBJ"));

    public static BulletConfig g12_equestrian_tkr = new BulletConfig()
            .setDamage(0F)
            .setRenderer(LegoClient.RENDER_LEGENDARY_BULLET)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0xB52B2B, SpentCasing.COLOR_CASE_EQUESTRIAN)
            .setScale(0.75F)
            .register("12gaEquestrianTKR"));

    public static BulletConfig g12_shredder =  g12.clone()
            .setRicochetAngle(90)
            .setRicochetCount(3)
            .setVel(0.5F)
            .setLife(50)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setRendererBeam(LegoClient.RENDER_LASER_CYAN)
            .setRenderRotations(false)
            .setLife(5)
            .setDamage(g12.damageMult * g12.projectilesMax)
            .setOnBeamImpact(LAMBDA_SHREDDER_IMPACT)
            .setOnRicochet(LAMBDA_SHREDDER_RICOCHET);

    public static BulletConfig g12_shredder_slug = g12_slug.clone()
            .setRicochetAngle(90)
            .setRicochetCount(3)
            .setVel(0.5F)
            .setLife(50)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setRendererBeam(LegoClient.RENDER_LASER_CYAN)
            .setRenderRotations(false)
            .setLife(5)
            .setDamage(g12_slug.damageMult * g12_slug.projectilesMax)
            .setOnBeamImpact(LAMBDA_SHREDDER_IMPACT)
            .setOnRicochet(LAMBDA_SHREDDER_RICOCHET);

    public static BulletConfig g12_shredder_flechette = g12_flechette.clone()
            .setRicochetAngle(90)
            .setRicochetCount(3)
            .setVel(0.5F)
            .setLife(50)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setRendererBeam(LegoClient.RENDER_LASER_CYAN)
            .setRenderRotations(false)
            .setLife(5)
            .setDamage(g12_flechette.damageMult * g12_flechette.projectilesMax)
            .setOnBeamImpact(LAMBDA_SHREDDER_IMPACT)
            .setOnRicochet(LAMBDA_SHREDDER_RICOCHET);

    public static BulletConfig g12_shredder_magnum = g12_magnum.clone()
            .setRicochetAngle(90)
            .setRicochetCount(3)
            .setVel(0.5F)
            .setLife(50)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setRendererBeam(LegoClient.RENDER_LASER_CYAN)
            .setRenderRotations(false)
            .setLife(5)
            .setDamage(g12_magnum.damageMult * g12_magnum.projectilesMax)
            .setOnBeamImpact(LAMBDA_SHREDDER_IMPACT)
            .setOnRicochet(LAMBDA_SHREDDER_RICOCHET);

    public static BulletConfig g12_shredder_explosive = g12_explosive.clone()
            .setRicochetAngle(90)
            .setRicochetCount(3)
            .setVel(0.5F)
            .setLife(50)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setRendererBeam(LegoClient.RENDER_LASER_CYAN)
            .setRenderRotations(false)
            .setLife(5)
            .setDamage(g12_explosive.damageMult * g12_explosive.projectilesMax)
            .setOnBeamImpact(LAMBDA_SHREDDER_IMPACT)
            .setOnRicochet(LAMBDA_SHREDDER_RICOCHET);

    public static BulletConfig g12_shredder_phosphorus = g12_phosphorus.clone()
            .setRicochetAngle(90)
            .setRicochetCount(3)
            .setVel(0.5F)
            .setLife(50)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setRendererBeam(LegoClient.RENDER_LASER_CYAN)
            .setRenderRotations(false)
            .setLife(5)
            .setDamage(g12_phosphorus.damageMult * g12_phosphorus.projectilesMax)
            .setOnBeamImpact(LAMBDA_SHREDDER_IMPACT)
            .setOnRicochet(LAMBDA_SHREDDER_RICOCHET);

    public static BulletConfig g10 = new BulletConfig()
            .setCasing(BUCKSHOT_ADVANCED, 4)
            .setProjectiles(10)
            .setDamage(1F/10F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(buckshotSpread)
            .setRicochetAngle(15)
            .setThresholdNegation(5F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0xB52B2B, SpentCasing.COLOR_CASE_12GA)
            .setScale(1F)
            .register("10GA"));

    public static BulletConfig g10_shrapnel = new BulletConfig()
            .setCasing(BUCKSHOT_ADVANCED, 4)
            .setProjectiles(10)
            .setDamage(1F/10F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(buckshotSpread)
            .setRicochetAngle(90)
            .setRicochetCount(15)
            .setThresholdNegation(5F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0xE5DD00, SpentCasing.COLOR_CASE_12GA)
            .setScale(1F)
            .register("10GAShrapnel"));

    public static BulletConfig g10_du = new BulletConfig()
            .setCasing(BUCKSHOT_ADVANCED, 4)
            .setProjectiles(10)
            .setDamage(1F/4F)
            .setRenderer(LegoClient.RENDER_DU_BULLET)
            .setSpread(buckshotSpread)
            .setRicochetAngle(15)
            .setThresholdNegation(10F)
            .setArmorPiercing(0.2F)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0x538D53, SpentCasing.COLOR_CASE_12GA)
            .setScale(1F)
            .register("10GADU"));

    public static BulletConfig g10_slug = new BulletConfig()
            .setCasing(BUCKSHOT_ADVANCED, 4)
            .setRicochetAngle(15)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setThresholdNegation(10F)
            .setArmorPiercing(0.1F)
            .setDoesPenetrate(true)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0x808080, SpentCasing.COLOR_CASE_12GA)
            .setScale(1F)
            .register("10GASlug"));

    public static BulletConfig g10_explosive = new BulletConfig()
            .setCasing(BUCKSHOT_ADVANCED, 4)
            .setWear(3F)
            .setProjectiles(10)
            .setDamage(1F/4F)
            .setRenderer(LegoClient.RENDER_HE_BULLET)
            .setSpread(buckshotSpread)
            .setCasing(new SpentCasing(SpentCasing.CasingType.SHOTGUN)
            .setColor(0xFAC943, SpentCasing.COLOR_CASE_12GA)
            .setScale(1F)
            .register("10GAEXP"))
            .setOnImpact(LAMBDA_TINY_EXPLODE);

    public static BulletConfig p9_sp = new BulletConfig()
            .setCasing(SMALL, 12)
            .setDamage(1.0F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(pistolSpread)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
            .setColor(SpentCasing.COLOR_CASE_BRASS)
            .setScale(1F, 1F, 0.75F)
            .register("P9_SP"));

    public static BulletConfig p9_fmj = new BulletConfig()
            .setCasing(SMALL, 12)
            .setDamage(0.8F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setThresholdNegation(2F)
            .setArmorPiercing(0.1F)
            .setSpread(pistolSpread)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(1F, 1F, 0.75F)
                    .register("P9_FMJ"));

    public static BulletConfig p9_jhp = new BulletConfig()
            .setCasing(SMALL, 12)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setHeadshot(1.5F)
            .setArmorPiercing(-0.25F)
            .setSpread(pistolSpread)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(1F, 1F, 0.75F)
                    .register("P9_JHP"));

    public static BulletConfig p9_ap = new BulletConfig()
            .setCasing(SMALL_STEEL, 12)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.25F)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setThresholdNegation(5F)
            .setArmorPiercing(0.15F)
            .setSpread(pistolSpread * 0.8F)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(1F, 1F, 0.75F)
                    .register("P9_AP"));

    public static BulletConfig p22_sp = new BulletConfig()
            .setCasing(SMALL, 24)
            .setKnockback(0F)
            .setDamage(1.0F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(pistolSpread * 0.5F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.5F)
                    .register("p22"));

    public static BulletConfig p22_fmj = new BulletConfig()
            .setCasing(SMALL, 24)
            .setKnockback(0F)
            .setDamage(0.8F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setThresholdNegation(1F)
            .setArmorPiercing(0.1F)
            .setSpread(pistolSpread * 0.5F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.5F)
                    .register("p22fmj"));

    public static BulletConfig p22_jhp = new BulletConfig()
            .setCasing(SMALL, 24)
            .setKnockback(0F)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setHeadshot(1.5F)
            .setArmorPiercing(-0.25F)
            .setSpread(pistolSpread * 0.5F)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.5F)
                    .register("p22jhp"));

    public static BulletConfig p22_ap = new BulletConfig()
            .setCasing(SMALL_STEEL, 24)
            .setKnockback(0F)
            .setDoesPenetrate(true)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setDamageFalloffByPen(false)
            .setDamage(1.25F)
            .setThresholdNegation(2.5F)
            .setArmorPiercing(0.15F)
            .setSpread(pistolSpread * 0.5F * 0.8F)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(0.5F)
                    .register("p22ap"));

    public static BulletConfig m357_bp = new BulletConfig()
            .setCasing(SMALL, 16)
            .setBlackPowder(true)
            .setDamage(0.75F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(pistolSpread * 0.6F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.7F)
                    .register("M357_BP"));

    public static BulletConfig m357_sp = new BulletConfig()
            .setCasing(SMALL, 8)
            .setDamage(1.0F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(pistolSpread * 0.6F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.7F)
                    .register("M357_SP"));

    public static BulletConfig m357_fmj = new BulletConfig()
            .setCasing(SMALL, 8)
            .setDamage(0.8F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setThresholdNegation(2F)
            .setArmorPiercing(0.1F)
            .setSpread(pistolSpread * 0.6F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.7F)
                    .register("M357_FMJ"));

    public static BulletConfig m357_jhp = new BulletConfig()
            .setCasing(SMALL, 8)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setHeadshot(1.5F)
            .setArmorPiercing(-0.25F)
            .setSpread(pistolSpread * 0.6F)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.7F)
                    .register("M357_JHP"));

    public static BulletConfig m357_ap = new BulletConfig()
            .setCasing(SMALL_STEEL, 8)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.25F)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setThresholdNegation(5F)
            .setArmorPiercing(0.15F)
            .setSpread(pistolSpread * 0.6F * 0.8F)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(0.7F)
                    .register("M357_AP"));

    public static BulletConfig m357_express = new BulletConfig()
            .setCasing(SMALL, 8)
            .setDoesPenetrate(true)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_EXPRESS_BULLET)
            .setThresholdNegation(2F)
            .setArmorPiercing(0.1F)
            .setWear(1.5F)
            .setSpread(pistolSpread * 0.6F * 0.9F)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.7F)
                    .register("M357_EXPRESS"));

    public static BulletConfig m44_bp = new BulletConfig()
            .setCasing(SMALL, 12)
            .setBlackPowder(true)
            .setDamage(0.75F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(pistolSpread * 0.7F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.8F)
                    .register("m44bp"));

    public static BulletConfig m44_sp = new BulletConfig()
            .setCasing(SMALL, 6)
            .setDamage(1.0F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(pistolSpread * 0.7F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.8F)
                    .register("m44"));

    public static BulletConfig m44_fmj = new BulletConfig()
            .setCasing(SMALL, 6)
            .setDamage(0.8F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setThresholdNegation(3F)
            .setArmorPiercing(0.1F)
            .setSpread(pistolSpread * 0.7F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.8F)
                    .register("m44fmj"));

    public static BulletConfig m44_jhp = new BulletConfig()
            .setCasing(SMALL, 6)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setHeadshot(1.5F)
            .setArmorPiercing(-0.25F)
            .setSpread(pistolSpread * 0.7F)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.8F)
                    .register("m44jhp"));

    public static BulletConfig m44_ap = new BulletConfig()
            .setCasing(SMALL_STEEL, 6)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.25F)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setThresholdNegation(7.5F)
            .setArmorPiercing(0.15F)
            .setSpread(pistolSpread * 0.7F * 0.8F)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(0.8F)
                    .register("m44ap"));

    public static BulletConfig m44_express = new BulletConfig()
            .setCasing(SMALL, 6)
            .setDoesPenetrate(true)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_EXPRESS_BULLET)
            .setThresholdNegation(3F)
            .setArmorPiercing(0.1F)
            .setWear(1.5F)
            .setSpread(pistolSpread * 0.7F * 0.9F)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
            .setColor(SpentCasing.COLOR_CASE_BRASS)
            .setScale(0.8F)
            .register("m44express")
    );

    public static BulletConfig bmg50_sp = new BulletConfig()
            .setCasing(LARGE, 12)
            .setDamage(1.0F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(pistolSpread * 0.3F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(1.5F)
                    .register("bmg50"));

    public static BulletConfig bmg50_fmj = new BulletConfig()
            .setCasing(LARGE, 12)
            .setDamage(0.8F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setThresholdNegation(7F)
            .setArmorPiercing(0.1F)
            .setSpread(pistolSpread * 0.3F)
            .setRicochetAngle(5)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(1.5F)
                    .register("bmg50fmj"));

    public static BulletConfig bmg50_jhp = new BulletConfig()
            .setCasing(LARGE, 12)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setHeadshot(1.5F)
            .setArmorPiercing(-0.25F)
            .setSpread(pistolSpread * 0.3F)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(1.5F)
                    .register("bmg50jhp"));

    public static BulletConfig bmg50_ap = new BulletConfig()
            .setCasing(LARGE_STEEL, 12)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.25F)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setThresholdNegation(17.5F)
            .setArmorPiercing(0.15F)
            .setSpread(pistolSpread * 0.3F * 0.8F)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(1.5F)
                    .register("bmg50ap"));

    public static BulletConfig bmg50_du = new BulletConfig()
            .setCasing(LARGE_STEEL, 12)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_DU_BULLET)
            .setThresholdNegation(21F)
            .setArmorPiercing(0.25F)
            .setSpread(pistolSpread * 0.3F * 0.8F)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(1.5F)
                    .register("bmg50du"));

    public static BulletConfig bmg50_he = new BulletConfig()
            .setCasing(LARGE_STEEL, 12)
            .setWear(3F)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.75F)
            .setRenderer(LegoClient.RENDER_HE_BULLET)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE)
            .setSpread(pistolSpread * 0.3F)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(1.5F)
                    .register("bmg50he"));

    public static BulletConfig bmg50_sm = new BulletConfig()
            .setCasing(LARGE_STEEL, 6)
            .setWear(10F)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(2.5F)
            .setRenderer(LegoClient.RENDER_SM_BULLET)
            .setThresholdNegation(30F)
            .setArmorPiercing(0.35F)
            .setSpread(pistolSpread * 0.3F * 0.7F)
            .setRicochetAngle(2)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(1.5F)
                    .register("bmg50sm"));

    public static BulletConfig r556_sp = new BulletConfig()
            .setCasing(SMALL, 8)
            .setDamage(1.0F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(rifleSpread)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.8F)
                    .register("r556"));

    public static BulletConfig r556_fmj = new BulletConfig()
            .setCasing(SMALL, 8)
            .setDamage(0.8F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setThresholdNegation(4F)
            .setArmorPiercing(0.1F)
            .setSpread(rifleSpread)
            .setRicochetAngle(3)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.8F)
                    .register("r556fmj"));

    public static BulletConfig r556_jhp = new BulletConfig()
            .setCasing(SMALL, 8)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setHeadshot(1.5F)
            .setArmorPiercing(-0.25F)
            .setSpread(rifleSpread)
            .setRicochetAngle(15)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(0.8F)
                    .register("r556jhp"));

    public static BulletConfig r556_ap = new BulletConfig()
            .setCasing(SMALL_STEEL, 8)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.25F)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setThresholdNegation(10F)
            .setArmorPiercing(0.15F)
            .setSpread(rifleSpread * 0.8F)
            .setRicochetAngle(2)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(SpentCasing.COLOR_CASE_44)
                    .setScale(0.8F)
                    .register("r556ap"));

    public static BulletConfig r556_inc_sp = r556_sp.clone()
            .setOnImpact(LAMBDA_INCENDIARY)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(0xFF4500)
                    .setScale(0.8F)
                    .register("r556inc"));

    public static BulletConfig r556_inc_fmj = r556_fmj.clone()
            .setOnImpact(LAMBDA_INCENDIARY)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(0xFF4500)
                    .setScale(0.8F)
                    .register("r556incfmj"));

    public static BulletConfig r556_inc_jhp = r556_jhp.clone()
            .setOnImpact(LAMBDA_INCENDIARY)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(0xFF4500)
                    .setScale(0.8F)
                    .register("r556incjhp"));

    public static BulletConfig r556_inc_ap = r556_ap.clone()
            .setOnImpact(LAMBDA_INCENDIARY)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setCasing(new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
                    .setColor(0xDA4127)
                    .setScale(0.8F)
                    .register("r556incap"));

    public static BulletConfig b75 = new BulletConfig()
            .setCasing(LARGE, 2)
            .setDamage(1.0F)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setSpread(0.05F)
            .setRicochetAngle(45)
            .setVel(0.5F)
            .setWear(2F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(2F, 2F, 1.5F)
                    .register("b75"))
            .setOnImpact(XFactory75Bolt.LAMBDA_TINY_EXPLODE);

    public static BulletConfig b75_inc = new BulletConfig()
            .setCasing(LARGE, 2)
            .setDamage(0.8F)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setArmorPiercing(0.1F)
            .setSpread(0.05F)
            .setRicochetAngle(45)
            .setGrav(0.05F)
            .setWear(2F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(2F, 2F, 1.5F)
                    .register("b75inc"))
            .setOnImpact(XFactory75Bolt.LAMBDA_INC);

    public static BulletConfig b75_exp = new BulletConfig()
            .setCasing(LARGE, 2)
            .setDamage(1.5F)
            .setRenderer(LegoClient.RENDER_EXPRESS_BULLET)
            .setArmorPiercing(-0.25F)
            .setSpread(0.05F)
            .setRicochetAngle(45)
            .setVel(0.5F)
            .setWear(3F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
                    .setColor(SpentCasing.COLOR_CASE_BRASS)
                    .setScale(2F, 2F, 1.5F)
                    .register("b75exp"))
            .setOnImpact(XFactory75Bolt.LAMBDA_STANDARD_EXPLODE);

    public static final SpentCasing CASING_762 = new SpentCasing(SpentCasing.CasingType.BOTTLENECK)
            .setColor(SpentCasing.COLOR_CASE_BRASS, SpentCasing.COLOR_CASE_BRASS)
            .setScale(0.6F);

    public static BulletConfig r762_sp = new BulletConfig()
            .setCasing(SMALL, 6)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setDamage(0.75F)
            .setSpread(0.01F)
            .setRicochetAngle(25)
            .setThresholdNegation(3F)
            .setCasing(CASING_762.clone().register("r762"));

    public static BulletConfig r762_fmj = new BulletConfig()
            .setCasing(SMALL, 6)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setDamage(0.8F)
            .setSpread(0.01F)
            .setRicochetAngle(25)
            .setThresholdNegation(5F)
            .setArmorPiercing(0.1F)
            .setCasing(CASING_762.clone().register("r762fmj"));

    public static BulletConfig r762_jhp = new BulletConfig()
            .setCasing(SMALL, 6)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setDamage(1.5F)
            .setHeadshot(1.5F)
            .setSpread(0.01F)
            .setRicochetAngle(25)
            .setArmorPiercing(-0.25F)
            .setCasing(CASING_762.clone().register("r762jhp"));

    public static BulletConfig r762_ap = new BulletConfig()
            .setCasing(SMALL_STEEL, 6)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.25F)
            .setSpread(0.01F)
            .setRicochetAngle(25)
            .setThresholdNegation(12.5F)
            .setArmorPiercing(0.15F)
            .setCasing(CASING_762.clone()
                    .setColor(SpentCasing.COLOR_CASE_44, SpentCasing.COLOR_CASE_BRASS)
                    .register("r762ap"));

    public static BulletConfig r762_du = new BulletConfig()
            .setCasing(SMALL_STEEL, 6)
            .setRenderer(LegoClient.RENDER_DU_BULLET)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setDamage(1.5F)
            .setSpread(0.01F)
            .setRicochetAngle(25)
            .setThresholdNegation(15F)
            .setArmorPiercing(0.25F)
            .setCasing(CASING_762.clone()
                    .setColor(SpentCasing.COLOR_CASE_44, SpentCasing.COLOR_CASE_BRASS)
                    .register("r762du"));

    public static BulletConfig r762_he = new BulletConfig()
            .setCasing(SMALL_STEEL, 6)
            .setRenderer(LegoClient.RENDER_EXPRESS_BULLET)
            .setWear(3F)
            .setDamage(1.75F)
            .setSpread(0.01F)
            .setRicochetAngle(25)
            .setOnImpact(XFactory762mm.LAMBDA_TINY_EXPLODE)
            .setCasing(CASING_762.clone()
                    .setColor(SpentCasing.COLOR_CASE_44, SpentCasing.COLOR_CASE_BRASS)
                    .register("r762he"));

    public static BulletConfig energy_lacunae = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 4 * 40)
            .setRendererBeam(LegoClient.RENDER_LASER_PURPLE)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setReloadCount(40)
            .setSpread(0.0F)
            .setLife(5)
            .setRenderRotations(false)
            .setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);

    public static BulletConfig energy_lacunae_overcharge = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 4 * 40)
            .setRendererBeam(LegoClient.RENDER_LASER_PURPLE)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setReloadCount(40)
            .setSpread(0.0F)
            .setLife(5)
            .setRenderRotations(false)
            .setDoesPenetrate(true)
            .setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);

    public static BulletConfig energy_lacunae_ir = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 4 * 40)
            .setRendererBeam(LegoClient.RENDER_LASER_PURPLE)
            .setupDamageClass(BulletConfig.DamageClass.FIRE)
            .setBeam()
            .setReloadCount(40)
            .setSpread(0.0F)
            .setLife(5)
            .setRenderRotations(false)
            .setOnBeamImpact(LAMBDA_IR_HIT);

    public static BulletConfig p35800 = new BulletConfig()
            .setArmorPiercing(0.5F).setThresholdNegation(50F).setBeam().setSpread(0.0F).setLife(3).setRenderRotations(false)
            .setRendererBeam(LegoClient.RENDER_CRACKLE_SIMPLE)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
            .setColor(0xCEB78E).register("35-800"))
            .setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);

    public static BulletConfig p35800_bl = new BulletConfig()
            .setArmorPiercing(0.5F).setThresholdNegation(50F).setBeam().setSpread(0.0F).setLife(3).setRenderRotations(false)
            .setRendererBeam(LegoClient.RENDER_BLACK_LIGHTNING)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
            .setColor(0xCEB78E).register("35-800"))
            .setOnBeamImpact(XFactory35800.LAMBDA_BLACK_IMPACT);

    public static BulletConfig energy_las = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 4)
            .setRendererBeam(LegoClient.RENDER_LASER_RED)
            .setupDamageClass(BulletConfig.DamageClass.LASER)
            .setBeam()
            .setSpread(0.0F)
            .setLife(5)
            .setRenderRotations(false)
            .setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);

    public static BulletConfig energy_las_overcharge = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 4)
            .setRendererBeam(LegoClient.RENDER_LASER_RED)
            .setupDamageClass(BulletConfig.DamageClass.LASER).setBeam()
            .setSpread(0.0F).setLife(5).setRenderRotations(false)
            .setDoesPenetrate(true)
            .setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);

    public static BulletConfig energy_las_ir = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 4)
            .setRendererBeam(LegoClient.RENDER_LASER_RED)
            .setupDamageClass(BulletConfig.DamageClass.FIRE).setBeam()
            .setSpread(0.0F).setLife(5).setRenderRotations(false)
            .setDoesPenetrate(true)
            .setOnBeamImpact(LAMBDA_IR_HIT);

    public static BulletConfig energy_emerald = energy_las.clone()
            .setArmorPiercing(0.5F)
            .setRendererBeam(LegoClient.RENDER_LASER_EMERALD)
            .setThresholdNegation(10F);
    public static BulletConfig energy_emerald_overcharge = energy_las_overcharge.clone()
            .setArmorPiercing(0.5F)
            .setRendererBeam(LegoClient.RENDER_LASER_EMERALD)
            .setThresholdNegation(15F);
    public static BulletConfig energy_emerald_ir = energy_las_ir.clone()
            .setArmorPiercing(0.5F)
            .setRendererBeam(LegoClient.RENDER_LASER_EMERALD)
            .setThresholdNegation(10F);

    public static BulletConfig energy_tesla = new BulletConfig()
            .setItem(GunFactory.EnumAmmo.CAPACITOR)
            .setRendererBeam(LegoClient.RENDER_LIGHTNING)
            .setCasing(ItemStack.EMPTY, 4)
            .setupDamageClass(BulletConfig.DamageClass.ELECTRIC).setBeam()
            .setSpread(0.0F).setLife(5).setRenderRotations(false).setDoesPenetrate(true)
            .setOnBeamImpact(XFactoryEnergy.LAMBDA_LIGHTNING_HIT);

    public static BulletConfig energy_tesla_overcharge = new BulletConfig()
            .setItem(GunFactory.EnumAmmo.CAPACITOR_OVERCHARGE)
            .setRendererBeam(LegoClient.RENDER_LIGHTNING)
            .setCasing(ItemStack.EMPTY, 4)
            .setupDamageClass(BulletConfig.DamageClass.ELECTRIC).setBeam()
            .setSpread(0.0F).setLife(5).setRenderRotations(false).setDoesPenetrate(true)
            .setDamage(1.5F).setOnBeamImpact(XFactoryEnergy.LAMBDA_LIGHTNING_HIT);

    public static BulletConfig energy_tesla_ir = new BulletConfig()
            .setItem(GunFactory.EnumAmmo.CAPACITOR_IR)
            .setRendererBeam(LegoClient.RENDER_LIGHTNING)
            .setCasing(ItemStack.EMPTY, 4)
            .setupDamageClass(BulletConfig.DamageClass.ELECTRIC).setBeam().setSpread(0.0F).setLife(5).setRenderRotations(false)
            .setDamage(0.8F).setOnBeamImpact(XFactoryEnergy.LAMBDA_LIGHTNING_SPLIT);

    public static BulletConfig energy_tesla_ir_sub = new BulletConfig()
            .setItem(GunFactory.EnumAmmo.CAPACITOR_IR)
            .setRendererBeam(LegoClient.RENDER_LIGHTNING_SUB)
            .setupDamageClass(BulletConfig.DamageClass.ELECTRIC).setBeam()
            .setSpread(0.0F).setLife(3).setWear(3F).setRenderRotations(false).setDoesPenetrate(true)
            .setDamage(0.5F).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);

    public static BulletConfig g26_flare = new BulletConfig()
            .setCasing(HBMEnums.EnumCasingType.LARGE, 4).setLife(100).setVel(2F).setGrav(0.015D)
            .setRenderer(LegoClient.RENDER_FLARE)
            .setRenderRotations(false).setOnImpact(XFactory40mm.LAMBDA_STANDARD_IGNITE)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT).setColor(0x9E1616).setScale(2F)
            .register("g26Flare"));

    public static BulletConfig g26_flare_supply = new BulletConfig()
            .setCasing(HBMEnums.EnumCasingType.LARGE, 4).setLife(100).setVel(2F).setGrav(0.015D)
            .setRenderer(LegoClient.RENDER_FLARE_SUPPLY)
            .setRenderRotations(false).setOnImpact(XFactory40mm.LAMBDA_STANDARD_IGNITE)
            .setOnUpdate(XFactory40mm.LAMBDA_SPAWN_C130_SUPPLIESS).setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT).setColor(0x3C80F0).setScale(2F)
            .register("g26FlareSupply"));

    public static BulletConfig g26_flare_weapon = new BulletConfig()
            .setCasing(HBMEnums.EnumCasingType.LARGE, 4).setLife(100).setVel(2F).setGrav(0.015D)
            .setRenderer(LegoClient.RENDER_FLARE_WEAPON)
            .setRenderRotations(false).setOnImpact(XFactory40mm.LAMBDA_STANDARD_IGNITE).setOnUpdate(XFactory40mm.LAMBDA_SPAWN_C130_WEAPONS)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT).setColor(0x278400).setScale(2F)
            .register("g26FlareWeapon"));

    public static BulletConfig g40_base = new BulletConfig()
            .setLife(200).setVel(2F)
            .setGrav(0.035D)
            .setRenderer(LegoClient.RENDER_GRENADE);

    public static BulletConfig g40_he = g40_base.clone()
            .setCasing(HBMEnums.EnumCasingType.LARGE, 4)
            .setOnImpact(XFactory40mm.LAMBDA_STANDARD_EXPLODE).setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT)
            .setColor(0x777777).setScale(2, 2F, 1.5F)
            .register("g40"));

    public static BulletConfig g40_heat = g40_base.clone()
            .setCasing(HBMEnums.EnumCasingType.LARGE, 4)
            .setOnImpact(XFactory40mm.LAMBDA_STANDARD_EXPLODE_HEAT).setDamage(0.5F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT).setColor(0x5E6854).setScale(2, 2F, 1.5F)
            .register("g40heat"));

    public static BulletConfig g40_demo = g40_base.clone()
            .setCasing(HBMEnums.EnumCasingType.LARGE, 4)
            .setOnImpact(XFactory40mm.LAMBDA_STANDARD_EXPLODE_DEMO).setDamage(0.75F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT).setColor(0xE30000).setScale(2, 2F, 1.5F)
            .register("g40demo"));

    public static BulletConfig g40_inc = g40_base.clone()
            .setCasing(HBMEnums.EnumCasingType.LARGE, 4)
            .setOnImpact(XFactory40mm.LAMBDA_STANDARD_EXPLODE_INC).setDamage(0.75F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT).setColor(0xE86F20).setScale(2, 2F, 1.5F)
            .register("g40inc"));

    public static BulletConfig g40_phosphorus = g40_base.clone()
            .setCasing(HBMEnums.EnumCasingType.LARGE, 4)
            .setOnImpact(XFactory40mm.LAMBDA_STANDARD_EXPLODE_PHOSPHORUS).setDamage(0.75F)
            .setCasing(new SpentCasing(SpentCasing.CasingType.STRAIGHT).setColor(0xC8C8C8).setScale(2, 2F, 1.5F)
            .register("g40phos"));


    static SpentCasing casing9 = new SpentCasing(SpentCasing.CasingType.STRAIGHT).setColor(SpentCasing.COLOR_CASE_BRASS).setScale(1F, 1F, 0.75F);

    public static BulletConfig p45_sp = new BulletConfig()
            .setCasing(HBMEnums.EnumCasingType.SMALL, 8)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setCasing(casing9.clone().register("p45"));

    public static BulletConfig p45_fmj = new BulletConfig()
            .setCasing(HBMEnums.EnumCasingType.SMALL, 8)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setDamage(0.8F).setThresholdNegation(2F).setArmorPiercing(0.1F)
            .setCasing(casing9.clone().register("p45fmj"));

    public static BulletConfig p45_jhp = new BulletConfig()
            .setCasing(HBMEnums.EnumCasingType.SMALL, 8)
            .setRenderer(LegoClient.RENDER_STANDARD_BULLET)
            .setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F)
            .setCasing(casing9.clone().register("p45jhp"));

    public static BulletConfig p45_ap = new BulletConfig()
            .setCasing(HBMEnums.EnumCasingType.SMALL_STEEL, 8).setDoesPenetrate(true)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(5F).setArmorPiercing(0.15F)
            .setCasing(casing9.clone().setColor(SpentCasing.COLOR_CASE_44).register("p45ap"));

    public static BulletConfig p45_du = new BulletConfig()
            .setCasing(HBMEnums.EnumCasingType.SMALL_STEEL, 8).setDoesPenetrate(true)
            .setRenderer(LegoClient.RENDER_DU_BULLET)
            .setDamageFalloffByPen(false).setDamage(2.5F).setThresholdNegation(15F).setArmorPiercing(0.25F)
            .setCasing(casing9.clone().setColor(SpentCasing.COLOR_CASE_44).register("p45du"));

    public static BulletConfig tau_uranium = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 16)
            .setupDamageClass(BulletConfig.DamageClass.SUBATOMIC).setBeam().setLife(5)
            .setRendererBeam(LegoClient.RENDER_TAU)
            .setRenderRotations(false).setDoesPenetrate(true).setDamageFalloffByPen(false)
            .setOnBeamImpact(BulletConfig.LAMBDA_BEAM_HIT);

    public static BulletConfig tau_uranium_charge = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 16)
            .setupDamageClass(BulletConfig.DamageClass.SUBATOMIC).setBeam().setLife(5)
            .setRendererBeam(LegoClient.RENDER_TAU_CHARGE)
            .setRenderRotations(false).setDoesPenetrate(true).setDamageFalloffByPen(false).setSpectral(true)
            .setOnBeamImpact(BulletConfig.LAMBDA_BEAM_HIT);

    public static BulletConfig coil_tungsten = new BulletConfig()
            .setVel(7.5F).setLife(50)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setDoesPenetrate(true).setDamageFalloffByPen(false).setSpectral(true)
            .setOnUpdate(XFactoryAccelerator.LAMBDA_UPDATE_TUNGSTEN);

    public static BulletConfig coil_ferrouranium = new BulletConfig()
            .setVel(7.5F).setLife(50)
            .setRenderer(LegoClient.RENDER_AP_BULLET)
            .setDoesPenetrate(true).setDamageFalloffByPen(false).setSpectral(true)
            .setOnUpdate(XFactoryAccelerator.LAMBDA_UPDATE_FERRO);

    public static BulletConfig ni4ni_arc = new BulletConfig()
            .setupDamageClass(BulletConfig.DamageClass.PHYSICAL)
            .setBeam().setLife(5).setThresholdNegation(10F).setArmorPiercing(0.2F)
            .setRendererBeam(LegoClient.RENDER_NI4NI_BOLT)
            .setRenderRotations(false).setDoesPenetrate(false)
            .setOnBeamImpact(BulletConfig.LAMBDA_BEAM_HIT);

    public static BulletConfig flame_diesel = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 500)
            .setupDamageClass(BulletConfig.DamageClass.FIRE).setLife(100).setVel(1F).setGrav(0.02D)
            .setReloadCount(500).setSelfDamageDelay(20).setKnockback(0F)
            .setOnUpdate(XFactoryFlamer.LAMBDA_FIRE).setOnRicochet(XFactoryFlamer.LAMBDA_LINGER_DIESEL);

    public static BulletConfig flame_gas = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 500)
            .setupDamageClass(BulletConfig.DamageClass.FIRE).setLife(10).setSpread(0.05F).setVel(1F).setGrav(0.0D)
            .setReloadCount(500).setSelfDamageDelay(20).setKnockback(0F)
            .setOnUpdate(XFactoryFlamer.LAMBDA_FIRE).setOnRicochet(XFactoryFlamer.LAMBDA_LINGER_GAS);

    public static BulletConfig flame_napalm = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 500)
            .setupDamageClass(BulletConfig.DamageClass.FIRE).setLife(200).setVel(1F).setGrav(0.02D)
            .setReloadCount(500).setSelfDamageDelay(20).setKnockback(0F)
            .setOnUpdate(XFactoryFlamer.LAMBDA_FIRE).setOnRicochet(XFactoryFlamer.LAMBDA_LINGER_NAPALM);

    public static BulletConfig flame_balefire = new BulletConfig()
            .setCasing(ItemStack.EMPTY, 500)
            .setupDamageClass(BulletConfig.DamageClass.FIRE).setLife(200).setVel(1F).setGrav(0.02D)
            .setReloadCount(500).setSelfDamageDelay(20).setKnockback(0F)
            .setOnUpdate(XFactoryFlamer.LAMBDA_BALEFIRE).setOnRicochet(XFactoryFlamer.LAMBDA_LINGER_BALEFIRE);

    public static BulletConfig flame_topaz_diesel = flame_diesel.clone().setProjectiles(2).setSpread(0.05F).setLife(60).setGrav(0.0D);
    public static BulletConfig flame_topaz_gas = flame_gas.clone().setProjectiles(2).setSpread(0.05F);
    public static BulletConfig flame_topaz_napalm = flame_napalm.clone().setProjectiles(2).setSpread(0.05F).setLife(60).setGrav(0.0D);
    public static BulletConfig flame_topaz_balefire = flame_balefire.clone().setProjectiles(2).setSpread(0.05F).setLife(60).setGrav(0.0D);

    public static BulletConfig flame_daybreaker_diesel = flame_diesel.clone().setLife(200).setVel(2F).setGrav(0.035D)
				.setOnImpact((bullet, mop) -> { Lego.standardExplode(bullet, mop, 5F); XFactoryFlamer.spawnFire(bullet, mop, 6F, 2F, 200, EntityFireLingering.TYPE_DIESEL); bullet.discard(); });
    public static BulletConfig flame_daybreaker_gas = flame_gas.clone().setLife(200).setVel(2F).setGrav(0.035D)
				.setOnImpact((bullet, mop) -> { Lego.standardExplode(bullet, mop, 5F); bullet.discard(); });
    public static BulletConfig flame_daybreaker_napalm = flame_napalm.clone().setLife(200).setVel(2F).setGrav(0.035D)
				.setOnImpact((bullet, mop) -> { Lego.standardExplode(bullet, mop, 7.5F); XFactoryFlamer.spawnFire(bullet, mop, 6F, 2F, 300, EntityFireLingering.TYPE_DIESEL); bullet.discard(); });
    public static BulletConfig flame_daybreaker_balefire = flame_balefire.clone().setLife(200).setVel(2F).setGrav(0.035D)
				.setOnImpact((bullet, mop) -> { Lego.standardExplode(bullet, mop, 5F); XFactoryFlamer.spawnFire(bullet, mop, 7.5F, 2.5F, 400, EntityFireLingering.TYPE_BALEFIRE); bullet.discard(); });

    public static BulletConfig rocket_rpzb_he = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_HE)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE)
            .setRenderer(LegoClient.RENDER_RPZB);

    public static BulletConfig rocket_rpzb_heat = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_HEAT)
            .setDamage(0.5F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_HEAT)
            .setRenderer(LegoClient.RENDER_RPZB);

    public static BulletConfig rocket_rpzb_demo = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_DEMO)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_DEMO)
            .setRenderer(LegoClient.RENDER_RPZB);

    public static BulletConfig rocket_rpzb_inc = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_INC)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_INC)
            .setRenderer(LegoClient.RENDER_RPZB);

    public static BulletConfig rocket_rpzb_phosphorus = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_PHOSPHORUS)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_PHOSPHORUS)
            .setRenderer(LegoClient.RENDER_RPZB);

    public static BulletConfig rocket_qd_he = new BulletConfig()
            .setLife(400)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STEERING_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_HE)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE)
            .setRenderer(LegoClient.RENDER_QD);

    public static BulletConfig rocket_qd_heat = new BulletConfig()
            .setLife(400)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STEERING_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_HEAT)
            .setDamage(0.5F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_HEAT)
            .setRenderer(LegoClient.RENDER_QD);

    public static BulletConfig rocket_qd_demo = new BulletConfig()
            .setLife(400)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STEERING_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_DEMO)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_DEMO)
            .setRenderer(LegoClient.RENDER_QD);

    public static BulletConfig rocket_qd_inc = new BulletConfig()
            .setLife(400)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STEERING_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_INC)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_INC)
            .setRenderer(LegoClient.RENDER_QD);

    public static BulletConfig rocket_qd_phosphorus = new BulletConfig()
            .setLife(400)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STEERING_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_PHOSPHORUS)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_PHOSPHORUS)
            .setRenderer(LegoClient.RENDER_QD);

    public static BulletConfig rocket_ml_he = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_HE)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE)
            .setRenderer(LegoClient.RENDER_ML);

    public static BulletConfig rocket_ml_heat = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_HEAT)
            .setDamage(0.5F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_HEAT)
            .setRenderer(LegoClient.RENDER_ML);

    public static BulletConfig rocket_ml_demo = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_DEMO)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_DEMO)
            .setRenderer(LegoClient.RENDER_ML);

    public static BulletConfig rocket_ml_inc = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_INC)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_INC)
            .setRenderer(LegoClient.RENDER_ML);

    public static BulletConfig rocket_ml_phosphorus = new BulletConfig()
            .setLife(300)
            .setSelfDamageDelay(10)
            .setVel(0.01F)
            .setGrav(0D)
            .setOnEntityHit(null)
            .setOnRicochet(null)
            .setOnUpdate(XFactoryRocket.LAMBDA_STANDARD_ACCELERATE)
            .setItem(GunFactory.EnumAmmo.ROCKET_PHOSPHORUS)
            .setDamage(0.75F)
            .setOnImpact(LAMBDA_STANDARD_EXPLODE_PHOSPHORUS)
            .setRenderer(LegoClient.RENDER_ML);

    public static BulletConfig nuke_standard = new BulletConfig()
            .setLife(300).setVel(3F)
            .setGrav(0.025F)
            .setRenderer(LegoClient.RENDER_NUKE)
            .setOnImpact(LAMBDA_NUKE_STANDARD);

    public static BulletConfig nuke_demo = new BulletConfig()
            .setLife(300).setVel(3F)
            .setGrav(0.025F)
            .setRenderer(LegoClient.RENDER_NUKE)
            .setOnImpact(LAMBDA_NUKE_DEMO);

    public static BulletConfig nuke_high = new BulletConfig()
            .setLife(300)
            .setVel(3F)
            .setGrav(0.025F)
            .setRenderer(LegoClient.RENDER_NUKE)
            .setOnImpact(LAMBDA_NUKE_HIGH);

    public static BulletConfig nuke_tots = new BulletConfig()
            .setProjectiles(8)
            .setLife(300)
            .setVel(3F)
            .setGrav(0.025F)
            .setSpread(0.1F)
            .setDamage(0.35F)
            .setRenderer(LegoClient.RENDER_NUKE)
            .setOnImpact(LAMBDA_NUKE_TINYTOT);

    public static BulletConfig nuke_hive = new BulletConfig()
            .setProjectiles(12)
            .setLife(300)
            .setVel(1F)
            .setGrav(0.025F)
            .setSpread(0.15F)
            .setDamage(0.25F)
            .setRenderer(LegoClient.RENDER_HIVE)
            .setOnImpact(LAMBDA_NUKE_HIVE);

    public static BulletConfig nuke_balefire = new BulletConfig()
            .setDamage(2.5F)
            .setLife(300)
            .setVel(3F)
            .setGrav(0.025F)
            .setRenderer(LegoClient.RENDER_NUKE_BALEFIRE)
            .setOnImpact(LAMBDA_NUKE_BALEFIRE);

    public static BulletConfig folly_sm = new BulletConfig()
            .setupDamageClass(BulletConfig.DamageClass.SUBATOMIC)
            .setRendererBeam(LegoClient.RENDER_FOLLY)
            .setBeam().setLife(100).setVel(2F).setGrav(0.015D)
            .setRenderRotations(false).setSpectral(true)
            .setDoesPenetrate(true)
            .setOnUpdate(XFactoryFolly.LAMBDA_SM_UPDATE);

    public static BulletConfig folly_nuke = new BulletConfig()
            .setChunkloading().setLife(600).setVel(4F).setGrav(0.015D)
            .setRendererBeam(LegoClient.RENDER_FOLLY)
            .setOnImpact(XFactoryFolly.LAMBDA_NUKE_IMPACT)
            .setOnUpdate(XFactoryFolly.LAMBDA_SM_UPDATE);

    public static BulletConfig fext_co2 = new BulletConfig()
            .setReloadCount(300).setLife(100).setVel(0.75F).setGrav(0.04D).setSpread(0.05F)
            .setOnUpdate(XFactoryTool.LAMBDA_CO2_UPDATE)
            .setOnEntityHit(XFactoryTool.EXTINGUISH_ENTITY)
            .setOnRicochet(XFactoryTool.LAMBDA_CO2_HIT);

    public static BulletConfig fext_foam = new BulletConfig()
            .setReloadCount(300).setLife(100).setVel(0.75F).setGrav(0.04D).setSpread(0.05F)
            .setOnUpdate(XFactoryTool.LAMBDA_FOAM_UPDATE)
            .setOnEntityHit(XFactoryTool.EXTINGUISH_ENTITY)
            .setOnRicochet(XFactoryTool.LAMBDA_FOAM_HIT);

    public static BulletConfig fext_sand = new BulletConfig()
            .setReloadCount(300).setLife(100).setVel(0.75F).setGrav(0.04D).setSpread(0.05F)
            .setOnUpdate(XFactoryTool.LAMBDA_SAND_UPDATE)
            .setOnEntityHit(XFactoryTool.EXTINGUISH_ENTITY)
            .setOnRicochet(XFactoryTool.LAMBDA_SAND_HIT);

    public static BulletConfig ct_hook = new BulletConfig()
            .setRenderRotations(false)
            .setRenderer(LegoClient.RENDER_CT_HOOK)
            .setLife(6_000).setVel(3F)
            .setGrav(0.035D)
            .setDoesPenetrate(true)
            .setDamageFalloffByPen(false)
            .setOnUpdate(XFactoryTool.LAMBDA_SET_HOOK)
            .setOnImpact(XFactoryTool.LAMBDA_HOOK);

    public static BulletConfig ct_mortar = new BulletConfig()
            .setDamage(2.5F)
            .setRenderer(LegoClient.RENDER_CT_MORTAR)
            .setLife(200)
            .setVel(3F)
            .setGrav(0.035D)
            .setOnImpact(XFactoryTool.LAMBDA_MORTAR);

    public static BulletConfig ct_mortar_charge = new BulletConfig()
            .setDamage(5F)
            .setRenderer(LegoClient.RENDER_CT_MORTAR_CHARGE)
            .setLife(200)
            .setVel(3F)
            .setGrav(0.035D)
            .setOnImpact(XFactoryTool.LAMBDA_MORTAR_CHARGE);

    //=====================================MOBS==========================================================

    // ==================== MASKMAN ORB ====================
    public static final BulletConfig MASKMAN_ORB = new BulletConfig()
            .setVel(0.25F)
            .setSpread(0.0F)
            .setWear(10F)
            .setProjectiles(1)
            .setDamage(100F)
            .setGrav(0.0D)
            .setLife(60)
            .setRicochetAngle(0.0F)
            .setDoesPenetrate(false)
            .setStyle(STYLE_ORB)
            .setTrail(1)
            .setExplosive(1.5F)
            .setBlockDamage(true)
            .setOnUpdate(XFactoryMobs::updateMaskmanOrb);

    // ==================== MASKMAN BOLT ====================
    public static final BulletConfig MASKMAN_BOLT = new BulletConfig()
            .setSpread(0.0F)
            .setDamageRange(15, 20)
            .setWear(10F)
            .setLeadChance(0)
            .setExplosive(0.5F)
            .setStyle(STYLE_BOLT)
            .setTrail(BOLT_LACUNAE)
            .setVFX("reddust")
            .setupDamageClass(DamageClass.LASER)
            .setLife(40)
            .setRicochetCount(0);

    // ==================== MASKMAN BULLET ====================
    public static final BulletConfig MASKMAN_BULLET = new BulletConfig()

            .setSpread(0.0F)
            .setDamageRange(5, 10)
            .setWear(10F)
            .setLeadChance(15)
            .setStyle(STYLE_FLECHETTE)
            .setVFX("bluedust");

    // ==================== MASKMAN TRACER ====================
    public static final BulletConfig MASKMAN_TRACER = new BulletConfig()

            .setSpread(0.0F)
            .setDamageRange(15, 20)
            .setWear(10F)
            .setLeadChance(0)
            .setStyle(STYLE_BOLT)
            .setTrail(BOLT_NIGHTMARE)
            .setVFX("reddust")
            .setupDamageClass(DamageClass.LASER)
            .setLife(40)
            .setRicochetCount(0)
            .setOnImpact(XFactoryMobs::onMaskmanTracerImpact);

    // ==================== MASKMAN ROCKET ====================
    public static final BulletConfig MASKMAN_ROCKET = new BulletConfig()

            .setGrav(0.1D)
            .setVel(1.0F)
            .setDamageRange(15, 20)
            .setBlockDamage(false)
            .setExplosive(5.0F)
            .setStyle(STYLE_ROCKET)
            .setLife(100)
            .setRicochetCount(0);

    // ==================== MASKMAN METEOR ====================
    public static final BulletConfig MASKMAN_METEOR = new BulletConfig()

            .setGrav(0.1D)
            .setVel(1.0F)
            .setDamageRange(20, 30)
            .setBlockDamage(false)
            .setIncendiary(3)
            .setExplosive(2.5F)
            .setStyle(STYLE_METEOR)
            .setLife(100)
            .setRicochetCount(0)
            .setOnUpdate(XFactoryMobs::updateMaskmanMeteor);

    // ==================== WORM BOLT ====================
    public static final BulletConfig WORM_BOLT = new BulletConfig()

            .setSpread(0.0F)
            .setLife(60)
            .setDamageRange(15, 25)
            .setLeadChance(0)
            .setRicochetAngle(0.0F)
            .setRicochetCount(0)
            .setStyle(STYLE_BOLT)
            .setTrail(BOLT_WORM)
            .setupDamageClass(DamageClass.LASER)
            .setVFX("reddust");

    // ==================== WORM HEAD BOLT ====================
    public static final BulletConfig WORM_HEAD_BOLT = new BulletConfig()

            .setSpread(0.0F)
            .setLife(100)
            .setDamageRange(35, 60)
            .setLeadChance(0)
            .setRicochetAngle(0.0F)
            .setRicochetCount(0)
            .setStyle(STYLE_BOLT)
            .setTrail(BOLT_LASER)
            .setupDamageClass(DamageClass.LASER);

    // ==================== UFO ROCKET (с самонаведением) ====================
    public static final BulletConfig UFO_ROCKET = new BulletConfig()

            .setVFX("reddust")
            .setBlockDamage(false)
            .setExplosive(0F)
            .setStyle(STYLE_ROCKET)
            .setLife(200)
            .setVel(1.5F)
            .setGrav(0.0D)
            .setDamageRange(50, 80)
            .setRicochetCount(0)
            .setDoesPenetrate(false)
            .setOnUpdate(XFactoryMobs::updateUfoRocket)
            .setOnImpact(XFactoryMobs::onUfoRocketImpact);

    // ==================== WORM LASER (для UFO) ====================
    public static final BulletConfig WORM_LASER = new BulletConfig()
            .setupDamageClass(DamageClass.LASER)
            .setBeam()
            .setLife(5)
            .setSpread(0.0F)
            .setRenderRotations(false)
            .setVel(2F)
            .setDamageRange(15, 25)
            .setRendererBeam(LegoClient.RENDER_LASER_RED)
            .setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);

    // ==================== TURBINE (из GunEnergyFactory) ====================
    public static final BulletConfig TURBINE = new BulletConfig()

            .setDamageRange(100, 150)
            .setVel(1.0F)
            .setGrav(0.0D)
            .setLife(200)
            .setStyle(STYLE_BLADE)
            .setBlockDamage(true)
            .setRicochetAngle(0.0F)
            .setRicochetCount(0);

    public static void initLate() {
        energy_lacunae.setCasing(new ItemStack(ModItems.INGOT_POLYMER.get(),2), 4 * 40);
        energy_lacunae_overcharge.setCasing(new ItemStack(ModItems.INGOT_POLYMER.get(),2), 4 * 40);
        energy_lacunae_ir.setCasing(new ItemStack(ModItems.INGOT_POLYMER.get(),2), 4 * 40);
        energy_las.setCasing(new ItemStack(ModItems.INGOT_POLYMER.get(),2), 4);
        energy_las_overcharge.setCasing(new ItemStack(ModItems.INGOT_POLYMER.get(),2), 4);
        energy_las_ir.setCasing(new ItemStack(ModItems.INGOT_POLYMER.get(),2), 4);
        energy_tesla.setCasing(new ItemStack(ModItems.INGOT_POLYMER.get(),2), 4);
        energy_tesla_overcharge.setCasing(new ItemStack(ModItems.INGOT_POLYMER.get(),2), 4);

        flame_diesel.setCasing(new ItemStack(ModItems.PLATE_STEEL.get(),2), 500);
        flame_napalm.setCasing(new ItemStack(ModItems.PLATE_STEEL.get(),2), 500);
        flame_gas.setCasing(new ItemStack(ModItems.PLATE_STEEL.get(),2), 500);
        flame_balefire.setCasing(new ItemStack(ModItems.PLATE_STEEL.get(),2), 500);

        tau_uranium.setCasing(new ItemStack(ModItems.PLATE_LEAD.get(),2), 16);
        tau_uranium_charge.setCasing(new ItemStack(ModItems.PLATE_LEAD.get(),2), 16);
    }

    public static void linkAllItems() {
        linkItem(g12, GunFactory.EnumAmmo.G12);
        linkItem(g12_slug, GunFactory.EnumAmmo.G12_SLUG);
        linkItem(g12_flechette, GunFactory.EnumAmmo.G12_FLECHETTE);
        linkItem(g12_magnum, GunFactory.EnumAmmo.G12_MAGNUM);
        linkItem(g12_explosive, GunFactory.EnumAmmo.G12_EXPLOSIVE);
        linkItem(g12_phosphorus, GunFactory.EnumAmmo.G12_PHOSPHORUS);

        linkItem(g12_shredder, GunFactory.EnumAmmo.G12);
        linkItem(g12_shredder_slug, GunFactory.EnumAmmo.G12_SLUG);
        linkItem(g12_shredder_flechette, GunFactory.EnumAmmo.G12_FLECHETTE);
        linkItem(g12_shredder_magnum, GunFactory.EnumAmmo.G12_MAGNUM);
        linkItem(g12_shredder_explosive, GunFactory.EnumAmmo.G12_EXPLOSIVE);
        linkItem(g12_shredder_phosphorus, GunFactory.EnumAmmo.G12_PHOSPHORUS);

        linkItem(g12_bp, GunFactory.EnumAmmo.G12_BP);
        linkItem(g12_bp_magnum, GunFactory.EnumAmmo.G12_BP_MAGNUM);
        linkItem(g12_bp_slug, GunFactory.EnumAmmo.G12_BP_SLUG);

        linkItem(g10, GunFactory.EnumAmmo.G10);
        linkItem(g10_shrapnel, GunFactory.EnumAmmo.G10_SHRAPNEL);
        linkItem(g10_du, GunFactory.EnumAmmo.G10_DU);
        linkItem(g10_slug, GunFactory.EnumAmmo.G10_SLUG);
        linkItem(g10_explosive, GunFactory.EnumAmmo.G10_EXPLOSIVE);

        linkItem(STONE, GunFactory.EnumAmmo.STONE);
        linkItem(FLINT, GunFactory.EnumAmmo.STONE_AP);
        linkItem(IRON, GunFactory.EnumAmmo.STONE_IRON);
        linkItem(SHOT, GunFactory.EnumAmmo.STONE_SHOT);

        linkItem(p9_sp, GunFactory.EnumAmmo.P9_SP);
        linkItem(p9_fmj, GunFactory.EnumAmmo.P9_FMJ);
        linkItem(p9_jhp, GunFactory.EnumAmmo.P9_JHP);
        linkItem(p9_ap, GunFactory.EnumAmmo.P9_AP);

        linkItem(p22_sp, GunFactory.EnumAmmo.P22_SP);
        linkItem(p22_fmj, GunFactory.EnumAmmo.P22_FMJ);
        linkItem(p22_jhp, GunFactory.EnumAmmo.P22_JHP);
        linkItem(p22_ap, GunFactory.EnumAmmo.P22_AP);

        linkItem(m357_bp, GunFactory.EnumAmmo.M357_BP);
        linkItem(m357_sp, GunFactory.EnumAmmo.M357_SP);
        linkItem(m357_fmj, GunFactory.EnumAmmo.M357_FMJ);
        linkItem(m357_jhp, GunFactory.EnumAmmo.M357_JHP);
        linkItem(m357_ap, GunFactory.EnumAmmo.M357_AP);
        linkItem(m357_express, GunFactory.EnumAmmo.M357_EXPRESS);

        linkItem(m44_bp, GunFactory.EnumAmmo.M44_BP);
        linkItem(m44_sp, GunFactory.EnumAmmo.M44_SP);
        linkItem(m44_fmj, GunFactory.EnumAmmo.M44_FMJ);
        linkItem(m44_jhp, GunFactory.EnumAmmo.M44_JHP);
        linkItem(m44_ap, GunFactory.EnumAmmo.M44_AP);
        linkItem(m44_express, GunFactory.EnumAmmo.M44_EXPRESS);

        linkItem(bmg50_sp, GunFactory.EnumAmmo.BMG50_SP);
        linkItem(bmg50_fmj, GunFactory.EnumAmmo.BMG50_FMJ);
        linkItem(bmg50_jhp, GunFactory.EnumAmmo.BMG50_JHP);
        linkItem(bmg50_ap, GunFactory.EnumAmmo.BMG50_AP);
        linkItem(bmg50_du, GunFactory.EnumAmmo.BMG50_DU);
        linkItem(bmg50_he, GunFactory.EnumAmmo.BMG50_HE);
        linkItem(bmg50_sm, GunFactory.EnumAmmo.BMG50_SM);

        linkItem(r556_sp, GunFactory.EnumAmmo.R556_SP);
        linkItem(r556_fmj, GunFactory.EnumAmmo.R556_FMJ);
        linkItem(r556_jhp, GunFactory.EnumAmmo.R556_JHP);
        linkItem(r556_ap, GunFactory.EnumAmmo.R556_AP);

        linkItem(r556_inc_sp, GunFactory.EnumAmmo.R556_SP);
        linkItem(r556_inc_fmj, GunFactory.EnumAmmo.R556_FMJ);
        linkItem(r556_inc_jhp, GunFactory.EnumAmmo.R556_JHP);
        linkItem(r556_inc_ap, GunFactory.EnumAmmo.R556_AP);

        linkItem(b75, GunFactory.EnumAmmo.B75);
        linkItem(b75_inc, GunFactory.EnumAmmo.B75_INC);
        linkItem(b75_exp, GunFactory.EnumAmmo.B75_EXP);

        linkItem(r762_sp, GunFactory.EnumAmmo.R762_SP);
        linkItem(r762_fmj, GunFactory.EnumAmmo.R762_FMJ);
        linkItem(r762_jhp, GunFactory.EnumAmmo.R762_JHP);
        linkItem(r762_ap, GunFactory.EnumAmmo.R762_AP);
        linkItem(r762_du, GunFactory.EnumAmmo.R762_DU);
        linkItem(r762_he, GunFactory.EnumAmmo.R762_HE);

        linkItem(energy_lacunae, GunFactory.EnumAmmo.CAPACITOR);
        linkItem(energy_lacunae_overcharge, GunFactory.EnumAmmo.CAPACITOR_OVERCHARGE);
        linkItem(energy_lacunae_ir, GunFactory.EnumAmmo.CAPACITOR_IR);

        linkItem(p35800, GunFactory.EnumAmmo.P35_800);
        linkItem(p35800_bl, GunFactory.EnumAmmo.P35_800_BL);

        linkItem(energy_las, GunFactory.EnumAmmo.CAPACITOR);
        linkItem(energy_las_overcharge, GunFactory.EnumAmmo.CAPACITOR_OVERCHARGE);
        linkItem(energy_las_ir, GunFactory.EnumAmmo.CAPACITOR_IR);

        linkItem(energy_emerald, GunFactory.EnumAmmo.CAPACITOR);
        linkItem(energy_emerald_overcharge, GunFactory.EnumAmmo.CAPACITOR_OVERCHARGE);
        linkItem(energy_emerald_ir, GunFactory.EnumAmmo.CAPACITOR_IR);

        linkItem(energy_tesla, GunFactory.EnumAmmo.CAPACITOR);
        linkItem(energy_tesla_overcharge, GunFactory.EnumAmmo.CAPACITOR_OVERCHARGE);
        linkItem(energy_tesla_ir, GunFactory.EnumAmmo.CAPACITOR_IR);

        linkItem(g26_flare, GunFactory.EnumAmmo.G26_FLARE);
        linkItem(g26_flare_supply, GunFactory.EnumAmmo.G26_FLARE_SUPPLY);
        linkItem(g26_flare_weapon, GunFactory.EnumAmmo.G26_FLARE_WEAPON);
        linkItem(g40_he, GunFactory.EnumAmmo.G40_HE);
        linkItem(g40_demo, GunFactory.EnumAmmo.G40_DEMO);
        linkItem(g40_heat, GunFactory.EnumAmmo.G40_HEAT);
        linkItem(g40_inc, GunFactory.EnumAmmo.G40_INC);
        linkItem(g40_phosphorus, GunFactory.EnumAmmo.G40_PHOSPHORUS);

        linkItem(p45_sp, GunFactory.EnumAmmo.P45_SP);
        linkItem(p45_ap, GunFactory.EnumAmmo.P45_AP);
        linkItem(p45_jhp, GunFactory.EnumAmmo.P45_JHP);
        linkItem(p45_du, GunFactory.EnumAmmo.P45_DU);
        linkItem(p45_fmj, GunFactory.EnumAmmo.P45_FMJ);

        linkItem(tau_uranium, GunFactory.EnumAmmo.TAU_URANIUM);
        linkItem(tau_uranium_charge, GunFactory.EnumAmmo.TAU_URANIUM);
        linkItem(coil_tungsten, GunFactory.EnumAmmo.COIL_TUNGSTEN);
        linkItem(coil_ferrouranium, GunFactory.EnumAmmo.COIL_FERROURANIUM);

        linkItem(flame_diesel, GunFactory.EnumAmmo.FLAME_DIESEL);
        linkItem(flame_napalm, GunFactory.EnumAmmo.FLAME_NAPALM);
        linkItem(flame_gas, GunFactory.EnumAmmo.FLAME_GAS);
        linkItem(flame_balefire, GunFactory.EnumAmmo.FLAME_BALEFIRE);
        linkItem(flame_topaz_diesel, GunFactory.EnumAmmo.FLAME_DIESEL);
        linkItem(flame_topaz_napalm, GunFactory.EnumAmmo.FLAME_NAPALM);
        linkItem(flame_topaz_gas, GunFactory.EnumAmmo.FLAME_GAS);
        linkItem(flame_topaz_balefire, GunFactory.EnumAmmo.FLAME_BALEFIRE);
        linkItem(flame_daybreaker_diesel, GunFactory.EnumAmmo.FLAME_DIESEL);
        linkItem(flame_daybreaker_napalm, GunFactory.EnumAmmo.FLAME_NAPALM);
        linkItem(flame_daybreaker_gas, GunFactory.EnumAmmo.FLAME_GAS);
        linkItem(flame_daybreaker_balefire, GunFactory.EnumAmmo.FLAME_BALEFIRE);

        linkItem(rocket_rpzb_he, GunFactory.EnumAmmo.ROCKET_HE);
        linkItem(rocket_rpzb_heat, GunFactory.EnumAmmo.ROCKET_HEAT);
        linkItem(rocket_rpzb_demo, GunFactory.EnumAmmo.ROCKET_DEMO);
        linkItem(rocket_rpzb_inc, GunFactory.EnumAmmo.ROCKET_INC);
        linkItem(rocket_rpzb_phosphorus, GunFactory.EnumAmmo.ROCKET_PHOSPHORUS);

        linkItem(rocket_qd_he, GunFactory.EnumAmmo.ROCKET_HE);
        linkItem(rocket_qd_heat, GunFactory.EnumAmmo.ROCKET_HEAT);
        linkItem(rocket_qd_demo, GunFactory.EnumAmmo.ROCKET_DEMO);
        linkItem(rocket_qd_inc, GunFactory.EnumAmmo.ROCKET_INC);
        linkItem(rocket_qd_phosphorus, GunFactory.EnumAmmo.ROCKET_PHOSPHORUS);

        linkItem(rocket_ml_he, GunFactory.EnumAmmo.ROCKET_HE);
        linkItem(rocket_ml_heat, GunFactory.EnumAmmo.ROCKET_HEAT);
        linkItem(rocket_ml_demo, GunFactory.EnumAmmo.ROCKET_DEMO);
        linkItem(rocket_ml_inc, GunFactory.EnumAmmo.ROCKET_INC);
        linkItem(rocket_ml_phosphorus, GunFactory.EnumAmmo.ROCKET_PHOSPHORUS);

        linkItem(nuke_standard, GunFactory.EnumAmmo.NUKE_STANDARD);
        linkItem(nuke_demo, GunFactory.EnumAmmo.NUKE_DEMO);
        linkItem(nuke_high, GunFactory.EnumAmmo.NUKE_HIGH);
        linkItem(nuke_tots, GunFactory.EnumAmmo.NUKE_TOTS);
        linkItem(nuke_hive, GunFactory.EnumAmmo.NUKE_HIVE);
        linkItem(nuke_balefire, GunFactory.EnumAmmo.NUKE_BALEFIRE);

        linkItem(folly_sm, GunFactory.EnumAmmo.FOLLY_SM);
        linkItem(folly_nuke, GunFactory.EnumAmmo.FOLLY_NUKE);

        linkItem(fext_co2, GunFactory.EnumAmmo.FEXT_CO2);
        linkItem(fext_foam, GunFactory.EnumAmmo.FEXT_FOAM);
        linkItem(fext_sand, GunFactory.EnumAmmo.FEXT_SAND);

        linkItem(ct_hook, GunFactory.EnumAmmo.CT_HOOK);
        linkItem(ct_mortar, GunFactory.EnumAmmo.CT_MORTAR);
        linkItem(ct_mortar_charge, GunFactory.EnumAmmo.CT_MORTAR_CHARGE);


    }

    private static void linkItem(BulletConfig config, GunFactory.EnumAmmo ammoType) {
        config.setItem(ammoType);
    }


}