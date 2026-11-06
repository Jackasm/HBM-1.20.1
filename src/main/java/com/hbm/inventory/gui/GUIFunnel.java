package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerFunnel;
import com.hbm.network.PacketHandler;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.machine.TileEntityMachineFunnel;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIFunnel extends AbstractContainerScreen<ContainerFunnel> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_funnel.png");
    private final TileEntityMachineFunnel funnel;

    public GUIFunnel(ContainerFunnel container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.funnel = container.getFunnel();
        this.imageWidth = 176;
        this.imageHeight = 168;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Info about mode
        if (mouseX >= leftPos + 159 && mouseX <= leftPos + 169 &&
                mouseY >= topPos + 73 && mouseY <= topPos + 83) {
            String mode = switch (funnel.mode) {
                case TileEntityMachineFunnel.MODE_3x3 -> "3x3 only";
                case TileEntityMachineFunnel.MODE_2x2 -> "2x2 only";
                default -> "3x3 then 2x2";
            };
            graphics.renderTooltip(font, Component.literal("Mode: " + mode), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Проверяем клик по кнопке переключения режима
        if (button == 0 &&
                mouseX >= leftPos + 159 && mouseX <= leftPos + 169 &&
                mouseY >= topPos + 73 && mouseY <= topPos + 83) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            // Отправляем пакет на сервер для переключения режима
            CompoundTag data = new CompoundTag();
            data.putBoolean("toggle", true);
            PacketHandler.sendToServer(new NBTControlPacket(data, funnel.getBlockPos()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(funnel.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Mode indicator
        int modeOffset = funnel.mode * 10;
        graphics.blit(TEXTURE, leftPos + 159, topPos + 73, 176, modeOffset, 10, 10);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
    }
}