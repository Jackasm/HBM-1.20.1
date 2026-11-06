package com.hbm.render.block;

import com.hbm.blocks.machine.BlockMachineFunnel;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineFunnel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RenderFunnel implements BlockEntityRenderer<TileEntityMachineFunnel> {

    public RenderFunnel(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntityMachineFunnel tile, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = tile.getBlockState();
        if (!(state.getBlock() instanceof BlockMachineFunnel)) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = state.getValue(BlockMachineFunnel.FACING);
        float rotation = switch (facing) {
            case SOUTH -> 0;
            case WEST -> 90;
            case NORTH -> 180;
            case EAST -> -90;
            default -> 0;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        HBMResourceManager.funnel.renderAll(
                poseStack, buffer, HBMResourceManager.funnel_tex, packedLight, packedOverlay
        );

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineFunnel blockEntity) {
        return true;
    }
}