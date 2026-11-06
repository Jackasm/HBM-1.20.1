package com.hbm.inventory.gui;

import com.hbm.api.redstoneoverradio.IRORInfo;
import com.hbm.api.redstoneoverradio.IRORValueProvider;
import com.hbm.blocks.network.RadioTorchBase;
import com.hbm.inventory.container.ContainerRadioTorch;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.network.TileEntityRadioTorchReader;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * Экран RTTY-читателя: восемь строк «канал — имя значения», кнопки опроса и
 * записи.
 *
 * <p>Порт {@code ref:inventory/gui/GUIScreenRadioTorchReader.java}. Раскладка
 * 8×2 (канал и имя в строке) — из эталона 256×204. Иконка у левого края
 * показывает список значений, которые умеет отдавать соседний блок.</p>
 */
public class RadioTorchReaderScreen extends RadioTorchScreenBase {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/machine/gui_rtty_reader.png");

    private final TileEntityRadioTorchReader reader;
    private final EditBox[] channels = new EditBox[8];
    private final EditBox[] names = new EditBox[8];

    public RadioTorchReaderScreen(ContainerRadioTorch menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.reader = (TileEntityRadioTorchReader) menu.getBE();
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected void sendControl(CompoundTag data) {
        PacketDispatcher.sendToServer(new NBTControlPacket(data, reader.getBlockPos()));
    }

    @Override
    protected void init() {
        super.init();

        int oX = 4;
        int oY = 4;

        for (int i = 0; i < 8; i++) {
            this.channels[i] = new EditBox(this.font,
                    leftPos + 25 + oX, topPos + 53 + i * 18 + oY, 72 - oX * 2, 14, Component.empty());
            this.channels[i].setTextColor(0x00FF00);
            this.channels[i].setTextColorUneditable(0x00FF00);
            this.channels[i].setBordered(false);
            this.channels[i].setMaxLength(RadioTorchScreenBase.MAX_CHAN_LENGTH);
            this.channels[i].setValue(reader.channels[i] == null ? "" : reader.channels[i]);
            this.addRenderableWidget(this.channels[i]);

            this.names[i] = new EditBox(this.font,
                    leftPos + 119 + oX, topPos + 53 + i * 18 + oY, 126 - oX * 2, 14, Component.empty());
            this.names[i].setTextColor(0x00FF00);
            this.names[i].setTextColorUneditable(0x00FF00);
            this.names[i].setBordered(false);
            this.names[i].setMaxLength(25);
            this.names[i].setValue(reader.names[i] == null ? "" : reader.names[i]);
            this.addRenderableWidget(this.names[i]);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (reader.polling) {
            graphics.blit(TEXTURE, leftPos + 173, topPos + 17, 0, 204, 18, 18);
        }

        for (int i = 0; i < 8; i++) {
            this.channels[i].render(graphics, mouseX, mouseY, partialTick);
            this.names[i].render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = this.title.getString();
        graphics.drawString(this.font, name,
                this.imageWidth / 2 - this.font.width(name) / 2,
                6, 4210752, false);

        if (isHovering(29, 17, 18, 18, mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("Доступные значения:"));
            for (String value : availableValues()) {
                lines.add(Component.literal("§d" + value));
            }
            graphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
        }

        if (isHovering(173, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal(reader.polling ? "Polling" : "State Change")),
                    Optional.empty(), mouseX, mouseY);
        }

        if (isHovering(209, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal("Save Settings")),
                    Optional.empty(), mouseX, mouseY);
        }
    }

    /** Значения, которые объявляет соседний блок (эталонный getFunctionInfo). */
    private List<String> availableValues() {
        List<String> values = new ArrayList<>();
        if (reader.getLevel() == null) return values;

        BlockEntity be = reader.getLevel().getBlockEntity(
                reader.getBlockPos().relative(reader.getBlockState().getValue(RadioTorchBase.FACING)));

        if (be instanceof IRORInfo info) {
            for (String line : info.getFunctionInfo()) {
                if (line != null && line.startsWith(IRORValueProvider.PREFIX_VALUE)) {
                    values.add(line.substring(IRORValueProvider.PREFIX_VALUE.length()));
                }
            }
        }

        return values;
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

        if (isHovering(173, 17, 18, 18, mouseX, mouseY)) {
            playClick();
            CompoundTag data = new CompoundTag();
            data.putBoolean("p", !reader.polling);
            sendControl(data);
            return true;
        }

        if (isHovering(209, 17, 18, 18, mouseX, mouseY)) {
            playClick();
            CompoundTag data = new CompoundTag();
            for (int i = 0; i < 8; i++) {
                data.putString("c" + i, this.channels[i].getValue());
                data.putString("n" + i, this.names[i].getValue());
            }
            sendControl(data);
            return true;
        }

        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedField() != null && super.keyPressed(keyCode, scanCode, modifiers)) return true;

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

    private EditBox focusedField() {
        for (EditBox box : this.channels) if (box != null && box.isFocused()) return box;
        for (EditBox box : this.names) if (box != null && box.isFocused()) return box;
        return null;
    }
}
