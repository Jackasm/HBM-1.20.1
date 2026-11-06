package com.hbm.render.block;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityFan;
import com.hbm.blocks.machine.BlockMachineFan;
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
public class RenderFan implements BlockEntityRenderer<TileEntityFan> {

    public RenderFan(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityFan te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Direction facing = te.getBlockState().getValue(BlockMachineFan.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.translate(0, 0.5, 0);

        switch (facing) {
            case DOWN  -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
            case UP    -> { /* 0 */ }
            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case WEST  -> poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            case EAST  -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        }

        poseStack.translate(0, -0.5, 0);

        ResourceLocation tex = HBMResourceManager.fan_tex;

        HBMResourceManager.fan.renderPart(poseStack, buffer, "Frame", tex, packedLight, packedOverlay);

        float rot = te.prevSpin + (te.spin - te.prevSpin) * partialTick;
        poseStack.mulPose(Axis.YP.rotationDegrees(-rot));

        HBMResourceManager.fan.renderPart(poseStack, buffer, "Blades", tex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityFan te) { return true; }

    @Override
    public int getViewDistance() { return 256; }
}