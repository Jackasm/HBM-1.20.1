package com.hbm.creativetabs;

import com.hbm.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MissileTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    public static final RegistryObject<CreativeModeTab> HBM_MISSILE_TAB = CREATIVE_MODE_TABS.register("missile_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.missile_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для вкладки missile
                        // output.accept(new ItemStack(ModItems.missile_nuclear.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.missile_thermo.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.missile_doomsday.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());
}
