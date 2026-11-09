package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerRadioTelex;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.network.TileEntityRadioTelex;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIScreenRadioTelex extends AbstractContainerScreen<ContainerRadioTelex> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_telex.png");

    private final TileEntityRadioTelex telex;
    private EditBox txFrequency;
    private EditBox rxFrequency;
    private boolean textFocus = false;

    private final String[] txBuffer;
    private int cursorPos = 0;

    public GUIScreenRadioTelex(ContainerRadioTelex container, Inventory inv, Component title) {
        super(container, inv, title);
        this.telex = container.telex;
        this.imageWidth = 256;
        this.imageHeight = 244;
        this.txBuffer = new String[telex.txBuffer.length];

        System.arraycopy(telex.txBuffer, 0, this.txBuffer, 0, txBuffer.length);

        for (int i = 4; i > 0; i--) {
            if (!txBuffer[i].isEmpty()) {
                cursorPos = i;
                break;
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        this.txFrequency = new EditBox(this.font, leftPos + 29, topPos + 110, 90, 14, Component.empty());
        this.txFrequency.setTextColor(0x00ff00);
        this.txFrequency.setMaxLength(10);
        this.txFrequency.setValue(telex.txChannel == null ? "" : telex.txChannel);

        this.rxFrequency = new EditBox(this.font, leftPos + 29, topPos + 224, 90, 14, Component.empty());
        this.rxFrequency.setTextColor(0x00ff00);
        this.rxFrequency.setMaxLength(10);
        this.rxFrequency.setValue(telex.rxChannel == null ? "" : telex.rxChannel);

        this.addRenderableWidget(txFrequency);
        this.addRenderableWidget(rxFrequency);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltips(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // TX буфер
        for (int line = 0; line < 5; line++) {
            String text = txBuffer[line];
            int y = 11 + 14 * line;
            String format = ChatFormatting.RESET.toString();

            for (int index = 0; index < text.length(); index++) {
                int x = 11 + 7 * index;
                char c = text.charAt(index);
                x += (7 - this.font.width(String.valueOf(c))) / 2;
                if (c == '§' && text.length() > index + 1) {
                    format = "§" + text.charAt(index + 1);
                    x -= 3;
                }
                String glyph = format + c;
                if (c == '\u0007') glyph = ChatFormatting.RED + "B";
                if (c == '\u000c') glyph = ChatFormatting.RED + "P";
                if (c == '\u007f') glyph = ChatFormatting.RED + "<";
                if (c == '\u0016') glyph = ChatFormatting.RED + "W";
                graphics.drawString(font, glyph, leftPos + x, topPos + y, 0x00ff00, false);
            }

            if (System.currentTimeMillis() % 1000 < 500 && this.textFocus) {
                int x = Math.max(11 + 7 * (text.length() - 1) + 7, 11);
                if (this.cursorPos == line) {
                    graphics.drawString(font, "|", leftPos + x, topPos + y, 0x00ff00, false);
                }
            }
        }

        // RX буфер
        for (int line = 0; line < 5; line++) {
            String text = telex.rxBuffer[line];
            int y = 145 + 14 * line;
            String format = ChatFormatting.RESET.toString();
            int x = 11;

            for (int index = 0; index < text.length(); index++) {
                char c = text.charAt(index);
                x += (7 - this.font.width(String.valueOf(c))) / 2;
                if (c == '§' && text.length() > index + 1) {
                    format = "§" + text.charAt(index + 1);
                    c = ' ';
                } else if (c == '§') {
                    c = ' ';
                } else if (index > 0 && text.charAt(index - 1) == '§') {
                    c = ' ';
                    x -= 14;
                }
                String glyph = format + c;
                graphics.drawString(font, glyph, leftPos + x, topPos + y, 0x00ff00, false);
                x += 7;
            }
        }

        // Осциллограф
        Random rand = new Random(telex.sendingChar);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = graphics.pose().last().pose();
        double offset = 0;
        for (int i = 0; i < 48; i++) {
            float x1 = leftPos + 199 + i;
            float y1 = (float) (topPos + 93.5 + offset);
            if (telex.sendingChar != ' ' && i > 4 && i < 43) offset = rand.nextGaussian() * 7;
            else offset = 0;
            offset = Mth.clamp(offset, -7D, 7D);
            float x2 = leftPos + 199 + i + 1;
            float y2 = (float) (topPos + 93.5 + offset);

            buffer.vertex(matrix, x1, y1, 0).color(0f, 1f, 0f, 1f).endVertex();
            buffer.vertex(matrix, x2, y2, 0).color(0f, 1f, 0f, 1f).endVertex();
        }
        Tesselator.getInstance().end();
    }

    private void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        if (checkClick(mouseX, mouseY, 7, 85, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.GOLD + "BELL"), Component.translatable("hbm.text.plays_a_bell_when_this_character_is_received")), mouseX, mouseY);
        if (checkClick(mouseX, mouseY, 27, 85, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.GOLD + "PRINT"), Component.translatable("hbm.text.forces_recipient_to_print_message_after_transmis")), mouseX, mouseY);
        if (checkClick(mouseX, mouseY, 47, 85, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.GOLD + "CLEAR SCREEN"), Component.translatable("hbm.text.wipes_message_buffer_when_this_character_is_rece")), mouseX, mouseY);
        if (checkClick(mouseX, mouseY, 67, 85, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.GOLD + "FORMAT"), Component.translatable("hbm.text.inserts_format_character_for_message_formatting")), mouseX, mouseY);
        if (checkClick(mouseX, mouseY, 87, 85, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.GOLD + "PAUSE"), Component.translatable("hbm.text.pauses_message_transmission_for_one_second")), mouseX, mouseY);

        if (checkClick(mouseX, mouseY, 127, 105, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.GREEN + "SAVE ID")), mouseX, mouseY);
        if (checkClick(mouseX, mouseY, 147, 105, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.YELLOW + "SEND MESSAGE")), mouseX, mouseY);
        if (checkClick(mouseX, mouseY, 167, 105, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.RED + "DELETE MESSAGE BUFFER")), mouseX, mouseY);

        if (checkClick(mouseX, mouseY, 127, 219, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.GREEN + "SAVE ID")), mouseX, mouseY);
        if (checkClick(mouseX, mouseY, 147, 219, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.AQUA + "PRINT MESSAGE")), mouseX, mouseY);
        if (checkClick(mouseX, mouseY, 167, 219, 18, 18)) graphics.renderComponentTooltip(font, List.of(Component.literal(ChatFormatting.RED + "CLEAR SCREEN")), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        this.txFrequency.mouseClicked(mouseX, mouseY, button);
        this.rxFrequency.mouseClicked(mouseX, mouseY, button);

        if (leftPos + 7 <= mouseX && leftPos + 7 + 242 > mouseX && topPos + 7 < mouseY && topPos + 7 + 74 >= mouseY) {
            this.textFocus = true;
        } else {
            this.textFocus = false;
        }

        char character = '\0';
        String cmd = null;

        if (checkClick((int) mouseX, (int) mouseY, 7, 85, 18, 18)) character = '\u0007';
        if (checkClick((int) mouseX, (int) mouseY, 27, 85, 18, 18)) character = '\u000c';
        if (checkClick((int) mouseX, (int) mouseY, 47, 85, 18, 18)) character = '\u007f';
        if (checkClick((int) mouseX, (int) mouseY, 67, 85, 18, 18)) character = '§';
        if (checkClick((int) mouseX, (int) mouseY, 87, 85, 18, 18)) character = '\u0016';

        if (checkClick((int) mouseX, (int) mouseY, 127, 105, 18, 18) || checkClick((int) mouseX, (int) mouseY, 127, 219, 18, 18)) cmd = "sve";
        if (checkClick((int) mouseX, (int) mouseY, 147, 105, 18, 18)) cmd = "snd";
        if (checkClick((int) mouseX, (int) mouseY, 167, 105, 18, 18)) {
            cmd = "rxdel";
            for (int j = 0; j < 5; j++) this.txBuffer[j] = "";
            CompoundTag data = new CompoundTag();
            for (int j = 0; j < 5; j++) data.putString("tx" + j, this.txBuffer[j]);
            PacketDispatcher.sendToServer(new NBTControlPacket(data, telex.getBlockPos()));
        }
        if (checkClick((int) mouseX, (int) mouseY, 147, 219, 18, 18)) cmd = "rxprt";
        if (checkClick((int) mouseX, (int) mouseY, 167, 219, 18, 18)) cmd = "rxcls";

        if (cmd != null) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            CompoundTag data = new CompoundTag();
            data.putString("cmd", cmd);

            if ("snd".equals(cmd)) {
                for (int j = 0; j < 5; j++) data.putString("tx" + j, this.txBuffer[j]);
            }

            if ("sve".equals(cmd)) {
                data.putString("txChan", this.txFrequency.getValue());
                data.putString("rxChan", this.rxFrequency.getValue());
            }

            PacketDispatcher.sendToServer(new NBTControlPacket(data, telex.getBlockPos()));
        }

        if (character != '\0') {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            setTextFocus();
            submitChar(character);
        }

        return true;
    }

    private boolean checkClick(int x, int y, int left, int top, int sizeX, int sizeY) {
        return leftPos + left <= x && leftPos + left + sizeX > x && topPos + top < y && topPos + top + sizeY >= y;
    }

    private void setTextFocus() {
        this.textFocus = true;
        this.txFrequency.setFocused(false);
        this.rxFrequency.setFocused(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.txFrequency.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (this.rxFrequency.keyPressed(keyCode, scanCode, modifiers)) return true;

        if (this.textFocus) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.textFocus = false;
                return true;
            }

            if (keyCode == GLFW.GLFW_KEY_UP) this.cursorPos--;
            if (keyCode == GLFW.GLFW_KEY_DOWN) this.cursorPos++;

            this.cursorPos = Mth.clamp(cursorPos, 0, 4);

            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && this.txBuffer[cursorPos].length() > 0) {
                this.txBuffer[cursorPos] = this.txBuffer[cursorPos].substring(0, this.txBuffer[cursorPos].length() - 1);
                return true;
            }

            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (this.txFrequency.charTyped(c, modifiers)) return true;
        if (this.rxFrequency.charTyped(c, modifiers)) return true;

        if (this.textFocus) {
            submitChar(c);
            return true;
        }

        return super.charTyped(c, modifiers);
    }

    private void submitChar(char c) {
        String line = this.txBuffer[cursorPos];
        if (line.length() < TileEntityRadioTelex.lineWidth) {
            this.txBuffer[cursorPos] = line + c;
        }
    }

    @Override
    public void onClose() {
        CompoundTag data = new CompoundTag();
        for (int j = 0; j < 5; j++) data.putString("tx" + j, this.txBuffer[j]);
        PacketDispatcher.sendToServer(new NBTControlPacket(data, telex.getBlockPos()));
        super.onClose();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        // У Telex нет слотов — ничего не рисуем
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}