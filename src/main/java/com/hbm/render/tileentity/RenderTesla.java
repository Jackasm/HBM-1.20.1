package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.util.BeamPronter;
import com.hbm.render.util.BeamPronter.EnumBeamType;
import com.hbm.render.util.BeamPronter.EnumWaveType;
import com.hbm.tileentity.machine.TileEntityTesla;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RenderTesla implements BlockEntityRenderer<TileEntityTesla> {

    public RenderTesla(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntityTesla tile, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        HBMResourceManager.tesla.renderAll(poseStack, buffer, HBMResourceManager.tesla_tex, packedLight, packedOverlay);

        double sx = tile.getBlockPos().getX() + 0.5D;
        double sy = tile.getBlockPos().getY() + TileEntityTesla.OFFSET;
        double sz = tile.getBlockPos().getZ() + 0.5D;

        poseStack.translate(0.0D, TileEntityTesla.OFFSET, 0.0D);

        for (double[] target : tile.getTargets()) {
            double length = Math.sqrt(Math.pow(target[0] - sx, 2) +
                    Math.pow(target[1] - sy, 2) +
                    Math.pow(target[2] - sz, 2));

            Vec3 direction = new Vec3(
                    -target[0] + sx,
                    target[1] - sy,
                    -target[2] + sz
            );

            BeamPronter.prontBeam(
                    poseStack,
                    buffer,
                    direction,
                    EnumWaveType.RANDOM,
                    EnumBeamType.SOLID,
                    0x404040,
                    0x404040,
                    (int) Objects.requireNonNull(tile.getLevel()).getGameTime() % 1000 + 1,
                    (int) (length * 5),
                    0.125F,
                    2,
                    0.03125F
            );
        }

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityTesla blockEntity) {
        return true;
    }
}