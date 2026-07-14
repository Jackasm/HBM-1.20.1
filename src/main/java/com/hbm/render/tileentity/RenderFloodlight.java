package com.hbm.render.tileentity;

import com.hbm.blocks.machine.BlockFloodlight;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityFloodlight;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderFloodlight implements BlockEntityRenderer<TileEntityFloodlight> {

    public RenderFloodlight(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityFloodlight floodlight, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        BlockState state = floodlight.getBlockState();
        Direction facing = state.getValue(BlockFloodlight.FACING);

        switch (facing) {
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(0));
            case NORTH -> { poseStack.mulPose(Axis.XP.rotationDegrees(90)); poseStack.mulPose(Axis.ZP.rotationDegrees(180)); }
            case SOUTH -> { poseStack.mulPose(Axis.XP.rotationDegrees(90)); poseStack.mulPose(Axis.ZP.rotationDegrees(0)); }
            case WEST -> { poseStack.mulPose(Axis.XP.rotationDegrees(90)); poseStack.mulPose(Axis.ZP.rotationDegrees(90)); }
            case EAST -> { poseStack.mulPose(Axis.XP.rotationDegrees(90)); poseStack.mulPose(Axis.ZP.rotationDegrees(270)); }
            default -> {}
        }

        poseStack.translate(0, -0.5, 0);

        if (facing != Direction.UP && facing != Direction.DOWN) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }

        // Base
        HBMResourceManager.floodlight.renderPart(poseStack, buffer, "Base",
                HBMResourceManager.floodlight_tex, combinedLight, combinedOverlay);

        float rotation = floodlight.rotation;
        if (facing == Direction.UP) rotation -= 90;
        if (facing == Direction.DOWN) rotation += 90;

        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        poseStack.translate(0, -0.5, 0);

        // Lights
        HBMResourceManager.floodlight.renderPart(poseStack, buffer, "Lights",
                HBMResourceManager.floodlight_tex, combinedLight, combinedOverlay);

        // Lamps (with or without glow)
        if (floodlight.isOn) {
            // Fullbright for lamps
            HBMResourceManager.floodlight.renderPart(poseStack, buffer, "Lamps",
                    HBMResourceManager.floodlight_tex, 0xF000F0, combinedOverlay);
        } else {
            // Dark lamps
            HBMResourceManager.floodlight.renderPart(poseStack, buffer, "Lamps",
                    HBMResourceManager.floodlight_tex, 0, combinedOverlay);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityFloodlight floodlight) {
        return false;
    }
}