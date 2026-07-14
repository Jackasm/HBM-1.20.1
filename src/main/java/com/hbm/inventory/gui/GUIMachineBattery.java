package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineBattery;

import com.hbm.network.PacketDispatcher;
import com.hbm.tileentity.storage.TileEntityMachineBattery;
import com.hbm.util.RefStrings;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIMachineBattery extends AbstractContainerScreen<ContainerMachineBattery> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/storage/gui_battery.png");
    private final TileEntityMachineBattery battery;

    public GUIMachineBattery(ContainerMachineBattery container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.battery = container.getBattery();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = battery.getDisplayName().getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Энергетическая шкала
        if (battery.power > 0) {
            int i = (int) battery.getPowerRemainingScaled(52);
            graphics.blit(TEXTURE, leftPos + 62, topPos + 69 - i, 176, 52 - i, 52, i);
        }

        // Режимы редстоуна
        int low = battery.redLow;
        graphics.blit(TEXTURE, leftPos + 133, topPos + 16, 176, 52 + low * 18, 18, 18);
        int high = battery.redHigh;
        graphics.blit(TEXTURE, leftPos + 133, topPos + 52, 176, 52 + high * 18, 18, 18);

        // Иконка приоритета
        graphics.blit(TEXTURE, leftPos + 152, topPos + 35, 194, 52 + battery.priority.ordinal() * 16 - 16, 16, 16);
    }

    private static String formatDelta(long delta) {
        String sign = delta > 0 ? "+" : (delta < 0 ? "-" : "");
        String num = String.valueOf(Math.abs(delta));
        return sign + num + "HE/s";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Кнопка для redHigh (режим с сигналом)
        if (isHovering(133, 52, 18, 18, mouseX, mouseY) && button == 0) {
            PacketDispatcher.sendAuxButtonPacket(battery.getBlockPos(), 0, 0);
            playButtonClickSound();
            return true;
        }

        // Кнопка для redLow (режим без сигнала)
        if (isHovering(133, 16, 18, 18, mouseX, mouseY) && button == 0) {
            PacketDispatcher.sendAuxButtonPacket(battery.getBlockPos(), 0, 1);
            playButtonClickSound();
            return true;
        }

        // Кнопка для приоритета
        if (isHovering(152, 35, 16, 16, mouseX, mouseY) && button == 0) {
            PacketDispatcher.sendAuxButtonPacket(battery.getBlockPos(), 0, 2);
            playButtonClickSound();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void playButtonClickSound() {
        assert minecraft != null;
        minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)
        );
    }

    // Тултипы для кнопок
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Тултип для redHigh
        if (isHovering(133, 52, 18, 18, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("§eMode with Redstone:"));
            String modeName = getModeName(menu.getRedHigh());
            tooltip.add(Component.literal("§7" + modeName));
            tooltip.add(Component.literal("§7Click to change"));
            graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
        }

        // Тултип для redLow
        if (isHovering(133, 16, 18, 18, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("§eMode without Redstone:"));
            String modeName = getModeName(menu.getRedLow());
            tooltip.add(Component.literal("§7" + modeName));
            tooltip.add(Component.literal("§7Click to change"));
            graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
        }

        // Тултип для приоритета
        if (isHovering(152, 35, 16, 16, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("§ePriority:"));
            String priorityName = getPriorityName(menu.getPriority());
            tooltip.add(Component.literal("§7" + priorityName));
            tooltip.add(Component.literal("§7Click to change"));
            graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
        }

        if (isHovering(62, 17, 52, 52, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("§eEnergy:"));
            tooltip.add(Component.literal("§7" + formatNumber(battery.power) + " / " +
                    formatNumber(battery.getMaxPower()) + " HE"));

            double percent = (double) battery.power / (double) battery.getMaxPower() * 100;
            tooltip.add(Component.literal("§7" + String.format("%.1f", percent) + "%"));

            if (battery.delta != 0) {
                String deltaText = formatDelta(battery.delta);
                tooltip.add(Component.literal("§7" + deltaText));
            }

            graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private static String formatNumber(long value) {
        if (value >= 1_000_000_000_000L) return (value / 1_000_000_000_000L) + "T";
        if (value >= 1_000_000_000L) return (value / 1_000_000_000L) + "B";
        if (value >= 1_000_000L) return (value / 1_000_000L) + "M";
        if (value >= 1_000L) return (value / 1_000L) + "k";
        return String.valueOf(value);
    }

    private String getModeName(short mode) {
        return switch(mode) {
            case 0 -> "Input Only";
            case 1 -> "Buffer (Input & Output)";
            case 2 -> "Output Only";
            case 3 -> "Disabled";
            default -> "Unknown";
        };
    }

    private String getPriorityName(int priority) {
        return switch(priority) {
            case 0 -> "Low";
            case 1 -> "Normal";
            case 2 -> "High";
            default -> "Unknown";
        };
    }

    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX <= leftPos + x + width &&
                mouseY >= topPos + y && mouseY <= topPos + y + height;
    }
}