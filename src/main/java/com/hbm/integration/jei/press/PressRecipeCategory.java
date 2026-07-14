package com.hbm.integration.jei.press;

import com.hbm.integration.jei.HBMJeiRecipeTypes;
import com.hbm.integration.jei.HBMRecipeWrapper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.hbm.blocks.ModBlocks;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class PressRecipeCategory implements IRecipeCategory<HBMRecipeWrapper> {

    public static final RecipeType<HBMRecipeWrapper> TYPE = HBMJeiRecipeTypes.PRESS;

    private final IDrawable background;
    private final IDrawable icon;

    public PressRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(
                ResLocation(MODID, "textures/gui/jei/smithing.png"),
                0, 0, 176, 86
        );
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.MACHINE_PRESS.get()));
    }

    @Override
    public @NotNull RecipeType<HBMRecipeWrapper> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("container.press");
    }

    @Override
    public void draw(@NotNull HBMRecipeWrapper recipe, @NotNull mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Отрисовываем фон
        this.background.draw(guiGraphics);

        // Здесь можно добавить отрисовку дополнительных элементов:
        // - стрелки прогресса
        // - текста
        // - анимаций
        // Например:
        // guiGraphics.drawString(Minecraft.getInstance().font, "Текст", x, y, color);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, HBMRecipeWrapper recipe, @NotNull IFocusGroup focuses) {
        recipe.setRecipe(builder, focuses);


        if (recipe instanceof PressRecipeWrapper animatedRecipe) {
            animatedRecipe.updateAnimation();
        }
    }

    @Override
    public int getWidth() {
        return 176; // ширина вашей текстуры
    }

    @Override
    public int getHeight() {
        return 86; // высота вашей текстуры
    }
}