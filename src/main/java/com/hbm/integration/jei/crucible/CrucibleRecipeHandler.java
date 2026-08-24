package com.hbm.integration.jei.crucible;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.MaterialIngredient;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.inventory.recipes.CrucibleRecipes;
import com.hbm.inventory.recipes.CrucibleRecipes.CrucibleRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.hbm.util.RefStrings.MODID;

public class CrucibleRecipeHandler {

    // Вспомогательный класс для хранения входа и его количества
    public static class InputWithAmount {
        ItemStack stack;
        int amount;

        InputWithAmount(ItemStack stack, int amount) {
            this.stack = stack;
            this.amount = amount;
        }
    }

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        // 1. Рецепты сплавов
        for (CrucibleRecipe recipe : CrucibleRecipes.recipes) {
            List<MaterialIngredient> inputs = new ArrayList<>();
            for (MaterialStack stack : recipe.input) {
                inputs.add(new MaterialIngredient(stack.material, stack.amount));
            }

            List<MaterialIngredient> outputs = new ArrayList<>();
            for (MaterialStack stack : recipe.output) {
                outputs.add(new MaterialIngredient(stack.material, stack.amount));
            }

            recipes.add(new CrucibleRecipeWrapper(inputs, outputs));
        }

        // 2. Обычная плавка — группируем по материалу
        Map<NTMMaterial, List<InputWithAmount>> groupedRecipes = new HashMap<>();

        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id == null || !id.getNamespace().equals(MODID)) continue;

            ItemStack stack = new ItemStack(item, 1);
            List<MaterialStack> smeltResult = Mats.getSmeltingMaterialsFromItem(stack);
            if (smeltResult.isEmpty()) continue;

            // Берём первый результат (обычно он один)
            MaterialStack result = smeltResult.get(0);
            if (result == null || result.amount <= 0) continue;

            // Сохраняем предмет с его количеством
            groupedRecipes.computeIfAbsent(result.material, k -> new ArrayList<>())
                    .add(new InputWithAmount(stack.copy(), result.amount));
        }

        // 3. Создаём рецепты для каждой группы
        for (Map.Entry<NTMMaterial, List<InputWithAmount>> entry : groupedRecipes.entrySet()) {
            NTMMaterial material = entry.getKey();
            List<InputWithAmount> inputsWithAmount = entry.getValue();

            List<ItemStack> inputStacks = inputsWithAmount.stream()
                    .map(ia -> ia.stack)
                    .toList();

            // Показываем минимальное количество (обычно 1 слиток)
            int minAmount = inputsWithAmount.stream()
                    .mapToInt(ia -> ia.amount)
                    .min()
                    .orElse(MaterialShapes.INGOT.q(1));

            List<MaterialIngredient> outputMaterials = List.of(
                    new MaterialIngredient(material, minAmount)
            );

            recipes.add(new CrucibleSmeltingWrapper(inputStacks, outputMaterials));
        }

        return recipes;
    }
}