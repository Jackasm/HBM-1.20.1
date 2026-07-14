package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerBlastFurnace;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIBlastFurnace extends AbstractContainerScreen<ContainerBlastFurnace> {
    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_blast_furnace.png");

    public GUIBlastFurnace(ContainerBlastFurnace container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        // Фон GUI
        graphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);

        // Прогресс плавки
        int progress = menu.getProgress();
        int progressScaled = progress * 24 / menu.getProcessingSpeed();
        if(progressScaled > 0) {
            graphics.blit(TEXTURE, left + 101, top + 35, 176, 14, progressScaled + 1, 17);
        }

        // Уровень топлива - ИСПРАВЛЕНО: используем правильные координаты
        int fuel = menu.getFuel();
        int fuelScaled = fuel * 52 / menu.getMaxFuel();
        if(fuelScaled > 0) {
            graphics.blit(TEXTURE, left + 44, top + 70 - fuelScaled, 201, 53 - fuelScaled, 16, fuelScaled);
        }

        // Анимация огня если идет процесс
        if(menu.getProgress() > 0) {
            graphics.blit(TEXTURE, left + 63, top + 37, 176, 0, 14, 14);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}