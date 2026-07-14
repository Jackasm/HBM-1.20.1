package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachinePumpBase;
import com.hbm.tileentity.machine.TileEntityMachinePumpSteam;
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
public class PumpRenderer implements BlockEntityRenderer<TileEntityMachinePumpBase> {

    public PumpRenderer(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityMachinePumpBase pump, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        BlockState state = pump.getBlockState();
        Direction facing = state.getValue(BlockDummyable.FACING);

        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            default -> {}
        }

        float angle = pump.lastRotor + (pump.rotor - pump.lastRotor) * partialTicks;
        boolean isSteam = pump instanceof TileEntityMachinePumpSteam;

        renderCommon(angle, isSteam, poseStack, buffer, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderCommon(float rot, boolean isSteam, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ResourceLocation texture = isSteam ? HBMResourceManager.pump_steam_tex : HBMResourceManager.pump_electric_tex;

        // Base
        HBMResourceManager.pump.renderPart(poseStack, buffer, "Base", texture, combinedLight, combinedOverlay);

        // Rotor
        poseStack.pushPose();
        poseStack.translate(0, 2.25, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rot - 90));
        poseStack.translate(0, -2.25, 0);
        HBMResourceManager.pump.renderPart(poseStack, buffer, "Rotor", texture, combinedLight, combinedOverlay);
        poseStack.popPose();

        double sin = Math.sin(rot * Math.PI / 180D) * 0.5D - 0.5D;
        double cos = Math.cos(rot * Math.PI / 180D) * 0.5D;
        double ang = Math.acos(cos / 2D);
        double cath = Math.sqrt(1 + (cos * cos) / 2);

        // Arms
        poseStack.pushPose();
        poseStack.translate(0, 1 - cath + sin, 0);
        poseStack.translate(0, 4.75, 0);
        poseStack.mulPose(Axis.ZN.rotationDegrees((float) (ang * 180D / Math.PI - 90D)));
        poseStack.translate(0, -4.75, 0);
        HBMResourceManager.pump.renderPart(poseStack, buffer, "Arms", texture, combinedLight, combinedOverlay);
        poseStack.popPose();

        // Piston
        poseStack.pushPose();
        poseStack.translate(0, 1 - cath + sin, 0);
        HBMResourceManager.pump.renderPart(poseStack, buffer, "Piston", texture, combinedLight, combinedOverlay);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachinePumpBase pump) {
        return false;
    }
}