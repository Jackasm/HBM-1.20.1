package com.hbm.integration.jei.combinationfurnace;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CombinationFurnaceRecipeWrapper implements HBMRecipeWrapper {

    private final List<ItemStack> inputVariants;
    private final ItemStack outputItem;
    private final FluidColorIngredient outputFluid;

    public CombinationFurnaceRecipeWrapper(List<ItemStack> inputVariants, ItemStack outputItem, FluidColorIngredient outputFluid) {
        this.inputVariants = inputVariants;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Входной слот (ингредиент)
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 40)
                .addItemStacks(inputVariants);

        // Выходной предмет (если есть)
        if (outputItem != null && !outputItem.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 40)
                    .addItemStack(outputItem);
        }

        // Выходная жидкость (если есть) - используем кастомный ингредиент
        if (outputFluid != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 118, 58)
                    .addIngredient(FluidColorIngredient.TYPE, outputFluid);
        }
    }
}