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
        // Определяем количество рядов (rows) по последнему занятому слоту
        int maxIndex = -1;
        for (int i = 0; i < inputsVariants.size(); i++) {
            if (!inputsVariants.get(i).isEmpty()) {
                maxIndex = i;
            }
        }
        if (maxIndex == -1) return; // нет ингредиентов

        int cols = 3; // всегда 3 колонки
        int rows = (maxIndex / cols) + 1;

        // Центр сетки по горизонтали и вертикали
        int xCenter = 64;  // центр сетки по X (можно подобрать)
        int yCenter = 43;  // центр сетки по Y (совпадает с центром выходного слота)

        int startX = xCenter - (cols * 18) / 2;
        int startY = yCenter - (rows * 18) / 2;

        // Добавляем слоты для ингредиентов
        for (int i = 0; i < inputsVariants.size() && i < 9; i++) {
            List<ItemStack> variants = inputsVariants.get(i);
            if (variants.isEmpty()) continue;

            int row = i / cols;
            int col = i % cols;
            int slotX = startX + col * 18;
            int slotY = startY + row * 18;

            builder.addSlot(RecipeIngredientRole.INPUT, slotX, slotY)
                    .addItemStacks(variants);
        }

        // Выходной предмет (первый выход)
        ItemStack output = recipe.output.get(0).stack.copy();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35)
                .addItemStack(output);
    }
}