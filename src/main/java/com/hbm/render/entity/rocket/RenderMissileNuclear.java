package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMissileTier4.*;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderMissileNuclear extends RenderMissileBase {

    public RenderMissileNuclear(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Entity entity) {
        if (entity instanceof EntityMissileMirv)              return HBMResourceManager.missileThermo_tex;
        if (entity instanceof EntityMissileDoomsday)          return HBMResourceManager.missileDoomsday_tex;
        if (entity instanceof EntityMissileDoomsdayRusted)    return HBMResourceManager.missileDoomsdayRusted_tex;
        if (entity instanceof EntityMissileVolcano)           return HBMResourceManager.missileVolcano_tex;

        return HBMResourceManager.missileNuclear_tex;
    }

    @Override
    protected void renderModel(Entity entity, PoseStack poseStack,
                               VertexConsumer consumer, int packedLight) {
        HBMResourceManager.missileNuclear.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }
}