package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.Objects;


public class GunBolterRenderer extends BaseGunRenderer {

    private ResourceLocation BOLTER_TEX = null;
    private HFRWavefrontObject BOLTER_MODEL = null;

    public GunBolterRenderer() {
        // Конструктор без параметров
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (BOLTER_TEX == null) {
            BOLTER_TEX = HBMResourceManager.bolter_tex;
        }
        return BOLTER_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (BOLTER_MODEL == null) {
            BOLTER_MODEL = HBMResourceManager.bolter;
        }
        return BOLTER_MODEL;
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        // Получаем анимации
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] tilt = HbmAnimations.getRelevantTransformation("TILT", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);

        // Применяем анимацию отдачи
        if (recoil != null && recoil.length >= 1) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (recoil[0] * 5)));
            poseStack.translate(0, 0, (float) recoil[0]);
        }

        // Применяем анимацию наклона
        if (tilt != null && tilt.length >= 1) {
            poseStack.translate(0, (float) tilt[0], 3);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (tilt[0] * 35)));
            poseStack.translate(0, 0, -3);
        }

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Рендерим основную часть
        getWeaponModel().renderPart(poseStack, builder, "Body", packedLight, packedOverlay);

        // Рендерим магазин с анимацией
        if (mag != null && mag.length >= 3) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 5);

            // Вычисляем угол вращения магазина
            float magRotation = (float) (mag[0] * 60 * (mag[2] == 1 ? 2.5 : 1));
            poseStack.mulPose(Axis.XN.rotationDegrees(magRotation));

            poseStack.translate(0, 0, -5);

            getWeaponModel().renderPart(poseStack, builder, "Mag", packedLight, packedOverlay);

            // Рендерим патрон, если магазин не в состоянии "1"
            if (mag[2] != 1) {
                getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
            }

            poseStack.popPose();
        }

        // Рендерим счетчик патронов
        renderAmmoCounter(poseStack, buffer, stack, gun);
    }

    /**
     * Рендерит счетчик патронов (цифровой дисплей)
     */
    private void renderAmmoCounter(PoseStack poseStack, MultiBufferSource buffer,
                                   ItemStack stack, GunItem gun) {
        poseStack.pushPose();

        // Получаем количество патронов
        String ammoCount;
        try {
            ammoCount = String.valueOf(
                    Objects.requireNonNull(gun.getConfig(stack, 0).getReceivers(stack)[0]
                            .getMagazine(stack)).getAmount(stack, null)
            );
        } catch (Exception e) {
            ammoCount = "0";
        }

        Font font = Minecraft.getInstance().font;
        float scale = 0.04F;

        // Позиционирование счетчика как в оригинале
        poseStack.translate(-0.25F, 2.11F, 2.9F);
        poseStack.scale(scale, -scale, scale);

        // Центрирование текста
        float textWidth = font.width(ammoCount) * scale;
        poseStack.translate(-textWidth / 2, 0, 0);

        poseStack.mulPose(Axis.XP.rotationDegrees(45));

        // Рендерим текст
        Matrix4f matrix = poseStack.last().pose();
        font.drawInBatch(
                ammoCount,
                0, 0,
                0xFF0000, // Красный цвет
                false,
                matrix,
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                15728880,
                false
        );

        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -2.0F * offset;
        double startZ = 2.5F * offset;

        double aimX = 1.065;
        double aimY = -5.8 / 8D;
        double aimZ = 1.25;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = -1F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 2.5f;
        float yOffset = 0.35F;
        float scale = 0.29f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 2.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, -0.75f, 0.75f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.15D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(5f, 1f, 0);
    }

    @Override
    protected void setupGroundTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupGroundTransforms(poseStack, stack);
        // Болтер обычно не рендерится на земле отдельно, используем базовый
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -12.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -0.5f, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        // Рендерим всю модель в GUI
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
        }
        return -0.25F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * 0.33F);
    }

    // Дополнительный метод для рендера на столе модификаций
    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);

        poseStack.popPose();
    }
}