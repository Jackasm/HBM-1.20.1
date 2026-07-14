package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityRainbow;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RenderRainbow extends EntityRenderer<EntityRainbow> {

    public RenderRainbow(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityRainbow entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        float radius = 0.12F;
        int distance = 4;

        poseStack.pushPose();

        Vec3 dir = entity.getDeltaMovement().normalize();

        float yaw = (float) Math.atan2(dir.x, dir.z);
        float pitch = (float) Math.asin(-dir.y);
        poseStack.mulPose(new Quaternionf().rotateY(yaw));
        poseStack.mulPose(new Quaternionf().rotateX(pitch));

        boolean red = entity.getColorR() == 1;
        boolean green = entity.getColorG() == 1;
        boolean blue = entity.getColorB() == 1;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f matrix = poseStack.last().pose();

        for (float o = 0; o <= radius; o += radius / 8) {
            float color = 1.0F - (o * 8.333F);
            if (color < 0) color = 0;

            float r = red ? 1.0F : color;
            float g = green ? 1.0F : color;
            float b = blue ? 1.0F : color;

            builder.vertex(matrix, o, -o, 0).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, o, o, 0).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, o, o, distance).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, o, -o, distance).color(r, g, b, 1.0F).endVertex();

            builder.vertex(matrix, -o, -o, 0).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, -o, o, 0).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, -o, o, distance).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, -o, -o, distance).color(r, g, b, 1.0F).endVertex();

            builder.vertex(matrix, -o, o, 0).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, o, o, 0).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, o, o, distance).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, -o, o, distance).color(r, g, b, 1.0F).endVertex();

            builder.vertex(matrix, -o, -o, 0).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, o, -o, 0).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, o, -o, distance).color(r, g, b, 1.0F).endVertex();
            builder.vertex(matrix, -o, -o, distance).color(r, g, b, 1.0F).endVertex();
        }

        BufferUploader.drawWithShader(builder.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityRainbow entity) {
        return null;
    }
}