package com.hbm.creativetabs;

import com.hbm.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class WeaponTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    public static final RegistryObject<CreativeModeTab> HBM_WEAPON_TAB = CREATIVE_MODE_TABS.register("weapon_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.weapon_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для вкладки weapon
                        // output.accept(new ItemStack(ModItems.gun_revolver.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.gun_kit_1.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.gun_kit_2.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.ammo_357.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.ammo_44.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.ammo_50.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.ammo_9.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());
}
