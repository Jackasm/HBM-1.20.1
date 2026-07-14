package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachinePressBlock;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachinePress;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PressRenderer implements BlockEntityRenderer<TileEntityMachinePress> {

    public PressRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntityMachinePress tile, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = tile.getBlockState().getValue(MachinePressBlock.FACING);

        if (facing != Direction.NORTH) {
            float rotation = getRotationFromFacing(facing);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        }

        ResourceLocation texture = HBMResourceManager.press_body_tex;
        HBMResourceManager.press_body.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);

        renderPressHead(tile, partialTicks, poseStack, buffer, combinedLight, combinedOverlay);

        poseStack.popPose();

        ItemStack processingItem = tile.getInventory().getStackInSlot(2);
        if (!processingItem.isEmpty()) {
            renderItem(tile, poseStack, buffer, combinedLight, combinedOverlay, processingItem, facing);
        } else {
            processingItem = tile.getInventory().getStackInSlot(3);
            if (!processingItem.isEmpty()) renderItem(tile, poseStack, buffer, combinedLight, combinedOverlay, processingItem, facing);
        }
    }

    private void renderPressHead(TileEntityMachinePress tile, float partialTicks, PoseStack poseStack,
                                 MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        double interpolatedPress = tile.getInterpolatedPress(partialTicks);

        double p = interpolatedPress / (double) TileEntityMachinePress.maxPress;
        double offset = Math.max(0, Math.min(1, 1 - p)) * 0.875D;

        poseStack.pushPose();

        poseStack.translate(0, offset, 0);
        poseStack.scale(0.99f, 1.0f, 0.99f);

        ResourceLocation texture = HBMResourceManager.press_head_tex;
        HBMResourceManager.press_head.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderItem(TileEntityMachinePress tile, PoseStack poseStack,
                            MultiBufferSource buffer, int combinedLight, int combinedOverlay, ItemStack processingItem, Direction facing) {
        poseStack.pushPose();

        poseStack.translate(0.5, 1.0, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        float itemRotation = getRotationFromFacing(facing);
        poseStack.mulPose(Axis.ZP.rotationDegrees(itemRotation));

        poseStack.scale(0.5f, 0.5f, 0.5f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                processingItem,
                ItemDisplayContext.FIXED,
                combinedLight,
                combinedOverlay,
                poseStack,
                buffer,
                tile.getLevel(),
                0
        );

        poseStack.popPose();
    }

    private float getRotationFromFacing(Direction facing) {
        return switch (facing) {
            case SOUTH -> 180.0f;
            case WEST -> -90.0f;
            case EAST -> 90.0f;
            default -> 0.0f;
        };
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachinePress tile) {
        return true;
    }

}