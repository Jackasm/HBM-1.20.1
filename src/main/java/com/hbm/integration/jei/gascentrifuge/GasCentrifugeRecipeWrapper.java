package com.hbm.integration.jei.gascentrifuge;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.items.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GasCentrifugeRecipeWrapper implements HBMRecipeWrapper {

    private final FluidColorIngredient fluidInput;
    private final List<ItemStack> outputs;
    private final boolean isHighSpeed;
    private final int centrifugeCount;

    private static final int FLUID_INPUT_X = 8;
    private static final int FLUID_INPUT_Y = 44;
    private static final int OUTPUT_START_X = 98;
    private static final int OUTPUT_Y = 44;
    private static final int OUTPUT_SLOT_SIZE = 18;

    public GasCentrifugeRecipeWrapper(FluidColorIngredient fluidInput, List<ItemStack> outputs,
                                      boolean isHighSpeed, int centrifugeCount) {
        this.fluidInput = fluidInput;
        this.outputs = outputs;
        this.isHighSpeed = isHighSpeed;
        this.centrifugeCount = centrifugeCount;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Входная жидкость
        builder.addSlot(RecipeIngredientRole.INPUT, FLUID_INPUT_X, FLUID_INPUT_Y)
                .addIngredient(FluidColorIngredient.TYPE, fluidInput);

        // Выходные предметы (максимум 4)
        int slotIndex = 0;
        for (int i = 0; i < Math.min(4, outputs.size()); i++) {
            ItemStack stack = outputs.get(i);
            if (!stack.isEmpty() && stack.getItem() != ModItems.NOTHING.get()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT,
                                OUTPUT_START_X + slotIndex * OUTPUT_SLOT_SIZE, OUTPUT_Y)
                        .addItemStack(stack);
                slotIndex++;
            }
        }
    }

    public boolean isHighSpeed() { return isHighSpeed; }
    public int getCentrifugeCount() { return centrifugeCount; }
}