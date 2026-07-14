package com.hbm.render.model;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ModelGlasses extends ModelArmorBase {

    public ModelGlasses(int type) {
        super(type, createEmptyModelPart());

        this.head = new ModelRendererObj(HBMResourceManager.armor_goggles);
        this.body = new ModelRendererObj(HBMResourceManager.armor_bj, "Body");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_bj, "LeftArm").setRotationPoint(5.0F, 2.0F, 0.0F);
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_bj, "RightArm").setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_bj, "LeftLeg").setRotationPoint(1.9F, 12.0F, 0.0F);
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_bj, "RightLeg").setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_bj, "LeftFoot").setRotationPoint(1.9F, 12.0F, 0.0F);
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_bj, "RightFoot").setRotationPoint(-1.9F, 12.0F, 0.0F);
    }

    private static ModelPart createEmptyModelPart() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0), 0);
        PartDefinition part = mesh.getRoot();
        return part.bake(64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        poseStack.pushPose();

        if (this.type == 0) {
            float scale = 0.6f;
            poseStack.scale(scale,scale,scale);
            VertexConsumer gogglesConsumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutout(HBMResourceManager.ash_glasses_tex));
            this.head.render(poseStack, gogglesConsumer, packedLight, packedOverlay, 0.1F);
        }

        poseStack.popPose();
    }

}