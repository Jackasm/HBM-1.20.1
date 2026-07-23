package com.hbm.render.overlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OverlayRenderer {
    private static final int BACKGROUND_COLOR = 0x1A000000;
    private static final int BORDER_COLOR = 0xFF6A5ACD;
    private static final int SECTION_SPACING = 2;
    private static final int CORNER_RADIUS = 4;

    public static void render(OverlayContext context, List<OverlaySection> sections) {
        GuiGraphics guiGraphics = context.guiGraphics();
        Font font = context.mc().font;
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        // Разделяем секции по типу
        List<OverlaySection> questSections = sections.stream()
                .filter(s -> s.getType() == OverlaySection.Type.QUEST)
                .toList();
        List<OverlaySection> otherSections = sections.stream()
                .filter(s -> s.getType() != OverlaySection.Type.QUEST)
                .toList();

        // Рендерим обычные секции (центр сверху)
        if (!otherSections.isEmpty()) {
            int maxWidth = calculateMaxWidth(otherSections, font);
            int totalHeight = calculateTotalHeight(otherSections, font);
            int x = (screenWidth - maxWidth) / 2;
            int y = 0;
            renderRoundedBackground(guiGraphics, x, y, maxWidth, totalHeight);
            int currentY = y + 3;
            for (OverlaySection section : otherSections) {
                currentY = renderSection(guiGraphics, font, section, x + 3, currentY, maxWidth);
            }
        }

        // Рендерим квестовые секции (правый край, центр по вертикали)
        if (!questSections.isEmpty()) {
            int maxWidth = calculateMaxWidth(questSections, font);
            int totalHeight = calculateTotalHeight(questSections, font);
            int x = screenWidth - maxWidth - 5; // отступ справа 5 пикселей
            int y = (screenHeight - totalHeight) / 2;
            renderQuestBackground(guiGraphics, x, y, maxWidth, totalHeight);
            int currentY = y + 3;
            for (OverlaySection section : questSections) {
                currentY = renderSection(guiGraphics, font, section, x + 3, currentY, maxWidth);
            }
        }
    }

    private static int calculateMaxWidth(List<OverlaySection> sections, Font font) {
        int maxTextWidth = 0;
        for (OverlaySection section : sections) {
            for (Component line : section.getLines()) {
                int width = font.width(line);
                if (width > maxTextWidth) maxTextWidth = width;
            }
        }
        // Левая иконка (18) + текст + промежуток 3 пикселя после названия + правая иконка (18) + отступы (5+5)
        return maxTextWidth + 18 + 18 + 10 + 6;
    }

    private static void renderRoundedBackground(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        // Основной фон
        guiGraphics.fill(x + CORNER_RADIUS, y, x + width - CORNER_RADIUS, y + height, BACKGROUND_COLOR);
        guiGraphics.fill(x, y + CORNER_RADIUS, x + width, y + height - CORNER_RADIUS, BACKGROUND_COLOR);

        // Закругленные углы для фона
        fillCircleQuarter(guiGraphics, x + CORNER_RADIUS, y + CORNER_RADIUS, 0);
        fillCircleQuarter(guiGraphics, x + width - CORNER_RADIUS - 1, y + CORNER_RADIUS, 1);
        fillCircleQuarter(guiGraphics, x + CORNER_RADIUS, y + height - CORNER_RADIUS - 1, 2);
        fillCircleQuarter(guiGraphics, x + width - CORNER_RADIUS - 1, y + height - CORNER_RADIUS - 1, 3);

        // Прямые части рамки
        // Верхняя линия (без углов)
        guiGraphics.hLine(x + CORNER_RADIUS, x + width - CORNER_RADIUS - 1, y, BORDER_COLOR);
        // Нижняя линия (без углов)
        guiGraphics.hLine(x + CORNER_RADIUS, x + width - CORNER_RADIUS - 1, y + height - 1, BORDER_COLOR);
        // Левая линия (без углов)
        guiGraphics.vLine(x, y + CORNER_RADIUS, y + height - CORNER_RADIUS - 1, BORDER_COLOR);
        // Правая линия (без углов)
        guiGraphics.vLine(x + width - 1, y + CORNER_RADIUS, y + height - CORNER_RADIUS - 1, BORDER_COLOR);

        // Закругленные углы для рамки
        drawCircleQuarterBorder(guiGraphics, x + CORNER_RADIUS, y + CORNER_RADIUS, 0);
        drawCircleQuarterBorder(guiGraphics, x + width - CORNER_RADIUS - 1, y + CORNER_RADIUS, 1);
        drawCircleQuarterBorder(guiGraphics, x + CORNER_RADIUS, y + height - CORNER_RADIUS - 1, 2);
        drawCircleQuarterBorder(guiGraphics, x + width - CORNER_RADIUS - 1, y + height - CORNER_RADIUS - 1, 3);
    }

    private static void renderQuestBackground(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, 0);
    }

    // Новый метод для рисования закругленных углов рамки
    private static void drawCircleQuarterBorder(GuiGraphics guiGraphics, int centerX, int centerY, int quadrant) {
        // Рисуем только пиксели на границе окружности
        for (int dx = 0; dx <= OverlayRenderer.CORNER_RADIUS; dx++) {
            for (int dy = 0; dy <= OverlayRenderer.CORNER_RADIUS; dy++) {
                // Точка на окружности (или близко к ней)
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (Math.abs(distance - OverlayRenderer.CORNER_RADIUS) < 0.5) { // Пиксели на границе
                    int x = centerX + (quadrant % 2 == 0 ? -dx : dx);
                    int y = centerY + (quadrant < 2 ? -dy : dy);
                    guiGraphics.fill(x, y, x + 1, y + 1, OverlayRenderer.BORDER_COLOR);
                }
            }
        }
    }

    private static void fillCircleQuarter(GuiGraphics guiGraphics, int centerX, int centerY, int quadrant) {
        for (int dx = 0; dx <= OverlayRenderer.CORNER_RADIUS; dx++) {
            for (int dy = 0; dy <= OverlayRenderer.CORNER_RADIUS; dy++) {
                if (dx * dx + dy * dy <= OverlayRenderer.CORNER_RADIUS * OverlayRenderer.CORNER_RADIUS) {
                    int x = centerX + (quadrant % 2 == 0 ? -dx : dx);
                    int y = centerY + (quadrant < 2 ? -dy : dy);
                    guiGraphics.fill(x, y, x + 1, y + 1, OverlayRenderer.BACKGROUND_COLOR);
                }
            }
        }
    }

    private static int renderSection(GuiGraphics guiGraphics, Font font, OverlaySection section, int x, int y, int maxWidth) {
        int currentY = y;

        // Левая иконка (блок) – отступ 3 пикселя от левой границы
        int leftIconX = x + 3;
        guiGraphics.renderItem(section.getIcon(), leftIconX, currentY);

        // Правая иконка (инструмент) – отступ 3 пикселя от правой границы
        if (section.getToolIcon() != null && !section.getToolIcon().isEmpty()) {
            int rightIconX = x + maxWidth - 18 - 3 - 3;

            if (section.hasToolFrame() && !section.getToolIcon().isEmpty()) {
                int frameX = rightIconX - 1;
                int frameY = currentY - 1;
                guiGraphics.fill(frameX, frameY, frameX + 18, frameY + 1, section.getToolFrameColor());
                guiGraphics.fill(frameX, frameY + 17, frameX + 18, frameY + 18, section.getToolFrameColor());
                guiGraphics.fill(frameX, frameY, frameX + 1, frameY + 18, section.getToolFrameColor());
                guiGraphics.fill(frameX + 17, frameY, frameX + 18, frameY + 18, section.getToolFrameColor());
            }
            guiGraphics.renderItem(section.getToolIcon(), rightIconX, currentY);
        }

        // Текст – начинается после левой иконки + отступ
        int textX = leftIconX + 18 + 2;

        currentY = currentY + 4;

        int textColor = 0xFFFFFF;
        if (section.getType() == OverlaySection.Type.QUEST) {
            int alpha = getQuestAlpha();
            if (alpha <= 0) return currentY;
            textColor = (alpha << 24) | 0xFFFFFF;
        }

        for (Component line : section.getLines()) {
            guiGraphics.drawString(font, line, textX, currentY, textColor, false);
            currentY += font.lineHeight;
        }
        return currentY;
    }

    private static int calculateTotalHeight(List<OverlaySection> sections, Font font) {
        int totalHeight = 10; // top + bottom padding

        for (int i = 0; i < sections.size(); i++) {
            int sectionHeight = getSectionHeight(sections, font, i);

            // Добавляем внутренние отступы сверху и снизу для секции
            totalHeight += sectionHeight;

            // Добавляем отступ между секциями (кроме последней)
            if (i < sections.size() - 1) {
                totalHeight += SECTION_SPACING;
            }
        }

        return totalHeight;
    }

    private static int getSectionHeight(List<OverlaySection> sections, Font font, int i) {
        OverlaySection section = sections.get(i);
        int sectionHeight;

        // Высота иконок (16px)
        sectionHeight = 16;

        // Высота текста
        if (!section.getLines().isEmpty()) {
            int linesHeight = section.getLines().size() * (font.lineHeight + 1) - 1;
            sectionHeight = Math.max(sectionHeight, linesHeight);
        }
        return sectionHeight;
    }

    private static int getQuestAlpha() {
        if (!QuestOverlay.isTemporaryVisible()) return 255;
        long elapsed = System.currentTimeMillis() - QuestOverlay.getTemporaryStartTime();
        int duration = QuestOverlay.getTemporaryDuration();
        if (duration <= 0) return 255;
        long remaining = duration - elapsed;
        if (remaining <= 0) return 0;
        if (remaining > 1000) return 255;
        // линейное затухание за последнюю секунду
        return (int) (255 * remaining / 1000);
    }
}