package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineGasCent;
import com.hbm.tileentity.machine.TileEntityMachineGasCent;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIMachineGasCent extends AbstractContainerScreen<ContainerMachineGasCent> implements IGuiInfoContainer {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_centrifuge_gas.png");
    private final TileEntityMachineGasCent centrifuge;

    public GUIMachineGasCent(ContainerMachineGasCent container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.centrifuge = container.centrifuge;
        this.imageWidth = 206;
        this.imageHeight = 204;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        String[] inTankInfo = new String[] {
                centrifuge.inputTank.getTankType().getName(),
                centrifuge.inputTank.getFill() + " / " + centrifuge.inputTank.getMaxFill() + " mB"
        };
        String[] outTankInfo = new String[] {
                centrifuge.outputTank.getTankType().getName(),
                centrifuge.outputTank.getFill() + " / " + centrifuge.outputTank.getMaxFill() + " mB"
        };

        this.drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 15, topPos + 15, 24, 55, inTankInfo);
        this.drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 137, topPos + 15, 25, 55, outTankInfo);
        this.drawElectricityTooltip(graphics, mouseX, mouseY);

        String[] enrichmentText = Component.translatable("desc.gui.gasCent.enrichment").getString().split("\n");
        this.drawCustomInfoStat(graphics, mouseX, mouseY, leftPos - 12, topPos + 16, 16, 16, enrichmentText);

        String[] transferText = Component.translatable("desc.gui.gasCent.output").getString().split("\n");
        this.drawCustomInfoStat(graphics, mouseX, mouseY, leftPos - 12, topPos + 32, 16, 16, transferText);
    }

    public void drawCustomInfoStat(GuiGraphics graphics, int mx, int my, int x, int y, int w, int h, String[] text) {
        if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
            List<Component> list = new ArrayList<>();
            for (String s : text) list.add(Component.literal(s));
            graphics.renderTooltip(font, list, java.util.Optional.empty(), mx, my);
        }
    }

    private void drawElectricityTooltip(GuiGraphics graphics, int mx, int my) {
        int x = leftPos + 182, y = topPos + 17, w = 16, h = 52;
        if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
            List<Component> list = List.of(
                    Component.literal(centrifuge.getPower() + " / " + centrifuge.getMaxPower() + " HE")
            );
            graphics.renderTooltip(font, list, java.util.Optional.empty(), mx, my);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // энергия
        int powerScaled = (int) (centrifuge.getPower() * 52 / centrifuge.getMaxPower());
        graphics.blit(TEXTURE, leftPos + 182, topPos + 69 - powerScaled, 206, 52 - powerScaled, 16, powerScaled);

        // прогресс
        int progressScaled = centrifuge.progress * 36 / centrifuge.getProcessingSpeed();
        graphics.blit(TEXTURE, leftPos + 70, topPos + 35, 206, 52, progressScaled, 13);

        // рендер входных баков (две полоски с текстурой жидкости)
        this.renderTank(graphics, leftPos + 16, topPos + 16, 6, 52, centrifuge.inputTank.getFill(), centrifuge.inputTank.getMaxFill());
        this.renderTank(graphics, leftPos + 32, topPos + 16, 6, 52, centrifuge.inputTank.getFill(), centrifuge.inputTank.getMaxFill());

        // рендер выходных баков (две полоски с текстурой жидкости)
        this.renderTank(graphics, leftPos + 138, topPos + 16, 6, 52, centrifuge.outputTank.getFill(), centrifuge.outputTank.getMaxFill());
        this.renderTank(graphics, leftPos + 154, topPos + 16, 6, 52, centrifuge.outputTank.getFill(), centrifuge.outputTank.getMaxFill());

        drawInfoPanel(graphics, leftPos - 12, topPos + 16, 16, 16, 3); // Large green I
        drawInfoPanel(graphics, leftPos - 12, topPos + 32, 16, 16, 2); // Large blue I
    }

    private void renderTank(GuiGraphics graphics, int x, int y, int width, int height, int fluid, int maxFluid) {
        if (fluid <= 0 || maxFluid <= 0) return;

        int fillHeight = fluid * height / maxFluid;
        if (fillHeight <= 0) return;

        ResourceLocation texture = centrifuge.tank.getTankType().getTexture();
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Координаты отрисовки (нижняя часть бака заполняется)
        int startY = y + height - fillHeight;

        // UV-координаты: текстура 16x16, растягиваем на ширину width и высоту fillHeight
        float minU = 0;
        float maxU = (float) width / 16.0f;
        float minV = 1.0f - (float) fillHeight / 16.0f;
        float maxV = 1.0f;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        // Нижний левый угол
        buffer.vertex(x, startY + fillHeight, 0).uv(minU, maxV).endVertex();
        // Нижний правый угол
        buffer.vertex(x + width, startY + fillHeight, 0).uv(maxU, maxV).endVertex();
        // Верхний правый угол
        buffer.vertex(x + width, startY, 0).uv(maxU, minV).endVertex();
        // Верхний левый угол
        buffer.vertex(x, startY, 0).uv(minU, minV).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
    }

}