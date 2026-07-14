package com.hbm.render.util;

import java.util.Random;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

public class BeamPronter {
    public static final ResourceLocation WHITE_TEXTURE = ResLocation(RefStrings.MODID, "textures/item/weapon/smoke_white.png");

    public enum EnumWaveType {
        RANDOM, SPIRAL
    }

    public enum EnumBeamType {
        SOLID, LINE
    }

    private static boolean depthMask = false;

    public static void prontBeamwithDepth(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 skeleton, EnumWaveType wave, EnumBeamType beam, int outerColor, int innerColor, int start, int segments, float size, int layers, float thickness) {
        depthMask = true;
        prontBeam(poseStack, bufferSource, skeleton, wave, beam, outerColor, innerColor, start, segments, size, layers, thickness);
        depthMask = false;
    }

    public static void prontBeam(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 skeleton, EnumWaveType wave, EnumBeamType beam, int outerColor, int innerColor, int start, int segments, float size, int layers, float thickness) {

        poseStack.pushPose();

        // Управление depth test
        if (!depthMask) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.depthMask(depthMask);

        // Повороты для выравнивания луча по направлению вектора skeleton
        float sYaw = (float) (Math.atan2(skeleton.x, skeleton.z) * 180F / Math.PI);
        float sqrt = Mth.sqrt((float) (skeleton.x * skeleton.x + skeleton.z * skeleton.z));
        float sPitch = (float) (Math.atan2(skeleton.y, sqrt) * 180F / Math.PI);

        poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(180));
        poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(sYaw));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(sPitch - 90));

        poseStack.pushPose();

        // Выбираем правильный RenderType
        VertexConsumer consumer;

        if (beam == EnumBeamType.SOLID) {
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(WHITE_TEXTURE));
        } else {
            consumer = bufferSource.getBuffer(RenderType.LINES);
        }

        Vec3 unit = new Vec3(0, 1, 0);
        Random rand = new Random(start);
        double length = skeleton.length();
        double segLength = length / segments;
        double lastX = 0;
        double lastY = 0;
        double lastZ = 0;

        Matrix4f lastPose = poseStack.last().pose();

        for(int i = 0; i <= segments; i++) {

            Vec3 spinner = new Vec3(size, 0, 0);

            if(wave == EnumWaveType.SPIRAL) {
                spinner = spinner.yRot((float) Math.PI * (float) start / 180F);
                spinner = spinner.yRot((float) Math.PI * 45F / 180F * i);
            } else if(wave == EnumWaveType.RANDOM) {
                spinner = spinner.yRot((float) Math.PI * 2 * rand.nextFloat());
                spinner = spinner.yRot((float) Math.PI * 2 * rand.nextFloat());
            }

            double pX = unit.x * segLength * i + spinner.x;
            double pY = unit.y * segLength * i + spinner.y;
            double pZ = unit.z * segLength * i + spinner.z;

            if(beam == EnumBeamType.LINE && i > 0) {
                VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.LINES);
                // Для линий нужны UV координаты (по умолчанию 0,0)
                lineConsumer.vertex(lastPose, (float) lastX, (float) lastY, (float) lastZ)
                        .color(outerColor)
                        .uv(0, 0)
                        .normal(0, 1, 0)
                        .endVertex();
                lineConsumer.vertex(lastPose, (float) pX, (float) pY, (float) pZ)
                        .color(outerColor)
                        .uv(0, 0)
                        .normal(0, 1, 0)
                        .endVertex();
            }

            if(beam == EnumBeamType.SOLID && i > 0) {
                float radius = thickness / layers;

                for(int j = 1; j <= layers; j++) {
                    float inter = (float) (j - 1) / (float) (layers - 1);

                    float alpha = Math.min(1.0f, (float)j / layers * 1.5f);

                    int r1 = ((outerColor >> 16) & 0xFF);
                    int g1 = ((outerColor >> 8) & 0xFF);
                    int b1 = (outerColor & 0xFF);

                    int r2 = ((innerColor >> 16) & 0xFF);
                    int g2 = ((innerColor >> 8) & 0xFF);
                    int b2 = (innerColor & 0xFF);

                    int r = (int)(r1 + (r2 - r1) * inter);
                    int g = (int)(g1 + (g2 - g1) * inter);
                    int b = (int)(b1 + (b2 - b1) * inter);

                    // Рисуем квады со всех четырёх сторон
                    // Сторона +X
                    drawQuad(consumer, lastPose,
                            lastX + (radius * j), lastY, lastZ + (radius * j),
                            lastX + (radius * j), lastY, lastZ - (radius * j),
                            pX + (radius * j), pY, pZ - (radius * j),
                            pX + (radius * j), pY, pZ + (radius * j),
                            r, g, b, alpha);

                    // Сторона -X
                    drawQuad(consumer, lastPose,
                            lastX - (radius * j), lastY, lastZ - (radius * j),
                            lastX - (radius * j), lastY, lastZ + (radius * j),
                            pX - (radius * j), pY, pZ + (radius * j),
                            pX - (radius * j), pY, pZ - (radius * j),
                            r, g, b, alpha);

                    // Сторона +Z
                    drawQuad(consumer, lastPose,
                            lastX + (radius * j), lastY, lastZ + (radius * j),
                            lastX - (radius * j), lastY, lastZ + (radius * j),
                            pX - (radius * j), pY, pZ + (radius * j),
                            pX + (radius * j), pY, pZ + (radius * j),
                            r, g, b, alpha);

                    // Сторона -Z
                    drawQuad(consumer, lastPose,
                            lastX - (radius * j), lastY, lastZ - (radius * j),
                            lastX + (radius * j), lastY, lastZ - (radius * j),
                            pX + (radius * j), pY, pZ - (radius * j),
                            pX - (radius * j), pY, pZ - (radius * j),
                            r, g, b, alpha);
                }
            }

            lastX = pX;
            lastY = pY;
            lastZ = pZ;
        }

        if(beam == EnumBeamType.LINE) {
            // Рисуем центральную линию
            VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.LINES);
            lineConsumer.vertex(lastPose, 0, 0, 0)
                    .color(innerColor)
                    .uv(0, 0)
                    .normal(0, 1, 0)
                    .endVertex();
            lineConsumer.vertex(lastPose, 0, (float) skeleton.length(), 0)
                    .color(innerColor)
                    .uv(0, 0)
                    .normal(0, 1, 0)
                    .endVertex();
        }

        if(beam == EnumBeamType.SOLID) {
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
        }

        poseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        poseStack.popPose();
    }

    // Вспомогательный метод для отрисовки одного quad'а
    private static void drawQuad(VertexConsumer consumer, Matrix4f pose,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 double x3, double y3, double z3,
                                 double x4, double y4, double z4,
                                 int r, int g, int b, float alpha) {

        float nx = 0, ny = 1, nz = 0; // нормаль
        int light = 15728880; // 0x00F000F0 - максимальное освещение
        int overlay = 655360; // OverlayTexture.NO_OVERLAY
        //int a = (int)(alpha);
        int a = 32;

        // Вершина 1
        consumer.vertex(pose, (float) x1, (float) y1, (float) z1)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();

        // Вершина 2
        consumer.vertex(pose, (float) x2, (float) y2, (float) z2)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();

        // Вершина 3
        consumer.vertex(pose, (float) x3, (float) y3, (float) z3)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();

        // Вершина 4
        consumer.vertex(pose, (float) x4, (float) y4, (float) z4)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();
    }
}