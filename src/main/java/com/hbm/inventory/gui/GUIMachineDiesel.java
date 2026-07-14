package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineDiesel;
import com.hbm.tileentity.machine.TileEntityMachineDiesel;
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
public class GUIMachineDiesel extends AbstractContainerScreen<ContainerMachineDiesel> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_diesel.png");
    private final TileEntityMachineDiesel diesel;

    public GUIMachineDiesel(ContainerMachineDiesel container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.diesel = container.getDiesel();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Tank info
        diesel.tank.renderTankInfo(graphics, mouseX, mouseY, leftPos + 80, topPos + 17, 16, 52);

        // Energy info
        drawElectricityInfo(graphics, mouseX, mouseY, leftPos + 152, topPos + 17, 16, 52, diesel.power, diesel.powerCap);

        // Info button
        String[] text = {
                "Fuel consumption rate:",
                "  1 mB/t",
                "  20 mB/s",
                "(Consumption rate is constant)"
        };
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos - 16, topPos + 36, 16, 16, text);

        if (!diesel.hasAcceptableFuel()) {
            String[] text2 = {
                    "Error: The currently set fuel type",
                    "is not supported by this engine!"
            };
            drawCustomInfoStat(graphics, mouseX, mouseY, leftPos - 16, topPos + 36 + 32, 16, 16, text2);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(diesel.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Energy bar
        if (diesel.power > 0) {
            int i = (int) diesel.getPowerScaled(52);
            graphics.blit(TEXTURE, leftPos + 152, topPos + 69 - i, 176, 52 - i, 16, i);
        }

        // Active indicator
        if (diesel.tank.getFill() > 0 && diesel.hasAcceptableFuel()) {
            graphics.blit(TEXTURE, leftPos + 43 + 18 * 4, topPos + 34, 208, 0, 18, 18);
        }

        // Info panel icons
        drawInfoPanel(graphics, leftPos - 16, topPos + 36, 16, 16, 2);
        if (!diesel.hasAcceptableFuel()) {
            drawInfoPanel(graphics, leftPos - 16, topPos + 36 + 32, 16, 16, 6);
        }

        // Render tank
        diesel.tank.renderTank(graphics, leftPos + 80, topPos + 69, 16, 52);
    }

    private void drawCustomInfoStat(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int w, int h, String[] text) {
        if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            java.util.List<Component> list = new java.util.ArrayList<>();
            for (String s : text) list.add(Component.literal(s));
            graphics.renderTooltip(font, list, java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    private void drawInfoPanel(GuiGraphics graphics, int x, int y, int w, int h, int icon) {
        graphics.blit(TEXTURE, x, y, 176 + icon * 16, 52, w, h);
    }

    private void drawElectricityInfo(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int w, int h, long power, long maxPower) {
        if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            java.util.List<Component> list = java.util.List.of(
                    Component.literal(power + " / " + maxPower + " HE")
            );
            graphics.renderTooltip(font, list, java.util.Optional.empty(), mouseX, mouseY);
        }
    }
}