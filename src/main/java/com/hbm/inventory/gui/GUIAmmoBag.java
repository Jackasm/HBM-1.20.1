package com.hbm.inventory.gui;

import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.hbm.inventory.container.ContainerAmmoBag;
import com.hbm.items.tool.ItemAmmoBag.InventoryAmmoBag;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GUIAmmoBag extends AbstractContainerScreen<ContainerAmmoBag> {

    private static final ResourceLocation TEXTURE = ResLocation.ResLocation(RefStrings.MODID, "textures/gui/gui_ammo_bag.png");

    public GUIAmmoBag(ContainerAmmoBag container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 168;
    }

    // Старый конструктор для обратной совместимости
    public GUIAmmoBag(Inventory playerInv, InventoryAmmoBag bag) {
        this(new ContainerAmmoBag(playerInv, bag), playerInv,
                Component.translatable("container.ammoBag"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
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