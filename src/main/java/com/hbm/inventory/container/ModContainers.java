package com.hbm.inventory.container;

import com.hbm.items.tool.ItemRebarPlacer;
import com.hbm.tileentity.bomb.*;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.storage.*;
import com.hbm.tileentity.turret.TileEntityTurretSentry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.util.RefStrings.MODID;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);


    public static final RegistryObject<MenuType<ContainerRadiobox>> RADIOBOX = MENUS.register("radiobox",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityRadiobox radiobox) {
                    return new ContainerRadiobox(windowId, inv, radiobox);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerAnvil>> ANVIL = MENUS.register("anvil",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                    int tier = data.readInt();
                return new ContainerAnvil(windowId, inv, tier);
            })
    );

    public static final RegistryObject<MenuType<ContainerMachinePress>> MACHINE_PRESS = MENUS.register("machine_press",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityMachinePress press) {
                    return new ContainerMachinePress(windowId, inv, press);
                }
                return null;
            })
    );

    public static final RegistryObject<MenuType<ContainerBarrel>> BARREL = MENUS.register("barrel",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityBarrel barrel) {
                    return new ContainerBarrel(windowId, inv, barrel);
                }
                return null;
            })
    );

    public static final RegistryObject<MenuType<ContainerBlastFurnace>> BLAST_FURNACE = MENUS.register("blast_furnace",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityBlastFurnace blastFurnace) {
                    return new ContainerBlastFurnace(windowId, inv, blastFurnace);
                }
                return null;
            })
    );

    public static final RegistryObject<MenuType<ContainerFileCabinet>> FILING_CABINET =
            MENUS.register("filing_cabinet", () ->
                    IForgeMenuType.create((windowId, inv, data) -> {
                        if (data == null) {
                            return null;
                        }
                        BlockPos pos = data.readBlockPos();
                        return new ContainerFileCabinet(windowId, inv, pos);
                    }));

    public static final RegistryObject<MenuType<ContainerRadioRec>> RADIOREC =
            MENUS.register("radio_rec",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        var pos = data.readBlockPos();
                        var level = inv.player.level();
                        var te = level.getBlockEntity(pos);
                        if (te instanceof TileEntityRadioRec radio) {
                            return new ContainerRadioRec(windowId, inv, radio);
                        }
                        return null;
                    }));

    public static final RegistryObject<MenuType<ContainerMachineFluidTank>> MACHINE_FLUID_TANK =
            MENUS.register("machine_fluid_tank",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        var level = inv.player.level();
                        var blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof TileEntityMachineFluidTank tank) {
                            return new ContainerMachineFluidTank(windowId, inv, tank);
                        }
                        return null;
                    }));

    public static final RegistryObject<MenuType<ContainerAshpit>> ASH_PIT =
            MENUS.register("ash_pit", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityAshpit ashpit) {
                    return new ContainerAshpit(windowId, inv, ashpit);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerFirebox>> FIREBOX =
            MENUS.register("firebox", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityFireboxBase firebox) {
                    return new ContainerFirebox(windowId, inv, firebox);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerOilburner>> OILBURNER =
            MENUS.register("oilburner", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityHeaterOilburner heater) {
                    return new ContainerOilburner(windowId, inv, heater);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerFurnaceSteel>> FURNACE_STEEL =
            MENUS.register("furnace_steel", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityFurnaceSteel furnace) {
                    return new ContainerFurnaceSteel(windowId, inv, inv.player, furnace);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerFurnaceIron>> FURNACE_IRON =
            MENUS.register("furnace_iron", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityFurnaceIron furnace) {
                    return new ContainerFurnaceIron(windowId, inv, inv.player, furnace);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerFurnaceCombination>> FURNACE_COMBINATION =
            MENUS.register("furnace_combination", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityFurnaceCombination furnace) {
                    return new ContainerFurnaceCombination(windowId, inv, inv.player, furnace);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerArmorTable>> ARMOR_TABLE =
            MENUS.register("armor_table", () -> IForgeMenuType.create((windowId, inv, data) -> new ContainerArmorTable(windowId, inv)));

    public static final RegistryObject<MenuType<ContainerNukeCustom>> NUKE_CUSTOM = MENUS.register("nuke_custom",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityNukeCustom nuke) {
                    return new ContainerNukeCustom(windowId, inv, nuke);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCargoContainer>> CARGO_CONTAINER = MENUS.register("cargo_container",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                BlockEntity tile = inv.player.level().getBlockEntity(pos);
                if (tile instanceof TileEntityCargoContainer cargoTile) {
                    return new ContainerCargoContainer(windowId, inv, cargoTile);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerMachineGasCent>> GAS_CENTRIFUGE = MENUS.register("machine_gas_cent",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TileEntityMachineGasCent centrifuge) {
                    return new ContainerMachineGasCent(windowId, inv, centrifuge);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerMachineDiesel>> MACHINE_DIESEL = MENUS.register("machine_diesel",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityMachineDiesel diesel) {
                    return new ContainerMachineDiesel(windowId, inv, diesel);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCentrifuge>> CENTRIFUGE = MENUS.register("centrifuge",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityMachineCentrifuge centrifuge) {
                    return new ContainerCentrifuge(windowId, inv, centrifuge);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerMachineRotaryFurnace>> ROTARY_FURNACE = MENUS.register("rotary_furnace",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityMachineRotaryFurnace furnace) {
                    return new ContainerMachineRotaryFurnace(windowId, inv, furnace);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCrucible>> CRUCIBLE = MENUS.register("crucible",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityCrucible crucible) {
                    return new ContainerCrucible(windowId, inv, crucible);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerMachineSolderingStation>> MACHINE_SOLDERING_STATION = MENUS.register("machine_soldering_station",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityMachineSolderingStation station) {
                    return new ContainerMachineSolderingStation(windowId, inv, station);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerMachineArcWelder>> MACHINE_ARC_WELDER =
            MENUS.register("machine_arc_welder", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityMachineArcWelder welder) {
                    return new ContainerMachineArcWelder(windowId, inv, welder);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerWeaponTable>> WEAPON_TABLE =
            MENUS.register("weapon_table", () -> IForgeMenuType.create((windowId, inv, data) ->
                    new ContainerWeaponTable(windowId, inv)));

    public static final RegistryObject<MenuType<ContainerNukeN2>> NUKE_N2 =
            MENUS.register("nuke_n2", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityNukeN2 tile) {
                    return new ContainerNukeN2(windowId, inv, tile);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerMachineShredder>> MACHINE_SHREDDER =
            MENUS.register("machine_shredder",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityMachineShredder shredder) {
                    return new ContainerMachineShredder(windowId, inv, shredder);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCrateIron>> CRATE_IRON = MENUS.register("crate_iron",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityCrateIron crate) {
                    return new ContainerCrateIron(windowId, inv, crate);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCrateSteel>> CRATE_STEEL = MENUS.register("crate_steel",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityCrateSteel crate) {
                    return new ContainerCrateSteel(windowId, inv, crate);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCrateDesh>> CRATE_DESH = MENUS.register("crate_desh",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityCrateDesh crate) {
                    return new ContainerCrateDesh(windowId, inv, crate);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCrateTemplate>> CRATE_TEMPLATE = MENUS.register("crate_template",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityCrateTemplate crate) {
                    return new ContainerCrateTemplate(windowId, inv, crate);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCrateTungsten>> CRATE_TUNGSTEN = MENUS.register("crate_tungsten",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityCrateTungsten crate) {
                    return new ContainerCrateTungsten(windowId, inv, crate);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerSafe>> SAFE = MENUS.register("safe",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntitySafe crate) {
                    return new ContainerSafe(windowId, inv, crate);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerTurretSentry>> TURRET_SENTRY = MENUS.register("turret_sentry",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var level = inv.player.level();
                var be = level.getBlockEntity(pos);
                if (be instanceof TileEntityTurretSentry turret) {
                    return new ContainerTurretSentry(windowId, inv, turret);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerCrystallizer>> CRYSTALLIZER =
            MENUS.register("crystallizer",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new ContainerCrystallizer(windowId, inv, (TileEntityMachineCrystallizer) inv.player.level().getBlockEntity(data.readBlockPos()))));

    public static final RegistryObject<MenuType<ContainerSatDock>> SAT_DOCK =
            MENUS.register("sat_dock",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                var te = inv.player.level().getBlockEntity(pos);
                if (te instanceof TileEntityMachineSatDock dock) {
                    return new ContainerSatDock(windowId, inv, dock);
                }
                return null;
            }));

    public static final RegistryObject<MenuType<ContainerLaunchPadLarge>> LAUNCH_PAD_LARGE =
            MENUS.register("launch_pad_large",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        var te = inv.player.level().getBlockEntity(pos);
                        if (te instanceof TileEntityLaunchPadBase launchPad) {
                            return new ContainerLaunchPadLarge(windowId, inv, launchPad);
                        }
                        return null;
                    }));

    public static final RegistryObject<MenuType<ContainerMachineRadarNT>> RADAR_NT =
            MENUS.register("radar_nt",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        var te = inv.player.level().getBlockEntity(pos);
                        if (te instanceof TileEntityMachineRadarNT radar) {
                            return new ContainerMachineRadarNT(windowId, inv, radar);
                        }
                        return null;
                    }));

    public static final RegistryObject<MenuType<ContainerToolBox>> TOOLBOX =
            MENUS.register("toolbox",
                    () -> IForgeMenuType.create((windowId, inv, data) ->
                            new ContainerToolBox(windowId, inv, null)));

    public static final RegistryObject<MenuType<ContainerRebarPlacer>> REBAR_PLACER =
            MENUS.register("rebar_placer",
                    () -> IForgeMenuType.create((windowId, inv, data) ->
                            new ContainerRebarPlacer(windowId, inv, new ItemRebarPlacer.InventoryRebarPlacer(
                                    inv.player, inv.player.getMainHandItem()
                            ))
                    ));

    public static final RegistryObject<MenuType<ContainerBook>> BOOK =
            MENUS.register("book",
                    () -> IForgeMenuType.create((windowId, inv, data) ->
                            new ContainerBook(windowId, inv)
                    ));

    public static final RegistryObject<MenuType<ContainerCompactLauncher>> COMPACT_LAUNCHER =
            MENUS.register("compact_launcher",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        var te = inv.player.level().getBlockEntity(pos);
                        if (te instanceof TileEntityCompactLauncher launcher) {
                            return new ContainerCompactLauncher(windowId, inv, launcher);
                        }
                        return null;
                    }));

    public static final RegistryObject<MenuType<ContainerLaunchTable>> LAUNCH_TABLE =
            MENUS.register("launch_table",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        var te = inv.player.level().getBlockEntity(pos);
                        if (te instanceof TileEntityLaunchTable launcher) {
                            return new ContainerLaunchTable(windowId, inv, launcher);
                        }
                        return null;
                    }));

    public static final RegistryObject<MenuType<ContainerLaunchPadRusted>> LAUNCH_PAD_RUSTED =
            MENUS.register("launch_pad_rusted",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        var te = inv.player.level().getBlockEntity(pos);
                        if (te instanceof TileEntityLaunchPadRusted launchpad) {
                            return new ContainerLaunchPadRusted(windowId, inv, launchpad);
                        }
                        return null;
                    }));

    public static final RegistryObject<MenuType<ContainerMachineBattery>> MACHINE_BATTERY =
            MENUS.register("machine_battery",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        BlockEntity be = inv.player.level().getBlockEntity(pos);
                        if (be instanceof TileEntityMachineBattery battery) {
                            return new ContainerMachineBattery(windowId, inv, battery);
                        }
                        return null;
                    }));
}