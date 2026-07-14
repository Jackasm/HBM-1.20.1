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
public class ModelBroadcaster extends Model {

    private final ModelPart shape1;
    private final ModelPart shape2;
    private final ModelPart shape3;
    private final ModelPart shape4;

    public ModelBroadcaster() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Shape1 - 14x10x8
        partDefinition.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 14, 10, 8),
                PartPose.offset(-7.0F, 14.0F, -4.0F));

        // Shape2 - 2x3x2
        partDefinition.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(4, 21)
                        .addBox(0.0F, 0.0F, 0.0F, 2, 3, 2),
                PartPose.offset(-5.0F, 11.0F, -1.0F));

        // Shape3 - 1x11x1
        partDefinition.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(0.0F, 0.0F, 0.0F, 1, 11, 1),
                PartPose.offset(-4.5F, 0.0F, -0.5F));

        // Shape4 - 3x2x1
        partDefinition.addOrReplaceChild("shape4",
                CubeListBuilder.create()
                        .texOffs(4, 18)
                        .addBox(0.0F, 0.0F, 0.0F, 3, 2, 1),
                PartPose.offset(2.0F, 12.0F, -0.5F));

        this.shape1 = partDefinition.getChild("shape1").bake(64, 32);
        this.shape2 = partDefinition.getChild("shape2").bake(64, 32);
        this.shape3 = partDefinition.getChild("shape3").bake(64, 32);
        this.shape4 = partDefinition.getChild("shape4").bake(64, 32);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shape1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}