package com.hbm.render.item.weapon.sedna;

import com.hbm.items.ModGunItems;
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
import java.util.Objects;

public class GunMinigunRenderer extends BaseGunRenderer {

    private final String textureName;
    private ResourceLocation MINIGUN_TEX = null;
    private HFRWavefrontObject MINIGUN_MODEL = null;

    public GunMinigunRenderer(String textureName) {
        this.textureName = textureName;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (MINIGUN_TEX == null) {
            // Определяем текстуру в зависимости от имени
            if (Objects.equals(textureName, "minigun_lacunae")) {
                MINIGUN_TEX = HBMResourceManager.minigun_lacunae_tex;
            } else {
                MINIGUN_TEX = HBMResourceManager.minigun_tex;
            }
        }
        return MINIGUN_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (MINIGUN_MODEL == null) {
            MINIGUN_MODEL = HBMResourceManager.minigun;
        }
        return MINIGUN_MODEL;
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
        double[] rotate = HbmAnimations.getRelevantTransformation("ROTATE", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 3, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, -3, 6);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основное тело и рукоятку
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Grip", packedLight, packedOverlay);

        // Рендерим вращающиеся стволы с анимацией ROTATE
        poseStack.pushPose();
        if (rotate != null && rotate.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotate[2]));
        }
        getWeaponModel().renderPart(poseStack, builder, "Barrels", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим дымовые узлы как в оригинале
        double smokeScale = 0.5;
        poseStack.pushPose();
        poseStack.translate(-2, 1.25f, -3.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 0, 12);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        // Проверяем, это ли Lacunae версия
        if (stack.getItem() == ModGunItems.GUN_MINIGUN_LACUNAE.get()) {
            // Лазерная вспышка для Lacunae
            renderLaserFlash(poseStack, gun.lastShot[0], 50, 1.0, 0xff00ff);
            poseStack.translate(0, 0, -0.25f);
            renderLaserFlash(poseStack, gun.lastShot[0], 50, 0.5, 0xff0080);
        } else {
            // Обычная огневая вспышка для обычного минитена
            if (gun.shotRand != 0) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float)(gun.shotRand * 90)));
            }
            poseStack.scale(1.5f, 1.5f, 1.5f);
            renderMuzzleFlash(poseStack, gun.lastShot[0], 5.0f, partialTick);
        }
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        // Стартовые позиции (без прицеливания)
        double startX = -1.75F * offset;
        double startY = -1.75F * offset;
        double startZ = 3.5F * offset;

        // Целевые позиции (с прицеливанием)
        double aimX = 0;
        double aimY = -6.25 / 8D;
        double aimZ = 1;

        // Интерполяция между стартовыми и целевыми позициями
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        // Смещение для левой/правой руки
        float xOffset = -0.25F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.5f;
        }

        float yOffset = 0.35F;
        float scale = 0.375f;
        z = z - 0.2;
        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        double scale = 1.75D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(2, -1f, 8);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        // Реализация для GUI (инвентаря)
        double scale = 0.05D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(14f, 5f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        // Для стола модификаций
        double scale = -6.25D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга всех частей минитена
     * Используется во всех режимах кроме first-person
     */
    private void renderAllParts(PoseStack poseStack, VertexConsumer builder,
                                int packedLight, int packedOverlay) {
        List<String> parts = Arrays.asList("Gun", "Grip", "Barrels");

        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }
    }



    /**
     * Метод для расчета прогресса вспышки
     */
    private float getFlashProgress(long shotTime, int duration, float partialTick) {
        if (shotTime <= 0) return 0;

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - shotTime;

        if (elapsed > duration) return 0;

        // Квадратичное затухание для более плавного эффекта
        float progress = 1.0f - (float) elapsed / duration;
        return progress * progress; // Квадратичное затухание
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);

        poseStack.popPose();
    }
}