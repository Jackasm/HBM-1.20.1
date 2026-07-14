// OverlaySection.java (если еще не создан)
package com.hbm.render.overlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OverlaySection {
    public enum Type {
        BLOCK_MAIN,
        BLOCK_DEBUG,
        ENTITY_MAIN,
        DEBUFFS,
        EFFECTS,
        TILE_ENTITY,
        CUSTOM,
        QUEST //Quests
    }

    private final Type type;
    private final List<Component> lines = new ArrayList<>();
    private ItemStack icon = ItemStack.EMPTY;

    public OverlaySection(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public List<Component> getLines() {
        return lines;
    }

    public void addLine(Component line) {
        lines.add(line);
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    private ItemStack toolIcon = ItemStack.EMPTY;

    public ItemStack getToolIcon() {
        return toolIcon;
    }

    public void setToolIcon(ItemStack toolIcon) {
        this.toolIcon = toolIcon;
    }

    // Добавьте этот метод в класс OverlaySection
    public void render(GuiGraphics guiGraphics, OverlayContext context, int x, int y, Font font) {
        // Стандартный рендер (по умолчанию)
        int currentY = y;
        for (Component line : lines) {
            guiGraphics.drawString(font, line, x, currentY, 0xFFFFFF, true);
            currentY += font.lineHeight;
        }
    }
}