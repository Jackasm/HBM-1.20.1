package com.hbm.blocks;

import com.hbm.inventory.recipes.RecipeType;

public interface ICraftingMachine {
    RecipeType getRecipeType(); // или String getRecipeTypeId()
}