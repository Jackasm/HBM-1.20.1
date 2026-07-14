package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineStirling;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityStirling;
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
public class RenderStirling implements BlockEntityRenderer<TileEntityStirling> {

    public RenderStirling(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityStirling stirling, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        BlockState state = stirling.getBlockState();
        Direction facing = state.getValue(MachineStirling.FACING);

        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            default -> {}
        }

        float rot = stirling.lastSpin + (stirling.spin - stirling.lastSpin) * partialTicks;
        boolean hasCog = stirling.hasCog;
        int type = stirling.getGearMeta();

        renderCommon(rot, hasCog, type, poseStack, buffer, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderCommon(float rot, boolean hasCog, int type, PoseStack poseStack,
                              MultiBufferSource buffer, int light, int overlay) {

        ResourceLocation texture = switch (type) {
            case 0 -> HBMResourceManager.stirling_tex;
            case 2 -> HBMResourceManager.stirling_creative_tex;
            default -> HBMResourceManager.stirling_steel_tex;
        };

        HBMResourceManager.stirling.renderPart(poseStack, buffer, "Base", texture, light, overlay);

        if (hasCog) {
            poseStack.pushPose();
            poseStack.translate(0, 1.375, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rot));
            poseStack.translate(0, -1.375, 0);
            HBMResourceManager.stirling.renderPart(poseStack, buffer, "Cog", texture, light, overlay);
            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.translate(0, 1.375, 0.25);
        poseStack.mulPose(Axis.XP.rotationDegrees(rot * 2 + 3));
        poseStack.translate(0, -1.375, -0.25);
        HBMResourceManager.stirling.renderPart(poseStack, buffer, "CogSmall", texture, light, overlay);
        poseStack.popPose();

        poseStack.translate(Math.sin(rot * Math.PI / 90D) * 0.25 + 0.125, 0, 0);
        HBMResourceManager.stirling.renderPart(poseStack, buffer, "Piston", texture, light, overlay);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityStirling stirling) {
        return false;
    }
}