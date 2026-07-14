package com.hbm.integration.jei.crucible;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.MaterialIngredient;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.CrucibleRecipes;
import com.hbm.inventory.recipes.CrucibleRecipes.CrucibleRecipe;

import java.util.ArrayList;
import java.util.List;

public class CrucibleRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for (CrucibleRecipe recipe : CrucibleRecipes.recipes) {
            List<MaterialIngredient> inputs = new ArrayList<>();
            for (MaterialStack stack : recipe.input) {
                inputs.add(new MaterialIngredient(stack.material, stack.amount));
            }

            List<MaterialIngredient> outputs = new ArrayList<>();
            for (MaterialStack stack : recipe.output) {
                outputs.add(new MaterialIngredient(stack.material, stack.amount));
            }

            recipes.add(new CrucibleRecipeWrapper(inputs, outputs));
        }
        return recipes;
    }
}