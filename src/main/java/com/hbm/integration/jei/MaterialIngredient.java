package com.hbm.integration.jei;

import com.hbm.inventory.material.NTMMaterial;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@SuppressWarnings("removal")
public class MaterialIngredient {
    private final NTMMaterial material;
    private final int amount;

    public static final IIngredientType<MaterialIngredient> TYPE = new Type();

    public MaterialIngredient(NTMMaterial material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public NTMMaterial getMaterial() { return material; }
    public int getAmount() { return amount; }

    public static class Type implements IIngredientType<MaterialIngredient> {
        @Override
        public @NotNull Class<? extends MaterialIngredient> getIngredientClass() {
            return MaterialIngredient.class;
        }
    }

    public static class Renderer implements IIngredientRenderer<MaterialIngredient> {
        private static final ResourceLocation TEXTURE = ResLocation("hbm", "textures/block/network/lava_gray.png");
        @Override
        public void render(@NotNull GuiGraphics graphics, @Nullable MaterialIngredient ingredient) {
            if (ingredient == null) return;
            render(graphics, 0, 0, ingredient);
        }

        public void render(@NotNull GuiGraphics graphics, int x, int y, @Nullable MaterialIngredient ingredient) {
            if (ingredient == null) return;
            RenderSystem.enableBlend();
            int color = ingredient.material.moltenColor;
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            RenderSystem.setShaderColor(r, g, b, 1f);
            graphics.blit(TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
        @Override
        public @NotNull List<Component> getTooltip(@Nullable MaterialIngredient ingredient, @NotNull TooltipFlag tooltipFlag) {
            List<Component> tooltip = new ArrayList<>();
            if (ingredient != null) {
                tooltip.add(Component.translatable(ingredient.material.getUnlocalizedName()));
                tooltip.add(Component.literal(ingredient.amount + " mB"));
            }
            return tooltip;
        }
        @Override
        public void getTooltip(mezz.jei.api.gui.builder.@NotNull ITooltipBuilder tooltip, @Nullable MaterialIngredient ingredient, @NotNull TooltipFlag tooltipFlag) {
            if (ingredient != null) {
                tooltip.add(Component.translatable(ingredient.material.getUnlocalizedName()));
                tooltip.add(Component.literal(ingredient.amount + " mB"));
            }
        }
        @Override
        public int getWidth() { return 16; }
        @Override
        public int getHeight() { return 16; }
    }

    public static class Helper implements IIngredientHelper<MaterialIngredient> {
        @Override
        public @NotNull String getDisplayName(MaterialIngredient ingredient) {
            return Component.translatable(ingredient.material.getUnlocalizedName()).getString();
        }
        @Override
        public @NotNull IIngredientType<MaterialIngredient> getIngredientType() { return TYPE; }
        @Override
        public @NotNull String getUniqueId(MaterialIngredient ingredient, @NotNull mezz.jei.api.ingredients.subtypes.UidContext context) {
            return "material_" + ingredient.material.id + "_" + ingredient.amount;
        }
        @Override
        public @NotNull String getDisplayModId(@NotNull MaterialIngredient ingredient) { return "hbm"; }
        @Override
        public @NotNull Iterable<Integer> getColors(MaterialIngredient ingredient) { return List.of(ingredient.material.moltenColor); }
        @Override
        public @NotNull String getErrorInfo(@Nullable MaterialIngredient ingredient) { return ingredient == null ? "null" : ingredient.material.names[0]; }
        @Override
        public @NotNull MaterialIngredient copyIngredient(MaterialIngredient ingredient) { return new MaterialIngredient(ingredient.material, ingredient.amount); }
        @Override
        public boolean isValidIngredient(@NotNull MaterialIngredient ingredient) { return ingredient != null && ingredient.material != null; }
        @Override
        public @NotNull ResourceLocation getResourceLocation(MaterialIngredient ingredient) { return ResLocation("hbm", ingredient.material.names[0]); }
    }
}