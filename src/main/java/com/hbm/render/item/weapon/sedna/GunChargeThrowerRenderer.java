package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.items.weapon.sedna.mags.MagazineFullReload;
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

import java.util.Objects;

public class GunChargeThrowerRenderer extends BaseGunRenderer {

    private ResourceLocation CHARGE_THROWER_TEX = null;
    private HFRWavefrontObject CHARGE_THROWER_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (CHARGE_THROWER_TEX == null) {
            CHARGE_THROWER_TEX = HBMResourceManager.charge_thrower_tex;
        }
        return CHARGE_THROWER_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (CHARGE_THROWER_MODEL == null) {
            CHARGE_THROWER_MODEL = HBMResourceManager.charge_thrower;
        }
        return CHARGE_THROWER_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 0F : -0.5F;
        }
        return 2.75F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * (isScoped(stack) ? 0.66F : 0.33F));
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        boolean usingScope = isScoped(stack) && GunItem.aimingProgress == 1 && GunItem.prevAimingProgress == 1;
        MagazineFullReload mag = (MagazineFullReload) gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
        boolean reloading = HbmAnimations.getRelevantAnim(0) != null &&
                Objects.requireNonNull(HbmAnimations.getRelevantAnim(0)).animation.getBus("AMMO") != null;

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] raise = HbmAnimations.getRelevantTransformation("RAISE", 0);
        double[] ammo = HbmAnimations.getRelevantTransformation("AMMO", 0);
        double[] twist = HbmAnimations.getRelevantTransformation("TWIST", 0);
        double[] turn = HbmAnimations.getRelevantTransformation("TURN", 0);
        double[] roll = HbmAnimations.getRelevantTransformation("ROLL", 0);

        // Масштабирование для прицела
        if (usingScope) {
            double scale = 3.5D;
            poseStack.scale((float) scale, (float) scale, (float) scale);
            poseStack.translate(-0.5, -1.5, -4);
        } else {
            double scale = 0.5D;
            poseStack.scale((float) scale, (float) scale, (float) scale);
        }

        // Анимация EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 0, -7);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 0, 7);
        }

        // Анимация RAISE
        if (raise != null && raise.length >= 1) {
            poseStack.translate(0, -7, 4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) raise[0]));
            poseStack.translate(0, 7, -4);
        }

        // Анимация RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(recoil[0], recoil[1], recoil[2]);
        }

        // Анимация TURN
        if (turn != null && turn.length >= 2) {
            poseStack.translate(0, 0, -2);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) turn[1]));
            poseStack.translate(0, 0, 2);
        }

        // Анимация ROLL
        if (roll != null && roll.length >= 3) {
            poseStack.translate(0, -1, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) roll[2]));
            poseStack.translate(0, 1, 0);
        }

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Рендерим основное оружие
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим прицел если есть и не в режиме прицеливания
        if (isScoped(stack) && !usingScope) {
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
        }

        // Рендерим снаряд если есть
        if (Objects.requireNonNull(mag).getAmount(stack, null) > 0 || reloading) {
            poseStack.pushPose();

            if (ammo != null && ammo.length >= 3) {
                poseStack.translate(ammo[0], ammo[1], ammo[2]);
            }
            if (twist != null && twist.length >= 3) {
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) twist[2]));
            }

            Object ammoType = mag.getType(stack, null);
            if (ammoType == BulletConfigRegistry.ct_hook) {
                VertexConsumer hookBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.charge_thrower_hook_tex));
                getWeaponModel().renderPart(poseStack, hookBuilder, "Hook", packedLight, packedOverlay);
            }
            if (ammoType == BulletConfigRegistry.ct_mortar) {
                VertexConsumer mortarBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.charge_thrower_mortar_tex));
                getWeaponModel().renderPart(poseStack, mortarBuilder, "Mortar", packedLight, packedOverlay);
            }
            if (ammoType == BulletConfigRegistry.ct_mortar_charge) {
                VertexConsumer mortarBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.charge_thrower_mortar_tex));
                getWeaponModel().renderPart(poseStack, mortarBuilder, "Mortar", packedLight, packedOverlay);
                getWeaponModel().renderPart(poseStack, builder, "Oomph", packedLight, packedOverlay);
            }

            poseStack.popPose();
        }
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;
        float zoom = 0.5F;

        if (isScoped(stack)) {
            double startX = -1.5F * offset;
            double startY = -1.25F * offset;
            double startZ = 3.5F * offset;
            double aimX = -0.15625;
            double aimY = -6.5 / 8D;
            double aimZ = 1.6875;

            double x = startX + (aimX - startX) * aimingProgress;
            double y = startY + (aimY - startY) * aimingProgress;
            double z = startZ + (aimZ - startZ) * aimingProgress;

            float xOffset = 0.0F;
            if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
            float yOffset = 0.875f;
            float scale = 0.875f;

            applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
        } else {
            double startX = -1.5F * offset;
            double startY = -1.25F * offset;
            double startZ = 3.5F * offset;
            double aimX = -1.5F * zoom;
            double aimY = -1.25F * zoom;
            double aimZ = 3.5F * zoom;

            double x = startX + (aimX - startX) * aimingProgress;
            double y = startY + (aimY - startY) * aimingProgress;
            double z = startZ + (aimZ - startZ) * aimingProgress;

            float xOffset = 0.0F;
            if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
            float yOffset = 0.875f;
            float scale = 0.875f;

            applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
        }
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0.75, 1, 4);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.translate(0.5, 0.5, -0.625);
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));

    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -8.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 0, -1);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        renderWeaponWithAmmo(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderWeaponWithAmmo(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderWeaponWithAmmo(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    private void renderWeaponWithAmmo(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        GunItem gun = (GunItem) stack.getItem();
        MagazineFullReload mag = (MagazineFullReload) gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        if (isScoped(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
        }

        if (Objects.requireNonNull(mag).getAmount(stack, null) > 0) {
            Object ammoType = mag.getType(stack, null);
            if (ammoType == BulletConfigRegistry.ct_hook) {
                VertexConsumer hookBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.charge_thrower_hook_tex));
                getWeaponModel().renderPart(poseStack, hookBuilder, "Hook", packedLight, packedOverlay);
            }
            if (ammoType == BulletConfigRegistry.ct_mortar) {
                VertexConsumer mortarBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.charge_thrower_mortar_tex));
                getWeaponModel().renderPart(poseStack, mortarBuilder, "Mortar", packedLight, packedOverlay);
            }
            if (ammoType == BulletConfigRegistry.ct_mortar_charge) {
                VertexConsumer mortarBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.charge_thrower_mortar_tex));
                getWeaponModel().renderPart(poseStack, mortarBuilder, "Mortar", packedLight, packedOverlay);
                getWeaponModel().renderPart(poseStack, builder, "Oomph", packedLight, packedOverlay);
            }
        }
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);
        renderWeaponWithAmmo(stack, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private boolean isScoped(ItemStack stack) {
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SCOPE);
    }
}