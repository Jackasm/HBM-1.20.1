package com.hbm.render.entity.item;

import com.hbm.entity.grenade.EntityGrenadeASchrab;
import com.hbm.entity.grenade.EntityGrenadeMk2;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RenderGrenade extends EntityRenderer<Entity> {

    public RenderGrenade(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull Entity grenade, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight) {

        poseStack.pushPose();

        // Позиционирование
        poseStack.translate(0, 0.125F, 0);

        // Вращение
        float yaw = grenade.yRotO + (grenade.getYRot() - grenade.yRotO) * partialTicks - 90.0F;
        float pitch = grenade.xRotO + (grenade.getXRot() - grenade.xRotO) * partialTicks;

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        // Выбор модели в зависимости от типа гранаты
        if (grenade instanceof EntityGrenadeMk2) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(0.125F, 0.125F, 0.125F);

            ResourceLocation texture = HBMResourceManager.grenade_mk2_tex;
            VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
            HBMResourceManager.grenade_frag.renderAll(poseStack, builder, packedLight, OverlayTexture.NO_OVERLAY);

        } else if (grenade instanceof EntityGrenadeASchrab) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(0.125F, 0.125F, 0.125F);

            ResourceLocation texture = HBMResourceManager.grenade_aschrab_tex;
            VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
            HBMResourceManager.grenade_aschrab.renderAll(poseStack, builder, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity grenade) {
        if (grenade instanceof EntityGrenadeMk2) {
            return HBMResourceManager.grenade_mk2_tex;
        }
        if (grenade instanceof EntityGrenadeASchrab) {
            return HBMResourceManager.grenade_aschrab_tex;
        }
        return null;
    }

    @Override
    public boolean shouldRender(@NotNull Entity entity, @NotNull Frustum frustum,
                                double camX, double camY, double camZ) {
        return true;
    }
}