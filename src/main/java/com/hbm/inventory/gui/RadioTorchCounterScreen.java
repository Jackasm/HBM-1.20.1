package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerRadioTorchCounter;
import com.hbm.module.ModulePatternMatcher;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.network.TileEntityRadioTorchCounter;
import com.hbm.util.RefStrings;
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

import static com.hbm.util.ResLocation.ResLocation;

/**
 * Экран RTTY-счётчика: три канала, три слота-образца, кнопки опроса и записи.
 *
 * <p>Порт {@code ref:inventory/gui/GUICounterTorch.java}. Размер окна 218×238,
 * поля частот — по одному на канал, слоты образцов рисует сам контейнер.
 * Подсказка о режиме сравнения висит на слоте, как в эталоне.</p>
 */
public class RadioTorchCounterScreen extends AbstractContainerScreen<ContainerRadioTorchCounter> {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/machine/gui_rtty_counter.png");

    private static final int SLOT_X = 138;
    private static final int SLOT_Y = 18;
    private static final int ROW_STEP = 44;

    private final TileEntityRadioTorchCounter counter;
    private final EditBox[] frequency = new EditBox[3];

    public RadioTorchCounterScreen(ContainerRadioTorchCounter menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.counter = menu.getCounter();
        this.imageWidth = 218;
        this.imageHeight = 238;
    }

    @Override
    protected void init() {
        super.init();

        int oX = 4;
        int oY = 4;

        for (int i = 0; i < 3; i++) {
            this.frequency[i] = new EditBox(this.font,
                    leftPos + 29, topPos + 21 + ROW_STEP * i, 86, 14, Component.empty());
            this.frequency[i].setTextColor(0x00FF00);
            this.frequency[i].setTextColorUneditable(0x00FF00);
            this.frequency[i].setBordered(false);
            this.frequency[i].setMaxLength(10);
            this.frequency[i].setValue(counter.channel[i] == null ? "" : counter.channel[i]);
            this.addRenderableWidget(this.frequency[i]);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (counter.polling) {
            graphics.blit(TEXTURE, leftPos + 193, topPos + 8, 218, 0, 18, 18);
        }

        for (EditBox box : this.frequency) {
            box.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = this.title.getString();
        graphics.drawString(this.font, name, 184 / 2 - this.font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(this.font, Component.translatable("container.inventory"),
                16, this.imageHeight - 96 + 2, 4210752, false);

        if (isHovering(193, 8, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal(counter.polling ? "Polling" : "State Change")),
                    Optional.empty(), mouseX, mouseY);
        }

        if (isHovering(193, 30, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal("Save Settings")),
                    Optional.empty(), mouseX, mouseY);
        }

        // Подсказка режима образца — как в эталонном drawScreen.
        if (this.menu.getCarried().isEmpty()) {
            for (int i = 0; i < 3; i++) {
                if (isHovering(SLOT_X, SLOT_Y + ROW_STEP * i, 18, 18, mouseX, mouseY)) {
                    graphics.renderTooltip(this.font,
                            List.of(Component.literal("Правый клик — сменить режим"),
                                    Component.literal(ModulePatternMatcher.getLabel(counter.matcher.modes[i]))),
                            Optional.empty(), mouseX, mouseY);
                }
            }
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

        if (isHovering(193, 8, 18, 18, mouseX, mouseY)) {
            playClick();
            CompoundTag data = new CompoundTag();
            data.putBoolean("polling", !counter.polling);
            sendControl(data);
            return true;
        }

        if (isHovering(193, 30, 18, 18, mouseX, mouseY)) {
            playClick();
            CompoundTag data = new CompoundTag();
            for (int i = 0; i < 3; i++) data.putString("c" + i, this.frequency[i].getValue());
            sendControl(data);
            return true;
        }

        return handled;
    }

    private void sendControl(CompoundTag data) {
        PacketDispatcher.sendToServer(new NBTControlPacket(data, counter.getBlockPos()));
    }

    private void playClick() {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (EditBox box : this.frequency) {
            if (box != null && box.isFocused() && super.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        if (keyCode == 256) {
            this.onClose();
            return true;
        }
        if (this.minecraft != null && keyCode == this.minecraft.options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
