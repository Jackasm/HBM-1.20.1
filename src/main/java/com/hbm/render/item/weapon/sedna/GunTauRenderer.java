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

public class GunTauRenderer extends BaseGunRenderer {

    private ResourceLocation TAU_TEX = null;
    private HFRWavefrontObject TAU_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (TAU_TEX == null) {
            TAU_TEX = HBMResourceManager.tau_tex;
        }
        return TAU_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (TAU_MODEL == null) {
            TAU_MODEL = HBMResourceManager.tau;
        }
        return TAU_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        // Применяем стандартное смещение
        float offset = 0.8F;

        // Получаем прогресс прицеливания
        float aimingProgress = getAimingProgress(partialTick);

        // Стартовые позиции (без прицеливания)
        double startX = -1.75F * offset;
        double startY = -1.75F * offset;
        double startZ = 3.5F * offset;

        // Целевые позиции (с прицеливанием) - можно настроить под Tau
        double aimX = -1.75F * offset;
        double aimY = -1.75F * offset;
        double aimZ = 3.5F * offset;

        // Интерполяция между стартовыми и целевыми позициями
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        // Смещение для левой/правой руки
        float xOffset = -1F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 3f;
        }
        z = z + 1;

        float yOffset = 0.7F;
        float scale = 0.75f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] rotate = HbmAnimations.getRelevantTransformation("ROTATE", 0);

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -1, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 1, 4);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            // Движение назад при отдаче
            poseStack.translate(0, 0, (float) recoil[2]);

            // Наклон при отдаче
            poseStack.translate(0, 0, -2);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (recoil[2] * 5)));
            poseStack.translate(0, 0, 2);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Body", packedLight, packedOverlay);

        // Рендерим ротор с анимацией вращения
        poseStack.pushPose();
        if (rotate != null && rotate.length >= 3) {
            poseStack.translate(0, -0.25f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotate[2]));
            poseStack.translate(0, 0.25f, 0);
        }
        getWeaponModel().renderPart(poseStack, builder, "Rotor", packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        double scale = 2.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0, 1, 2);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.12D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(6f, 2f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -2.5D; // -10D в оригинале (-10 * 0.25)
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
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
     * Метод для рендеринга стандартных частей Tau пушки
     */
    private void renderStandardParts(PoseStack poseStack, ItemStack stack,
                                     MultiBufferSource buffer, int packedLight,
                                     int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Основные части Tau
        List<String> mainParts = Arrays.asList(
                "Body",
                "Rotor"
        );

        for (String part : mainParts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        // Tau обычно не имеет прицела или имеет уникальный прицел
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * 0.66F);
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    // Проверка наличия уникальных модификаций для Tau
    public boolean hasSpecialMod(ItemStack stack, ResourceLocation modId) {
        return WeaponModManager.hasUpgrade(stack, 0, modId);
    }
}