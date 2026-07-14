package com.hbm.creativetabs;

import com.hbm.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MachineTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    public static final RegistryObject<CreativeModeTab> HBM_MACHINE_TAB = CREATIVE_MODE_TABS.register("machine_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.machine_tab"))
                    .icon(() -> new ItemStack(ModItems.MACHINE_PRESS_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для вкладки machine
                        // output.accept(new ItemStack(ModItems.assembly_template.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.chemistry_template.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.chemistry_icon.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());
}
