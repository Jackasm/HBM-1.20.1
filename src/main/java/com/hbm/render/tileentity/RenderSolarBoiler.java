package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.config.ClientConfig;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.util.BeamPronter;
import com.hbm.tileentity.machine.TileEntitySolarBoiler;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RenderSolarBoiler implements BlockEntityRenderer<TileEntitySolarBoiler> {

    public RenderSolarBoiler(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntitySolarBoiler te, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Level level = te.getLevel();
        if (level == null) return;

        BlockPos pos = te.getBlockPos();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0, 0.5D);

        // Поворот в зависимости от направления
        Direction facing = Direction.NORTH;
        if (level.getBlockState(pos).getBlock() instanceof BlockDummyable dummy) {
            facing = level.getBlockState(pos).getValue(BlockDummyable.FACING);
        }

        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));

        switch (facing) {
            case NORTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
            case EAST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(0));
            case WEST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270));
            default -> {}
        }

        // Рендерим модель базы
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.solar_boiler_tex));
        HBMResourceManager.solar_boiler.renderPart(poseStack, consumer, "Base", packedLight, packedOverlay);

        poseStack.popPose();

        // Лучи от гелиостатов
        if (Minecraft.getInstance().options.graphicsMode().get().getId() > 0) {
            renderBeams(te, poseStack, buffer, partialTicks, packedOverlay);
        }

        poseStack.popPose();
    }

    private void renderBeams(TileEntitySolarBoiler te, PoseStack poseStack,
                             MultiBufferSource buffer, float partialTicks, int packedOverlay) {

        Level level = te.getLevel();
        if (level == null) return;

        var secondaries = te.getSecondaries();
        if (secondaries == null || secondaries.isEmpty()) return;

        int beamCount = 0;
        int beamLimit = ClientConfig.RENDER_HELIOSTAT_BEAM_LIMIT;

        for (BlockPos secondary : secondaries) {
            beamCount++;
            if (beamCount > beamLimit) break;

            int dx = te.getBlockPos().getX() - secondary.getX();
            int dy = te.getBlockPos().getY() - secondary.getY();
            int dz = te.getBlockPos().getZ() - secondary.getZ();

            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 1) continue;

            float min = 0.005F;
            float max = 0.01F;

            poseStack.pushPose();

            poseStack.translate(-dx, -dy, -dz);

            double pitch = Math.toDegrees(-Math.asin((dy + 0.5) / dist)) + 90;
            double yaw = Math.toDegrees(-Math.atan2(dz, dx)) + 180;

            poseStack.translate(0, 1, 0);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees((float) yaw));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float) pitch));
            poseStack.translate(0, -1, 0);

            Matrix4f matrix = poseStack.last().pose();

            VertexConsumer beamConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(BeamPronter.WHITE_TEXTURE));

            float thickness = 0.0625F;
            int layers = 4;

            float yTop = 1.0625F;
            float yBottom = (float) dist;

            for (int layer = 1; layer <= layers; layer++) {
                float inter = (float) (layer - 1) / (float) (layers - 1);
                float radius = thickness / layers * layer;

                float alphaTop = max * (1 - inter * 0.5F);
                float alphaBottom = min * (1 - inter * 0.5F);

                drawBeamQuad(beamConsumer, matrix, packedOverlay,
                        radius, yTop, radius,      // верх: x, y, z
                        radius, yBottom, radius,   // низ: x, y, z
                        1.0F, 1.0F, 1.0F, alphaTop, alphaBottom);

                drawBeamQuad(beamConsumer, matrix, packedOverlay,
                        -radius, yTop, -radius,
                        -radius, yBottom, -radius,
                        1.0F, 1.0F, 1.0F, alphaTop, alphaBottom);

                drawBeamQuad(beamConsumer, matrix, packedOverlay,
                        radius, yTop, radius,
                        radius, yBottom, radius,
                        1.0F, 1.0F, 1.0F, alphaTop, alphaBottom);

                drawBeamQuad(beamConsumer, matrix, packedOverlay,
                        -radius, yTop, -radius,
                        -radius, yBottom, -radius,
                        1.0F, 1.0F, 1.0F, alphaTop, alphaBottom);
            }

            poseStack.popPose();
        }
    }

    /**
     * Рисует один квад луча (4 вершины) с градиентом прозрачности.
     */
    private void drawBeamQuad(VertexConsumer consumer, Matrix4f matrix, int packedOverlay,
                              float x1, float y1, float z1,  // верхняя точка
                              float x2, float y2, float z2,  // нижняя точка
                              float r, float g, float b,
                              float alphaTop, float alphaBottom) {

        float halfWidth = 0.5F;

        float cx1 = x1;
        float cz1 = z1;
        float cx2 = x2;
        float cz2 = z2;

        float nx = 0, ny = 1, nz = 0;
        int light = 15728880;

        consumer.vertex(matrix, cx1 - halfWidth, y1, cz1 - halfWidth)
                .color(r, g, b, alphaTop)
                .uv(0, 0)
                .overlayCoords(packedOverlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();

        consumer.vertex(matrix, cx1 + halfWidth, y1, cz1 - halfWidth)
                .color(r, g, b, alphaTop)
                .uv(0, 0)
                .overlayCoords(packedOverlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();

        consumer.vertex(matrix, cx2 + halfWidth, y2, cz2 - halfWidth)
                .color(r, g, b, alphaBottom)
                .uv(0, 0)
                .overlayCoords(packedOverlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();

        consumer.vertex(matrix, cx2 - halfWidth, y2, cz2 - halfWidth)
                .color(r, g, b, alphaBottom)
                .uv(0, 0)
                .overlayCoords(packedOverlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntitySolarBoiler te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}