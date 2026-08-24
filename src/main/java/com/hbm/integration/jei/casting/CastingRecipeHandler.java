package com.hbm.integration.jei.casting;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.MaterialIngredient;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.items.machine.ItemMold;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CastingRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        // Перебираем все типы форм
        for (ItemMold.MoldType moldType : ItemMold.MoldType.values()) {
            // Перебираем все материалы
            for (NTMMaterial mat : Mats.orderedList) {
                // Пропускаем материалы, которые не плавятся
                if (mat.smeltable == NTMMaterial.SmeltingBehavior.NOT_SMELTABLE) continue;
                if (mat.moltenColor == 0) continue;

                // Проверяем, есть ли выходной предмет для этого материала и формы
                ItemStack output = moldType.getOutput(mat);
                if (output == null || output.isEmpty()) continue;

                // Создаём форму
                ItemStack moldStack = ItemMold.getMoldStack(moldType);
                if (moldStack.isEmpty()) continue;

                // Количество материала для этой формы
                int amount = moldType.getCost();

                // Создаём ингредиент материала (жидкость)
                MaterialIngredient materialIngredient = new MaterialIngredient(mat, amount);

                recipes.add(new CastingRecipeWrapper(moldStack, materialIngredient, output));
            }
        }

        return recipes;
    }
}