package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerClayTablet;
import com.hbm.inventory.recipes.PedestalRecipes;
import com.hbm.inventory.recipes.PedestalRecipes.PedestalRecipe;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIScreenClayTablet extends AbstractContainerScreen<ContainerClayTablet> {

    protected int xSize = 142;
    protected int ySize = 84;
    protected int guiLeft;
    protected int guiTop;
    protected int tabletMeta = 0;

    protected static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/guide_pedestal.png");

    public GUIScreenClayTablet(ContainerClayTablet menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 142;
        this.imageHeight = 84;
        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        Player player = Objects.requireNonNull(this.minecraft).player;
        if (player != null && player.getMainHandItem() != null) {
            tabletMeta = player.getMainHandItem().getDamageValue();
        }

        int tabletOffset = tabletMeta == 1 ? 84 : 0;
        int iconOffset = tabletMeta == 1 ? 16 : 0;
        float revealChance = tabletMeta == 1 ? 0.25F : 0.5F;

        graphics.blit(TEXTURE, leftPos, topPos, 0, tabletOffset, xSize, ySize);

        if (PedestalRecipes.recipeSets == null || PedestalRecipes.recipeSets.isEmpty()) {
            drawHiddenCells(graphics, iconOffset);
            return;
        }

        var recipeSet = PedestalRecipes.recipeSets.get(Math.abs(tabletMeta) % 2);

        if (player != null && player.getMainHandItem().hasTag()
                && Objects.requireNonNull(player.getMainHandItem().getTag()).contains("tabletSeed") && !recipeSet.isEmpty()) {
            Random rand = new Random(player.getMainHandItem().getTag().getLong("tabletSeed"));
            PedestalRecipe recipe = recipeSet.get(rand.nextInt(recipeSet.size()));

            // Иконки условий
            if (recipe.extra == PedestalRecipes.PedestalExtraCondition.FULL_MOON)
                graphics.blit(TEXTURE, leftPos + 120, topPos + 62, 142 + iconOffset, 32, 16, 16);
            if (recipe.extra == PedestalRecipes.PedestalExtraCondition.NEW_MOON)
                graphics.blit(TEXTURE, leftPos + 120, topPos + 62, 142 + iconOffset, 48, 16, 16);
            if (recipe.extra == PedestalRecipes.PedestalExtraCondition.SUN)
                graphics.blit(TEXTURE, leftPos + 120, topPos + 62, 142 + iconOffset, 64, 16, 16);

            // Сетка 3x3
            for (int l = 0; l < 3; l++) {
                for (int r = 0; r < 3; r++) {
                    int x = leftPos + 7 + r * 27;
                    int y = topPos + 7 + l * 27;
                    if (rand.nextFloat() > revealChance) {
                        graphics.blit(TEXTURE, x, y, 142 + iconOffset, 16, 16, 16);
                    } else {
                        AStack ingredient = recipe.input[r + l * 3];
                        if (ingredient == null) {
                            graphics.blit(TEXTURE, x, y, 142 + iconOffset, 0, 16, 16);
                            continue;
                        }
                        List<ItemStack> inputs = ingredient.extractForNEI();
                        ItemStack input = inputs.isEmpty() ? new ItemStack(ModItems.NOTHING.get())
                                : inputs.get((int) (Math.abs(System.currentTimeMillis() / 1000) % inputs.size()));
                        if (input != null) {
                            graphics.renderItem(input, x, y);
                            if (input.getCount() > 1) {
                                graphics.renderItemDecorations(this.font, input, x, y, String.valueOf(input.getCount()));
                            }
                        }
                    }
                }
            }

            // Результат
            if (recipe.output != null) {
                int outX = leftPos + xSize / 2 - 8;
                int outY = topPos - 20;
                graphics.renderItem(recipe.output, outX, outY);
                if (recipe.output.getCount() > 1) {
                    graphics.renderItemDecorations(this.font, recipe.output, outX, outY, String.valueOf(recipe.output.getCount()));
                }
                String label = recipe.output.getHoverName().getString();
                graphics.drawString(font, label, leftPos + (xSize - font.width(label)) / 2, topPos - 30, 0xffffff);
            }
        } else {
            drawHiddenCells(graphics, iconOffset);
        }
    }

    private void renderIngredientTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        // Проверяем, что рецепт загружен и есть
        if (PedestalRecipes.recipeSets == null || PedestalRecipes.recipeSets.isEmpty()) return;

        Player player = Objects.requireNonNull(this.minecraft).player;
        if (player == null) return;
        if (!player.getMainHandItem().hasTag() || !Objects.requireNonNull(player.getMainHandItem().getTag()).contains("tabletSeed")) return;

        var recipeSet = PedestalRecipes.recipeSets.get(Math.abs(tabletMeta) % 2);
        if (recipeSet == null || recipeSet.isEmpty()) return;

        Random rand = new Random(player.getMainHandItem().getTag().getLong("tabletSeed"));
        PedestalRecipe recipe = recipeSet.get(rand.nextInt(recipeSet.size()));

        float revealChance = tabletMeta == 1 ? 0.25F : 0.5F;

        for (int l = 0; l < 3; l++) {
            for (int r = 0; r < 3; r++) {
                int x = guiLeft + 7 + r * 27;
                int y = guiTop + 7 + l * 27;

                // Проверяем, что ячейка раскрыта
                if (rand.nextFloat() <= revealChance) {
                    AStack ingredient = recipe.input[r + l * 3];
                    if (ingredient == null) continue;

                    List<ItemStack> inputs = ingredient.extractForNEI();
                    if (inputs.isEmpty()) continue;

                    ItemStack input = inputs.get((int) (Math.abs(System.currentTimeMillis() / 1000) % inputs.size()));
                    if (input == null || input.isEmpty()) continue;

                    // Проверяем, находится ли мышь над этой ячейкой
                    if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                        graphics.renderTooltip(this.font, input.getDisplayName(), mouseX, mouseY);
                        break;
                    }
                }
            }
        }
    }

    protected void drawGuiContainerForegroundLayer(GuiGraphics graphics, int mouseX, int mouseY) {
        // Нет элементов
    }

    protected void drawGuiContainerBackgroundLayer(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Player player = Objects.requireNonNull(this.minecraft).player;
        if (player != null && player.getMainHandItem() != null) {
            tabletMeta = player.getMainHandItem().getDamageValue();
        }

        RenderSystem.setShaderTexture(0, TEXTURE);

        int tabletOffset = tabletMeta == 1 ? 84 : 0;
        int iconOffset = tabletMeta == 1 ? 16 : 0;
        float revealChance = tabletMeta == 1 ? 0.25F : 0.5F;

        graphics.blit(TEXTURE, guiLeft, guiTop, 0, tabletOffset, xSize, ySize);

        // Если рецепты ещё не загружены или пусты — просто рисуем скрытые ячейки
        if (PedestalRecipes.recipeSets == null || PedestalRecipes.recipeSets.isEmpty()) {
            drawHiddenCells(graphics, iconOffset);
            return;
        }

        var recipeSet = PedestalRecipes.recipeSets.get(Math.abs(tabletMeta) % 2);

        Objects.requireNonNull(player).getMainHandItem();
        if (player.getMainHandItem().hasTag() && Objects.requireNonNull(player.getMainHandItem().getTag()).contains("tabletSeed") && !recipeSet.isEmpty()) {
            Random rand = new Random(player.getMainHandItem().getTag().getLong("tabletSeed"));
            PedestalRecipe recipe = recipeSet.get(rand.nextInt(recipeSet.size()));

            // Иконки условий (луна, солнце)
            if (recipe.extra == PedestalRecipes.PedestalExtraCondition.FULL_MOON)
                graphics.blit(TEXTURE, guiLeft + 120, guiTop + 62, 142 + iconOffset, 32, 16, 16);
            if (recipe.extra == PedestalRecipes.PedestalExtraCondition.NEW_MOON)
                graphics.blit(TEXTURE, guiLeft + 120, guiTop + 62, 142 + iconOffset, 48, 16, 16);
            if (recipe.extra == PedestalRecipes.PedestalExtraCondition.SUN)
                graphics.blit(TEXTURE, guiLeft + 120, guiTop + 62, 142 + iconOffset, 64, 16, 16);

            // Рисуем сетку 3x3
            for (int l = 0; l < 3; l++) {
                for (int r = 0; r < 3; r++) {
                    int x = guiLeft + 7 + r * 27;
                    int y = guiTop + 7 + l * 27;
                    if (rand.nextFloat() > revealChance) {
                        graphics.blit(TEXTURE, x, y, 142 + iconOffset, 16, 16, 16); // скрыто
                    } else {
                        AStack ingredient = recipe.input[r + l * 3];

                        if (ingredient == null) {
                            graphics.blit(TEXTURE, x, y, 142 + iconOffset, 0, 16, 16);
                            continue;
                        }

                        List<ItemStack> inputs = ingredient.extractForNEI();
                        ItemStack input = inputs.size() <= 0 ? new ItemStack(ModItems.NOTHING.get()) : inputs.get((int) (Math.abs(System.currentTimeMillis() / 1000) % inputs.size()));

                        if (input != null) {
                            graphics.renderItem(input, x, y);
                            if (input.getCount() > 1) {
                                graphics.renderItemDecorations(this.font, input, x, y, String.valueOf(input.getCount()));
                            }
                        }
                    }
                }
            }

            // Рисуем результат (в центре сверху)
            if (recipe.output != null) {
                int outX = guiLeft + xSize / 2 - 8;
                int outY = guiTop - 20;
                graphics.renderItem(recipe.output, outX, outY);
                // Можно добавить overlay количества
                if (recipe.output.getCount() > 1) {
                    graphics.renderItemDecorations(this.font, recipe.output, outX, outY, String.valueOf(recipe.output.getCount()));
                }
                // Название предмета
                String label = recipe.output.getHoverName().getString();
                graphics.drawString(font, label, guiLeft + (xSize - font.width(label)) / 2, guiTop - 30, 0xffffff);
            }

        } else {
            drawHiddenCells(graphics, iconOffset);
        }
    }

    private void drawHiddenCells(GuiGraphics graphics, int iconOffset) {
        for (int l = 0; l < 3; l++) {
            for (int r = 0; r < 3; r++) {
                graphics.blit(TEXTURE, guiLeft + 7 + r * 27, guiTop + 7 + l * 27, 142 + iconOffset, 16, 16, 16);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == Objects.requireNonNull(this.minecraft).options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}