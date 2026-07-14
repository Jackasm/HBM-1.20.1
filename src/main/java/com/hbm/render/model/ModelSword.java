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

public class ModelSword extends EntityModel<Entity> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "sword_redstone"), "main");

    private final ModelPart GripBottom;
    private final ModelPart GripHandle;
    private final ModelPart Shield;
    private final ModelPart Blade;
    private final ModelPart BladeTip;
    private final ModelPart Shield1;
    private final ModelPart Shield2;

    public ModelSword(ModelPart root) {
        this.GripBottom = root.getChild("GripBottom");
        this.GripHandle = root.getChild("GripHandle");
        this.Shield = root.getChild("Shield");
        this.Blade = root.getChild("Blade");
        this.BladeTip = root.getChild("BladeTip");
        this.Shield1 = root.getChild("Shield1");
        this.Shield2 = root.getChild("Shield2");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition GripBottom = partdefinition.addOrReplaceChild("GripBottom",
                CubeListBuilder.create().texOffs(0, 17).addBox(0F, 0F, 0F, 3, 3, 1),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition GripHandle = partdefinition.addOrReplaceChild("GripHandle",
                CubeListBuilder.create().texOffs(8, 2).addBox(0F, 0F, 0F, 2, 5, 1),
                PartPose.offset(0.5F, -5F, 0F));

        PartDefinition Shield = partdefinition.addOrReplaceChild("Shield",
                CubeListBuilder.create().texOffs(14, 5).addBox(0F, 0F, 0F, 6, 1, 3),
                PartPose.offset(-1.5F, -6F, -1F));

        PartDefinition Blade = partdefinition.addOrReplaceChild("Blade",
                CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 3, 16, 1),
                PartPose.offset(0F, -22F, 0F));

        PartDefinition BladeTip = partdefinition.addOrReplaceChild("BladeTip",
                CubeListBuilder.create().texOffs(8, 0).addBox(0F, 0F, 0F, 2, 1, 1),
                PartPose.offset(0.5F, -23F, 0F));

        PartDefinition Shield1 = partdefinition.addOrReplaceChild("Shield1",
                CubeListBuilder.create().texOffs(14, 0).addBox(0F, 0F, 0F, 1, 1, 4),
                PartPose.offset(-2F, -6.5F, -1.5F));

        PartDefinition Shield2 = partdefinition.addOrReplaceChild("Shield2",
                CubeListBuilder.create().texOffs(24, 0).addBox(0F, 0F, 0F, 1, 1, 4),
                PartPose.offset(4F, -6.5F, -1.5F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull Entity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        GripBottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        GripHandle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Shield.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Blade.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        BladeTip.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Shield1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Shield2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}