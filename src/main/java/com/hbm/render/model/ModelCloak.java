package com.hbm.render.model;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class ModelCloak extends HumanoidModel<LivingEntity> {

    public static final ModelLayerLocation CLOAK = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "cloak"), "main");

    private final ModelPart cloak;
    private LivingEntity cachedEntity;

    public ModelCloak(ModelPart root) {
        super(root);
        this.cloak = root.getChild("cloak");
    }

    public void setEntity(LivingEntity entity) {
        this.cachedEntity = entity;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition cloak = partdefinition.addOrReplaceChild("cloak", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        cloak.addOrReplaceChild("cape",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity instanceof Player player) {
            this.young = player.isBaby();
            this.crouching = player.isCrouching();
        }
        this.cachedEntity = entity; // Сохраняем сущность
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (!(this.cachedEntity instanceof Player player)) return;

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);

        // Вычисляем движение для анимации плаща
        double d3 = player.xCloak + (player.xCloakO - player.xCloak) * (1.0F - alpha) - (player.xo + (player.getX() - player.xo) * (1.0F - alpha));
        double d4 = player.yCloak + (player.yCloakO - player.yCloak) * (1.0F - alpha) - (player.yo + (player.getY() - player.yo) * (1.0F - alpha));
        double d0 = player.zCloak + (player.zCloakO - player.zCloak) * (1.0F - alpha) - (player.zo + (player.getZ() - player.zo) * (1.0F - alpha));
        float f4 = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * (1.0F - alpha);
        double d1 = Mth.sin(f4 * (float) Math.PI / 180.0F);
        double d2 = (-Mth.cos(f4 * (float) Math.PI / 180.0F));
        float f5 = (float) d4 * 10.0F;

        if (f5 < -6.0F) f5 = -6.0F;
        if (f5 > 32.0F) f5 = 32.0F;

        float f6 = (float) (d3 * d1 + d0 * d2) * 100.0F;
        float f7 = (float) (d3 * d2 - d0 * d1) * 100.0F;

        if (f6 < 0.0F) f6 = 0.0F;

        float f8 = player.oBob + (player.bob - player.oBob) * (1.0F - alpha);
        f5 += Mth.sin((player.walkDistO + (player.walkDist - player.walkDistO) * (1.0F - alpha)) * 6.0F) * 32.0F * f8;

        if (player.isCrouching()) f5 += 25.0F;

        // Применяем повороты для анимации плаща
        poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + f6 / 2.0F + f5));
        poseStack.mulPose(Axis.ZP.rotationDegrees(f7 / 2.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-f7 / 2.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        this.cloak.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}