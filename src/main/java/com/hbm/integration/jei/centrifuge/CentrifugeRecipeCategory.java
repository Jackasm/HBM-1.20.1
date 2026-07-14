package com.hbm.integration.jei.centrifuge;

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

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class CentrifugeRecipeCategory implements IRecipeCategory<HBMRecipeWrapper> {

    public static final RecipeType<HBMRecipeWrapper> TYPE = HBMJeiRecipeTypes.CENTRIFUGE;

    private final IDrawable background;
    private final IDrawable icon;

    public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(
                ResLocation(MODID, "textures/gui/jei/gui_centrifuge.png"),
                0, 0, 176, 78
        );
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.MACHINE_CENTRIFUGE.get()));
    }

    @Override
    public @NotNull RecipeType<HBMRecipeWrapper> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("block.hbm.machines.machine_centrifuge");
    }

    @Override
    public void draw(@NotNull HBMRecipeWrapper recipe, @NotNull mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, HBMRecipeWrapper recipe, @NotNull IFocusGroup focuses) {
        recipe.setRecipe(builder, focuses);
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 78;
    }
}