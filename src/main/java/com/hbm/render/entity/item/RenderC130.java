package com.hbm.render.entity.item;

import com.hbm.entity.logic.EntityC130;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderC130 extends EntityRenderer<EntityC130> {

    public RenderC130(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityC130 entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // Позиция
        poseStack.translate(0, 0, 0);

        // Вращение по рысканью (yaw)
        float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks - 90.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        // Базовый поворот (как в оригинале - 90 градусов по Z)
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        // Вращение по тангажу (pitch)
        float pitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        // Включаем отсечение граней
        // В 1.20.1 это обычно включено по умолчанию

        // Рендерим основную модель
        var builder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.c130_0_tex));
        HBMResourceManager.c130.renderPart(poseStack, builder, "Plane", packedLight, OverlayTexture.NO_OVERLAY);

        // Вращение пропеллеров
        double spin = System.currentTimeMillis() * 15D % 360D;

        // Пропеллер 1
        poseStack.pushPose();
        poseStack.translate(10, 4.2, -20.5);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) spin));
        poseStack.translate(-10, -4.2, 20.5);
        HBMResourceManager.c130.renderPart(poseStack, builder, "Prop1", packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        // Пропеллер 2
        poseStack.pushPose();
        poseStack.translate(10, 4.2, -11.16);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) spin));
        poseStack.translate(-10, -4.2, 11.16);
        HBMResourceManager.c130.renderPart(poseStack, builder, "Prop2", packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        // Пропеллер 3
        poseStack.pushPose();
        poseStack.translate(10, 4.2, 11.16);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) spin));
        poseStack.translate(-10, -4.2, -11.16);
        HBMResourceManager.c130.renderPart(poseStack, builder, "Prop3", packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        // Пропеллер 4
        poseStack.pushPose();
        poseStack.translate(10, 4.2, 20.5);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) spin));
        poseStack.translate(-10, -4.2, -20.5);
        HBMResourceManager.c130.renderPart(poseStack, builder, "Prop4", packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityC130 entity) {
        return HBMResourceManager.c130_0_tex;
    }
}