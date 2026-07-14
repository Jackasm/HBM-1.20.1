package com.hbm.integration.jei.gascentrifuge;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.GasCentrifugeRecipes;
import com.hbm.items.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GasCentrifugeRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        Map<Object, Object[]> jeiRecipes = GasCentrifugeRecipes.getGasCentrifugeRecipes();

        for (Map.Entry<Object, Object[]> entry : jeiRecipes.entrySet()) {
            Object key = entry.getKey();
            Object[] data = entry.getValue();

            // Ключ — это ItemStack (иконка жидкости)
            if (!(key instanceof ItemStack fluidIcon)) continue;

            // Извлекаем данные
            ItemStack[] outputs = (ItemStack[]) data[0];
            boolean isHighSpeed = (boolean) data[1];
            int centrifugeCount = (int) data[2];

            // Конвертируем fluidIcon в FluidColorIngredient
            // fluidIcon - это ItemStack с жидкостью (ItemFluidIcon)
            // Можно получить FluidType из damage или NBT
            int fluidId = fluidIcon.getDamageValue(); // предполагаем, что ID жидкости хранится в damage
            var fluid = Fluids.fromID(fluidId);
            if (fluid == null || fluid == Fluids.NONE.get()) continue;

            int amount = 1000; // по умолчанию 1000 мБ, можно уточнить из рецепта, но у нас нет точного количества, используем стандартное

            FluidColorIngredient fluidIngredient = FluidColorIngredient.fromFluid(fluid, amount);

            // Собираем выходные предметы (убираем NOTHING)
            List<ItemStack> outputStacks = new ArrayList<>();
            for (ItemStack stack : outputs) {
                if (!stack.isEmpty() && stack.getItem() != ModItems.NOTHING.get()) {
                    outputStacks.add(stack.copy());
                }
            }

            recipes.add(new GasCentrifugeRecipeWrapper(fluidIngredient, outputStacks, isHighSpeed, centrifugeCount));
        }

        return recipes;
    }
}