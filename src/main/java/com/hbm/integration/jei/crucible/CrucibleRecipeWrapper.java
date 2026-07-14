package com.hbm.integration.jei.crucible;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.integration.jei.MaterialIngredient;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;

import java.util.List;

public class CrucibleRecipeWrapper implements HBMRecipeWrapper {

    private final List<MaterialIngredient> inputs;
    private final List<MaterialIngredient> outputs;

    private static final int[][] INPUT_POSITIONS = {
            {8, 44},
            {26, 44},
            {44, 44}
    };
    private static final int OUTPUT_POS_X = 107;
    private static final int OUTPUT_POS_Y = 44;

    public CrucibleRecipeWrapper(List<MaterialIngredient> inputs, List<MaterialIngredient> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Входные материалы
        for (int i = 0; i < inputs.size() && i < INPUT_POSITIONS.length; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_POSITIONS[i][0], INPUT_POSITIONS[i][1])
                    .addIngredient(MaterialIngredient.TYPE, inputs.get(i));
        }

        // Выходные материалы (обычно один или два, например сплав + шлак)
        for (int i = 0; i < outputs.size(); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_POS_X + i * 18, OUTPUT_POS_Y)
                    .addIngredient(MaterialIngredient.TYPE, outputs.get(i));
        }
    }
}