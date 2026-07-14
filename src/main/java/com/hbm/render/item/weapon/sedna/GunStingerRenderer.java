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

public class GunStingerRenderer extends BaseGunRenderer {

    private ResourceLocation STINGER_TEX = null;
    private ResourceLocation PANZERSCHRECK_TEX = null;
    private HFRWavefrontObject STINGER_MODEL = null;
    private HFRWavefrontObject PANZERSCHRECK_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (STINGER_TEX == null) {
            STINGER_TEX = HBMResourceManager.stinger_tex;
        }
        return STINGER_TEX;
    }

    private ResourceLocation getPanzerschreckTexture() {
        if (PANZERSCHRECK_TEX == null) {
            PANZERSCHRECK_TEX = HBMResourceManager.panzerschreck_tex;
        }
        return PANZERSCHRECK_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (STINGER_MODEL == null) {
            STINGER_MODEL = HBMResourceManager.stinger;
        }
        return STINGER_MODEL;
    }

    private HFRWavefrontObject getPanzerschreckModel() {
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
        return fov * (1 - aimingProgress * 0.5F);
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        // Не рендерим если полностью прицелились
        float aimingProgress = getAimingProgress(partialTick);
        if (aimingProgress >= 1.0F) return;

        poseStack.translate(0, 0, 0.875);

        float offset = 0.8F;

        double startX = -3.75F * offset;
        double startY = -9F * offset;
        double startZ = -3.5F * offset;

        double aimX = -2.625F * offset;
        double aimY = -6.5;
        double aimZ = -8.5F;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? 1.5f : 0.0f;
        float yOffset = 0.4F;
        float scale = 1.5f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        // Не рендерим если полностью прицелились
        if (getAimingProgress(partialTick) >= 1.0F) return;

        GunItem gun = (GunItem) stack.getItem();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] reload = HbmAnimations.getRelevantTransformation("RELOAD", 0);
        double[] rocket = HbmAnimations.getRelevantTransformation("ROCKET", 0);

        // Базовая анимация EQUIP
        poseStack.translate(0, -1, -1);
        if (equip != null) poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
        poseStack.translate(0, 1, 1);

        // Анимация перезарядки
        poseStack.translate(0, -4, -3);
        if (reload != null) poseStack.mulPose(Axis.XP.rotationDegrees((float) reload[0]));
        poseStack.translate(0, 4, 3);

        poseStack.pushPose();
        {
            // Поворачиваем модель на 180 градусов (как в оригинале)
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            // Рендерим основную модель Stinger
            getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
        }
        poseStack.popPose();

        // Рендерим ракету (используем модель Panzerschreck)
        VertexConsumer rocketBuilder = buffer.getBuffer(RenderType.entityCutout(getPanzerschreckTexture()));

        poseStack.pushPose();
        {
            if (rocket != null && rocket.length >= 3) {
                poseStack.translate(rocket[0], rocket[1] + 3.5, rocket[2] - 3);
            } else {
                poseStack.translate(0, 3.5, -3);
            }

            getPanzerschreckModel().renderPart(poseStack, rocketBuilder, "Rocket", packedLight, packedOverlay);

            // Рендерим текст "Not accurate"
            renderWarningLabel(poseStack, buffer, packedLight);
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

    private void renderWarningLabel(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        String label = "Not accurate";
        int lightmap = 0xF000F0; // Максимальный свет для текста

        float scale = 0.04f;
        float textWidth = Minecraft.getInstance().font.width(label) * scale;

        poseStack.translate(0.025F, -0.5F, textWidth / 2 - 3);
        poseStack.scale(scale, -scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XN.rotationDegrees(45));

        Minecraft.getInstance().font.drawInBatch(
                label,
                0, 0,
                0xff0000,
                false,
                poseStack.last().pose(),
                buffer,
                net.minecraft.client.gui.Font.DisplayMode.NORMAL,
                0,
                lightmap
        );

        poseStack.popPose();
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, -2.5, -3.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.06D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(12, 3, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -7.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        poseStack.translate(0, -4, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
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
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);

        poseStack.popPose();
    }
}