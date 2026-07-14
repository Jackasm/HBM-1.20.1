package com.hbm.inventory.gui;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.block.TileEntitySnowglobe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GUIScreenSnowglobe extends Screen {

    private final TileEntitySnowglobe snowglobe;

    public GUIScreenSnowglobe(TileEntitySnowglobe snowglobe) {
        super(Component.translatable("gui.snowglobe"));
        this.snowglobe = snowglobe;
    }

    @Override
    protected void init() {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(ModSounds.BLOCK_BOBBLE.get(), 1.0F)
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        double sizeX = 300;
        double sizeY = 150;
        double left = (this.width - sizeX) / 2;
        double top = (this.height - sizeY) / 2;

        // Отрисовка фона
        graphics.fillGradient(
                (int) left, (int) top,
                (int) (left + sizeX), (int) (top + sizeY),
                0xCC002200, 0xCC002200
        );

        RenderSystem.disableBlend();

        int nextLevel = (int) top + 10;

        String bobbleTitle = "Nuclear Tech Commemorative Snowglobe";
        graphics.drawString(font, bobbleTitle,
                (int) (left + sizeX / 2 - font.width(bobbleTitle) / 2),
                nextLevel, 0x00ff00);

        nextLevel += 10;

        String bobbleName = this.snowglobe.type.label;
        graphics.drawString(font, bobbleName,
                (int) (left + sizeX / 2 - font.width(bobbleName) / 2),
                nextLevel, 0x009900);

        nextLevel += 20;

        if (this.snowglobe.type.inscription != null) {
            String title = "On the bottom is the following inscription:";
            graphics.drawString(font, title,
                    (int) (left + sizeX / 2 - font.width(title) / 2),
                    nextLevel, 0x00ff00);

            nextLevel += 10;

            List<String> list = breakLines(this.snowglobe.type.inscription, 280);
            for (String text : list) {
                graphics.drawString(font, text,
                        (int) (left + sizeX / 2 - font.width(text) / 2),
                        nextLevel, 0x009900);
                nextLevel += 10;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private List<String> breakLines(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] paragraphs = text.split("\\$");
        for (String paragraph : paragraphs) {
            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                if (font.width(line + word) > maxWidth) {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    if (line.length() > 0) line.append(" ");
                    line.append(word);
                }
            }
            if (line.length() > 0) lines.add(line.toString());
        }
        return lines;
    }
}