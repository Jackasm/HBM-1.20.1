package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineMiniRTG;
import com.hbm.util.Library;
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
public class RenderMiniRTG implements BlockEntityRenderer<TileEntityMachineMiniRTG> {

    public RenderMiniRTG(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityMachineMiniRTG tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        var block = tile.getBlockState().getBlock();
        ResourceLocation texture = (block == ModBlocks.MACHINE_POWERRTG.get())
                ? HBMResourceManager.rtg_polonium_tex
                : HBMResourceManager.rtg_cell_tex;

        HBMResourceManager.rtg.renderPart(poseStack, buffer, "Gen",
                texture, packedLight, packedOverlay);

        var pos = tile.getBlockPos();
        var level = tile.getLevel();
        if (level == null) {
            poseStack.popPose();
            return;
        }

        if (Library.canConnect(level, pos.offset(1, 0, 0), Direction.EAST))
            HBMResourceManager.rtg.renderPart(poseStack, buffer, "Connector",
                    texture, packedLight, packedOverlay);

        if (Library.canConnect(level, pos.offset(-1, 0, 0), Direction.WEST)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            HBMResourceManager.rtg.renderPart(poseStack, buffer, "Connector",
                    texture, packedLight, packedOverlay);
            poseStack.mulPose(Axis.YP.rotationDegrees(-180));
        }

        if (Library.canConnect(level, pos.offset(0, 0, -1), Direction.NORTH)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            HBMResourceManager.rtg.renderPart(poseStack, buffer, "Connector",
                    texture, packedLight, packedOverlay);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        }

        if (Library.canConnect(level, pos.offset(0, 0, 1), Direction.SOUTH)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            HBMResourceManager.rtg.renderPart(poseStack, buffer, "Connector",
                    texture, packedLight, packedOverlay);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineMiniRTG tile) {
        return true;
    }
}