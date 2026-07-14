package com.hbm.render.model;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ModelJetPack extends HumanoidModel<LivingEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "jetpack"), "main");

    private final ModelPart JetPack;
    private LivingEntity entity;

    public ModelJetPack(ModelPart root) {
        super(root);
        this.JetPack = root.getChild("JetPack");
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition JetPack = partdefinition.addOrReplaceChild("JetPack", CubeListBuilder.create(), PartPose.offset(0, 0, -2));

        PartDefinition Pack = JetPack.addOrReplaceChild("Pack",
                CubeListBuilder.create()
                        .texOffs(12, 10)
                        .addBox(-2F, 3F, 0F, 4, 6, 1),
                PartPose.offset(0, 0, 0));

        PartDefinition Tank1 = JetPack.addOrReplaceChild("Tank1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.5F, 2F, 0.5F, 3, 8, 3),
                PartPose.offset(0, 0, 0));

        PartDefinition Tank2 = JetPack.addOrReplaceChild("Tank2",
                CubeListBuilder.create()
                        .texOffs(0, 11)
                        .addBox(-3.5F, 2F, 0.5F, 3, 8, 3),
                PartPose.offset(0, 0, 0));

        PartDefinition Tip1 = JetPack.addOrReplaceChild("Tip1",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(1F, 1F, 1F, 2, 1, 2),
                PartPose.offset(0, 0, 0));

        PartDefinition Tip2 = JetPack.addOrReplaceChild("Tip2",
                CubeListBuilder.create()
                        .texOffs(0, 25)
                        .addBox(-3F, 1F, 1F, 2, 1, 2),
                PartPose.offset(0, 0, 0));

        PartDefinition Duct1 = JetPack.addOrReplaceChild("Duct1",
                CubeListBuilder.create()
                        .texOffs(8, 22)
                        .addBox(1F, 9.5F, 1F, 2, 1, 2),
                PartPose.offset(0, 0, 0));

        PartDefinition Duct2 = JetPack.addOrReplaceChild("Duct2",
                CubeListBuilder.create()
                        .texOffs(8, 25)
                        .addBox(-3F, 9.5F, 1F, 2, 1, 2),
                PartPose.offset(0, 0, 0));

        PartDefinition Thruster1 = JetPack.addOrReplaceChild("Thruster1",
                CubeListBuilder.create()
                        .texOffs(12, 0)
                        .addBox(0.5F, 10.5F, 0.5F, 3, 2, 3),
                PartPose.offset(0, 0, 0));

        PartDefinition Thruster2 = JetPack.addOrReplaceChild("Thruster2",
                CubeListBuilder.create()
                        .texOffs(12, 5)
                        .addBox(-3.5F, 10.5F, 0.5F, 3, 2, 3),
                PartPose.offset(0, 0, 0));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity instanceof Player player) {
            this.young = player.isBaby();
            this.crouching = player.isCrouching();
        }

        // Копируем позицию и повороты тела
        this.JetPack.x = this.body.x;
        this.JetPack.y = this.body.y;
        this.JetPack.z = this.body.z;
        this.JetPack.xRot = this.body.xRot;
        this.JetPack.yRot = this.body.yRot;
        this.JetPack.zRot = this.body.zRot;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (entity != null) {
            this.setupAnim(entity, 0, 0, 0, 0, 0);
        }

        poseStack.pushPose();
        this.JetPack.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}