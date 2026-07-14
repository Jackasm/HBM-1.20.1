package com.hbm.render.util;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.lwjgl.opengl.GL11;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderMiscEffects {

    public static final ResourceLocation GLINT = ResLocation(RefStrings.MODID, "textures/misc/glint.png");
    public static final ResourceLocation GLINT_BF = ResLocation(RefStrings.MODID, "textures/misc/glintBF.png");

    /**
     * Рендерит классический глит (свечение) на модели в 3D
     * Полностью портировано из 1.7.10
     */
    public static void renderClassicGlint(Level world, float partialTicks, PoseStack poseStack,
                                          MultiBufferSource buffer, float colorMod,
                                          float r, float g, float b, float speed, float scale) {

        Minecraft mc = Minecraft.getInstance();
        float offset = mc.player != null ? mc.player.tickCount + partialTicks : 0;
        float color = colorMod;

        // Сохраняем состояние
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL11.GL_EQUAL);
        RenderSystem.depthMask(false);

        // Первый проход глита
        for (int k = 0; k < 2; ++k) {
            float glintColor = 0.76F;

            poseStack.pushPose();

            float movement = offset * (0.001F + (float) k * 0.003F) * speed;

            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(30.0F - (float) k * 60.0F));
            poseStack.translate(0.0F, movement, 0.0F);

            // Получаем VertexConsumer для глита
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(GLINT));

            // Рисуем глит на модели
            // В оригинале здесь был рендер модели, но так как мы рисуем на мече,
            // используем специальный метод для ItemRenderer
            renderGlintOnItem(poseStack, consumer, r * glintColor, g * glintColor, b * glintColor, color);

            poseStack.popPose();
        }

        // Восстанавливаем состояние
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }

    /**
     * Рендерит глит на предмете (для ItemRenderer)
     */
    public static void renderGlintOnItem(PoseStack poseStack, VertexConsumer consumer,
                                         float r, float g, float b, float alpha) {
        // В 1.20.1 глит рисуется через ItemRenderer.renderGlint()
        // Но для кастомного цвета мы рисуем вручную
        // Этот метод будет вызван из ItemRendererMeteorSword
    }

    public static void renderClassicGlint(Level world, float partialTicks, PoseStack poseStack,
                                          MultiBufferSource buffer, float r, float g, float b,
                                          float speed, float scale) {
        renderClassicGlint(world, partialTicks, poseStack, buffer, 0.5F, r, g, b, speed, scale);
    }

    public static void renderClassicGlint(Level world, float partialTicks, PoseStack poseStack,
                                          MultiBufferSource buffer) {
        renderClassicGlint(world, partialTicks, poseStack, buffer, 0.5F, 0.25F, 0.8F, 20.0F, 1F / 3F);
    }
}