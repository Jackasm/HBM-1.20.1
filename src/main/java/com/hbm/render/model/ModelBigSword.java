package com.hbm.render.model;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class ModelBigSword extends EntityModel<Entity> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "big_sword"), "main");

    private final ModelPart HandleBottom;
    private final ModelPart HandleGrip;
    private final ModelPart Handle1;
    private final ModelPart Handle2;
    private final ModelPart Blade;
    private final ModelPart SBladeTip;

    public ModelBigSword(ModelPart root) {
        this.HandleBottom = root.getChild("HandleBottom");
        this.HandleGrip = root.getChild("HandleGrip");
        this.Handle1 = root.getChild("Handle1");
        this.Handle2 = root.getChild("Handle2");
        this.Blade = root.getChild("Blade");
        this.SBladeTip = root.getChild("SBladeTip");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition HandleBottom = partdefinition.addOrReplaceChild("HandleBottom",
                CubeListBuilder.create().texOffs(1, 1).addBox(0F, 0F, 0F, 1, 3, 3),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.7853982F, 0F, 0F));

        PartDefinition HandleGrip = partdefinition.addOrReplaceChild("HandleGrip",
                CubeListBuilder.create().texOffs(17, 1).addBox(0F, 0F, 0F, 1, 5, 2),
                PartPose.offset(0F, -4F, -1F));

        PartDefinition Handle1 = partdefinition.addOrReplaceChild("Handle1",
                CubeListBuilder.create().texOffs(25, 1).addBox(0F, -1F, 0F, 2, 1, 4),
                PartPose.offsetAndRotation(-0.5F, -3F, 0F, 0.2617994F, 0F, 0F));

        PartDefinition Handle2 = partdefinition.addOrReplaceChild("Handle2",
                CubeListBuilder.create().texOffs(41, 1).addBox(0F, -1F, -4F, 2, 1, 4),
                PartPose.offsetAndRotation(-0.5F, -3F, 0F, -0.2617994F, 0F, 0F));

        PartDefinition Blade = partdefinition.addOrReplaceChild("Blade",
                CubeListBuilder.create().texOffs(57, 1).addBox(0F, 0F, 0F, 3, 18, 1),
                PartPose.offsetAndRotation(0F, -22F, 1.5F, 0F, 1.570796F, 0F));

        PartDefinition SBladeTip = partdefinition.addOrReplaceChild("SBladeTip",
                CubeListBuilder.create().texOffs(2, 10).addBox(0F, 0F, 0F, 1, 2, 2),
                PartPose.offsetAndRotation(0F, -23.5F, 0F, -0.7853982F, 0F, 0F));

        return LayerDefinition.create(meshdefinition, 128, 32);
    }

    @Override
    public void setupAnim(@NotNull Entity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        HandleBottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        HandleGrip.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Handle1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Handle2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Blade.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        SBladeTip.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}