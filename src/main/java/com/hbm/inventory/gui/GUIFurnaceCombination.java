package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerFurnaceCombination;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.tileentity.machine.TileEntityFurnaceCombination;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIFurnaceCombination extends AbstractContainerScreen<ContainerFurnaceCombination> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_furnace_combination.png");
    private final TileEntityFurnaceCombination furnace;

    private int lastProgress = -1;
    private int lastHeat = -1;
    private int lastFluidFill = -1;
    private int lastFluidType = -1;

    public GUIFurnaceCombination(ContainerFurnaceCombination container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.furnace = container.getFurnace();
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    public void containerTick() {
        super.containerTick();

        // Проверяем изменения и принудительно обновляем GUI если нужно
        int currentProgress = furnace.progress;
        int currentHeat = furnace.heat;
        int currentFluidFill = furnace.tank.getFill();
        int currentFluidType = Fluids.getID(furnace.tank.getTankType());

        if (currentProgress != lastProgress || currentHeat != lastHeat ||
                currentFluidFill != lastFluidFill || currentFluidType != lastFluidType) {
            lastProgress = currentProgress;
            lastHeat = currentHeat;
            lastFluidFill = currentFluidFill;
            lastFluidType = currentFluidType;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Прогресс бар
        int p = furnace.progress * 38 / TileEntityFurnaceCombination.processTime;
        guiGraphics.blit(TEXTURE, leftPos + 45, topPos + 37, 176, 0, p, 5);

        // Тепло бар
        int h = furnace.heat * 37 / TileEntityFurnaceCombination.maxHeat;
        guiGraphics.blit(TEXTURE, leftPos + 45, topPos + 46, 176, 5, h, 5);

        // Отрисовка жидкости
        renderFluidTank(guiGraphics);
    }

    private void renderFluidTank(GuiGraphics guiGraphics) {
        int fill = furnace.tank.getFill();
        int capacity = furnace.tank.getMaxFill();
        FluidTypeHBM fluidType = furnace.tank.getTankType();

        if (fill > 0 && capacity > 0 && fluidType != Fluids.NONE.get()) {
            int fluidHeight = (int) (52 * ((float) fill / (float) capacity));

            ResourceLocation fluidTexture = fluidType.getTexture();

            if (fluidTexture != null) {
                int tankX = leftPos + 118;
                int tankY = topPos + 70 - fluidHeight;

                IClientFluidTypeExtensions fluidExtensions = IClientFluidTypeExtensions.of(fluidType);
                int color = fluidExtensions.getTintColor();
                if (color == -1) color = fluidType.getColor();

                float r = ((color >> 16) & 0xFF) / 255.0F;
                float g = ((color >> 8) & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;

                RenderSystem.setShaderColor(r, g, b, 1.0F);

                // Текстура жидкости 16x16, рисуем вертикально
                for (int i = 0; i < 16; i += 16) {
                    int height = fluidHeight;
                    if (height > 16) height = 16;

                    guiGraphics.blit(fluidTexture,
                            tankX + i, tankY + (fluidHeight - height),
                            0, 16 - height,
                            16, height,
                            16, 16);

                    fluidHeight -= height;
                    if (fluidHeight <= 0) break;
                }

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                // Fallback - цветной прямоугольник
                int color = fluidType.getColor();
                guiGraphics.fill(leftPos + 118, topPos + 70 - fluidHeight,
                        leftPos + 134, topPos + 70, color | 0xFF000000);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String title = Component.translatable("container.furnaceCombination").getString();
        guiGraphics.drawString(this.font, title, this.imageWidth / 2 - this.font.width(title) / 2, 6, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Тултип для бака с жидкостью
        if (isHovering(118, 18, 16, 52, mouseX, mouseY)) {
            int fill = furnace.tank.getFill();
            int capacity = furnace.tank.getMaxFill();
            FluidTypeHBM fluidType = furnace.tank.getTankType();

            List<Component> tooltip = new ArrayList<>();

            if (fluidType != Fluids.NONE.get()) {
                tooltip.add(Component.literal(fluidType.getLocalizedName()));
            } else {
                tooltip.add(Component.literal("Empty"));
            }

            tooltip.add(Component.literal("§7" + fill + "/" + capacity + "mB"));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }

        // Тултип для прогресс бара
        if (isHovering(44, 36, 39, 7, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            String text = String.format(Locale.US, "%,d", furnace.progress) + " / " +
                    String.format(Locale.US, "%,d", TileEntityFurnaceCombination.processTime) + "TU";
            tooltip.add(Component.literal(text));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }

        // Тултип для тепла
        if (isHovering(44, 45, 39, 7, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            String text = String.format(Locale.US, "%,d", furnace.heat) + " / " +
                    String.format(Locale.US, "%,d", TileEntityFurnaceCombination.maxHeat) + "TU";
            tooltip.add(Component.literal(text));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private boolean isHovering(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + width &&
                mouseY >= topPos + y && mouseY < topPos + y + height;
    }
}