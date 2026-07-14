// HbmCreativeTabs.java
package com.hbm.creativetabs;

import com.hbm.util.RefStrings;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HbmCreativeTabs {

    // Используем Registries.CREATIVE_MODE_TAB вместо ForgeRegistries.CREATIVE_MODE_TABS
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RefStrings.MODID);

    // Регистрация всех вкладок
    public static final RegistryObject<CreativeModeTab> PARTS_TAB =
            CREATIVE_TABS.register("parts_tab", PartsTab::create);

    public static final RegistryObject<CreativeModeTab> BLOCK_TAB =
            CREATIVE_TABS.register("block_tab", BlockTab::create);

    public static final RegistryObject<CreativeModeTab> CONSUMABLE_TAB =
            CREATIVE_TABS.register("consumable_tab", ConsumableTab::create);

    public static final RegistryObject<CreativeModeTab> CONTROL_TAB =
            CREATIVE_TABS.register("control_tab", ControlTab::create);

    public static final RegistryObject<CreativeModeTab> MACHINE_TAB =
            CREATIVE_TABS.register("machine_tab", MachineTab::create);

    public static final RegistryObject<CreativeModeTab> MISSILE_TAB =
            CREATIVE_TABS.register("missile_tab", MissileTab::create);

    public static final RegistryObject<CreativeModeTab> NUKE_TAB =
            CREATIVE_TABS.register("nuke_tab", NukeTab::create);

    public static final RegistryObject<CreativeModeTab> TEMPLATE_TAB =
            CREATIVE_TABS.register("template_tab", TemplateTab::create);

    public static final RegistryObject<CreativeModeTab> WEAPON_TAB =
            CREATIVE_TABS.register("weapon_tab", WeaponTab::create);

    // Регистрация в Forge
    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}