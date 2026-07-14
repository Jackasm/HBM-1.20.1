package com.hbm.render.overlay;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HazardOverlay {

    public class ClientHazardsData {
        private static int blackLung = 0;
        private static int asbestos = 0;

        public static int getBlackLung() {
            return blackLung;
        }

        public static void setBlackLung(int value) {
            blackLung = value;
        }

        public static int getAsbestos() {
            return asbestos;
        }

        public static void setAsbestos(int value) {
            asbestos = value;
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.options.hideGui) return;

        int blackLung = ClientHazardsData.getBlackLung();
        int asbestos = ClientHazardsData.getAsbestos();

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        // Позиция в правом верхнем углу
        int x = screenWidth - 80; // Отступ от правого края
        int y = 5; // Отступ сверху

        RenderSystem.enableBlend();

        // Черные легкие
        if (blackLung > 0) {
            renderHazard(guiGraphics, x, y, "§cBlack Lung", blackLung, HbmLivingProps.MAX_BLACK_LUNG);
            y += 30;
        }

        // Асбест
        if (asbestos > 0) {
            renderHazard(guiGraphics, x, y, "§7Asbestos", asbestos, HbmLivingProps.MAX_ASBESTOS);
        }

        RenderSystem.disableBlend();
    }

    private static void renderHazard(GuiGraphics guiGraphics, int x, int y, String name, int value, int max) {
        Minecraft mc = Minecraft.getInstance();

        // Процент заполнения
        float percent = (float) value / max;
        String text = name + ": " + (int)(percent * 100) + "%";

        // Рисуем текст с черной обводкой

        guiGraphics.drawString(mc.font, text, x, y, 0xFFFFFF);
        text = "Value: " + value;
        guiGraphics.drawString(mc.font, text, x, y + 10, 0xFFFFFF);

        // Прогресс-бар
        int barWidth = 100;
        int barHeight = 4;
        int barX = x;
        int barY = y + 20;

        // Фон
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x80000000);

        // Заполненная часть
        int filledWidth = (int)(barWidth * percent);
        int color = getColorForPercent(percent);
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight, color);
    }

    private static int getColorForPercent(float percent) {
        if (percent < 0.3f) return 0xFF00FF00; // Зеленый
        if (percent < 0.7f) return 0xFFFFFF00; // Желтый
        return 0xFFFF0000; // Красный
    }
}
