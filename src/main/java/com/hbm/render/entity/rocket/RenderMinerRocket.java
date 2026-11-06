package com.hbm.render.entity.rocket;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RenderMinerRocket<T extends Entity> extends EntityRenderer<T> {

    public RenderMinerRocket(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        ResourceLocation texture = HBMResourceManager.miner_rocket_tex;
        HBMResourceManager.miner_rocket.renderAll(poseStack, buffer, texture, packedLight, 0);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return HBMResourceManager.miner_rocket_tex;
    }
}