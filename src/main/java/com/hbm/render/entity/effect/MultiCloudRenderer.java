package com.hbm.render.entity.effect;

import com.hbm.entity.particle.EntityModFX;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

public class MultiCloudRenderer extends EntityRenderer<EntityModFX> {

    private final String type;

    public MultiCloudRenderer(EntityRendererProvider.Context context, String type) {
        super(context);
        this.type = type;
    }

    @Override
    public void render(@NotNull EntityModFX entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        int index = 0;
        if (entity.particleAge <= entity.maxAge && entity.particleAge >= entity.maxAge / 8 * 7) {
            index = 7;
        } else if (entity.particleAge < entity.maxAge / 8 * 7 && entity.particleAge >= entity.maxAge / 8 * 6) {
            index = 6;
        } else if (entity.particleAge < entity.maxAge / 8 * 6 && entity.particleAge >= entity.maxAge / 8 * 5) {
            index = 5;
        } else if (entity.particleAge < entity.maxAge / 8 * 5 && entity.particleAge >= entity.maxAge / 8 * 4) {
            index = 4;
        } else if (entity.particleAge < entity.maxAge / 8 * 4 && entity.particleAge >= entity.maxAge / 8 * 3) {
            index = 3;
        } else if (entity.particleAge < entity.maxAge / 8 * 3 && entity.particleAge >= entity.maxAge / 8 * 2) {
            index = 2;
        } else if (entity.particleAge < entity.maxAge / 8 * 2 && entity.particleAge >= entity.maxAge / 8 * 1) {
            index = 1;
        }

        String path = "textures/particles/" + type + (index + 1) + ".png";
        ResourceLocation texture = ResLocation(RefStrings.MODID, path);

        renderCloud(poseStack, buffer, texture, packedLight, entity);
    }

    private void renderCloud(PoseStack poseStack, MultiBufferSource buffer, ResourceLocation texture, int light, EntityModFX entity) {
        poseStack.pushPose();

        poseStack.translate(0, 0, 0);
        poseStack.scale(3.75F, 3.75F, 3.75F);

        Random entityRand = new Random(entity.hashCode());
        Random rand = new Random(100);

        for (int i = 0; i < 5; i++) {
            double d = entityRand.nextInt(10) * 0.05;
            float gray = (float) (1.0 - d);
            RenderSystem.setShaderColor(gray, gray, gray, 1.0F);

            double dX = (rand.nextGaussian() - 1.0) * 0.15;
            double dY = (rand.nextGaussian() - 1.0) * 0.15;
            double dZ = (rand.nextGaussian() - 1.0) * 0.15;
            double size = rand.nextDouble() * 0.5 + 0.25;

            poseStack.translate(dX, dY, dZ);
            poseStack.scale((float) size, (float) size, (float) size);

            renderQuad(poseStack, buffer, texture, light);

            poseStack.scale(1.0F / (float) size, 1.0F / (float) size, 1.0F / (float) size);
            poseStack.translate(-dX, -dY, -dZ);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private void renderQuad(PoseStack poseStack, MultiBufferSource buffer, ResourceLocation texture, int light) {
        poseStack.pushPose();

        Quaternionf rotation = new Quaternionf().rotationYXZ(
                -Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() * ((float) Math.PI / 180F),
                Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() * ((float) Math.PI / 180F),
                0.0F
        );
        poseStack.mulPose(rotation);

        Matrix4f matrix = poseStack.last().pose();
        Vector3f normal = new Vector3f(0, 0, 1);

        float size = 1.0F;
        float halfSize = size / 2.0F;
        float yOffset = -0.25F;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // Вершина 1 (левый верхний)
        consumer.vertex(matrix, -halfSize, yOffset + size, 0)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();

        // Вершина 2 (правый верхний)
        consumer.vertex(matrix, halfSize, yOffset + size, 0)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();

        // Вершина 3 (правый нижний)
        consumer.vertex(matrix, halfSize, yOffset, 0)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();

        // Вершина 4 (левый нижний)
        consumer.vertex(matrix, -halfSize, yOffset, 0)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityModFX entity) {
        return ResLocation.ResLocation("minecraft", "textures/block/dirt.png");
    }
}