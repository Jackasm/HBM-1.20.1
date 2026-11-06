package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMissileTier2.*;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderMissileStrong extends RenderMissileBase {

    public RenderMissileStrong(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Entity entity) {
        if (entity instanceof EntityMissileIncendiaryStrong) return HBMResourceManager.missileStrong_IN_tex;
        if (entity instanceof EntityMissileClusterStrong)    return HBMResourceManager.missileStrong_CL_tex;
        if (entity instanceof EntityMissileBusterStrong)     return HBMResourceManager.missileStrong_BU_tex;
        if (entity instanceof EntityMissileEMPStrong)        return HBMResourceManager.missileStrong_EMP_tex;

        return HBMResourceManager.missileStrong_HE_tex;
    }

    @Override
    protected void renderModel(Entity entity, PoseStack poseStack,
                               VertexConsumer consumer, int packedLight) {
        HBMResourceManager.missileStrong.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected float getScale(Entity entity) {
        return 1.5F;
    }
}