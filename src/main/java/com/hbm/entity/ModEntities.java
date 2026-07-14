package com.hbm.entity;

import com.hbm.entity.effect.*;
import com.hbm.entity.grenade.*;
import com.hbm.entity.item.EntityBoatRubber;
import com.hbm.entity.item.EntityFallingBlockNT;
import com.hbm.entity.item.EntityParachuteCrate;
import com.hbm.entity.item.EntityTNTPrimedBase;
import com.hbm.entity.missile.EntityMissileTier0.*;
import com.hbm.entity.missile.EntityMissileTier1.*;
import com.hbm.entity.missile.EntityMissileTier2.*;
import com.hbm.entity.missile.EntityMissileTier3.*;
import com.hbm.entity.missile.EntityMissileTier4.*;
import com.hbm.entity.logic.*;
import com.hbm.entity.missile.*;
import com.hbm.entity.mob.*;
import com.hbm.entity.mob.botprime.EntityBOTPrimeBody;
import com.hbm.entity.mob.botprime.EntityBOTPrimeHead;
import com.hbm.entity.mob.glyphid.*;
import com.hbm.entity.particle.EntityCloudFX;
import com.hbm.entity.particle.EntityOrangeFX;
import com.hbm.entity.particle.EntityPinkCloudFX;
import com.hbm.entity.projectile.*;
import com.hbm.util.RefStrings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RefStrings.MODID);

    public static final RegistryObject<EntityType<EntityBulletBaseMK4>> BULLET =
            ENTITIES.register("bullet", () ->
                    EntityType.Builder.<EntityBulletBaseMK4>of(
                                    EntityBulletBaseMK4::new,
                                    MobCategory.MISC
                            )
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("bullet")
            );

    public static final RegistryObject<EntityType<EntityBullet>> BULLET_LEGACY =
            ENTITIES.register("bullet_legacy", () ->
                    EntityType.Builder.<EntityBullet>of(EntityBullet::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(10)
                            .updateInterval(20)
                            .build("bullet_legacy")
            );

    public static final RegistryObject<EntityType<EntityRubble>> RUBBLE = ENTITIES.register(
            "rubble",
            () -> EntityType.Builder.<EntityRubble>of(EntityRubble::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("rubble")
    );

    public static final RegistryObject<EntityType<EntityShrapnel>> SHRAPNEL = ENTITIES.register(
            "shrapnel",
            () -> EntityType.Builder.<EntityShrapnel>of(EntityShrapnel::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("shrapnel")
    );

    public static final RegistryObject<EntityType<EntityTNTPrimedBase>> TNT_PRIMED_BASE =
            ENTITIES.register("tnt_primed_base", () ->
                    EntityType.Builder.<EntityTNTPrimedBase>of(EntityTNTPrimedBase::new, MobCategory.MISC)
                            .sized(0.98F, 0.98F)
                            .clientTrackingRange(10)
                            .updateInterval(10)
                            .build("hbm:tnt_primed_base")
            );

    public static final RegistryObject<EntityType<EntityFireLingering>> FIRE_LINGERING =
            ENTITIES.register("fire_lingering", () ->
                    EntityType.Builder.of(EntityFireLingering::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("fire_lingering")
    );

    public static final RegistryObject<EntityType<EntityBulletBeamBase>> BULLET_BEAM =
            ENTITIES.register("bullet_beam",
                    () -> EntityType.Builder.<EntityBulletBeamBase>of(EntityBulletBeamBase::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("hbm:bullet_beam")
            );

    public static final RegistryObject<EntityType<EntityCoin>> COIN = ENTITIES.register("coin",
            () -> EntityType.Builder.<EntityCoin>of(EntityCoin::new, MobCategory.MISC)
                    .sized(1.0F, 0.1F) // Ширина, высота
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("coin"));

    public static final RegistryObject<EntityType<EntityParachuteCrate>> PARACHUTE_CRATE =
            ENTITIES.register("parachute_crate",
                    () -> EntityType.Builder.of(EntityParachuteCrate::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .build("parachute_crate"));

    public static final RegistryObject<EntityType<EntityC130>> C130 = ENTITIES.register("c130",
            () -> EntityType.Builder.of(EntityC130::new, MobCategory.MISC)
                    .sized(8.0F, 4.0F)
                    .clientTrackingRange(250)
                    .build("c130"));

    public static final RegistryObject<EntityType<EntityCyberCrab>> CYBER_CRAB = ENTITIES.register("cyber_crab",
            () -> EntityType.Builder.of(EntityCyberCrab::new, MobCategory.MONSTER)
                    .sized(0.75F, 0.35F)
                    .build("cyber_crab"));

    public static final RegistryObject<EntityType<EntityTaintCrab>> TAINT_CRAB = ENTITIES.register("taint_crab",
            () -> EntityType.Builder.of(EntityTaintCrab::new, MobCategory.MONSTER)
                    .sized(1.25F, 1.25F)
                    .build("taint_crab"));

    public static final RegistryObject<EntityType<EntityCreeperNuclear>> CREEPER_NUCLEAR = ENTITIES.register("creeper_nuclear",
            () -> EntityType.Builder.of(EntityCreeperNuclear::new, MobCategory.MONSTER)
                    .build("creeper_nuclear"));

    public static final RegistryObject<EntityType<EntityCreeperTainted>> CREEPER_TAINTED = ENTITIES.register("creeper_tainted",
            () -> EntityType.Builder.of(EntityCreeperTainted::new, MobCategory.MONSTER)
                    .build("creeper_tainted"));

    public static final RegistryObject<EntityType<EntityCreeperGold>> CREEPER_GOLD =
            ENTITIES.register("creeper_gold",
                    () -> EntityType.Builder.of(EntityCreeperGold::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.7F)
                            .build("creeper_gold"));

    public static final RegistryObject<EntityType<EntityCreeperPhosgene>> CREEPER_PHOSGENE =
            ENTITIES.register("creeper_phosgene",
                    () -> EntityType.Builder.of(EntityCreeperPhosgene::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.7F)
                            .build("creeper_phosgene"));

    public static final RegistryObject<EntityType<EntityCreeperVolatile>> CREEPER_VOLATILE =
            ENTITIES.register("creeper_volatile",
                    () -> EntityType.Builder.of(EntityCreeperVolatile::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.7F)
                            .build("creeper_volatile"));

    public static final RegistryObject<EntityType<EntityFallingBlockNT>> FALLING_BLOCK_NT = ENTITIES.register("falling_block_nt",
            () -> EntityType.Builder.<EntityFallingBlockNT>of(EntityFallingBlockNT::new, MobCategory.MISC)
                    .build("falling_block_nt"));

    public static final RegistryObject<EntityType<EntityCloudFleijaRainbow>> CLOUD_FLEIJA_RAINBOW =
            ENTITIES.register("cloud_fleija_rainbow",
                    () -> EntityType.Builder.<EntityCloudFleijaRainbow>of(EntityCloudFleijaRainbow::new, MobCategory.MISC)
                            .sized(1, 4)
                            .clientTrackingRange(10)
                            .build("cloud_fleija_rainbow"));

    public static final RegistryObject<EntityType<EntityExplosiveBeam>> EXPLOSIVE_BEAM = ENTITIES.register("explosive_beam",
            () -> EntityType.Builder.<EntityExplosiveBeam>of(EntityExplosiveBeam::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build("explosive_beam"));

    public static final RegistryObject<EntityType<EntityRainbow>> RAINBOW = ENTITIES.register("rainbow",
            () -> EntityType.Builder.<EntityRainbow>of(EntityRainbow::new, MobCategory.MISC)
                    .build("creeper_tainted"));

    public static final RegistryObject<EntityType<EntityNukeExplosionMK3>> NUKE_EXPLOSION_MK3 = ENTITIES.register("nuke_explosion_mk3",
            () -> EntityType.Builder.of(EntityNukeExplosionMK3::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(100) // Большой радиус для взрывов
                    .updateInterval(1) // Частые обновления
                    .fireImmune() // Иммунитет к огню
                    .build("nuke_explosion_mk3"));

    public static final RegistryObject<EntityType<EntityFalloutRain>> FALLOUT_RAIN = ENTITIES.register("fallout_rain",
            () -> EntityType.Builder.<EntityFalloutRain>of(EntityFalloutRain::new, MobCategory.MISC)
                    .sized(4.0F, 20.0F)
                    .clientTrackingRange(100)
                    .updateInterval(1)
                    .fireImmune()
                    .build("fallout_rain"));

    public static final RegistryObject<EntityType<EntityNukeExplosionMK5>> NUKE_EXPLOSION_MK5 = ENTITIES.register("nuke_explosion_mk5",
            () -> EntityType.Builder.of(EntityNukeExplosionMK5::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(100)
                    .updateInterval(1)
                    .fireImmune()
                    .build("nuke_explosion_mk5"));

    // В ModEntities.java

    public static final RegistryObject<EntityType<EntityRocket>> ROCKET = ENTITIES.register("rocket",
            () -> EntityType.Builder.<EntityRocket>of(EntityRocket::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .fireImmune()
                    .build("rocket"));

    public static final RegistryObject<EntityType<EntityMist>> MIST = ENTITIES.register("mist",
            () -> EntityType.Builder.<EntityMist>of(EntityMist::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .fireImmune()
                    .noSummon()
                    .build("mist"));

    public static final RegistryObject<EntityType<EntityDisperserCanister>> DISPERSER_CANISTER = ENTITIES.register("disperser_canister",
            () -> EntityType.Builder.<EntityDisperserCanister>of(EntityDisperserCanister::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(10)
                    .build("disperser_canister"));

    public static final RegistryObject<EntityType<EntitySchrab>> SCHRAB = ENTITIES.register("schrab",
            () -> EntityType.Builder.<EntitySchrab>of(EntitySchrab::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .fireImmune()
                    .build("schrab"));

    public static final RegistryObject<EntityType<EntityCloudFleija>> CLOUD_FLEIJA = ENTITIES.register("cloud_fleija",
            () -> EntityType.Builder.<EntityCloudFleija>of(EntityCloudFleija::new, MobCategory.MISC)
                    .sized(1, 4)
                    .clientTrackingRange(100)
                    .updateInterval(1)
                    .fireImmune()
                    .build("cloud_fleija"));

    public static final RegistryObject<EntityType<EntityBlackHole>> BLACK_HOLE = ENTITIES.register("black_hole",
            () -> EntityType.Builder.<EntityBlackHole>of(EntityBlackHole::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(100)
                    .updateInterval(1)
                    .fireImmune()
                    .build("black_hole"));

    public static final RegistryObject<EntityType<EntityCloudFX>> CLOUD_FX = ENTITIES.register("cloud_fx",
            () -> EntityType.Builder.<EntityCloudFX>of(EntityCloudFX::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("cloud_fx"));

    public static final RegistryObject<EntityType<EntityPinkCloudFX>> PINK_CLOUD_FX = ENTITIES.register("pink_cloud_fx",
            () -> EntityType.Builder.<EntityPinkCloudFX>of(EntityPinkCloudFX::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("pink_cloud_fx"));

    public static final RegistryObject<EntityType<EntityOrangeFX>> ORANGE_FX = ENTITIES.register("orange_fx",
            () -> EntityType.Builder.<EntityOrangeFX>of(EntityOrangeFX::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("orange_fx"));

    public static final RegistryObject<EntityType<EntityRagingVortex>> RAGING_VORTEX = ENTITIES.register("raging_vortex",
            () -> EntityType.Builder.<EntityRagingVortex>of(EntityRagingVortex::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(100)
                    .updateInterval(1)
                    .fireImmune()
                    .build("raging_vortex"));

    public static final RegistryObject<EntityType<EntityVortex>> VORTEX = ENTITIES.register("vortex",
            () -> EntityType.Builder.<EntityVortex>of(EntityVortex::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(100)
                    .updateInterval(1)
                    .fireImmune()
                    .build("vortex"));

    // Гранаты
    public static final RegistryObject<EntityType<EntityGrenadeGeneric>> GRENADE_GENERIC = ENTITIES.register("grenade_generic",
            () -> EntityType.Builder.<EntityGrenadeGeneric>of(EntityGrenadeGeneric::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_generic"));
    public static final RegistryObject<EntityType<EntityGrenadeStrong>> GRENADE_STRONG = ENTITIES.register("grenade_strong",
            () -> EntityType.Builder.<EntityGrenadeStrong>of(EntityGrenadeStrong::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_strong"));
    public static final RegistryObject<EntityType<EntityGrenadeFrag>> GRENADE_FRAG = ENTITIES.register("grenade_frag",
            () -> EntityType.Builder.<EntityGrenadeFrag>of(EntityGrenadeFrag::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_frag"));
    public static final RegistryObject<EntityType<EntityGrenadeFire>> GRENADE_FIRE = ENTITIES.register("grenade_fire",
            () -> EntityType.Builder.<EntityGrenadeFire>of(EntityGrenadeFire::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_fire"));
    public static final RegistryObject<EntityType<EntityGrenadeCluster>> GRENADE_CLUSTER = ENTITIES.register("grenade_cluster",
            () -> EntityType.Builder.<EntityGrenadeCluster>of(EntityGrenadeCluster::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_cluster"));
    public static final RegistryObject<EntityType<EntityGrenadeFlare>> GRENADE_FLARE = ENTITIES.register("grenade_flare",
            () -> EntityType.Builder.<EntityGrenadeFlare>of(EntityGrenadeFlare::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_flare"));
    public static final RegistryObject<EntityType<EntityGrenadeElectric>> GRENADE_ELECTRIC = ENTITIES.register("grenade_electric",
            () -> EntityType.Builder.<EntityGrenadeElectric>of(EntityGrenadeElectric::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_electric"));
    public static final RegistryObject<EntityType<EntityGrenadePoison>> GRENADE_POISON = ENTITIES.register("grenade_poison",
            () -> EntityType.Builder.<EntityGrenadePoison>of(EntityGrenadePoison::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_poison"));
    public static final RegistryObject<EntityType<EntityGrenadeGas>> GRENADE_GAS = ENTITIES.register("grenade_gas",
            () -> EntityType.Builder.<EntityGrenadeGas>of(EntityGrenadeGas::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_gas"));
    public static final RegistryObject<EntityType<EntityGrenadeSchrabidium>> GRENADE_SCHRABIDIUM = ENTITIES.register("grenade_schrabidium",
            () -> EntityType.Builder.<EntityGrenadeSchrabidium>of(EntityGrenadeSchrabidium::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_schrabidium"));
    public static final RegistryObject<EntityType<EntityGrenadeNuke>> GRENADE_NUKE = ENTITIES.register("grenade_nuke",
            () -> EntityType.Builder.<EntityGrenadeNuke>of(EntityGrenadeNuke::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_nuke"));
    public static final RegistryObject<EntityType<EntityGrenadeNuclear>> GRENADE_NUCLEAR = ENTITIES.register("grenade_nuclear",
            () -> EntityType.Builder.<EntityGrenadeNuclear>of(EntityGrenadeNuclear::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_nuclear"));
    public static final RegistryObject<EntityType<EntityGrenadePulse>> GRENADE_PULSE = ENTITIES.register("grenade_pulse",
            () -> EntityType.Builder.<EntityGrenadePulse>of(EntityGrenadePulse::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_pulse"));
    public static final RegistryObject<EntityType<EntityGrenadePlasma>> GRENADE_PLASMA = ENTITIES.register("grenade_plasma",
            () -> EntityType.Builder.<EntityGrenadePlasma>of(EntityGrenadePlasma::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_plasma"));
    public static final RegistryObject<EntityType<EntityGrenadeTau>> GRENADE_TAU = ENTITIES.register("grenade_tau",
            () -> EntityType.Builder.<EntityGrenadeTau>of(EntityGrenadeTau::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_tau"));
    public static final RegistryObject<EntityType<EntityGrenadeLemon>> GRENADE_LEMON = ENTITIES.register("grenade_lemon",
            () -> EntityType.Builder.<EntityGrenadeLemon>of(EntityGrenadeLemon::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_lemon"));
    public static final RegistryObject<EntityType<EntityGrenadeMk2>> GRENADE_MK2 = ENTITIES.register("grenade_mk2",
            () -> EntityType.Builder.<EntityGrenadeMk2>of(EntityGrenadeMk2::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_mk2"));
    public static final RegistryObject<EntityType<EntityGrenadeASchrab>> GRENADE_ASCHRAB = ENTITIES.register("grenade_aschrab",
            () -> EntityType.Builder.<EntityGrenadeASchrab>of(EntityGrenadeASchrab::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_aschrab"));
    public static final RegistryObject<EntityType<EntityGrenadeZOMG>> GRENADE_ZOMG = ENTITIES.register("grenade_zomg",
            () -> EntityType.Builder.<EntityGrenadeZOMG>of(EntityGrenadeZOMG::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_zomg"));
    public static final RegistryObject<EntityType<EntityGrenadeShrapnel>> GRENADE_SHRAPNEL = ENTITIES.register("grenade_shrapnel",
            () -> EntityType.Builder.<EntityGrenadeShrapnel>of(EntityGrenadeShrapnel::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_shrapnel"));
    public static final RegistryObject<EntityType<EntityGrenadeBlackHole>> GRENADE_BLACK_HOLE = ENTITIES.register("grenade_black_hole",
            () -> EntityType.Builder.<EntityGrenadeBlackHole>of(EntityGrenadeBlackHole::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_black_hole"));
    public static final RegistryObject<EntityType<EntityGrenadeGascan>> GRENADE_GASCAN = ENTITIES.register("grenade_gascan",
            () -> EntityType.Builder.<EntityGrenadeGascan>of(EntityGrenadeGascan::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_gascan"));
    public static final RegistryObject<EntityType<EntityGrenadeCloud>> GRENADE_CLOUD = ENTITIES.register("grenade_cloud",
            () -> EntityType.Builder.<EntityGrenadeCloud>of(EntityGrenadeCloud::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_cloud"));
    public static final RegistryObject<EntityType<EntityGrenadePC>> GRENADE_PC = ENTITIES.register("grenade_pc",
            () -> EntityType.Builder.<EntityGrenadePC>of(EntityGrenadePC::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_pc"));
    public static final RegistryObject<EntityType<EntityGrenadeSmart>> GRENADE_SMART = ENTITIES.register("grenade_smart",
            () -> EntityType.Builder.<EntityGrenadeSmart>of(EntityGrenadeSmart::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_smart"));
    public static final RegistryObject<EntityType<EntityGrenadeMIRV>> GRENADE_MIRV = ENTITIES.register("grenade_mirv",
            () -> EntityType.Builder.<EntityGrenadeMIRV>of(EntityGrenadeMIRV::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_mirv"));
    public static final RegistryObject<EntityType<EntityGrenadeBreach>> GRENADE_BREACH = ENTITIES.register("grenade_breach",
            () -> EntityType.Builder.<EntityGrenadeBreach>of(EntityGrenadeBreach::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_breach"));
    public static final RegistryObject<EntityType<EntityGrenadeBurst>> GRENADE_BURST = ENTITIES.register("grenade_burst",
            () -> EntityType.Builder.<EntityGrenadeBurst>of(EntityGrenadeBurst::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_burst"));

    // IF Series
    public static final RegistryObject<EntityType<EntityGrenadeIFGeneric>> GRENADE_IF_GENERIC = ENTITIES.register("grenade_if_generic",
            () -> EntityType.Builder.<EntityGrenadeIFGeneric>of(EntityGrenadeIFGeneric::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_generic"));
    public static final RegistryObject<EntityType<EntityGrenadeIFHE>> GRENADE_IF_HE = ENTITIES.register("grenade_if_he",
            () -> EntityType.Builder.<EntityGrenadeIFHE>of(EntityGrenadeIFHE::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_he"));
    public static final RegistryObject<EntityType<EntityGrenadeIFBouncy>> GRENADE_IF_BOUNCY = ENTITIES.register("grenade_if_bouncy",
            () -> EntityType.Builder.<EntityGrenadeIFBouncy>of(EntityGrenadeIFBouncy::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_bouncy"));
    public static final RegistryObject<EntityType<EntityGrenadeIFSticky>> GRENADE_IF_STICKY = ENTITIES.register("grenade_if_sticky",
            () -> EntityType.Builder.<EntityGrenadeIFSticky>of(EntityGrenadeIFSticky::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_sticky"));
    public static final RegistryObject<EntityType<EntityGrenadeIFImpact>> GRENADE_IF_IMPACT = ENTITIES.register("grenade_if_impact",
            () -> EntityType.Builder.<EntityGrenadeIFImpact>of(EntityGrenadeIFImpact::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_impact"));
    public static final RegistryObject<EntityType<EntityGrenadeIFIncendiary>> GRENADE_IF_INCENDIARY = ENTITIES.register("grenade_if_incendiary",
            () -> EntityType.Builder.<EntityGrenadeIFIncendiary>of(EntityGrenadeIFIncendiary::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_incendiary"));
    public static final RegistryObject<EntityType<EntityGrenadeIFToxic>> GRENADE_IF_TOXIC = ENTITIES.register("grenade_if_toxic",
            () -> EntityType.Builder.<EntityGrenadeIFToxic>of(EntityGrenadeIFToxic::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_toxic"));
    public static final RegistryObject<EntityType<EntityGrenadeIFConcussion>> GRENADE_IF_CONCUSSION = ENTITIES.register("grenade_if_concussion",
            () -> EntityType.Builder.<EntityGrenadeIFConcussion>of(EntityGrenadeIFConcussion::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_concussion"));
    public static final RegistryObject<EntityType<EntityGrenadeIFBrimstone>> GRENADE_IF_BRIMSTONE = ENTITIES.register("grenade_if_brimstone",
            () -> EntityType.Builder.<EntityGrenadeIFBrimstone>of(EntityGrenadeIFBrimstone::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_brimstone"));
    public static final RegistryObject<EntityType<EntityGrenadeIFMystery>> GRENADE_IF_MYSTERY = ENTITIES.register("grenade_if_mystery",
            () -> EntityType.Builder.<EntityGrenadeIFMystery>of(EntityGrenadeIFMystery::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_mystery"));
    public static final RegistryObject<EntityType<EntityGrenadeIFSpark>> GRENADE_IF_SPARK = ENTITIES.register("grenade_if_spark",
            () -> EntityType.Builder.<EntityGrenadeIFSpark>of(EntityGrenadeIFSpark::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_spark"));
    public static final RegistryObject<EntityType<EntityGrenadeIFHopwire>> GRENADE_IF_HOPWIRE = ENTITIES.register("grenade_if_hopwire",
            () -> EntityType.Builder.<EntityGrenadeIFHopwire>of(EntityGrenadeIFHopwire::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_hopwire"));
    public static final RegistryObject<EntityType<EntityGrenadeIFNull>> GRENADE_IF_NULL = ENTITIES.register("grenade_if_null",
            () -> EntityType.Builder.<EntityGrenadeIFNull>of(EntityGrenadeIFNull::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_if_null"));

    public static final RegistryObject<EntityType<EntityAcidBomb>> ACID_BOMB = ENTITIES.register("acid_bomb",
            () -> EntityType.Builder.<EntityAcidBomb>of(EntityAcidBomb::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("acid_bomb"));

    public static final RegistryObject<EntityType<EntityChemical>> CHEMICAL = ENTITIES.register("chemical",
            () -> EntityType.Builder.<EntityChemical>of(EntityChemical::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("chemical"));

    public static final RegistryObject<EntityType<EntityGrenadeImpactGeneric>> GRENADE_IMPACT_GENERIC = ENTITIES.register("grenade_impact_generic",
            () -> EntityType.Builder.<EntityGrenadeImpactGeneric>of(EntityGrenadeImpactGeneric::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("grenade_impact_generic"));

    public static final RegistryObject<EntityType<EntityGrenadeBouncyGeneric>> GRENADE_BOUNCY_GENERIC = ENTITIES.register("grenade_bouncy_generic",
            () -> EntityType.Builder.<EntityGrenadeBouncyGeneric>of(EntityGrenadeBouncyGeneric::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("grenade_bouncy_generic"));

    // Другие
    public static final RegistryObject<EntityType<EntityWastePearl>> WASTE_PEARL = ENTITIES.register("waste_pearl",
            () -> EntityType.Builder.<EntityWastePearl>of(EntityWastePearl::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("waste_pearl"));

    public static final RegistryObject<EntityType<EntityGrenadeDynamite>> GRENADE_DYNAMITE = ENTITIES.register("grenade_dynamite",
            () -> EntityType.Builder.<EntityGrenadeDynamite>of(EntityGrenadeDynamite::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_dynamite"));

    public static final RegistryObject<EntityType<EntityGrenadeKyiv>> GRENADE_KYIV = ENTITIES.register("grenade_kyiv",
            () -> EntityType.Builder.of(EntityGrenadeKyiv::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(10).build("grenade_kyiv"));


    public static final RegistryObject<EntityType<EntityWaypoint>> WAYPOINT =
            ENTITIES.register("waypoint", () -> EntityType.Builder.of(EntityWaypoint::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("waypoint"));

    public static final RegistryObject<EntityType<EntityGlyphid>> GLYPHID =
            ENTITIES.register("glyphid", () -> EntityType.Builder.of(EntityGlyphid::new, MobCategory.MONSTER)
                    .sized(1.75F, 1F)
                    .build("glyphid"));

    public static final RegistryObject<EntityType<EntityGlyphidScout>> GLYPHID_SCOUT =
            ENTITIES.register("glyphid_scout", () -> EntityType.Builder.of(EntityGlyphidScout::new, MobCategory.MONSTER)
                    .sized(1.25F, 0.75F)
                    .build("glyphid_scout"));

    public static final RegistryObject<EntityType<EntityGlyphidNuclear>> GLYPHID_NUCLEAR =
            ENTITIES.register("glyphid_nuclear", () -> EntityType.Builder.of(EntityGlyphidNuclear::new, MobCategory.MONSTER)
                    .sized(2.5F, 1.75F)
                    .build("glyphid_nuclear"));

    public static final RegistryObject<EntityType<EntityGlyphidDigger>> GLYPHID_DIGGER =
            ENTITIES.register("glyphid_digger", () -> EntityType.Builder.of(EntityGlyphidDigger::new, MobCategory.MONSTER)
                    .build("glyphid_digger"));

    public static final RegistryObject<EntityType<EntityGlyphidBrawler>> GLYPHID_BRAWLER = ENTITIES.register("glyphid_brawler",
            () -> EntityType.Builder.of(EntityGlyphidBrawler::new, MobCategory.MONSTER)
                    .sized(2.0F, 1.125F)
                    .build("glyphid_brawler"));

    public static final RegistryObject<EntityType<EntityGlyphidBombardier>> GLYPHID_BOMBARDIER = ENTITIES.register("glyphid_bombardier",
            () -> EntityType.Builder.of(EntityGlyphidBombardier::new, MobCategory.MONSTER)
                    .sized(1.75F, 1.0F)
                    .build("glyphid_bombardier"));

    public static final RegistryObject<EntityType<EntityGlyphidBlaster>> GLYPHID_BLASTER = ENTITIES.register("glyphid_blaster",
            () -> EntityType.Builder.of(EntityGlyphidBlaster::new, MobCategory.MONSTER)
                    .sized(2.0F, 1.125F)
                    .build("glyphid_blaster"));

    public static final RegistryObject<EntityType<EntityGlyphidBehemoth>> GLYPHID_BEHEMOTH = ENTITIES.register("glyphid_behemoth",
            () -> EntityType.Builder.of(EntityGlyphidBehemoth::new, MobCategory.MONSTER)
                    .sized(2.5F, 1.5F)
                    .build("glyphid_behemoth"));

    public static final RegistryObject<EntityType<EntityGlyphidBrenda>> GLYPHID_BRENDA = ENTITIES.register("glyphid_brenda",
            () -> EntityType.Builder.of(EntityGlyphidBrenda::new, MobCategory.MONSTER)
                    .sized(2.5F, 1.5F)
                    .build("glyphid_brenda"));

    public static final RegistryObject<EntityType<EntitySawblade>> SAWBLADE =
            ENTITIES.register("sawblade",
                    () -> EntityType.Builder.<EntitySawblade>of(EntitySawblade::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(10)
                            .updateInterval(20)
                            .build("sawblade"));

    public static final RegistryObject<EntityType<EntityNukeTorex>> NUKE_TOREX = ENTITIES.register("nuke_torex",
            () -> EntityType.Builder.of(EntityNukeTorex::new, MobCategory.MISC)
                    .sized(1.0F, 50.0F)
                    .clientTrackingRange(1000)
                    .updateInterval(1)
                    .fireImmune()
                    .build("nuke_torex"));

    public static final RegistryObject<EntityType<EntityFallingNuke>> FALLING_NUKE = ENTITIES.register("falling_nuke",
            () -> EntityType.Builder.<EntityFallingNuke>of(EntityFallingNuke::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(250)
                    .updateInterval(1)
                    .fireImmune()
                    .build("falling_nuke"));

    public static final RegistryObject<EntityType<EntityBalefire>> BALEFIRE = ENTITIES.register("balefire",
            () -> EntityType.Builder.of(EntityBalefire::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(500)
                    .updateInterval(1)
                    .fireImmune()
                    .build("balefire"));

    public static final RegistryObject<EntityType<CustomSkeleton>> CUSTOM_SKELETON = ENTITIES.register("custom_skeleton",
            () -> EntityType.Builder.of(CustomSkeleton::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8)
                    .build("custom_skeleton")
    );

    public static final RegistryObject<EntityType<EntityCog>> COG =
            ENTITIES.register("cog",
                    () -> EntityType.Builder.<EntityCog>of(EntityCog::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(10)
                            .updateInterval(20)
                            .build("cog"));

    public static final RegistryObject<EntityType<EntityEMPBlast>> EMP_BLAST =
            ENTITIES.register("emp_blast",
            () -> EntityType.Builder.<EntityEMPBlast>of(EntityEMPBlast::new, MobCategory.MISC)
                    .sized(1.5F, 1.5F)
                    .clientTrackingRange(100)
                    .updateInterval(1)
                    .fireImmune()
                    .build("emp_blast"));

    public static final RegistryObject<EntityType<EntityMissileCustom>> MISSILE_CUSTOM = ENTITIES.register("missile_custom",
            () -> EntityType.Builder.<EntityMissileCustom>of(EntityMissileCustom::new, MobCategory.MISC)
                    .sized(1.5F, 1.5F)
                    .setTrackingRange(500)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .fireImmune()
                    .build("missile_custom"));

    public static final RegistryObject<EntityType<EntityQuasar>> QUASAR = ENTITIES.register("quasar",
            () -> EntityType.Builder.<EntityQuasar>of(EntityQuasar::new, MobCategory.MISC)
                    .sized(1.5F, 1.5F)
                    .clientTrackingRange(256)
                    .updateInterval(1)
                    .build("quasar"));

    public static final RegistryObject<EntityType<EntityFBI>> FBI = ENTITIES.register("fbi",
            () -> EntityType.Builder.of(EntityFBI::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("fbi"));


    public static final RegistryObject<EntityType<EntityFBIDrone>> FBI_DRONE = ENTITIES.register("fbi_drone",
            () -> EntityType.Builder.<EntityFBIDrone>of(EntityFBIDrone::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("fbi_drone"));

    public static final RegistryObject<EntityType<EntityUFO>> UFO = ENTITIES.register("ufo",
            () -> EntityType.Builder.<EntityUFO>of(EntityUFO::new, MobCategory.MONSTER)
                    .sized(15.0F, 4.0F)
                    .clientTrackingRange(128)
                    .updateInterval(1)
                    .build("ufo"));

    public static final RegistryObject<EntityType<EntityMaskMan>> MASKMAN = ENTITIES.register("maskman",
            () -> EntityType.Builder.of(EntityMaskMan::new, MobCategory.MONSTER)
                    .sized(2.0F, 5.0F)
                    .fireImmune()
                    .build("maskman")
    );

    public static final RegistryObject<EntityType<EntityQuackos>> QUACKOS = ENTITIES.register("quackos",
            () -> EntityType.Builder.<EntityQuackos>of(EntityQuackos::new, MobCategory.CREATURE)
                    .sized(0.3F * 25, 0.7F * 25)
                    .clientTrackingRange(64)
                    .build("quackos"));

    public static final RegistryObject<EntityType<EntityDuck>> DUCK = ENTITIES.register("duck",
            () -> EntityType.Builder.<EntityDuck>of(EntityDuck::new, MobCategory.CREATURE)
                    .sized(0.4F, 0.7F)
                    .clientTrackingRange(10)
                    .build("duck"));

    public static final RegistryObject<EntityType<EntityMeteor>> METEOR = ENTITIES.register("meteor",
            () -> EntityType.Builder.<EntityMeteor>of(EntityMeteor::new, MobCategory.MISC)
                    .sized(4.0F, 4.0F)
                    .clientTrackingRange(128)
                    .build("meteor"));

    public static final RegistryObject<EntityType<EntityGhost>> GHOST = ENTITIES.register("ghost",
            () -> EntityType.Builder.of(EntityGhost::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(64)
                    .build("ghost"));

    public static final RegistryObject<EntityType<EntityRADBeast>> RADBEAST = ENTITIES.register("radbeast",
            () -> EntityType.Builder.<EntityRADBeast>of(EntityRADBeast::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .fireImmune()
                    .clientTrackingRange(64)
                    .build("radbeast"));

    public static final RegistryObject<EntityType<EntityBomber>> BOMBER = ENTITIES.register("bomber",
            () -> EntityType.Builder.of(EntityBomber::new, MobCategory.MISC)
                    .sized(8.0F, 4.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(250)
                    .setUpdateInterval(1)
                    .build("bomber"));

    public static final RegistryObject<EntityType<EntityBombletZeta>> BOMBLET_ZETA = ENTITIES.register("bomblet_zeta",
            () -> EntityType.Builder.<EntityBombletZeta>of(EntityBombletZeta::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(150)
                    .setUpdateInterval(1)
                    .build("bomblet_zeta"));

    public static final RegistryObject<EntityType<EntityBoxcar>> BOXCAR = ENTITIES.register("boxcar",
            () -> EntityType.Builder.of(EntityBoxcar::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(150)
                    .setUpdateInterval(1)
                    .build("boxcar"));

    public static final RegistryObject<EntityType<EntityBuilding>> BUILDING = ENTITIES.register("building",
            () -> EntityType.Builder.of(EntityBuilding::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(150)
                    .setUpdateInterval(1)
                    .build("building"));

    public static final RegistryObject<EntityType<EntityTorpedo>> TORPEDO = ENTITIES.register("torpedo",
            () -> EntityType.Builder.of(EntityTorpedo::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(150)
                    .setUpdateInterval(1)
                    .build("torpedo"));

    public static final RegistryObject<EntityType<EntityBurningFOEQ>> BURNING_FOEQ = ENTITIES.register("burning_foeq",
            () -> EntityType.Builder.of(EntityBurningFOEQ::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(250)
                    .setUpdateInterval(1)
                    .build("burning_foeq"));

    public static final RegistryObject<EntityType<EntityMinerRocket>> MINER_ROCKET = ENTITIES.register("miner_rocket",
            () -> EntityType.Builder.of(EntityMinerRocket::new, MobCategory.MISC)
                    .sized(1.0F, 3.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(150)
                    .setUpdateInterval(1)
                    .build("miner_rocket"));

    public static final RegistryObject<EntityType<EntityTom>> TOM = ENTITIES.register("tom",
            () -> EntityType.Builder.<EntityTom>of(EntityTom::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(500)
                    .setUpdateInterval(1)
                    .build("tom"));

    public static final RegistryObject<EntityType<EntityTomBlast>> TOM_BLAST = ENTITIES.register("tom_blast",
            () -> EntityType.Builder.of(EntityTomBlast::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(500)
                    .setUpdateInterval(1)
                    .build("tom_blast"));

    public static final RegistryObject<EntityType<EntityCloudTom>> CLOUD_TOM = ENTITIES.register("cloud_tom",
            () -> EntityType.Builder.<EntityCloudTom>of(EntityCloudTom::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(500)
                    .setUpdateInterval(1)
                    .build("cloud_tom"));

    public static final RegistryObject<EntityType<EntityDeathBlast>> DEATH_BLAST = ENTITIES.register("death_blast",
            () -> EntityType.Builder.of(EntityDeathBlast::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(250)
                    .setUpdateInterval(1)
                    .build("death_blast"));

    public static final RegistryObject<EntityType<EntityEMP>> EMP = ENTITIES.register("emp",
            () -> EntityType.Builder.<EntityEMP>of(EntityEMP::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build("emp"));

    public static final RegistryObject<EntityType<EntityMissileAntiBallistic>> MISSILE_ANTI_BALLISTIC =
            ENTITIES.register("missile_anti_ballistic",
                    () -> EntityType.Builder.<EntityMissileAntiBallistic>of(
                                    EntityMissileAntiBallistic::new,
                                    MobCategory.MISC)
                            .sized(1.5F, 1.5F)
                            .setTrackingRange(500)
                            .fireImmune()
                            .build("missile_anti_ballistic")
            );

    public static final RegistryObject<EntityType<EntityBobmazon>> BOBMAZON = ENTITIES.register("bobmazon",
            () -> EntityType.Builder.<EntityBobmazon>of(EntityBobmazon::new, MobCategory.MISC)
                    .sized(1.0F, 3.0F)
                    .setTrackingRange(500)
                    .build("bobmazon")
    );

    public static final RegistryObject<EntityType<EntityBoatRubber>> BOAT_RUBBER = ENTITIES.register("boat_rubber",
            () -> EntityType.Builder.<EntityBoatRubber>of(EntityBoatRubber::new, MobCategory.MISC)
                    .sized(1.5F, 0.6F)
                    .build("boat_rubber")
    );

    public static final RegistryObject<EntityType<EntityHunterChopper>> CHOPPER = ENTITIES.register("chopper",
            () -> EntityType.Builder.of(EntityHunterChopper::new, MobCategory.MISC)
                    .sized(12.0F, 12.0F)
                    .build("chopper_mine")
    );

    public static final RegistryObject<EntityType<EntityChopperMine>> CHOPPER_MINE = ENTITIES.register("chopper_mine",
            () -> EntityType.Builder.<EntityChopperMine>of(EntityChopperMine::new, MobCategory.MISC)
                    .sized(12.0F, 12.0F)
                    .build("chopper_mine")
    );

    public static final RegistryObject<EntityType<EntityBOTPrimeHead>> BOT_PRIME_HEAD = ENTITIES.register("bot_prime_head",
            () -> EntityType.Builder.of(EntityBOTPrimeHead::new, MobCategory.MONSTER)
                    .sized(3.0F, 3.0F)
                    .build("bot_prime_head")
    );

    public static final RegistryObject<EntityType<EntityBOTPrimeBody>> BOT_PRIME_BODY = ENTITIES.register("bot_prime_body",
            () -> EntityType.Builder.of(EntityBOTPrimeBody::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .build("bot_prime_body")
    );

    // Tier 0
    public static final RegistryObject<EntityType<EntityMissileTest>> MISSILE_TEST =
            ENTITIES.register("missile_test", () -> EntityType.Builder.<EntityMissileTest>of(
                    EntityMissileTest::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_test"));

    public static final RegistryObject<EntityType<EntityMissileMicro>> MISSILE_MICRO =
            ENTITIES.register("missile_micro", () -> EntityType.Builder.<EntityMissileMicro>of(
                    EntityMissileMicro::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_micro"));

    public static final RegistryObject<EntityType<EntityMissileSchrabidium>> MISSILE_SCHRABIDIUM =
            ENTITIES.register("missile_schrabidium", () -> EntityType.Builder.<EntityMissileSchrabidium>of(
                    EntityMissileSchrabidium::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_schrabidium"));

    public static final RegistryObject<EntityType<EntityMissileBHole>> MISSILE_BHOLE =
            ENTITIES.register("missile_bhole", () -> EntityType.Builder.<EntityMissileBHole>of(
                    EntityMissileBHole::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_bhole"));

    public static final RegistryObject<EntityType<EntityMissileTaint>> MISSILE_TAINT =
            ENTITIES.register("missile_taint", () -> EntityType.Builder.<EntityMissileTaint>of(
                    EntityMissileTaint::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_taint"));

    public static final RegistryObject<EntityType<EntityMissileEMP>> MISSILE_EMP =
            ENTITIES.register("missile_emp", () -> EntityType.Builder.<EntityMissileEMP>of(
                    EntityMissileEMP::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_emp"));

    // Tier 1
    public static final RegistryObject<EntityType<EntityMissileGeneric>> MISSILE_GENERIC =
            ENTITIES.register("missile_generic", () -> EntityType.Builder.<EntityMissileGeneric>of(
                    EntityMissileGeneric::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_generic"));

    public static final RegistryObject<EntityType<EntityMissileDecoy>> MISSILE_DECOY =
            ENTITIES.register("missile_decoy", () -> EntityType.Builder.<EntityMissileDecoy>of(
                    EntityMissileDecoy::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_decoy"));

    public static final RegistryObject<EntityType<EntityMissileIncendiary>> MISSILE_INCENDIARY =
            ENTITIES.register("missile_incendiary", () -> EntityType.Builder.<EntityMissileIncendiary>of(
                    EntityMissileIncendiary::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_incendiary"));

    public static final RegistryObject<EntityType<EntityMissileCluster>> MISSILE_CLUSTER =
            ENTITIES.register("missile_cluster", () -> EntityType.Builder.<EntityMissileCluster>of(
                    EntityMissileCluster::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_cluster"));

    public static final RegistryObject<EntityType<EntityMissileBunkerBuster>> MISSILE_BUSTER =
            ENTITIES.register("missile_buster", () -> EntityType.Builder.<EntityMissileBunkerBuster>of(
                    EntityMissileBunkerBuster::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_buster"));

    // Tier 2
    public static final RegistryObject<EntityType<EntityMissileStrong>> MISSILE_STRONG =
            ENTITIES.register("missile_strong", () -> EntityType.Builder.<EntityMissileStrong>of(
                    EntityMissileStrong::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_strong"));

    public static final RegistryObject<EntityType<EntityMissileIncendiaryStrong>> MISSILE_INCENDIARY_STRONG =
            ENTITIES.register("missile_incendiary_strong", () -> EntityType.Builder.<EntityMissileIncendiaryStrong>of(
                    EntityMissileIncendiaryStrong::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_incendiary_strong"));

    public static final RegistryObject<EntityType<EntityMissileClusterStrong>> MISSILE_CLUSTER_STRONG =
            ENTITIES.register("missile_cluster_strong", () -> EntityType.Builder.<EntityMissileClusterStrong>of(
                    EntityMissileClusterStrong::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_cluster_strong"));

    public static final RegistryObject<EntityType<EntityMissileBusterStrong>> MISSILE_BUSTER_STRONG =
            ENTITIES.register("missile_buster_strong", () -> EntityType.Builder.<EntityMissileBusterStrong>of(
                    EntityMissileBusterStrong::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_buster_strong"));

    public static final RegistryObject<EntityType<EntityMissileTier2.EntityMissileEMPStrong>> MISSILE_EMP_STRONG =
            ENTITIES.register("missile_emp_strong", () -> EntityType.Builder.<EntityMissileTier2.EntityMissileEMPStrong>of(
                    EntityMissileEMPStrong::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_emp_strong"));

    // Tier 3
    public static final RegistryObject<EntityType<EntityMissileTier3.EntityMissileBurst>> MISSILE_BURST =
            ENTITIES.register("missile_burst", () -> EntityType.Builder.<EntityMissileTier3.EntityMissileBurst>of(
                    EntityMissileBurst::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_burst"));

    public static final RegistryObject<EntityType<EntityMissileTier3.EntityMissileInferno>> MISSILE_INFERNO =
            ENTITIES.register("missile_inferno", () -> EntityType.Builder.<EntityMissileTier3.EntityMissileInferno>of(
                    EntityMissileInferno::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_inferno"));

    public static final RegistryObject<EntityType<EntityMissileTier3.EntityMissileRain>> MISSILE_RAIN =
            ENTITIES.register("missile_rain", () -> EntityType.Builder.<EntityMissileTier3.EntityMissileRain>of(
                    EntityMissileRain::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_rain"));

    public static final RegistryObject<EntityType<EntityMissileTier3.EntityMissileDrill>> MISSILE_DRILL =
            ENTITIES.register("missile_drill", () -> EntityType.Builder.<EntityMissileTier3.EntityMissileDrill>of(
                    EntityMissileDrill::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_drill"));

    // Tier 4
    public static final RegistryObject<EntityType<EntityMissileTier4.EntityMissileNuclear>> MISSILE_NUCLEAR =
            ENTITIES.register("missile_nuclear", () -> EntityType.Builder.<EntityMissileTier4.EntityMissileNuclear>of(
                    EntityMissileNuclear::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_nuclear"));

    public static final RegistryObject<EntityType<EntityMissileTier4.EntityMissileMirv>> MISSILE_NUCLEAR_CLUSTER =
            ENTITIES.register("missile_nuclear_cluster", () -> EntityType.Builder.<EntityMissileTier4.EntityMissileMirv>of(
                    EntityMissileMirv::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_nuclear_cluster"));

    public static final RegistryObject<EntityType<EntityMissileTier4.EntityMissileVolcano>> MISSILE_VOLCANO =
            ENTITIES.register("missile_volcano", () -> EntityType.Builder.<EntityMissileTier4.EntityMissileVolcano>of(
                    EntityMissileVolcano::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_volcano"));

    public static final RegistryObject<EntityType<EntityMissileTier4.EntityMissileDoomsday>> MISSILE_DOOMSDAY =
            ENTITIES.register("missile_doomsday", () -> EntityType.Builder.<EntityMissileTier4.EntityMissileDoomsday>of(
                    EntityMissileDoomsday::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_doomsday"));

    public static final RegistryObject<EntityType<EntityMissileTier4.EntityMissileDoomsdayRusted>> MISSILE_DOOMSDAY_RUSTED =
            ENTITIES.register("missile_doomsday_rusted", () -> EntityType.Builder.<EntityMissileTier4.EntityMissileDoomsdayRusted>of(
                    EntityMissileDoomsdayRusted::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_doomsday_rusted"));

    // Special
    public static final RegistryObject<EntityType<EntityMissileStealth>> MISSILE_STEALTH =
            ENTITIES.register("missile_stealth", () -> EntityType.Builder.<EntityMissileStealth>of(
                    EntityMissileStealth::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_stealth"));

    public static final RegistryObject<EntityType<EntityMissileShuttle>> MISSILE_SHUTTLE =
            ENTITIES.register("missile_shuttle", () -> EntityType.Builder.<EntityMissileShuttle>of(
                    EntityMissileShuttle::new, MobCategory.MISC).sized(1.5F, 1.5F).build("missile_shuttle"));



    public static final RegistryObject<EntityType<EntityWaterSplash>> WATER_SPLASH =
            ENTITIES.register("water_splash",
                    () -> EntityType.Builder.<EntityWaterSplash>of(EntityWaterSplash::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(RefStrings.MODID + ":water_splash"));
}