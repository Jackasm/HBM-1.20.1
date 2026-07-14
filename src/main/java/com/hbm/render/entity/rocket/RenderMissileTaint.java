package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.entity.missile.EntityMissileTier0.*;
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

public class RenderMissileTaint extends EntityRenderer<Entity> {

    private final Quaternionf prevQuat = new Quaternionf();
    private boolean initialized = false;

    public RenderMissileTaint(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull Entity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // === 1. Выбор текстуры ===
        ResourceLocation texture = HBMResourceManager.missileMicro_tex;
        if (entity instanceof EntityMissileTaint) texture = HBMResourceManager.missileMicroTaint_tex;
        else if (entity instanceof EntityMissileBHole) texture = HBMResourceManager.missileMicroBHole_tex;
        else if (entity instanceof EntityMissileSchrabidium) texture = HBMResourceManager.missileMicroSchrab_tex;
        else if (entity instanceof EntityMissileEMP) texture = HBMResourceManager.missileMicroEMP_tex;
        else if (entity instanceof EntityMissileTest) texture = HBMResourceManager.missileMicroTest_tex;

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
        // Micro ракеты меньше
        float scale = 1.0F;
        poseStack.scale(scale, scale, scale);

        // === 5. Рендерим модель ===
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));
        HBMResourceManager.missileMicro.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        if (entity instanceof EntityMissileTaint) return HBMResourceManager.missileMicroTaint_tex;
        if (entity instanceof EntityMissileBHole) return HBMResourceManager.missileMicroBHole_tex;
        if (entity instanceof EntityMissileSchrabidium) return HBMResourceManager.missileMicroSchrab_tex;
        if (entity instanceof EntityMissileEMP) return HBMResourceManager.missileMicroEMP_tex;
        if (entity instanceof EntityMissileTest) return HBMResourceManager.missileMicroTest_tex;
        return HBMResourceManager.missileMicro_tex;
    }
}