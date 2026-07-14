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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GunAm180Render extends BaseGunRenderer {

    private ResourceLocation AM180_TEX = null;
    private HFRWavefrontObject AM180_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack ignoredStack) {
        if (AM180_TEX == null) {
            AM180_TEX = HBMResourceManager.am180_tex;
        }
        return AM180_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (AM180_MODEL == null) {
            AM180_MODEL = HBMResourceManager.am180;
        }
        return AM180_MODEL;
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

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] magazine = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] magTurn = HbmAnimations.getRelevantTransformation("MAGTURN", 0);
        double[] magSpin = HbmAnimations.getRelevantTransformation("MAGSPIN", 0);
        double[] bolt = HbmAnimations.getRelevantTransformation("BOLT", 0);
        double[] turn = HbmAnimations.getRelevantTransformation("TURN", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        boolean silenced = hasSilencer(stack, 0);

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -2, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 2, 6);
        }

        // Применяем анимацию TURN
        if (turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) turn[2]));
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим глушитель если есть
        if (silenced) {
            getWeaponModel().renderPart(poseStack, builder, "Silencer", packedLight, packedOverlay);
        }

        // Рендерим спусковой крючок
        poseStack.pushPose();
        HbmAnimations.applyRelevantTransformation(poseStack, "Trigger");
        getWeaponModel().renderPart(poseStack, builder, "Trigger", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим затвор
        poseStack.pushPose();
        if (bolt != null && bolt.length >= 3) {
            poseStack.translate(0, 0, (float) bolt[2]);
        }
        HbmAnimations.applyRelevantTransformation(poseStack, "Bolt");
        getWeaponModel().renderPart(poseStack, builder, "Bolt", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим магазин с анимациями
        poseStack.pushPose();
        if (magazine != null && magazine.length >= 3) {
            poseStack.translate((float) magazine[0], (float) magazine[1], (float) magazine[2]);
        }

        // Применяем поворот магазина
        if (magTurn != null && magTurn.length >= 1) {
            poseStack.translate(0, 2.0625f, 3.75f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) magTurn[0]));
            if (magTurn.length >= 3) {
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) magTurn[2]));
            }
            poseStack.translate(0, -2.0625f, -3.75f);
        }

        // Применяем вращение магазина
        if (magSpin != null && magSpin.length >= 1) {
            poseStack.translate(0, 2.3125f, 1.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) magSpin[0]));
            poseStack.translate(0, -2.3125f, -1.5f);
        }

        HbmAnimations.applyRelevantTransformation(poseStack, "Mag");

        // Рендерим магазин с вращением в зависимости от количества патронов
        poseStack.pushPose();
        int mag = Objects.requireNonNull(gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, Objects.requireNonNull(Minecraft.getInstance().player).getInventory());
        poseStack.translate(0, 0, 1.5f);
        poseStack.mulPose(Axis.YN.rotationDegrees(mag / 59.0f * 360.0f));
        poseStack.translate(0, 0, -1.5f);
        getWeaponModel().renderPart(poseStack, builder, "Mag", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим пластину магазина
        getWeaponModel().renderPart(poseStack, builder, "MagPlate", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим дымовые узлы
        poseStack.pushPose();
        float smokeYPos = silenced ? 17.0f : 13.0f;
        poseStack.translate(0, 1.875f, smokeYPos);
        if (turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) -turn[2]));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.25D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        float flashZPos = silenced ? 16.75f : 12.0f;
        poseStack.translate(0, 1.875f, flashZPos);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        float flashScale = silenced ? 0.5f : 0.75f;
        poseStack.scale(flashScale, flashScale, flashScale);
        float flashSize = silenced ? 5.0f : 7.5f;
        renderMuzzleFlash(poseStack, gun.lastShot[0], flashSize, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        poseStack.translate(0, 0, 0.875f);

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1F * offset;
        double startY = -1F * offset;

        double aimX = 0.06;
        double aimY = -4.05 / 8D;
        double aimZ = 0.25;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = (double) offset + (aimZ - (double) offset) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 0.6f;
        }
        float yOffset = 0.25F;
        float scale = 0.23f;
        z = z + 0.85;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.2D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, -0.5, 1);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.05D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(15, 4, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 0, -2);
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
     * Метод для рендеринга стандартных частей AM180
     * Используется во всех режимах кроме first-person
     */
    private void renderStandardParts(PoseStack poseStack, VertexConsumer builder,
                                     ItemStack stack, int packedLight, int packedOverlay) {
        List<String> parts = Arrays.asList(
                "Gun",
                "Trigger",
                "Bolt",
                "Mag",
                "MagPlate"
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
}