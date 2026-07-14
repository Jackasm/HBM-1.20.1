package com.hbm.integration.jei.press;

import com.hbm.integration.jei.HBMRecipeWrapper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PressRecipeWrapper implements HBMRecipeWrapper {

    private final ItemStack input;
    private final List<ItemStack> stampVariants;
    private final ItemStack output;
    private long lastUpdateTime = 0;
    private int currentStampIndex = 0;

    public PressRecipeWrapper(ItemStack input, List<ItemStack> stampVariants, ItemStack output) {
        this.input = input;
        this.stampVariants = stampVariants;
        this.output = output;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Слот для входного предмета
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 35)
                .addItemStack(input);

        // Слот для штампа (будет анимироваться)
        builder.addSlot(RecipeIngredientRole.INPUT, 80, 35)
                .addItemStacks(stampVariants);

        // Слот для выхода
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35)
                .addItemStack(output);
    }

    // Метод для обновления анимации
    public void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > 1000) { // Меняем каждую секунду
            lastUpdateTime = currentTime;
            currentStampIndex = (currentStampIndex + 1) % stampVariants.size();
        }
    }

    public ItemStack getCurrentStamp() {
        if (stampVariants.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return stampVariants.get(currentStampIndex);
    }

    public ItemStack getInput() {
        return input;
    }

    public List<ItemStack> getStampVariants() {
        return stampVariants;
    }

    public ItemStack getOutput() {
        return output;
    }
}