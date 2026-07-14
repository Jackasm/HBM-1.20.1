package com.hbm.integration.jei.anvil;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.recipes.AnvilRecipes.AnvilConstructionRecipe;
import com.hbm.inventory.recipes.common.AStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AnvilConstructionRecipeWrapper implements HBMRecipeWrapper {
    private final AnvilConstructionRecipe recipe;
    private final List<List<ItemStack>> inputsVariants; // Для каждого слота — список возможных предметов

    public AnvilConstructionRecipeWrapper(AnvilConstructionRecipe recipe) {
        this.recipe = recipe;
        this.inputsVariants = new ArrayList<>();

        // Для каждого входа собираем все возможные предметы
        for (AStack input : recipe.input) {
            List<ItemStack> variants = input.extractForNEI();
            inputsVariants.add(variants);
        }
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull IFocusGroup focuses) {
        // Входные предметы: размещаем в сетке 2x2
        int x = 44;
        int y = 26;
        int offset = 18;

        for (int i = 0; i < inputsVariants.size() && i < 4; i++) {
            int slotX = x + (i % 2) * offset;
            int slotY = y + (i / 2) * offset;
            List<ItemStack> variants = inputsVariants.get(i);

            if (!variants.isEmpty()) {
                // Добавляем все варианты для этого слота
                builder.addSlot(RecipeIngredientRole.INPUT, slotX, slotY)
                        .addItemStacks(variants);
            }
        }

        // Выходной предмет (первый выход)
        ItemStack output = recipe.output.get(0).stack.copy();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35)
                .addItemStack(output);
    }
}