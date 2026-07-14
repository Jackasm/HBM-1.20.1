package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityZirnoxDestroyed;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.blocks.BlockDummyable.FACING;

@OnlyIn(Dist.CLIENT)
public class RenderZirnoxDestroyed implements BlockEntityRenderer<TileEntityZirnoxDestroyed> {

    public RenderZirnoxDestroyed(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityZirnoxDestroyed te, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5D, 0, 0.5D);

        BlockState state = te.getBlockState();
        Direction dir = state.getValue(FACING);

        switch (dir) {
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            default -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
        }

        HBMResourceManager.zirnox_destroyed.renderAll(poseStack, buffer,
                HBMResourceManager.zirnox_destroyed_tex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityZirnoxDestroyed te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}