package com.hbm.render.tileentity;

import com.hbm.blocks.machine.BlockFilingCabinet;
import com.hbm.blocks.machine.BlockFilingCabinetSteel;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.storage.TileEntityFileCabinet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class FileCabinetRenderer implements BlockEntityRenderer<TileEntityFileCabinet> {

    public FileCabinetRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntityFileCabinet tile, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        if (HBMResourceManager.file_cabinet == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = tile.getBlockState().getValue(BlockFilingCabinet.FACING);
        switch (facing) {
            case NORTH -> poseStack.mulPose(new Quaternionf().rotationY(0));
            case SOUTH -> poseStack.mulPose(new Quaternionf().rotationY((float) Math.PI));
            case WEST -> poseStack.mulPose(new Quaternionf().rotationY((float) (Math.PI / 2)));
            case EAST -> poseStack.mulPose(new Quaternionf().rotationY((float) (-Math.PI / 2)));
            default -> {}
        }

        ResourceLocation texture;
        if (tile.getBlockState().getBlock() instanceof BlockFilingCabinetSteel) {
            texture = HBMResourceManager.file_cabinet_steel_tex;
        } else {
            texture = HBMResourceManager.file_cabinet_tex;
        }

        HBMResourceManager.file_cabinet.renderPart(
                poseStack, buffer, "Cabinet", texture, combinedLight, combinedOverlay
        );

        // Анимированные ящики с интерполяцией
        if (tile.shouldAnimate()) {
            // Нижний ящик
            poseStack.pushPose();
            float lower = tile.prevLowerExtent + (tile.lowerExtent - tile.prevLowerExtent) * partialTicks;
            poseStack.translate(0, 0, 0.6875f * lower);
            HBMResourceManager.file_cabinet.renderPart(
                    poseStack, buffer, "LowerDrawer", texture, combinedLight, combinedOverlay
            );
            poseStack.popPose();

            // Верхний ящик
            poseStack.pushPose();
            float upper = tile.prevUpperExtent + (tile.upperExtent - tile.prevUpperExtent) * partialTicks;
            poseStack.translate(0, 0, 0.6875f * upper);
            HBMResourceManager.file_cabinet.renderPart(
                    poseStack, buffer, "UpperDrawer", texture, combinedLight, combinedOverlay
            );
            poseStack.popPose();
        } else {
            // Если анимация не нужна, рисуем ящики в закрытом состоянии
            HBMResourceManager.file_cabinet.renderPart(
                    poseStack, buffer, "LowerDrawer", texture, combinedLight, combinedOverlay
            );
            HBMResourceManager.file_cabinet.renderPart(
                    poseStack, buffer, "UpperDrawer", texture, combinedLight, combinedOverlay
            );
        }

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityFileCabinet tile) {
        return true;
    }
}