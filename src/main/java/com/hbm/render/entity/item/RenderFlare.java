package com.hbm.render.entity.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Random;

public class RenderFlare extends EntityRenderer<Entity> {

    private final Random random = new Random();

    public RenderFlare(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull Entity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight) {

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        poseStack.pushPose();

        float age = (entity.tickCount + partialTicks) / 200.0F;
        float fade = 0.0F;
        int count = 250;

        if (entity.tickCount < 250) {
            count = entity.tickCount * 3;
        }

        if (age > 0.8F) {
            fade = (age - 0.8F) / 0.2F;
        }

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder vertexBuffer = tesselator.getBuilder();

        random.setSeed(432L);

        for (int i = 0; i < count; i++) {
            poseStack.pushPose();

            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + age * 90.0F));

            float size = random.nextFloat() * 20.0F + 5.0F + fade * 10.0F;
            float width = random.nextFloat() * 2.0F + 1.0F + fade * 2.0F;



            Matrix4f matrix = poseStack.last().pose();

            vertexBuffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

            float alpha = 1.0F - fade;
            float alphaZero = 0.0F;

            for (int t = 0; t < 3; t++) {
                vertexBuffer.vertex(matrix, 0, 0, 0)
                        .color(1.0F, 1.0F, 1.0F, alpha)
                        .endVertex();

                switch (t) {
                    case 0:
                        vertexBuffer.vertex(matrix, -0.866F * width, size, -0.5F * width)
                                .color(1.0F, 1.0F, 1.0F, alphaZero)
                                .endVertex();
                        vertexBuffer.vertex(matrix, 0.866F * width, size, -0.5F * width)
                                .color(1.0F, 1.0F, 1.0F, alphaZero)
                                .endVertex();
                        break;
                    case 1:
                        vertexBuffer.vertex(matrix, 0.866F * width, size, -0.5F * width)
                                .color(1.0F, 1.0F, 1.0F, alphaZero)
                                .endVertex();
                        vertexBuffer.vertex(matrix, 0, size, 1.0F * width)
                                .color(1.0F, 1.0F, 1.0F, alphaZero)
                                .endVertex();
                        break;
                    case 2:
                        vertexBuffer.vertex(matrix, 0, size, 1.0F * width)
                                .color(1.0F, 1.0F, 1.0F, alphaZero)
                                .endVertex();
                        vertexBuffer.vertex(matrix, -0.866F * width, size, -0.5F * width)
                                .color(1.0F, 1.0F, 1.0F, alphaZero)
                                .endVertex();
                        break;
                }
            }

            tesselator.end();

            poseStack.scale(0.99F, 0.99F, 0.99F);

            poseStack.popPose();
        }

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(@NotNull Entity entity, @NotNull Frustum frustum,
                                double camX, double camY, double camZ) {
        return true;
    }
}