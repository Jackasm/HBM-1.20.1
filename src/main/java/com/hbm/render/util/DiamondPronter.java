package com.hbm.render.util;

import com.hbm.util.HBMEnums;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

public class DiamondPronter {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/models/misc/danger_diamond.png");

    public static void renderNFPA(PoseStack poseStack, MultiBufferSource buffer, int poison, int flammability, int reactivity,
                                  HBMEnums.EnumSymbol symbol, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();

        // Размеры и позиционирование
        float scale = 0.5f; // Масштаб под новый рендер
        poseStack.scale(scale, scale, scale);

        // Получаем VertexConsumer для текстуры
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        Matrix4f pose = poseStack.last().pose();

        // Рисуем основной ромб
        float p = 1F/256F; // Коэффициент для текстурных координат
        float s = 1F/139F; // Коэффициент для позиций

        // Основной ромб (белый фон)
        renderQuad(vertexConsumer, pose,
                0.0f, 0.5f, -0.5f, p * 144, p * 45,
                0.0f, 0.5f, 0.5f, p * 5, p * 45,
                0.0f, -0.5f, 0.5f, p * 5, p * 184,
                0.0f, -0.5f, -0.5f, p * 144, p * 184,
                combinedLight, combinedOverlay);

        float width = 10F * s;
        float height = 14F * s;

        // Яд (синяя секция - слева)
        if(poison >= 0 && poison < 6) {
            float oY = 0;
            float oZ = 33 * s;

            int x = 5 + (poison - 1) * 24;
            int y = 5;

            if(poison == 0) x = 125;

            renderQuad(vertexConsumer, pose,
                    0.01f, height + oY, -width + oZ, (x + 20) * p, y * p,
                    0.01f, height + oY, width + oZ, x * p, y * p,
                    0.01f, -height + oY, width + oZ, x * p, (y + 28) * p,
                    0.01f, -height + oY, -width + oZ, (x + 20) * p, (y + 28) * p,
                    combinedLight, combinedOverlay);
        }

        // Горючесть (красная секция - сверху)
        if(flammability >= 0 && flammability < 6) {
            float oY = 33 * s;
            float oZ = 0;

            int x = 5 + (flammability - 1) * 24;
            int y = 5;

            if(flammability == 0) x = 125;

            renderQuad(vertexConsumer, pose,
                    0.01f, height + oY, -width + oZ, (x + 20) * p, y * p,
                    0.01f, height + oY, width + oZ, x * p, y * p,
                    0.01f, -height + oY, width + oZ, x * p, (y + 28) * p,
                    0.01f, -height + oY, -width + oZ, (x + 20) * p, (y + 28) * p,
                    combinedLight, combinedOverlay);
        }

        // Реактивность (желтая секция - справа)
        if(reactivity >= 0 && reactivity < 6) {
            float oY = 0;
            float oZ = -33 * s;

            int x = 5 + (reactivity - 1) * 24;
            int y = 5;

            if(reactivity == 0) x = 125;

            renderQuad(vertexConsumer, pose,
                    0.01f, height + oY, -width + oZ, (x + 20) * p, y * p,
                    0.01f, height + oY, width + oZ, x * p, y * p,
                    0.01f, -height + oY, width + oZ, x * p, (y + 28) * p,
                    0.01f, -height + oY, -width + oZ, (x + 20) * p, (y + 28) * p,
                    combinedLight, combinedOverlay);
        }

        // Символ (белая секция - снизу)
        float symSize = 59F/2F * s;

        if(symbol != null && symbol != HBMEnums.EnumSymbol.NONE) {
            float oY = -33 * s;
            float oZ = 0;

            int x = symbol.x;
            int y = symbol.y;

            renderQuad(vertexConsumer, pose,
                    0.01f, symSize + oY, -symSize + oZ, (x + 59) * p, y * p,
                    0.01f, symSize + oY, symSize + oZ, x * p, y * p,
                    0.01f, -symSize + oY, symSize + oZ, x * p, (y + 59) * p,
                    0.01f, -symSize + oY, -symSize + oZ, (x + 59) * p, (y + 59) * p,
                    combinedLight, combinedOverlay);
        }

        poseStack.popPose();
    }

    private static void renderQuad(VertexConsumer consumer, Matrix4f pose,
                                   float x1, float y1, float z1, float u1, float v1,
                                   float x2, float y2, float z2, float u2, float v2,
                                   float x3, float y3, float z3, float u3, float v3,
                                   float x4, float y4, float z4, float u4, float v4,
                                   int light, int overlay) {

        consumer.vertex(pose, x1, y1, z1)
                .color(255, 255, 255, 255)
                .uv(u1, v1)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(1, 0, 0) // нормаль для плоского рендеринга
                .endVertex();

        consumer.vertex(pose, x2, y2, z2)
                .color(255, 255, 255, 255)
                .uv(u2, v2)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(1, 0, 0)
                .endVertex();

        consumer.vertex(pose, x3, y3, z3)
                .color(255, 255, 255, 255)
                .uv(u3, v3)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(1, 0, 0)
                .endVertex();

        consumer.vertex(pose, x4, y4, z4)
                .color(255, 255, 255, 255)
                .uv(u4, v4)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(1, 0, 0)
                .endVertex();
    }
}