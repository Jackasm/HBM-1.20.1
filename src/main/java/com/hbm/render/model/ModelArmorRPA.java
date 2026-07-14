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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ModelArmorRPA extends ModelArmorBase {

    public static final ModelLayerLocation[] RPA_LAYERS = new ModelLayerLocation[]{
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "rpa_helmet"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "rpa_chest"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "rpa_legs"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "rpa_boots"), "main")
    };

    private ModelRendererObj fan;
    private ModelRendererObj glow;

    public ModelArmorRPA(ModelPart root, int type) {
        super(type, root);

        this.head = new ModelRendererObj(HBMResourceManager.armor_remnant, "Head");
        this.body = new ModelRendererObj(HBMResourceManager.armor_remnant, "Body");
        this.fan = new ModelRendererObj(HBMResourceManager.armor_remnant, "Fan");
        this.glow = new ModelRendererObj(HBMResourceManager.armor_remnant, "Glow");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_remnant, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_remnant, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_remnant, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_remnant, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_remnant, "LeftBoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_remnant, "RightBoot");
    }


    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        poseStack.pushPose();
        float scale = 0.0625f;


        // Копируем повороты тела для свечения
        this.glow.rotationPointX = this.body.rotationPointX;
        this.glow.rotationPointY = this.body.rotationPointY;
        this.glow.rotationPointZ = this.body.rotationPointZ;
        this.glow.rotateAngleX = this.body.rotateAngleX;
        this.glow.rotateAngleY = this.body.rotateAngleY;
        this.glow.rotateAngleZ = this.body.rotateAngleZ;

        if (this.type == 3) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/rpa_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 2) {
            // Руки
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/rpa_arm.png")));
            this.leftArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumer, packedLight, packedOverlay, scale);

            // Тело
            consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/rpa_chest.png")));
            this.body.render(poseStack, consumer, packedLight, packedOverlay, scale);

            // Свечение
            this.glow.render(poseStack, consumer, 0xF000F0, packedOverlay, scale);

            // Вентилятор с вращением
            poseStack.pushPose();
            double px = 0.0625D;
            poseStack.translate(this.body.rotationPointX * px, this.body.rotationPointY * px, this.body.rotationPointZ * px);

            if (this.body.rotateAngleZ != 0.0F) {
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotation(this.body.rotateAngleZ));
            }
            if (this.body.rotateAngleY != 0.0F) {
                poseStack.mulPose(com.mojang.math.Axis.YP.rotation(this.body.rotateAngleY));
            }
            if (this.body.rotateAngleX != 0.0F) {
                poseStack.mulPose(com.mojang.math.Axis.XP.rotation(this.body.rotateAngleX));
            }

            poseStack.translate(0, 4.875 * px, 0);
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-System.currentTimeMillis() / 2 % 360));
            poseStack.translate(0, -4.875 * px, 0);
            this.fan.render(poseStack, consumer, packedLight, packedOverlay, scale);
            poseStack.popPose();
        }
        if (this.type == 1) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/rpa_leg.png")));
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 0) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/rpa_leg.png")));
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}