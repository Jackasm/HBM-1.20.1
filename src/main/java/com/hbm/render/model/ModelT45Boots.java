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

public class ModelT45Boots extends ModelArmorBase {

    public static final ModelLayerLocation T45_BOOTS = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "t45_boots"), "main");

    private final ModelPart leftleg;
    private final ModelPart rightleg;

    public ModelT45Boots(ModelPart root) {
        super(3, root);
        this.leftleg = root.getChild("leftleg");
        this.rightleg = root.getChild("rightleg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition leftleg = partdefinition.addOrReplaceChild("leftleg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightleg = partdefinition.addOrReplaceChild("rightleg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Левая нога
        leftleg.addOrReplaceChild("shape1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 9.5F, -4.0F, 4.0F, 2.0F, 6.0F),
                PartPose.ZERO);
        leftleg.addOrReplaceChild("shape3",
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 8.5F, -4.0F, 4.0F, 2.0F, 4.0F),
                PartPose.rotation(0.2617994F, 0.0F, 0.0F));

        // Правая нога
        rightleg.addOrReplaceChild("shape2",
                CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, 9.5F, -4.0F, 4.0F, 2.0F, 6.0F),
                PartPose.ZERO);
        rightleg.addOrReplaceChild("shape4",
                CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 8.5F, -4.0F, 4.0F, 2.0F, 4.0F),
                PartPose.rotation(0.2617994F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        this.leftleg.x = this.leftLeg.rotationPointX;
        this.leftleg.y = this.leftLeg.rotationPointY - 1.5F;
        this.leftleg.z = this.leftLeg.rotationPointZ;
        this.leftleg.xRot = this.leftLeg.rotateAngleX;
        this.leftleg.yRot = this.leftLeg.rotateAngleY;
        this.leftleg.zRot = this.leftLeg.rotateAngleZ;

        this.rightleg.x = this.rightLeg.rotationPointX;
        this.rightleg.y = this.rightLeg.rotationPointY - 1.5F;
        this.rightleg.z = this.rightLeg.rotationPointZ;
        this.rightleg.xRot = this.rightLeg.rotateAngleX;
        this.rightleg.yRot = this.rightLeg.rotateAngleY;
        this.rightleg.zRot = this.rightLeg.rotateAngleZ;

        if (this.crouching) {
            this.leftleg.z -= 0.5F;
            this.rightleg.z -= 0.5F;
            this.leftleg.y += 0.5F;
            this.rightleg.y += 0.5F;
        }
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }

        poseStack.pushPose();
        poseStack.scale(1.125F, 1.125F, 1.125F);
        this.leftleg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightleg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}