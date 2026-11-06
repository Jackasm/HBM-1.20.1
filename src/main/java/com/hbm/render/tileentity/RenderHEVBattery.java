package com.hbm.render.tileentity;

import com.hbm.blocks.generic.HEVBattery;
import com.hbm.main.HBMResourceManager;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderHEVBattery implements BlockEntityRenderer<HEVBattery.TileEntityHEVBattery> {

    public RenderHEVBattery(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull HEVBattery.TileEntityHEVBattery tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        HBMResourceManager.hev_battery.renderAll(poseStack, buffer,
                HBMResourceManager.hev_battery_tex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull HEVBattery.TileEntityHEVBattery tile) {
        return false;
    }
}