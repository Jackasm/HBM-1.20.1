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
public class ModelGeiger extends Model {

    private final ModelPart shape1;
    private final ModelPart shape2;
    private final ModelPart shape3;
    private final ModelPart shape4;
    private final ModelPart shape5;
    private final ModelPart shape6;
    private final ModelPart shape7;
    private final ModelPart shape8;

    public ModelGeiger() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Shape1 - 12x7x5
        partDefinition.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 12, 7, 5),
                PartPose.offset(-5.0F, 17.0F, 1.0F));

        // Shape2 - 7x1x1
        partDefinition.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(0, 30)
                        .addBox(0.0F, 0.0F, 0.0F, 7, 1, 1),
                PartPose.offset(-2.5F, 15.0F, 3.0F));

        // Shape3 - 1x2x1
        partDefinition.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(10, 18)
                        .addBox(0.0F, 0.0F, 0.0F, 1, 2, 1),
                PartPose.offset(-3.0F, 15.5F, 3.0F));

        // Shape4 - 1x2x1
        partDefinition.addOrReplaceChild("shape4",
                CubeListBuilder.create()
                        .texOffs(14, 18)
                        .addBox(0.0F, 0.0F, 0.0F, 1, 2, 1),
                PartPose.offset(4.0F, 15.5F, 3.0F));

        // Shape5 - 7x3x3
        partDefinition.addOrReplaceChild("shape5",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(0.0F, 0.0F, 0.0F, 7, 3, 3),
                PartPose.offset(-4.0F, 21.0F, -6.0F));

        // Shape6 - 2x6x2
        partDefinition.addOrReplaceChild("shape6",
                CubeListBuilder.create()
                        .texOffs(20, 12)
                        .addBox(0.0F, 0.0F, 0.0F, 2, 6, 2),
                PartPose.offset(-7.0F, 18.0F, 2.5F));

        // Shape7 - 3x2x2
        partDefinition.addOrReplaceChild("shape7",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(0.0F, 0.0F, 0.0F, 3, 2, 2),
                PartPose.offset(-7.0F, 22.0F, -5.5F));

        // Shape8 - 2x2x6
        partDefinition.addOrReplaceChild("shape8",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(0.0F, 0.0F, 0.0F, 2, 2, 6),
                PartPose.offset(-7.0F, 22.0F, -3.5F));

        this.shape1 = partDefinition.getChild("shape1").bake(64, 32);
        this.shape2 = partDefinition.getChild("shape2").bake(64, 32);
        this.shape3 = partDefinition.getChild("shape3").bake(64, 32);
        this.shape4 = partDefinition.getChild("shape4").bake(64, 32);
        this.shape5 = partDefinition.getChild("shape5").bake(64, 32);
        this.shape6 = partDefinition.getChild("shape6").bake(64, 32);
        this.shape7 = partDefinition.getChild("shape7").bake(64, 32);
        this.shape8 = partDefinition.getChild("shape8").bake(64, 32);
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
    }
}