package com.hbm.integration.jei.solderingstation;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.JeiUtil;
import com.hbm.inventory.recipes.SolderingRecipes;
import com.hbm.inventory.recipes.SolderingRecipes.SolderingRecipe;
import com.hbm.inventory.recipes.common.AStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SolderingStationRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for (SolderingRecipe recipe : SolderingRecipes.recipes) {
            // Toppings (слоты 0-2)
            List<ItemStack> toppings = new ArrayList<>();
            for (AStack aStack : recipe.toppings) {
                List<ItemStack> variants = JeiUtil.getStacksFromAStack(aStack);
                if (variants.isEmpty()) {
                    toppings.clear();
                    break;
                }
                toppings.add(variants.get(0));
            }


            // PCB (слоты 3-4)
            List<ItemStack> pcb = new ArrayList<>();
            for (AStack aStack : recipe.pcb) {
                List<ItemStack> variants = JeiUtil.getStacksFromAStack(aStack);
                if (variants.isEmpty()) {
                    pcb.clear();
                    break;
                }
                pcb.add(variants.get(0));
            }


            // Solder (слот 5)
            List<ItemStack> solder = new ArrayList<>();
            for (AStack aStack : recipe.solder) {
                List<ItemStack> variants = JeiUtil.getStacksFromAStack(aStack);
                if (variants.isEmpty()) {
                    solder.clear();
                    break;
                }
                solder.add(variants.get(0));
            }


            // Входная жидкость
            FluidColorIngredient inputFluid = null;
            if (recipe.fluid != null && recipe.fluid.type != null && recipe.fluid.fill > 0) {
                String registryName = recipe.fluid.type.getName().toLowerCase(Locale.ROOT);
                inputFluid = new FluidColorIngredient(
                        recipe.fluid.type.getLocalizedName(),
                        registryName,
                        recipe.fluid.fill,
                        recipe.fluid.type.getColor(),
                        recipe.fluid.type.getTexture()
                );
            }

            // Выходной предмет
            ItemStack outputItem = recipe.output;

            recipes.add(new SolderingStationRecipeWrapper(toppings, pcb, solder, inputFluid, outputItem, recipe.duration, recipe.consumption));
        }
        return recipes;
    }
}