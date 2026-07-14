package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMinerRocket;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderMinerRocket extends EntityRenderer<EntityMinerRocket> {

    public RenderMinerRocket(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityMinerRocket entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        ResourceLocation texture = HBMResourceManager.miner_rocket_tex;

        // Масштабирование
        float scale = 1.0F;
        poseStack.scale(scale, scale, scale);

        // Поворот (если нужно)
        // poseStack.mulPose(Axis.YP.rotationDegrees(180));

        HBMResourceManager.miner_rocket.renderAll(poseStack, buffer, texture, packedLight, 0);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityMinerRocket entity) {
        return HBMResourceManager.miner_rocket_tex;
    }
}