package com.hbm.render.block;

import com.hbm.blocks.generic.BlockSpikes;
import com.hbm.main.HBMResourceManager;
import com.hbm.blocks.generic.BlockSpikes.TileEntitySpikes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RenderSpikeBlock implements BlockEntityRenderer<TileEntitySpikes> {

    public RenderSpikeBlock(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(@NotNull TileEntitySpikes tile, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = tile.getBlockState();
        if (!(state.getBlock() instanceof BlockSpikes)) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        HBMResourceManager.spikes.renderAll(
                poseStack, buffer, HBMResourceManager.spikes_tex, packedLight, packedOverlay
        );

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntitySpikes blockEntity) {
        return true;
    }
}