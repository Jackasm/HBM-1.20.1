package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.util.DiamondPronter;
import com.hbm.tileentity.storage.TileEntityBarrel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BarrelRenderer implements BlockEntityRenderer<TileEntityBarrel> {

    public BarrelRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntityBarrel tile, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        ResourceLocation texture;
        Block block = tile.getBlockState().getBlock();
        if (block == ModBlocks.BARREL_IRON.get()) {
            texture = HBMResourceManager.barrel_iron_tex;
        } else if (block == ModBlocks.BARREL_STEEL.get()) {
            texture = HBMResourceManager.barrel_steel_tex;
        }else if (block == ModBlocks.BARREL_PLASTIC.get()) {
            texture = HBMResourceManager.barrel_plastic_tex;
        }else if (block == ModBlocks.BARREL_CORRODED.get()) {
            texture = HBMResourceManager.barrel_corroded_tex;
        }else if (block == ModBlocks.BARREL_TCALLOY.get()) {
            texture = HBMResourceManager.barrel_tcalloy_tex;
        }else {
            texture = HBMResourceManager.barrel_antimatter_tex;
        }

        HBMResourceManager.barrel.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);

        FluidTypeHBM fluidType = tile.tank.getTankType();

        if (fluidType != null && fluidType != Fluids.NONE.get()) {
            int poison = fluidType.getPoison();
            int flammability = fluidType.getFlammability();
            int reactivity = fluidType.getReactivity();
            var symbol = fluidType.getSymbol();

            poseStack.pushPose();

            poseStack.translate(0.0, 0.5, 0.0);
            poseStack.scale(0.4f, 0.4f, 0.4f);

            for (int i = 0; i < 4; i++) {
                poseStack.pushPose();

                poseStack.translate(-0.5, 0.7, -0.94);

                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));

                DiamondPronter.renderNFPA(poseStack, buffer,
                        poison, flammability, reactivity, symbol,
                        combinedLight, combinedOverlay);

                poseStack.popPose();

                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
            }

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}