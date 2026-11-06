package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineKeyForge;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIMachineKeyForge extends AbstractContainerScreen<ContainerMachineKeyForge> {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/machine/gui_keyforge.png");

    public GUIMachineKeyForge(ContainerMachineKeyForge menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Инфо-панели (в оригинале — drawInfoPanel)
        // В 1.20.1 проще сделать свои иконки или пропустить
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = this.title.getString();
        graphics.drawString(this.font, name,
                this.imageWidth / 2 - this.font.width(name) / 2, 6, 0xFFFFFF, false);
        graphics.drawString(this.font, Component.translatable("container.inventory"),
                8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        // Тултипы для инфо-панелей
        if (isHovering(12, 28, 16, 16, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    Component.translatable("desc.gui.keyforge.key"), mouseX, mouseY);
        }
        if (isHovering(12, 44, 16, 16, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    Component.translatable("desc.gui.keyforge.random"), mouseX, mouseY);
        }

        this.renderTooltip(graphics, mouseX, mouseY);
    }
}