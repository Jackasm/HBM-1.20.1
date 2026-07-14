package com.hbm.integration.jei.crystallizer;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.JeiUtil;
import com.hbm.inventory.recipes.CrystallizerRecipes;
import com.hbm.inventory.recipes.CrystallizerRecipes.CrystallizerRecipe;
import com.hbm.inventory.recipes.common.TagStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrystallizerRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for (Map.Entry<Object, CrystallizerRecipe> entry : CrystallizerRecipes.getRecipes().entrySet()) {
            Object input = entry.getKey();
            CrystallizerRecipe recipe = entry.getValue();

            List<ItemStack> inputs = new ArrayList<>();

            if (input instanceof TagStack tagStack) {
                inputs.addAll(JeiUtil.getStacksFromAStack(tagStack));
            } else if (input instanceof ItemStack itemStack) {
                inputs.add(itemStack);
            }

            FluidColorIngredient fluidInput = FluidColorIngredient.fromFluid(recipe.acid, recipe.acidAmount);

            recipes.add(new CrystallizerRecipeWrapper(inputs, fluidInput, recipe.output.copy(), recipe.duration));
        }

        return recipes;
    }
}