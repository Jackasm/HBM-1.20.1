package com.hbm.integration.jei.anvil;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.recipes.AnvilRecipes;

import java.util.ArrayList;
import java.util.List;

public class AnvilConstructionRecipeHandler {
    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();
        for (var recipe : AnvilRecipes.getConstruction()) {
            recipes.add(new AnvilConstructionRecipeWrapper(recipe));
        }
        return recipes;
    }
}