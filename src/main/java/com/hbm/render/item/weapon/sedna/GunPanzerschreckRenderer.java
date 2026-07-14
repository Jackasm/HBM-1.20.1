package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
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

public class GunPanzerschreckRenderer extends BaseGunRenderer {

    private ResourceLocation PANZERSCHRECK_TEX = null;
    private HFRWavefrontObject PANZERSCHRECK_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (PANZERSCHRECK_TEX == null) {
            PANZERSCHRECK_TEX = HBMResourceManager.panzerschreck_tex;
        }
        return PANZERSCHRECK_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (PANZERSCHRECK_MODEL == null) {
            PANZERSCHRECK_MODEL = HBMResourceManager.panzerschreck;
        }
        return PANZERSCHRECK_MODEL;
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
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        poseStack.translate(0, 0, 0.875);

        float offset = 0.8F;
        float aimingProgress = getAimingProgress(partialTick);

        double startX = -2.75F * offset;
        double startY = -2F * offset;
        double startZ = 2.5F * offset;

        double aimX = -0.9375;
        double aimY = -9.25 / 8D;
        double aimZ = 0.25;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? 1.5f : 0.0f;
        float yOffset = 0.4F;
        float scale = 1.25f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] reload = HbmAnimations.getRelevantTransformation("RELOAD", 0);
        double[] rocket = HbmAnimations.getRelevantTransformation("ROCKET", 0);

        // Базовая анимация EQUIP
        poseStack.translate(0, -1, -1);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) (equip != null ? equip[0] : 0)));
        poseStack.translate(0, 1, 1);

        // Анимация перезарядки
        poseStack.translate(0, -4, -3);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) (reload != null ? reload[0] : 0)));
        poseStack.translate(0, 4, 3);

        poseStack.pushPose();
        {
            // Труба
            getWeaponModel().renderPart(poseStack, builder, "Tube", packedLight, packedOverlay);

            // Щит (если есть)
            if (hasShield(stack)) {
                getWeaponModel().renderPart(poseStack, builder, "Shield", packedLight, packedOverlay);
            }

            // Ракета с анимацией
            poseStack.pushPose();
            {
                if (rocket != null && rocket.length >= 3) {
                    poseStack.translate(rocket[0], rocket[1], rocket[2]);
                }
                getWeaponModel().renderPart(poseStack, builder, "Rocket", packedLight, packedOverlay);
            }
            poseStack.popPose();
        }
        poseStack.popPose();

        // Вспышка выстрела
        poseStack.pushPose();
        {
            poseStack.translate(0, 0, 6.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (90 * gun.shotRand)));
            poseStack.scale(0.75f, 0.75f, 0.75f);
            renderMuzzleFlash(poseStack, gun.lastShot[0], 15, partialTick);
        }
        poseStack.popPose();
    }

    private boolean hasShield(ItemStack stack) {
        return !WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.NO_SHIELD);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 3D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 0.5, 1);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.075D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(8, 4, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -10D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Tube", packedLight, packedOverlay);
        if (hasShield(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Shield", packedLight, packedOverlay);
        }
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderGUIWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderGUIWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderPart(poseStack, builder, "Tube", packedLight, packedOverlay);
        if (hasShield(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Shield", packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}