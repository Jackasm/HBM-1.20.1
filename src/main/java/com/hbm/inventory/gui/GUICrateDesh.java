package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerCrateDesh;
import com.hbm.tileentity.storage.TileEntityCrateDesh;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class GUICrateDesh extends AbstractContainerScreen<ContainerCrateDesh> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/storage/gui_crate_desh.png");
    private final TileEntityCrateDesh crate;

    public GUICrateDesh(ContainerCrateDesh container, Inventory inv, Component title) {
        super(container, inv, title);
        this.crate = container.crate;
        this.imageWidth = 248; // 266 (13 колонок * 18 + отступы)
        this.imageHeight = 256; // 84 + 8*18 + 30
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, imageWidth / 2 - font.width(title) / 2 + 26, imageHeight - 92, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}