package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityCloudTom;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RenderCloudTom extends EntityRenderer<EntityCloudTom> {

    public RenderCloudTom(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityCloudTom entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        double scale = entity.age + partialTicks;
        int segments = 16;
        float angle = (float) Math.toRadians(360.0 / segments);
        int height = 20;
        int depth = 20;


        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity)));

        float movement = -(Minecraft.getInstance().player.tickCount + partialTicks) * 0.005F * 10;

        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < segments; i++) {
            for (int j = 0; j < 5; j++) {
                double mod = 1 - j * 0.025;
                double h = height + j * 10;
                double off = 1.0 / Math.max(j, 1);

                Vec3 vec = new Vec3(scale, 0, 0);
                vec = vec.yRot(angle * i);
                double x0 = vec.x * mod;
                double z0 = vec.z * mod;

                // Вершина 1
                vertex(consumer, matrix, x0, h, z0, 0, (float)(1 + off), 0.0F, 0.0F, 0.0F, 1.0F);
                // Вершина 2
                vertex(consumer, matrix, x0, -depth, z0, 0, (float)(0 + off), 1.0F, 1.0F, 1.0F, 1.0F);

                vec = vec.yRot(angle);
                x0 = vec.x * mod;
                z0 = vec.z * mod;

                // Вершина 3
                vertex(consumer, matrix, x0, -depth, z0, 1, (float)(0 + off), 1.0F, 1.0F, 1.0F, 1.0F);
                // Вершина 4
                vertex(consumer, matrix, x0, h, z0, 1, (float)(1 + off), 0.0F, 0.0F, 0.0F, 1.0F);
            }
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private void vertex(VertexConsumer consumer, Matrix4f matrix, double x, double y, double z,
                        float u, float v, float r, float g, float b, float a) {
        consumer.vertex(matrix, (float) x, (float) y, (float) z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(0, 0)
                .uv2(0xF000F0)
                .normal(0, 0, 0)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityCloudTom entity) {
        return HBMResourceManager.tomblast_tex;
    }
}