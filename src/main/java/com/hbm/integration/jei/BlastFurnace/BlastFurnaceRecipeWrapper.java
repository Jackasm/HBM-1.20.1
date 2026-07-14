package com.hbm.integration.jei.BlastFurnace;

import com.hbm.integration.jei.HBMRecipeWrapper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BlastFurnaceRecipeWrapper implements HBMRecipeWrapper {

    private final List<ItemStack> input1Variants;
    private final List<ItemStack> input2Variants;
    private final List<ItemStack> fuelVariants;
    private final List<ItemStack> outputVariants;
    private final ItemStack baseOutput;

    public BlastFurnaceRecipeWrapper(List<ItemStack> input1Variants, List<ItemStack> input2Variants,
                                     List<ItemStack> fuelVariants, List<ItemStack> outputVariants,
                                     ItemStack baseOutput) {
        this.input1Variants = input1Variants;
        this.input2Variants = input2Variants;
        this.fuelVariants = fuelVariants;
        this.outputVariants = outputVariants;
        this.baseOutput = baseOutput;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 36)
                .addItemStacks(fuelVariants);

        builder.addSlot(RecipeIngredientRole.INPUT, 81, 18)
                .addItemStacks(input1Variants);

        builder.addSlot(RecipeIngredientRole.INPUT, 81, 54)
                .addItemStacks(input2Variants);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 135, 36)
                .addItemStacks(outputVariants);
    }

    public List<ItemStack> getInput1Variants() {
        return input1Variants;
    }

    public List<ItemStack> getInput2Variants() {
        return input2Variants;
    }

    public List<ItemStack> getFuelVariants() {
        return fuelVariants;
    }

    public List<ItemStack> getOutputVariants() {
        return outputVariants;
    }

    public ItemStack getBaseOutput() {
        return baseOutput;
    }
}