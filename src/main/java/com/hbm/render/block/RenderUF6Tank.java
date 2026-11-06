package com.hbm.render.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.BlockUF6Tank;
import com.hbm.blocks.machine.BlockUF6Tank.TileEntityUF6Tank;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderUF6Tank implements BlockEntityRenderer<TileEntityUF6Tank> {

    public RenderUF6Tank(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityUF6Tank te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Direction facing = te.getBlockState().getValue(BlockUF6Tank.FACING);
        Block block = te.getBlockState().getBlock();

        ResourceLocation tex = (block == ModBlocks.MACHINE_PUF6_TANK.get())
                ? HBMResourceManager.puf6_tex
                : HBMResourceManager.uf6_tex;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        switch (facing) {
            case EAST  -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST  -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            default    -> { /* NORTH — 0 */ }
        }

        HBMResourceManager.uf6_tank.renderAll(poseStack, buffer, tex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityUF6Tank te) { return true; }

    @Override
    public int getViewDistance() { return 128; }
}