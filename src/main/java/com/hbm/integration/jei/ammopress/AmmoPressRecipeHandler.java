package com.hbm.integration.jei.ammopress;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.recipes.AmmoPressRecipes;
import com.hbm.inventory.recipes.AmmoPressRecipes.AmmoPressRecipe;
import com.hbm.inventory.recipes.common.AStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AmmoPressRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for (AmmoPressRecipe recipe : AmmoPressRecipes.recipes) {
            // Создаём список из 9 элементов с вариантами для каждого слота
            List<List<ItemStack>> inputVariants = new ArrayList<>(9);
            for (int i = 0; i < 9; i++) {
                inputVariants.add(new ArrayList<>());
            }

            // Заполняем слоты вариантами, сохраняя позицию
            for (int i = 0; i < 9; i++) {
                AStack stack = recipe.input[i];
                if (stack != null) {
                    List<ItemStack> variants = stack.extractForNEI();
                    if (!variants.isEmpty()) {
                        inputVariants.set(i, variants);
                    }
                }
            }

            ItemStack output = recipe.output.copy();
            recipes.add(new AmmoPressRecipeWrapper(inputVariants, output));
        }

        return recipes;
    }
}