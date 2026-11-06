package com.hbm.render.tileentity;

import com.hbm.blocks.machine.Charger;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityCharger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderCharger implements BlockEntityRenderer<TileEntityCharger> {

    public RenderCharger(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityCharger tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = tile.getBlockState().getValue(Charger.FACING);
        float rot = switch (facing) {
            case SOUTH -> 270;
            case NORTH -> 90;
            case WEST  -> 180;
            default    -> 0;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        double time = (tile.lastUsingTicks + (tile.usingTicks - tile.lastUsingTicks) * partialTicks) / (double) TileEntityCharger.delay;
        double extend = Math.min(1, time * 2);
        double swivel = Math.max(0, (time - 0.5) * 2);

        // Base
        HBMResourceManager.charger.renderPart(poseStack, buffer,"Base",
                HBMResourceManager.charger_tex, combinedLight, combinedOverlay);

        // Left / Right со swivel
        poseStack.pushPose();
        poseStack.translate(0, 0.28, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float)(30 * swivel)));
        poseStack.translate(0, -0.28, 0);
        HBMResourceManager.charger.renderPart(poseStack, buffer,"Left",
                HBMResourceManager.charger_tex, combinedLight, combinedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 0.28, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float)(-30 * swivel)));
        poseStack.translate(0, -0.28, 0);
        HBMResourceManager.charger.renderPart(poseStack, buffer,"Right",
                HBMResourceManager.charger_tex, combinedLight, combinedOverlay);
        poseStack.popPose();

        // Light — полная яркость
        HBMResourceManager.charger.renderPart(poseStack, buffer,"Light",
                HBMResourceManager.charger_tex, LightTexture.FULL_BRIGHT, combinedOverlay);

        // Slide со сдвигом extend
        poseStack.pushPose();
        poseStack.translate(-0.34375, 0.25, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(10));
        poseStack.translate(0.34375, -0.25, 0);
        poseStack.translate(0, -0.25 * extend, 0);
        HBMResourceManager.charger.renderPart(poseStack, buffer,"Slide",
                HBMResourceManager.charger_tex, combinedLight, combinedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityCharger tile) {
        return true;
    }
}