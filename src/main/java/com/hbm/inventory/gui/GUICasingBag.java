package com.hbm.inventory.gui;

import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.hbm.inventory.container.ContainerCasingBag;
import com.hbm.items.tool.ItemCasingBag.InventoryCasingBag;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GUICasingBag extends AbstractContainerScreen<ContainerCasingBag> {

    private static final ResourceLocation TEXTURE = ResLocation.ResLocation(RefStrings.MODID, "textures/gui/gui_casing_bag.png");

    public GUICasingBag(ContainerCasingBag container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    // Старый конструктор для обратной совместимости
    public GUICasingBag(Inventory playerInv, InventoryCasingBag bag) {
        this(new ContainerCasingBag(playerInv, bag), playerInv,
                Component.translatable("container.casingBag")); // Используем стандартный заголовок
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Используем заголовок из конструктора
        guiGraphics.drawString(this.font, this.title,
                this.imageWidth / 2 - this.font.width(this.title) / 2,
                6, 0xffffff, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
                8, this.imageHeight - 98, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int leftPos = this.leftPos;
        int topPos = this.topPos;

        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}