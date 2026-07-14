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
public class ModelSteelRoof extends Model {

    private final ModelPart shape1;
    private final ModelPart shape2;
    private final ModelPart shape3;

    public ModelSteelRoof() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Shape1 - 16x1x16
        partDefinition.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 16, 1, 16),
                PartPose.offset(-8.0F, 23.0F, -8.0F));

        // Shape2 - 1x1x16
        partDefinition.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .texOffs(30, 15)
                        .addBox(0.0F, 0.0F, 0.0F, 1, 1, 16),
                PartPose.offset(-3.0F, 22.0F, -8.0F));

        // Shape3 - 16x2x2
        partDefinition.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .texOffs(0, 17)
                        .addBox(0.0F, 0.0F, 0.0F, 16, 2, 2),
                PartPose.offset(-8.0F, 21.0F, 2.0F));

        this.shape1 = partDefinition.getChild("shape1").bake(64, 32);
        this.shape2 = partDefinition.getChild("shape2").bake(64, 32);
        this.shape3 = partDefinition.getChild("shape3").bake(64, 32);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shape1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.shape3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}