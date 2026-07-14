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
public class ModelArmorT51 extends ModelArmorBase {

    public static final ModelLayerLocation[] T51_LAYERS = new ModelLayerLocation[]{
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "t51_helmet"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "t51_chest"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "t51_legs"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "t51_boots"), "main")
    };

    private final int type;

    public ModelArmorT51(ModelPart root, int type) {
        super(type, root);
        this.type = type;

        // Загружаем модели из OBJ
        this.head = new ModelRendererObj(HBMResourceManager.armor_t51, "Helmet");
        this.body = new ModelRendererObj(HBMResourceManager.armor_t51, "Chest");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_t51, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_t51, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_t51, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_t51, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_t51, "LeftBoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_t51, "RightBoot");
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        poseStack.pushPose();
        float scale = 0.0625F;

        // Рендерим только нужную часть в зависимости от типа
        if (this.type == 3) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/t51_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);
        } else if (this.type == 2) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/t51_chest.png")));
            this.body.render(poseStack, consumer, packedLight, packedOverlay, scale);
            consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/t51_arm.png")));
            this.leftArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
        } else if (this.type == 1) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/t51_leg.png")));
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
        } else if (this.type == 0) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/t51_leg.png")));
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}