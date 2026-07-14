package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMissileAntiBallistic;
import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.entity.missile.EntityMissileStealth;
import com.hbm.entity.missile.EntityMissileTier1.*;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderMissileGeneric extends EntityRenderer<Entity> {

    private final Quaternionf prevQuat = new Quaternionf();
    private boolean initialized = false;

    public RenderMissileGeneric(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull Entity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // === 1. Определяем модель и текстуру ===
        boolean isAntiBallistic = entity instanceof EntityMissileAntiBallistic;
        boolean isStealth = entity instanceof EntityMissileStealth;
        boolean isV2 = !isAntiBallistic && !isStealth;

        ResourceLocation texture;
        if (isAntiBallistic) {
            texture = HBMResourceManager.missileAA_tex;
        } else if (isStealth) {
            texture = HBMResourceManager.missileStealth_tex;
        } else {
            // Tier 1 ракеты (V2)
            if (entity instanceof EntityMissileGeneric) texture = HBMResourceManager.missileV2_HE_tex;
            else if (entity instanceof EntityMissileIncendiary) texture = HBMResourceManager.missileV2_IN_tex;
            else if (entity instanceof EntityMissileCluster) texture = HBMResourceManager.missileV2_CL_tex;
            else if (entity instanceof EntityMissileBunkerBuster) texture = HBMResourceManager.missileV2_BU_tex;
            else if (entity instanceof EntityMissileDecoy) texture = HBMResourceManager.missileV2_decoy_tex;
            else texture = HBMResourceManager.missileV2_HE_tex;
        }

        // === 2. Поворот по вектору движения ===
        double dx = entity.getX() - entity.xOld;
        double dy = entity.getY() - entity.yOld;
        double dz = entity.getZ() - entity.zOld;

        if (dx * dx + dy * dy + dz * dz < 0.0001) {
            // Fallback на углы
            float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks - 90.0F;
            float pitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));
            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        } else {
            double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
            Vec3 direction = new Vec3(dx / len, dy / len, dz / len);

            Quaternionf currentQuat = new Quaternionf().rotationTo(
                    new Vector3f(0, 1, 0),
                    new Vector3f((float) direction.x, (float) direction.y, (float) direction.z)
            );

            if (!initialized) {
                prevQuat.set(currentQuat);
                initialized = true;
            }

            float smoothFactor = 0.25F;
            Quaternionf smoothQuat = new Quaternionf(prevQuat).slerp(currentQuat, smoothFactor);
            prevQuat.set(smoothQuat);

            poseStack.mulPose(smoothQuat);
        }

        // === 3. Поворот из datawatcher (ROTATION) ===
        if (entity instanceof EntityMissileBaseNT missile) {
            byte rot = missile.getEntityData().get(EntityMissileBaseNT.ROTATION);
            switch (rot) {
                case 2 -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
                case 4 -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
                case 3 -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
                case 5 -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            }
        }

        // === 4. Масштаб ===
        float scale = 1.5F;
        poseStack.scale(scale, scale, scale);

        // === 5. Рендерим модель ===
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        if (isAntiBallistic) {
            HBMResourceManager.missileABM.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        } else if (isStealth) {
            HBMResourceManager.missileStealth.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        } else {
            HBMResourceManager.missileV2.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        // Fallback текстура
        return HBMResourceManager.missileV2_HE_tex;
    }
}