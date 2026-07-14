package com.hbm.creativetabs;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.*;
import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ControlTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.control_tab"))
                .icon(() -> {
                    var items = com.hbm.items.ItemRegistryHelper.getItemsForTab(HBMEnums.CreativeTabRegistry.CONTROL_TAB);
                    if (!items.isEmpty()) {
                        return new ItemStack(items.values().iterator().next().get());
                    }
                    return TabFallback.getFallbackIcon();
                })
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.CONTROL_TAB, output);

                    // Танк (1000 mB) - все жидкости кроме NONE и газов
                    for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                        if (fluid != Fluids.NONE.get() && !fluid.isGaseous()) {
                            output.accept(ItemFluidContainer.createForFluid(ModItems.FLUID_TANK.get(), fluid));
                        }
                    }

                    // Бочка (16000 mB) - все жидкости кроме NONE и газов
                    for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                        if (fluid != Fluids.NONE.get() && !fluid.isGaseous()) {
                            output.accept(ItemFluidContainer.createForFluid(ModItems.FLUID_BARREL.get(), fluid));
                        }
                    }

                    // Канистра (8000 mB) - все жидкости кроме NONE и газов
                    for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                        if (fluid != Fluids.NONE.get() && !fluid.isGaseous()) {
                            output.accept(ItemFluidContainer.createForFluid(ModItems.FLUID_CANISTER.get(), fluid));
                        }
                    }

                    // Газовый баллон (8000 mB) - ТОЛЬКО газы
                    for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                        if (fluid != Fluids.NONE.get() && fluid.isGaseous()) {
                            output.accept(ItemFluidContainer.createForFluid(ModItems.GAS_TANK.get(), fluid));
                        }
                    }

                    // Свинцовый танк (1000 mB) - ТОЛЬКО для жидкостей требующих свинец
                    for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                        if (fluid != Fluids.NONE.get() && fluid.needsLeadContainer()) {
                            output.accept(ItemFluidContainer.createForFluid(ModItems.FLUID_TANK_LEAD.get(), fluid));
                        }
                    }

                    // Ведро (1000 mB) - ТОЛЬКО для жидкостей (НЕ газы, НЕ антиматерия, НЕ коррозивные)
                    for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                        if (fluid != Fluids.NONE.get() &&
                                !fluid.isGaseous() &&
                                !fluid.isAntimatter() &&
                                !fluid.isCorrosive() &&
                                !fluid.needsLeadContainer()) {
                            output.accept(ItemFluidContainer.createForFluid(ModItems.FLUID_BUCKET.get(), fluid));
                        }
                    }

                    // Дисперсная канистра (2000 mB) - ТОЛЬКО для dispersable жидкостей
                    for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                        if (fluid != Fluids.NONE.get() && fluid.isDispersable() && !fluid.isGaseous()) {
                            output.accept(ItemFluidContainer.createForFluid(ModItems.DISPERSER_CANISTER.get(), fluid));
                            output.accept(ItemFluidContainer.createForFluid(ModItems.GLYPHID_GLAND.get(), fluid));
                        }
                    }

                })
                .build();
    }
}