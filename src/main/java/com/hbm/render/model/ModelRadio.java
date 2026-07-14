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
public class ModelRadio extends Model {

    private final ModelPart box;
    private final ModelPart plate;
    private final ModelPart lever;

    public ModelRadio() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Box - 8x14x4
        partDefinition.addOrReplaceChild("box",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 8, 14, 4),
                PartPose.offset(-4.0F, 9.0F, -12.0F));

        // Plate - 7x13x1
        partDefinition.addOrReplaceChild("plate",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(0.0F, 0.0F, 0.0F, 7, 13, 1),
                PartPose.offset(-3.5F, 9.5F, -12.5F));

        // Lever - 2x8x2
        partDefinition.addOrReplaceChild("lever",
                CubeListBuilder.create()
                        .texOffs(16, 18)
                        .addBox(0.0F, -1.0F, -1.0F, 2, 8, 2),
                PartPose.offset(4.0F, 16.0F, -10.0F));

        this.box = partDefinition.getChild("box").bake(32, 32);
        this.plate = partDefinition.getChild("plate").bake(32, 32);
        this.lever = partDefinition.getChild("lever").bake(32, 32);
    }

    public void renderModel(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, int rotation) {
        this.box.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.plate.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        this.lever.xRot = -(rotation / 180F * (float) Math.PI);
        this.lever.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.box.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.plate.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.lever.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}