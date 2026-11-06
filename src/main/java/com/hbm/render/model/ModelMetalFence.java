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
public class ModelMetalFence extends Model {

    private final ModelPart post;
    private final ModelPart panelNorth;
    private final ModelPart panelSouth;
    private final ModelPart panelEast;
    private final ModelPart panelWest;

    public ModelMetalFence() {
        super(RenderType::entityCutout);

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("post",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -8.0F, -2.0F, 4, 16, 4),
                PartPose.offset(0.0F, 8.0F, 0.0F));

        partDefinition.addOrReplaceChild("panel_west",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(0.0F, 0.0F, 0.0F, 8, 16, 0.01F),
                PartPose.offset(-8.0F, 0.0F, -0.005F));

        partDefinition.addOrReplaceChild("panel_east",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(0.0F, 0.0F, 0.0F, 8, 16, 0.01F),
                PartPose.offset(0F, 0.0F, -0.005F));

        partDefinition.addOrReplaceChild("panel_north",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(0.0F, 0.0F, 0.0F, 0.01F, 16, 8),
                PartPose.offset(-0.005F, 0.0F, -8.0F));

        partDefinition.addOrReplaceChild("panel_south",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(0.0F, 0.0F, 0.0F, 0.01F, 16, 8),
                PartPose.offset(-0.005F, 0.0F, 0F));

        this.post = partDefinition.getChild("post").bake(64, 64);
        this.panelNorth = partDefinition.getChild("panel_north").bake(64, 64);
        this.panelSouth = partDefinition.getChild("panel_south").bake(64, 64);
        this.panelEast = partDefinition.getChild("panel_east").bake(64, 64);
        this.panelWest = partDefinition.getChild("panel_west").bake(64, 64);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer,
                       int packedLight, int packedOverlay,
                       boolean north, boolean south, boolean east, boolean west,
                       boolean isPost) {

        if (isPost) {
            this.post.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        if (north) {
            this.panelNorth.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
        if (south) {
            this.panelSouth.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
        if (east) {
            this.panelEast.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
        if (west) {
            this.panelWest.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        this.post.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.panelNorth.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.panelSouth.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.panelEast.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.panelWest.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}