package com.hbm.render.model;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class ModelM65Blaze extends ModelArmorBase  {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "model_m65_blaze"), "main");

    private final ModelPart mask;
    private final ModelPart filter;

    public ModelM65Blaze(ModelPart root) {
        super(0, root);
        this.mask = root.getChild("mask");
        this.filter = root.getChild("filter");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        float yOffset = 0.5F;

        // Маска
        PartDefinition mask = partdefinition.addOrReplaceChild("mask",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // Основная часть головы
        mask.addOrReplaceChild("Shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F + yOffset, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.ZERO);

        // Нос
        mask.addOrReplaceChild("Shape2",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-1.5F, -3.5F + yOffset, -5.0F, 3.0F, 3.0F, 1.0F),
                PartPose.ZERO);

        // Выпускной клапан
        mask.addOrReplaceChild("Shape3",
                CubeListBuilder.create()
                        .texOffs(0, 20)
                        .addBox(-1.0F, -3.5F + yOffset, -5.0F, 2.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(0F, 0.4F, -1F, -0.4799655F, 0F, 0F));

        // Склон носа
        mask.addOrReplaceChild("Shape4",
                CubeListBuilder.create()
                        .texOffs(8, 16)
                        .addBox(-1.5F, -2.0F + yOffset, -4.0F, 3.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(0F, -1.2F, -2F, 0.6108652F, 0F, 0F));

        // Левый глаз
        mask.addOrReplaceChild("Shape5",
                CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(-3.5F, -6.0F + yOffset, -4.2F, 3.0F, 3.0F, 0.0F),
                PartPose.ZERO);

        // Правый глаз
        mask.addOrReplaceChild("Shape6",
                CubeListBuilder.create()
                        .texOffs(0, 26)
                        .addBox(0.5F, -6.0F + yOffset, -4.2F, 3.0F, 3.0F, 0.0F),
                PartPose.ZERO);

        // Небольшая деталь
        mask.addOrReplaceChild("Shape7",
                CubeListBuilder.create()
                        .texOffs(6, 20)
                        .addBox(-1.0F, -3.2F + yOffset, -6.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        // Фильтр
        PartDefinition filter = partdefinition.addOrReplaceChild("filter",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // Соединитель фильтра
        filter.addOrReplaceChild("Shape8",
                CubeListBuilder.create()
                        .texOffs(6, 23)
                        .addBox(-1.0F, -2.0F + yOffset, -4.0F, 2.0F, 2.0F, 1.0F),
                PartPose.rotation(0.6108652F, 0.0F, 0.0F));

        // Фильтр 1
        filter.addOrReplaceChild("Shape9",
                CubeListBuilder.create()
                        .texOffs(18, 21)
                        .addBox(-1.5F, -2.0F + yOffset, -4.0F, 3.0F, 4.0F, 2.0F),
                PartPose.rotation(0.6108652F, 0.0F, 0.0F));

        // Фильтр 2
        filter.addOrReplaceChild("Shape10",
                CubeListBuilder.create()
                        .texOffs(18, 16)
                        .addBox(-2.0F, -2.0F + yOffset, -4.0F, 4.0F, 3.0F, 2.0F),
                PartPose.rotation(0.6108652F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Копируем повороты из head в mask и filter
        this.mask.x = this.head.rotationPointX;
        this.mask.y = this.head.rotationPointY;
        this.mask.z = this.head.rotationPointZ;
        this.mask.xRot = this.head.rotateAngleX;
        this.mask.yRot = this.head.rotateAngleY;
        this.mask.zRot = this.head.rotateAngleZ;

        this.filter.x = this.head.rotationPointX;
        this.filter.y = this.head.rotationPointY;
        this.filter.z = this.head.rotationPointZ;
        this.filter.xRot = this.head.rotateAngleX;
        this.filter.yRot = this.head.rotateAngleY;
        this.filter.zRot = this.head.rotateAngleZ;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();

        if (this.young) {
            float scale = 1.5F / 2.0F;
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0.0F, 16.0F / 16.0F, 0.0F);
        }

        double d = 1.0D / 16.0D * 18.0D;
        poseStack.scale((float) d, (float) d, (float) d);
        poseStack.scale(1.01F, 1.01F, 1.01F);

        this.mask.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.filter.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.popPose();
    }
}