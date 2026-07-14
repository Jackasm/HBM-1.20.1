package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineArcWelder;
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
public class RenderArcWelder implements BlockEntityRenderer<TileEntityMachineArcWelder> {

    public RenderArcWelder(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(@NotNull TileEntityMachineArcWelder tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Поворот в зависимости от направления блока
        BlockState state = tile.getBlockState();
        if (state.hasProperty(BlockDummyable.FACING)) {
            Direction facing = state.getValue(BlockDummyable.FACING);
            float rotation = switch (facing) {
                case SOUTH -> 90;
                case WEST -> 180;
                case NORTH -> 270;
                case EAST -> 0;
                default -> 0;
            };
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            switch (facing) {
                case SOUTH:
                case NORTH:
                    poseStack.translate(1, 0, 0);

            }
        }

        poseStack.translate(-0.5, 0, 0);

        // Рендер модели
        HBMResourceManager.arc_welder.renderAll(poseStack, buffer,
                HBMResourceManager.arc_welder_tex, combinedLight, combinedOverlay);

        // Рендер предмета на столе
        ItemStack display = tile.getCurrentDisplay();
        if (display != null && !display.isEmpty()) {
            renderDisplayItem(tile, poseStack, buffer, combinedLight, combinedOverlay, display);
        }

        poseStack.popPose();
    }

    private void renderDisplayItem(TileEntityMachineArcWelder tile, PoseStack poseStack,
                                   MultiBufferSource buffer, int combinedLight, int combinedOverlay,
                                   ItemStack stack) {
        poseStack.pushPose();
        poseStack.translate(0.0625 * 2.5, 1.125, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.scale(1.5f, 1.5f, 1.5f);

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

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineArcWelder tile) {
        return true;
    }
}