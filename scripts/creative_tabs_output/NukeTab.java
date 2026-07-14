package com.hbm.creativetabs;

import com.hbm.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class NukeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    public static final RegistryObject<CreativeModeTab> HBM_NUKE_TAB = CREATIVE_MODE_TABS.register("nuke_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.nuke_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для вкладки nuke
                        // output.accept(new ItemStack(ModItems.nuke_mk4.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.nuke_mk5.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.nuke_schrabidium.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.nuke_fleija.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());
}
