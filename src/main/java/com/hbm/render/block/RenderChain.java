package com.hbm.render.block;

import com.hbm.blocks.generic.BlockChain;
import com.hbm.blocks.generic.BlockChain.ChainOrientation;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.model.ModelChain;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderChain implements BlockEntityRenderer<BlockChain.TileEntityChain> {

    private final ModelChain model;

    public RenderChain(BlockEntityRendererProvider.Context ignoredCtx) {
        this.model = new ModelChain();
    }

    @Override
    public void render(@NotNull BlockChain.TileEntityChain te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Level level = te.getLevel();
        if (level == null) return;

        BlockState state = te.getBlockState();
        ChainOrientation orientation = state.getValue(BlockChain.ORIENTATION);
        BlockPos pos = te.getBlockPos();

        boolean hasSupport = BlockChain.hasSupportBelow(level, pos, state);
        ResourceLocation tex = hasSupport
                ? HBMResourceManager.steel_chain_tex
                : HBMResourceManager.steel_chain_end_tex;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        if (!hasSupport) {
            poseStack.translate(0, 0.25F + 0.0625F, 0);
            //poseStack.scale(1F, 0.75F, 1F);
        }

        switch (orientation) {
            case VERTICAL -> model.renderVertical(poseStack, consumer, packedLight, packedOverlay, 1F, 1F, 1F, 1F);
            case WALL_NORTH -> renderWallPlane(poseStack, consumer,   0F, 0F, -1F, packedLight, packedOverlay);
            case WALL_SOUTH -> renderWallPlane(poseStack, consumer, 180F, 0F, +1F, packedLight, packedOverlay);
            case WALL_EAST  -> renderWallPlane(poseStack, consumer,  90F, +1F, 0F, packedLight, packedOverlay);
            case WALL_WEST  -> renderWallPlane(poseStack, consumer, 270F, -1F, 0F, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private void renderWallPlane(PoseStack poseStack, VertexConsumer consumer,
                                 float yRot, float dx, float dz,
                                 int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(dx * 7.0F / 16.0F, 0, dz * 7.0F / 16.0F);  // сдвиг к стене
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));               // ориентация плиты
        model.renderWall(poseStack, consumer, packedLight, packedOverlay, 1F, 1F, 1F, 1F);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockChain.TileEntityChain te) {
        return false;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}