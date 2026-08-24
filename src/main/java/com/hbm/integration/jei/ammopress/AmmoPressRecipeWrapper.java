package com.hbm.integration.jei.ammopress;

import com.hbm.integration.jei.HBMRecipeWrapper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AmmoPressRecipeWrapper implements HBMRecipeWrapper {

    private final List<List<ItemStack>> inputVariants; // список вариантов для каждого слота
    private final ItemStack output;

    public AmmoPressRecipeWrapper(List<List<ItemStack>> inputVariants, ItemStack output) {
        this.inputVariants = inputVariants;
        this.output = output;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Входные слоты (3x3 сетка)
        for (int i = 0; i < 9 && i < inputVariants.size(); i++) {
            int row = i / 3;
            int col = i % 3;
            int x = 59 + col * 18;
            int y = 15 + row * 18;

            List<ItemStack> variants = inputVariants.get(i);
            if (variants != null && !variants.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addItemStacks(variants); // добавляем все варианты
            }
        }

        // Выходной слот
        if (output != null && !output.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 69)
                    .addItemStack(output);
        }
    }
}