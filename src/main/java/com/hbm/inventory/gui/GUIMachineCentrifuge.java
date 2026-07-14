package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerCentrifuge;
import com.hbm.tileentity.machine.TileEntityMachineCentrifuge;
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
public class GUIMachineCentrifuge extends AbstractContainerScreen<ContainerCentrifuge> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_centrifuge.png");
    private final TileEntityMachineCentrifuge centrifuge;

    public GUIMachineCentrifuge(ContainerCentrifuge container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.centrifuge = container.centrifuge;
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        drawElectricityInfo(graphics, mouseX, mouseY, leftPos + 9, topPos + 13, 16, 34, centrifuge.power, TileEntityMachineCentrifuge.maxPower);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Energy bar
        if (centrifuge.hasPower()) {
            int i1 = (int) centrifuge.getPowerRemainingScaled(35);
            graphics.blit(TEXTURE, leftPos + 9, topPos + 48 - i1, 176, 35 - i1, 16, i1);
        }

        // Progress arrow
        if (centrifuge.isProcessing()) {
            int p = centrifuge.getCentrifugeProgressScaled(145);
            for (int i = 0; i < 4; i++) {
                int h = Math.min(p, 36);
                graphics.blit(TEXTURE, leftPos + 65 + i * 20, topPos + 50 - h, 176, 71 - h, 12, h);
                p -= h;
                if (p <= 0) break;
            }
        }
    }

    private void drawElectricityInfo(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int w, int h, long power, long maxPower) {
        if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            graphics.renderTooltip(font, Component.literal(power + " / " + maxPower + " HE"), mouseX, mouseY);
        }
    }
}