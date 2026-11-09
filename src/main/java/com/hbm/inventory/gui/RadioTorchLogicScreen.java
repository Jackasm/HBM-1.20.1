package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerRadioTorch;
import com.hbm.tileentity.network.TileEntityRadioTorchLogic;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * Экран логического RTTY-факела: канал, опрос, 16 строк маппинга и столбец
 * условий сравнения.
 *
 * <p>Порт {@code ref:inventory/gui/GUIScreenRadioTorchLogic.java}. Столбец
 * условий — это 16 квадратов 18×18 левее строк: левый клик листает условие
 * вперёд, колесо — вперёд и назад, а «Save Settings» отправляет весь столбец
 * вместе с каналом. Раньше столбца не было, и строки всегда сравнивались как
 * «меньше» — то есть девять условий из десяти были недоступны из GUI.</p>
 */
public class RadioTorchLogicScreen extends RadioTorchScreenBase {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/machine/gui_rtty_logic_receiver.png");

    /** Десять условий эталона: 0 — «<», 1 — «<=», … 9 — «не содержит». */
    private static final int CONDITION_COUNT = 10;

    private final TileEntityRadioTorchLogic logic;
    private final int[] conditions = new int[16];

    public RadioTorchLogicScreen(ContainerRadioTorch menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.logic = (TileEntityRadioTorchLogic) menu.getBE();
        for (int i = 0; i < 16; i++) this.conditions[i] = logic.conditions[i];
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected int getMappingOffset() {
        return 18;
    }

    @Override
    protected int getMappingWidth() {
        return 54 - 8;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (logic.descending) {
            graphics.blit(TEXTURE, leftPos + 137, topPos + 17, 0, 204, 18, 18);
        }
        if (radio.polling) {
            graphics.blit(TEXTURE, leftPos + 173, topPos + 17, 0, 222, 18, 18);
        }

        for (int i = 0; i < 16; i++) {
            int x = leftPos + 7 + (130 * (i / 8));
            int y = topPos + 53 + (18 * (i % 8));
            boolean empty = this.remap[i].getValue().isEmpty();

            // Пустая строка и заполненная рисуются разными спрайтами эталона.
            if (empty && this.conditions[i] == 0) continue;
            graphics.blit(TEXTURE, x, y, 18 + this.conditions[i] * 18, empty ? 222 : 204, 18, 18);

            if (!empty) {
                graphics.blit(TEXTURE, leftPos + 85 + (130 * (i / 8)), topPos + 57 + (18 * (i % 8)),
                        198, 204, 14, 10);
            }
        }

        for (int i = 0; i < 16; i++) {
            this.remap[i].render(graphics, mouseX, mouseY, partialTick);
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
            graphics.renderTooltip(this.font,
                    List.of(Component.literal(logic.descending ? "Descending Order" : "Ascending Order")),
                    Optional.empty(), mouseX, mouseY);
        }
        if (isHovering(173, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal(radio.polling ? "Polling" : "State Change")),
                    Optional.empty(), mouseX, mouseY);
        }
        if (isHovering(209, 17, 18, 18, mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    List.of(Component.translatable("hbm.text.save_settings")),
                    Optional.empty(), mouseX, mouseY);
        }

        for (int i = 0; i < 16; i++) {
            if (isHovering(7 + (130 * (i / 8)), 53 + (18 * (i % 8)), 18, 18, mouseX, mouseY)) {
                graphics.renderTooltip(this.font,
                        List.of(Component.translatable("desc.gui.rttyLogic.cond" + this.conditions[i])),
                        Optional.empty(), mouseX, mouseY);
                break;
            }
        }
    }

    @Override
    protected void onLeftButton() {
        // В логическом факеле левая кнопка — не «свой маппинг», а порядок обхода.
        CompoundTag data = new CompoundTag();
        data.putBoolean("d", !logic.descending);
        sendControl(data);
    }

    @Override
    protected void addScreenData(CompoundTag data) {
        for (int i = 0; i < 16; i++) data.putInt("k" + i, this.conditions[i]);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);

        for (int i = 0; i < 16; i++) {
            if (isHovering(7 + (130 * (i / 8)), 53 + (18 * (i % 8)), 18, 18, mouseX, mouseY)) {
                playClick();
                this.conditions[i] = (this.conditions[i] + 1) % CONDITION_COUNT;
                return true;
            }
        }

        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (int i = 0; i < 16; i++) {
            if (isHovering(7 + (130 * (i / 8)), 53 + (18 * (i % 8)), 18, 18, mouseX, mouseY)) {
                if (delta > 0) {
                    this.conditions[i] = (this.conditions[i] + 1) % CONDITION_COUNT;
                } else if (delta < 0) {
                    this.conditions[i] = (this.conditions[i] + CONDITION_COUNT - 1) % CONDITION_COUNT;
                }
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
