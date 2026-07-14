package com.hbm.render.util;

import com.hbm.config.ClientConfig;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.items.weapon.sedna.Crosshair;
import com.hbm.items.weapon.sedna.impl.ItemGunStinger;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class RenderScreenOverlay {

    public static final ResourceLocation misc = ResLocation.ResLocation(RefStrings.MODID, "textures/misc/overlay_misc.png");

    private static long lastSurvey;
    private static float prevResult;
    private static float lastResult;

    private static float fadeOut = 0F;

    public static void renderRadCounter(GuiGraphics guiGraphics, float in) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();

        float radiation = 0;
        radiation = lastResult - prevResult;

        long time_ms = System.currentTimeMillis();
        if(time_ms >= lastSurvey + 1000) {
            lastSurvey = time_ms;
            prevResult = lastResult;
            lastResult = in;
        }

        int length = 74;
        int maxRad = 1000;
        int bar = getScaled(in, maxRad, 74);

        int posX = 16 + ClientConfig.GEIGER_OFFSET_HORIZONTAL;
        int posY = mc.getWindow().getGuiScaledHeight() - 20 - ClientConfig.GEIGER_OFFSET_VERTICAL;

        guiGraphics.blit(misc, posX, posY, 0, 0, 94, 18);
        guiGraphics.blit(misc, posX + 1, posY + 1, 1, 19, bar, 16);

        if(radiation >= 25) {
            guiGraphics.blit(misc, posX + length + 2, posY - 18, 36, 36, 18, 18);
        } else if(radiation >= 10) {
            guiGraphics.blit(misc, posX + length + 2, posY - 18, 18, 36, 18, 18);
        } else if(radiation >= 2.5) {
            guiGraphics.blit(misc, posX + length + 2, posY - 18, 0, 36, 18, 18);
        }

        if(radiation > 1000) {
            guiGraphics.drawString(mc.font, ">1000 RAD/s", posX, posY - 8, 0xFF0000);
        } else if(radiation >= 1) {
            guiGraphics.drawString(mc.font, ((int) Math.round(radiation)) + " RAD/s", posX, posY - 8, 0xFF0000);
        } else if(radiation > 0) {
            guiGraphics.drawString(mc.font, "<1 RAD/s", posX, posY - 8, 0xFF0000);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static int getScaled(double cur, double max, double scale) {
        return (int) Math.min(cur / max * scale, scale);
    }

    public static void renderCustomCrosshairs(GuiGraphics guiGraphics, Gui gui, Crosshair cross) {
        if(cross == Crosshair.NONE) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        int size = cross.size;
        int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        int centerY = mc.getWindow().getGuiScaledHeight() / 2;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ZERO
        );

        guiGraphics.blit(misc, centerX - size / 2, centerY - size / 2, cross.x, cross.y, size, size);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    public static void renderStingerLockon(GuiGraphics guiGraphics, Gui gui, Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        int centerY = mc.getWindow().getGuiScaledHeight() / 2;

        // Получаем прогресс из ItemGunStinger
        int progress = (int) (ItemGunStinger.lockon * 28);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        guiGraphics.blit(misc, centerX - 15, centerY + 18, 146, 18, 30, 10);
        guiGraphics.blit(misc, centerX - 14, centerY + 19, 147, 29, progress, 8);

        poseStack.popPose();
    }

    public static void renderAmmo(GuiGraphics guiGraphics, ItemStack ammo, int count, int max, int dura, boolean renderCount) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int pX = width / 2 + 62 + 36;
        int pZ = height - 21;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        guiGraphics.blit(misc, pX, pZ + 16, 94, 0, 52, 3);
        guiGraphics.blit(misc, pX + 1, pZ + 16, 95, 3, 50 - dura, 3);

        String cap = max == -1 ? "∞" : "" + max;

        if(renderCount) {
            guiGraphics.drawString(mc.font, count + " / " + cap, pX + 16, pZ + 6, 0xFFFFFF);
        }

        // Рендер предмета
        if(!ammo.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(pX, pZ, 0);
            poseStack.scale(1.0F, 1.0F, 1.0F);

            ItemRenderer itemRenderer = mc.getItemRenderer();
            BakedModel model = itemRenderer.getModel(ammo, mc.level, mc.player, 0);

            guiGraphics.renderItem(ammo, pX, pZ);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public static void renderAmmoAlt(GuiGraphics guiGraphics, ItemStack ammo, int count) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int pX = width / 2 + 62 + 36 + 18;
        int pZ = height - 21 - 16;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        guiGraphics.drawString(mc.font, count + "x", pX + 16, pZ + 6, 0xFFFFFF);

        // Рендер предмета
        if(!ammo.isEmpty()) {
            guiGraphics.renderItem(ammo, pX, pZ);
        }

        poseStack.popPose();
    }

    public static void renderDashBar(GuiGraphics guiGraphics, HbmPlayerProps.IHbmPlayerProps props) {
        Minecraft mc = Minecraft.getInstance();
        int height = mc.getWindow().getGuiScaledHeight();

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();

        int width = 30;
        int posX = 16;
        int posY = height - 40 - 2;

        guiGraphics.blit(misc, posX - 10, posY, 107, 18, 7, 10);

        int stamina = props.getStamina();
        int dashes = props.getTotalDashCount();
        int rows = dashes / 3;
        int finalColumns = dashes % 3;

        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < 3; x++) {
                if(y == rows && x > finalColumns) break;

                guiGraphics.blit(misc, posX + (width + 2) * x, posY - 12 * y, 76, 48, width, 10);

                int staminaDiv = stamina / 30;
                int staminaMod = stamina % 30;
                int barID = (3 * y) + x;
                int barStatus = 1; // 0 = red, 1 = normal, 2 = greyed, 3 = dashed, 4 = ascended
                int barSize = width;

                if(staminaDiv < barID) {
                    barStatus = 3;
                } else if(staminaDiv == barID) {
                    barStatus = 2;
                    barSize = (int)((float)(stamina % 30) * (width / 30F));
                    if(barID == 0) barStatus = 0;
                }

                guiGraphics.blit(misc, posX + (width + 2) * x, posY - 12 * y, 76, 18 + (10 * barStatus), barSize, 10);

                if(staminaDiv == barID && staminaMod >= 27) {
                    fadeOut = 1F;
                }

                if(fadeOut > 0 && staminaDiv - 1 == barID) {
                    RenderSystem.setShaderColor(1F, 1F, 1F, fadeOut);
                    int bar = barID;
                    if(stamina % 30 >= 25) bar++;
                    if(bar / 3 != y) y++;
                    bar = bar % 3;
                    guiGraphics.blit(misc, posX + (width + 2) * bar, posY - 12 * y, 76, 58, width, 10);
                    fadeOut -= 0.04F;
                    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                }
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    public static void renderShieldBar(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;

        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
        if(props == null) return; // Добавь проверку на null

        Font font = mc.font;

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        int left = width / 2 - 91;

        ForgeGui forgeGui = (ForgeGui) mc.gui;
        int top = height - forgeGui.leftHeight;

        guiGraphics.blit(misc, left, top, 146, 0, 81, 9);
        int i = (int) Math.ceil(props.getShield() * 79 / props.getEffectiveMaxShield());
        guiGraphics.blit(misc, left + 1, top, 147, 9, i, 9);

        String label = "" + ((int) (props.getShield() * 10F)) / 10D;
        int textWidth = font.width(label);
        int textX = left + 40 - textWidth / 2;
        int textY = top + 1;

        // Обводка текста
        guiGraphics.drawString(font, label, textX + 1, textY, 0x000000, false);
        guiGraphics.drawString(font, label, textX - 1, textY, 0x000000, false);
        guiGraphics.drawString(font, label, textX, textY + 1, 0x000000, false);
        guiGraphics.drawString(font, label, textX, textY - 1, 0x000000, false);

        // Основной текст
        guiGraphics.drawString(font, label, textX, textY, 0xFFFF80, false);

        forgeGui.leftHeight += 10;
    }

    public static void renderScope(GuiGraphics guiGraphics, ResourceLocation tex) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        double w = screenWidth;
        double h = screenHeight;

        double smallest = Math.min(w, h);
        double divisor = smallest / (9D / 16D);
        smallest = 9D / 16D;
        double largest = Math.max(w, h) / divisor;

        double hMin = h < w ? 0.5 - smallest / 2D : 0.5 - largest / 2D;
        double hMax = h < w ? 0.5 + smallest / 2D : 0.5 + largest / 2D;
        double wMin = w < h ? 0.5 - smallest / 2D : 0.5 - largest / 2D;
        double wMax = w < h ? 0.5 + smallest / 2D : 0.5 + largest / 2D;

        // Рисуем текстуру прицела
        guiGraphics.blit(tex, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}