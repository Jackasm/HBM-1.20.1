package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineMicrowave;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMicrowave;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderMicrowave implements BlockEntityRenderer<TileEntityMicrowave> {

    public RenderMicrowave(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityMicrowave tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, -0.785, 0.5);

        Direction facing = tile.getBlockState().getValue(MachineMicrowave.FACING);
        float rot = switch (facing) {
            case EAST -> 270F;
            case SOUTH -> 180F;
            case WEST -> 90F;
            default -> 0F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        poseStack.translate(-0.5, 0, 0.65);
        poseStack.translate(0.0125, 0, 0);

        HBMResourceManager.microwave.renderPart(poseStack, buffer, "mainbody_Cube.001",
                HBMResourceManager.microwave_tex, packedLight, packedOverlay);
        HBMResourceManager.microwave.renderPart(poseStack, buffer, "window_Cube.002",
                HBMResourceManager.microwave_tex, packedLight, packedOverlay);

        double rotPlate = (System.currentTimeMillis() * tile.speed / 10D) % 360;

        if (tile.time > 0) {
            poseStack.pushPose();
            poseStack.translate(0.575, 0, -0.45);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) rotPlate));
            poseStack.translate(-0.575, 0, 0.45);
            HBMResourceManager.microwave.renderPart(poseStack, buffer, "plate_Cylinder",
                    HBMResourceManager.microwave_tex, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            HBMResourceManager.microwave.renderPart(poseStack, buffer, "plate_Cylinder",
                    HBMResourceManager.microwave_tex, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMicrowave tile) {
        return true;
    }
}