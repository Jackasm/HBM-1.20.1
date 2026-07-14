package com.hbm.render.model;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class ModelHat extends ModelArmorBase {

    public static final ModelLayerLocation HAT = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "hat"), "main");

    public ModelRendererObj hat;

    public ModelHat(ModelPart root) {
        super(0, root);
        this.hat = new ModelRendererObj(HBMResourceManager.armor_hat);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Копируем повороты головы
        this.hat.rotationPointX = this.head.rotationPointX;
        this.hat.rotationPointY = this.head.rotationPointY;
        this.hat.rotationPointZ = this.head.rotationPointZ;
        this.hat.rotateAngleX = this.head.rotateAngleX;
        this.hat.rotateAngleY = this.head.rotateAngleY;
        this.hat.rotateAngleZ = this.head.rotateAngleZ;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }

        poseStack.pushPose();
        float scale = 0.0625f;
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/armor_hat.png")));
        this.hat.render(poseStack, consumer, packedLight, packedOverlay, scale);
        poseStack.popPose();
    }
}