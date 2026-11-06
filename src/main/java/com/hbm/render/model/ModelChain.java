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
public class ModelChain extends Model {

    private final ModelPart diag1;
    private final ModelPart diag2;
    private final ModelPart wallPlate;

    public ModelChain() {
        super(RenderType::entityCutoutNoCull);

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("diag1",
                CubeListBuilder.create()
                        .texOffs(6, 0)
                        .addBox(-1.5F, -8.0F, -0.005F, 3.0F, 16.0F, 0.01F),
                PartPose.ZERO);

        root.addOrReplaceChild("diag2",
                CubeListBuilder.create()
                        .texOffs(6, 0)
                        .addBox(-1.5F, -8.0F, -0.005F, 3.0F, 16.0F, 0.01F),
                PartPose.ZERO);

        root.addOrReplaceChild("wall_plate",
                CubeListBuilder.create()
                        .texOffs(6, 0)
                        .addBox(-1.5F, -8.0F, -0.005F, 3.0F, 16.0F, 0.01F),
                PartPose.ZERO);

        this.diag1 = root.getChild("diag1").bake(16, 16);
        this.diag2 = root.getChild("diag2").bake(16, 16);
        this.wallPlate = root.getChild("wall_plate").bake(16, 16);
    }

    public void renderVertical(@NotNull PoseStack poseStack, @NotNull VertexConsumer consumer,
                               int light, int overlay, float r, float g, float b, float a) {
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45F));
        diag1.render(poseStack, consumer, light, overlay, r, g, b, a);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(135F));
        diag2.render(poseStack, consumer, light, overlay, r, g, b, a);
        poseStack.popPose();
    }

    public void renderWall(@NotNull PoseStack poseStack, @NotNull VertexConsumer consumer,
                           int light, int overlay, float r, float g, float b, float a) {
        wallPlate.render(poseStack, consumer, light, overlay, r, g, b, a);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer consumer,
                               int light, int overlay, float r, float g, float b, float a) {
    }
}