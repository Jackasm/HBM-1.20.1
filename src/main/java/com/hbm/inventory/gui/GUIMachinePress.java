package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachinePress;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIMachinePress extends AbstractContainerScreen<ContainerMachinePress> {

    private static final ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_press.png");

    public GUIMachinePress(ContainerMachinePress container, Inventory inv, Component name) {
        super(container, inv, name);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int burnTime = menu.getMachineBurnTime();
        int speed = menu.getMachineSpeed();

        if(isHovering(25, 16, 18, 18, mouseX, mouseY)) {
            int speedPercent = (int)(speed * 100 / 400.0f);
            guiGraphics.renderTooltip(this.font, Component.literal("Speed: " + speedPercent + "%"), mouseX, mouseY);
        }

        if(isHovering(25, 34, 18, 18, mouseX, mouseY)) {
            int operationsLeft = burnTime / 200;
            guiGraphics.renderTooltip(this.font, Component.literal(operationsLeft + " operations left"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title.getString()) / 2, 6, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int burnTime = menu.getMachineBurnTime();
        int speed = menu.getMachineSpeed();
        int pressValue = menu.getMachinePress();

        if(burnTime > 0) {
            int burnProgress = getBurnLeftScaled(burnTime);
            guiGraphics.blit(texture, this.leftPos + 27, this.topPos + 36 + 14 - burnProgress, 176, 14 - burnProgress, 14, burnProgress);
        }

        int pressHeight = pressValue * 16 / 200;
        guiGraphics.blit(texture, this.leftPos + 79, this.topPos + 35, 194, 0, 18, pressHeight);

        // Индикатор скорости со стрелкой
        float speedProgress = speed / 400.0f;
        renderSpeedGauge(guiGraphics, this.leftPos + 25, this.topPos + 16, speedProgress);
        renderSpeedArrow(guiGraphics, this.leftPos + 25, this.topPos + 16, speedProgress);
    }

    private int getBurnLeftScaled(int burnTime) {
        int totalBurnTime = 1600;
        if(burnTime <= 0) return 0;
        return Math.min(14, Math.max(1, burnTime * 14 / totalBurnTime));
    }

    private void renderSpeedGauge(GuiGraphics guiGraphics, int x, int y, float progress) {
        int width = (int)(18 * progress);
        if(width > 0) {
            guiGraphics.blit(texture, x, y, 176, 14, width, 18);
        }
    }

    private void renderSpeedArrow(GuiGraphics guiGraphics, int x, int y, float progress) {
        // Центр индикатора
        int centerX = x + 9;
        int centerY = y + 9;

        // Длина стрелки
        int arrowLength = 7;

        // Угол стрелки
        double angle = (progress * 270.0) - 135.0;
        double angleRad = Math.toRadians(angle);

        // Конец стрелки
        int endX = centerX + (int)(arrowLength * Math.sin(angleRad));
        int endY = centerY - (int)(arrowLength * Math.cos(angleRad));

        // Рисуем простую красную линию от центра до конца
        drawLine(guiGraphics, centerX, centerY, endX, endY);
    }

    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int steps = Math.max(dx, dy);

        for (int i = 0; i <= steps; i++) {
            float t = (float)i / steps;
            int x = Math.round(x1 + t * (x2 - x1));
            int y = Math.round(y1 + t * (y2 - y1));
            guiGraphics.fill(x, y, x + 1, y + 1, 0xFFFF0000);
        }
    }
}