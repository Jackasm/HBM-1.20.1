package com.hbm.render.util;

import com.hbm.items.weapon.ItemCustomMissilePart.PartType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MissilePronter {

    public static void prontMissile(MissileMultipart missile, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
        if (missile == null) return;

        poseStack.pushPose();

        // 1. Рендерим Thrusters (двигатель) — сначала нижняя часть
        if (missile.thruster != null && missile.thruster.type == PartType.THRUSTER) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(missile.thruster.texture));
            missile.thruster.model.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.translate(0, missile.thruster.height, 0);
        }

        // 2. Рендерим Fuselage (корпус) и Fins (крылья)
        if (missile.fuselage != null && missile.fuselage.type == PartType.FUSELAGE) {

            // Сначала крылья (если есть)
            if (missile.fins != null && missile.fins.type == PartType.FINS) {
                VertexConsumer finsConsumer = buffer.getBuffer(RenderType.entityCutout(missile.fins.texture));
                missile.fins.model.renderAll(poseStack, finsConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }

            // Затем корпус
            VertexConsumer fuselageConsumer = buffer.getBuffer(RenderType.entityCutout(missile.fuselage.texture));
            missile.fuselage.model.renderAll(poseStack, fuselageConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.translate(0, missile.fuselage.height, 0);
        }

        // 3. Рендерим Warhead (боеголовка) — верхняя часть
        if (missile.warhead != null && missile.warhead.type == PartType.WARHEAD) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(missile.warhead.texture));
            missile.warhead.model.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }
}