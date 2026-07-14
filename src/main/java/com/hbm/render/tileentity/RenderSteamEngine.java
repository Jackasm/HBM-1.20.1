package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineSteamEngine;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntitySteamEngine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderSteamEngine implements BlockEntityRenderer<TileEntitySteamEngine> {

    public RenderSteamEngine(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntitySteamEngine engine, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        BlockState state = engine.getBlockState();
        Direction facing = state.getValue(MachineSteamEngine.FACING).getOpposite();

        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            default -> {}
        }

        float rot = engine.lastRotor + (engine.rotor - engine.lastRotor) * partialTicks;

        renderCommon(rot, poseStack, buffer, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderCommon(float rot, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        ResourceLocation texture = HBMResourceManager.steam_engine_tex;

        HBMResourceManager.steam_engine.renderPart(poseStack, buffer, "Base", texture, light, overlay);

        poseStack.pushPose();
        poseStack.translate(2, 1.375, 0);
        poseStack.mulPose(Axis.ZN.rotationDegrees(rot));
        poseStack.translate(-2, -1.375, 0);
        HBMResourceManager.steam_engine.renderPart(poseStack, buffer, "Flywheel", texture, light, overlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 1.375, -0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(rot * 2));
        poseStack.translate(0, -1.375, 0.5);
        HBMResourceManager.steam_engine.renderPart(poseStack, buffer, "Shaft", texture, light, overlay);
        poseStack.popPose();

        double sin = Math.sin(rot * Math.PI / 180D) * 0.25D - 0.25D;
        double cos = Math.cos(rot * Math.PI / 180D) * 0.25D;
        double ang = Math.acos(cos / 1.875D);

        poseStack.pushPose();
        poseStack.translate(sin, cos, 0);
        poseStack.translate(2.25, 1.375, 0);
        poseStack.mulPose(Axis.ZN.rotationDegrees((float) (ang * 180D / Math.PI - 90D)));
        poseStack.translate(-2.25, -1.375, 0);
        HBMResourceManager.steam_engine.renderPart(poseStack, buffer, "Transmission", texture, light, overlay);
        poseStack.popPose();

        double cath = Math.sqrt(3.515625D - (cos * cos) / 2);
        poseStack.translate(1.875 - cath + sin, 0, 0);
        HBMResourceManager.steam_engine.renderPart(poseStack, buffer, "Piston", texture, light, overlay);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntitySteamEngine engine) {
        return false;
    }
}