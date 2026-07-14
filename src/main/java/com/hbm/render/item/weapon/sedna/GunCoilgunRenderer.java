package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class GunCoilgunRenderer extends BaseGunRenderer {

    private ResourceLocation COILGUN_TEX = null;
    private HFRWavefrontObject COILGUN_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (COILGUN_TEX == null) {
            COILGUN_TEX = HBMResourceManager.coilgun_tex;
        }
        return COILGUN_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (COILGUN_MODEL == null) {
            COILGUN_MODEL = HBMResourceManager.coilgun;
        }
        return COILGUN_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * 0.33F);
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float offset = 0.8F;
        float aimingProgress = getAimingProgress(partialTick);

        // Стартовые позиции
        double startX = -1.25F * offset;
        double startY = -1.5F * offset;
        double startZ = 2.5F * offset;

        // Целевые позиции при прицеливании
        double aimX = 0;
        double aimY = -7.5 / 8D;
        double aimZ = 1;

        // Интерполяция
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        // Смещение для рук
        float xOffset = -1F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 3f;
        }
        z = z + 1;

        float yOffset = -0.2F;
        float scale = 0.75f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);

        // Дополнительный поворот для катушечной пушки
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Получаем анимации
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] reload = HbmAnimations.getRelevantTransformation("RELOAD", 0);

        // Применяем анимацию отдачи
        if (recoil != null && recoil.length >= 1) {
            poseStack.translate(-1.5f - (float)recoil[0] * 0.5f, 0, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (recoil[0] * 45)));
            poseStack.translate(1.5f, 0, 0);
        }

        // Применяем анимацию перезарядки
        if (reload != null && reload.length >= 1) {
            poseStack.translate(-2.5f, 0, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (-reload[0] * 45)));
            poseStack.translate(2.5f, 0, 0);
        }

        // Рендерим всю модель
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        double scale = 3.0D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0, 0.25, 1.25);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.25D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(3f, 1f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -5.0D; // -20D * 0.25
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -0.25f, 0.5f);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга стандартных частей катушечной пушки
     */
    private void renderStandardParts(PoseStack poseStack, ItemStack stack,
                                     MultiBufferSource buffer, int packedLight,
                                     int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Поворот для правильной ориентации
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));

        // Рендерим всю модель
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    // Метод для рендеринга на столе модификаций
    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}