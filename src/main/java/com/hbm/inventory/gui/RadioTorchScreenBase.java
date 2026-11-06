package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerRadioTorch;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.network.TileEntityRadioTorchBase;
import com.hbm.tileentity.network.TileEntityRadioTorchSender;
import com.mojang.blaze3d.systems.RenderSystem;
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

import java.util.List;
import java.util.Optional;

public abstract class RadioTorchScreenBase extends AbstractContainerScreen<ContainerRadioTorch> {

    public static final int MAX_CHAN_LENGTH = 15;

    protected final TileEntityRadioTorchBase radio;

    protected EditBox frequency;
    protected final EditBox[] remap = new EditBox[16];

    public RadioTorchScreenBase(ContainerRadioTorch menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // У reader и controller свои TE, поэтому базовый факел может быть null:
        // такие экраны переопределяют init и не трогают это поле.
        this.radio = menu.getBE() instanceof TileEntityRadioTorchBase base ? base : null;
        this.imageWidth = 256;
        this.imageHeight = 204;
    }

    /** Текстура конкретного экрана. */
    protected abstract ResourceLocation getTexture();

    /** Смещение текстового поля частоты. */
    protected int getFrequencyOffset() {
        return 0;
    }

    /**
     * Дополнительный сдвиг полей маппинга. Нужен логическому факелу: в эталоне
     * слева от каждой строки стоит столбец условий шириной 18 px.
     */
    protected int getMappingOffset() {
        return 0;
    }

    /** Ширина поля маппинга: у логического она уже на столбец условий. */
    protected int getMappingWidth() {
        return 90 - 8;
    }

    /** Что экран дописывает в пакет «Save Settings» помимо канала и строк. */
    protected void addScreenData(CompoundTag data) {
    }

    @Override
    protected void init() {
        super.init();

        // Экраны reader и controller строят свои поля сами.
        if (this.radio == null) return;

        int oX = 4;
        int oY = 4;
        int in = getFrequencyOffset();
        int mp = getMappingOffset();

        this.frequency = new EditBox(this.font,
                leftPos + 25 + oX, topPos + 17 + oY, 90 - oX * 2, 14,
                Component.empty());
        this.frequency.setTextColor(0x00FF00);
        this.frequency.setTextColorUneditable(0x00FF00);
        this.frequency.setBordered(false);
        this.frequency.setMaxLength(MAX_CHAN_LENGTH);
        this.frequency.setValue(radio.channel == null ? "" : radio.channel);
        this.addRenderableWidget(this.frequency);

        for (int i = 0; i < 16; i++) {
            this.remap[i] = new EditBox(this.font,
                    leftPos + 7 + (130 * (i / 8)) + oX + in + mp, topPos + 53 + (18 * (i % 8)) + oY, getMappingWidth(), 14,
                    Component.empty());
            this.remap[i].setTextColor(0x00FF00);
            this.remap[i].setTextColorUneditable(0x00FF00);
            this.remap[i].setBordered(false);
            this.remap[i].setMaxLength(32);
            this.remap[i].setValue(radio.mapping[i] == null ? "" : radio.mapping[i]);
            this.addRenderableWidget(this.remap[i]);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (radio.customMap) {
            graphics.blit(getTexture(), leftPos + 137, topPos + 17, 0, 204, 18, 18);
            if (radio.polling) {
                graphics.blit(getTexture(), leftPos + 173, topPos + 17, 0, 222, 18, 18);
            }
            for (int j = 0; j < 16; j++) {
                this.remap[j].render(graphics, mouseX, mouseY, partialTick);
            }
        } else {
            // Перерисовываем верхнюю часть
            graphics.blit(getTexture(), leftPos, topPos, 0, 0, imageWidth, 35);
            graphics.blit(getTexture(), leftPos, topPos + 35, 0, 197, imageWidth, 7);
            if (radio.polling) {
                graphics.blit(getTexture(), leftPos + 173, topPos + 17, 0, 222, 18, 18);
            }
        }

        this.frequency.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = this.title.getString();
        graphics.drawString(this.font, name,
                this.imageWidth / 2 - this.font.width(name) / 2,
                6, 4210752, false);

        // Тултипы
        if (isHovering(137, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal(radio.customMap ? "Custom Mapping" : "Redstone Passthrough")),
                    Optional.empty(), mouseX, mouseY);
        }
        if (isHovering(173, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal(radio.polling ? "Polling" : "State Change")),
                    Optional.empty(), mouseX, mouseY);
        }
        if (isHovering(209, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal("Save Settings")),
                    Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);

        if (isHovering(137, 17, 18, 18, mouseX, mouseY)) {
            playClick();
            onLeftButton();
            return true;
        }

        if (isHovering(173, 17, 18, 18, mouseX, mouseY)) {
            playClick();
            onPollingButton();
            return true;
        }

        if (isHovering(209, 17, 18, 18, mouseX, mouseY)) {
            playClick();
            onSaveButton();
            return true;
        }

        return handled;
    }

    /** Левая кнопка: у sender/receiver — свой маппинг, у логики — порядок обхода. */
    protected void onLeftButton() {
        CompoundTag data = new CompoundTag();
        data.putBoolean("m", !radio.customMap);
        sendControl(data);
    }

    protected void onPollingButton() {
        CompoundTag data = new CompoundTag();
        data.putBoolean("p", !radio.polling);
        sendControl(data);
    }

    protected void onSaveButton() {
        CompoundTag data = new CompoundTag();
        data.putString("c", this.frequency.getValue());
        for (int j = 0; j < 16; j++) {
            data.putString("m" + j, this.remap[j].getValue());
        }
        addScreenData(data);
        sendControl(data);
    }

    protected void sendControl(CompoundTag data) {
        PacketDispatcher.sendToServer(new NBTControlPacket(data, radio.getBlockPos()));
    }

    protected void playClick() {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.frequency.isFocused() || anyRemapFocused()) {
            if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        if (keyCode == 256) { // ESC
            this.onClose();
            return true;
        }
        if (this.minecraft != null
                && keyCode == this.minecraft.options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected boolean anyRemapFocused() {
        for (EditBox box : remap) {
            if (box != null && box.isFocused()) return true;
        }
        return false;
    }

    public static RadioTorchScreenBase createRadioTorchScreen(
            ContainerRadioTorch menu, Inventory inv, Component title) {
        // Диспетчеризация по типу TE: у counter/reader/controller свои экраны.
        if (menu.getBE() instanceof TileEntityRadioTorchSender) {
            return new RadioTorchSenderScreen(menu, inv, title);
        }
        if (menu.getBE() instanceof com.hbm.tileentity.network.TileEntityRadioTorchLogic) {
            return new RadioTorchLogicScreen(menu, inv, title);
        }
        if (menu.getBE() instanceof com.hbm.tileentity.network.TileEntityRadioTorchReader) {
            return new RadioTorchReaderScreen(menu, inv, title);
        }
        if (menu.getBE() instanceof com.hbm.tileentity.network.TileEntityRadioTorchController) {
            return new RadioTorchControllerScreen(menu, inv, title);
        }
        return new RadioTorchReceiverScreen(menu, inv, title);
    }

}