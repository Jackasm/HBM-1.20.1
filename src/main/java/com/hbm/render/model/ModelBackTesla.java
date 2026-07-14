package com.hbm.render.model;

import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ModelBackTesla extends HumanoidModel<LivingEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "back_tesla"), "main");

    public static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/models/armor/mod_tesla.png");

    private LivingEntity entity;

    public ModelBackTesla(ModelPart root) {
        super(root);
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0, 0, 0));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (this.entity == null) {
            this.entity = entity;
        }

        this.young = entity.isBaby();
        this.crouching = entity.isCrouching();
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer consumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        HBMResourceManager.armor_mod_tesla.renderAll(poseStack, consumer, packedLight, packedOverlay);
    }
}