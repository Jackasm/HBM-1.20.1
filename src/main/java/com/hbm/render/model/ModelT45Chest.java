package com.hbm.render.model;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class ModelT45Chest extends ModelArmorBase {

    public static final ModelLayerLocation T45_CHEST = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "t45_chest"), "main");

    private final ModelPart chest;
    private final ModelPart leftarm;
    private final ModelPart rightarm;

    public ModelT45Chest(ModelPart root) {
        super(1, root);
        this.chest = root.getChild("chest");
        this.leftarm = root.getChild("leftarm");
        this.rightarm = root.getChild("rightarm");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftarm = partdefinition.addOrReplaceChild("leftarm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightarm = partdefinition.addOrReplaceChild("rightarm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Основная часть груди
        chest.addOrReplaceChild("shape1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        // Верхняя накладка
        chest.addOrReplaceChild("shape2",
                CubeListBuilder.create().texOffs(0, 16).addBox(-3.5F, 2.0F, -3.5F, 7.0F, 5.0F, 2.0F),
                PartPose.ZERO);
        // Левая точка
        chest.addOrReplaceChild("shape3",
                CubeListBuilder.create().texOffs(0, 23).addBox(-2.5F, 7.0F, -3.0F, 1.0F, 1.0F, 1.0F),
                PartPose.ZERO);
        // Правая точка
        chest.addOrReplaceChild("shape4",
                CubeListBuilder.create().texOffs(0, 25).addBox(1.5F, 7.0F, -3.0F, 1.0F, 1.0F, 1.0F),
                PartPose.ZERO);
        // Наклонная часть
        chest.addOrReplaceChild("shape5",
                CubeListBuilder.create().texOffs(0, 28).addBox(-3.5F, 0.0F, -3.5F, 7.0F, 2.0F, 2.0F),
                PartPose.rotation(-0.6108652F, 0.0F, 0.0F));
        // Задние накладки
        chest.addOrReplaceChild("shape10",
                CubeListBuilder.create().texOffs(32, 30).addBox(1.0F, 4.0F, 2.0F, 2.0F, 6.0F, 2.0F),
                PartPose.ZERO);
        chest.addOrReplaceChild("shape11",
                CubeListBuilder.create().texOffs(42, 30).addBox(-3.0F, 4.0F, 2.0F, 2.0F, 6.0F, 2.0F),
                PartPose.ZERO);
        chest.addOrReplaceChild("shape12",
                CubeListBuilder.create().texOffs(26, 9).addBox(1.5F, -2.0F, 2.0F, 1.0F, 6.0F, 1.0F),
                PartPose.ZERO);
        chest.addOrReplaceChild("shape13",
                CubeListBuilder.create().texOffs(26, 0).addBox(-2.5F, -2.0F, 2.0F, 1.0F, 6.0F, 1.0F),
                PartPose.ZERO);
        chest.addOrReplaceChild("shape14",
                CubeListBuilder.create().texOffs(20, 18).addBox(-1.0F, 1.0F, 2.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        chest.addOrReplaceChild("shape15",
                CubeListBuilder.create().texOffs(21, 23).addBox(-1.5F, 0.5F, 3.0F, 3.0F, 3.0F, 1.0F),
                PartPose.rotation(0.7853982F, 0.0F, 0.0F));

        // Левая рука
        leftarm.addOrReplaceChild("shape6",
                CubeListBuilder.create().texOffs(48, 0).addBox(4.25F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        leftarm.addOrReplaceChild("shape8",
                CubeListBuilder.create().texOffs(32, 16).addBox(4.25F, 1.0F, -3.0F, 5.0F, 6.0F, 6.0F),
                PartPose.ZERO);
        leftarm.addOrReplaceChild("shape17",
                CubeListBuilder.create().texOffs(0, 55).addBox(4.25F, 9.0F, -2.0F, 3.0F, 1.0F, 4.0F),
                PartPose.rotation(-0.5235988F, 0.0F, 0.0F));
        leftarm.addOrReplaceChild("shape18",
                CubeListBuilder.create().texOffs(90, 0).addBox(4.25F, -3.0F, -3.0F, 5.0F, 3.0F, 6.0F),
                PartPose.rotation(0.2617994F, 0.0F, 0.0F));

        // Правая рука
        rightarm.addOrReplaceChild("shape7",
                CubeListBuilder.create().texOffs(32, 0).addBox(-8.25F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        rightarm.addOrReplaceChild("shape9",
                CubeListBuilder.create().texOffs(0, 34).addBox(-9.25F, 1.0F, -3.0F, 5.0F, 6.0F, 6.0F),
                PartPose.ZERO);
        rightarm.addOrReplaceChild("shape16",
                CubeListBuilder.create().texOffs(0, 48).addBox(-8.25F, 9.0F, -2.0F, 3.0F, 1.0F, 4.0F),
                PartPose.rotation(0.5235988F, 0.0F, 0.0F));
        rightarm.addOrReplaceChild("shape19",
                CubeListBuilder.create().texOffs(66, 0).addBox(-9.25F, -3.0F, -3.0F, 5.0F, 3.0F, 6.0F),
                PartPose.rotation(-0.2617994F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        this.chest.x = this.body.rotationPointX;
        this.chest.y = this.body.rotationPointY;
        this.chest.z = this.body.rotationPointZ;
        this.chest.xRot = this.body.rotateAngleX;
        this.chest.yRot = this.body.rotateAngleY;
        this.chest.zRot = this.body.rotateAngleZ;

        this.leftarm.x = this.leftArm.rotationPointX;
        this.leftarm.y = this.leftArm.rotationPointY;
        this.leftarm.z = this.leftArm.rotationPointZ;
        this.leftarm.xRot = this.leftArm.rotateAngleX;
        this.leftarm.yRot = this.leftArm.rotateAngleY;
        this.leftarm.zRot = this.leftArm.rotateAngleZ;

        this.rightarm.x = this.rightArm.rotationPointX;
        this.rightarm.y = this.rightArm.rotationPointY;
        this.rightarm.z = this.rightArm.rotationPointZ;
        this.rightarm.xRot = this.rightArm.rotateAngleX;
        this.rightarm.yRot = this.rightArm.rotateAngleY;
        this.rightarm.zRot = this.rightArm.rotateAngleZ;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        poseStack.pushPose();
        poseStack.scale(1.125F, 1.125F, 1.125F);
        this.chest.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.pushPose();
        poseStack.translate(-0.37D, 0.05D, 0.0D);
        this.leftarm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
        poseStack.translate(0.37D, 0.05D, 0.0D);
        this.rightarm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}