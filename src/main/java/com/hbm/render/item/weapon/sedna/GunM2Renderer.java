package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
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

public class GunM2Renderer extends BaseGunRenderer {

    private ResourceLocation M2_TEX = null;
    private HFRWavefrontObject M2_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (M2_TEX == null) {
            M2_TEX = HBMResourceManager.m2_tex;
        }
        return M2_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (M2_MODEL == null) {
            M2_MODEL = HBMResourceManager.m2;
        }
        return M2_MODEL;
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);


        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 1, -2.25);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, -1, 2.25);
        }

        // Применяем анимации отдачи
        if (recoil != null) {
            poseStack.translate(0, 0, recoil[2]);
        }

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Основная модель
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);

        // Рендерим дым
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        poseStack.translate(0, 1.625, 5);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale(0.375f, 0.375f, 0.375f);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.375, packedLight, packedOverlay);
        poseStack.popPose();

        // Вспышка выстрела
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        poseStack.translate(0, 1.625, 5);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        if (Minecraft.getInstance().player != null) {
            ItemStack mainHand = Minecraft.getInstance().player.getMainHandItem();
            if (mainHand.getItem() instanceof GunItem gunItem) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gunItem.shotRand)));
            }
        }

        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8f;

        double startX = -1.5f * offset;
        double startY = -2.5f * offset;
        double startZ = 1.75f * offset;

        double aimX = -2.56;
        double aimY = -10.1 / 8.0;
        double aimZ = 0.8;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;
        poseStack.translate(x, y, z);

        float xOffset = 2.5F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.0f;
        float yOffset = 0.25F;
        poseStack.translate(xOffset, 0, -2.8);

        float scale = 0.8f;
        poseStack.scale(scale, scale, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0.5, -2, 2.4);
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        super.setupGUITransforms(poseStack, stack);
        double scale = 0.08D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, -5, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -15D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -1.5, -0.5);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
        }
        return 2.75F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * 0.33F);
    }

    // Метод для стола модификаций
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