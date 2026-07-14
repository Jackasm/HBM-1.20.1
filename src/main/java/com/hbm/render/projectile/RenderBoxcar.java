package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityBoxcar;
import com.hbm.entity.projectile.EntityBuilding;
import com.hbm.entity.projectile.EntityDuchessGambit;
import com.hbm.entity.projectile.EntityTorpedo;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RenderBoxcar extends EntityRenderer<Entity> {

    public RenderBoxcar(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull Entity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        if (entity instanceof EntityBoxcar) {
            poseStack.translate(0, 0, -1.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));

            HBMResourceManager.boxcar.renderAll(poseStack, buffer, HBMResourceManager.boxcar_tex, packedLight, 0);
        }

        if (entity instanceof EntityDuchessGambit) {
            poseStack.translate(0, 0, -1.0F);

            HBMResourceManager.boxcar.renderAll(poseStack, buffer, HBMResourceManager.boxcar_tex, packedLight, 0);
        }

        if (entity instanceof EntityBuilding) {
            HBMResourceManager.building.renderAll(poseStack, buffer, HBMResourceManager.building_tex, packedLight, 0);
        }

        if (entity instanceof EntityTorpedo torpedo) {
            float f = torpedo.tickCount + partialTicks;
            float rot = Math.min(85, f * 3);
            poseStack.mulPose(Axis.XP.rotationDegrees(rot));

            HBMResourceManager.torpedo.renderAll(poseStack, buffer, HBMResourceManager.torpedo_tex, packedLight, 0);
        }

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        return HBMResourceManager.boxcar_tex;
    }
}