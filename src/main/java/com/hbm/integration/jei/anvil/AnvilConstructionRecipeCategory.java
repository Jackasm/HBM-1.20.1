package com.hbm.integration.jei.anvil;

import com.hbm.blocks.ModBlocks;
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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class AnvilConstructionRecipeCategory implements IRecipeCategory<HBMRecipeWrapper> {
    private final IDrawable background;
    private final IDrawable icon;

    public static final RecipeType<HBMRecipeWrapper> TYPE = HBMJeiRecipeTypes.ANVIL_CONSTRUCTION;

    public AnvilConstructionRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(
                ResLocation(MODID, "textures/gui/jei/construction.png"),
                0, 0, 176, 86
        );
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.ANVIL_IRON.get()));
    }

    @Override
    public @NotNull RecipeType<HBMRecipeWrapper> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("container.anvil.construction");
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull HBMRecipeWrapper recipe, @NotNull IFocusGroup focuses) {
        recipe.setRecipe(builder, focuses);
    }

    @Override
    public void draw(@NotNull HBMRecipeWrapper recipe, @NotNull mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 86;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }
}