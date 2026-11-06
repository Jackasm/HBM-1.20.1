package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;

import com.hbm.tileentity.block.TileEntitySkeletonHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class RenderSkeletonHolder implements BlockEntityRenderer<TileEntitySkeletonHolder> {

    public RenderSkeletonHolder(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(@NotNull TileEntitySkeletonHolder te, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Поворот в зависимости от facing блока
        BlockState state = te.getBlockState();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            float rotation = switch (facing) {
                case SOUTH -> 180;
                case WEST -> 270;
                case EAST -> 90;
                default -> 0;
            };
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
        }

        // Рендерим модель Holder1
        var consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.skeleton_holder_tex));
        HBMResourceManager.skeleton_holder.renderPart(poseStack, consumer, "Holder1", packedLight, OverlayTexture.NO_OVERLAY);

        // Рендерим предмет, если есть
        ItemStack stack = te.getItem();
        if (!stack.isEmpty()) {
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
            poseStack.translate(0, 0.125, 0);

            // Масштабируем предмет
            if (stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                // Проверяем, рендерится ли блок в 3D
                if (!Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(block.defaultBlockState()).isCustomRenderer()) {
                    poseStack.scale(1.5F, 1.5F, 1.5F);
                }
            } else {
                poseStack.scale(1.5F, 1.5F, 1.5F);
            }

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, te.getLevel(), 0);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntitySkeletonHolder te) {
        return true;
    }

}