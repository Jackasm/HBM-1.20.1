package com.hbm.integration.jei.arcwelder;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArcWelderRecipeWrapper implements HBMRecipeWrapper {

    private final List<ItemStack> inputs;
    private final FluidColorIngredient inputFluid;
    private final ItemStack output;
    private final int duration;
    private final long consumption;

    // Позиции слотов на фоне
    private static final int[][] INPUT_POSITIONS = {
            {17, 36},   // слот 0
            {35, 36},   // слот 1
            {53, 36}    // слот 2
    };

    private static final int FLUID_POS_X = 35;
    private static final int FLUID_POS_Y = 63;
    private static final int OUTPUT_POS_X = 107;
    private static final int OUTPUT_POS_Y = 36;

    public ArcWelderRecipeWrapper(List<ItemStack> inputs, FluidColorIngredient inputFluid,
                                  ItemStack output, int duration, long consumption) {
        this.inputs = inputs;
        this.inputFluid = inputFluid;
        this.output = output;
        this.duration = duration;
        this.consumption = consumption;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Входные предметы (3 слота)
        for (int i = 0; i < inputs.size() && i < INPUT_POSITIONS.length; i++) {
            int[] pos = INPUT_POSITIONS[i];
            builder.addSlot(RecipeIngredientRole.INPUT, pos[0], pos[1])
                    .addItemStack(inputs.get(i));
        }

        // Входная жидкость (если есть)
        if (inputFluid != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, FLUID_POS_X, FLUID_POS_Y)
                    .addIngredient(FluidColorIngredient.TYPE, inputFluid);
        }

        // Выходной предмет
        if (output != null && !output.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_POS_X, OUTPUT_POS_Y)
                    .addItemStack(output);
        }
    }
}