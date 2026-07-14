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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ModelArmorBJ extends ModelArmorBase {

    public static final ModelLayerLocation[] BJ_LAYERS = new ModelLayerLocation[]{
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "bj_helmet"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "bj_chest"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "bj_legs"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "bj_boots"), "main")
    };

    private final ModelRendererObj jetpack;

    public ModelArmorBJ(ModelPart root, int type) {
        super(type, root);

        this.head = new ModelRendererObj(HBMResourceManager.armor_bj, "Head");
        this.body = new ModelRendererObj(HBMResourceManager.armor_bj, "Body");
        this.jetpack = new ModelRendererObj(HBMResourceManager.armor_bj, "Jetpack");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_bj, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_bj, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_bj, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_bj, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_bj, "LeftFoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_bj, "RightFoot");
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Копируем повороты тела для джетпака
        this.jetpack.rotationPointX = this.body.rotationPointX;
        this.jetpack.rotationPointY = this.body.rotationPointY;
        this.jetpack.rotationPointZ = this.body.rotationPointZ;
        this.jetpack.rotateAngleX = this.body.rotateAngleX;
        this.jetpack.rotateAngleY = this.body.rotateAngleY;
        this.jetpack.rotateAngleZ = this.body.rotateAngleZ;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        poseStack.pushPose();
        float scale = 0.0625F;

        if (this.type == 3) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/bj_eyepatch.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 2 || this.type == 5) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/bj_chest.png")));
            this.body.render(poseStack, consumer, packedLight, packedOverlay, scale);

            if (this.type == 5) {
                consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                        .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/bj_jetpack.png")));
                this.jetpack.render(poseStack, consumer, packedLight, packedOverlay, scale);
            }
            consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/bj_arm.png")));
            this.leftArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 1) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/bj_leg.png")));
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 0) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/bj_leg.png")));
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}