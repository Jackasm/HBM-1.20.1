package com.hbm.render.item.weapon.sedna;

import com.hbm.items.ModGunItems;
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
import java.util.Objects;

public class GunHeavyRevolverRenderer extends BaseGunRenderer {

    private final String textureName;
    private ResourceLocation REVOLVER_TEX = null;
    private ResourceLocation SCOPE_TEX = null;
    private HFRWavefrontObject REVOLVER_MODEL = null;

    public GunHeavyRevolverRenderer(String textureName) {
        this.textureName = textureName;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (REVOLVER_TEX == null) {
            if (Objects.equals(textureName, "lilmac")) {
                REVOLVER_TEX = HBMResourceManager.lilmac_tex;
            } else if (Objects.equals(textureName, "protege")) {
                REVOLVER_TEX = HBMResourceManager.heavy_revolver_protege_tex;
            } else {
                REVOLVER_TEX = HBMResourceManager.heavy_revolver_tex;
            }
        }
        return REVOLVER_TEX;
    }

    protected ResourceLocation getScopeTexture() {
        if (SCOPE_TEX == null) {
            SCOPE_TEX = HBMResourceManager.lilmac_scope_tex;
        }
        return SCOPE_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (REVOLVER_MODEL == null) {
            REVOLVER_MODEL = HBMResourceManager.lilmac;
        }
        return REVOLVER_MODEL;
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
        boolean isScoped = isScoped(stack);
        return fov * (1 - aimingProgress * (isScoped ? 0.66F : 0.33F));
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        boolean isScoped = this.isScoped(stack);
        float aimingProgress = getAimingProgress(partialTick);

        // Если полностью прицелились с прицелом, не рендерим оружие
        if (isScoped && aimingProgress == 1.0f) {
            return;
        }

        GunItem gun = (GunItem) stack.getItem();

        // Получаем анимации
        double[] equipSpin = HbmAnimations.getRelevantTransformation("ROTATE", 0);
        double[] spin = HbmAnimations.getRelevantTransformation("SPIN", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] reloadLift = HbmAnimations.getRelevantTransformation("RELOAD_LIFT", 0);
        double[] reloadJolt = HbmAnimations.getRelevantTransformation("RELOAD_JOLT", 0);
        double[] reloadTilt = HbmAnimations.getRelevantTransformation("RELAOD_TILT", 0);
        double[] cylinderFlip = HbmAnimations.getRelevantTransformation("RELOAD_CYLINDER", 0);
        double[] reloadBullets = HbmAnimations.getRelevantTransformation("RELOAD_BULLETS", 0);
        double[] drum = HbmAnimations.getRelevantTransformation("DRUM", 0);
        double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER", 0);
        double[] reloadBulletsCon = HbmAnimations.getRelevantTransformation("RELOAD_BULLETS_CON", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Масштаб и вращение как в оригинале

        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        // Применяем анимацию SPIN
        if (spin != null && spin.length >= 1) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) spin[0]));
        }

        // Применяем анимацию EQUIP_SPIN
        if (equipSpin != null && equipSpin.length >= 1) {
            poseStack.translate(6, -3, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) equipSpin[0]));
            poseStack.translate(-6, 3, 0);
        }
        // Применяем анимацию RECOIL
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (recoil[2] * 10)));

        // Применяем анимации перезарядки
        if (reloadLift != null && reloadLift.length >= 1) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) reloadLift[0]));
        }
        if (reloadJolt != null && reloadJolt.length >= 1) {
            poseStack.translate((float) reloadJolt[0], 0, 0);
        }
        if (reloadTilt != null && reloadTilt.length >= 1) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) reloadTilt[0]));
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим барабан с анимациями
        poseStack.pushPose();

        // Применяем анимацию цилиндра
        if (cylinderFlip != null && cylinderFlip.length >= 1) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) cylinderFlip[0]));
        }

        getWeaponModel().renderPart(poseStack, builder, "Pivot", packedLight, packedOverlay);

        // Применяем вращение барабана
        poseStack.translate(0, 1.75f, 0);
        if (drum != null && drum.length >= 3) {
            poseStack.mulPose(Axis.XN.rotationDegrees((float) (drum[2] * 60)));
        }
        poseStack.translate(0, -1.75f, 0);

        getWeaponModel().renderPart(poseStack, builder, "Cylinder", packedLight, packedOverlay);

        // Применяем анимацию патронов
        if (reloadBullets != null && reloadBullets.length >= 3) {
            poseStack.translate((float) reloadBullets[0], (float) reloadBullets[1], (float) reloadBullets[2]);
        }

        // Рендерим патроны только если нужно
        if (reloadBulletsCon == null || reloadBulletsCon.length == 0 || reloadBulletsCon[0] != 1) {
            getWeaponModel().renderPart(poseStack, builder, "Bullets", packedLight, packedOverlay);
        }

        getWeaponModel().renderPart(poseStack, builder, "Casings", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим курок
        poseStack.pushPose();
        float hammerRotation = -30f;
        if (hammer != null && hammer.length >= 3) {
            hammerRotation += (float) (30 * hammer[2]);
        }
        poseStack.translate(4, 1.25f, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(hammerRotation));
        poseStack.translate(-4, -1.25f, 0);
        getWeaponModel().renderPart(poseStack, builder, "Hammer", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим прицел если есть
        if (isScoped) {
            VertexConsumer scopeBuilder = buffer.getBuffer(RenderType.entityCutout(getScopeTexture()));
            getWeaponModel().renderPart(poseStack, scopeBuilder, "Scope", packedLight, packedOverlay);
        }

        // Рендерим дымовые узлы
        poseStack.pushPose();
        poseStack.translate(-9, 2.5f, 0);
        poseStack.mulPose(Axis.ZN.rotationDegrees((float) (recoil[2] * 10)));
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5D, packedLight, packedOverlay);
        poseStack.popPose();


        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0.125f, 2.5f, 0);
        renderGapFlash(poseStack, gun.lastShot[0], partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        boolean isScoped = this.isScoped(stack);
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.0F * offset;
        double startY = -0.75F * offset;

        double aimX = 0.16;
        double aimY = isScoped ? (-4.75 / 8D) : (-3.5 / 8D);
        double aimZ = isScoped ? -0.25 : 0;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = (double) offset + (aimZ - (double) offset) * aimingProgress;
        z = z - 0.2;

        float xOffset = -0.1F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 0.7f;
        }
        float yOffset = 0.35F;
        float scale = 0.125f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 0.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 1, 3);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        boolean isScoped = this.isScoped(stack);

        if (isScoped) {
            double scale = 0.07D;
            poseStack.scale((float)scale, (float)scale, (float)scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.translate(10, 4f, 0);
        } else {
            double scale = 0.07D;
            poseStack.scale((float)scale, (float)scale, (float)scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.translate(10, 4f, 0);
        }
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -0.5f, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, buffer, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга стандартных частей револьвера
     */
    private void renderStandardParts(PoseStack poseStack, VertexConsumer builder,
                                     ItemStack stack, MultiBufferSource buffer,
                                     int packedLight, int packedOverlay) {
        // Поворот как в оригинале
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        List<String> parts = Arrays.asList(
                "Gun",
                "Cylinder",
                "Bullets",
                "Casings",
                "Pivot",
                "Hammer"
        );

        // Рендерим все стандартные части
        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }

        if (isScoped(stack)) {
            VertexConsumer scopeBuilder = buffer.getBuffer(RenderType.entityCutout(getScopeTexture()));
            getWeaponModel().renderPart(poseStack, scopeBuilder, "Scope", packedLight, packedOverlay);
        }
    }

    public boolean isScoped(ItemStack stack) {
        return stack.getItem() == ModGunItems.GUN_HEAVY_REVOLVER_LILMAC.get() ||
                WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SCOPE);
    }

}