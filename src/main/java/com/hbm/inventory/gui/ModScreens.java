package com.hbm.inventory.gui;

import com.hbm.inventory.container.ModContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.hbm.util.RefStrings.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModContainers.ANVIL.get(), GUIAnvil::new);
            MenuScreens.register(ModContainers.MACHINE_PRESS.get(), GUIMachinePress::new);
            MenuScreens.register(ModContainers.BLAST_FURNACE.get(), GUIBlastFurnace::new);
            MenuScreens.register(ModContainers.BARREL.get(), GUIBarrel::new);
            MenuScreens.register(ModContainers.FILING_CABINET.get(), GUIFileCabinet::new);
            MenuScreens.register(ModContainers.RADIOREC.get(), GUIRadioRec::new);
            MenuScreens.register(ModContainers.MACHINE_FLUID_TANK.get(), GUIMachineFluidTank::new);
            MenuScreens.register(ModContainers.ASH_PIT.get(), GUIAshpit::new);
            MenuScreens.register(ModContainers.FIREBOX.get(), GUIFirebox::new);
            MenuScreens.register(ModContainers.OILBURNER.get(), GUIOilburner::new);
            MenuScreens.register(ModContainers.FURNACE_STEEL.get(), GUIFurnaceSteel::new);
            MenuScreens.register(ModContainers.FURNACE_IRON.get(), GUIFurnaceIron::new);
            MenuScreens.register(ModContainers.FURNACE_COMBINATION.get(), GUIFurnaceCombination::new);
            MenuScreens.register(ModContainers.NUKE_CUSTOM.get(), GUINukeCustom::new);
            MenuScreens.register(ModContainers.ARMOR_TABLE.get(), GUIArmorTable::new);
            MenuScreens.register(ModContainers.CARGO_CONTAINER.get(), CargoContainerGUI::new);
            MenuScreens.register(ModContainers.GAS_CENTRIFUGE.get(), GUIMachineGasCent::new);
            MenuScreens.register(ModContainers.MACHINE_DIESEL.get(), GUIMachineDiesel::new);
            MenuScreens.register(ModContainers.CENTRIFUGE.get(), GUIMachineCentrifuge::new);
            MenuScreens.register(ModContainers.ROTARY_FURNACE.get(), GUIMachineRotaryFurnace::new);
            MenuScreens.register(ModContainers.CRUCIBLE.get(), GUICrucible::new);
            MenuScreens.register(ModContainers.MACHINE_SOLDERING_STATION.get(), GUIMachineSolderingStation::new);
            MenuScreens.register(ModContainers.MACHINE_ARC_WELDER.get(), GUIMachineArcWelder::new);
            MenuScreens.register(ModContainers.WEAPON_TABLE.get(), GUIWeaponTable::new);
            MenuScreens.register(ModContainers.NUKE_N2.get(), GUINukeN2::new);
            MenuScreens.register(ModContainers.MACHINE_SHREDDER.get(), GUIMachineShredder::new);
            MenuScreens.register(ModContainers.CRATE_IRON.get(), GUICrateIron::new);
            MenuScreens.register(ModContainers.CRATE_STEEL.get(), GUICrateSteel::new);
            MenuScreens.register(ModContainers.CRATE_DESH.get(), GUICrateDesh::new);
            MenuScreens.register(ModContainers.CRATE_TEMPLATE.get(), GUICrateTemplate::new);
            MenuScreens.register(ModContainers.CRATE_TUNGSTEN.get(), GUICrateTungsten::new);
            MenuScreens.register(ModContainers.SAFE.get(), GUISafe::new);
            MenuScreens.register(ModContainers.TURRET_SENTRY.get(), GUITurretSentry::new);
            MenuScreens.register(ModContainers.CRYSTALLIZER.get(), GUICrystallizer::new);
            MenuScreens.register(ModContainers.SAT_DOCK.get(), GUISatDock::new);
            MenuScreens.register(ModContainers.LAUNCH_PAD_LARGE.get(), GUILaunchPadLarge::new);
            MenuScreens.register(ModContainers.TOOLBOX.get(), GUIToolBox::new);
            MenuScreens.register(ModContainers.REBAR_PLACER.get(), GUIRebarPlacer::new);
            MenuScreens.register(ModContainers.BOOK.get(), GUIBook::new);
            MenuScreens.register(ModContainers.COMPACT_LAUNCHER.get(), GUIMachineCompactLauncher::new);
            MenuScreens.register(ModContainers.LAUNCH_TABLE.get(), GUIMachineLaunchTable::new);
            MenuScreens.register(ModContainers.LAUNCH_PAD_RUSTED.get(), GUILaunchPadRusted::new);
            MenuScreens.register(ModContainers.MACHINE_BATTERY.get(), GUIMachineBattery::new);

        });
    }
}