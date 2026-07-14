package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderCloudFleija extends EntityRenderer<EntityCloudFleija> {

    private static final float SCALE_FACTOR = 0.2F;

    public RenderCloudFleija(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityCloudFleija cloud, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight) {

        poseStack.pushPose();

        // Позиционирование
        poseStack.translate(0, 0, 0);

        // Масштабирование на основе возраста
        float scale = cloud.age * SCALE_FACTOR;

        if (partialTicks > 0) {
            scale = (cloud.age + partialTicks) * SCALE_FACTOR;
        }

        poseStack.scale(scale, scale, scale);

        // Получаем текстуру и рендерим модель
        ResourceLocation texture = HBMResourceManager.blast_fleija_tex;
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        // Рендерим сферу
        HBMResourceManager.sphere.renderAll(poseStack, builder, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityCloudFleija cloud) {
        return HBMResourceManager.blast_fleija_tex;
    }

    @Override
    public boolean shouldRender(@NotNull EntityCloudFleija entity, @NotNull Frustum frustum,
                                double camX, double camY, double camZ) {
        return true;
    }
}