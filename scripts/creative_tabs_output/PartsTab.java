package com.hbm.creativetabs;

import com.hbm.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PartsTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    public static final RegistryObject<CreativeModeTab> HBM_PARTS_TAB = CREATIVE_MODE_TABS.register("parts_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.parts_tab"))
                    .icon(() -> new ItemStack(ModItems.PLATE_STEEL.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для вкладки parts
                        // output.accept(new ItemStack(ModItems.mech_key.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.pin.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.key_kit.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.key_fake.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());
}
