package com.hbm.creativetabs;

import com.hbm.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TemplateTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    public static final RegistryObject<CreativeModeTab> HBM_TEMPLATE_TAB = CREATIVE_MODE_TABS.register("template_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.template_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для вкладки template
                        // output.accept(new ItemStack(ModItems.blueprints.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.blueprint_folder.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.template_folder.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.assembly_template.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.chemistry_template.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());
}
