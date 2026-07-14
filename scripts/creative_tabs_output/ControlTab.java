package com.hbm.creativetabs;

import com.hbm.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ControlTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    public static final RegistryObject<CreativeModeTab> HBM_CONTROL_TAB = CREATIVE_MODE_TABS.register("control_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.control_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для вкладки control
                        // output.accept(new ItemStack(ModItems.key_red.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.key_red_cracked.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());
}
