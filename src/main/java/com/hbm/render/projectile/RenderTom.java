package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityTom;
import com.hbm.render.util.TomPronter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderTom extends EntityRenderer<EntityTom> {

    public RenderTom(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityTom entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, -50, 0);

        TomPronter.prontTom(poseStack, buffer, packedLight);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityTom entity) {
        return null;
    }
}