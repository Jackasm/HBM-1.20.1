package com.hbm.render.tileentity;

import com.hbm.blocks.generic.BlockPedestal.TileEntityPedestal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderPedestalTile implements BlockEntityRenderer<TileEntityPedestal> {

    public RenderPedestalTile(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityPedestal pedestal, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        ItemStack stack = pedestal.item;
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.0, 0.5);
        poseStack.scale(1.5F, 1.5F, 1.5F);

        var player = Minecraft.getInstance().player;
        boolean is3D = stack.getItem() instanceof net.minecraft.world.item.BlockItem bi
                && bi.getBlock().getRenderShape(bi.getBlock().defaultBlockState()) != net.minecraft.world.level.block.RenderShape.INVISIBLE;

        float yBob = (float) Math.sin((player != null ? player.tickCount + partialTicks : 0) * 0.1) * 0.0625F;

        if (!is3D) {
            poseStack.translate(0, 0.125 + yBob, 0);
            if (player != null) {
                float yaw = player.yRotO + (player.getYRot() - player.yRotO) * partialTicks + 180;
                poseStack.mulPose(Axis.YN.rotationDegrees(yaw));
            }
        } else {
            poseStack.translate(0, yBob + 0.0625, 0);
        }

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        renderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, pedestal.getLevel(), 0);

        poseStack.popPose();
    }
}