package com.hbm.client;

import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.*;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import com.hbm.particle.*;
import com.hbm.render.block.*;
import com.hbm.render.entity.effect.*;
import com.hbm.render.entity.item.*;
import com.hbm.render.entity.mob.*;
import com.hbm.render.entity.rocket.*;
import com.hbm.render.item.weapon.sedna.RegistryGunRender;
import com.hbm.render.projectile.*;
import com.hbm.render.tileentity.*;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.RefStrings;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;

import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistry {

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticles.BLACK_POWDER_SMOKE.get(),
                new ParticleBlackPowderSmoke.Factory());

        event.registerSpecial(ModParticles.BLACK_POWDER_SPARK.get(),
                new ParticleBlackPowderSpark.Factory());

        event.registerSpecial(ModParticles.EXPLOSION_SMALL.get(),
                new ParticleExplosionSmall.Factory());

        event.registerSpecial(ModParticles.PLASMA_BLAST.get(),
                new ParticlePlasmaBlast.Factory());

        event.registerSpecial(ModParticles.GIBLET.get(),
                new ParticleGiblet.Factory());

        event.registerSpecial(ModParticles.ASHES.get(),
                new ParticleAshes.Factory());

        event.registerSpecial(ModParticles.COOLING_TOWER.get(),
                new ParticleCoolingTower.Factory());

        event.registerSpriteSet(ModParticles.EX_SMOKE.get(),
                spriteSet -> new ParticleExSmoke.Factory());

        event.registerSpriteSet(ModParticles.DIGAMMA_SMOKE.get(),
                spriteSet -> new ParticleDigammaSmoke.Factory());

        event.registerSpriteSet(ModParticles.PARTICLE_CLOUD.get(),
                spriteSet -> new ParticleCloud.Factory());

        event.registerSpriteSet(ModParticles.FOAM.get(),
                spriteSet -> new ParticleFoam.Factory());

        event.registerSpriteSet(ModParticles.RADIATION_FOG.get(),
                spriteSet -> new ParticleRadiationFog.Factory());

        event.registerSpecial(ModParticles.SMOKE_PLUME.get(), new ParticleSmokePlume.Factory());

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_PRESS.get(), PressRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.BARREL.get(), BarrelRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_FLUID_TANK.get(), FluidTankRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.FILING_CABINET.get(), FileCabinetRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.LAMP_DEMON.get(), RenderLampDemon::new);
        event.registerBlockEntityRenderer(ModTileEntity.SIMPLE_OBJ_BLOCK.get(), SimpleOBJBlockRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.SUPPLY_CRATE.get(), SupplyCrateRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.LANDMINE.get(), LandmineRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.FLUID_DUCT.get(), RenderFluidDuct::new);
        event.registerBlockEntityRenderer(ModTileEntity.FLUID_DUCT_BOX.get(), RenderFluidDuctBox::new);
        event.registerBlockEntityRenderer(ModTileEntity.FLUID_DUCT_EXHAUST.get(), RenderFluidDuctBox::new);
        event.registerBlockEntityRenderer(ModTileEntity.ASHPIT.get(), AshpitRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.HEATER_FIREBOX.get(), HeaterFireboxRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.HEATER_OVEN.get(), HeaterOvenRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.HEATER_OILBURNER.get(), OilburnerRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.FURNACE_STEEL.get(), FurnaceSteelRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.FURNACE_IRON.get(), FurnaceIronRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.FURNACE_COMBINATION.get(), FurnaceCombinationRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.SAWMILL.get(), SawmillRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.HEAT_BOILER.get(), BoilerRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.AUTOSAW.get(), AutosawRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.BROADCASTER.get(), DecoBlockRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.RADIOREC.get(), DecoBlockRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.RADIOBOX.get(), DecoBlockRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.DECO.get(), DecoBlockRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.SAT_DOCK.get(), DecoBlockRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.GEIGER.get(), DecoBlockRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.DET_CORD.get(), DetCordRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.TURRET_SENTRY.get(), RenderTurretSentry::new);
        event.registerBlockEntityRenderer(ModTileEntity.TURRET_SENTRY_DAMAGED.get(), RenderTurretSentryDamaged::new);
        event.registerBlockEntityRenderer(ModTileEntity.CARGO_CONTAINER.get(), CargoContainerRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_DIESEL.get(), DieselGenRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_CENTRIFUGE.get(), CentrifugeRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.GAS_CENTRIFUGE.get(), CentrifugeRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.RED_CABLE.get(), RedCableRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_PUMP_STEAM.get(), PumpRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_PUMP_ELECTRIC.get(), PumpRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_STIRLING.get(), RenderStirling::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_STEAM_ENGINE.get(), RenderSteamEngine::new);
        event.registerBlockEntityRenderer(ModTileEntity.CONDUIT.get(), ConduitRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntity.RED_CONNECTOR.get(), RenderConnector::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_ROTARY_FURNACE.get(), RenderRotaryFurnace::new);
        event.registerBlockEntityRenderer(ModTileEntity.CRUCIBLE.get(), RenderCrucible::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_CRYSTALLIZER.get(), RenderCrystallizer::new);
        event.registerBlockEntityRenderer(ModTileEntity.FOUNDRY_MOLD.get(), RenderFoundryMold::new);
        event.registerBlockEntityRenderer(ModTileEntity.FOUNDRY_BASIN.get(), RenderFoundryBasin::new);
        event.registerBlockEntityRenderer(ModTileEntity.FOUNDRY_CHANNEL.get(), RenderFoundryChannel::new);
        event.registerBlockEntityRenderer(ModTileEntity.FOUNDRY_OUTLET.get(), RenderFoundryOutlet::new);
        event.registerBlockEntityRenderer(ModTileEntity.FOUNDRY_SLAGTAP.get(), RenderFoundryOutlet::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_SOLDERING_STATION.get(), RenderSolderingStation::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_ARC_WELDER.get(), RenderArcWelder::new);
        event.registerBlockEntityRenderer(ModTileEntity.DECO_BLOCK_ALT_F.get(), RenderDecoBlockAlt::new);
        event.registerBlockEntityRenderer(ModTileEntity.CRASHED_BOMB.get(), RenderCrashedBomb::new);
        event.registerBlockEntityRenderer(ModTileEntity.FLOODLIGHT.get(), RenderFloodlight::new);
        event.registerBlockEntityRenderer(ModTileEntity.ZIRNOX_DESTROYED.get(), RenderZirnoxDestroyed::new);
        event.registerBlockEntityRenderer(ModTileEntity.CAPACITOR.get(), RenderCapacitor::new);
        event.registerBlockEntityRenderer(ModTileEntity.TESLA.get(), RenderTesla::new);
        event.registerBlockEntityRenderer(ModTileEntity.NUKE_CUSTOM.get(), RenderNukeCustom::new);
        event.registerBlockEntityRenderer(ModTileEntity.NUKE_N2.get(), RenderNukeN2::new);
        event.registerBlockEntityRenderer(ModTileEntity.SNOWGLOBE.get(), RenderSnowglobe::new);
        event.registerBlockEntityRenderer(ModTileEntity.PLUSHIE.get(), RenderPlushie::new);
        event.registerBlockEntityRenderer(ModTileEntity.LAUNCH_PAD.get(), RenderLaunchPad::new);
        event.registerBlockEntityRenderer(ModTileEntity.SOLAR_BOILER.get(), RenderSolarBoiler::new);
        event.registerBlockEntityRenderer(ModTileEntity.SOLAR_MIRROR.get(), RenderMirror::new);
        event.registerBlockEntityRenderer(ModTileEntity.MULTIBLOCK.get(), RenderMultiblock::new);
        event.registerBlockEntityRenderer(ModTileEntity.COMPACT_LAUNCHER.get(), RenderCompactLauncher::new);
        event.registerBlockEntityRenderer(ModTileEntity.LAUNCH_TABLE.get(), RenderLaunchTable::new);
        event.registerBlockEntityRenderer(ModTileEntity.LAUNCH_PAD_RUSTED.get(), RenderLaunchPadRusted::new);
        event.registerBlockEntityRenderer(ModTileEntity.RADAR_NT.get(), RenderRadar::new);
        event.registerBlockEntityRenderer(ModTileEntity.RADAR_SCREEN.get(), RenderRadarScreen::new);
        event.registerBlockEntityRenderer(ModTileEntity.MACHINE_BLAST_FURNACE.get(), RenderBlastFurnace::new);


//======================================== Entity ========================================================================

        event.registerEntityRenderer(ModEntities.WATER_SPLASH.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.MIST.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.NUKE_EXPLOSION_MK3.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.EMP.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.WAYPOINT.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.NUKE_EXPLOSION_MK5.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.BALEFIRE.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.FALLING_NUKE.get(), NoopRenderer::new);

        event.registerEntityRenderer(ModEntities.BULLET.get(), RenderBulletMK4::new);
        event.registerEntityRenderer(ModEntities.BULLET_BEAM.get(), BeamEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.FIRE_LINGERING.get(), RenderFireLingering::new);
        event.registerEntityRenderer(ModEntities.CLOUD_FLEIJA.get(), RenderCloudFleija::new);

        event.registerEntityRenderer(ModEntities.SHRAPNEL.get(), RenderShrapnel::new);
        event.registerEntityRenderer(ModEntities.ROCKET.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.MAN_CORE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.BULLET_LEGACY.get(), RenderRocket::new);
        event.registerEntityRenderer(ModEntities.SCHRAB.get(), RenderFlare::new);
        event.registerEntityRenderer(ModEntities.RAINBOW.get(), RenderRainbow::new);
        event.registerEntityRenderer(ModEntities.BLACK_HOLE.get(), RenderBlackHole::new);
        event.registerEntityRenderer(ModEntities.VORTEX.get(), RenderBlackHole::new);
        event.registerEntityRenderer(ModEntities.RAGING_VORTEX.get(), RenderBlackHole::new);
        event.registerEntityRenderer(ModEntities.QUASAR.get(), RenderQuasar::new);
        event.registerEntityRenderer(ModEntities.RUBBLE.get(), RenderRubble::new);
        event.registerEntityRenderer(ModEntities.BOMBER.get(), RenderBomber::new);
        event.registerEntityRenderer(ModEntities.BOMBLET_ZETA.get(), RenderBombletZeta::new);
        event.registerEntityRenderer(ModEntities.BOXCAR.get(), RenderBoxcar::new);
        event.registerEntityRenderer(ModEntities.BUILDING.get(), RenderBoxcar::new);
        event.registerEntityRenderer(ModEntities.TORPEDO.get(), RenderBoxcar::new);
        event.registerEntityRenderer(ModEntities.PINK_CLOUD_FX.get(),
                context -> new MultiCloudRenderer(context, "pc"));
        event.registerEntityRenderer(ModEntities.CLOUD_FX.get(),
                context -> new MultiCloudRenderer(context, "cloud"));
        event.registerEntityRenderer(ModEntities.ORANGE_FX.get(),
                context -> new MultiCloudRenderer(context, "orange"));

        event.registerEntityRenderer(ModEntities.EMP_BLAST.get(), RenderEMPBlast::new);

        event.registerEntityRenderer(ModEntities.FBI.get(), RenderFBI::new);
        event.registerEntityRenderer(ModEntities.FBI_DRONE.get(), RenderFBIDrone::new);
        event.registerEntityRenderer(ModEntities.UFO.get(), RenderUFO::new);
        event.registerEntityRenderer(ModEntities.MASKMAN.get(), RenderMaskMan::new);
        event.registerEntityRenderer(ModEntities.METEOR.get(), RenderMeteor::new);
        event.registerEntityRenderer(ModEntities.GHOST.get(), RenderGhost::new);
        event.registerEntityRenderer(ModEntities.RADBEAST.get(), RenderRADBeast::new);

        event.registerEntityRenderer(ModEntities.CREEPER_NUCLEAR.get(),
                (context) -> new RenderCreeperUniversal<EntityCreeperNuclear>(
                        context,
                        "textures/entity/creeper.png",
                        "textures/entity/creeper_armor.png"
                ).setSwellMod(5.0F)
        );

        event.registerEntityRenderer(ModEntities.CREEPER_TAINTED.get(),
                (context) -> new RenderCreeperUniversal<>(
                        context,
                        "textures/entity/creeper_tainted.png",
                        "textures/entity/creeper_armor_taint.png"
                )
        );

        event.registerEntityRenderer(ModEntities.CREEPER_PHOSGENE.get(),
                (context) -> new RenderCreeperUniversal<>(
                        context,
                        "textures/entity/creeper_phosgene.png",
                        "textures/entity/creeper/creeper_armor.png"
                )
        );

        event.registerEntityRenderer(ModEntities.CREEPER_VOLATILE.get(),
                (context) -> new RenderCreeperUniversal<>(
                        context,
                        "textures/entity/creeper_volatile.png",
                        "textures/entity/creeper/creeper_armor.png"
                )
        );

        event.registerEntityRenderer(ModEntities.CREEPER_GOLD.get(),
                (context) -> new RenderCreeperUniversal<>(
                        context,
                        "textures/entity/creeper_gold.png",
                        "textures/entity/creeper/creeper_armor.png"
                )
        );



        event.registerEntityRenderer(ModEntities.GLYPHID.get(),
                context -> new RenderGlyphidUniversal(context, 1.0F));
        event.registerEntityRenderer(ModEntities.GLYPHID_DIGGER.get(),
                context -> new RenderGlyphidUniversal(context, 1.3F));
        event.registerEntityRenderer(ModEntities.GLYPHID_NUCLEAR.get(),
                context -> new RenderGlyphidUniversal(context, 2.0F));
        event.registerEntityRenderer(ModEntities.GLYPHID_SCOUT.get(),
                context -> new RenderGlyphidUniversal(context, 0.75F));
        event.registerEntityRenderer(ModEntities.GLYPHID_BRAWLER.get(),
                context -> new RenderGlyphidUniversal(context, 1.25F));
        event.registerEntityRenderer(ModEntities.GLYPHID_BOMBARDIER.get(),
                context -> new RenderGlyphidUniversal(context, 1.0F));
        event.registerEntityRenderer(ModEntities.GLYPHID_BLASTER.get(),
                context -> new RenderGlyphidUniversal(context, 1.25F));
        event.registerEntityRenderer(ModEntities.GLYPHID_BEHEMOTH.get(),
                context -> new RenderGlyphidUniversal(context, 1.5F));
        event.registerEntityRenderer(ModEntities.GLYPHID_BRENDA.get(),
                context -> new RenderGlyphidUniversal(context, 1.5F));

        event.registerEntityRenderer(ModEntities.ACID_BOMB.get(), RenderAcidBomb::new);
        event.registerEntityRenderer(ModEntities.CHEMICAL.get(), RenderChemical::new);
        event.registerEntityRenderer(ModEntities.DUCK.get(), RenderDuck::new);
        event.registerEntityRenderer(ModEntities.QUACKOS.get(), RenderQuackos::new);

        event.registerEntityRenderer(ModEntities.TNT_PRIMED_BASE.get(), RenderTNTPrimedBase::new);

        event.registerEntityRenderer(ModEntities.NUKE_TOREX.get(), RenderNukeTorex::new);


        event.registerEntityRenderer(ModEntities.FALLOUT_RAIN.get(), RenderFallout::new);
        event.registerEntityRenderer(ModEntities.COG.get(), RenderCog::new);
        event.registerEntityRenderer(ModEntities.CUSTOM_SKELETON.get(), CustomSkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.COIN.get(), RenderCoin::new);

        event.registerEntityRenderer(ModEntities.C130.get(), RenderC130::new);
        event.registerEntityRenderer(ModEntities.PARACHUTE_CRATE.get(), RenderParachuteCrate::new);

        event.registerEntityRenderer(ModEntities.FALLING_BLOCK_NT.get(), RenderFallingBlockNT::new);

        event.registerEntityRenderer(ModEntities.GRENADE_GENERIC.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_GENERIC.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_STRONG.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_STRONG.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_FRAG.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_FRAG.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_FIRE.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_FIRE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_CLUSTER.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_CLUSTER.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_ELECTRIC.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_ELECTRIC.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_POISON.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_POISON.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_GAS.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_GAS.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_SCHRABIDIUM.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_SCHRABIDIUM.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_NUKE.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_NUKE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_NUCLEAR.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_NUCLEAR.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_PULSE.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_PULSE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_PLASMA.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_PLASMA.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_TAU.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_TAU.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_LEMON.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_LEMON.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_ZOMG.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_ZOMG.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_SHRAPNEL.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_SHRAPNEL.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_BLACK_HOLE.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_BLACK_HOLE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_GASCAN.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_GASCAN.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_CLOUD.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_CLOUD.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_PC.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_PINK_CLOUD.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_SMART.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_SMART.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_MIRV.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_MIRV.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_BREACH.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_BREACH.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_BURST.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_BURST.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_GENERIC.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_GENERIC.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_HE.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_HE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_BOUNCY.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_BOUNCY.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_STICKY.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_STICKY.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_IMPACT.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_IMPACT.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_INCENDIARY.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_INCENDIARY.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_TOXIC.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_TOXIC.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_CONCUSSION.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_CONCUSSION.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_BRIMSTONE.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_BRIMSTONE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_MYSTERY.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_MYSTERY.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_SPARK.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_SPARK.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_HOPWIRE.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_HOPWIRE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_IF_NULL.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_IF_NULL.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.WASTE_PEARL.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.NUCLEAR_WASTE_PEARL.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_DYNAMITE.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.STICK_DYNAMITE.get()), 1.0F));
        event.registerEntityRenderer(ModEntities.GRENADE_KYIV.get(),
                (context) -> new RenderGrenadeItem<>(context, new ItemStack(ModItems.GRENADE_KYIV.get()), 1.0F));

        event.registerEntityRenderer(ModEntities.GRENADE_FLARE.get(), RenderFlare::new);
        event.registerEntityRenderer(ModEntities.GRENADE_MK2.get(), RenderGrenade::new);
        event.registerEntityRenderer(ModEntities.GRENADE_ASCHRAB.get(), RenderGrenade::new);
        event.registerEntityRenderer(ModEntities.BURNING_FOEQ.get(), RenderFOEQ::new);
        event.registerEntityRenderer(ModEntities.TOM.get(), RenderTom::new);
        event.registerEntityRenderer(ModEntities.CLOUD_TOM.get(), RenderCloudTom::new);
        event.registerEntityRenderer(ModEntities.MINER_ROCKET.get(), RenderMinerRocket::new);
        event.registerEntityRenderer(ModEntities.BOAT_RUBBER.get(), RenderBoatRubber::new);

        event.registerEntityRenderer(ModEntities.MISSILE_TAINT.get(), RenderMissileTaint::new);
        event.registerEntityRenderer(ModEntities.MISSILE_MICRO.get(), RenderMissileTaint::new);
        event.registerEntityRenderer(ModEntities.MISSILE_BHOLE.get(), RenderMissileTaint::new);
        event.registerEntityRenderer(ModEntities.MISSILE_SCHRABIDIUM.get(), RenderMissileTaint::new);
        event.registerEntityRenderer(ModEntities.MISSILE_EMP.get(), RenderMissileTaint::new);
        event.registerEntityRenderer(ModEntities.MISSILE_TEST.get(), RenderMissileTaint::new);

        event.registerEntityRenderer(ModEntities.MISSILE_GENERIC.get(), RenderMissileGeneric::new);
        event.registerEntityRenderer(ModEntities.MISSILE_INCENDIARY.get(), RenderMissileGeneric::new);
        event.registerEntityRenderer(ModEntities.MISSILE_CLUSTER.get(), RenderMissileGeneric::new);
        event.registerEntityRenderer(ModEntities.MISSILE_BUSTER.get(), RenderMissileGeneric::new);
        event.registerEntityRenderer(ModEntities.MISSILE_DECOY.get(), RenderMissileGeneric::new);
        event.registerEntityRenderer(ModEntities.MISSILE_ANTI_BALLISTIC.get(), RenderMissileGeneric::new);
        event.registerEntityRenderer(ModEntities.MISSILE_STEALTH.get(), RenderMissileGeneric::new);

        event.registerEntityRenderer(ModEntities.MISSILE_BURST.get(), RenderMissileHuge::new);
        event.registerEntityRenderer(ModEntities.MISSILE_INFERNO.get(), RenderMissileHuge::new);
        event.registerEntityRenderer(ModEntities.MISSILE_RAIN.get(), RenderMissileHuge::new);
        event.registerEntityRenderer(ModEntities.MISSILE_DRILL.get(), RenderMissileHuge::new);

        event.registerEntityRenderer(ModEntities.MISSILE_STRONG.get(), RenderMissileStrong::new);
        event.registerEntityRenderer(ModEntities.MISSILE_INCENDIARY_STRONG.get(), RenderMissileStrong::new);
        event.registerEntityRenderer(ModEntities.MISSILE_CLUSTER_STRONG.get(), RenderMissileStrong::new);
        event.registerEntityRenderer(ModEntities.MISSILE_BUSTER_STRONG.get(), RenderMissileStrong::new);
        event.registerEntityRenderer(ModEntities.MISSILE_EMP_STRONG.get(), RenderMissileStrong::new);

        event.registerEntityRenderer(ModEntities.MISSILE_NUCLEAR.get(), RenderMissileNuclear::new);
        event.registerEntityRenderer(ModEntities.MISSILE_NUCLEAR_CLUSTER.get(), RenderMissileNuclear::new);
        event.registerEntityRenderer(ModEntities.MISSILE_VOLCANO.get(), RenderMissileNuclear::new);
        event.registerEntityRenderer(ModEntities.MISSILE_DOOMSDAY.get(), RenderMissileNuclear::new);
        event.registerEntityRenderer(ModEntities.MISSILE_DOOMSDAY_RUSTED.get(), RenderMissileNuclear::new);
    }

    @SubscribeEvent
    public static void registerItemRenderers(RegisterClientReloadListenersEvent event) {

        RegistryGunRender.init();
        ModToolItems.register();
    }
}