package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerSatDock;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUISatDock extends AbstractContainerScreen<ContainerSatDock> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_dock.png");

    public GUISatDock(ContainerSatDock container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);

        this.imageWidth = 176;
        this.imageHeight = 168;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Рисуем информационную панель
        if (mouseX >= this.leftPos - 16 && mouseX < this.leftPos && mouseY >= this.topPos + 36 && mouseY < this.topPos + 52) {
            guiGraphics.renderTooltip(font,
                    Component.literal("Requires linked miner sat chip."),
                    mouseX, mouseY
            );
            guiGraphics.renderTooltip(font,
                    Component.literal("Cargo ship will land periodically to"),
                    mouseX, mouseY + 10
            );
            guiGraphics.renderTooltip(font,
                    Component.literal("deliver resources."),
                    mouseX, mouseY + 20
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String name = this.title.getString();
        guiGraphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 0x404040, false);
        guiGraphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight);

        // Информационная панель (знак вопроса)
        guiGraphics.blit(TEXTURE, this.leftPos - 16, this.topPos+ 36, 176, 0, 16, 16);
    }
}