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

public class GunSTG77Renderer extends BaseGunRenderer {

    private ResourceLocation STG77_TEX = null;
    private HFRWavefrontObject STG77_MODEL = null;

    public GunSTG77Renderer() {
        // Конструктор без параметров - одна текстура
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (STG77_TEX == null) {
            STG77_TEX = HBMResourceManager.stg77_tex;
        }
        return STG77_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (STG77_MODEL == null) {
            STG77_MODEL = HBMResourceManager.stg77;
        }
        return STG77_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 0.5F : -0.25F;
        }
        return -0.25F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * 0.66F);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        // Если полностью наведен - не рендерим оружие
        if(getAimingProgress(partialTick) >= 1.0F) {
            return;
        }

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] bolt = HbmAnimations.getRelevantTransformation("BOLT", 0);
        double[] handle = HbmAnimations.getRelevantTransformation("HANDLE", 0);
        double[] safety = HbmAnimations.getRelevantTransformation("SAFETY", 0);

        double[] inspectGun = HbmAnimations.getRelevantTransformation("INSPECT_GUN", 0);
        double[] inspectBarrel = HbmAnimations.getRelevantTransformation("INSPECT_BARREL", 0);
        double[] inspectMove = HbmAnimations.getRelevantTransformation("INSPECT_MOVE", 0);
        double[] inspectLever = HbmAnimations.getRelevantTransformation("INSPECT_LEVER", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -1, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 1, 4);
        }

        // Применяем анимацию LIFT
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, 0, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 0, 4);
        }

        // Применяем анимацию RECOIL (отдача)
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Основной рендеринг оружия
        poseStack.pushPose();

        // Применяем инспекционную анимацию
        if (inspectGun != null && inspectGun.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) inspectGun[2]));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) inspectGun[0]));
        }

        // Рендерим основную часть оружия
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим магазин
        poseStack.pushPose();
        HbmAnimations.applyRelevantTransformation(poseStack, "Magazine");
        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим рычаг
        poseStack.pushPose();
        if (inspectLever != null && inspectLever.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) inspectLever[2]));
        }
        HbmAnimations.applyRelevantTransformation(poseStack, "Lever");
        getWeaponModel().renderPart(poseStack, builder, "Lever", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим затворную группу
        poseStack.pushPose();
        if (bolt != null && bolt.length >= 3) {
            poseStack.translate(0, 0, (float) bolt[2]);
        }

        // Рендерим затвор
        poseStack.pushPose();
        HbmAnimations.applyRelevantTransformation(poseStack, "Breech");
        getWeaponModel().renderPart(poseStack, builder, "Breech", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим рукоятку взведения
        poseStack.translate(0.125f, 0, 0);
        if (handle != null && handle.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) handle[2]));
        }
        poseStack.translate(-0.125f, 0, 0);

        HbmAnimations.applyRelevantTransformation(poseStack, "Handle");
        getWeaponModel().renderPart(poseStack, builder, "Handle", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим предохранитель
        poseStack.pushPose();
        if (safety != null && safety.length >= 3) {
            poseStack.translate((float) safety[0], 0, 0);
        }
        HbmAnimations.applyRelevantTransformation(poseStack, "Safety");
        getWeaponModel().renderPart(poseStack, builder, "Safety", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose(); // Конец основной части

        // Рендерим ствол отдельно (для инспекции)
        poseStack.pushPose();

        // Инспекционная анимация ствола
        if (inspectMove != null && inspectMove.length >= 3) {
            poseStack.translate((float) inspectMove[0], (float) inspectMove[1], (float) inspectMove[2]);
        }

        if (inspectBarrel != null && inspectBarrel.length >= 3) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) inspectBarrel[0]));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) inspectBarrel[2]));
        }

        HbmAnimations.applyRelevantTransformation(poseStack, "Gun");
        HbmAnimations.applyRelevantTransformation(poseStack, "Barrel");
        getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим дымовые узлы
        poseStack.pushPose();
        poseStack.translate(0, 0, 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale(0.75f, 0.75f, 0.75f);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 0, 7.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale(0.25f, 0.25f, 0.25f);

        // Добавляем небольшую случайную ротацию для вспышки
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-5 + (float)(gun.shotRand * 10)));
        }

        renderGapFlash(poseStack, gun.lastShot[0], partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -1.0F * offset;
        double startZ = 2.5F * offset;

        double aimX = 0;
        double aimY = -5.75 / 8D;
        double aimZ = 2;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = -0.5f;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 2.0f;
        }

        float yOffset = 0f;
        float scale = 0.5f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 1, 1);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(9f, 4f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -7.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderSTG77Weapon(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        renderSTG77Weapon(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderSTG77Weapon(poseStack, buffer, packedLight, packedOverlay);
    }

    /**
     * Универсальный метод рендеринга STG77 для всех режимов кроме первого лица
     */
    private void renderSTG77Weapon(PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Рендерим все части
        List<String> parts = Arrays.asList(
                "Gun",
                "Barrel",
                "Lever",
                "Magazine",
                "Safety",
                "Handle",
                "Breech"
        );

        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }
    }

    /**
     * Получает базовый FOV для прицеливания
     */
    protected float getBaseFOV(ItemStack stack) {
        float aimingProgress = getAimingProgress(0);
        return 70F - aimingProgress * 65;
    }
}