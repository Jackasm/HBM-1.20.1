package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineRotaryFurnace;
import com.hbm.inventory.material.Mats;
import com.hbm.tileentity.machine.TileEntityMachineRotaryFurnace;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIMachineRotaryFurnace extends AbstractContainerScreen<ContainerMachineRotaryFurnace> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_rotary_furnace.png");
    private final TileEntityMachineRotaryFurnace furnace;

    public GUIMachineRotaryFurnace(ContainerMachineRotaryFurnace container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.furnace = container.getFurnace();
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Tank tooltips
        furnace.tanks[0].renderTankInfo(graphics, mouseX, mouseY, leftPos + 8, topPos + 36, 52, 16);
        furnace.tanks[1].renderTankInfo(graphics, mouseX, mouseY, leftPos + 134, topPos + 18, 16, 52);
        furnace.tanks[2].renderTankInfo(graphics, mouseX, mouseY, leftPos + 152, topPos + 18, 16, 52);

        // Fuel slot tooltip
        Slot fuelSlot = this.menu.slots.get(4);
        if (this.isHovering(fuelSlot.x, fuelSlot.y, 16, 16, mouseX, mouseY) && !fuelSlot.hasItem()) {
            List<Component> bonuses = TileEntityMachineRotaryFurnace.burnModule.getDesc();
            if (!bonuses.isEmpty()) {
                graphics.renderTooltip(font, bonuses, java.util.Optional.empty(), mouseX, mouseY);
            }
        }

        // Output tooltip
        if (furnace.output == null) {
            if (isHovering(98, 18, 16, 52, mouseX, mouseY)) {
                graphics.renderTooltip(font, Component.literal("Empty"), mouseX, mouseY);
            }
        } else {
            if (isHovering(98, 18, 16, 52, mouseX, mouseY)) {
                boolean shiftDown = GLFW.glfwGetKey(Objects.requireNonNull(minecraft).getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
                String text = Component.translatable(furnace.output.material.getUnlocalizedName()).getString() + ": "
                        + Mats.formatAmount(furnace.output.amount, shiftDown);
                graphics.renderTooltip(font, Component.literal(text), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(furnace.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, 8, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Progress bar
        int p = (int) Math.ceil(furnace.progress * 33);
        graphics.blit(TEXTURE, leftPos + 63, topPos + 30, 176, 0, p, 10);

        // Burn time
        if (furnace.maxBurnTime > 0) {
            int b = furnace.burnTime * 14 / furnace.maxBurnTime;
            graphics.blit(TEXTURE, leftPos + 26, topPos + 69 - b, 176, 24 - b, 14, b);
        }

        // Output material
        if (furnace.output != null) {
            int hex = furnace.output.material.moltenColor;
            int amount = furnace.output.amount * 52 / TileEntityMachineRotaryFurnace.maxOutput;
            Color color = new Color(hex);
            graphics.setColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1F);
            graphics.blit(TEXTURE, leftPos + 98, topPos + 70 - amount, 176, 76 - amount, 16, amount);
            graphics.setColor(1F, 1F, 1F, 0.3F);
            graphics.blit(TEXTURE, leftPos + 98, topPos + 70 - amount, 176, 76 - amount, 16, amount);
            graphics.setColor(1F, 1F, 1F, 1F);
        }

        // Render tanks
        furnace.tanks[0].renderTank(graphics, leftPos + 8, topPos + 52, 52, 16, 1);
        furnace.tanks[1].renderTank(graphics, leftPos + 134, topPos + 70, 16, 52);
        furnace.tanks[2].renderTank(graphics, leftPos + 152, topPos + 70, 16, 52);
    }

    private boolean isHovering(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + width && mouseY >= topPos + y && mouseY < topPos + y + height;
    }
}