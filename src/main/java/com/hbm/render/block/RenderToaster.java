package com.hbm.render.block;

import com.hbm.blocks.generic.BlockDecoToaster;
import com.hbm.main.HBMResourceManager;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderToaster implements BlockEntityRenderer<BlockDecoToaster.TileEntityToaster> {

    public RenderToaster(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull BlockDecoToaster.TileEntityToaster tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = tile.getBlockState().getValue(BlockDecoToaster.FACING);
        float rot = switch (facing) {
            case NORTH -> 90F;
            case EAST  -> 0F;
            case SOUTH -> 270F;
            case WEST  -> 180F;
            default -> 0F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        // Текстура зависит от блока (iron / steel / wood)
        ResourceLocation texture = getTexture(tile);

        HBMResourceManager.toaster.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private ResourceLocation getTexture(BlockDecoToaster.TileEntityToaster tile) {
        var block = tile.getBlockState().getBlock();
        if (block == com.hbm.blocks.ModBlocks.DECO_TOASTER_IRON.get())  return HBMResourceManager.toaster_iron_tex;
        if (block == com.hbm.blocks.ModBlocks.DECO_TOASTER_STEEL.get()) return HBMResourceManager.toaster_steel_tex;
        if (block == com.hbm.blocks.ModBlocks.DECO_TOASTER_WOOD.get())  return HBMResourceManager.toaster_wood_tex;
        return HBMResourceManager.toaster_iron_tex;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockDecoToaster.TileEntityToaster tile) {
        return false;
    }
}