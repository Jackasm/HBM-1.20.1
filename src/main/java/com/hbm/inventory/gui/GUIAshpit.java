package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerAshpit;

import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIAshpit extends AbstractContainerScreen<ContainerAshpit> {

    private final ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_ashpit.png");

    public GUIAshpit(ContainerAshpit container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.imageWidth = 176;
        this.imageHeight = 168;
    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String name = Component.translatable("container.ashpit").getString();
        guiGraphics.drawString(this.font, name, this.imageWidth / 2 - this.font.width(name) / 2, 6, 4210752, false);
        guiGraphics.drawString(this.font, I18n.get("container.inventory"), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // В 1.20.1 setColor для GuiGraphics не нужен, текстура сама имеет цвет
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}