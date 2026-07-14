package com.hbm.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Интерфейс для блоков с кастомным выделением (подсветкой) при наведении
 */
public interface ICustomBlockHighlight {

    /**
     * Определяет, нужно ли рисовать кастомное выделение для этого блока
     */
    @OnlyIn(Dist.CLIENT)
    boolean shouldDrawHighlight(Level level, BlockPos pos);

    /**
     * Рисует кастомное выделение блока
     * @param poseStack стейк для трансформаций
     * @param level мир
     * @param pos позиция блока
     */
    @OnlyIn(Dist.CLIENT)
    void drawHighlight(PoseStack poseStack, MultiBufferSource bufferSource, Level level, BlockPos pos);

    /**
     * Настройка OpenGL перед рендером выделения
     */
    @OnlyIn(Dist.CLIENT)
    @Deprecated
    static void setup(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.4F);
        RenderSystem.lineWidth(2.0F);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Очистка OpenGL после рендера выделения
     */
    @OnlyIn(Dist.CLIENT)
    @Deprecated
    static void cleanup(PoseStack poseStack) {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    static void renderAABB(PoseStack poseStack, VertexConsumer consumer, AABB aabb, float r, float g, float b, float a) {
        LevelRenderer.renderLineBox(poseStack, consumer, aabb, r, g, b, a);
    }
}