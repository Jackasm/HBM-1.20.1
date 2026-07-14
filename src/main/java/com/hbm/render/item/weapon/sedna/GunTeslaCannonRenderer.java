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

import java.util.Objects;

public class GunTeslaCannonRenderer extends BaseGunRenderer {

    private ResourceLocation TESLA_CANNON_TEX = null;
    private HFRWavefrontObject TESLA_CANNON_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (TESLA_CANNON_TEX == null) {
            TESLA_CANNON_TEX = HBMResourceManager.tesla_cannon_tex;
        }
        return TESLA_CANNON_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (TESLA_CANNON_MODEL == null) {
            TESLA_CANNON_MODEL = HBMResourceManager.tesla_cannon;
        }
        return TESLA_CANNON_MODEL;
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
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float offset = 0.8F;

        // Стандартное преобразование для прицеливания
        float aimingProgress = getAimingProgress(partialTick);

        // Стартовые позиции (без прицеливания)
        double startX = -1.75F * offset;
        double startY = -0.5F * offset;
        double startZ = 1.75F * offset;

        // Целевые позиции (с прицеливанием)
        double aimX = -1.03F;
        double aimY = 0F;
        double aimZ = -0.5F * offset;

        // Интерполяция между стартовыми и целевыми позициями
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        z = z - 0.3;
        // Смещение для левой/правой руки
        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.5f;
        }

        float yOffset = 0.4F;
        float scale = 0.75f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] cycle = HbmAnimations.getRelevantTransformation("CYCLE", 0);
        double[] count = HbmAnimations.getRelevantTransformation("COUNT", 0);
        double[] yomi = HbmAnimations.getRelevantTransformation("YOMI", 0);
        double[] squeeze = HbmAnimations.getRelevantTransformation("SQUEEZE", 0);

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -2, -2);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 2, 2);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (recoil[2] * 2)));
        }

        // Рендерим основные части
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Extension", packedLight, packedOverlay);

        // Рендерим шестерню с анимацией CYCLE
        double cogAngle = 0;
        if (cycle != null && cycle.length >= 3) {
            cogAngle = cycle[2];
        }

        poseStack.pushPose();
        poseStack.translate(0, -1.625, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) cogAngle));
        poseStack.translate(0, 1.625, 0);
        getWeaponModel().renderPart(poseStack, builder, "Cog", packedLight, packedOverlay);
        poseStack.popPose();


        // Рендерим конденсаторы
        poseStack.pushPose();

// Применяем анимацию cogAngle
        poseStack.translate(0, -1.625, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) cogAngle));
        poseStack.translate(0, 1.625, 0);

        int amount = 8; // Значение по умолчанию
        if (count != null && count.length >= 1) {
            int configAmount = Objects.requireNonNull(gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack))
                    .getAmount(stack, Objects.requireNonNull(Minecraft.getInstance().player).getInventory());
            amount = Math.max((int) count[0], configAmount);
        }
        amount = Math.min(amount, 8);

// Первые 4 конденсатора по кругу - зеркально
        for (int i = 0; i < Math.min(amount, 4); i++) {
            // Левый конденсатор
            poseStack.pushPose();
            float leftAngle = -22.5f * (i + 1); // Отрицательный угол для левой стороны
            poseStack.translate(0, -1.625, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(leftAngle));
            poseStack.translate(0, 1.625, 0);
            getWeaponModel().renderPart(poseStack, builder, "Capacitor", packedLight, packedOverlay);
            poseStack.popPose();

            // Правый конденсатор (зеркальный)
            poseStack.pushPose();
            float rightAngle = 22.5f * (i + 1); // Положительный угол для правой стороны
            poseStack.translate(0, -1.625, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(rightAngle));
            poseStack.translate(0, 1.625, 0);
            getWeaponModel().renderPart(poseStack, builder, "Capacitor", packedLight, packedOverlay);
            poseStack.popPose();
        }

// Остальные конденсаторы линейно - зеркально
        int remainingPairs = (amount - 4) / 2;
        for (int i = 0; i < remainingPairs; i++) {
            // Левый конденсатор (отрицательный X)
            poseStack.pushPose();
            float leftOffset = -(i + 1) * 0.5f;
            poseStack.translate(leftOffset, 0, 0);
            getWeaponModel().renderPart(poseStack, builder, "Capacitor", packedLight, packedOverlay);
            poseStack.popPose();

        }

// Если остался один непарный конденсатор (по центру)
        if ((amount - 4) % 2 == 1) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 0); // По центру
            getWeaponModel().renderPart(poseStack, builder, "Capacitor", packedLight, packedOverlay);
            poseStack.popPose();
        }

        poseStack.popPose();

        // Рендерим Yomi (чучело)
        if (yomi != null && yomi.length >= 3 && squeeze != null && squeeze.length >= 3) {
            poseStack.pushPose();
            poseStack.translate((float) yomi[0], (float) yomi[1], (float) yomi[2]);
            poseStack.mulPose(Axis.YP.rotationDegrees(135));
            poseStack.scale((float) squeeze[0], (float) squeeze[1], (float) squeeze[2]);

            VertexConsumer plushieBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.yomi_tex));
            HFRWavefrontObject plushie = HBMResourceManager.yomi;
            plushie.renderAll(poseStack, plushieBuilder, packedLight, packedOverlay);

            poseStack.popPose();
        }
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        // Масштаб и смещение как в оригинале
        double scale = 2.75D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0, 1.5, 0);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        // Реализация для GUI (инвентаря)
        double scale = 0.07D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(10, 4, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        // Для стола модификаций
        double scale = -8.75D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 0.5, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        // Для GUI рендерим все части статично
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Рендерим основные части
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Extension", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Cog", packedLight, packedOverlay);

        // Рендерим конденсаторы (статические, 10 штук как в оригинале)
        poseStack.pushPose();
        for (int i = 0; i < 10; i++) {
            getWeaponModel().renderPart(poseStack, builder, "Capacitor", packedLight, packedOverlay);

            if (i < 4) {
                poseStack.translate(0, -1.625, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-22.5f));
                poseStack.translate(0, 1.625, 0);
            } else {
                poseStack.translate(0.5f, 0, 0);
            }
        }
        poseStack.popPose();
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
        // Для третьего лица рендерим все статично
        renderGUIWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Включаем освещение как в оригинале
        // Note: В современных версиях освещение обычно включено по умолчанию

        // Рендерим все части как в оригинале
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Extension", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Cog", packedLight, packedOverlay);

        poseStack.pushPose();
        for (int i = 0; i < 10; i++) {
            getWeaponModel().renderPart(poseStack, builder, "Capacitor", packedLight, packedOverlay);

            if (i < 4) {
                poseStack.translate(0, -1.625, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-22.5f));
                poseStack.translate(0, 1.625, 0);
            } else {
                poseStack.translate(0.5f, 0, 0);
            }
        }
        poseStack.popPose();

        poseStack.popPose();
    }
}