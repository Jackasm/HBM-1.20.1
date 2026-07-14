package com.hbm.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;

public interface HBMRecipeWrapper {
    void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses);
}