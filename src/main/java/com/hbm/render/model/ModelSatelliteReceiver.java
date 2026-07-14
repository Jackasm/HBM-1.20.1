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
public class ModelSatelliteReceiver extends Model {

    private final ModelPart shape1;
    private final ModelPart shape2;
    private final ModelPart shape3;
    private final ModelPart shape4;
    private final ModelPart shape5;
    private final ModelPart shape6;
    private final ModelPart shape7;
    private final ModelPart shape8;
    private final ModelPart shape9;

    public ModelSatelliteReceiver() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Shape1 - 12x16x12
        partDefinition.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 12, 16, 12),
                PartPose.offset(-6.0F, 8.0F, -6.0F));

        // Shape2 - 8x8x2 с поворотом
        partDefinition.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(10, 28)
                        .addBox(3.0F, 9.0F, -8.0F, 8, 8, 2),
                PartPose.offsetAndRotation(-3.0F, 6.0F, 0.0F, -0.2617994F, -0.4363323F, 0.0F));

        // Shape3 - 8x2x3 с поворотом
        partDefinition.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(0, 39)
                        .addBox(3.0F, 7.0F, -10.0F, 8, 2, 3),
                PartPose.offsetAndRotation(-3.0F, 6.0F, 0.0F, -0.2617994F, -0.4363323F, 0.0F));

        // Shape4 - 2x8x3 с поворотом
        partDefinition.addOrReplaceChild("shape4",
                CubeListBuilder.create()
                        .texOffs(0, 28)
                        .addBox(1.0F, 9.0F, -10.0F, 2, 8, 3),
                PartPose.offsetAndRotation(-3.0F, 6.0F, 0.0F, -0.2617994F, -0.4363323F, 0.0F));

        // Shape5 - 2x8x3 с поворотом
        partDefinition.addOrReplaceChild("shape5",
                CubeListBuilder.create()
                        .texOffs(0, 28)
                        .addBox(11.0F, 9.0F, -10.0F, 2, 8, 3),
                PartPose.offsetAndRotation(-3.0F, 6.0F, 0.0F, -0.2617994F, -0.4363323F, 0.0F));

        // Shape6 - 8x2x3 с поворотом
        partDefinition.addOrReplaceChild("shape6",
                CubeListBuilder.create()
                        .texOffs(0, 39)
                        .addBox(3.0F, 17.0F, -10.0F, 8, 2, 3),
                PartPose.offsetAndRotation(-3.0F, 6.0F, 0.0F, -0.2617994F, -0.4363323F, 0.0F));

        // Shape7 - 2x2x3 с поворотом
        partDefinition.addOrReplaceChild("shape7",
                CubeListBuilder.create()
                        .texOffs(0, 44)
                        .addBox(6.0F, 12.0F, -11.0F, 2, 2, 3),
                PartPose.offsetAndRotation(-3.0F, 6.0F, 0.0F, -0.2617994F, -0.4363323F, 0.0F));

        // Shape8 - 1x1x3 с поворотом
        partDefinition.addOrReplaceChild("shape8",
                CubeListBuilder.create()
                        .texOffs(0, 49)
                        .addBox(6.5F, 12.5F, -14.0F, 1, 1, 3),
                PartPose.offsetAndRotation(-3.0F, 6.0F, 0.0F, -0.2617994F, -0.4363323F, 0.0F));

        // Shape9 - 2x2x2 с поворотом
        partDefinition.addOrReplaceChild("shape9",
                CubeListBuilder.create()
                        .texOffs(0, 53)
                        .addBox(6.0F, 12.0F, -16.0F, 2, 2, 2),
                PartPose.offsetAndRotation(-3.0F, 6.0F, 0.0F, -0.2617994F, -0.4363323F, 0.0F));

        this.shape1 = partDefinition.getChild("shape1").bake(64, 64);
        this.shape2 = partDefinition.getChild("shape2").bake(64, 64);
        this.shape3 = partDefinition.getChild("shape3").bake(64, 64);
        this.shape4 = partDefinition.getChild("shape4").bake(64, 64);
        this.shape5 = partDefinition.getChild("shape5").bake(64, 64);
        this.shape6 = partDefinition.getChild("shape6").bake(64, 64);
        this.shape7 = partDefinition.getChild("shape7").bake(64, 64);
        this.shape8 = partDefinition.getChild("shape8").bake(64, 64);
        this.shape9 = partDefinition.getChild("shape9").bake(64, 64);
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
        this.shape7.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape8.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape9.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}