package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityHeatBoiler;
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
public class BoilerRenderer implements BlockEntityRenderer<TileEntityHeatBoiler> {

    public BoilerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityHeatBoiler tile, float partialTicks, PoseStack poseStack,
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

        ResourceLocation texture = HBMResourceManager.heat_boiler_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        if (!tile.hasExploded) {
            // Пульсация при высоком заполнении
            if (tile.tanks[1].getFill() > tile.tanks[1].getMaxFill() * 0.9) {
                double sine = Math.sin(System.currentTimeMillis() / 50D % (Math.PI * 2));
                sine *= 0.01D;
                poseStack.scale(1 - (float) sine, 1 + (float) sine, 1 - (float) sine);
            }

            HBMResourceManager.heat_boiler.renderAll(poseStack, vertexConsumer, combinedLight, combinedOverlay);
        } else {
            HBMResourceManager.heat_boiler_burst.renderAll(poseStack, vertexConsumer, combinedLight, combinedOverlay);
        }

        poseStack.popPose();
    }
}