package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;

import com.hbm.tileentity.machine.TileEntityMachineSolderingStation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderSolderingStation implements BlockEntityRenderer<TileEntityMachineSolderingStation> {

    public RenderSolderingStation(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntityMachineSolderingStation tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Поворот в зависимости от направления блока
        BlockState state = tile.getBlockState();
        if (state.hasProperty(BlockDummyable.FACING)) {
            Direction facing = state.getValue(BlockDummyable.FACING);
            float rotation = switch (facing) {
                case SOUTH -> 0;
                case WEST -> 90;
                case NORTH -> 180;
                case EAST -> 270;
                default -> 0;
            };
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            switch (facing) {
                case SOUTH:
                case NORTH:
                    poseStack.translate(-1, 0, -1);
                case EAST:
                case WEST:
                    poseStack.translate(1, 0, 0);
                default:
            }
        }


        poseStack.translate(-0.5, 0, 0.5);

        // Рендер модели
        HBMResourceManager.soldering_station.renderAll(poseStack, buffer,
                HBMResourceManager.soldering_station_tex, combinedLight, combinedOverlay);

        // Рендер предмета на столе
        if (tile.getCurrentDisplay() != null && !tile.getCurrentDisplay().isEmpty()) {
            renderDisplayItem(tile, poseStack, buffer, combinedLight, combinedOverlay);
        }

        poseStack.popPose();
    }

    private void renderDisplayItem(TileEntityMachineSolderingStation tile, PoseStack poseStack,
                                   MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.0625 * 2.5, 1.125, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.scale(1.5f, 1.5f, 1.5f);

        ItemStack stack = tile.getCurrentDisplay().copy();
        if (!stack.isEmpty()) {
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    net.minecraft.world.item.ItemDisplayContext.FIXED,
                    combinedLight,
                    combinedOverlay,
                    poseStack,
                    buffer,
                    tile.getLevel(),
                    0
            );
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineSolderingStation tile) {
        return true;
    }
}