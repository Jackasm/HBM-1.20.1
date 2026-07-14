package com.hbm.integration.jei.arcwelder;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.JeiUtil;
import com.hbm.inventory.recipes.ArcWelderRecipes;
import com.hbm.inventory.recipes.ArcWelderRecipes.ArcWelderRecipe;
import com.hbm.inventory.recipes.common.AStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArcWelderRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for (ArcWelderRecipe recipe : ArcWelderRecipes.recipes) {
            // Входные предметы (3 слота)
            List<ItemStack> inputs = new ArrayList<>();
            for (AStack aStack : recipe.ingredients) {
                List<ItemStack> variants = JeiUtil.getStacksFromAStack(aStack);
                if (variants.isEmpty()) {
                    inputs.clear();
                    break;
                }
                inputs.add(variants.get(0));
            }
            if (inputs.isEmpty()) continue;

            // Входная жидкость (опционально)
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

            recipes.add(new ArcWelderRecipeWrapper(inputs, inputFluid, outputItem, recipe.duration, recipe.consumption));
        }
        return recipes;
    }
}