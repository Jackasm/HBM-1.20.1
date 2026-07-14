package com.hbm.render.loader;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModelRendererObj {

    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float originPointX;
    public float originPointY;
    public float originPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public float offsetX;
    public float offsetY;
    public float offsetZ;

    public boolean doRender = true;

    String[] parts;
    HFRWavefrontObject model;

    public ModelRendererObj(HFRWavefrontObject model, String... parts) {
        this.model = model;
        this.parts = parts;
    }

    public ModelRendererObj setPosition(float x, float y, float z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        return this;
    }

    public ModelRendererObj setRotationPoint(float x, float y, float z) {
        this.originPointX = this.rotationPointX = x;
        this.originPointY = this.rotationPointY = y;
        this.originPointZ = this.rotationPointZ = z;
        return this;
    }

    public void copyTo(ModelRendererObj obj) {
        obj.offsetX = offsetX;
        obj.offsetY = offsetY;
        obj.offsetZ = offsetZ;
        obj.rotateAngleX = rotateAngleX;
        obj.rotateAngleY = rotateAngleY;
        obj.rotateAngleZ = rotateAngleZ;
        obj.rotationPointX = rotationPointX;
        obj.rotationPointY = rotationPointY;
        obj.rotationPointZ = rotationPointZ;
    }

    public void copyRotationFrom(ModelPart model) {
        offsetX = model.x;
        offsetY = model.y;
        offsetZ = model.z;
        rotateAngleX = model.xRot;
        rotateAngleY = model.yRot;
        rotateAngleZ = model.zRot;
        rotationPointX = model.x;
        rotationPointY = model.y;
        rotationPointZ = model.z;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float scale) {
        if (parts == null || !doRender)
            return;

        poseStack.pushPose();

        // Смещение к точке вращения
        poseStack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

        // Повороты (порядок: Z, Y, X)
        if (this.rotateAngleZ != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotation(this.rotateAngleZ));
        }
        if (this.rotateAngleY != 0.0F) {
            poseStack.mulPose(Axis.YP.rotation(this.rotateAngleY));
        }
        if (this.rotateAngleX != 0.0F) {
            poseStack.mulPose(Axis.XP.rotation(this.rotateAngleX));
        }

        // Смещение обратно к origin и offset
        poseStack.translate(-this.originPointX * scale, -this.originPointY * scale, -this.originPointZ * scale);
        poseStack.translate(-this.offsetX * scale, -this.offsetY * scale, -this.offsetZ * scale);

        // Масштаб
        poseStack.scale(scale, scale, scale);

        // Рендер модели
        if (parts.length > 0) {
            for (String part : parts) {
                model.renderPart(poseStack, vertexConsumer, part, packedLight, packedOverlay);
            }
        } else {
            model.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void postRender(PoseStack poseStack, float scale) {
        if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
            if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                poseStack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            }
        } else {
            poseStack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

            if (this.rotateAngleZ != 0.0F) {
                poseStack.mulPose(Axis.ZP.rotation(this.rotateAngleZ));
            }
            if (this.rotateAngleY != 0.0F) {
                poseStack.mulPose(Axis.YP.rotation(this.rotateAngleY));
            }
            if (this.rotateAngleX != 0.0F) {
                poseStack.mulPose(Axis.XP.rotation(this.rotateAngleX));
            }
        }
    }
}