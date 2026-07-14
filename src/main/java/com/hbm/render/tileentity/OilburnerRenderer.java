package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityHeaterOilburner;
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
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class OilburnerRenderer implements BlockEntityRenderer<TileEntityHeaterOilburner> {

    public OilburnerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityHeaterOilburner tile, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
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

        ResourceLocation texture = HBMResourceManager.heater_oilburner_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // Рендерим модель
        HBMResourceManager.heater_oilburner.renderAll(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();
    }
}