package com.hbm.render.model;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ModelArmorEnvsuit extends ModelArmorBase {

    public static final ModelLayerLocation[] ENV_LAYERS = new ModelLayerLocation[]{
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "env_helmet"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "env_chest"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "env_legs"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "env_boots"), "main")
    };

    private ModelRendererObj lamps;

    public ModelArmorEnvsuit(ModelPart root, int type) {
        super(type, root);

        this.head = new ModelRendererObj(HBMResourceManager.armor_envsuit, "Helmet");
        this.lamps = new ModelRendererObj(HBMResourceManager.armor_envsuit, "Lamps");
        this.body = new ModelRendererObj(HBMResourceManager.armor_envsuit, "Chest");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_envsuit, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_envsuit, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_envsuit, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_envsuit, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_envsuit, "LeftFoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_envsuit, "RightFoot");
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        poseStack.pushPose();
        float scale = 0.0625f;

        // Копируем повороты головы для ламп
        this.lamps.rotationPointX = this.head.rotationPointX;
        this.lamps.rotationPointY = this.head.rotationPointY;
        this.lamps.rotationPointZ = this.head.rotationPointZ;
        this.lamps.rotateAngleX = this.head.rotateAngleX;
        this.lamps.rotateAngleY = this.head.rotateAngleY;
        this.lamps.rotateAngleZ = this.head.rotateAngleZ;

        if (this.type == 3) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);
            // Лампы с максимальной яркостью
            this.lamps.render(poseStack, consumer, 0xF000F0, packedOverlay, scale);
        }
        if (this.type == 2) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_chest.png")));
            this.body.render(poseStack, consumer, packedLight, packedOverlay, scale);
            consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_arm.png")));
            this.leftArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 1) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_leg.png")));
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 0) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_leg.png")));
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}