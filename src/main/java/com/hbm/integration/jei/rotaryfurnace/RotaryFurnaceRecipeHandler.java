package com.hbm.integration.jei.rotaryfurnace;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.JeiUtil;
import com.hbm.inventory.recipes.RotaryFurnaceRecipes;
import com.hbm.inventory.recipes.RotaryFurnaceRecipes.RotaryFurnaceRecipe;
import com.hbm.inventory.recipes.common.AStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RotaryFurnaceRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for (RotaryFurnaceRecipe recipe : RotaryFurnaceRecipes.recipes) {
            // Список ингредиентов, каждый со своим списком вариантов
            List<List<ItemStack>> allInputVariants = new ArrayList<>();
            boolean valid = true;

            for (AStack aStack : recipe.ingredients) {
                List<ItemStack> variants = JeiUtil.getStacksFromAStack(aStack);
                if (variants.isEmpty()) {
                    valid = false;
                    break;
                }
                allInputVariants.add(variants);
            }
            if (!valid) continue;

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

            recipes.add(new RotaryFurnaceRecipeWrapper(allInputVariants, inputFluid,
                    recipe.output, recipe.duration, recipe.steam));
        }
        return recipes;
    }
}