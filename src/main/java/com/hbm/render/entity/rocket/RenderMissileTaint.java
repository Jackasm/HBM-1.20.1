package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMissileTier0.*;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderMissileTaint extends RenderMissileBase {

    public RenderMissileTaint(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Entity entity) {
        if (entity instanceof EntityMissileTaint)            return HBMResourceManager.missileMicroTaint_tex;
        if (entity instanceof EntityMissileBHole)            return HBMResourceManager.missileMicroBHole_tex;
        if (entity instanceof EntityMissileSchrabidium)      return HBMResourceManager.missileMicroSchrab_tex;
        if (entity instanceof EntityMissileEMP)              return HBMResourceManager.missileMicroEMP_tex;
        if (entity instanceof EntityMissileTest)             return HBMResourceManager.missileMicroTest_tex;

        return HBMResourceManager.missileMicro_tex;
    }

    @Override
    protected void renderModel(Entity entity, PoseStack poseStack,
                               VertexConsumer consumer, int packedLight) {
        HBMResourceManager.missileMicro.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }
}