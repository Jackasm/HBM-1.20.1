package com.hbm.integration.jei.casting;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.MaterialIngredient;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

public class CastingRecipeWrapper implements HBMRecipeWrapper {

    private final ItemStack mold;
    private final MaterialIngredient material;
    private final ItemStack output;

    public CastingRecipeWrapper(ItemStack mold, MaterialIngredient material, ItemStack output) {
        this.mold = mold;
        this.material = material;
        this.output = output;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Слот для формы
        if (!mold.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 30, 24)
                    .addItemStack(mold);
        }

        // Слот для материала (жидкость)
        if (material != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 57, 24)
                    .addIngredient(MaterialIngredient.TYPE, material);
        }

        // Выходной предмет
        if (!output.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 24)
                    .addItemStack(output);
        }
    }
}