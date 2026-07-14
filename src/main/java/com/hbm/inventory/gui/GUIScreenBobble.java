package com.hbm.inventory.gui;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.deco.TileEntityBobble;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GUIScreenBobble extends Screen {

    private final TileEntityBobble bobble;

    public GUIScreenBobble(TileEntityBobble bobble) {
        super(Component.literal("Bobblehead"));
        this.bobble = bobble;
    }

    @Override
    protected void init() {
        super.init();
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.BLOCK_BOBBLE.get(), 1.0F));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        double sizeX = 300;
        double sizeY = 150;
        double left = (this.width - sizeX) / 2;
        double top = (this.height - sizeY) / 2;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buffer.vertex(left + sizeX, top, 0).color(0.0F, 0.2F, 0.0F, 0.8F).endVertex();
        buffer.vertex(left, top, 0).color(0.0F, 0.2F, 0.0F, 0.8F).endVertex();
        buffer.vertex(left, top + sizeY, 0).color(0.0F, 0.2F, 0.0F, 0.8F).endVertex();
        buffer.vertex(left + sizeX, top + sizeY, 0).color(0.0F, 0.2F, 0.0F, 0.8F).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();

        int nextLevel = (int) top + 10;

        String bobbleTitle = "Nuclear Tech Commemorative Bobblehead";
        guiGraphics.drawString(font, bobbleTitle,
                (int)(left + sizeX / 2 - font.width(bobbleTitle) / 2),
                nextLevel, 0x00ff00, true);

        nextLevel += 10;

        String bobbleName = this.bobble.type.name;
        if (this.bobble.type == TileEntityBobble.BobbleType.MELLOW) {
            bobbleName = anagramIt(bobbleName, "GEORGEWILLIAMPATON");
        }
        guiGraphics.drawString(font, bobbleName,
                (int)(left + sizeX / 2 - font.width(bobbleName) / 2),
                nextLevel, 0x009900, true);

        nextLevel += 20;

        if (this.bobble.type.contribution != null) {
            String title = "Has contributed";
            guiGraphics.drawString(font, title,
                    (int)(left + sizeX / 2 - font.width(title) / 2),
                    nextLevel, 0x00ff00, true);

            nextLevel += 10;

            String[] list = this.bobble.type.contribution.split("\\$");
            for (String text : list) {
                guiGraphics.drawString(font, text,
                        (int)(left + sizeX / 2 - font.width(text) / 2),
                        nextLevel, 0x009900, true);
                nextLevel += 10;
            }

            nextLevel += 10;
        }

        if (this.bobble.type.inscription != null) {
            String title = "On the bottom is the following inscription:";
            guiGraphics.drawString(font, title,
                    (int)(left + sizeX / 2 - font.width(title) / 2),
                    nextLevel, 0x00ff00, true);

            nextLevel += 10;

            String[] list = this.bobble.type.inscription.split("\\$");
            for (String text : list) {
                guiGraphics.drawString(font, text,
                        (int)(left + sizeX / 2 - font.width(text) / 2),
                        nextLevel, 0x009900, true);
                nextLevel += 10;
            }

            nextLevel += 10;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == minecraft.options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private String anagramIt(String from, String to) {
        double t = Math.sin((double) System.currentTimeMillis() / 1500.0) * 0.75 + 0.5;

        char[] lettersFrom = from.toCharArray();
        char[] lettersTo = to.toCharArray();
        boolean[] hasPairedLetter = new boolean[lettersFrom.length];
        List<Pair> letterTargets = new ArrayList<>();

        for (int i = 0; i < lettersFrom.length; i++) {
            char letterFrom = lettersFrom[i];
            for (int o = 0; o < lettersTo.length; o++) {
                char letterTo = lettersTo[o];
                if (letterFrom == letterTo && !hasPairedLetter[o]) {
                    double v = lerp((double) i, (double) o, t);
                    letterTargets.add(new Pair(v, lettersFrom[i]));
                    hasPairedLetter[o] = true;
                    break;
                }
            }
        }

        for (int i = 0; i < letterTargets.size(); i++) {
            for (int j = i + 1; j < letterTargets.size(); j++) {
                if (letterTargets.get(i).value > letterTargets.get(j).value) {
                    Pair temp = letterTargets.get(i);
                    letterTargets.set(i, letterTargets.get(j));
                    letterTargets.set(j, temp);
                }
            }
        }

        StringBuilder anagrammedText = new StringBuilder();
        for (Pair pair : letterTargets) {
            anagrammedText.append(pair.character);
        }

        return anagrammedText.toString();
    }

    private double lerp(double a, double b, double t) {
        t = Mth.clamp(t, 0, 1);
        return a * (1 - t) + b * t;
    }

    private static class Pair {
        double value;
        char character;

        Pair(double value, char character) {
            this.value = value;
            this.character = character;
        }
    }
}