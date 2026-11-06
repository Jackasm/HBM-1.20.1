package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerNukeMan;

import com.hbm.tileentity.bomb.TileEntityNukeMan;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class GUINukeMan extends AbstractContainerScreen<ContainerNukeMan> {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/weapon/fatManSchematic.png");

    private final TileEntityNukeMan nukeMan;

    public GUINukeMan(ContainerNukeMan menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        // Получаем TE через меню (если у тебя есть геттер) или через level
        this.nukeMan = menu.getNukeMan();
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (nukeMan.exp1()) graphics.blit(TEXTURE, leftPos + 82, topPos + 19, 176, 0, 24, 24);
        if (nukeMan.exp2()) graphics.blit(TEXTURE, leftPos + 106, topPos + 19, 200, 0, 24, 24);
        if (nukeMan.exp3()) graphics.blit(TEXTURE, leftPos + 82, topPos + 43, 176, 24, 24, 24);
        if (nukeMan.exp4()) graphics.blit(TEXTURE, leftPos + 106, topPos + 43, 200, 24, 24, 24);
        if (nukeMan.isReady()) graphics.blit(TEXTURE, leftPos + 134, topPos + 35, 176, 48, 16, 16);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = this.title.getString();
        graphics.drawString(this.font, name,
                this.imageWidth / 2 - this.font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(this.font, Component.translatable("container.inventory"),
                8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}