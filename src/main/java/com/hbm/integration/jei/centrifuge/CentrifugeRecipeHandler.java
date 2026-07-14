package com.hbm.integration.jei.centrifuge;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CentrifugeRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for (Map.Entry<com.hbm.inventory.recipes.common.AStack, ItemStack[]> entry : CentrifugeRecipes.recipes.entrySet()) {
            com.hbm.inventory.recipes.common.AStack input = entry.getKey();
            ItemStack[] outputs = entry.getValue();

            recipes.add(new CentrifugeRecipeWrapper(input, outputs));
        }

        return recipes;
    }
}