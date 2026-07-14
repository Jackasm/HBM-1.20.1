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
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class ModelGoggles extends HumanoidModel<LivingEntity> {

    public static final ModelLayerLocation GOGGLES = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "goggles"), "main");

    private final ModelPart goggles;
    private LivingEntity entity;

    public ModelGoggles(ModelPart root) {
        super(root);
        this.goggles = root.getChild("goggles");
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition goggles = partdefinition.addOrReplaceChild("goggles", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Shape1 - основная рамка
        goggles.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.5F, -5.0F, -4.5F, 9.0F, 3.0F, 1.0F),
                PartPose.ZERO);

        // Shape2 - верхняя часть
        goggles.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(0, 4)
                        .addBox(-4.5F, -5.0F, -3.5F, 9.0F, 2.0F, 5.0F),
                PartPose.ZERO);

        // Shape5 - правый глаз
        goggles.addOrReplaceChild("shape5",
                CubeListBuilder.create()
                        .texOffs(26, 0)
                        .addBox(1.0F, -4.5F, -5.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        // Shape6 - левый глаз
        goggles.addOrReplaceChild("shape6",
                CubeListBuilder.create()
                        .texOffs(20, 0)
                        .addBox(-3.0F, -4.5F, -5.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        // Shape7 - нижняя часть
        goggles.addOrReplaceChild("shape7",
                CubeListBuilder.create()
                        .texOffs(0, 11)
                        .addBox(-4.5F, -5.0F, 0.5F, 9.0F, 1.0F, 4.0F),
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity instanceof Player player) {
            this.young = player.isBaby();
            this.crouching = player.isCrouching();
        }

        // Копируем позицию и повороты головы
        this.goggles.x = this.head.x;
        this.goggles.y = this.head.y;
        this.goggles.z = this.head.z;
        this.goggles.xRot = this.head.xRot;
        this.goggles.yRot = this.head.yRot;
        this.goggles.zRot = this.head.zRot;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (entity != null) {
            this.setupAnim(entity, 0, 0, 0, 0, 0);
        }

        poseStack.pushPose();
        this.goggles.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}