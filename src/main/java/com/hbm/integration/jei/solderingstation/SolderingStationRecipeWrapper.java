package com.hbm.integration.jei.solderingstation;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SolderingStationRecipeWrapper implements HBMRecipeWrapper {

    private final List<ItemStack> toppings;
    private final List<ItemStack> pcb;
    private final List<ItemStack> solder;
    private final FluidColorIngredient inputFluid;
    private final ItemStack output;
    private final int duration;
    private final long consumption;

    // Позиции слотов
    private static final int[][] TOPPING_POSITIONS = {
            {17, 18},   // слот 0
            {35, 18},   // слот 1
            {53, 18}    // слот 2
    };

    private static final int[][] PCB_POSITIONS = {
            {17, 36},   // слот 3
            {35, 36}    // слот 4
    };

    private static final int[] SOLDER_POSITION = {53, 36};  // слот 5

    private static final int FLUID_POS_X = 35;
    private static final int FLUID_POS_Y = 63;
    private static final int OUTPUT_POS_X = 107;
    private static final int OUTPUT_POS_Y = 27;

    public SolderingStationRecipeWrapper(List<ItemStack> toppings, List<ItemStack> pcb, List<ItemStack> solder,
                                         FluidColorIngredient inputFluid, ItemStack output,
                                         int duration, long consumption) {
        this.toppings = toppings;
        this.pcb = pcb;
        this.solder = solder;
        this.inputFluid = inputFluid;
        this.output = output;
        this.duration = duration;
        this.consumption = consumption;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Toppings (слоты 0-2)
        for (int i = 0; i < toppings.size() && i < TOPPING_POSITIONS.length; i++) {
            int[] pos = TOPPING_POSITIONS[i];
            builder.addSlot(RecipeIngredientRole.INPUT, pos[0], pos[1])
                    .addItemStack(toppings.get(i));
        }

        // PCB (слоты 3-4)
        for (int i = 0; i < pcb.size() && i < PCB_POSITIONS.length; i++) {
            int[] pos = PCB_POSITIONS[i];
            builder.addSlot(RecipeIngredientRole.INPUT, pos[0], pos[1])
                    .addItemStack(pcb.get(i));
        }

        // Solder (слот 5)
        if (!solder.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, SOLDER_POSITION[0], SOLDER_POSITION[1])
                    .addItemStack(solder.get(0));
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