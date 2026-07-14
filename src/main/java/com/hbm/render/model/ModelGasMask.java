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

public class ModelGasMask extends ModelArmorBase {

    public static final ModelLayerLocation GAS_MASK = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "gas_mask"), "main");

    private final ModelPart mask;
    protected LivingEntity entity;

    public ModelGasMask(ModelPart root) {
        super(0, root);
        this.mask = root.getChild("mask");
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition mask = partdefinition.addOrReplaceChild("mask", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Shape1 - основная часть маски
        mask.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 3.0F),
                PartPose.ZERO);

        // Shape2 - левый глаз
        mask.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(22, 0)
                        .addBox(-3.0F, -5.0F, -4.533F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        // Shape3 - правый глаз
        mask.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(22, 0)
                        .addBox(1.0F, -5.0F, -4.5F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        // Shape4 - нижняя часть (фильтр)
        mask.addOrReplaceChild("shape4",
                CubeListBuilder.create()
                        .texOffs(0, 11)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(0F, -2.0F, -4.0F, -0.7853982F, 0.0F, 0.0F));

// Shape5 - фильтр
        mask.addOrReplaceChild("shape5",
                CubeListBuilder.create()
                        .texOffs(0, 15)
                        .addBox(-1.5F, -1.5F, -1.5F, 3.0F, 4.0F, 3.0F),
                PartPose.offsetAndRotation(0F, -1.0F, -6.0F, -0.7853982F, 0.0F, 0.0F));

        // Shape6 - нижняя часть маски
        mask.addOrReplaceChild("shape6",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-4.0F, -5.0F, -1.0F, 8.0F, 1.0F, 5.0F),
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Копируем повороты головы
        this.mask.x = this.head.rotationPointX;
        this.mask.y = this.head.rotationPointY;
        this.mask.z = this.head.rotationPointZ;
        this.mask.xRot = this.head.rotateAngleX;
        this.mask.yRot = this.head.rotateAngleY;
        this.mask.zRot = this.head.rotateAngleZ;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        if (this.young) {
            poseStack.pushPose();
            float scale = 1.5F / 2.0F;
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0.0F, 16.0F / 16.0F, 0.0F);
        } else {
            poseStack.pushPose();
            poseStack.scale(1.15F, 1.15F, 1.15F);
        }

        this.mask.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}