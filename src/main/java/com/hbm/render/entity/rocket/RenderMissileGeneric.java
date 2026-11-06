package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMissileAntiBallistic;
import com.hbm.entity.missile.EntityMissileStealth;
import com.hbm.entity.missile.EntityMissileTier1.*;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderMissileGeneric extends RenderMissileBase {

    public RenderMissileGeneric(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Entity entity) {
        if (entity instanceof EntityMissileAntiBallistic) return HBMResourceManager.missileAA_tex;
        if (entity instanceof EntityMissileStealth)       return HBMResourceManager.missileStealth_tex;

        if (entity instanceof EntityMissileGeneric)           return HBMResourceManager.missileV2_HE_tex;
        if (entity instanceof EntityMissileIncendiary)        return HBMResourceManager.missileV2_IN_tex;
        if (entity instanceof EntityMissileCluster)           return HBMResourceManager.missileV2_CL_tex;
        if (entity instanceof EntityMissileBunkerBuster)      return HBMResourceManager.missileV2_BU_tex;
        if (entity instanceof EntityMissileDecoy)             return HBMResourceManager.missileV2_decoy_tex;

        return HBMResourceManager.missileV2_HE_tex;
    }

    @Override
    protected void renderModel(Entity entity, PoseStack poseStack,
                               VertexConsumer consumer, int packedLight) {
        if (entity instanceof EntityMissileAntiBallistic) {
            HBMResourceManager.missileABM.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        } else if (entity instanceof EntityMissileStealth) {
            HBMResourceManager.missileStealth.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        } else {
            HBMResourceManager.missileV2.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}