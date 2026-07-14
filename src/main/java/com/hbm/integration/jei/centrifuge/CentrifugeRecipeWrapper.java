package com.hbm.integration.jei.centrifuge;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.JeiUtil;
import com.hbm.inventory.recipes.common.AStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CentrifugeRecipeWrapper implements HBMRecipeWrapper {

    private final List<ItemStack> inputs;
    private final List<ItemStack> outputs;

    private static final int INPUT_SLOT_X = 36;
    private static final int INPUT_SLOT_Y = 50;
    private static final int OUTPUT_START_X = 63;
    private static final int OUTPUT_Y = 50;
    private static final int OUTPUT_SLOT_SIZE = 20;
    private static final int MAX_OUTPUTS = 4;

    public CentrifugeRecipeWrapper(AStack input, ItemStack[] outputs) {
        this.inputs = JeiUtil.getStacksFromAStack(input);
        this.outputs = new ArrayList<>();
        for (ItemStack stack : outputs) {
            if (stack != null && !stack.isEmpty()) {
                this.outputs.add(stack.copy());
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Входной предмет (циклически)
        if (!inputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
                    .addItemStacks(inputs);
        }

        // Выходные предметы (до 4)
        for (int i = 0; i < Math.min(MAX_OUTPUTS, outputs.size()); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_START_X + i * OUTPUT_SLOT_SIZE, OUTPUT_Y)
                    .addItemStack(outputs.get(i));
        }
    }


}