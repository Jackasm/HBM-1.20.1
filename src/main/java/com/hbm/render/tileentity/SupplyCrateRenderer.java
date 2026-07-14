package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.storage.TileEntitySupplyCrate;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public class SupplyCrateRenderer implements BlockEntityRenderer<TileEntitySupplyCrate> {

    public SupplyCrateRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntitySupplyCrate tile, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        if (HBMResourceManager.supply_crate == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        ResourceLocation texture = HBMResourceManager.supply_crate_tex;


        HBMResourceManager.supply_crate.renderAll(
                poseStack, buffer, texture, combinedLight, combinedOverlay
        );
        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

}