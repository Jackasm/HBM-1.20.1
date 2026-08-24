package com.hbm.integration.jei.crucible;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.MaterialIngredient;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CrucibleSmeltingWrapper implements HBMRecipeWrapper {

    private final List<ItemStack> inputItems;
    private final List<MaterialIngredient> outputMaterials;

    public CrucibleSmeltingWrapper(List<ItemStack> inputItems,
                                   List<MaterialIngredient> outputMaterials) {
        this.inputItems = inputItems;
        this.outputMaterials = outputMaterials;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Входной слот — показываем все предметы циклично
        if (!inputItems.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 44)
                    .addItemStacks(inputItems);
        }

        // Выходной слот
        if (!outputMaterials.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 44)
                    .addIngredient(MaterialIngredient.TYPE, outputMaterials.get(0));
        }
    }

}