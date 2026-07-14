package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
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

public class GunCarbineRenderer extends BaseGunRenderer {

    private ResourceLocation CARBINE_TEX = null;
    private ResourceLocation CARBINE_BAYONET_TEX = null;
    private HFRWavefrontObject CARBINE_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (CARBINE_TEX == null) {
            CARBINE_TEX = HBMResourceManager.carbine_tex;
        }
        return CARBINE_TEX;
    }

    protected ResourceLocation getBayonetTexture() {
        if (CARBINE_BAYONET_TEX == null) {
            CARBINE_BAYONET_TEX = HBMResourceManager.carbine_bayonet_tex;
        }
        return CARBINE_BAYONET_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (CARBINE_MODEL == null) {
            CARBINE_MODEL = HBMResourceManager.carbine;
        }
        return CARBINE_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
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

        // Получаем анимации как в оригинале
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] slide = HbmAnimations.getRelevantTransformation("SLIDE", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", 0);
        double[] rel = HbmAnimations.getRelevantTransformation("REL", 0);
        double[] stab = HbmAnimations.getRelevantTransformation("STAB", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -1, -2);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 1, 2);
        }

        // Применяем анимацию LIFT
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, 0, -2);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 0, 2);
        }

        // Применяем анимацию STAB
        if (stab != null && stab.length >= 3) {
            poseStack.translate(stab[0], stab[1], stab[2]);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим затвор с анимацией SLIDE
        poseStack.pushPose();
        if (slide != null && slide.length >= 3) {
            poseStack.translate(0, 0, (float) slide[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Slide", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим магазин с анимациями MAG и REL/BULLET
        poseStack.pushPose();
        if (mag != null && mag.length >= 3) {
            poseStack.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        }

        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);

        // Рендерим патрон если есть анимация BULLET
        if (rel != null && rel.length >= 3) {
            poseStack.translate((float) rel[0], (float) rel[1], (float) rel[2]);
        }

        if (bullet != null && bullet.length >= 1 && bullet[0] != 1) {
            getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
        }
        poseStack.popPose();

        // Рендерим штык если есть
        if (hasBayonet(stack)) {
            VertexConsumer bayonetBuilder = buffer.getBuffer(RenderType.entityCutout(getBayonetTexture()));
            getWeaponModel().renderPart(poseStack, bayonetBuilder, "Bayonet", packedLight, packedOverlay);
        }

        // Рендерим дым как в оригинале
        double smokeScale = 0.25D;
        poseStack.pushPose();
        poseStack.translate(0, 1, 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 1D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 1, 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        // Перевод оригинального GL11.glTranslated(0, 0, 0.875)
        poseStack.translate(0, 0, 0.875);

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        // Стартовые позиции (без прицеливания)
        double startX = -1.5F * offset;
        double startY = -1.5F * offset;
        double startZ = 0.875F * offset;

        // Целевые позиции (с прицеливанием)
        double aimX = 0.06;
        double aimY = -5.3 / 8D;
        double aimZ = 0.25;

        // Интерполяция между стартовыми и целевыми позициями
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        // Смещение для левой/правой руки
        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.5f;
        }

        float yOffset = 0.25F;
        float scale = 0.5f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        // Масштаб и смещение как в оригинале
        double scale = 1.7D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0, 0, 0);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        // Реализация для GUI (инвентаря)
        if (hasBayonet(stack)) {
            // С штыком
            double scale = 0.06D;
            poseStack.scale((float) scale, (float) scale, (float) scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.translate(9, 4, 0);
        } else {
            // Без штыка
            double scale = 0.07D;
            poseStack.scale((float) scale, (float) scale, (float) scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.translate(9, 4, 0);
        }
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        // Для стола модификаций
        double scale = -7.75D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 0, -1.75);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        // Для GUI рендерим все части кроме анимированных
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Bullet"); // Исключаем только патрон

        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);

        // Рендерим штык если есть
        if (hasBayonet(stack)) {
            VertexConsumer bayonetBuilder = buffer.getBuffer(RenderType.entityCutout(getBayonetTexture()));
            getWeaponModel().renderPart(poseStack, bayonetBuilder, "Bayonet", packedLight, packedOverlay);
        }
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
        List<String> excludedParts = Arrays.asList("Bullet");

        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);

        // Рендерим штык если есть
        if (hasBayonet(stack)) {
            VertexConsumer bayonetBuilder = buffer.getBuffer(RenderType.entityCutout(getBayonetTexture()));
            getWeaponModel().renderPart(poseStack, bayonetBuilder, "Bayonet", packedLight, packedOverlay);
        }
    }

    // Проверка наличия штыка (из оригинала)
    public boolean hasBayonet(ItemStack stack) {
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.CARBINE_BAYONET);
    }

    // Метод для рендеринга на столе модификаций (отдельный метод как в оригинале)
    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);

        if (hasBayonet(stack)) {
            VertexConsumer bayonetBuilder = buffer.getBuffer(RenderType.entityCutout(getBayonetTexture()));
            getWeaponModel().renderPart(poseStack, bayonetBuilder, "Bayonet", packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}