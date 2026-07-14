package com.hbm.render.block;

import com.hbm.blocks.network.FluidDuctBox;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.util.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RenderFluidDuctBox implements BlockEntityRenderer<TileEntityPipeBaseNT> {

    private static final float MIN = 0.125f;
    private static final float MAX = 0.875f;
    private static final float J_MIN = 0.0625f;
    private static final float J_MAX = 0.9375f;

    public RenderFluidDuctBox(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityPipeBaseNT tile, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = tile.getLevel();
        BlockPos pos = tile.getBlockPos();
        BlockState state = tile.getBlockState();
        Block block = state.getBlock();
        if (!(block instanceof FluidDuctBox duct)) return;

        boolean pX = duct.canConnectTo(level, pos, Direction.EAST, tile.getFluidType());
        boolean nX = duct.canConnectTo(level, pos, Direction.WEST, tile.getFluidType());
        boolean pY = duct.canConnectTo(level, pos, Direction.UP, tile.getFluidType());
        boolean nY = duct.canConnectTo(level, pos, Direction.DOWN, tile.getFluidType());
        boolean pZ = duct.canConnectTo(level, pos, Direction.SOUTH, tile.getFluidType());
        boolean nZ = duct.canConnectTo(level, pos, Direction.NORTH, tile.getFluidType());

        int mask = (pX ? 32 : 0) | (nX ? 16 : 0) | (pY ? 8 : 0) | (nY ? 4 : 0) | (pZ ? 2 : 0) | (nZ ? 1 : 0);
        int count = (pX ? 1 : 0) + (nX ? 1 : 0) + (pY ? 1 : 0) + (nY ? 1 : 0) + (pZ ? 1 : 0) + (nZ ? 1 : 0);

        int material = state.getValue(FluidDuctBox.MATERIAL);
        int meta = material * 5;
        float lower = MIN;
        float upper = MAX;
        float jLower = J_MIN;
        float jUpper = J_MAX;
        for (int i = 2; i < 13; i += 3) {
            if (meta > i) {
                lower += 0.0625f;
                upper -= 0.0625f;
                jLower += 0.0625f;
                jUpper -= 0.0625f;
            }
        }

        int color = 0xffffff;
        if (tile.getFluidType() != null) {
            color = tile.getFluidType().getColor();
            if (meta % 3 == 2) color = ColorUtil.lightenColor(color, 0.25);
        }

        ResourceLocation sideTex = duct.getStraightTexture(state);
        ResourceLocation endTex = duct.getEndTexture(state);

        VertexConsumer sideConsumer = buffer.getBuffer(RenderType.entityCutout(sideTex));
        VertexConsumer endConsumer = buffer.getBuffer(RenderType.entityCutout(endTex));

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        if (mask == 0) {
            // Изолированный блок – все грани используют endTex
            drawCube(poseStack, sideConsumer, endConsumer,
                    jLower, jLower, jLower, jUpper, jUpper, jUpper,
                    packedLight, packedOverlay, color, false, true);
        } else if ((mask & 0b001111) == 0 && count > 0) {
            // Прямая по оси X (торцы – Z-грани)
            drawCube(poseStack, sideConsumer, endConsumer,
                    0, lower, lower, 1, upper, upper,
                    packedLight, packedOverlay, color, true, false);
        } else if ((mask & 0b111100) == 0 && count > 0) {
            // Прямая по оси Z (торцы – X-грани, но мы не используем endTex, оставляем sideTex)
            drawCube(poseStack, sideConsumer, endConsumer,
                    lower, lower, 0, upper, upper, 1,
                    packedLight, packedOverlay, color, false, false);
        } else if ((mask & 0b110011) == 0 && count > 0) {
            // Прямая по оси Y (торцов нет, все грани sideTex)
            drawCube(poseStack, sideConsumer, endConsumer,
                    lower, 0, lower, upper, 1, upper,
                    packedLight, packedOverlay, color, false, false);
        } else if (count == 2) {
            // Угол – все грани sideTex (можно расширить специальными текстурами, но пока так)
            drawCube(poseStack, sideConsumer, endConsumer,
                    lower, lower, lower, upper, upper, upper,
                    packedLight, packedOverlay, color, false, false);
            if (nY) drawCube(poseStack, sideConsumer, endConsumer,
                    lower, 0, lower, upper, lower, upper,
                    packedLight, packedOverlay, color, false, false);
            if (pY) drawCube(poseStack, sideConsumer, endConsumer,
                    lower, upper, lower, upper, 1, upper,
                    packedLight, packedOverlay, color, false, false);
            if (nX) drawCube(poseStack, sideConsumer, endConsumer,
                    0, lower, lower, lower, upper, upper,
                    packedLight, packedOverlay, color, false, false);
            if (pX) drawCube(poseStack, sideConsumer, endConsumer,
                    upper, lower, lower, 1, upper, upper,
                    packedLight, packedOverlay, color, false, false);
            if (nZ) drawCube(poseStack, sideConsumer, endConsumer,
                    lower, lower, 0, upper, upper, lower,
                    packedLight, packedOverlay, color, false, false);
            if (pZ) drawCube(poseStack, sideConsumer, endConsumer,
                    lower, lower, upper, upper, upper, 1,
                    packedLight, packedOverlay, color, false, false);
        } else {
            // Узел (3+ соединений) – все грани sideTex
            drawCube(poseStack, sideConsumer, endConsumer,
                    jLower, jLower, jLower, jUpper, jUpper, jUpper,
                    packedLight, packedOverlay, color, false, false);
            if (nY) drawCube(poseStack, sideConsumer, endConsumer,
                    lower, 0, lower, upper, jLower, upper,
                    packedLight, packedOverlay, color, false, false);
            if (pY) drawCube(poseStack, sideConsumer, endConsumer,
                    lower, jUpper, lower, upper, 1, upper,
                    packedLight, packedOverlay, color, false, false);
            if (nX) drawCube(poseStack, sideConsumer, endConsumer,
                    0, lower, lower, jLower, upper, upper,
                    packedLight, packedOverlay, color, false, false);
            if (pX) drawCube(poseStack, sideConsumer, endConsumer,
                    jUpper, lower, lower, 1, upper, upper,
                    packedLight, packedOverlay, color, false, false);
            if (nZ) drawCube(poseStack, sideConsumer, endConsumer,
                    lower, lower, 0, upper, upper, jLower,
                    packedLight, packedOverlay, color, false, false);
            if (pZ) drawCube(poseStack, sideConsumer, endConsumer,
                    lower, lower, jUpper, upper, upper, 1,
                    packedLight, packedOverlay, color, false, false);
        }

        poseStack.popPose();
    }

    private void drawCube(PoseStack poseStack, VertexConsumer sideConsumer, VertexConsumer endConsumer,
                          float x1, float y1, float z1, float x2, float y2, float z2,
                          int light, int overlay, int color,
                          boolean useEndForZ, boolean useEndForAll) {
        float cx1 = x1 - 0.5f;
        float cy1 = y1 - 0.5f;
        float cz1 = z1 - 0.5f;
        float cx2 = x2 - 0.5f;
        float cy2 = y2 - 0.5f;
        float cz2 = z2 - 0.5f;

        Matrix4f matrix = poseStack.last().pose();

        VertexConsumer xNeg = useEndForAll ? endConsumer : sideConsumer;
        VertexConsumer xPos = useEndForAll ? endConsumer : sideConsumer;
        VertexConsumer yNeg = useEndForAll ? endConsumer : sideConsumer;
        VertexConsumer yPos = useEndForAll ? endConsumer : sideConsumer;
        VertexConsumer zNeg = (useEndForAll || useEndForZ) ? endConsumer : sideConsumer;
        VertexConsumer zPos = (useEndForAll || useEndForZ) ? endConsumer : sideConsumer;

        // X- (west)
        vertex(matrix, xNeg, cx1, cy1, cz1, 0, 1, -1, 0, 0, light, overlay, color);
        vertex(matrix, xNeg, cx1, cy1, cz2, 1, 1, -1, 0, 0, light, overlay, color);
        vertex(matrix, xNeg, cx1, cy2, cz2, 1, 0, -1, 0, 0, light, overlay, color);
        vertex(matrix, xNeg, cx1, cy2, cz1, 0, 0, -1, 0, 0, light, overlay, color);

        // X+ (east)
        vertex(matrix, xPos, cx2, cy1, cz1, 0, 1, 1, 0, 0, light, overlay, color);
        vertex(matrix, xPos, cx2, cy2, cz1, 0, 0, 1, 0, 0, light, overlay, color);
        vertex(matrix, xPos, cx2, cy2, cz2, 1, 0, 1, 0, 0, light, overlay, color);
        vertex(matrix, xPos, cx2, cy1, cz2, 1, 1, 1, 0, 0, light, overlay, color);

        // Y- (down)
        vertex(matrix, yNeg, cx2, cy1, cz1, 0, 1, 0, -1, 0, light, overlay, color);
        vertex(matrix, yNeg, cx2, cy1, cz2, 1, 1, 0, -1, 0, light, overlay, color);
        vertex(matrix, yNeg, cx1, cy1, cz2, 1, 0, 0, -1, 0, light, overlay, color);
        vertex(matrix, yNeg, cx1, cy1, cz1, 0, 0, 0, -1, 0, light, overlay, color);

        // Y+ (up)
        vertex(matrix, yPos, cx1, cy2, cz1, 0, 1, 0, 1, 0, light, overlay, color);
        vertex(matrix, yPos, cx1, cy2, cz2, 1, 1, 0, 1, 0, light, overlay, color);
        vertex(matrix, yPos, cx2, cy2, cz2, 1, 0, 0, 1, 0, light, overlay, color);
        vertex(matrix, yPos, cx2, cy2, cz1, 0, 0, 0, 1, 0, light, overlay, color);

        // Z- (north)
        vertex(matrix, zNeg, cx1, cy1, cz1, 0, 1, 0, 0, -1, light, overlay, color);
        vertex(matrix, zNeg, cx1, cy2, cz1, 0, 0, 0, 0, -1, light, overlay, color);
        vertex(matrix, zNeg, cx2, cy2, cz1, 1, 0, 0, 0, -1, light, overlay, color);
        vertex(matrix, zNeg, cx2, cy1, cz1, 1, 1, 0, 0, -1, light, overlay, color);

        // Z+ (south)
        vertex(matrix, zPos, cx2, cy1, cz2, 0, 1, 0, 0, 1, light, overlay, color);
        vertex(matrix, zPos, cx2, cy2, cz2, 0, 0, 0, 0, 1, light, overlay, color);
        vertex(matrix, zPos, cx1, cy2, cz2, 1, 0, 0, 0, 1, light, overlay, color);
        vertex(matrix, zPos, cx1, cy1, cz2, 1, 1, 0, 0, 1, light, overlay, color);
    }

    private void vertex(Matrix4f matrix, VertexConsumer consumer, float x, float y, float z,
                        float u, float v, float nx, float ny, float nz,
                        int light, int overlay, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        consumer.vertex(matrix, x, y, z)
                .color(r, g, b, 255)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();
    }
}