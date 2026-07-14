package com.hbm.inventory.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public interface IGuiInfoContainer {

    ResourceLocation GUI_UTIL = ResLocation("hbm", "textures/gui/gui_utility.png");

    default void drawElectricityInfo(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, long power, long maxPower) {
        if (isMouseOver(mouseX, mouseY, x, y, width, height)) {
            drawInfo(graphics, new String[]{power + " / " + maxPower + " HE"}, mouseX, mouseY);
        }
    }

    default void drawCustomInfoStat(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, String... text) {
        drawCustomInfoStat(graphics, mouseX, mouseY, x, y, width, height, mouseX, mouseY, text);
    }

    default void drawCustomInfoStat(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, int tPosX, int tPosY, String... text) {
        if (isMouseOver(mouseX, mouseY, x, y, width, height)) {
            List<Component> list = new ArrayList<>();
            for (String s : text) {
                list.add(Component.literal(s));
            }
            graphics.renderTooltip(Minecraft.getInstance().font, list, java.util.Optional.empty(), tPosX, tPosY);
        }
    }

    default void drawInfo(GuiGraphics graphics, String[] text, int x, int y) {
        List<Component> list = new ArrayList<>();
        for (String s : text) {
            list.add(Component.literal(s));
        }
        graphics.renderTooltip(Minecraft.getInstance().font, list, java.util.Optional.empty(), x, y);
    }

    default void drawInfoPanel(GuiGraphics graphics, int x, int y, int width, int height, int type) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        switch(type) {
            case 0: graphics.blit(GUI_UTIL, x, y, 0, 0, 8, 8); break;      // Small blue I
            case 1: graphics.blit(GUI_UTIL, x, y, 0, 8, 8, 8); break;      // Small green I
            case 2: graphics.blit(GUI_UTIL, x, y, 8, 0, 16, 16); break;     // Large blue I
            case 3: graphics.blit(GUI_UTIL, x, y, 24, 0, 16, 16); break;    // Large green I
            case 4: graphics.blit(GUI_UTIL, x, y, 0, 16, 8, 8); break;      // Small red !
            case 5: graphics.blit(GUI_UTIL, x, y, 0, 24, 8, 8); break;      // Small yellow !
            case 6: graphics.blit(GUI_UTIL, x, y, 8, 16, 16, 16); break;    // Large red !
            case 7: graphics.blit(GUI_UTIL, x, y, 24, 16, 16, 16); break;   // Large yellow !
            case 8: graphics.blit(GUI_UTIL, x, y, 0, 32, 8, 8); break;      // Small blue *
            case 9: graphics.blit(GUI_UTIL, x, y, 0, 40, 8, 8); break;      // Small grey *
            case 10: graphics.blit(GUI_UTIL, x, y, 8, 32, 16, 16); break;   // Large blue *
            case 11: graphics.blit(GUI_UTIL, x, y, 24, 32, 16, 16); break;  // Large grey *
        }
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    default void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, null);
    }
}