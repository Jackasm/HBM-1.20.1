package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMissileTier3.*;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderMissileHuge extends RenderMissileBase {

    public RenderMissileHuge(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Entity entity) {
        if (entity instanceof EntityMissileInferno) return HBMResourceManager.missileHuge_IN_tex;
        if (entity instanceof EntityMissileRain)    return HBMResourceManager.missileHuge_CL_tex;
        if (entity instanceof EntityMissileDrill)   return HBMResourceManager.missileHuge_BU_tex;

        return HBMResourceManager.missileHuge_HE_tex;
    }

    @Override
    protected void renderModel(Entity entity, PoseStack poseStack,
                               VertexConsumer consumer, int packedLight) {
        HBMResourceManager.missileHuge.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

}