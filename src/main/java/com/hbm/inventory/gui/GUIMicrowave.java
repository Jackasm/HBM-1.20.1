package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMicrowave;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.AuxButtonPacket;
import com.hbm.tileentity.machine.TileEntityMicrowave;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIMicrowave extends AbstractContainerScreen<ContainerMicrowave> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_microwave.png");
    private final TileEntityMicrowave microwave;

    public GUIMicrowave(ContainerMicrowave container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.microwave = container.getMicrowave();
        this.imageWidth = 176;
        this.imageHeight = 168;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Тултип энергии
        if (isHovering(8, 17, 16, 34, mouseX, mouseY)) {
            graphics.renderTooltip(font,
                    Component.literal(microwave.getPower() + " / " + microwave.getMaxPower() + " HE"),
                    mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) microwave.getPowerScaled(34);
        graphics.blit(TEXTURE, leftPos + 8, topPos + 51 - power, 176, 34 - power, 16, power);

        int progress = Math.min(microwave.getProgressScaled(23), 22);
        graphics.blit(TEXTURE, leftPos + 104, topPos + 34, 192, 0, progress, 16);

        int speed = microwave.getSpeedScaled(34);
        graphics.blit(TEXTURE, leftPos + 62, topPos + 60 - speed, 214, 34 - speed, 4, speed);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable("container.microwave").getString();
        graphics.drawString(font, name, this.imageWidth / 2 - font.width(name) / 2, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Кнопки скорости
        if (isHovering(43, 25, 18, 18, mouseX, mouseY)) {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            PacketDispatcher.sendToServer(new AuxButtonPacket(microwave.getBlockPos(), 0, 0));
            return true;
        }
        if (isHovering(43, 43, 18, 18, mouseX, mouseY)) {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            PacketDispatcher.sendToServer(new AuxButtonPacket(microwave.getBlockPos(), 1, 0));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w &&
                mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}