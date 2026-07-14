package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerBook;
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
public class GUIBook extends AbstractContainerScreen<ContainerBook> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/processing/gui_book.png");

    public GUIBook(ContainerBook container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, "Extended 4-Slot Crafting", 28, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Если в первом слоте есть предмет, показываем подсветку области крафта
        if (this.menu.getSlot(0).hasItem()) {
            graphics.blit(TEXTURE, leftPos + 29, topPos + 16, 176, 0, 54, 54);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}