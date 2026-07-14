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
public class ModelRubble extends Model {

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

    public ModelRubble() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Shape1 - 14x6x6
        partDefinition.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 14, 6, 6),
                PartPose.offset(-7.0F, 1.0F, 2.0F));

        // Shape2 - 6x13x5
        partDefinition.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 6, 13, 5),
                PartPose.offset(-7.0F, -6.0F, -5.0F));

        // Shape3 - 6x6x6
        partDefinition.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 6, 6, 6),
                PartPose.offset(1.0F, 1.0F, -5.0F));

        // Shape4 - 14x7x4 с поворотом Y 0.4363323F
        partDefinition.addOrReplaceChild("shape4",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 14, 7, 4),
                PartPose.offsetAndRotation(-7.0F, -7.0F, 2.0F,
                        0.0F, 0.4363323F, 0.0F));

        // Shape5 - 6x6x11
        partDefinition.addOrReplaceChild("shape5",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 6, 6, 11),
                PartPose.offset(0.0F, -6.0F, -5.0F));

        // Shape6 - 8x8x8
        partDefinition.addOrReplaceChild("shape6",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 8, 8, 8),
                PartPose.offset(-4.0F, -4.0F, -4.0F));

        // Shape7 - 6x5x7
        partDefinition.addOrReplaceChild("shape7",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 6, 5, 7),
                PartPose.offset(-7.0F, -5.0F, 1.0F));

        // Shape8 - 12x6x4 с поворотом Z -0.3490659F
        partDefinition.addOrReplaceChild("shape8",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 12, 6, 4),
                PartPose.offsetAndRotation(-6.0F, -1.0F, 3.0F,
                        0.0F, 0.0F, -0.3490659F));

        // Shape9 - 12x6x6 с поворотом Y -0.2094395F
        partDefinition.addOrReplaceChild("shape9",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 12, 6, 6),
                PartPose.offsetAndRotation(-6.0F, 2.0F, -3.0F,
                        0.0F, -0.2094395F, 0.0F));

        // Shape10 - 6x10x4 с поворотом Z -0.3490659F
        partDefinition.addOrReplaceChild("shape10",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 6, 10, 4),
                PartPose.offsetAndRotation(-5.0F, -3.0F, -6.0F,
                        0.0F, 0.0F, -0.3490659F));

        // Создаём ModelPart из определения
        this.shape1 = partDefinition.getChild("shape1").bake(16, 16);
        this.shape2 = partDefinition.getChild("shape2").bake(16, 16);
        this.shape3 = partDefinition.getChild("shape3").bake(16, 16);
        this.shape4 = partDefinition.getChild("shape4").bake(16, 16);
        this.shape5 = partDefinition.getChild("shape5").bake(16, 16);
        this.shape6 = partDefinition.getChild("shape6").bake(16, 16);
        this.shape7 = partDefinition.getChild("shape7").bake(16, 16);
        this.shape8 = partDefinition.getChild("shape8").bake(16, 16);
        this.shape9 = partDefinition.getChild("shape9").bake(16, 16);
        this.shape10 = partDefinition.getChild("shape10").bake(16, 16);
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
    }
}