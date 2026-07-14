package com.hbm.integration.jei;

import com.hbm.inventory.fluid.FluidTypeHBM;
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
import java.util.Locale;

import static com.hbm.util.ResLocation.ResLocation;

public class FluidColorIngredient {
    private final String fluidName;        // отображаемое имя (для тултипа)
    private final String registryName;     // registry name (для ResourceLocation)
    private final int amount;
    private final int color;
    private final ResourceLocation texture;

    // Определяем тип как статическую константу для удобства
    public static final IIngredientType<FluidColorIngredient> TYPE = new Type();

    public FluidColorIngredient(String fluidName, String registryName, int amount, int color, ResourceLocation texture) {
        this.fluidName = fluidName;
        this.registryName = registryName;
        this.amount = amount;
        this.color = color;
        this.texture = texture;
    }

    public static FluidColorIngredient fromFluid(FluidTypeHBM fluid, int amount) {
        // Используем getName() который возвращает латиницу (например "sodium")
        String registryName = fluid.getName().toLowerCase(Locale.ROOT);

        return new FluidColorIngredient(
                fluid.getLocalizedName(),
                registryName,
                amount,
                fluid.getColor(),
                fluid.getTexture()
        );
    }

    public static FluidColorIngredient fromFluid(FluidTypeHBM fluid) {
        return fromFluid(fluid, 1000);
    }

    public String getFluidName() { return fluidName; }
    public String getRegistryName() { return registryName; }
    public int getAmount() { return amount; }
    public int getColor() { return color; }
    public ResourceLocation getTexture() { return texture; }

    // Тип ингредиента
    public static class Type implements IIngredientType<FluidColorIngredient> {
        @Override
        public @NotNull Class<? extends FluidColorIngredient> getIngredientClass() {
            return FluidColorIngredient.class;
        }
    }

    // Рендерер
    public static class Renderer implements IIngredientRenderer<FluidColorIngredient> {

        @Override
        public void render(@NotNull GuiGraphics graphics, @Nullable FluidColorIngredient ingredient) {
            if (ingredient == null) return;

            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();

            renderFluidIngredient(graphics, 0, 0, ingredient);

            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
        }

        // Этот метод уже есть в интерфейсе как default, но мы можем его переопределить для ясности
        @Override
        public void render(@NotNull GuiGraphics guiGraphics, @Nullable FluidColorIngredient ingredient, int posX, int posY) {
            if (ingredient == null) return;

            var pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(posX, posY, 0);
            this.render(guiGraphics, ingredient);
            pose.popPose();
        }

        // Устаревший метод, но мы обязаны его реализовать
        @Override
        public @NotNull List<Component> getTooltip(@Nullable FluidColorIngredient ingredient, @NotNull TooltipFlag tooltipFlag) {
            List<Component> tooltip = new ArrayList<>();
            if (ingredient != null) {
                tooltip.add(Component.literal(ingredient.getFluidName()));
                tooltip.add(Component.literal(ingredient.getAmount() + " mB"));
            }
            return tooltip;
        }

        // Новый метод с ITooltipBuilder (рекомендуемый)
        @Override
        public void getTooltip(mezz.jei.api.gui.builder.@NotNull ITooltipBuilder tooltip,
                               @Nullable FluidColorIngredient ingredient,
                               @NotNull TooltipFlag tooltipFlag) {
            if (ingredient != null) {
                tooltip.add(Component.literal(ingredient.getFluidName()));
                tooltip.add(Component.literal(ingredient.getAmount() + " mB"));
            }
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        private void renderFluidIngredient(GuiGraphics guiGraphics, int x, int y, @Nullable FluidColorIngredient ingredient) {
            if (ingredient == null) return;

            ResourceLocation fluidTexture = ingredient.getTexture();
            int color = ingredient.getColor();

            // Если нет текстуры — рисуем цветной прямоугольник
            if (fluidTexture == null) {
                guiGraphics.fill(x, y, x + 16, y + 16, 0xFF000000 | color);
                return;
            }

            var pose = guiGraphics.pose();
            pose.pushPose();

            // Применяем цвет оттенка
            float r = ((color >> 16) & 0xFF) / 255.0F;
            float g = ((color >> 8) & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            RenderSystem.setShaderColor(r, g, b, 1.0F);

            // Рисуем текстуру 16x16
            guiGraphics.blit(fluidTexture, x, y, 0, 0, 16, 16, 16, 16);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            pose.popPose();
        }
    }

    // Helper
    public static class Helper implements IIngredientHelper<FluidColorIngredient> {

        @Override
        public @NotNull String getDisplayName(FluidColorIngredient ingredient) {
            return ingredient.getFluidName();
        }

        @Override
        public @NotNull IIngredientType<FluidColorIngredient> getIngredientType() {
            return TYPE;
        }

        @Override
        public @NotNull String getUniqueId(FluidColorIngredient ingredient, mezz.jei.api.ingredients.subtypes.@NotNull UidContext context) {
            return "fluid_color_" + ingredient.getRegistryName() + "_" + ingredient.getAmount();
        }

        @Override
        public @NotNull String getDisplayModId(@NotNull FluidColorIngredient ingredient) {
            return "hbm";
        }

        @Override
        public @NotNull Iterable<Integer> getColors(FluidColorIngredient ingredient) {
            return List.of(ingredient.getColor());
        }

        @Override
        public @NotNull String getErrorInfo(@Nullable FluidColorIngredient ingredient) {
            return ingredient == null ? "null" : ingredient.getRegistryName();
        }

        @Override
        public @NotNull FluidColorIngredient copyIngredient(FluidColorIngredient ingredient) {
            return new FluidColorIngredient(
                    ingredient.getFluidName(),
                    ingredient.getRegistryName(),
                    ingredient.getAmount(),
                    ingredient.getColor(),
                    ingredient.getTexture()
            );
        }

        @Override
        public boolean isValidIngredient(@NotNull FluidColorIngredient ingredient) {
            return ingredient != null && ingredient.getRegistryName() != null;
        }

        @Override
        public @NotNull ResourceLocation getResourceLocation(FluidColorIngredient ingredient) {
            // Используем registry name из getName() (латиница)
            return ResLocation("hbm", ingredient.getRegistryName());
        }
    }
}