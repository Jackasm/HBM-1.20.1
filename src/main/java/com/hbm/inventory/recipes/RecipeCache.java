package com.hbm.inventory.recipes;

import net.minecraft.world.item.ItemStack;

import java.util.*;

public class RecipeCache {

    // Кэш: RecipeType -> список рецептов с преобразованными данными
    private static final Map<RecipeType, List<DisplayRecipe>> cache = new HashMap<>();

    /**
     * Получить все рецепты для типа с преобразованными данными
     */
    public static List<DisplayRecipe> getDisplayRecipes(RecipeType type) {
        if (!cache.containsKey(type)) {
            loadRecipes(type);
        }
        return cache.getOrDefault(type, Collections.emptyList());
    }

    /**
     * Загрузить рецепты для типа
     */
    private static void loadRecipes(RecipeType type) {
        List<DisplayRecipe> displayRecipes = new ArrayList<>();
        List<?> rawRecipes = type.getRecipes();

        for (Object recipe : rawRecipes) {
            List<ItemStack> outputs = RecipeDisplayUtil.getDisplayItems(recipe);
            List<ItemStack> ingredients = RecipeDisplayUtil.getDisplayIngredients(recipe);

            if (!outputs.isEmpty()) {
                displayRecipes.add(new DisplayRecipe(outputs, ingredients, recipe));
            }
        }

        cache.put(type, displayRecipes);
    }

    /**
     * Перезагрузить кэш (при перезагрузке рецептов)
     */
    public static void reload() {
        cache.clear();
    }

    /**
     * Класс для хранения отображаемых данных рецепта
     */
    public static class DisplayRecipe {
        private final List<ItemStack> outputs;
        private final List<ItemStack> ingredients;
        private final Object rawRecipe;

        public DisplayRecipe(List<ItemStack> outputs, List<ItemStack> ingredients, Object rawRecipe) {
            this.outputs = outputs;
            this.ingredients = ingredients;
            this.rawRecipe = rawRecipe;
        }

        public List<ItemStack> getOutputs() {
            return outputs;
        }

        public List<ItemStack> getIngredients() {
            return ingredients;
        }

        public Object getRawRecipe() {
            return rawRecipe;
        }
    }
}