package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.GunConfig;
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

public class GunFlaregunRenderer extends BaseGunRenderer {

    private ResourceLocation FLAREGUN_TEX = null;
    private HFRWavefrontObject FLAREGUN_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (FLAREGUN_TEX == null) {
            FLAREGUN_TEX = HBMResourceManager.flaregun_tex;
        }
        return FLAREGUN_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (FLAREGUN_MODEL == null) {
            FLAREGUN_MODEL = HBMResourceManager.flaregun;
        }
        return FLAREGUN_MODEL;
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
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER", 0);
        double[] open = HbmAnimations.getRelevantTransformation("OPEN", 0);
        double[] shell = HbmAnimations.getRelevantTransformation("SHELL", 0);
        double[] flip = HbmAnimations.getRelevantTransformation("FLIP", 0);

        // Применяем анимацию отдачи
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(recoil[0], recoil[1], recoil[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (recoil[2] * 10)));
        }

        // Применяем анимацию FLIP
        if (flip != null && flip.length >= 1) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) flip[0]));
        }

        // Применяем анимацию экипировки
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 0, -8);
            poseStack.mulPose(Axis.XN.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 0, 8);
        }

        // Рендерим основную часть
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим курок с анимацией
        poseStack.pushPose();
        if (hammer != null && hammer.length >= 1) {
            poseStack.translate(0, 1.8125f, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (hammer[0] - 15)));
            poseStack.translate(0, -1.8125f, 4);
        }
        getWeaponModel().renderPart(poseStack, builder, "Hammer", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим ствол и сигнальную ракету
        poseStack.pushPose();
        if (open != null && open.length >= 1) {
            poseStack.translate(0, 2.156f, 1.78f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) open[0]));
            poseStack.translate(0, -2.156f, -1.78f);
        }

        getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);

        // Рендерим сигнальную ракету
        if (shell != null && shell.length >= 3) {
            poseStack.pushPose();
            poseStack.translate((float) shell[0], (float) shell[1], (float) shell[2]);
            getWeaponModel().renderPart(poseStack, builder, "Flare", packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            getWeaponModel().renderPart(poseStack, builder, "Flare", packedLight, packedOverlay);
        }

        poseStack.popPose();

        // Рендерим дым
        GunConfig cfg = gun.getConfig(stack, 0);
        double smokeScale = 0.5;

        // Первая группа дыма
        poseStack.pushPose();
        poseStack.translate(0, 4f, 9f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, cfg.smokeNodes, 2.5D, packedLight, packedOverlay);
        poseStack.popPose();

        // Вторая группа дыма (смещена)
        poseStack.pushPose();
        poseStack.translate(0, 4f, 9f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        poseStack.translate(0, 0, 0.1f / (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, cfg.smokeNodes, 2D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 4f, 9f);
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

        double startX = -1.25F * offset;
        double startY = -1.5F * offset;
        double startZ = 2F * offset;

        double aimX = 0;
        double aimY = -5.5 / 8D;
        double aimZ = 0.5;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = -0.5F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.3f;
        float scale = 0.125f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 0.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 0.25f, 3);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.06D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(12f, 4, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        // В GUI рендерим все части, включая сигнальную ракету
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        // В инвентаре рендерим все части
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        // В третьем лице рендерим все части
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

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