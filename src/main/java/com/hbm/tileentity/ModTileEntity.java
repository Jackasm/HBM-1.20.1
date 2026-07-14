package com.hbm.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.MachineBattery;
import com.hbm.blocks.machine.MachineStirling;
import com.hbm.entity.DecoCTBlockEntity;
import com.hbm.tileentity.block.*;
import com.hbm.tileentity.bomb.*;
import com.hbm.tileentity.conduit.ConduitTileEntity;
import com.hbm.tileentity.deco.*;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.pile.TileEntityPileBreedingFuel;
import com.hbm.tileentity.machine.pile.TileEntityPileFuel;
import com.hbm.tileentity.machine.pile.TileEntityPileNeutronDetector;
import com.hbm.tileentity.machine.pile.TileEntityPileSource;
import com.hbm.tileentity.network.*;
import com.hbm.tileentity.storage.*;
import com.hbm.tileentity.turret.TileEntityTurretSentry;
import com.hbm.tileentity.turret.TileEntityTurretSentryDamaged;
import com.hbm.util.RefStrings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("ConstantConditions")
public class ModTileEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RefStrings.MODID);

    public static final RegistryObject<BlockEntityType<TileEntityMachinePress>> MACHINE_PRESS =
            BLOCK_ENTITIES.register("machine_press",
                    () -> BlockEntityType.Builder.of(TileEntityMachinePress::new,
                            ModBlocks.MACHINE_PRESS.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityBarrel>> BARREL =
            BLOCK_ENTITIES.register("barrel", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TileEntityBarrel(pos, state, 16000),
                            ModBlocks.BARREL_IRON.get(),
                            ModBlocks.BARREL_STEEL.get(),
                            ModBlocks.BARREL_ANTIMATTER.get(),
                            ModBlocks.BARREL_CORRODED.get(),
                            ModBlocks.BARREL_PLASTIC.get(),
                            ModBlocks.BARREL_TCALLOY.get()
                    ).build(null)
            );
    public static final RegistryObject<BlockEntityType<TileEntityBlastFurnace>> MACHINE_BLAST_FURNACE =
            BLOCK_ENTITIES.register("machine_blast_furnace",
                    () -> BlockEntityType.Builder.of(TileEntityBlastFurnace::new, ModBlocks.MACHINE_BLAST_FURNACE.get()).build(null));



    public static final RegistryObject<BlockEntityType<TileEntityFileCabinet>> FILING_CABINET =
            BLOCK_ENTITIES.register("filing_cabinet",
                    () -> BlockEntityType.Builder.of(TileEntityFileCabinet::new,
                            ModBlocks.FILING_CABINET.get(),
                            ModBlocks.FILING_CABINET_STEEL.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySupplyCrate>> SUPPLY_CRATE =
            BLOCK_ENTITIES.register("supply_crate",
                    () -> BlockEntityType.Builder.of(TileEntitySupplyCrate::new,
                            ModBlocks.CRATE_SUPPLIES.get(),
                            ModBlocks.CRATE_WEAPONS.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySimpleOBJ>> SIMPLE_OBJ_BLOCK =
            BLOCK_ENTITIES.register("simple_obj_block",
                    () -> BlockEntityType.Builder.of(TileEntitySimpleOBJ::new,
                            ModBlocks.STEEL_SCAFFOLD.get(),
                            ModBlocks.STEEL_GRATE.get(),
                            ModBlocks.STEEL_GRATE_WIDE.get(),
                            ModBlocks.STEEL_BEAM.get(),
                            ModBlocks.STEEL_POLES.get(),
                            ModBlocks.TAPE_RECORDER.get(),
                            ModBlocks.ANVIL_IRON.get(),
                            ModBlocks.ANVIL_LEAD.get(),
                            ModBlocks.ANVIL_STEEL.get(),
                            ModBlocks.ANVIL_ARSENIC_BRONZE.get(),
                            ModBlocks.ANVIL_BISMUTH_BRONZE.get(),
                            ModBlocks.ANVIL_DNT.get(),
                            ModBlocks.ANVIL_DESH.get(),
                            ModBlocks.ANVIL_FERROURANIUM.get(),
                            ModBlocks.ANVIL_OSMIRIDIUM.get(),
                            ModBlocks.ANVIL_SATURNITE.get(),
                            ModBlocks.ANVIL_SCHRABIDATE.get(),
                            ModBlocks.ANVIL_MURKY.get(),
                            ModBlocks.TAINT_BARREL.get(),
                            ModBlocks.LOX_BARREL.get(),
                            ModBlocks.RED_BARREL.get(),
                            ModBlocks.YELLOW_BARREL.get(),
                            ModBlocks.PINK_BARREL.get(),
                            ModBlocks.SPOTLIGHT_INCANDESCENT.get(),
                            ModBlocks.SPOTLIGHT_INCANDESCENT_OFF.get(),
                            ModBlocks.SPOTLIGHT_FLUORO.get(),
                            ModBlocks.SPOTLIGHT_FLUORO_OFF.get(),
                            ModBlocks.SPOTLIGHT_HALOGEN.get(),
                            ModBlocks.SPOTLIGHT_HALOGEN_OFF.get(),
                            ModBlocks.DECO_CRT.get()
                            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFloodlight>> FLOODLIGHT =
            BLOCK_ENTITIES.register("floodlight", () -> BlockEntityType.Builder.of(
                    TileEntityFloodlight::new, ModBlocks.FLOODLIGHT.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFloodlightBeam>> FLOODLIGHT_BEAM =
            BLOCK_ENTITIES.register("floodlight_beam", () -> BlockEntityType.Builder.of(
                    TileEntityFloodlightBeam::new, ModBlocks.FLOODLIGHT_BEAM.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityDeco>> DECO =
            BLOCK_ENTITIES.register("deco", () -> BlockEntityType.Builder.of(
                    TileEntityDeco::new,
                    ModBlocks.BOAT.get(),
                    ModBlocks.POLE_TOP.get(),
                    ModBlocks.POLE_SATELLITE_RECEIVER.get(),
                    ModBlocks.BOXCAR.get(),
                    ModBlocks.STEEL_ROOF.get(),
                    ModBlocks.STEEL_WALL.get(),
                    ModBlocks.STEEL_CORNER.get(),
                    ModBlocks.SAT_RADAR.get(),
                    ModBlocks.SAT_RESONATOR.get(),
                    ModBlocks.SAT_SCANNER.get(),
                    ModBlocks.SAT_MAPPER.get(),
                    ModBlocks.SAT_LASER.get(),
                    ModBlocks.SAT_FOEQ.get()
            ).build(null));


    public static final RegistryObject<BlockEntityType<TileEntityVolcanoCore>> VOLCANO_CORE =
            BLOCK_ENTITIES.register("volcano_core",
                    () -> BlockEntityType.Builder.of(
                            TileEntityVolcanoCore::new,
                            ModBlocks.VOLCANO_CORE.get(),
                            ModBlocks.VOLCANO_RAD_CORE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityDecoBlockAltF>> DECO_BLOCK_ALT_F =
            BLOCK_ENTITIES.register("deco_block_alt_f",
                    () -> BlockEntityType.Builder.of(
                            TileEntityDecoBlockAltF::new,
                            ModBlocks.STATUE_ELB_F.get()
                    ).build(null));


    public static final RegistryObject<BlockEntityType<TileEntityRadioRec>> RADIOREC =
            BLOCK_ENTITIES.register("radiorec",
                    () -> BlockEntityType.Builder.of(
                            TileEntityRadioRec::new,
                            ModBlocks.RADIOREC.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityBroadcaster>> BROADCASTER =
            BLOCK_ENTITIES.register("broadcaster",
                    () -> BlockEntityType.Builder.of(
                            TileEntityBroadcaster::new,
                            ModBlocks.BROADCASTER_PC.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineFluidTank>> MACHINE_FLUID_TANK =
            BLOCK_ENTITIES.register("machine_fluid_tank",
                    () -> BlockEntityType.Builder.of(
                            TileEntityMachineFluidTank::new,
                            ModBlocks.MACHINE_FLUIDTANK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityProxyCombo>> PROXY_COMBO =
            BLOCK_ENTITIES.register("proxy_combo", () ->
                    BlockEntityType.Builder.of(
                            TileEntityProxyCombo::new,
                            ModBlocks.DUMMY_BLOCK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<TileEntityPipeBaseNT>> FLUID_DUCT =
            BLOCK_ENTITIES.register("fluid_duct", () ->
                    BlockEntityType.Builder.of(
                            TileEntityPipeBaseNT::new,
                            ModBlocks.FLUID_DUCT.get(),
                            ModBlocks.FLUID_DUCT_SILVER.get(),
                            ModBlocks.FLUID_DUCT_COLORED.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<TileEntityPipeBoxNT>> FLUID_DUCT_BOX =
            BLOCK_ENTITIES.register("fluid_duct_box", () ->
                    BlockEntityType.Builder.of(
                            TileEntityPipeBoxNT::new,
                            ModBlocks.FLUID_DUCT_BOX_SILVER.get(),
                            ModBlocks.FLUID_DUCT_BOX_COPPER.get(),
                            ModBlocks.FLUID_DUCT_BOX_WHITE.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<TileEntityPipeExhaust>> FLUID_DUCT_EXHAUST =
            BLOCK_ENTITIES.register("fluid_duct_exhaust",
            () -> BlockEntityType.Builder.of(TileEntityPipeExhaust::new, ModBlocks.FLUID_DUCT_EXHAUST.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityHeaterFirebox>> HEATER_FIREBOX =
            BLOCK_ENTITIES.register("heater_firebox",
                    () -> BlockEntityType.Builder.of(
                            TileEntityHeaterFirebox::new,
                            ModBlocks.HEATER_FIREBOX.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityAshpit>> ASHPIT =
            BLOCK_ENTITIES.register("ashpit",
                    () -> BlockEntityType.Builder.of(
                            TileEntityAshpit::new,
                            ModBlocks.MACHINE_ASHPIT.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityHeaterOven>> HEATER_OVEN =
            BLOCK_ENTITIES.register("heater_oven",
                    () -> BlockEntityType.Builder.of(TileEntityHeaterOven::new,
                            ModBlocks.HEATER_OVEN.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityHeaterOilburner>> HEATER_OILBURNER =
            BLOCK_ENTITIES.register("heater_oilburner",
                    () -> BlockEntityType.Builder.of(TileEntityHeaterOilburner::new,
                            ModBlocks.HEATER_OILBURNER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFurnaceSteel>> FURNACE_STEEL =
            BLOCK_ENTITIES.register("furnace_steel",
                    () -> BlockEntityType.Builder.of(TileEntityFurnaceSteel::new,
                            ModBlocks.FURNACE_STEEL.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFurnaceIron>> FURNACE_IRON =
            BLOCK_ENTITIES.register("furnace_iron",
                    () -> BlockEntityType.Builder.of(TileEntityFurnaceIron::new,
                            ModBlocks.FURNACE_IRON.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFurnaceCombination>> FURNACE_COMBINATION =
            BLOCK_ENTITIES.register("furnace_combination",
                    () -> BlockEntityType.Builder.of(TileEntityFurnaceCombination::new,
                            ModBlocks.FURNACE_COMBINATION.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySawmill>> SAWMILL =
            BLOCK_ENTITIES.register("sawmill",
                    () -> BlockEntityType.Builder.of(TileEntitySawmill::new,
                            ModBlocks.MACHINE_SAWMILL.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityHeatBoiler>> HEAT_BOILER =
            BLOCK_ENTITIES.register("heat_boiler",
                    () -> BlockEntityType.Builder.of(TileEntityHeatBoiler::new,
                            ModBlocks.MACHINE_HEAT_BOILER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineAutosaw>> AUTOSAW =
            BLOCK_ENTITIES.register("autosaw",
                    () -> BlockEntityType.Builder.of(TileEntityMachineAutosaw::new,
                            ModBlocks.MACHINE_AUTOSAW.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityNukeCustom>> NUKE_CUSTOM =
            BLOCK_ENTITIES.register("nuke_custom",
                    () -> BlockEntityType.Builder.of(TileEntityNukeCustom::new,
                            ModBlocks.NUKE_CUSTOM.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityLandmine>> LANDMINE =
            BLOCK_ENTITIES.register("landmine", () -> BlockEntityType.Builder.of(
                    TileEntityLandmine::new,
                    ModBlocks.MINE_AP.get(),
                    ModBlocks.MINE_HE.get(),
                    ModBlocks.MINE_SHRAP.get(),
                    ModBlocks.MINE_FAT.get(),
                    ModBlocks.MINE_NAVAL.get()
            ).build(null));


    public static final RegistryObject<BlockEntityType<TileEntityBobble>> BOBBLE =
            BLOCK_ENTITIES.register("bobble", () -> BlockEntityType.Builder.of(
                    TileEntityBobble::new, ModBlocks.BOBBLEHEAD.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityDetCord>> DET_CORD =
            BLOCK_ENTITIES.register("det_cord",
                    () -> BlockEntityType.Builder.of(TileEntityDetCord::new,
                            ModBlocks.DET_CORD.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCargoContainer>> CARGO_CONTAINER =
            BLOCK_ENTITIES.register("cargo_container",
                    () -> BlockEntityType.Builder.of(TileEntityCargoContainer::new,
                                    ModBlocks.CARGO_CONTAINER.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineGasCent>> GAS_CENTRIFUGE =
            BLOCK_ENTITIES.register("machine_gas_cent",
                    () -> BlockEntityType.Builder.of(TileEntityMachineGasCent::new,
                            ModBlocks.MACHINE_GAS_CENT.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineDiesel>> MACHINE_DIESEL =
            BLOCK_ENTITIES.register("machine_diesel",
            () -> BlockEntityType.Builder.of(TileEntityMachineDiesel::new,
                    ModBlocks.MACHINE_DIESEL.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineCentrifuge>> MACHINE_CENTRIFUGE =
            BLOCK_ENTITIES.register("machine_centrifuge",
            () -> BlockEntityType.Builder.of(TileEntityMachineCentrifuge::new,
                    ModBlocks.MACHINE_CENTRIFUGE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCableBaseNT>> RED_CABLE =
            BLOCK_ENTITIES.register("red_cable",
            () -> BlockEntityType.Builder.of(TileEntityCableBaseNT::new,
                    ModBlocks.RED_CABLE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachinePumpSteam>> MACHINE_PUMP_STEAM =
            BLOCK_ENTITIES.register("machine_pump_steam",
            () -> BlockEntityType.Builder.of(TileEntityMachinePumpSteam::new,
                    ModBlocks.PUMP_STEAM.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachinePumpElectric>> MACHINE_PUMP_ELECTRIC =
            BLOCK_ENTITIES.register("machine_pump_electric",
            () -> BlockEntityType.Builder.of(TileEntityMachinePumpElectric::new,
                    ModBlocks.PUMP_ELECTRIC.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityStirling>> MACHINE_STIRLING =
            BLOCK_ENTITIES.register("machine_stirling",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        Block block = state.getBlock();
                        if (block instanceof MachineStirling stirling) {
                            return new TileEntityStirling(pos, state, stirling.type);
                        }
                        return new TileEntityStirling(pos, state, MachineStirling.StirlingType.NORMAL);
                    },
                    ModBlocks.MACHINE_STIRLING.get(),
                    ModBlocks.MACHINE_STIRLING_STEEL.get(),
                    ModBlocks.MACHINE_STIRLING_CREATIVE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySteamEngine>> MACHINE_STEAM_ENGINE =
            BLOCK_ENTITIES.register("machine_steam_engine",
            () -> BlockEntityType.Builder.of(TileEntitySteamEngine::new,
                    ModBlocks.MACHINE_STEAM_ENGINE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<ConduitTileEntity>> CONDUIT =
            BLOCK_ENTITIES.register("conduit",
            () -> BlockEntityType.Builder.of(ConduitTileEntity::new,
                    ModBlocks.CONDUIT.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityConnector>> RED_CONNECTOR =
            BLOCK_ENTITIES.register("red_connector",
                    () -> BlockEntityType.Builder.of(TileEntityConnector::new,
                            ModBlocks.RED_CONNECTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineRotaryFurnace>> MACHINE_ROTARY_FURNACE =
            BLOCK_ENTITIES.register("machine_rotary_furnace",
                    () -> BlockEntityType.Builder.of(TileEntityMachineRotaryFurnace::new,
                            ModBlocks.MACHINE_ROTARY_FURNACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCrucible>> CRUCIBLE =
            BLOCK_ENTITIES.register("crucible",
                    () -> BlockEntityType.Builder.of(TileEntityCrucible::new,
                            ModBlocks.MACHINE_CRUCIBLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFoundryMold>> FOUNDRY_MOLD =
            BLOCK_ENTITIES.register("foundry_mold",
                    () -> BlockEntityType.Builder.of(TileEntityFoundryMold::new,
                            ModBlocks.FOUNDRY_MOLD.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFoundryBasin>> FOUNDRY_BASIN =
            BLOCK_ENTITIES.register("foundry_basin",
                    () -> BlockEntityType.Builder.of(TileEntityFoundryBasin::new,
                            ModBlocks.FOUNDRY_BASIN.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFoundryChannel>> FOUNDRY_CHANNEL =
            BLOCK_ENTITIES.register("foundry_channel",
                    () -> BlockEntityType.Builder.of(TileEntityFoundryChannel::new,
                            ModBlocks.FOUNDRY_CHANNEL.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFoundryTank>> FOUNDRY_TANK =
            BLOCK_ENTITIES.register("foundry_tank",
                    () -> BlockEntityType.Builder.of(TileEntityFoundryTank::new,
                            ModBlocks.FOUNDRY_TANK.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFoundryOutlet>> FOUNDRY_OUTLET =
            BLOCK_ENTITIES.register("foundry_outlet",
                    () -> BlockEntityType.Builder.of(TileEntityFoundryOutlet::new,
                            ModBlocks.FOUNDRY_OUTLET.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFoundrySlagtap>> FOUNDRY_SLAGTAP =
            BLOCK_ENTITIES.register("foundry_slagtap",
                    () -> BlockEntityType.Builder.of(TileEntityFoundrySlagtap::new,
                            ModBlocks.FOUNDRY_SLAGTAP.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySlag>> SLAG =
            BLOCK_ENTITIES.register("slag",
                    () -> BlockEntityType.Builder.of(TileEntitySlag::new, ModBlocks.SLAG.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineSolderingStation>> MACHINE_SOLDERING_STATION =
            BLOCK_ENTITIES.register("machine_soldering_station",
                    () -> BlockEntityType.Builder.of(TileEntityMachineSolderingStation::new,
                    ModBlocks.MACHINE_SOLDERING_STATION.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineArcWelder>> MACHINE_ARC_WELDER =
            BLOCK_ENTITIES.register("machine_arc_welder",
                    () -> BlockEntityType.Builder.of(TileEntityMachineArcWelder::new,
                    ModBlocks.MACHINE_ARC_WELDER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityNukeN2>> NUKE_N2 =
            BLOCK_ENTITIES.register("nuke_n2",
                    () -> BlockEntityType.Builder.of(TileEntityNukeN2::new,
                    ModBlocks.NUKE_N2.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityDemonLamp>> LAMP_DEMON =
            BLOCK_ENTITIES.register("lamp_demon",
                    () -> BlockEntityType.Builder.of(TileEntityDemonLamp::new, ModBlocks.LAMP_DEMON.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityTurretSentry>> TURRET_SENTRY =
            BLOCK_ENTITIES.register("turret_sentry", () -> BlockEntityType.Builder.of(
                    TileEntityTurretSentry::new,
                    ModBlocks.TURRET_SENTRY.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityTurretSentryDamaged>> TURRET_SENTRY_DAMAGED =
            BLOCK_ENTITIES.register("turret_sentry_damaged", () -> BlockEntityType.Builder.of(
                    TileEntityTurretSentryDamaged::new,
                    ModBlocks.TURRET_SENTRY_DAMAGED.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<DecoCTBlockEntity>> DECO_CT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("deco_ct",
                    () -> BlockEntityType.Builder.of(DecoCTBlockEntity::new,
                            ModBlocks.DECO_RED_COPPER.get(),
                            ModBlocks.DECO_TUNGSTEN.get(),
                            ModBlocks.DECO_ALUMINIUM.get(),
                            ModBlocks.DECO_STEEL.get(),
                            ModBlocks.DECO_LEAD.get(),
                            ModBlocks.DECO_BERYLLIUM.get(),
                            ModBlocks.DECO_STAINLESS.get(),
                            ModBlocks.DECO_RUSTY_STEEL.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityBedrockOre>> BEDROCK_ORE =
            BLOCK_ENTITIES.register("bedrock_ore", () -> BlockEntityType.Builder.of(
                    TileEntityBedrockOre::new,
                    ModBlocks.ORE_BEDROCK.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCrashedBomb>> CRASHED_BOMB =
            BLOCK_ENTITIES.register("crashed_bomb",
                    () -> BlockEntityType.Builder.of(TileEntityCrashedBomb::new,
                            ModBlocks.CRASHED_BALEFIRE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineShredder>> MACHINE_SHREDDER =
            BLOCK_ENTITIES.register("machine_shredder",
                    () -> BlockEntityType.Builder.of(TileEntityMachineShredder::new,
                            ModBlocks.MACHINE_SHREDDER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCrateIron>> CRATE_IRON = BLOCK_ENTITIES.register("crate_iron",
            () -> BlockEntityType.Builder.of(TileEntityCrateIron::new, ModBlocks.CRATE_IRON.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityCrateSteel>> CRATE_STEEL = BLOCK_ENTITIES.register("crate_steel",
            () -> BlockEntityType.Builder.of(TileEntityCrateSteel::new, ModBlocks.CRATE_STEEL.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityCrateDesh>> CRATE_DESH = BLOCK_ENTITIES.register("crate_desh",
            () -> BlockEntityType.Builder.of(TileEntityCrateDesh::new, ModBlocks.CRATE_DESH.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityCrateTemplate>> CRATE_TEMPLATE = BLOCK_ENTITIES.register("crate_template",
            () -> BlockEntityType.Builder.of(TileEntityCrateTemplate::new, ModBlocks.CRATE_TEMPLATE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityCrateTungsten>> CRATE_TUNGSTEN = BLOCK_ENTITIES.register("crate_tungsten",
            () -> BlockEntityType.Builder.of(TileEntityCrateTungsten::new, ModBlocks.CRATE_TUNGSTEN.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntitySafe>> SAFE = BLOCK_ENTITIES.register("safe",
            () -> BlockEntityType.Builder.of(TileEntitySafe::new, ModBlocks.SAFE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadiobox>> RADIOBOX = BLOCK_ENTITIES.register("radiobox",
            () -> BlockEntityType.Builder.of(TileEntityRadiobox::new, ModBlocks.RADIOBOX.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineCrystallizer>> MACHINE_CRYSTALLIZER =
            BLOCK_ENTITIES.register("machine_crystallizer", () -> BlockEntityType.Builder.of(TileEntityMachineCrystallizer::new,
                    ModBlocks.MACHINE_CRYSTALLIZER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityPileFuel>> PILE_FUEL =
            BLOCK_ENTITIES.register("pile_fuel",
                    () -> BlockEntityType.Builder.of(TileEntityPileFuel::new,
                            ModBlocks.BLOCK_GRAPHITE_FUEL.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityPileSource>> PILE_SOURCE =
            BLOCK_ENTITIES.register("pile_source",
                    () -> BlockEntityType.Builder.of(TileEntityPileSource::new,
                            ModBlocks.BLOCK_GRAPHITE_SOURCE.get(),
                            ModBlocks.BLOCK_GRAPHITE_PLUTONIUM.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityPileBreedingFuel>> PILE_BREEDING_FUEL =
            BLOCK_ENTITIES.register("pile_breeding_fuel",
                    () -> BlockEntityType.Builder.of(TileEntityPileBreedingFuel::new,
                            ModBlocks.BLOCK_GRAPHITE_LITHIUM.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityPileNeutronDetector>> PILE_NEUTRON_DETECTOR =
            BLOCK_ENTITIES.register("pile_neutron_detector",
                    () -> BlockEntityType.Builder.of(TileEntityPileNeutronDetector::new,
                            ModBlocks.BLOCK_GRAPHITE_DETECTOR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineSatDock>> SAT_DOCK =
            BLOCK_ENTITIES.register("sat_dock",
            () -> BlockEntityType.Builder.of(TileEntityMachineSatDock::new,
                    ModBlocks.SAT_DOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityZirnoxDestroyed>> ZIRNOX_DESTROYED =
            BLOCK_ENTITIES.register("zirnox_destroyed",
            () -> BlockEntityType.Builder.of(TileEntityZirnoxDestroyed::new,
                    ModBlocks.ZIRNOX_DESTROYED.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCapacitor>> CAPACITOR =
            BLOCK_ENTITIES.register("capacitor",
            () -> BlockEntityType.Builder.of(TileEntityCapacitor::new,
                    ModBlocks.CAPACITOR_COPPER.get(),
                    ModBlocks.CAPACITOR_GOLD.get(),
                    ModBlocks.CAPACITOR_NIOBIUM.get(),
                    ModBlocks.CAPACITOR_TANTALIUM.get(),
                    ModBlocks.CAPACITOR_SCHRABIDATE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityTesla>> TESLA =
            BLOCK_ENTITIES.register("tesla",
            () -> BlockEntityType.Builder.of(TileEntityTesla::new,
                    ModBlocks.TESLA.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityLoot>> LOOT =
            BLOCK_ENTITIES.register("loot",
            () -> BlockEntityType.Builder.of(TileEntityLoot::new,
                    ModBlocks.DECO_LOOT.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityLaunchPad>> LAUNCH_PAD =
            BLOCK_ENTITIES.register("launch_pad",
            () -> BlockEntityType.Builder.of(TileEntityLaunchPad::new,
                    ModBlocks.LAUNCH_PAD.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineRadarNT>> RADAR_NT =
            BLOCK_ENTITIES.register("radar_nt",
                    () -> BlockEntityType.Builder.of(TileEntityMachineRadarNT::new,
                            ModBlocks.MACHINE_RADAR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineRadarScreen>> RADAR_SCREEN =
            BLOCK_ENTITIES.register("radar_screen",
                    () -> BlockEntityType.Builder.of(TileEntityMachineRadarScreen::new,
                            ModBlocks.RADAR_SCREEN.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineTeleporter>> TELEPORTER =
            BLOCK_ENTITIES.register("teleporter",
                    () -> BlockEntityType.Builder.of(TileEntityMachineTeleporter::new,
                            ModBlocks.MACHINE_TELEPORTER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityGeiger>> GEIGER =
            BLOCK_ENTITIES.register("geiger",
                    () -> BlockEntityType.Builder.of(TileEntityGeiger::new,
                            ModBlocks.GEIGER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySolarMirror>> SOLAR_MIRROR =
            BLOCK_ENTITIES.register("solar_mirror",
                    () -> BlockEntityType.Builder.of(TileEntitySolarMirror::new,
                            ModBlocks.SOLAR_MIRROR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySolarBoiler>> SOLAR_BOILER =
            BLOCK_ENTITIES.register("solar_boiler",
                    () -> BlockEntityType.Builder.of(TileEntitySolarBoiler::new,
                            ModBlocks.MACHINE_SOLAR_BOILER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRebar>> REBAR =
            BLOCK_ENTITIES.register("rebar",
                    () -> BlockEntityType.Builder.of(TileEntityRebar::new,
                            ModBlocks.REBAR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityPlushie>> PLUSHIE =
            BLOCK_ENTITIES.register("plushie",
                    () -> BlockEntityType.Builder.of(TileEntityPlushie::new,
                            ModBlocks.PLUSHIE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySnowglobe>> SNOWGLOBE =
            BLOCK_ENTITIES.register("snowglobe",
                    () -> BlockEntityType.Builder.of(TileEntitySnowglobe::new,
                            ModBlocks.SNOWGLOBE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMultiblock>> MULTIBLOCK =
            BLOCK_ENTITIES.register("multiblock",
                    () -> BlockEntityType.Builder.of(TileEntityMultiblock::new,
                            ModBlocks.STRUCT_LAUNCHER_CORE.get(),
                            ModBlocks.STRUCT_LAUNCHER_CORE_LARGE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCompactLauncher>> COMPACT_LAUNCHER =
            BLOCK_ENTITIES.register("compact_launcher",
                    () -> BlockEntityType.Builder.of(TileEntityCompactLauncher::new,
                            ModBlocks.COMPACT_LAUNCHER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityLaunchTable>> LAUNCH_TABLE =
            BLOCK_ENTITIES.register("launch_table",
                    () -> BlockEntityType.Builder.of(TileEntityLaunchTable::new,
                            ModBlocks.LAUNCH_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityLaunchPadRusted>> LAUNCH_PAD_RUSTED =
            BLOCK_ENTITIES.register("launch_pad_rusted",
                    () -> BlockEntityType.Builder.of(TileEntityLaunchPadRusted::new,
                            ModBlocks.LAUNCH_PAD_RUSTED.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityGeysir>> GEYSIR =
            BLOCK_ENTITIES.register("geysir",
                    () -> BlockEntityType.Builder.of(TileEntityGeysir::new,
                            ModBlocks.GEYSIR_WATER.get(),
                            ModBlocks.GEYSIR_CHLORINE.get(),
                            ModBlocks.GEYSIR_VAPOR.get(),
                            ModBlocks.GEYSIR_NETHER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineBattery>> MACHINE_BATTERY =
            BLOCK_ENTITIES.register("machine_battery",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new TileEntityMachineBattery(pos, state,
                                    ((MachineBattery) state.getBlock()).getMaxPower()),
                            ModBlocks.MACHINE_BATTERY.get(),
                            ModBlocks.MACHINE_BATTERY_POTATO.get(),
                            ModBlocks.MACHINE_LITHIUM_BATTERY.get(),
                            ModBlocks.MACHINE_SCHRABIDIUM_BATTERY.get(),
                            ModBlocks.MACHINE_DINEUTRONIUM_BATTERY.get()
                    ).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}