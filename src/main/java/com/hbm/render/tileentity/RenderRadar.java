package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineRadarNT;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class RenderRadar implements BlockEntityRenderer<TileEntityMachineRadarNT> {

    public RenderRadar(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityMachineRadarNT radar, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));

        // Base
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.radar_base_tex));
        HBMResourceManager.radar.renderPart(poseStack, consumer, "Base", packedLight, packedOverlay);

        // Dish (вращается)
        float rotation = radar.prevRotation + (radar.rotation - radar.prevRotation) * partialTicks;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-rotation));
        poseStack.translate(-0.125, 0, 0);

        consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(HBMResourceManager.radar_dish_tex));
        HBMResourceManager.radar.renderPart(poseStack, consumer, "Dish", packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineRadarNT radar) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}