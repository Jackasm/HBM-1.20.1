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

public class GunLasRifleRenderer extends BaseGunRenderer {


    private ResourceLocation LASRIFLE_TEX = null;
    private ResourceLocation LASRIFLE_MODS_TEX = null;
    private HFRWavefrontObject LASRIFLE_MODEL = null;
    private HFRWavefrontObject LASRIFLE_MODS_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (LASRIFLE_TEX == null) {
            LASRIFLE_TEX = HBMResourceManager.lasrifle_tex;
        }
        return LASRIFLE_TEX;
    }

    protected ResourceLocation getModsTexture() {
        if (LASRIFLE_MODS_TEX == null) {
            LASRIFLE_MODS_TEX = HBMResourceManager.lasrifle_mods_tex;
        }
        return LASRIFLE_MODS_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (LASRIFLE_MODEL == null) {
            LASRIFLE_MODEL = HBMResourceManager.lasrifle;
        }
        return LASRIFLE_MODEL;
    }

    protected HFRWavefrontObject getModsModel() {
        if (LASRIFLE_MODS_MODEL == null) {
            LASRIFLE_MODS_MODEL = HBMResourceManager.lasrifle_mods;
        }
        return LASRIFLE_MODS_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        boolean hasScope = hasScope(stack);
        return fov * (1 - aimingProgress * (hasScope ? 0.75F : 0.66F));
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {


        float offset = 0.8F;
        boolean hasScope = hasScope(stack);
        float aimingProgress = getAimingProgress(partialTick);

        // Стартовые позиции (без прицеливания)
        double startX = -1.5F * offset;
        double startY = -1.5F * offset;
        double startZ = 2.5F * offset;

        // Целевые позиции (с прицеливанием)
        double aimX = 0;
        double aimY = hasScope ? (-7.375 / 8D) : (-5.25 / 8D);
        double aimZ = hasScope ? 0.75 : 1.0;

        // Интерполяция между стартовыми и целевыми позициями
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        // Смещение для левой/правой руки
        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.5f;
        }

        float yOffset = 0.50F;
        float scale = 0.3125f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        // Если есть прицел и полностью прицелились, не рендерим оружие
        if (hasScope(stack) && getAimingProgress(partialTick) == 1.0f) {
            return;
        }

        GunItem gun = (GunItem) stack.getItem();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] lever = HbmAnimations.getRelevantTransformation("LEVER", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -1, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 1, 6);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основные части
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);

        // Рендерим прицел если есть
        if (hasScope(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
        }

        // Рендерим рычаг с анимацией LEVER
        poseStack.pushPose();
        if (lever != null && lever.length >= 3) {
            poseStack.translate(0, -0.375f, 2.375f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lever[0]));
            poseStack.translate(0, 0.375f, -2.375f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Lever", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим батарею с анимацией MAG
        poseStack.pushPose();
        if (mag != null && mag.length >= 3) {
            poseStack.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Battery", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим ствол (если нет дробовика)
        if (!hasShotgun(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }

        // Рендерим модификации
        VertexConsumer modsBuilder = buffer.getBuffer(RenderType.entityCutout(getModsTexture()));
        if (hasShotgun(stack)) {
            getModsModel().renderPart(poseStack, modsBuilder, "BarrelShotgun", packedLight, packedOverlay);
        }
        if (hasCapacitor(stack)) {
            getModsModel().renderPart(poseStack, modsBuilder, "UnderBarrel", packedLight, packedOverlay);
        }

        // Рендерим лазерную вспышку
        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 12);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        renderLaserFlash(poseStack, gun.lastShot[0], 150, 1.5D, 0xff0000);

        poseStack.translate(0, 0, -0.25f);
        renderLaserFlash(poseStack, gun.lastShot[0], 150, 0.75D, 0xff8000);

        poseStack.popPose();
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        double scale = 1.75D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0, 0, 1);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.06D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(11f, 4.5, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -6.25D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -1, -1);
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
     * Метод для рендеринга стандартных частей лазерной винтовки
     */
    private void renderStandardParts(PoseStack poseStack, ItemStack stack,
                                     MultiBufferSource buffer, int packedLight,
                                     int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Основные части
        List<String> mainParts = Arrays.asList(
                "Gun",
                "Stock",
                "Lever",
                "Battery"
        );

        for (String part : mainParts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }

        // Прицел если есть
        if (hasScope(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
        }

        // Стандартный ствол если нет дробовика
        if (!hasShotgun(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }

        // Модификации
        VertexConsumer modsBuilder = buffer.getBuffer(RenderType.entityCutout(getModsTexture()));
        if (hasShotgun(stack)) {
            getModsModel().renderPart(poseStack, modsBuilder, "BarrelShotgun", packedLight, packedOverlay);
        }
        if (hasCapacitor(stack)) {
            getModsModel().renderPart(poseStack, modsBuilder, "UnderBarrel", packedLight, packedOverlay);
        }
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

    // Проверка наличия прицела
    public boolean hasScope(ItemStack stack) {
        return !WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.LAS_AUTO);
    }

    // Проверка наличия дробовика
    public boolean hasShotgun(ItemStack stack) {
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.LAS_SHOTGUN);
    }

    // Проверка наличия конденсатора
    public boolean hasCapacitor(ItemStack stack) {
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.LAS_CAPACITOR);
    }
}