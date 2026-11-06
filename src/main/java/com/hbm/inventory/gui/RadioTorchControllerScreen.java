package com.hbm.inventory.gui;

import com.hbm.api.redstoneoverradio.IRORInfo;
import com.hbm.api.redstoneoverradio.IRORInteractive;
import com.hbm.api.redstoneoverradio.IRORValueProvider;
import com.hbm.blocks.network.RadioTorchBase;
import com.hbm.inventory.container.ContainerRadioTorch;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.network.TileEntityRadioTorchController;
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
 * Экран RTTY-контроллера: канал, опрос и запись. Окно узкое — 256×42, как в
 * эталоне {@code ref:inventory/gui/GUIScreenRadioTorchController.java}.
 *
 * <p>Иконка слева показывает, какие функции принимает соседний блок: команда
 * уходит ему в {@code runRORFunction} строкой {@code FUN:имя!параметр:параметр}.</p>
 */
public class RadioTorchControllerScreen extends RadioTorchScreenBase {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/machine/gui_rtty_controller.png");

    private final TileEntityRadioTorchController controller;
    private EditBox frequency;

    public RadioTorchControllerScreen(ContainerRadioTorch menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.controller = (TileEntityRadioTorchController) menu.getBE();
        // Эталонное окно узкое: только строка канала и кнопки.
        this.imageWidth = 256;
        this.imageHeight = 42;
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected void sendControl(CompoundTag data) {
        PacketDispatcher.sendToServer(new NBTControlPacket(data, controller.getBlockPos()));
    }

    @Override
    protected void init() {
        super.init();

        int oX = 4;
        int oY = 4;

        this.frequency = new EditBox(this.font,
                leftPos + 25 + oX, topPos + 17 + oY, 90 - oX * 2, 14, Component.empty());
        this.frequency.setTextColor(0x00FF00);
        this.frequency.setTextColorUneditable(0x00FF00);
        this.frequency.setBordered(false);
        this.frequency.setMaxLength(RadioTorchScreenBase.MAX_CHAN_LENGTH);
        this.frequency.setValue(controller.channel == null ? "" : controller.channel);
        this.addRenderableWidget(this.frequency);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (controller.polling) {
            graphics.blit(TEXTURE, leftPos + 173, topPos + 17, 0, 42, 18, 18);
        }

        this.frequency.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = this.title.getString();
        graphics.drawString(this.font, name,
                this.imageWidth / 2 - this.font.width(name) / 2,
                6, 4210752, false);

        if (isHovering(137, 17, 18, 18, mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("Доступные функции:"));
            for (String function : availableFunctions()) {
                lines.add(Component.literal("§b" + function));
            }
            graphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
        }

        if (isHovering(173, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal(controller.polling ? "Polling" : "State Change")),
                    Optional.empty(), mouseX, mouseY);
        }

        if (isHovering(209, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal("Save Settings")),
                    Optional.empty(), mouseX, mouseY);
        }
    }

    /** Функции соседнего блока: всё, что объявлено с префиксом {@code FUN:}. */
    private List<String> availableFunctions() {
        List<String> functions = new ArrayList<>();
        if (controller.getLevel() == null) return functions;

        BlockEntity be = controller.getLevel().getBlockEntity(
                controller.getBlockPos().relative(controller.getBlockState().getValue(RadioTorchBase.FACING)));

        if (be instanceof IRORInfo info) {
            for (String line : info.getFunctionInfo()) {
                if (line != null && line.startsWith(IRORValueProvider.PREFIX_FUNCTION)) {
                    functions.add(line.substring(IRORValueProvider.PREFIX_FUNCTION.length()));
                }
            }
        }

        return functions;
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
            data.putBoolean("p", !controller.polling);
            sendControl(data);
            return true;
        }

        if (isHovering(209, 17, 18, 18, mouseX, mouseY)) {
            playClick();
            CompoundTag data = new CompoundTag();
            data.putString("c", this.frequency.getValue());
            sendControl(data);
            return true;
        }

        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.frequency.isFocused() && super.keyPressed(keyCode, scanCode, modifiers)) return true;

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
