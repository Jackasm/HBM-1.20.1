package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.network.TileEntityRadioTelex;
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
public class RenderTelex implements BlockEntityRenderer<TileEntityRadioTelex> {

    public RenderTelex(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityRadioTelex tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = tile.getBlockState().getValue(BlockDummyable.FACING);
        float rot = switch (facing) {
            case NORTH -> 90F;
            case WEST  -> 180F;
            case SOUTH -> 270F;
            case EAST  -> 0F;
            default -> 0F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        HBMResourceManager.telex.renderAll(poseStack, buffer,
                HBMResourceManager.telex_tex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityRadioTelex tile) {
        return true;
    }
}