package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineRadarNT;

import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.machine.TileEntityMachineRadarNT;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIMachineRadarNTSlots extends AbstractContainerScreen<ContainerMachineRadarNT> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_radar_link.png");
    private final TileEntityMachineRadarNT radar;

    public GUIMachineRadarNTSlots(ContainerMachineRadarNT container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.radar = container.getRadar();
        this.imageWidth = 176;
        this.imageHeight = 184;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        if (checkClick(mouseX, mouseY, 5, 5, 8, 8)) {
            graphics.renderTooltip(this.font, Component.translatable("radar.toggleGui"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(this.radar.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, this.imageWidth / 2 - font.width(name) / 2, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (radar.getPower() > 0) {
            int i = (int) (radar.getPower() * 160 / radar.getMaxPower());
            graphics.blit(TEXTURE, leftPos + 8, topPos + 64, 0, 185, i, 16);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (checkClick(mouseX, mouseY, 5, 5, 8, 8)) {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F)
            );
            // Закрываем контейнер и открываем главное GUI радара
            Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).closeContainer();
            CompoundTag data = new CompoundTag();
            data.putBoolean("gui0", true);
            PacketDispatcher.sendToServer(new NBTControlPacket(data, radar.getBlockPos()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean checkClick(double x, double y, int left, int top, int sizeX, int sizeY) {
        return leftPos + left <= x && leftPos + left + sizeX > x && topPos + top < y && topPos + top + sizeY >= y;
    }
}