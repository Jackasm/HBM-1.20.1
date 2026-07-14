package com.hbm.render.model;

import com.hbm.entity.mob.EntityMaskMan;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ModelMaskMan extends EntityModel<EntityMaskMan> {



    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Этот метод вызывается MobRenderer, но мы не можем использовать VertexConsumer напрямую
        // Поэтому оставляем пустым, рендеринг будет через наш кастомный метод
    }

    @Override
    public void setupAnim(@NotNull EntityMaskMan entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Анимации обрабатываются в render
    }

    public void render(EntityMaskMan entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();


        poseStack.translate(0, -1.5F, 0);
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                (float) Math.toRadians(-90),
                0
        ));

        // Анимация ходьбы
        double swing = Math.toDegrees(Mth.cos(limbSwing / 2F + (float) Math.PI) * 1.4F * limbSwingAmount);

        // Torso
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                (float) Math.toRadians(swing * -0.1),
                0,
                0
        ));
        renderPart(HBMResourceManager.maskman, "Torso", poseStack, buffer, packedLight, packedOverlay);

        // Left Leg
        poseStack.pushPose();
        poseStack.translate(-0.5F, 1.75F, -0.5F);
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                0,
                (float) Math.toRadians(swing)
        ));
        renderPart(HBMResourceManager.maskman, "LLeg", poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();

        // Right Leg
        poseStack.pushPose();
        poseStack.translate(-0.5F, 1.75F, 0.5F);
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                0,
                (float) Math.toRadians(swing * -1)
        ));
        renderPart(HBMResourceManager.maskman, "RLeg", poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();

        // Left Arm
        poseStack.pushPose();
        poseStack.translate(-0.5F, 3.75F, -1.5F);
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                0,
                (float) Math.toRadians(swing * 0.25)
        ));
        renderPart(HBMResourceManager.maskman, "LArm", poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();

        // Right Arm
        poseStack.pushPose();
        poseStack.translate(-0.5F, 3.75F, 1.5F);
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                0,
                (float) Math.toRadians(swing * -0.25)
        ));
        renderPart(HBMResourceManager.maskman, "RArm", poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();

        // Head
        poseStack.pushPose();
        poseStack.translate(0.5F, 4F, 0);
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                (float) Math.toRadians(-netHeadYaw),
                0
        ));

        if (entity.getHealth() >= entity.getMaxHealth() / 2) {
            renderPart(HBMResourceManager.maskman, "Head", poseStack, buffer, packedLight, packedOverlay);
        } else {
            renderPart(HBMResourceManager.maskman, "Skull", poseStack, buffer, packedLight, packedOverlay);
            // Рендерим IOU поверх скелета
            renderIOU(HBMResourceManager.maskman, poseStack, buffer, packedLight, packedOverlay);
        }
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderPart(HFRWavefrontObject model, String partName, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ResourceLocation texture = HBMResourceManager.maskman_tex;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));
        model.renderPart(poseStack, consumer, partName, packedLight, packedOverlay);
    }

    private void renderIOU(HFRWavefrontObject model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ResourceLocation texture = HBMResourceManager.maskman_iou_tex;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));
        model.renderPart(poseStack, consumer, "IOU", packedLight, packedOverlay);
    }
}