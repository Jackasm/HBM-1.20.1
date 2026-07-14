package com.hbm.sound;

import com.hbm.util.RefStrings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.util.ResLocation.ResLocation;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RefStrings.MODID);


    public static final RegistryObject<SoundEvent> FILING_CABINET_OPEN =
            SOUNDS.register("block.filing_cabinet.open",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "block.filing_cabinet.open")));

    public static final RegistryObject<SoundEvent> FILING_CABINET_CLOSE =
            SOUNDS.register("block.filing_cabinet.close",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "block.filing_cabinet.close")));

    public static final RegistryObject<SoundEvent> RICOCHET =
            SOUNDS.register("block.ricochet",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "block.ricochet")));

    public static final RegistryObject<SoundEvent> ALARM_GAMBIT =
            SOUNDS.register("alarm.gambit",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "alarm.gambit")));

    public static final RegistryObject<SoundEvent> RELOAD_MAGSMALLINSERT =
            SOUNDS.register("weapon.reload.magsmallinsert",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.magsmallinsert")));

    public static final RegistryObject<SoundEvent> RELOAD_REVOLVERSPIN =
            SOUNDS.register("weapon.reload.revolverspin",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.revolverspin")));

    public static final RegistryObject<SoundEvent> RELOAD_REVOLVERCOCK =
            SOUNDS.register("weapon.reload.revolvercock",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.revolvercock")));

    public static final RegistryObject<SoundEvent> RELOAD_DRYFIRECLICK =
            SOUNDS.register("weapon.reload.dryfireclick",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.dryfireclick")));

    public static final RegistryObject<SoundEvent> FIRE_BLACK_POWDER =
            SOUNDS.register("weapon.fire.blackpowder",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.blackpowder")));

    public static final RegistryObject<SoundEvent> FIRE_SHOTGUN =
            SOUNDS.register("weapon.fire.shotgun",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.shotgun")));

    public static final RegistryObject<SoundEvent> FIRE_GREASEGUN =
            SOUNDS.register("weapon.fire.greasegun",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.greasegun")));

    public static final RegistryObject<SoundEvent> FIRE_PISTOL =
            SOUNDS.register("weapon.fire.pistol",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.pistol")));

    public static final RegistryObject<SoundEvent> FIRE_UZI =
            SOUNDS.register("weapon.fire.uzi",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.uzi")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_CALSHOOT =
            SOUNDS.register("weapon.fire.calshoot",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.calshoot")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_LASER_GATLING =
            SOUNDS.register("weapon.fire.lasergatling",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.lasergatling")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_RIFLE_HEAVY =
            SOUNDS.register("weapon.fire.rifleheavy",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.rifleheavy")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_SHOTGUN_ALT =
            SOUNDS.register("weapon.fire.shotgunalt",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.shotgunalt")));

    public static final RegistryObject<SoundEvent> SHOTGUN_SHOOT =
            SOUNDS.register("weapon.fire.shotgunshoot",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.shotgunshoot")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_SHOTGUN_AUTO =
            SOUNDS.register("weapon.fire.shotgunauto",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.shotgunauto")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_RIFLE =
            SOUNDS.register("weapon.fire.rifle",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.rifle")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_AMAT =
            SOUNDS.register("weapon.fire.amat",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.amat")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_SILENSERSHOT =
            SOUNDS.register("weapon.fire.silencershot",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.silencershot")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_SILENCED =
            SOUNDS.register("weapon.fire.silenced",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.silenced")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_ASSAULT =
            SOUNDS.register("weapon.fire.assault",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.assault")));

    public static final RegistryObject<SoundEvent> TURRET_CHEKHOV_FIRE =
            SOUNDS.register("turret.chekhovfire",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "turret.chekhovfire")));

    public static final RegistryObject<SoundEvent> TURRET_SENTRY_FIRE =
            SOUNDS.register("turret_sentry_fire",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "turret_sentry_fire")));

    public static final RegistryObject<SoundEvent> TURRET_SENTRY_LOCKON =
            SOUNDS.register("turret_sentry_lockon",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "turret_sentry_lockon")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_44SHOOT =
            SOUNDS.register("weapon.fire.44shoot",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.44shoot")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_ABERRATOR =
            SOUNDS.register("weapon.fire.aberrator",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.aberrator")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_TESLA =
            SOUNDS.register("weapon.fire.tesla",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.tesla")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_LASER_PISTOL =
            SOUNDS.register("weapon.fire.laserpistol",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.laserpistol")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_LASER =
            SOUNDS.register("weapon.fire.laser",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.laser")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_SHREDDERCYCLE =
            SOUNDS.register("weapon.fire.shreddercycle",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.shreddercycle")));

    public static final RegistryObject<SoundEvent> RELOAD_SHOTGUNRELOAD =
            SOUNDS.register("weapon.reload.shotgunreload",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.shotgunreload")));

    public static final RegistryObject<SoundEvent> RELOAD_SHOTGUNCOCK =
            SOUNDS.register("weapon.reload.shotguncock",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.shotguncock")));

    public static final RegistryObject<SoundEvent> RELOAD_SHOTGUNCOCKOPEN =
            SOUNDS.register("weapon.reload.shotguncockopen",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.shotguncockopen")));

    public static final RegistryObject<SoundEvent> RELOAD_SHOTGUNCOCKCLOSE =
            SOUNDS.register("weapon.reload.shotguncockclose",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.shotguncockclose")));

    public static final RegistryObject<SoundEvent> RELOAD_LEVERCOCK =
            SOUNDS.register("weapon.reload.levercock",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.levercock")));

    public static final RegistryObject<SoundEvent> RELOAD_RIFLECOCK =
            SOUNDS.register("weapon.reload.riflecock",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.riflecock")));

    public static final RegistryObject<SoundEvent> RELOAD_BOLT_OPEN =
            SOUNDS.register("weapon.reload.boltopen",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.boltopen")));

    public static final RegistryObject<SoundEvent> RELOAD_BOLT_CLOSE =
            SOUNDS.register("weapon.reload.boltclose",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.boltclose")));

    public static final RegistryObject<SoundEvent> RELOAD_REVOLVERCLOSE =
            SOUNDS.register("weapon.reload.revolverclose",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.revolverclose")));

    public static final RegistryObject<SoundEvent> FOLEY_GUNWHACK =
            SOUNDS.register("weapon.foley.gunwhack",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.foley.gunwhack")));

    public static final RegistryObject<SoundEvent> RELOAD_MAGSMALLREMOVE =
            SOUNDS.register("weapon.reload.magsmallremove",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.magsmallremove")));

    public static final RegistryObject<SoundEvent> RELOAD_MAGREMOVE =
            SOUNDS.register("weapon.reload.magremove",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.magremove")));

    public static final RegistryObject<SoundEvent> RELOAD_IMPACT =
            SOUNDS.register("weapon.reload.impact",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.impact")));

    public static final RegistryObject<SoundEvent> RELOAD_MAGINSERT =
            SOUNDS.register("weapon.reload.maginsert",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.maginsert")));

    public static final RegistryObject<SoundEvent> RELOAD_OPENLATCH =
            SOUNDS.register("weapon.reload.openlatch",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.openlatch")));

    public static final RegistryObject<SoundEvent> RELOAD_PISTOLCOCK =
            SOUNDS.register("weapon.reload.pistolcock",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.pistolcock")));

    public static final RegistryObject<SoundEvent> TURRET_HOWARD_RELOAD =
            SOUNDS.register("turret.howardreload",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "turret.howardreload")));

    public static final RegistryObject<SoundEvent> EXPLOSION_SMALL_NEAR =
            SOUNDS.register("weapon.explosionsmallnear",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.explosionsmallnear")));

    public static final RegistryObject<SoundEvent> EXPLOSION_SMALL_FAR =
            SOUNDS.register("weapon.explosionsmallfar",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.explosionsmallfar")));

    public static final RegistryObject<SoundEvent> BLOCK_SQUEAKYTOY =
            SOUNDS.register("block_squeakytoy",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "block_squeakytoy")));

    public static final RegistryObject<SoundEvent> ENTITY_UFOBLAST =
            SOUNDS.register("entity.ufoblast",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "entity.ufoblast")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_CONGOLAKE =
            SOUNDS.register("weapon.fire.congolake",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.congolake")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_FLAREGUN =
            SOUNDS.register("weapon.fire.flaregun",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.flaregun")));

    public static final RegistryObject<SoundEvent> TECH_BLEEP =
            SOUNDS.register("tool.techbleep",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "tool.techbleep")));

    public static final RegistryObject<SoundEvent> TECH_BOOP =
            SOUNDS.register("tool.techboop",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "tool.techboop")));

    public static final RegistryObject<SoundEvent> RELOAD_INSERT_CANISTER =
            SOUNDS.register("weapon.reload.insertcanister",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.insertcanister")));

    public static final RegistryObject<SoundEvent> RELOAD_GLRELOAD =
            SOUNDS.register("weapon.reload.glreload",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.glreload")));

    public static final RegistryObject<SoundEvent> INSPECT_GLOPEN =
            SOUNDS.register("weapon.glopen",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.glopen")));

    public static final RegistryObject<SoundEvent> INSPECT_GLCLOSE =
            SOUNDS.register("weapon.glclose",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.glclose")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_TAURELEASE =
            SOUNDS.register("weapon.fire.taurelease",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.taurelease")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_COILGUNSHOOT =
            SOUNDS.register("weapon.fire.coilgunshoot",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.coilgunshoot")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_TAULOOP =
            SOUNDS.register("weapon.fire.tauloop",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.tauloop")));

    public static final RegistryObject<SoundEvent> WEAPON_RELOAD_COILGUN =
            SOUNDS.register("weapon.reload.coilgunreload",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.coilgunreload")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_FLAMELOOP =
            SOUNDS.register("weapon.fire.flameloop",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.flameloop")));

    public static final RegistryObject<SoundEvent> WEAPON_RELOAD_PRESSUREVALVE =
            SOUNDS.register("weapon.reload.pressure_valve",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.reload.pressure_valve")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_TAU =
            SOUNDS.register("weapon.fire.tau",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.tau")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_RPG =
            SOUNDS.register("weapon.fire.rpgshoot",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.rpgshoot")));

    public static final RegistryObject<SoundEvent> WEAPON_FIRE_LOCKON =
            SOUNDS.register("weapon.fire.lockon",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.lockon")));

    public static final RegistryObject<SoundEvent> CRATE_BREAK =
            SOUNDS.register("block.cratebreak",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "block.cratebreak")));

    public static final RegistryObject<SoundEvent> PLANE_SHOT_DOWN =
            SOUNDS.register("entity.planeshotdown",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "entity.planeshotdown")));

    public static final RegistryObject<SoundEvent> PLANE_CRASH =
            SOUNDS.register("entity.planecrash",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "entity.planecrash")));

    public static final RegistryObject<SoundEvent> BOMBER_LOOP =
            SOUNDS.register("entity.bomber_loop",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "entity.bomber_loop")));

    public static final RegistryObject<SoundEvent> BOMBER_SMALL_LOOP =
            SOUNDS.register("entity.bomber_small_loop",
            () -> SoundEvent.createVariableRangeEvent(
                    ResLocation(RefStrings.MODID, "entity.bomber_small_loop")));

    public static final RegistryObject<SoundEvent> MISSILE_TAKE_OFF =
            SOUNDS.register("weapon.missile_take_off",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.missile_take_off")));

    public static final RegistryObject<SoundEvent> BOMB_WHISTLE =
            SOUNDS.register("entity.bomb_whistle",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "entity.bomb_whistle")));

    public static final RegistryObject<SoundEvent> RADAWAY =
            SOUNDS.register("item.radaway",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "item.radaway")));

    public static final RegistryObject<SoundEvent> SYRINGE =
            SOUNDS.register("item.syringe",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "item.syringe")));

    public static final RegistryObject<SoundEvent> GASMASK_SCREW =
            SOUNDS.register("item.gasmaskscrew",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "item.gasmaskscrew")));

    public static final RegistryObject<SoundEvent> JETPACK_TANK =
            SOUNDS.register("item.jetpacktank",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "item.jetpacktank")));

    public static final RegistryObject<SoundEvent> VICE =
            SOUNDS.register("item.vice",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "item.vice")));

    public static final RegistryObject<SoundEvent> ZOMG_SHOOT =
            SOUNDS.register("weapon.fire.zomgshoot",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.fire.zomgshoot")));

    public static final RegistryObject<SoundEvent> MUKE_EXPLOSION =
            SOUNDS.register("weapon.mukeexplosion",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "weapon.mukeexplosion")));

    public static final RegistryObject<SoundEvent> BROADCAST_1 =
            SOUNDS.register("block.broadcast1",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "block.broadcast1")));
    public static final RegistryObject<SoundEvent> BROADCAST_2 =
            SOUNDS.register("block.broadcast2",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "block.broadcast2")));
    public static final RegistryObject<SoundEvent> BROADCAST_3 =
            SOUNDS.register("block.broadcast3",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResLocation(RefStrings.MODID, "block.broadcast3")));

    public static final RegistryObject<SoundEvent> GEIGER_1 = SOUNDS.register("tool.geiger1",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "tool.geiger1")));
    public static final RegistryObject<SoundEvent> GEIGER_2 = SOUNDS.register("tool.geiger2",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "tool.geiger2")));
    public static final RegistryObject<SoundEvent> GEIGER_3 = SOUNDS.register("tool.geiger3",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "tool.geiger3")));
    public static final RegistryObject<SoundEvent> GEIGER_4 = SOUNDS.register("tool.geiger4",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "tool.geiger4")));
    public static final RegistryObject<SoundEvent> GEIGER_5 = SOUNDS.register("tool.geiger5",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "tool.geiger5")));
    public static final RegistryObject<SoundEvent> GEIGER_6 = SOUNDS.register("tool.geiger6",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "tool.geiger6")));

    public static final RegistryObject<SoundEvent> UPGRADE_PLUG = SOUNDS.register("item.upgrade_plug",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "item.upgrade_plug")));
    public static final RegistryObject<SoundEvent> BLOCK_BOILER = SOUNDS.register("block.boiler",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "block.boiler")));
    public static final RegistryObject<SoundEvent> BOILER_GROAN = SOUNDS.register("block.boiler_groan",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "block.boiler_groan")));

    public static final RegistryObject<SoundEvent> PLAYER_VOMIT = SOUNDS.register("player.vomit",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "player.vomit")));
    public static final RegistryObject<SoundEvent> PLAYER_COUGH = SOUNDS.register("player.cough",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "player.cough")));
    public static final RegistryObject<SoundEvent> ROCKET_FLAME = SOUNDS.register("weapon.rocket_flame",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.rocket_flame")));
    public static final RegistryObject<SoundEvent> STEP_POWERED = SOUNDS.register("step_powered",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "step_powered")));
    public static final RegistryObject<SoundEvent> NUCLEAR_EXPLOSION = SOUNDS.register("weapon.nuclear_explosion",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.nuclear_explosion")));
    public static final RegistryObject<SoundEvent> FSTBMB_START = SOUNDS.register("weapon.fstbmb_start",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.fstbmb_start")));
    public static final RegistryObject<SoundEvent> STEP_METAL = SOUNDS.register("footsteps.step_metal",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "footsteps.step_metal")));
    public static final RegistryObject<SoundEvent> STEP_IRON_JUMP = SOUNDS.register("footsteps.step_iron_jump",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "footsteps.step_iron_jump")));
    public static final RegistryObject<SoundEvent> STEP_IRON_LAND = SOUNDS.register("footsteps.step_iron_land",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "footsteps.step_iron_land")));
    public static final RegistryObject<SoundEvent> IMMOLATOR_SHOOT = SOUNDS.register("weapon.immolator_shoot",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.immolator_shoot")));

    public static final RegistryObject<SoundEvent> BONK = SOUNDS.register("weapon.bonk",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.bonk")));
    public static final RegistryObject<SoundEvent> BANG = SOUNDS.register("weapon.bang",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.bang")));
    public static final RegistryObject<SoundEvent> SLICE = SOUNDS.register("weapon.slice",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.slice")));
    public static final RegistryObject<SoundEvent> WHACK = SOUNDS.register("weapon.whack",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.whack")));
    public static final RegistryObject<SoundEvent> STOP = SOUNDS.register("weapon.stop",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.stop")));
    public static final RegistryObject<SoundEvent> KAPENG = SOUNDS.register("weapon.kapeng",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.kapeng")));
    public static final RegistryObject<SoundEvent> CHAINSAW = SOUNDS.register("weapon.chainsaw",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.chainsaw")));
    public static final RegistryObject<SoundEvent> BLOCK_BOBBLE = SOUNDS.register("block.bobble",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "block.bobble")));
    public static final RegistryObject<SoundEvent> SPRAY = SOUNDS.register("tool.spray",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "tool.spray")));
    public static final RegistryObject<SoundEvent> REPAIR = SOUNDS.register("tool.repair",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "tool.repair")));
    public static final RegistryObject<SoundEvent> DOOR_OPEN = SOUNDS.register("door_open",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "door_open")));
    public static final RegistryObject<SoundEvent> DOOR_CLOSE = SOUNDS.register("door_close",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "door_close")));
    public static final RegistryObject<SoundEvent> CENTRIFUGE_OPERATE = SOUNDS.register("centrifuge_operate",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "centrifuge_operate")));
    public static final RegistryObject<SoundEvent> DIESEL_ENGINE = SOUNDS.register("diesel_engine",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "diesel_engine")));
    public static final RegistryObject<SoundEvent> STEAM_ENGINE_OPERATE = SOUNDS.register("steam_engine_operate",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "steam_engine_operate")));
    public static final RegistryObject<SoundEvent> WARN_OVERSPEED = SOUNDS.register("warn_overspeed",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "warn_overspeed")));
    public static final RegistryObject<SoundEvent> CRUCIBLE_SWING = SOUNDS.register("crucible_swing",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "crucible_swing")));
    public static final RegistryObject<SoundEvent> FIRE_FATMAN = SOUNDS.register("fire_fatman",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "fire_fatman")));
    public static final RegistryObject<SoundEvent> FIRE_FOLLY = SOUNDS.register("fire_folly",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "fire_folly")));
    public static final RegistryObject<SoundEvent> EXTINGUISHER = SOUNDS.register("extinguisher",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "extinguisher")));
    public static final RegistryObject<SoundEvent> GRENADE = SOUNDS.register("grenade",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "grenade")));
    public static final RegistryObject<SoundEvent> INSERT_ROCKET = SOUNDS.register("insert_rocket",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "insert_rocket")));
    public static final RegistryObject<SoundEvent> SCREW = SOUNDS.register("weapon.reload.screw",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.reload.screw")));
    public static final RegistryObject<SoundEvent> FATMAN_FULL = SOUNDS.register("weapon.reload.fatman_full",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.reload.fatman_full")));
    public static final RegistryObject<SoundEvent> SMACK = SOUNDS.register("weapon.fire.smack",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon.fire.smack")));
    public static final RegistryObject<SoundEvent> WEAPON_BOAT = SOUNDS.register("weapon_boat",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "weapon_boat")));
    public static final RegistryObject<SoundEvent> POTATOES_RANDOM = SOUNDS.register("potatoes_random",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "potatoes_random")));
    public static final RegistryObject<SoundEvent> OLD_EXPLOSION = SOUNDS.register("entity.old_explosion",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "entity.old_explosion")));
    public static final RegistryObject<SoundEvent> UNPACK = SOUNDS.register("item.unpack",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "item.unpack")));
    public static final RegistryObject<SoundEvent> ALARM_CHIME = SOUNDS.register("alarm.chime",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "alarm.chime")));
    public static final RegistryObject<SoundEvent> FLAMETHROWER_SHOOT = SOUNDS.register("flamethrower_shoot",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "flamethrower_shoot")));
    public static final RegistryObject<SoundEvent> SYRINGE_USE = SOUNDS.register("syringe_use",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "syringe_use")));
    public static final RegistryObject<SoundEvent> SLICER = SOUNDS.register("slicer",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "slicer")));
    public static final RegistryObject<SoundEvent> FOLLY_AQUIRED = SOUNDS.register("folly_aquired",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "folly_aquired")));
    public static final RegistryObject<SoundEvent> ROBIN_EXPLOSION = SOUNDS.register("robin_explosion",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "robin_explosion")));
    public static final RegistryObject<SoundEvent> MISSILE_TAKEOFF = SOUNDS.register("missile_takeoff",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "missile_takeoff")));
    public static final RegistryObject<SoundEvent> WGH_STOP = SOUNDS.register("wgh_stop",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "wgh_stop")));
    public static final RegistryObject<SoundEvent> WGH_START = SOUNDS.register("wgh_start",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "wgh_start")));
    public static final RegistryObject<SoundEvent> GARAGE_STOP = SOUNDS.register("garage_stop",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "garage_stop")));
    public static final RegistryObject<SoundEvent> GARAGE_MOVE = SOUNDS.register("garage_move",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "garage_move")));
    public static final RegistryObject<SoundEvent> SONAR_PING = SOUNDS.register("sonar_ping",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "sonar_ping")));
    public static final RegistryObject<SoundEvent> CRATE_OPEN = SOUNDS.register("crate_open",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "crate_open")));
    public static final RegistryObject<SoundEvent> CRATE_CLOSE = SOUNDS.register("crate_close",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "crate_close")));
    public static final RegistryObject<SoundEvent> BOLTGUN = SOUNDS.register("boltgun",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "boltgun")));
    public static final RegistryObject<SoundEvent> NULL_CHOPPER = SOUNDS.register("nullchopper",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "nullchopper")));

    public static final RegistryObject<SoundEvent> OSIPR_SHOOT = SOUNDS.register("osiprshoot",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "osiprshoot")));

    public static final RegistryObject<SoundEvent> CHOPPER_CHARGE = SOUNDS.register("choppercharge",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "choppercharge")));

    public static final RegistryObject<SoundEvent> CHOPPER_DROP = SOUNDS.register("chopperdrop",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "chopperdrop")));

    public static final RegistryObject<SoundEvent> NULL_CRASHING = SOUNDS.register("nullcrashing",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "nullcrashing")));

    public static final RegistryObject<SoundEvent> CHOPPER_DAMAGE = SOUNDS.register("chopperdamage",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "chopperdamage")));

    public static final RegistryObject<SoundEvent> NULL_MINE = SOUNDS.register("nullmine",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "nullmine")));

    public static final RegistryObject<SoundEvent> BOMB_DET = SOUNDS.register("bomb_det",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "bomb_det")));

    public static final RegistryObject<SoundEvent> BALLS_LASER = SOUNDS.register("balls_laser",
            () -> SoundEvent.createVariableRangeEvent(ResLocation(RefStrings.MODID, "balls_laser")));



    private static final java.util.Map<String, SoundEvent> SOUND_CACHE = new java.util.HashMap<>();

    public static SoundEvent getCachedSound(String soundName) {
        return SOUND_CACHE.computeIfAbsent(soundName, key -> {
            // Убираем префикс hbm: если есть
            if (key.startsWith("hbm:")) {
                key = key.substring(4);
            }

            // Пробуем разные варианты
            ResourceLocation location = ResLocation(RefStrings.MODID, key);
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(location);

            // Если не нашли, пробуем стандартные варианты
            if (sound == null) {
                // Вариант 1: как есть
                sound = ForgeRegistries.SOUND_EVENTS.getValue(location);

                // Вариант 2: с weapon. префиксом
                if (sound == null && !key.startsWith("weapon.")) {
                    location = ResLocation(RefStrings.MODID, "weapon." + key);
                    sound = ForgeRegistries.SOUND_EVENTS.getValue(location);
                }

                // Вариант 3: с block. префиксом
                if (sound == null && !key.startsWith("block.")) {
                    location = ResLocation(RefStrings.MODID, "block." + key);
                    sound = ForgeRegistries.SOUND_EVENTS.getValue(location);
                }

                // Вариант 4: с item. префиксом
                if (sound == null && !key.startsWith("item.")) {
                    location = ResLocation(RefStrings.MODID, "item." + key);
                    sound = ForgeRegistries.SOUND_EVENTS.getValue(location);
                }
            }

            if (sound == null) {
                System.err.println("[HBM] Sound event not found after all attempts: " + key);
                // Возвращаем заглушку чтобы не крашилось
                return SOUNDS.getEntries().iterator().next().get();
            }

            return sound;
        });
    }
}