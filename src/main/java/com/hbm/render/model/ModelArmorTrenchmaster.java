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
public class ModelArmorTrenchmaster extends ModelArmorBase {

    public static final ModelLayerLocation[] TRENCH_LAYERS = new ModelLayerLocation[]{
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "trench_helmet"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "trench_chest"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "trench_legs"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "trench_boots"), "main")
    };

    private ModelRendererObj light;

    public ModelArmorTrenchmaster(ModelPart root, int type) {
        super(type, root);

        this.head = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "Helmet");
        this.light = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "Light");
        this.body = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "Chest");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "LeftBoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "RightBoot");
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        poseStack.pushPose();
        float scale = 0.0625f;

        // Копируем повороты головы для света
        this.light.rotationPointX = this.head.rotationPointX;
        this.light.rotationPointY = this.head.rotationPointY;
        this.light.rotationPointZ = this.head.rotationPointZ;
        this.light.rotateAngleX = this.head.rotateAngleX;
        this.light.rotateAngleY = this.head.rotateAngleY;
        this.light.rotateAngleZ = this.head.rotateAngleZ;

        if (this.type == 3) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);
            // Свет с максимальной яркостью
            this.light.render(poseStack, consumer, 0xF000F0, packedOverlay, scale);
        }
        if (this.type == 2) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_chest.png")));
            this.body.render(poseStack, consumer, packedLight, packedOverlay, scale);
            consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_arm.png")));
            this.leftArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 1) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_leg.png")));
            poseStack.translate(-0.01, 0, 0);
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            poseStack.translate(0.02, 0, 0);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 0) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_leg.png")));
            poseStack.translate(-0.01, 0, 0);
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            poseStack.translate(0.02, 0, 0);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}