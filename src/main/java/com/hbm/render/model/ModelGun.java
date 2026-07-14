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
public class ModelGun extends Model {

    private final ModelPart shape1;
    private final ModelPart shape2;
    private final ModelPart shape3;
    private final ModelPart shape4;
    private final ModelPart shape5;
    private final ModelPart shape6;
    private final ModelPart shape7;
    private final ModelPart shape8;
    private final ModelPart shape9;
    private final ModelPart shape10;
    private final ModelPart shape11;
    private final ModelPart shape12;
    private final ModelPart shape13;
    private final ModelPart shape14;
    private final ModelPart shape15;
    private final ModelPart shape16;

    public ModelGun() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Shape1 - 6x12x4
        partDefinition.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0F, 0F, 0F, 6, 12, 4),
                PartPose.offset(0F, -4F, -1F));

        // Shape2 - 3x3x3
        partDefinition.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(52, 0)
                        .addBox(0F, 0F, 0F, 3, 3, 3),
                PartPose.offset(4F, -7F, -0.5F));

        // Shape3 - 15x3x3
        partDefinition.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(28, 58)
                        .addBox(0F, 0F, 0F, 15, 3, 3),
                PartPose.offset(-15F, -7F, -0.5F));

        // Shape4 - 1x2x1 с поворотом
        partDefinition.addOrReplaceChild("shape4",
                CubeListBuilder.create()
                        .texOffs(0, 61)
                        .addBox(0F, 0F, 0F, 1, 2, 1),
                PartPose.offsetAndRotation(2F, -3F, 0.5F, 0F, 0F, 0.715585F));

        // Shape5 - 2x2x1 с поворотом
        partDefinition.addOrReplaceChild("shape5",
                CubeListBuilder.create()
                        .texOffs(0, 57)
                        .addBox(0F, 0F, 0F, 2, 2, 1),
                PartPose.offsetAndRotation(-13.5F, -8F, 0.5F, 0F, 0F, 0.6108652F));

        // Shape6 - 4x3x2
        partDefinition.addOrReplaceChild("shape6",
                CubeListBuilder.create()
                        .texOffs(52, 7)
                        .addBox(0F, 0F, 0F, 4, 3, 2),
                PartPose.offset(0F, -6.5F, 0F));

        // Shape7 - 6x5x3
        partDefinition.addOrReplaceChild("shape7",
                CubeListBuilder.create()
                        .texOffs(46, 49)
                        .addBox(0F, 0F, 0F, 6, 5, 3),
                PartPose.offset(-15F, -3F, -0.5F));

        // Shape8 - 12x1x2
        partDefinition.addOrReplaceChild("shape8",
                CubeListBuilder.create()
                        .texOffs(22, 0)
                        .addBox(0F, 0F, 0F, 12, 1, 2),
                PartPose.offset(-15F, -4F, 0F));

        // Shape9 - 3x3x3
        partDefinition.addOrReplaceChild("shape9",
                CubeListBuilder.create()
                        .texOffs(52, 13)
                        .addBox(0F, 0F, 0F, 3, 3, 3),
                PartPose.offset(-3F, -4F, -0.5F));

        // Shape10 - 6x2x2
        partDefinition.addOrReplaceChild("shape10",
                CubeListBuilder.create()
                        .texOffs(11, 60)
                        .addBox(0F, 0F, 0F, 6, 2, 2),
                PartPose.offset(-9F, -3F, 0F));

        // Shape11 - 2x4x3
        partDefinition.addOrReplaceChild("shape11",
                CubeListBuilder.create()
                        .texOffs(35, 50)
                        .addBox(0F, 0F, 0F, 2, 4, 3),
                PartPose.offset(-9F, -1F, -0.5F));

        // Shape12 - 7x1x1
        partDefinition.addOrReplaceChild("shape12",
                CubeListBuilder.create()
                        .texOffs(12, 57)
                        .addBox(0F, 0F, 0F, 7, 1, 1),
                PartPose.offset(-7F, 2F, 0.5F));

        // Shape13 - 4x1x4
        partDefinition.addOrReplaceChild("shape13",
                CubeListBuilder.create()
                        .texOffs(0, 51)
                        .addBox(0F, 0F, 0F, 4, 1, 4),
                PartPose.offset(0F, -5F, -1F));

        // Shape14 - 3x5x2 с поворотом
        partDefinition.addOrReplaceChild("shape14",
                CubeListBuilder.create()
                        .texOffs(0, 43)
                        .addBox(0F, 0F, 0F, 3, 5, 2),
                PartPose.offsetAndRotation(7F, -7F, 0F, 0F, 0F, 0.7853982F));

        // Shape15 - 3x1x3 с поворотом
        partDefinition.addOrReplaceChild("shape15",
                CubeListBuilder.create()
                        .texOffs(0, 38)
                        .addBox(0F, 0F, 0F, 3, 1, 3),
                PartPose.offsetAndRotation(-9F, 3F, -0.5F, 0F, 0F, -2.792527F));

        // Shape16 - 2x3x1 с поворотом
        partDefinition.addOrReplaceChild("shape16",
                CubeListBuilder.create()
                        .texOffs(0, 17)
                        .addBox(0F, 0F, 0F, 2, 3, 1),
                PartPose.offsetAndRotation(-1F, -2F, 0.5F, 0F, 0F, 0.2617994F));

        this.shape1 = partDefinition.getChild("shape1").bake(64, 64);
        this.shape2 = partDefinition.getChild("shape2").bake(64, 64);
        this.shape3 = partDefinition.getChild("shape3").bake(64, 64);
        this.shape4 = partDefinition.getChild("shape4").bake(64, 64);
        this.shape5 = partDefinition.getChild("shape5").bake(64, 64);
        this.shape6 = partDefinition.getChild("shape6").bake(64, 64);
        this.shape7 = partDefinition.getChild("shape7").bake(64, 64);
        this.shape8 = partDefinition.getChild("shape8").bake(64, 64);
        this.shape9 = partDefinition.getChild("shape9").bake(64, 64);
        this.shape10 = partDefinition.getChild("shape10").bake(64, 64);
        this.shape11 = partDefinition.getChild("shape11").bake(64, 64);
        this.shape12 = partDefinition.getChild("shape12").bake(64, 64);
        this.shape13 = partDefinition.getChild("shape13").bake(64, 64);
        this.shape14 = partDefinition.getChild("shape14").bake(64, 64);
        this.shape15 = partDefinition.getChild("shape15").bake(64, 64);
        this.shape16 = partDefinition.getChild("shape16").bake(64, 64);
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
        this.shape10.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape11.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape12.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape13.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape14.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape15.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape16.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}