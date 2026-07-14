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

public class GunGreasegunRenderer extends BaseGunRenderer {

    private ResourceLocation GREASEGUN_TEX = null;
    private ResourceLocation GREASEGUN_CLEAN_TEX = null;
    private HFRWavefrontObject GREASEGUN_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        // Текстура будет выбрана в зависимости от модификации
        return null; // Переопределяем getWeaponTexture(ItemStack stack)
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (isRefurbished(stack)) {
            if (GREASEGUN_CLEAN_TEX == null) {
                GREASEGUN_CLEAN_TEX = HBMResourceManager.greasegun_clean_tex;
            }
            return GREASEGUN_CLEAN_TEX;
        } else {
            if (GREASEGUN_TEX == null) {
                GREASEGUN_TEX = HBMResourceManager.greasegun_tex;
            }
            return GREASEGUN_TEX;
        }
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (GREASEGUN_MODEL == null) {
            GREASEGUN_MODEL = HBMResourceManager.greasegun;
        }
        return GREASEGUN_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
        }
        return -0.5F;
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
        double[] stock = HbmAnimations.getRelevantTransformation("STOCK", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] flap = HbmAnimations.getRelevantTransformation("FLAP", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] handle = HbmAnimations.getRelevantTransformation("HANDLE", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] turn = HbmAnimations.getRelevantTransformation("TURN", 0);
        double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -3, -3);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 3, 3);
        }

        // Применяем анимацию LIFT
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, -3, -3);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 3, 3);
        }

        // Применяем поворот только если не прицеливаемся полностью
        float aimingProgress = getAimingProgress(partialTick);
        if (aimingProgress < 1F && turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) turn[2]));
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим приклад с анимацией STOCK
        poseStack.pushPose();
        if (stock != null && stock.length >= 3) {
            poseStack.translate(0, 0, (float) (-4 - stock[2]));
        }
        getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим магазин с анимацией MAG
        poseStack.pushPose();
        if (mag != null && mag.length >= 3) {
            poseStack.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);
        // Рендерим патрон только если он есть
        if (bullet != null && bullet.length >= 1 && bullet[0] != 1) {
            getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
        }
        poseStack.popPose();

        // Рендерим рукоятку с анимацией HANDLE
        poseStack.pushPose();
        if (handle != null && handle.length >= 1) {
            poseStack.translate(0, -1.4375f, -0.125f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) handle[0]));
            poseStack.translate(0, 1.4375f, 0.125f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Handle", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим крышку с анимацией FLAP
        poseStack.pushPose();
        if (flap != null && flap.length >= 3) {
            poseStack.translate(0, 0.53125f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) flap[2]));
            poseStack.translate(0, -0.5125f, 0);
        }
        getWeaponModel().renderPart(poseStack, builder, "Flap", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим дым как в оригинале
        double smokeScale = 0.25;

        // Первая группа дыма
        poseStack.pushPose();
        poseStack.translate(-0.25f, 0, 1.5f);
        if (turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) -turn[2]));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 1D, packedLight, packedOverlay);
        poseStack.popPose();

        // Вторая группа дыма
        poseStack.pushPose();
        poseStack.translate(0, 0, 8f);
        if (turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) -turn[2]));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 1D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 0, 8f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -1F * offset;
        double startZ = 1.75F * offset;

        double aimX = 0.06;
        double aimY = -2.625 / 8D + 0.025;
        double aimZ = 1.125;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.35F;
        float scale = 0.39f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        poseStack.translate(0, 1, 0); // Как в оригинале
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(8, 5, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -7.5D; // Как в оригинале
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 2, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    public boolean isRefurbished(ItemStack stack) {
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.GREASEGUN_CLEAN);
    }
}