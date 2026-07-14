package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntitySawmill;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SawmillRenderer implements BlockEntityRenderer<TileEntitySawmill> {

    public SawmillRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntitySawmill tile, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Получаем направление из состояния блока
        BlockState state = tile.getBlockState();
        Direction facing = state.getValue(BlockDummyable.FACING).getOpposite();

        // Поворачиваем в зависимости от направления
        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.YN.rotationDegrees(0));
            case EAST -> poseStack.mulPose(Axis.YN.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(Axis.YN.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YN.rotationDegrees(270));
        }

        float rot = tile.lastSpin + (tile.spin - tile.lastSpin) * partialTicks;
        renderCommon(poseStack, buffer, rot, tile.hasBlade, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderCommon(PoseStack poseStack, MultiBufferSource buffer, float rot, boolean hasBlade,
                              int combinedLight, int combinedOverlay) {

        ResourceLocation texture = HBMResourceManager.sawmill_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // Рендерим основную часть
        HBMResourceManager.sawmill.renderPart(poseStack, vertexConsumer, "Main", combinedLight, combinedOverlay);

        if (hasBlade) {
            poseStack.pushPose();
            poseStack.translate(0, 1.375, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rot * 2));
            poseStack.translate(0, -1.375, 0);
            HBMResourceManager.sawmill.renderPart(poseStack, vertexConsumer, "Blade", combinedLight, combinedOverlay);
            poseStack.popPose();
        }

        // Левая шестерня
        poseStack.pushPose();
        poseStack.translate(0.5625, 1.375, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
        poseStack.translate(-0.5625, -1.375, 0);
        HBMResourceManager.sawmill.renderPart(poseStack, vertexConsumer, "GearLeft", combinedLight, combinedOverlay);
        poseStack.popPose();

        // Правая шестерня
        poseStack.pushPose();
        poseStack.translate(-0.5625, 1.375, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-rot));
        poseStack.translate(0.5625, -1.375, 0);
        HBMResourceManager.sawmill.renderPart(poseStack, vertexConsumer, "GearRight", combinedLight, combinedOverlay);
        poseStack.popPose();
    }
}