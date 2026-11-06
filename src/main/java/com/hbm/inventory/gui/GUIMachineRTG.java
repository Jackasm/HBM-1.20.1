package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineRTG;
import com.hbm.tileentity.machine.TileEntityMachineRTG;
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
public class GUIMachineRTG extends AbstractContainerScreen<ContainerMachineRTG> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_rtg.png");
    private final TileEntityMachineRTG rtg;

    public GUIMachineRTG(ContainerMachineRTG container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.rtg = container.getRtg();
        this.imageWidth = 176;
        this.imageHeight = 188;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (rtg.hasHeat()) {
            int i = rtg.getHeatScaled(51);
            graphics.blit(TEXTURE, leftPos + 124, topPos + 61 - i, 176, 10 + (51 - i), 16, i);
        }

        if (rtg.hasPower()) {
            int i = (int) rtg.getPowerScaled(51);
            graphics.blit(TEXTURE, leftPos + 146, topPos + 61 - i, 192, 10 + (51 - i), 16, i);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable("container.rtg").getString();
        graphics.drawString(font, name, 13, 7, 10925486, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 0x404040, false);
    }
}