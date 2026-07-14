package com.hbm.render.tileentity;

import com.hbm.api.entity.RadarEntry;
import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineRadarScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class RenderRadarScreen implements BlockEntityRenderer<TileEntityMachineRadarScreen> {

    public RenderRadarScreen(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityMachineRadarScreen screen, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = screen.getBlockState().getValue(BlockDummyable.FACING);
        switch (facing) {
            case EAST -> {
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
                poseStack.translate(0, 0, -1);
            }
            case SOUTH -> {
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
                poseStack.translate(0, 0, -1);
            }
            case WEST -> {
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(0));
                poseStack.translate(0, 0, -1);
            }
            case NORTH -> {
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270));
                poseStack.translate(0, 0, -1);
            }
            default -> {}
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.radar_screen_tex));
        HBMResourceManager.radar_screen.renderAll(poseStack, consumer, packedLight, packedOverlay);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();

        if (screen.linked) {
            // Сканирующая линия
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            double offset = ((Objects.requireNonNull(screen.getLevel()).getGameTime() % 56) + partialTicks) / 30D;
            Matrix4f matrix = poseStack.last().pose();

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            builder.vertex(matrix, 0.38f, (float) (2 - offset), 1.375f).color(0, 1, 0, 0).endVertex();
            builder.vertex(matrix, 0.38f, (float) (2 - offset), -0.375f).color(0, 1, 0, 0).endVertex();
            builder.vertex(matrix, 0.38f, (float) (2 - offset - 0.125), -0.375f).color(0, 1, 0, 50 / 255f).endVertex();
            builder.vertex(matrix, 0.38f, (float) (2 - offset - 0.125), 1.375f).color(0, 1, 0, 50 / 255f).endVertex();
            tessellator.end();

            // Blips
            if (!screen.entries.isEmpty()) {
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                builder.normal(0, 1, 0);

                for (RadarEntry entry : screen.entries) {
                    double sX = (entry.posX - screen.refX) / ((double) screen.range + 1) * 0.875D;
                    double sZ = (entry.posZ - screen.refZ) / ((double) screen.range + 1) * 0.875D;
                    double size = 0.0625D;

                    float r, g, b;
                    switch (entry.blipLevel) {
                        case 0 -> { r = 0; g = 1; b = 0; }
                        case 1 -> { r = 1; g = 1; b = 0; }
                        case 2 -> { r = 1; g = 0.5f; b = 0; }
                        case 3 -> { r = 1; g = 0; b = 0; }
                        case 4 -> { r = 1; g = 0; b = 1; }
                        case 5 -> { r = 0; g = 0; b = 1; }
                        default -> { r = 1; g = 1; b = 1; }
                    }

                    float alpha = 1.0F;
                    builder.vertex(matrix, 0.38f, (float) (1 - sZ + size), (float) (0.5 - sX + size))
                            .color(r, g, b, alpha).endVertex();
                    builder.vertex(matrix, 0.38f, (float) (1 - sZ + size), (float) (0.5 - sX - size))
                            .color(r, g, b, alpha).endVertex();
                    builder.vertex(matrix, 0.38f, (float) (1 - sZ - size), (float) (0.5 - sX - size))
                            .color(r, g, b, alpha).endVertex();
                    builder.vertex(matrix, 0.38f, (float) (1 - sZ - size), (float) (0.5 - sX + size))
                            .color(r, g, b, alpha).endVertex();
                }
                tessellator.end();
            }

            // Восстанавливаем состояние
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);

        } else {
            int offset = 118 + Objects.requireNonNull(screen.getLevel()).random.nextInt(81);
            Matrix4f matrix = poseStack.last().pose();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, HBMResourceManager.radar_screen_tex);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            builder.normal(0, 1, 0);
            builder.vertex(matrix, 0.38f, 1.875f, 1.375f).uv(216F / 256F, (offset + 40F) / 256F).endVertex();
            builder.vertex(matrix, 0.38f, 1.875f, -0.375f).uv(256F / 256F, (offset + 40F) / 256F).endVertex();
            builder.vertex(matrix, 0.38f, 0.125f, -0.375f).uv(256F / 256F, offset / 256F).endVertex();
            builder.vertex(matrix, 0.38f, 0.125f, 1.375f).uv(216F / 256F, offset / 256F).endVertex();
            tessellator.end();

            RenderSystem.disableBlend();
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineRadarScreen screen) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}