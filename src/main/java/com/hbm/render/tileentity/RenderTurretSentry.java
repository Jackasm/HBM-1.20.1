package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.turret.TileEntityTurretSentry;
import com.hbm.tileentity.turret.TileEntityTurretSentryDamaged;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RenderTurretSentry implements BlockEntityRenderer<TileEntityTurretSentry> {

    public RenderTurretSentry(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(@NotNull TileEntityTurretSentry turret, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Vec3 pos = turret.getHorizontalOffset();

        poseStack.pushPose();
        poseStack.translate(pos.x, 0, pos.z);


        boolean damaged = turret instanceof TileEntityTurretSentryDamaged;

        ResourceLocation texture = damaged ? HBMResourceManager.turret_sentry_damaged_tex : HBMResourceManager.turret_sentry_tex;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        // Base
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer,"Base",  packedLight, packedOverlay);

        double yaw = -Math.toDegrees(turret.lastRotationYaw + (turret.rotationYaw - turret.lastRotationYaw) * partialTick);
        double pitch = Math.toDegrees(turret.lastRotationPitch + (turret.rotationPitch - turret.lastRotationPitch) * partialTick);

        poseStack.mulPose(Axis.YP.rotationDegrees((float) yaw));
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "Pivot", packedLight, packedOverlay);

        poseStack.pushPose();
        poseStack.translate(0, 1.25, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) -pitch));
        poseStack.translate(0, -1.25, 0);
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "Body", packedLight, packedOverlay);
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "Drum", packedLight, packedOverlay);

        // Left barrel
        poseStack.pushPose();
        double leftBarrelOffset = (turret.lastBarrelLeftPos + (turret.barrelLeftPos - turret.lastBarrelLeftPos) * partialTick) * -0.5;
        poseStack.translate(0, 0, leftBarrelOffset);
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "BarrelL", packedLight, packedOverlay);
        poseStack.popPose();

        // Right barrel
        poseStack.pushPose();
        if (damaged) {
            poseStack.translate(0, 1.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            poseStack.translate(0, -1.5, -0.5);
        } else {
            double rightBarrelOffset = (turret.lastBarrelRightPos + (turret.barrelRightPos - turret.lastBarrelRightPos) * partialTick) * -0.5;
            poseStack.translate(0, 0, rightBarrelOffset);
        }
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "BarrelR", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
        poseStack.popPose();
    }



    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityTurretSentry tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}