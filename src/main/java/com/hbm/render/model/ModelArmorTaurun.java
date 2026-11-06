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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ModelArmorTaurun extends ModelArmorBase {

    public static final ModelLayerLocation[] TAURUN_LAYERS = new ModelLayerLocation[]{
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "taurun_helmet"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "taurun_chest"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "taurun_legs"), "main"),
            new ModelLayerLocation(ResLocation(RefStrings.MODID, "taurun_boots"), "main")
    };

    private static final ModelArmorTaurun[] models = new ModelArmorTaurun[4];

    public static ModelArmorTaurun getArmorModel(EquipmentSlot slot, LivingEntity living) {
        int index = switch (slot) {
            case HEAD -> 0;
            case CHEST -> 1;
            case LEGS -> 2;
            case FEET -> 3;
            default -> 0;
        };

        if (models[index] == null) {
            Minecraft mc = Minecraft.getInstance();
            ModelPart root = mc.getEntityModels().bakeLayer(TAURUN_LAYERS[index]);
            models[index] = new ModelArmorTaurun(root, index);
        }

        // Обновляем entity для анимации
        if (living != null) {
            models[index].setEntity(living);
            models[index].young = living.isBaby();
        }

        return models[index];
    }

    public ModelArmorTaurun(ModelPart root, int type) {
        super(type, root);

        this.head = new ModelRendererObj(HBMResourceManager.armor_taurun, "Helmet");
        this.body = new ModelRendererObj(HBMResourceManager.armor_taurun, "Chest");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_taurun, "LeftArm").setRotationPoint(5.0F, 2.0F, 0.0F);
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_taurun, "RightArm").setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_taurun, "LeftLeg").setRotationPoint(1.9F, 12.0F, 0.0F);
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_taurun, "RightLeg").setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_taurun, "LeftBoot").setRotationPoint(1.9F, 12.0F, 0.0F);
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_taurun, "RightBoot").setRotationPoint(-1.9F, 12.0F, 0.0F);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }
        poseStack.pushPose();
        float scale = 0.0625f;

        if (this.type == 0) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/taurun_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 1) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/taurun_chest.png")));
            this.body.render(poseStack, consumer, packedLight, packedOverlay, scale);
            consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/taurun_arm.png")));
            this.leftArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 2) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/taurun_leg.png")));
            poseStack.translate(-0.01, 0, 0);
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            poseStack.translate(0.02, 0, 0);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }
        if (this.type == 3) {
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutoutNoCull(ResLocation(RefStrings.MODID, "textures/models/armor/taurun_leg.png")));
            poseStack.translate(-0.01, 0, 0);
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            poseStack.translate(0.02, 0, 0);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}