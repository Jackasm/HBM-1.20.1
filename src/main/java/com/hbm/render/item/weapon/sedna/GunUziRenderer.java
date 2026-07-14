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

public class GunUziRenderer extends BaseGunRenderer {

    private ResourceLocation UZI_TEX = null;
    private ResourceLocation UZI_SATURNITE_TEX = null;
    private HFRWavefrontObject UZI_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        // Переопределяем в getWeaponTexture(ItemStack stack)
        return null;
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (isSaturnite(stack)) {
            if (UZI_SATURNITE_TEX == null) {
                UZI_SATURNITE_TEX = HBMResourceManager.uzi_saturnite_tex;
            }
            return UZI_SATURNITE_TEX;
        } else {
            if (UZI_TEX == null) {
                UZI_TEX = HBMResourceManager.uzi_tex;
            }
            return UZI_TEX;
        }
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (UZI_MODEL == null) {
            UZI_MODEL = HBMResourceManager.uzi;
        }
        return UZI_MODEL;
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

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        // Получаем анимации как в оригинале
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] stockFront = HbmAnimations.getRelevantTransformation("STOCKFRONT", 0);
        double[] stockBack = HbmAnimations.getRelevantTransformation("STOCKBACK", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", 0);
        double[] slide = HbmAnimations.getRelevantTransformation("SLIDE", 0);
        double[] yeet = HbmAnimations.getRelevantTransformation("YEET", 0);
        double[] speen = HbmAnimations.getRelevantTransformation("SPEEN", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Применяем анимации YEET и SPEEN
        if (yeet != null && yeet.length >= 3) {
            poseStack.translate((float) yeet[0], (float) yeet[1], (float) yeet[2]);
        }
        if (speen != null && speen.length >= 1) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) speen[0]));
        }

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -2, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 2, 4);
        }

        // Применяем анимацию LIFT
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, 0, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 0, 6);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим глушитель если есть
        boolean silenced = hasSilencer(stack, 0);
        if (silenced) {
            getWeaponModel().renderPart(poseStack, builder, "Silencer", packedLight, packedOverlay);
        }

        // Рендерим переднюю часть приклада с анимацией STOCKFRONT
        poseStack.pushPose();
        if (stockFront != null && stockFront.length >= 1) {
            poseStack.translate(0, 0.3125f, -5.75f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (180 - stockFront[0])));
            poseStack.translate(0, -0.3125f, 5.75f);
        }
        getWeaponModel().renderPart(poseStack, builder, "StockFront", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим заднюю часть приклада с анимацией STOCKBACK
        poseStack.pushPose();

        poseStack.translate(0, -0.3125f, -3f);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) -(-200 - stockBack[0])));
        poseStack.translate(0, 0.3125f, 3f);

        getWeaponModel().renderPart(poseStack, builder, "StockBack", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим затвор с анимацией SLIDE
        poseStack.pushPose();
        if (slide != null && slide.length >= 3) {
            poseStack.translate(0, 0, (float) slide[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Slide", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим магазин с анимацией MAG
        poseStack.pushPose();
        if (mag != null && mag.length >= 3) {
            poseStack.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);
        // Рендерим патрон только если нужно
        if (bullet != null && bullet.length >= 1 && bullet[0] == 1) {
            getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
        }
        poseStack.popPose();

        // Рендерим дым только если нет глушителя
        if (!silenced) {
            double smokeScale = 0.5;

            // Группа дыма
            poseStack.pushPose();
            poseStack.translate(0, 0.75f, 8.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
            renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.75D, packedLight, packedOverlay);
            poseStack.popPose();

            // Рендерим вспышку выстрела
            poseStack.pushPose();
            poseStack.translate(0, 0.75f, 8.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            if (gun.shotRand != 0) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
            }
            renderMuzzleFlash(poseStack, gun.lastShot[0],7.5f, partialTick);
            poseStack.popPose();
        }
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.75F * offset;
        double startY = -1.5F * offset;
        double startZ = 2.5F * offset;

        double aimX = 0.06;
        double aimY = -4.375 / 8D - 0.04;
        double aimZ = 1;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.35F;
        float scale = 0.29f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        poseStack.translate(0, 1, -3); // Как в оригинале
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(8, 4, 0);

        // Специальная трансформация для инвентаря с глушителем
        if (hasSilencer(stack, 0)) {
            double silencerScale = 0.625D;
            poseStack.scale((float)silencerScale, (float)silencerScale, (float)silencerScale);
            poseStack.translate(0, 0, -4);
        }
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -6.25D; // Как в оригинале
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 1, -4); // Как в оригинале
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга стандартных частей Uzi
     * Используется во всех режимах кроме first-person
     */
    private void renderStandardParts(PoseStack poseStack, VertexConsumer builder,
                                     ItemStack stack, int packedLight, int packedOverlay) {
        List<String> parts = Arrays.asList(
                "Gun",
                "StockBack",
                "StockFront",
                "Slide",
                "Magazine"
        );

        // Рендерим все стандартные части
        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }

        // Рендерим глушитель если есть
        if (hasSilencer(stack, 0)) {
            getWeaponModel().renderPart(poseStack, builder, "Silencer", packedLight, packedOverlay);
        }
    }

    public boolean hasSilencer(ItemStack stack, int cfg) {
        return WeaponModManager.hasUpgrade(stack, cfg, WeaponModManager.SILENCER);
    }

    public boolean isSaturnite(ItemStack stack) {
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.UZI_SATURN);
    }
}