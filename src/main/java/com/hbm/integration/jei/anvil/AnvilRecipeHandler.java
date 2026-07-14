package com.hbm.integration.jei.anvil;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.recipes.AnvilRecipes;
import com.hbm.inventory.recipes.AnvilRecipes.AnvilSmithingRecipe;

import java.util.ArrayList;
import java.util.List;

public class AnvilRecipeHandler {
    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for(AnvilSmithingRecipe recipe : AnvilRecipes.getSmithing()) {
            recipes.add(new AnvilRecipeWrapper(recipe));
        }

        return recipes;
    }
}