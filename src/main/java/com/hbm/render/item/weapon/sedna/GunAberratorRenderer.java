package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class GunAberratorRenderer extends BaseGunRenderer {

    private ResourceLocation ABERRATOR_TEX = null;
    private HFRWavefrontObject ABERRATOR_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (ABERRATOR_TEX == null) {
            ABERRATOR_TEX = HBMResourceManager.aberrator_tex;
        }
        return ABERRATOR_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (ABERRATOR_MODEL == null) {
            ABERRATOR_MODEL = HBMResourceManager.aberrator;
        }
        return ABERRATOR_MODEL;
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
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        // Получаем все анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] rise = HbmAnimations.getRelevantTransformation("RISE", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] slide = HbmAnimations.getRelevantTransformation("SLIDE", 0);
        double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", 0);
        double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER", 0);
        double[] roll = HbmAnimations.getRelevantTransformation("ROLL", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] magroll = HbmAnimations.getRelevantTransformation("MAGROLL", 0);
        double[] sight = HbmAnimations.getRelevantTransformation("SIGHT", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Применяем анимацию RISE
        if (rise != null && rise.length >= 2) {
            poseStack.translate(0, rise[1], 0);
        }

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 1, -2.25);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, -1, 2.25);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 1) {
            poseStack.translate(0, -1, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) recoil[0]));
            poseStack.translate(0, 1, 4);
        }

        // Применяем анимацию ROLL
        if (roll != null && roll.length >= 3) {
            poseStack.translate(0, 1, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) roll[2]));
            poseStack.translate(0, -1, 0);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим прицел с анимацией SIGHT
        poseStack.pushPose();
        if (sight != null && sight.length >= 1) {
            poseStack.translate(0, 2.4375, -1.9375);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) sight[0]));
            poseStack.translate(0, -2.4375, 1.9375);
        }
        getWeaponModel().renderPart(poseStack, builder, "Sight", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим магазин с анимациями MAG и MAGROLL
        poseStack.pushPose();
        if (mag != null && mag.length >= 3) {
            poseStack.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        }

        if (magroll != null && magroll.length >= 3) {
            poseStack.translate(0, 1, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) magroll[2]));
            poseStack.translate(0, -1, 0);
        }

        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);

        // Рендерим патрон если есть
        if (bullet != null && bullet.length >= 3) {
            poseStack.translate((float) bullet[0], (float) bullet[1], (float) bullet[2]);
            getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
        }
        poseStack.popPose();

        // Рендерим затвор с анимацией SLIDE
        poseStack.pushPose();
        if (slide != null && slide.length >= 3) {
            poseStack.translate(0, 0, (float) slide[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Slide", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим курок с анимацией HAMMER
        poseStack.pushPose();
        poseStack.translate(0, 1.25, -3.625);
        if (hammer != null && hammer.length >= 1) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-45 + (float) hammer[0]));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(-45));
        }
        poseStack.translate(0, -1.25, 3.625);
        getWeaponModel().renderPart(poseStack, builder, "Hammer", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим дым
        double smokeScale = 0.5;
        poseStack.pushPose();
        poseStack.translate(0, 2, 4);
        if (recoil != null && recoil.length >= 1) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-(float) recoil[0]));
        }
        if (roll != null && roll.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-(float) roll[2]));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 2, 4);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        poseStack.scale(0.75f, 0.75f, 0.75f);
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();

        // Рендерим fireball (пламя)
        poseStack.pushPose();
        poseStack.translate(0, 2, -1.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderFireball(poseStack, gun.lastShot[0], partialTick);
        poseStack.popPose();

        // Рендерим эффект прицеливания (вращающиеся квадраты)
        renderAimingEffect(poseStack, partialTick);
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.0F * offset;
        double startY = -0.75F * offset;
        double startZ = 1.25F * offset;

        double aimX = 0.31;
        double aimY = -2.1 / 8D;
        double aimZ = 0.125;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = -0.25F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.0f;
        }

        float yOffset = 0.0F;
        float scale = 0.25f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        poseStack.translate(0, -1, 2.5);
        double scale = 1.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        // Для GUI (инвентаря) - как в оригинале
        double scale = 0.15D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(5, 0, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        // Для стола модификаций
        double scale = -12.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -1, 0.5);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        // Для GUI рендерим все части кроме анимированных
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Bullet", "Misc");

        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        // Для инвентаря/земли рендерим аналогично GUI
        renderGUIWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        // Для третьего лица рендерим все кроме анимированных частей
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Bullet", "Misc");

        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    // Вспомогательные методы для эффектов

    private void renderFireball(PoseStack poseStack, long lastShot, float partialTick) {
        // Реализация renderFireball для 1.20.1
        // (Нужно адаптировать оригинальный метод с использованием новой системы рендеринга)
        // RenderUtil.renderFireball(poseStack, lastShot, partialTick);
    }

    private void renderAimingEffect(PoseStack poseStack, float partialTick) {
        // Реализация вращающихся квадратов при прицеливании
        // (Нужно адаптировать оригинальный метод с Tessellator)
        float aimingProgress = getAimingProgress(partialTick);
        aimingProgress = Math.min(1F, aimingProgress * 2);

        // Временная заглушка - можно реализовать позже
        // RenderUtil.renderAimingSquares(poseStack, aimingProgress);
    }
}