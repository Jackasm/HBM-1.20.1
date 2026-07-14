package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunConfig;
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

public class GunLiberatorRenderer extends BaseGunRenderer {

    private ResourceLocation LIBERATOR_TEX = null;
    private HFRWavefrontObject LIBERATOR_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (LIBERATOR_TEX == null) {
            LIBERATOR_TEX = HBMResourceManager.liberator_tex;
        }
        return LIBERATOR_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (LIBERATOR_MODEL == null) {
            LIBERATOR_MODEL = HBMResourceManager.liberator;
        }
        return LIBERATOR_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
        }
        return 2.75F;
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
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] latch = HbmAnimations.getRelevantTransformation("LATCH", 0);
        double[] brk = HbmAnimations.getRelevantTransformation("BREAK", 0);
        double[] shell1 = HbmAnimations.getRelevantTransformation("SHELL1", 0);
        double[] shell2 = HbmAnimations.getRelevantTransformation("SHELL2", 0);
        double[] shell3 = HbmAnimations.getRelevantTransformation("SHELL3", 0);
        double[] shell4 = HbmAnimations.getRelevantTransformation("SHELL4", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -1, -3);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 1, 3);
        }

        // Применяем анимацию LIFT
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, -3, -3);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 3, 3);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(recoil[0] * 2, recoil[1], recoil[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (recoil[2] * 10)));
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим ствол с анимацией BREAK
        poseStack.pushPose();
        if (brk != null && brk.length >= 1) {
            poseStack.translate(0, -0.5f, 0.75f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) brk[0]));
            poseStack.translate(0, 0.5f, -0.75f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);

        // Рендерим гильзы с анимациями
        if (shell1 != null && shell1.length >= 3) {
            poseStack.pushPose();
            poseStack.translate(shell1[0], shell1[1], shell1[2]);
            getWeaponModel().renderPart(poseStack, builder, "Shell1", packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (shell2 != null && shell2.length >= 3) {
            poseStack.pushPose();
            poseStack.translate(shell2[0], shell2[1], shell2[2]);
            getWeaponModel().renderPart(poseStack, builder, "Shell2", packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (shell3 != null && shell3.length >= 3) {
            poseStack.pushPose();
            poseStack.translate(shell3[0], shell3[1], shell3[2]);
            getWeaponModel().renderPart(poseStack, builder, "Shell3", packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (shell4 != null && shell4.length >= 3) {
            poseStack.pushPose();
            poseStack.translate(shell4[0], shell4[1], shell4[2]);
            getWeaponModel().renderPart(poseStack, builder, "Shell4", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Рендерим защелку с анимацией LATCH
        poseStack.pushPose();
        if (latch != null && latch.length >= 1) {
            poseStack.translate(0, 1.15625f, 0.75f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) latch[0]));
            poseStack.translate(0, -1.15625f, -0.75f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Latch", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose(); // Закрываем push для Barrel

        // Рендерим дым как в оригинале
        GunConfig cfg = gun.getConfig(stack, 0);
        double smokeScale = 0.375;

        // Первая группа дыма
        poseStack.pushPose();
        poseStack.translate(0, 0.25f, 7.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        poseStack.translate(0, 0, 0.25f / smokeScale);
        renderSmokeNodes(poseStack, buffer, cfg.smokeNodes, 1D, packedLight, packedOverlay);
        poseStack.popPose();

        // Вторая группа дыма
        poseStack.pushPose();
        poseStack.translate(0, 0.25f, 7.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        poseStack.translate(0, 0, -0.5f / smokeScale);
        renderSmokeNodes(poseStack, buffer, cfg.smokeNodes, 1D, packedLight, packedOverlay);
        poseStack.popPose();

        // Третья группа дыма
        poseStack.pushPose();
        poseStack.translate(0, 0.25f, 7.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        poseStack.translate(0, 0.5f / smokeScale, 0);
        renderSmokeNodes(poseStack, buffer, cfg.smokeNodes, 1D, packedLight, packedOverlay);
        poseStack.popPose();

        // Четвертая группа дыма
        poseStack.pushPose();
        poseStack.translate(0, 0.25f, 7.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        poseStack.translate(0, 0.5f / smokeScale, 0.5f / smokeScale);
        renderSmokeNodes(poseStack, buffer, cfg.smokeNodes, 1D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 0.5f, 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        poseStack.scale(1.5f, 1.5f, 1.5f);
        renderMuzzleFlash(poseStack, gun.lastShot[0], 5, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -1.25F * offset;
        double startZ = 1.25F * offset;

        double aimX = 0.055;
        double aimY = -4.625 / 8D;
        double aimZ = 0.25;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.25F;
        float scale = 0.49f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.25D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 1, -1.5);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(9, 4, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        // Как в оригинале: scale -8.75, rotate 90°Y
        double scale = -8.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        // Для GUI рендерим без гильз
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell1", "Shell2", "Shell3", "Shell4");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        // Для инвентаря рендерим без гильз
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell1", "Shell2", "Shell3", "Shell4");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        // Для третьего лица рендерим без гильз
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell1", "Shell2", "Shell3", "Shell4");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    // Метод для рендеринга на столе модификаций
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