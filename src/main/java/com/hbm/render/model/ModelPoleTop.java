package com.hbm.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ModelPoleTop extends Model {

    private final ModelPart shape1;
    private final ModelPart shape2;
    private final ModelPart shape3;
    private final ModelPart shape4;
    private final ModelPart shape5;
    private final ModelPart shape6;

    public ModelPoleTop() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Shape1 - 12x8x12
        partDefinition.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 1)
                        .addBox(0.0F, 0.0F, 0.0F, 12, 8, 12),
                PartPose.offset(-6.0F, 16.0F, -6.0F));

        // Shape2 - 4x16x4
        partDefinition.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(0.0F, 0.0F, 0.0F, 4, 16, 4),
                PartPose.offset(4.0F, 4.0F, -8.0F));

        // Shape3 - 4x16x4
        partDefinition.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(0.0F, 0.0F, 0.0F, 4, 16, 4),
                PartPose.offset(4.0F, 4.0F, 4.0F));

        // Shape4 - 4x16x4
        partDefinition.addOrReplaceChild("shape4",
                CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(0.0F, 0.0F, 0.0F, 4, 16, 4),
                PartPose.offset(-8.0F, 4.0F, -8.0F));

        // Shape5 - 4x16x4
        partDefinition.addOrReplaceChild("shape5",
                CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(0.0F, 0.0F, 0.0F, 4, 16, 4),
                PartPose.offset(-8.0F, 4.0F, 4.0F));

        // Shape6 - 4x2x4
        partDefinition.addOrReplaceChild("shape6",
                CubeListBuilder.create()
                        .texOffs(0, 47)
                        .addBox(0.0F, 0.0F, 0.0F, 4, 2, 4),
                PartPose.offset(-2.0F, 14.0F, -2.0F));

        this.shape1 = partDefinition.getChild("shape1").bake(64, 64);
        this.shape2 = partDefinition.getChild("shape2").bake(64, 64);
        this.shape3 = partDefinition.getChild("shape3").bake(64, 64);
        this.shape4 = partDefinition.getChild("shape4").bake(64, 64);
        this.shape5 = partDefinition.getChild("shape5").bake(64, 64);
        this.shape6 = partDefinition.getChild("shape6").bake(64, 64);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shape1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape5.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape6.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}