package com.hbm.render.util;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;

public class TomPronter {

    public static void prontTom(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.scale(100F, 100F, 100F);

        // Основная модель
        VertexConsumer mainConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.tom_main_tex));
        HBMResourceManager.tom_main.renderAll(poseStack, mainConsumer, packedLight, 0);

        // Огонь
        poseStack.pushPose();

        float rot = -((float) System.currentTimeMillis() / 10) % 360;
        poseStack.scale(0.8F, 5F, 0.8F);

        RandomSource rand = RandomSource.create(0);

        for (int i = 0; i < 20; i++) {
            int r = rand.nextInt(90);

            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rot + r));

            VertexConsumer flameConsumer = buffer.getBuffer(RenderType.entityTranslucent(HBMResourceManager.tom_flame_tex));
            HBMResourceManager.tom_flame.renderAll(poseStack, flameConsumer, 0xF000F0, 0);

            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-rot));

            poseStack.scale(-1.015F, 0.9F, 1.015F);
        }

        poseStack.popPose();

        poseStack.popPose();
    }
}