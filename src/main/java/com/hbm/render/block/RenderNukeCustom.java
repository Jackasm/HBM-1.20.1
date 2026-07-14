package com.hbm.render.block;

import com.hbm.blocks.bomb.NukeCustom;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderNukeCustom implements BlockEntityRenderer<TileEntityNukeCustom> {

    public RenderNukeCustom(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityNukeCustom tile, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = tile.getBlockState().getValue(NukeCustom.FACING);
        float rotation;

        switch (facing) {
            case NORTH:
                rotation = 180;
                poseStack.translate(1, 0, 0);
                break;
            case SOUTH:
                rotation = 0;
                poseStack.translate(-1, 0, 0);
                break;
            case WEST:
                rotation = -90;
                poseStack.translate(0, 0, -1);
                break;
            case EAST:
                rotation = 90;
                poseStack.translate(0, 0, 1);
                break;
            case UP:
            case DOWN:
            default:
                rotation = 0;
                break;
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        ResourceLocation texture = HBMResourceManager.nuke_custom_tex;
        HBMResourceManager.nuke_custom.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
