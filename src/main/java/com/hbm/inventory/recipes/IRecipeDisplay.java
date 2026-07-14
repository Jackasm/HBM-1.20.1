package com.hbm.inventory.recipes;

import net.minecraft.world.item.ItemStack;
import java.util.List;

/**
 * Интерфейс для унифицированного получения отображаемых данных рецепта.
 */
public interface IRecipeDisplay {
    /**
     * @return список выходных предметов для отображения в GUI
     */
    List<ItemStack> getDisplayOutputs();

    /**
     * @return список входных предметов для отображения в GUI (ингредиенты)
     */
    List<ItemStack> getDisplayIngredients();
}
