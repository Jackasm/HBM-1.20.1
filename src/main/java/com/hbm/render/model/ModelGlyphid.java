package com.hbm.render.model;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static com.hbm.util.ResLocation.ResLocation;

public class ModelGlyphid extends EntityModel<EntityGlyphid> {

    private static final ResourceLocation GLYPHID_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid.png");
    private double bite = 0;
    private float prevYaw = 0;
    private float currentYaw = 0;

    @Override
    public void setupAnim(@NotNull EntityGlyphid entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.currentYaw = entity.getYRot();
        this.bite = entity.getAttackAnim(ageInTicks);
        this.prevYaw = entity.yRotO;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Пустой - используем кастомный рендер
    }

    public void render(EntityGlyphid entity, float limbSwing, float limbSwingAmount,
                       float rotationYaw, float rotationHeadYaw, float rotationPitch,
                       float scale, PoseStack poseStack, MultiBufferSource buffer,
                       ResourceLocation texture, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        if (entity.getDeltaMovement().lengthSqr() > 0.01) {
            // Вращаем модель в направлении движения
            float yaw = (float) Math.toDegrees(Math.atan2(
                    entity.getDeltaMovement().x(),
                    entity.getDeltaMovement().z()
            ));
            poseStack.mulPose(new Quaternionf().rotationXYZ(
                    0,
                    (float) Math.toRadians(yaw),
                    0
            ));
        } else {
            // Используем сохранённое вращение
            float yaw = Mth.lerp(0.1F, prevYaw, currentYaw);
            poseStack.mulPose(new Quaternionf().rotationXYZ(
                    0,
                    (float) Math.toRadians(yaw),
                    0
            ));
        }

        renderModel(entity, limbSwing, poseStack, buffer, texture, packedLight, packedOverlay);

        poseStack.popPose();
    }

    public void renderModel(EntityGlyphid entity, float limbSwing,
                            PoseStack poseStack, MultiBufferSource buffer,
                            ResourceLocation texture, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        double s = entity.getScale();
        poseStack.scale((float) s, (float) s, (float) s);

        byte armor = entity.getEntityData().get(EntityGlyphid.DW_ARMOR);

        double cy0 = Math.sin((double) limbSwing % (Math.PI * 2));
        double cy1 = Math.sin((double) limbSwing % (Math.PI * 2) - Math.PI * 0.5);
        double cy2 = Math.sin((double) limbSwing % (Math.PI * 2) - Math.PI);
        double cy3 = Math.sin((double) limbSwing % (Math.PI * 2) - Math.PI * 0.75);

        double bite = Mth.clamp(Math.sin(this.bite * Math.PI * 2 - Math.PI * 0.5), 0, 1) * 20;
        double headTilt = Math.sin(this.bite * Math.PI) * 30;

        // Body
        renderPart(HBMResourceManager.glyphid, "Body", poseStack, buffer, texture, packedLight, packedOverlay);
        if ((armor & (1 << 0)) > 0) renderPart(HBMResourceManager.glyphid, "ArmorFront", poseStack, buffer, texture, packedLight, packedOverlay);
        if ((armor & (1 << 1)) > 0) renderPart(HBMResourceManager.glyphid, "ArmorLeft", poseStack, buffer, texture, packedLight, packedOverlay);
        if ((armor & (1 << 2)) > 0) renderPart(HBMResourceManager.glyphid, "ArmorRight", poseStack, buffer, texture, packedLight, packedOverlay);

        // LEFT ARM
        poseStack.pushPose();
        poseStack.translate(0.25, 0.625, 0.0625);
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) Math.toRadians(10), (float) Math.toRadians(35 + cy1 * 20)));
        poseStack.translate(-0.25, -0.625, -0.0625);
        renderPart(HBMResourceManager.glyphid, "ArmLeftUpper", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.translate(0.25, 0.625, 0.4375);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(-75 - cy1 * 20 + cy0 * 20), 0, 0));
        poseStack.translate(-0.25, -0.625, -0.4375);
        renderPart(HBMResourceManager.glyphid, "ArmLeftMid", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.translate(0.25, 0.625, 0.9375);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(90 - cy0 * 45), 0, 0));
        poseStack.translate(-0.25, -0.625, -0.9375);
        renderPart(HBMResourceManager.glyphid, "ArmLeftLower", poseStack, buffer, texture, packedLight, packedOverlay);
        if ((armor & (1 << 3)) > 0) renderPart(HBMResourceManager.glyphid, "ArmLeftArmor", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.popPose();

        // RIGHT ARM
        poseStack.pushPose();
        poseStack.translate(-0.25, 0.625, 0.0625);
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) Math.toRadians(-10), (float) Math.toRadians(35 + cy2 * 20)));
        poseStack.translate(0.25, -0.625, -0.0625);
        renderPart(HBMResourceManager.glyphid, "ArmRightUpper", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.translate(-0.25, 0.625, 0.4375);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(-75 - cy2 * 20 + cy3 * 20), 0, 0));
        poseStack.translate(0.25, -0.625, -0.4375);
        renderPart(HBMResourceManager.glyphid, "ArmRightMid", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.translate(-0.25, 0.625, 0.9375);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(90 - cy3 * 45), 0, 0));
        poseStack.translate(0.25, -0.625, -0.9375);
        renderPart(HBMResourceManager.glyphid, "ArmRightLower", poseStack, buffer, texture, packedLight, packedOverlay);
        if ((armor & (1 << 4)) > 0) renderPart(HBMResourceManager.glyphid, "ArmRightArmor", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.popPose();

        // JAWS
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0.25);
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, (float) Math.toRadians(headTilt)));
        poseStack.translate(0, -0.5, -0.25);

        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0.25);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(-bite), 0, 0));
        poseStack.translate(0, -0.5, -0.25);
        renderPart(HBMResourceManager.glyphid, "JawTop", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0.25);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(bite), (float) Math.toRadians(bite), 0));
        poseStack.translate(0, -0.5, -0.25);
        renderPart(HBMResourceManager.glyphid, "JawLeft", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0.25);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(bite), (float) Math.toRadians(-bite), 0));
        poseStack.translate(0, -0.5, -0.25);
        renderPart(HBMResourceManager.glyphid, "JawRight", poseStack, buffer, texture, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();

        // LEGS
        double steppy = 15;
        double bend = 60;

        for (int i = 0; i < 3; i++) {
            double c0 = cy0 * (i == 1 ? -1 : 1);
            double c1 = cy1 * (i == 1 ? -1 : 1);

            // Left Leg
            poseStack.pushPose();
            poseStack.translate(0, 0.25, 0);
            poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) Math.toRadians(i * 30 - 15 + c0 * 7.5), (float) Math.toRadians(steppy + c1 * steppy)));
            poseStack.translate(0, -0.25, 0);
            renderPart(HBMResourceManager.glyphid, "LegLeftUpper", poseStack, buffer, texture, packedLight, packedOverlay);
            poseStack.translate(0.5625, 0.25, 0);
            poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(-bend - c1 * steppy), 0, 0));
            poseStack.translate(-0.5625, -0.25, 0);
            renderPart(HBMResourceManager.glyphid, "LegLeftLower", poseStack, buffer, texture, packedLight, packedOverlay);
            poseStack.popPose();

            // Right Leg
            poseStack.pushPose();
            poseStack.translate(0, 0.25, 0);
            poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) Math.toRadians(i * 30 - 45 + c0 * 7.5), (float) Math.toRadians(-steppy + c1 * steppy)));
            poseStack.translate(0, -0.25, 0);
            renderPart(HBMResourceManager.glyphid, "LegRightUpper", poseStack, buffer, texture, packedLight, packedOverlay);
            poseStack.translate(-0.5625, 0.25, 0);
            poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(bend - c1 * steppy), 0, 0));
            poseStack.translate(0.5625, -0.25, 0);
            renderPart(HBMResourceManager.glyphid, "LegRightLower", poseStack, buffer, texture, packedLight, packedOverlay);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void renderPart(HFRWavefrontObject model, String partName,
                            PoseStack poseStack, MultiBufferSource buffer,
                            ResourceLocation texture, int packedLight, int packedOverlay) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));
        model.renderPart(poseStack, consumer, partName, packedLight, packedOverlay);
    }
}