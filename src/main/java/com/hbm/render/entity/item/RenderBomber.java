package com.hbm.render.entity.item;

import com.hbm.entity.logic.EntityBomber;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class RenderBomber extends EntityRenderer<EntityBomber> {

    public RenderBomber(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityBomber entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        // Поворот по рысканью
        float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        // Поворот по крену
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        // Поворот по тангажу
        float pitch = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        int style = entity.getEntityData().get(EntityBomber.Style.STYLE);

        // Выбор текстуры
        ResourceLocation texture = switch (style) {
            case 0, 1, 3 -> HBMResourceManager.dornier_1_tex;
            case 2 -> HBMResourceManager.dornier_2_tex;
            case 4 -> HBMResourceManager.dornier_4_tex;
            case 5 -> HBMResourceManager.b29_0_tex;
            case 6 -> HBMResourceManager.b29_1_tex;
            case 7 -> HBMResourceManager.b29_2_tex;
            case 8 -> HBMResourceManager.b29_3_tex;
            default -> HBMResourceManager.dornier_1_tex;
        };

        // Колебание крыльев
        float wingSway = (float) Math.sin((entity.tickCount + partialTicks) * 0.05) * 10;
        poseStack.mulPose(Axis.XP.rotationDegrees(wingSway));

        // Масштабирование и рендеринг модели
        if (style >= 0 && style <= 4) {
            // Dornier (малый бомбардировщик)
            poseStack.scale(5.0F, 5.0F, 5.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            HBMResourceManager.dornier.renderAll(poseStack, buffer, texture, packedLight, OverlayTexture.NO_OVERLAY);
        } else if (style >= 5 && style <= 8) {
            // B-29 (большой бомбардировщик)
            float scale = 30.0F / 3.1F;
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            HBMResourceManager.b29.renderAll(poseStack, buffer, texture, packedLight, OverlayTexture.NO_OVERLAY);
        } else {
            // Fallback
            HBMResourceManager.dornier.renderAll(poseStack, buffer, texture, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityBomber entity) {
        int style = entity.getEntityData().get(EntityBomber.Style.STYLE);
        return switch (style) {
            case 0, 1, 3 -> HBMResourceManager.dornier_1_tex;
            case 2 -> HBMResourceManager.dornier_2_tex;
            case 4 -> HBMResourceManager.dornier_4_tex;
            case 5 -> HBMResourceManager.b29_0_tex;
            case 6 -> HBMResourceManager.b29_1_tex;
            case 7 -> HBMResourceManager.b29_2_tex;
            case 8 -> HBMResourceManager.b29_3_tex;
            default -> HBMResourceManager.dornier_1_tex;
        };
    }
}