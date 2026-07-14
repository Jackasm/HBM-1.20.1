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

public class ModelT45Helmet extends ModelArmorBase {

    public static final ModelLayerLocation T45_HELMET = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "t45_helmet"), "main");

    private final ModelPart helmet;

    public ModelT45Helmet(ModelPart root) {
        super(0, root);
        this.helmet = root.getChild("helmet");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition helmet = partdefinition.addOrReplaceChild("helmet", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        helmet.addOrReplaceChild("shape1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.ZERO);
        helmet.addOrReplaceChild("shape2",
                CubeListBuilder.create().texOffs(32, 0).addBox(1.0F, -6.0F, -5.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        helmet.addOrReplaceChild("shape3",
                CubeListBuilder.create().texOffs(40, 6).addBox(-5.0F, -6.0F, -5.466667F, 1.0F, 1.0F, 4.0F),
                PartPose.ZERO);
        helmet.addOrReplaceChild("shape4",
                CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 2.0F, 2.0F),
                PartPose.rotation(-0.7853982F, 0.0F, 0.0F));
        helmet.addOrReplaceChild("shape5",
                CubeListBuilder.create().texOffs(54, 0).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 1.0F, 2.0F),
                PartPose.rotation(-0.7853982F, 0.0F, 0.0F));
        helmet.addOrReplaceChild("shape6",
                CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -2.0F, -4.5F, 10.0F, 1.0F, 9.0F),
                PartPose.ZERO);
        helmet.addOrReplaceChild("shape7",
                CubeListBuilder.create().texOffs(32, 7).addBox(-1.5F, -3.0F, -4.5F, 1.0F, 1.0F, 1.0F),
                PartPose.rotation(-0.7853982F, 0.0F, 0.0F));
        helmet.addOrReplaceChild("shape8",
                CubeListBuilder.create().texOffs(32, 5).addBox(0.5F, -3.0F, -4.5F, 1.0F, 1.0F, 1.0F),
                PartPose.rotation(-0.7853982F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        this.helmet.x = this.head.rotationPointX;
        this.helmet.y = this.head.rotationPointY;
        this.helmet.z = this.head.rotationPointZ;
        this.helmet.xRot = this.head.rotateAngleX;
        this.helmet.yRot = this.head.rotateAngleY;
        this.helmet.zRot = this.head.rotateAngleZ;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        poseStack.pushPose();
        poseStack.scale(1.125F, 1.125F, 1.125F);
        poseStack.scale(1.0625F, 1.0625F, 1.0625F);
        this.helmet.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}