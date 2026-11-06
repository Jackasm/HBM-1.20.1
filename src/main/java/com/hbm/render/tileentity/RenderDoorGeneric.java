package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.render.tileentity.door.IRenderDoors;
import com.hbm.tileentity.DoorDecl;
import com.hbm.tileentity.TileEntityDoorGeneric;
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
public class RenderDoorGeneric implements BlockEntityRenderer<TileEntityDoorGeneric> {

    public RenderDoorGeneric(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        DoorDecl decl = door.getDoorType();
        if (decl == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = door.getBlockState().getValue(BlockDummyable.FACING);
        float yaw = switch (facing) {
            case NORTH -> 90F;
            case WEST  -> 180F;
            case SOUTH -> 270F;
            case EAST  -> 0F;
            default -> 0F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        IRenderDoors sednaRenderer = decl.getSEDNARenderer();
        if (sednaRenderer != null) {
            sednaRenderer.render(door, poseStack, buffer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityDoorGeneric door) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}