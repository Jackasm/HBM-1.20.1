package com.hbm.render.model;

import com.hbm.render.loader.ModelRendererObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ModelArmorBase extends HumanoidModel<LivingEntity> {

    public int type;
    public ModelRendererObj head;
    public ModelRendererObj body;
    public ModelRendererObj leftArm;
    public ModelRendererObj rightArm;
    public ModelRendererObj leftLeg;
    public ModelRendererObj rightLeg;
    public ModelRendererObj leftFoot;
    public ModelRendererObj rightFoot;

    protected LivingEntity entity;

    public ModelArmorBase(int type, ModelPart root) {
        super(root);
        this.type = type;

        // Создаем пустые части для предотвращения ошибок
        this.head = new ModelRendererObj(null);
        this.body = new ModelRendererObj(null);
        this.leftArm = new ModelRendererObj(null).setRotationPoint(5.0F, 2.0F, 0.0F);
        this.rightArm = new ModelRendererObj(null).setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.leftLeg = new ModelRendererObj(null).setRotationPoint(1.9F, 12.0F, 0.0F);
        this.rightLeg = new ModelRendererObj(null).setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.leftFoot = new ModelRendererObj(null).setRotationPoint(1.9F, 12.0F, 0.0F);
        this.rightFoot = new ModelRendererObj(null).setRotationPoint(-1.9F, 12.0F, 0.0F);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        boolean calculateRotations = true;

        // Пытаемся получить модель из рендера
        EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var render = renderDispatcher.getRenderer(entity);

        if (render instanceof LivingEntityRenderer<?, ?> livingRenderer) {
            var model = livingRenderer.getModel();
            if (model instanceof HumanoidModel<?> humanoidModel) {
                copyPropertiesFromHumanoidModel(humanoidModel);
                calculateRotations = false;
            }
        } else if (render instanceof LivingEntityRenderer<?, ?> livingRenderer) {
            var livingModel = livingRenderer.getModel();
            if (livingModel instanceof HumanoidModel<?> humanoidModel) {
                copyPropertiesFromHumanoidModel(humanoidModel);
                calculateRotations = false;
            }
        }

        /// FALLBACK ///
        if (calculateRotations) {
            this.crouching = entity.isCrouching();
            this.riding = entity.isPassenger();

            if (this.crouching) {
                this.rightFoot.offsetZ = this.rightLeg.offsetZ = 4.0F;
                this.leftFoot.offsetZ = this.leftLeg.offsetZ = 4.0F;
                this.rightFoot.offsetY = this.rightLeg.offsetY = -3.0F;
                this.leftFoot.offsetY = this.leftLeg.offsetY = -3.0F;
            } else {
                this.rightFoot.offsetZ = this.rightLeg.offsetZ = 0.1F;
                this.leftFoot.offsetZ = this.leftLeg.offsetZ = 0.1F;
                this.rightFoot.offsetY = this.rightLeg.offsetY = 0.0F;
                this.leftFoot.offsetY = this.leftLeg.offsetY = 0.0F;
            }

            this.head.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
            this.head.rotateAngleX = headPitch / (180F / (float) Math.PI);
            this.rightArm.rotateAngleX = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.rotateAngleX = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.rightArm.rotateAngleZ = 0.0F;
            this.leftArm.rotateAngleZ = 0.0F;

            if (this.riding) {
                this.rightArm.rotateAngleX -= (float) Math.PI / 5F;
                this.leftArm.rotateAngleX -= (float) Math.PI / 5F;
            }

            if (this.leftArmPose != ArmPose.EMPTY) {
                this.leftArm.rotateAngleX = this.leftArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
            }

            if (this.rightArmPose != ArmPose.EMPTY) {
                this.rightArm.rotateAngleX = this.rightArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
            }

            this.rightArm.rotateAngleY = 0.0F;
            this.leftArm.rotateAngleY = 0.0F;

            // Анимация атаки (swingProgress)
            if (this.attackTime > 0.0F) {
                float f6 = this.attackTime;
                this.body.rotateAngleY = Mth.sin(Mth.sqrt(f6) * (float) Math.PI * 2.0F) * 0.2F;
                this.rightArm.rotationPointZ = Mth.sin(this.body.rotateAngleY) * 5.0F;
                this.rightArm.rotationPointX = -Mth.cos(this.body.rotateAngleY) * 5.0F;
                this.leftArm.rotationPointZ = -Mth.sin(this.body.rotateAngleY) * 5.0F;
                this.leftArm.rotationPointX = Mth.cos(this.body.rotateAngleY) * 5.0F;
                this.rightArm.rotateAngleY += this.body.rotateAngleY;
                this.leftArm.rotateAngleY += this.body.rotateAngleY;
                this.leftArm.rotateAngleX += this.body.rotateAngleY;
                f6 = 1.0F - this.attackTime;
                f6 *= f6;
                f6 *= f6;
                f6 = 1.0F - f6;
                float f7 = Mth.sin(f6 * (float) Math.PI);
                float f8 = Mth.sin(this.attackTime * (float) Math.PI) * -(this.head.rotateAngleX - 0.7F) * 0.75F;
                this.rightArm.rotateAngleX = (float) ((double) this.rightArm.rotateAngleX - ((double) f7 * 1.2D + (double) f8));
                this.rightArm.rotateAngleY += this.body.rotateAngleY * 2.0F;
                this.rightArm.rotateAngleZ = Mth.sin(this.attackTime * (float) Math.PI) * -0.4F;
            }

            if (this.crouching) {
                this.body.rotateAngleX = 0.5F;
                this.rightArm.rotateAngleX += 0.4F;
                this.leftArm.rotateAngleX += 0.4F;
                this.head.rotationPointY = 1.0F;
            } else {
                this.body.rotateAngleX = 0.0F;
                this.head.rotationPointY = 0.0F;
            }

            this.rightArm.rotateAngleZ += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.leftArm.rotateAngleZ -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.rightArm.rotateAngleX += Mth.sin(ageInTicks * 0.067F) * 0.05F;
            this.leftArm.rotateAngleX -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
        }
    }

    protected static void bindTexture(ResourceLocation location) {
        Minecraft.getInstance().getTextureManager().bindForSetup(location);
    }

    public void copyPropertiesFromHumanoidModel(HumanoidModel<?> model) {
        this.head.copyRotationFrom(model.head);
        this.body.copyRotationFrom(model.body);
        this.leftArm.copyRotationFrom(model.leftArm);
        this.rightArm.copyRotationFrom(model.rightArm);
        this.leftLeg.copyRotationFrom(model.leftLeg);
        this.rightLeg.copyRotationFrom(model.rightLeg);
        this.leftFoot.copyRotationFrom(model.leftLeg);
        this.rightFoot.copyRotationFrom(model.rightLeg);

        // Совместимость
        this.attackTime = model.attackTime;
        this.crouching = model.crouching;
        this.riding = model.riding;
    }
}