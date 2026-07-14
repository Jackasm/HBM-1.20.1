package com.hbm.integration.jei.anvil;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.recipes.AnvilRecipes.AnvilSmithingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnvilRecipeWrapper implements HBMRecipeWrapper { // Изменили с record на class
    private final AnvilSmithingRecipe recipe;

    public AnvilRecipeWrapper(AnvilSmithingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull IFocusGroup focuses) {
        setupSmithingRecipe(builder, recipe);
    }

    private void setupSmithingRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull AnvilSmithingRecipe recipe) {
        List<ItemStack> leftStacks = recipe.getLeft();
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 35)
                .addItemStacks(leftStacks);

        List<ItemStack> rightStacks = recipe.getRight();
        builder.addSlot(RecipeIngredientRole.INPUT, 80, 35)
                .addItemStacks(rightStacks);

        ItemStack output = recipe.getSimpleOutput();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35)
                .addItemStack(output);
    }
}