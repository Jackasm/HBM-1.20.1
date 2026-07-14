package com.hbm.creativetabs;


import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.CrucibleRecipes;
import com.hbm.items.machine.ItemCrucibleTemplate;
import com.hbm.items.fluid.ItemFluidID;
import com.hbm.items.machine.ItemMold;
import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class TemplateTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.template_tab"))
                .icon(() -> {
                    var items = com.hbm.items.ItemRegistryHelper.getItemsForTab(HBMEnums.CreativeTabRegistry.TEMPLATE_TAB);
                    if (!items.isEmpty()) {
                        return new ItemStack(items.values().iterator().next().get());
                    }
                    return TabFallback.getFallbackIcon();
                })
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.TEMPLATE_TAB, output);

                    for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                        ItemStack stack = ItemFluidID.createForFluid(fluid);
                        output.accept(stack);
                    }

                    for (int i = 0; i < CrucibleRecipes.recipes.size(); i++) {
                        output.accept(ItemCrucibleTemplate.forRecipe(i));
                    }
                    for (ItemMold.MoldType type : ItemMold.MoldType.values()) {
                        output.accept(ItemMold.forMold(type));
                    }
                })
                .build();
    }
}