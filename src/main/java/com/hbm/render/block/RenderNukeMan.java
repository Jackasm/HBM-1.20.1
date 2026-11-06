package com.hbm.render.block;

import com.hbm.blocks.bomb.BlockNukeMan;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.bomb.TileEntityNukeMan;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderNukeMan implements BlockEntityRenderer<TileEntityNukeMan> {

    public RenderNukeMan(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityNukeMan te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Direction facing = te.getBlockState().getValue(BlockNukeMan.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        switch (facing) {
            case EAST  -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST  -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            default    -> { /* NORTH — 0 */ }
        }

        ResourceLocation tex = HBMResourceManager.bomb_man_tex;
        HBMResourceManager.bomb_man.renderAll(poseStack, buffer, tex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityNukeMan te) { return true; }

    @Override
    public int getViewDistance() { return 256; }
}