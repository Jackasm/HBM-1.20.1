package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityAshpit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AshpitRenderer implements BlockEntityRenderer<TileEntityAshpit> {

    public AshpitRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityAshpit tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
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
        poseStack.mulPose(Axis.YN.rotationDegrees(-90)); // Дополнительный поворот

        ResourceLocation texture = HBMResourceManager.ashpit_tex;

        // Рендерим основную часть
        HBMResourceManager.heater_oven.renderPart(poseStack, buffer, "Main", texture, combinedLight, combinedOverlay);

        // Рендерим дверь с анимацией
        poseStack.pushPose();
        float door = tile.prevDoorAngle + (tile.doorAngle - tile.prevDoorAngle) * partialTicks;
        poseStack.translate(0, 0, door * 0.75D / 135D);
        HBMResourceManager.heater_oven.renderPart(poseStack, buffer, "Door", texture, combinedLight, combinedOverlay);
        poseStack.popPose();

        // Рендерим внутреннюю часть
        if (tile.isFull) {
            HBMResourceManager.heater_oven.renderPart(poseStack, buffer, "InnerBurning", texture, 0xF000F0, combinedOverlay);
        } else {
            HBMResourceManager.heater_oven.renderPart(poseStack, buffer, "Inner", texture, combinedLight, combinedOverlay);
        }

        poseStack.popPose();
    }
}