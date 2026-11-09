package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerElectricFurnace;
import com.hbm.tileentity.machine.TileEntityMachineElectricFurnace;
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
public class GUIMachineElectricFurnace extends AbstractContainerScreen<ContainerElectricFurnace> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_electric_furnace.png");
    private final TileEntityMachineElectricFurnace furnace;

    public GUIMachineElectricFurnace(ContainerElectricFurnace container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.furnace = container.getFurnace();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Energy info
        if (mouseX >= leftPos + 20 && mouseX <= leftPos + 36 &&
                mouseY >= topPos + 17 && mouseY <= topPos + 69) {
            graphics.renderTooltip(font, Component.literal(furnace.power + " / " + TileEntityMachineElectricFurnace.MAX_POWER + " HE"), mouseX, mouseY);
        }

        // Upgrade info
        if (mouseX >= leftPos + 151 && mouseX <= leftPos + 159 &&
                mouseY >= topPos + 19 && mouseY <= topPos + 27) {
            graphics.renderTooltip(font, Component.translatable("hbm.text.upgrade_slot"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(furnace.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Energy bar
        if (furnace.power > 0) {
            int i = (int) furnace.getPowerScaled(52);
            graphics.blit(TEXTURE, leftPos + 20, topPos + 69 - i, 200, 52 - i, 16, i);
        }

        // Active indicator (burning)
        if (furnace.isActive()) {
            graphics.blit(TEXTURE, leftPos + 56, topPos + 35, 176, 0, 16, 16);
        }

        // Progress arrow
        int progress = furnace.getProgressScaled(24);
        if (progress > 0) {
            graphics.blit(TEXTURE, leftPos + 79, topPos + 34, 176, 17, progress + 1, 17);
        }

        // Upgrade icon
        graphics.blit(TEXTURE, leftPos + 151, topPos + 19, 208, 0, 8, 8);
    }
}