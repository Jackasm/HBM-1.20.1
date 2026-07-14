package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerRadioRec;
import com.hbm.network.PacketHandler;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.machine.TileEntityRadioRec;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIRadioRec extends AbstractContainerScreen<ContainerRadioRec> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_radio.png");
    protected TileEntityRadioRec radio;
    protected int xSize = 220;
    protected int ySize = 42;
    protected int guiLeft;
    protected int guiTop;
    protected EditBox frequency;

    public GUIRadioRec(ContainerRadioRec container, Inventory inv, Component title) {
        super(container, inv, title);
        this.radio = container.getRadio();
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        int oX = 4;
        int oY = 4;

        this.frequency = new EditBox(this.font, guiLeft + 25 + oX, guiTop + 17 + oY, 90 - oX * 2, 14, Component.empty());
        this.frequency.setTextColor(0x00ff00);
        this.frequency.setValue(radio.channel == null ? "" : radio.channel);
        this.frequency.setMaxLength(10);
        this.addRenderableWidget(this.frequency);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        this.drawGuiContainerForegroundLayer(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, guiLeft, guiTop, 0, 0, xSize, ySize);

        if (this.radio.isOn) {
            graphics.blit(TEXTURE, guiLeft + 173, guiTop + 17, 0, 42, 18, 18);
        }
    }

    private void drawGuiContainerForegroundLayer(GuiGraphics graphics, int x, int y) {
        String name = Component.translatable("container.radio").getString();
        graphics.drawString(this.font, name, guiLeft + this.xSize / 2 - this.font.width(name) / 2, guiTop + 6, 4210752, false);

        if (guiLeft + 137 <= x && guiLeft + 137 + 18 > x && guiTop + 17 < y && guiTop + 17 + 18 >= y) {
            graphics.renderComponentTooltip(this.font, List.of(Component.literal("Save Settings")), x, y);
        }
        if (guiLeft + 173 <= x && guiLeft + 173 + 18 > x && guiTop + 17 < y && guiTop + 17 + 18 >= y) {
            graphics.renderComponentTooltip(this.font, List.of(Component.literal("Toggle")), x, y);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (guiLeft + 137 <= x && guiLeft + 137 + 18 > x && guiTop + 17 < y && guiTop + 17 + 18 >= y) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            CompoundTag data = new CompoundTag();
            data.putString("channel", this.frequency.getValue());
            PacketHandler.sendToServer(new NBTControlPacket(data, radio.getBlockPos()));
            return true;
        }

        if (guiLeft + 173 <= x && guiLeft + 173 + 18 > x && guiTop + 17 < y && guiTop + 17 + 18 >= y) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            CompoundTag data = new CompoundTag();
            data.putBoolean("isOn", !radio.isOn);
            PacketHandler.sendToServer(new NBTControlPacket(data, radio.getBlockPos()));
            return true;
        }

        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.frequency.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == this.minecraft.options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}