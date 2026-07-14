package com.hbm.integration.jei;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class HBMFluidIngredients {

    private static List<FluidColorIngredient> allFluids = null;

    public static Collection<FluidColorIngredient> getAllFluids() {
        if (allFluids == null) {
            allFluids = new ArrayList<>();

            // Получаем все жидкости из Fluids.getAllFluids()
            FluidTypeHBM[] fluids = Fluids.getAllFluids();

            for (FluidTypeHBM fluid : fluids) {
                // Получаем registry name из имени жидкости
                String registryName = fluid.getName().toLowerCase(Locale.ROOT);

                // Добавляем каждую жидкость с количеством 1000 mB для отображения
                allFluids.add(new FluidColorIngredient(
                        fluid.getLocalizedName(),  // отображаемое имя
                        registryName,              // registry name (латиница)
                        1000,                     // количество
                        fluid.getColor(),          // цвет
                        fluid.getTexture()         // текстура
                ));
            }
        }
        return allFluids;
    }
}