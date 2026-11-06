package com.hbm.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.*;
import com.hbm.blocks.machine.BlockUF6Tank;
import com.hbm.blocks.machine.BlockVendingMachine;
import com.hbm.blocks.machine.MachineBattery;
import com.hbm.blocks.machine.MachineStirling;
import com.hbm.blocks.network.CableDiode;
import com.hbm.entity.CTBlockEntity;
import com.hbm.tileentity.block.*;
import com.hbm.tileentity.bomb.*;
import com.hbm.tileentity.conduit.ConduitTileEntity;
import com.hbm.tileentity.deco.*;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.oil.TileEntityMachineFractionTower;
import com.hbm.tileentity.machine.oil.TileEntitySpacer;
import com.hbm.tileentity.machine.pile.TileEntityPileBreedingFuel;
import com.hbm.tileentity.machine.pile.TileEntityPileFuel;
import com.hbm.tileentity.machine.pile.TileEntityPileNeutronDetector;
import com.hbm.tileentity.machine.pile.TileEntityPileSource;
import com.hbm.tileentity.network.*;
import com.hbm.tileentity.storage.*;
import com.hbm.tileentity.turret.TileEntityTurretHoward;
import com.hbm.tileentity.turret.TileEntityTurretHowardDamaged;
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
                            ModBlocks.STEEL_SCAFFOLD_STEEL.get(),
                            ModBlocks.STEEL_SCAFFOLD_RED.get(),
                            ModBlocks.STEEL_SCAFFOLD_WHITE.get(),
                            ModBlocks.STEEL_SCAFFOLD_YELLOW.get(),
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
                            ModBlocks.VITRIFIED_BARREL.get(),
                            ModBlocks.PINK_BARREL.get(),
                            ModBlocks.SPOTLIGHT_INCANDESCENT.get(),
                            ModBlocks.SPOTLIGHT_INCANDESCENT_OFF.get(),
                            ModBlocks.SPOTLIGHT_FLUORO.get(),
                            ModBlocks.SPOTLIGHT_FLUORO_OFF.get(),
                            ModBlocks.SPOTLIGHT_HALOGEN.get(),
                            ModBlocks.SPOTLIGHT_HALOGEN_OFF.get(),
                            ModBlocks.DECO_CRT.get(),
                            ModBlocks.BARBED_WIRE.get(),
                            ModBlocks.BARBED_WIRE_FIRE.get(),
                            ModBlocks.BARBED_WIRE_POISON.get(),
                            ModBlocks.BARBED_WIRE_ACID.get(),
                            ModBlocks.BARBED_WIRE_WITHER.get(),
                            ModBlocks.BARBED_WIRE_ULTRA_DEATH.get()
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
                    ModBlocks.SAT_FOEQ.get(),
                    ModBlocks.DECO_COMPUTER.get()
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
                    ModBlocks.RED_CABLE.get(),
                    ModBlocks.RED_WIRE_COATED.get(),
                    ModBlocks.CABLE_DIODE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCableSwitch>> CABLE_SWITCH =
            BLOCK_ENTITIES.register("cable_switch",
            () -> BlockEntityType.Builder.of(TileEntityCableSwitch::new,
                    ModBlocks.CABLE_SWITCH.get(),
                    // Детектор — тот же рубильник по эталону 1.7.10. Без этой строки его
                    // сущность считается невалидной для блока: тикер не регистрируется,
                    // ощущение «invalid for ticking» в логе и узла нет (#180).
                    ModBlocks.CABLE_DETECTOR.get()
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

    /**
     * Общий носитель данных модели для всех CT-блоков: deco, platemetal, катушки, стекло.
     * Зарегистрирован под историческим именем {@code deco_ct} — так старые миры не теряют
     * блок-энтити. Список допустимых блоков пуст намеренно: BE создаёт сам блок.
     */
    public static final RegistryObject<BlockEntityType<CTBlockEntity>> CT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("deco_ct",
                    () -> BlockEntityType.Builder.of(CTBlockEntity::new).build(null));

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

    public static final RegistryObject<BlockEntityType<TileEntityMachineAmmoPress>> MACHINE_AMMO_PRESS =
            BLOCK_ENTITIES.register("machine_ammo_press", () -> BlockEntityType.Builder.of(
                    TileEntityMachineAmmoPress::new,
                    ModBlocks.MACHINE_AMMO_PRESS.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySkeletonHolder>> SKELETON_HOLDER =
            BLOCK_ENTITIES.register("skeleton_holder",
                    () -> BlockEntityType.Builder.of(
                            TileEntitySkeletonHolder::new,
                            ModBlocks.SKELETON_HOLDER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityDungeonSpawner>> DUNGEON_SPAWNER =
            BLOCK_ENTITIES.register("dungeon_spawner",
                    () -> BlockEntityType.Builder.of(
                            TileEntityDungeonSpawner::new,
                            ModBlocks.DUNGEON_SPAWNER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityWandLoot>> WAND_LOOT =
            BLOCK_ENTITIES.register("wand_loot",
                    () -> BlockEntityType.Builder.of(TileEntityWandLoot::new,
                            ModBlocks.WAND_LOOT.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityPipe>> PIPE_TE =
            BLOCK_ENTITIES.register("pipe_te",
                    () -> BlockEntityType.Builder.of(TileEntityPipe::new,
                            ModBlocks.DECO_PIPE.get(),
                            ModBlocks.DECO_PIPE_RUSTED.get(),
                            ModBlocks.DECO_PIPE_GREEN.get(),
                            ModBlocks.DECO_PIPE_GREEN_RUSTED.get(),
                            ModBlocks.DECO_PIPE_RED.get(),
                            ModBlocks.DECO_PIPE_MARKED.get(),
                            ModBlocks.DECO_PIPE_RIM.get(),
                            ModBlocks.DECO_PIPE_RIM_RUSTED.get(),
                            ModBlocks.DECO_PIPE_RIM_GREEN.get(),
                            ModBlocks.DECO_PIPE_RIM_GREEN_RUSTED.get(),
                            ModBlocks.DECO_PIPE_RIM_RED.get(),
                            ModBlocks.DECO_PIPE_RIM_MARKED.get(),
                            ModBlocks.DECO_PIPE_QUAD.get(),
                            ModBlocks.DECO_PIPE_QUAD_RUSTED.get(),
                            ModBlocks.DECO_PIPE_QUAD_GREEN.get(),
                            ModBlocks.DECO_PIPE_QUAD_GREEN_RUSTED.get(),
                            ModBlocks.DECO_PIPE_QUAD_RED.get(),
                            ModBlocks.DECO_PIPE_QUAD_MARKED.get(),
                            ModBlocks.DECO_PIPE_FRAMED.get(),
                            ModBlocks.DECO_PIPE_FRAMED_RUSTED.get(),
                            ModBlocks.DECO_PIPE_FRAMED_GREEN.get(),
                            ModBlocks.DECO_PIPE_FRAMED_GREEN_RUSTED.get(),
                            ModBlocks.DECO_PIPE_FRAMED_RED.get(),
                            ModBlocks.DECO_PIPE_FRAMED_MARKED.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<BlockMetalFence.TileEntityMetalFence>> METAL_FENCE =
            BLOCK_ENTITIES.register("metal_fence",
                    () -> BlockEntityType.Builder.of(BlockMetalFence.TileEntityMetalFence::new,
                            ModBlocks.FENCE_METAL.get(),
                            ModBlocks.FENCE_METAL_POST.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineFunnel>> MACHINE_FUNNEL =
            BLOCK_ENTITIES.register("machine_funnel",
                    () -> BlockEntityType.Builder.of(TileEntityMachineFunnel::new,
                            ModBlocks.MACHINE_FUNNEL.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<BlockSpikes.TileEntitySpikes>> SPIKES =
            BLOCK_ENTITIES.register("spikes",
                    () -> BlockEntityType.Builder.of(BlockSpikes.TileEntitySpikes::new,
                            ModBlocks.SPIKES.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineElectricFurnace>> MACHINE_ELECTRIC_FURNACE =
            BLOCK_ENTITIES.register("machine_electric_furnace",
                    () -> BlockEntityType.Builder.of(TileEntityMachineElectricFurnace::new,
                            ModBlocks.MACHINE_ELECTRIC_FURNACE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityTurretHoward>> TURRET_HOWARD =
            BLOCK_ENTITIES.register("turret_howard", () -> BlockEntityType.Builder.of(
                    TileEntityTurretHoward::new,
                    ModBlocks.TURRET_HOWARD.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityTurretHowardDamaged>> TURRET_HOWARD_DAMAGED =
            BLOCK_ENTITIES.register("turret_howard_damaged", () -> BlockEntityType.Builder.of(
                    TileEntityTurretHowardDamaged::new,
                    ModBlocks.TURRET_HOWARD_DAMAGED.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<BlockChain.TileEntityChain>> CHAIN =
            BLOCK_ENTITIES.register("chain", () -> BlockEntityType.Builder.of(
                    BlockChain.TileEntityChain::new,
                    ModBlocks.STEEL_CHAIN.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySpacer>> SPACER =
            BLOCK_ENTITIES.register("spacer",
                    () -> BlockEntityType.Builder.of(TileEntitySpacer::new,
                            ModBlocks.FRACTION_SPACER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineFractionTower>> MACHINE_FRACTION_TOWER =
            BLOCK_ENTITIES.register("machine_fraction_tower",
                    () -> BlockEntityType.Builder.of(TileEntityMachineFractionTower::new,
                            ModBlocks.MACHINE_FRACTION_TOWER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityPipeGauge>> FLUID_DUCT_GAUGE =
            BLOCK_ENTITIES.register("fluid_duct_gauge",
                    () -> BlockEntityType.Builder.of(TileEntityPipeGauge::new,
                            ModBlocks.FLUID_DUCT_GAUGE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCharger>> CHARGER =
            BLOCK_ENTITIES.register("charger",
                    () -> BlockEntityType.Builder.of(TileEntityCharger::new,
                            ModBlocks.CHARGER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMicrowave>> MACHINE_MICROWAVE =
            BLOCK_ENTITIES.register("machine_microwave",
                    () -> BlockEntityType.Builder.of(TileEntityMicrowave::new,
                            ModBlocks.MACHINE_MICROWAVE.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlockDecoToaster.TileEntityToaster>> DECO_TOASTER =
            BLOCK_ENTITIES.register("deco_toaster",
                    () -> BlockEntityType.Builder.of(BlockDecoToaster.TileEntityToaster::new,
                            ModBlocks.DECO_TOASTER_IRON.get(),
                            ModBlocks.DECO_TOASTER_STEEL.get(),
                            ModBlocks.DECO_TOASTER_WOOD.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineRTG>> MACHINE_RTG =
            BLOCK_ENTITIES.register("machine_rtg",
                    () -> BlockEntityType.Builder.of(TileEntityMachineRTG::new,
                            ModBlocks.MACHINE_RTG_GREY.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineMiniRTG>> MACHINE_MINI_RTG =
            BLOCK_ENTITIES.register("machine_mini_rtg",
                    () -> BlockEntityType.Builder.of(TileEntityMachineMiniRTG::new,
                            ModBlocks.MACHINE_POWERRTG.get(),
                            ModBlocks.MACHINE_MINIRTG.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCableGauge>> CABLE_GAUGE =
            BLOCK_ENTITIES.register("cable_gauge",
                    () -> BlockEntityType.Builder.of(TileEntityCableGauge::new,
                            ModBlocks.RED_CABLE_GAUGE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityBarrier>> BARRIER =
            BLOCK_ENTITIES.register("barrier",
                    () -> BlockEntityType.Builder.of(TileEntityBarrier::new,
                            ModBlocks.WOOD_BARRIER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityWoodStructure>> WOOD_STRUCTURE =
            BLOCK_ENTITIES.register("wood_structure",
                    () -> BlockEntityType.Builder.of(TileEntityWoodStructure::new,
                            ModBlocks.WOOD_STRUCTURE_ROOF.get(),
                            ModBlocks.WOOD_STRUCTURE_SCAFFOLD.get(),
                            ModBlocks.WOOD_STRUCTURE_CEILING.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlockVendingMachine.TileEntityVendingMachine>> VENDING_MACHINE =
            BLOCK_ENTITIES.register("vending_machine",
                    () -> BlockEntityType.Builder.of(BlockVendingMachine.TileEntityVendingMachine::new,
                            ModBlocks.VENDING_MACHINE_SODA.get(),
                            ModBlocks.VENDING_MACHINE_OBAMNA.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlockSandbags.TileEntitySandbags>> SANDBAGS =
            BLOCK_ENTITIES.register("sandbags",
                    () -> BlockEntityType.Builder.of(BlockSandbags.TileEntitySandbags::new,
                            ModBlocks.SANDBAGS.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadioTelex>> RADIO_TELEX =
            BLOCK_ENTITIES.register("radio_telex",
                    () -> BlockEntityType.Builder.of(TileEntityRadioTelex::new,
                            ModBlocks.RADIO_TELEX.get()).build(null));

    public static final RegistryObject<BlockEntityType<HEVBattery.TileEntityHEVBattery>> HEV_BATTERY =
            BLOCK_ENTITIES.register("hev_battery",
                    () -> BlockEntityType.Builder.of(HEVBattery.TileEntityHEVBattery::new,
                            ModBlocks.HEV_BATTERY.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityWandLogic>> WAND_LOGIC =
            BLOCK_ENTITIES.register("wand_logic",
                    () -> BlockEntityType.Builder.of(TileEntityWandLogic::new,
                            ModBlocks.WAND_LOGIC.get()).build(null));

    public static final RegistryObject<BlockEntityType<LogicBlock.TileEntityLogicBlock>> LOGIC_BLOCK =
            BLOCK_ENTITIES.register("logic_block",
                    () -> BlockEntityType.Builder.of(LogicBlock.TileEntityLogicBlock::new,
                            ModBlocks.LOGIC_BLOCK.get(),
                            ModBlocks.LOGIC_BLOCK_INVIS.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlockPedestal.TileEntityPedestal>> PEDESTAL =
            BLOCK_ENTITIES.register("pedestal",
                    () -> BlockEntityType.Builder.of(BlockPedestal.TileEntityPedestal::new,
                            ModBlocks.PEDESTAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCharge>> CHARGE =
            BLOCK_ENTITIES.register("charge",
                    () -> BlockEntityType.Builder.of(TileEntityCharge::new,
                            ModBlocks.CHARGE_DYNAMITE.get(),
                            ModBlocks.CHARGE_MINER.get(),
                            ModBlocks.CHARGE_C4.get(),
                            ModBlocks.CHARGE_SEMTEX.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityDoorGeneric>> DOOR_GENERIC =
            BLOCK_ENTITIES.register("door_generic",
                    () -> BlockEntityType.Builder.of(TileEntityDoorGeneric::new,
                            ModBlocks.SLIDING_BLAST_DOOR.get(),
                            ModBlocks.FIRE_DOOR.get(),
                            ModBlocks.SILO_HATCH.get(),
                            ModBlocks.SILO_HATCH_LARGE.get(),
                            ModBlocks.SECURE_ACCESS_DOOR.get(),
                            ModBlocks.LARGE_VEHICLE_DOOR.get(),
                            ModBlocks.QE_CONTAINMENT.get(),
                            ModBlocks.QE_SLIDING_DOOR.get(),
                            ModBlocks.ROUND_AIRLOCK_DOOR.get(),
                            ModBlocks.SLIDING_SEAL_DOOR.get(),
                            ModBlocks.WATER_DOOR.get(),
                            ModBlocks.CARGO_DOOR.get(),
                            ModBlocks.VAULT_DOOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlockGlyphid.TileEntityGlyphid>> GLYPHID =
            BLOCK_ENTITIES.register("glyphid",
                    () -> BlockEntityType.Builder.of(
                            BlockGlyphid.TileEntityGlyphid::new,
                            ModBlocks.GLYPHID_BASE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityGlyphidSpawner>> GLYPHID_SPAWNER =
            BLOCK_ENTITIES.register("glyphid_spawner",
                    () -> BlockEntityType.Builder.of(
                            TileEntityGlyphidSpawner::new,
                            ModBlocks.GLYPHID_SPAWNER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntitySoyuzCapsule>> SOYUZ_CAPSULE =
            BLOCK_ENTITIES.register("soyuz_capsule",
                    () -> BlockEntityType.Builder.of(
                            TileEntitySoyuzCapsule::new,
                            ModBlocks.SOYUZ_CAPSULE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityLantern>> LANTERN =
            BLOCK_ENTITIES.register("lantern",
                    () -> BlockEntityType.Builder.of(
                            TileEntityLantern::new,
                            ModBlocks.LANTERN.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityLanternBehemoth>> LANTERN_BEHEMOTH =
            BLOCK_ENTITIES.register("lantern_behemoth",
                    () -> BlockEntityType.Builder.of(
                            TileEntityLanternBehemoth::new,
                            ModBlocks.LANTERN_BEHEMOTH.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityMachineKeyForge>> MACHINE_KEY_FORGE =
            BLOCK_ENTITIES.register("machine_key_forge",
                    () -> BlockEntityType.Builder.of(
                            TileEntityMachineKeyForge::new,
                            ModBlocks.MACHINE_KEYFORGE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<BlockSellafield.TileEntitySellafield>> SELLAFIELD =
            BLOCK_ENTITIES.register("sellafield",
                    () -> BlockEntityType.Builder.of(
                            BlockSellafield.TileEntitySellafield::new,
                            ModBlocks.SELLAFIELD.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityNukeMan>> NUKE_MAN =
            BLOCK_ENTITIES.register("nuke_man",
                    () -> BlockEntityType.Builder.of(
                            TileEntityNukeMan::new,
                            ModBlocks.NUKE_MAN.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<BlockUF6Tank.TileEntityUF6Tank>> UF6_TANK =
            BLOCK_ENTITIES.register("uf6_tank",
                    () -> BlockEntityType.Builder.of(
                            BlockUF6Tank.TileEntityUF6Tank::new,
                            ModBlocks.MACHINE_UF6_TANK.get(),
                            ModBlocks.MACHINE_PUF6_TANK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityFan>> FAN =
            BLOCK_ENTITIES.register("fan",
                    () -> BlockEntityType.Builder.of(
                            TileEntityFan::new,
                            ModBlocks.FAN.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityCondenser>> CONDENSER =
            BLOCK_ENTITIES.register("condenser",
                    () -> BlockEntityType.Builder.of(
                            TileEntityCondenser::new,
                            ModBlocks.MACHINE_CONDENSER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<CableDiode.TileEntityDiode>> CABLE_DIODE =
            BLOCK_ENTITIES.register("cable_diode",
                    () -> BlockEntityType.Builder.of(
                            CableDiode.TileEntityDiode::new,
                            ModBlocks.CABLE_DIODE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadioTorchSender>> RADIO_TORCH_SENDER =
            BLOCK_ENTITIES.register("radio_torch_sender",
                    () -> BlockEntityType.Builder.of(
                            TileEntityRadioTorchSender::new,
                            ModBlocks.RADIO_TORCH_SENDER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadioTorchReceiver>> RADIO_TORCH_RECEIVER =
            BLOCK_ENTITIES.register("radio_torch_receiver",
                    () -> BlockEntityType.Builder.of(
                            TileEntityRadioTorchReceiver::new,
                            ModBlocks.RADIO_TORCH_RECEIVER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadioTorchBase>> RADIO_TORCH_BASE =
            BLOCK_ENTITIES.register("radio_torch_base",
                    () -> BlockEntityType.Builder.of(
                            TileEntityRadioTorchBase::new
                            // без блока — базовый TE не используется напрямую
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadioTorchLogic>> RADIO_TORCH_LOGIC =
            BLOCK_ENTITIES.register("radio_torch_logic",
                    () -> BlockEntityType.Builder.of(
                            TileEntityRadioTorchLogic::new,
                            ModBlocks.RADIO_TORCH_LOGIC.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadioTorchCounter>> RADIO_TORCH_COUNTER =
            BLOCK_ENTITIES.register("radio_torch_counter",
                    () -> BlockEntityType.Builder.of(
                            TileEntityRadioTorchCounter::new,
                            ModBlocks.RADIO_TORCH_COUNTER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadioTorchReader>> RADIO_TORCH_READER =
            BLOCK_ENTITIES.register("radio_torch_reader",
                    () -> BlockEntityType.Builder.of(
                            TileEntityRadioTorchReader::new,
                            ModBlocks.RADIO_TORCH_READER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRadioTorchController>> RADIO_TORCH_CONTROLLER =
            BLOCK_ENTITIES.register("radio_torch_controller",
                    () -> BlockEntityType.Builder.of(
                            TileEntityRadioTorchController::new,
                            ModBlocks.RADIO_TORCH_CONTROLLER.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}