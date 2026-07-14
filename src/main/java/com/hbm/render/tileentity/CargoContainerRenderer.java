package com.hbm.render.tileentity;

import com.hbm.blocks.machine.CargoContainer;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityCargoContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class CargoContainerRenderer implements BlockEntityRenderer<TileEntityCargoContainer> {

    public CargoContainerRenderer(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(@NotNull TileEntityCargoContainer tile, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();

        Direction facing = tile.getBlockState().getValue(CargoContainer.FACING);

        // Поворот и позиционирование как у цистерны
        switch (facing) {
            case NORTH -> {
                poseStack.mulPose(new Quaternionf().rotationY(0));
                poseStack.translate(-1.935, 0, -0.94);
            }
            case SOUTH -> {
                poseStack.mulPose(new Quaternionf().rotationY((float) Math.PI));
                poseStack.translate(- 2.935, 0, - 1.94);
            }
            case WEST -> {
                poseStack.mulPose(new Quaternionf().rotationY((float) (Math.PI / 2)));
                poseStack.translate(-2.935, 0, -0.94);
            }
            case EAST -> {
                poseStack.mulPose(new Quaternionf().rotationY((float) (-Math.PI / 2)));
                poseStack.translate(-1.935, 0, -1.94);
            }
            default -> {}
        }

        ResourceLocation texture = HBMResourceManager.cargo_container_tex;
        HBMResourceManager.cargo_container.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);

        poseStack.popPose();
    }
}