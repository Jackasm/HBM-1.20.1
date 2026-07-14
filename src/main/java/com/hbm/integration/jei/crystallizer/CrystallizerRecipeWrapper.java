package com.hbm.integration.jei.crystallizer;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CrystallizerRecipeWrapper implements HBMRecipeWrapper {

    private final List<ItemStack> inputs;
    private final FluidColorIngredient fluidInput;
    private final ItemStack output;
    private final int duration;

    private static final int INPUT_SLOT_X = 62;
    private static final int INPUT_SLOT_Y = 45;
    private static final int FLUID_INPUT_X = 35;
    private static final int FLUID_INPUT_Y = 54;
    private static final int OUTPUT_X = 113;
    private static final int OUTPUT_Y = 45;

    public CrystallizerRecipeWrapper(List<ItemStack> inputs, FluidColorIngredient fluidInput,
                                     ItemStack output, int duration) {
        this.inputs = inputs;
        this.fluidInput = fluidInput;
        this.output = output;
        this.duration = duration;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {

        if (!inputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
                    .addItemStacks(inputs);
        }

        builder.addSlot(RecipeIngredientRole.INPUT, FLUID_INPUT_X, FLUID_INPUT_Y)
                .addIngredient(FluidColorIngredient.TYPE, fluidInput);

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(output);
    }

    public int getDuration() {
        return duration;
    }
}