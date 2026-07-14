package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerCrucible;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.material.NTMMaterial.SmeltingBehavior;
import com.hbm.tileentity.machine.TileEntityCrucible;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUICrucible extends AbstractContainerScreen<ContainerCrucible> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_crucible.png");
    private final TileEntityCrucible crucible;

    public GUICrucible(ContainerCrucible container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.crucible = container.getCrucible();
        this.imageWidth = 176;
        this.imageHeight = 214;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Waste stack tooltip
        drawStackInfo(graphics, crucible.wasteStack, mouseX, mouseY, 16, 17);
        // Recipe stack tooltip
        drawStackInfo(graphics, crucible.recipeStack, mouseX, mouseY, 61, 17);

        // Progress tooltip
        if (isHovering(125, 81, 34, 7, mouseX, mouseY)) {
            String text = String.format(Locale.US, "%,d", crucible.progress) + " / " + String.format(Locale.US, "%,d", TileEntityCrucible.processTime) + "TU";
            graphics.renderTooltip(font, Component.literal(text), mouseX, mouseY);
        }

        // Heat tooltip
        if (isHovering(125, 90, 34, 7, mouseX, mouseY)) {
            String text = String.format(Locale.US, "%,d", crucible.heat) + " / " + String.format(Locale.US, "%,d", TileEntityCrucible.maxHeat) + "TU";
            graphics.renderTooltip(font, Component.literal(text), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(crucible.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 0xffffff, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Progress gauge
        int pGauge = crucible.progress * 33 / TileEntityCrucible.processTime;
        if (pGauge > 0) graphics.blit(TEXTURE, leftPos + 126, topPos + 82, 176, 0, pGauge, 5);

        // Heat gauge
        int hGauge = crucible.heat * 33 / TileEntityCrucible.maxHeat;
        if (hGauge > 0) graphics.blit(TEXTURE, leftPos + 126, topPos + 91, 176, 5, hGauge, 5);

        // Recipe stack
        if (!crucible.recipeStack.isEmpty()) drawStack(graphics, crucible.recipeStack, TileEntityCrucible.recipeZCapacity, 62, 97);
        // Waste stack
        if (!crucible.wasteStack.isEmpty()) drawStack(graphics, crucible.wasteStack, TileEntityCrucible.wasteZCapacity, 17, 97);
    }

    private void drawStackInfo(GuiGraphics graphics, List<MaterialStack> stack, int mouseX, int mouseY, int x, int y) {
        List<Component> list = new ArrayList<>();
        boolean shiftDown = GLFW.glfwGetKey(Objects.requireNonNull(minecraft).getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;

        if (stack.isEmpty()) {
            list.add(Component.literal("Empty").withStyle(net.minecraft.ChatFormatting.RED));
        }

        List<MaterialStack> reversed = new ArrayList<>(stack);
        Collections.reverse(reversed);
        for (MaterialStack sta : reversed) {
            list.add(Component.literal(
                    Component.translatable(sta.material.getUnlocalizedName()).getString() + ": " + Mats.formatAmount(sta.amount, shiftDown)
            ).withStyle(net.minecraft.ChatFormatting.YELLOW));
        }

        if (isHovering(x, y, 36, 81, mouseX, mouseY)) {
            graphics.renderTooltip(font, list, java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    private void drawStack(GuiGraphics graphics, List<MaterialStack> stack, int capacity, int x, int y) {
        if (stack.isEmpty()) return;

        int lastHeight = 0;
        int lastQuant = 0;

        for (MaterialStack sta : stack) {
            int targetHeight = (lastQuant + sta.amount) * 79 / capacity;
            if (lastHeight == targetHeight) continue;

            int offset = sta.material.smeltable == SmeltingBehavior.ADDITIVE ? 34 : 0;
            int hex = sta.material.moltenColor;
            Color color = new Color(hex);

            // Solid color
            graphics.setColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1F);
            graphics.blit(TEXTURE, leftPos + x, topPos + y - targetHeight, 176 + offset, 89 - targetHeight, 34, targetHeight - lastHeight);
            // Overlay
            graphics.setColor(1F, 1F, 1F, 0.3F);
            graphics.blit(TEXTURE, leftPos + x, topPos + y - targetHeight, 176 + offset, 89 - targetHeight, 34, targetHeight - lastHeight);
            graphics.setColor(1F, 1F, 1F, 1F);

            lastQuant += sta.amount;
            lastHeight = targetHeight;
        }
    }

    private boolean isHovering(int x, int y, int w, int h, int mouseX, int mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w && mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}