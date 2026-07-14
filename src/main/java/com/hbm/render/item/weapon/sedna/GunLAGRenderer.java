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

import java.util.Objects;

public class GunLAGRenderer extends BaseGunRenderer {

    private ResourceLocation LAG_TEX = null;
    private HFRWavefrontObject LAG_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (LAG_TEX == null) {
            LAG_TEX = HBMResourceManager.lag_tex;
        }
        return LAG_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (LAG_MODEL == null) {
            LAG_MODEL = HBMResourceManager.lag;
        }
        return LAG_MODEL;
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
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float offset = 0.8F;
        double startX = -1.5F * offset;
        double startY = -1F * offset;
        double startZ = 1.5F * offset;

        double aimX = 0.21;
        double aimY = -3.375f / 8f + 0.03;
        double aimZ = 0.5f;

        float aimingProgress = getAimingProgress(partialTick);
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = -0.15F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.27F;
        float scale = 1.2f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        double scale = 0.25D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
        double[] addTrans = HbmAnimations.getRelevantTransformation("ADD_TRANS");
        double[] addRot = HbmAnimations.getRelevantTransformation("ADD_ROT");

        // Применяем анимацию EQUIP
        poseStack.translate(4, -4, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) -equip[0]));
        poseStack.translate(-4, 4, 0);

        // Применяем дополнительные трансформации
        if (addTrans != null && addTrans.length >= 3) {
            poseStack.translate((float)addTrans[0], (float)addTrans[1], (float)addTrans[2]);
        }

        if (addRot != null && addRot.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)addRot[2]));
            poseStack.mulPose(Axis.YP.rotationDegrees((float)addRot[1]));
        }

        // Рендерим части оружия
        poseStack.pushPose();

        // Применяем трансформации для рукоятки
        HbmAnimations.applyRelevantTransformation(poseStack, "Grip");
        getWeaponModel().renderPart(poseStack, builder, "Grip", packedLight, packedOverlay);

        // Затвор
        poseStack.pushPose();
        HbmAnimations.applyRelevantTransformation(poseStack, "Slide");
        getWeaponModel().renderPart(poseStack, builder, "Slide", packedLight, packedOverlay);
        poseStack.popPose();

        // Курок
        poseStack.pushPose();
        poseStack.translate(3.125f, 0.125f, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-25));
        poseStack.translate(-3.125f, -0.125f, 0);

        HbmAnimations.applyRelevantTransformation(poseStack, "Hammer");
        getWeaponModel().renderPart(poseStack, builder, "Hammer", packedLight, packedOverlay);
        poseStack.popPose();

        // Патрон (если есть в магазине)
        if(Objects.requireNonNull(gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, null) > 0) {
            poseStack.pushPose();
            HbmAnimations.applyRelevantTransformation(poseStack, "Bullet");
            getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Магазин
        poseStack.pushPose();
        HbmAnimations.applyRelevantTransformation(poseStack, "Magazine");
        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);
        poseStack.popPose();

        // Дым
        double smokeScale = 0.5;
        poseStack.pushPose();
        poseStack.translate(-10.25f, 1, 0);
        poseStack.scale((float)smokeScale, (float)smokeScale, (float)smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5D, packedLight, packedOverlay);
        poseStack.popPose();

        // Вспышка выстрела
        poseStack.pushPose();
        poseStack.translate(-10.25f, 1, 0);
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        poseStack.translate(0, 1, -2);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(6, 5, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -7.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 0.5f, -2);
    }


    protected void renderOtherWeapon(ItemStack stack, PoseStack poseStack,
                                     MultiBufferSource buffer, int packedLight,
                                     int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        getWeaponModel().renderPart(poseStack, builder, "Grip", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Slide", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Hammer", packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderOtherWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        renderOtherWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderOtherWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }


}