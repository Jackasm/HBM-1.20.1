package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.turret.TileEntityTurretHoward;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RenderTurretHoward implements BlockEntityRenderer<TileEntityTurretHoward> {

    public RenderTurretHoward(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(@NotNull TileEntityTurretHoward turret, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        poseStack.translate(0.5, 0, 0.5);


        // Получаем направление из состояния блока
        BlockState state = turret.getBlockState();
        Direction facing = state.getValue(BlockDummyable.FACING);

        // Поворачиваем в зависимости от направления
        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.YN.rotationDegrees(0));
            case EAST -> poseStack.mulPose(Axis.YN.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(Axis.YN.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YN.rotationDegrees(270));
        }
        poseStack.translate(1, 0, 1);

        poseStack.translate(-0.5, 0, -0.5);


        ResourceLocation texture = HBMResourceManager.turret_howard_tex;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        // Base
        ResourceLocation baseTex = HBMResourceManager.turret_base_tex;
        VertexConsumer baseConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(baseTex));
        HBMResourceManager.turret_chekhov.renderPart(poseStack, baseConsumer, "Base", packedLight, packedOverlay);

        double yaw = -Math.toDegrees(turret.lastRotationYaw + (turret.rotationYaw - turret.lastRotationYaw) * partialTick) - 180D;
        double pitch = Math.toDegrees(turret.lastRotationPitch + (turret.rotationPitch - turret.lastRotationPitch) * partialTick);

        poseStack.mulPose(Axis.YP.rotationDegrees((float) yaw));

        ResourceLocation carriageTex = HBMResourceManager.turret_carriage_ciws_tex;
        VertexConsumer carriageConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(carriageTex));
        HBMResourceManager.turret_howard.renderPart(poseStack, carriageConsumer, "Carriage", packedLight, packedOverlay);

        poseStack.translate(0, 2.25, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) pitch));
        poseStack.translate(0, -2.25, 0);

        VertexConsumer bodyConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        HBMResourceManager.turret_howard.renderPart(poseStack, bodyConsumer, "Body", packedLight, packedOverlay);

        float rot = turret.lastSpin + (turret.spin - turret.lastSpin) * partialTick;

        ResourceLocation barrelsTex = HBMResourceManager.turret_howard_barrels_tex;
        VertexConsumer barrelsConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(barrelsTex));

        // BarrelsTop
        poseStack.pushPose();
        poseStack.translate(0, 2.5, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-rot));
        poseStack.translate(0, -2.5, 0);
        HBMResourceManager.turret_howard.renderPart(poseStack, barrelsConsumer, "BarrelsTop", packedLight, packedOverlay);
        poseStack.popPose();

        // BarrelsBottom
        poseStack.pushPose();
        poseStack.translate(0, 2, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(rot));
        poseStack.translate(0, -2, 0);
        HBMResourceManager.turret_howard.renderPart(poseStack, barrelsConsumer, "BarrelsBottom", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityTurretHoward tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}