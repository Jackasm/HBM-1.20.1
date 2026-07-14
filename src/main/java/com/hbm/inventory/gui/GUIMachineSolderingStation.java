package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineSolderingStation;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.machine.TileEntityMachineSolderingStation;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
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
public class GUIMachineSolderingStation extends AbstractContainerScreen<ContainerMachineSolderingStation> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_soldering_station.png");
    private final TileEntityMachineSolderingStation solderer;

    public GUIMachineSolderingStation(ContainerMachineSolderingStation container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.solderer = container.getSolderingStation();
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        solderer.tank.renderTankInfo(graphics, mouseX, mouseY, leftPos + 35, topPos + 63, 34, 16);

        if (isHovering(152, 18, 16, 52, mouseX, mouseY)) {
            String text = String.format("%,d / %,d HE", solderer.getPower(), solderer.getMaxPower());
            graphics.renderTooltip(font, Component.literal(text), mouseX, mouseY);
        }

        if (isHovering(5, 66, 10, 10, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Recipe Collision Prevention: " + (solderer.collisionPrevention ?
                    ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF")));
            tooltip.add(Component.literal("Prevents no-fluid recipes from being processed"));
            tooltip.add(Component.literal("when fluid is present."));
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(solderer.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2 - 18, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Collision prevention indicator
        if (solderer.collisionPrevention) {
            graphics.blit(TEXTURE, leftPos + 5, topPos + 66, 192, 14, 10, 10);
        }

        // Power gauge
        int power = (int) (solderer.getPower() * 52 / Math.max(solderer.getMaxPower(), 1));
        if (power > 0) {
            graphics.blit(TEXTURE, leftPos + 152, topPos + 70 - power, 176, 52 - power, 16, power);
        }

        // Progress bar
        int progress = solderer.progress * 33 / Math.max(solderer.processTime, 1);
        if (progress > 0) {
            graphics.blit(TEXTURE, leftPos + 72, topPos + 28, 192, 0, progress, 14);
        }

        // Power indicator (has enough power)
        if (solderer.getPower() >= solderer.consumption) {
            graphics.blit(TEXTURE, leftPos + 156, topPos + 4, 176, 52, 9, 12);
        }

        // Upgrade info panel button
        graphics.blit(TEXTURE, leftPos + 78, topPos + 67, 176, 64, 8, 8);

        // Tank
        solderer.tank.renderTank(graphics, leftPos + 35, topPos + 79, 34, 16, 1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Collision prevention toggle
        if (isHovering(5, 66, 10, 10, mouseX, mouseY)) {
            CompoundTag data = new CompoundTag();
            data.putBoolean("collision", true);
            PacketDispatcher.sendToServer(new NBTControlPacket(data, solderer.getBlockPos()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w && mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}