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
public class ModelStatue extends Model {

    private final ModelPart shape1;
    private final ModelPart shape2;
    private final ModelPart shape3;
    private final ModelPart shape4;
    private final ModelPart shape6;
    private final ModelPart shape7;
    private final ModelPart shape8;
    private final ModelPart shape9;
    private final ModelPart shape10;

    public ModelStatue() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Shape1 - 16x8x16 (основание)
        partDefinition.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0F, 0F, 0F, 16, 8, 16),
                PartPose.offset(-8F, 16F, -8F));

        // Shape2 - 4x12x4 (левая нога)
        partDefinition.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(0F, 0F, 0F, 4, 12, 4),
                PartPose.offset(-4F, 4F, -2F));

        // Shape3 - 4x12x4 (правая нога)
        partDefinition.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(16, 24)
                        .addBox(0F, 0F, 0F, 4, 12, 4),
                PartPose.offset(0F, 4F, -2F));

        // Shape4 - 8x12x4 (тело)
        partDefinition.addOrReplaceChild("shape4",
                CubeListBuilder.create()
                        .texOffs(32, 40)
                        .addBox(0F, 0F, 0F, 8, 12, 4),
                PartPose.offset(-4F, -8F, -2F));

        // Shape6 - 4x8x4 с поворотом (правая рука)
        partDefinition.addOrReplaceChild("shape6",
                CubeListBuilder.create()
                        .texOffs(0, 40)
                        .addBox(0F, 0F, -2F, 4, 8, 4),
                PartPose.offsetAndRotation(4F, -8F, 0F, 0.5235988F, 0F, 0F));

        // Shape7 - 4x8x4 с поворотом (левая рука)
        partDefinition.addOrReplaceChild("shape7",
                CubeListBuilder.create()
                        .texOffs(16, 40)
                        .addBox(-4F, 0F, -2F, 4, 8, 4),
                PartPose.offsetAndRotation(-4F, -8F, 0F, -0.0872665F, 0F, 0F));

        // Shape8 - 4x8x4 с поворотом (правый предмет)
        partDefinition.addOrReplaceChild("shape8",
                CubeListBuilder.create()
                        .texOffs(0, 52)
                        .addBox(-2F, 0F, -2F, 4, 8, 4),
                PartPose.offsetAndRotation(6F, -2F, 3F, 1.22173F, 0F, 0F));

        // Shape9 - 4x8x4 с поворотом (левый предмет)
        partDefinition.addOrReplaceChild("shape9",
                CubeListBuilder.create()
                        .texOffs(16, 52)
                        .addBox(0F, 0F, -2F, 4, 8, 4),
                PartPose.offsetAndRotation(-8F, -1F, -0.5F, 0.2617994F, 0F, 0F));

        // Shape10 - 8x8x8 с поворотом (голова)
        partDefinition.addOrReplaceChild("shape10",
                CubeListBuilder.create()
                        .texOffs(32, 24)
                        .addBox(-4F, -8F, -4F, 8, 8, 8),
                PartPose.offsetAndRotation(0F, -8F, 0F, -0.1745329F, 0F, 0F));

        this.shape1 = partDefinition.getChild("shape1").bake(64, 64);
        this.shape2 = partDefinition.getChild("shape2").bake(64, 64);
        this.shape3 = partDefinition.getChild("shape3").bake(64, 64);
        this.shape4 = partDefinition.getChild("shape4").bake(64, 64);
        this.shape6 = partDefinition.getChild("shape6").bake(64, 64);
        this.shape7 = partDefinition.getChild("shape7").bake(64, 64);
        this.shape8 = partDefinition.getChild("shape8").bake(64, 64);
        this.shape9 = partDefinition.getChild("shape9").bake(64, 64);
        this.shape10 = partDefinition.getChild("shape10").bake(64, 64);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shape1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape6.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape7.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape8.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape9.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape10.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}