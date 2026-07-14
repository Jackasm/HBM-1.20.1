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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GunHenryRenderer extends BaseGunRenderer {

    private final String textureName;
    private ResourceLocation HENRY_TEX = null;
    private HFRWavefrontObject HENRY_MODEL = null;

    public GunHenryRenderer(String textureName) {
        this.textureName = textureName;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (HENRY_TEX == null) {
            // Определяем текстуру в зависимости от имени
            if (Objects.equals(textureName, "henry_lincoln")) {
                HENRY_TEX = HBMResourceManager.henry_lincoln_tex;
            } else {
                HENRY_TEX = HBMResourceManager.henry_tex;
            }
        }
        return HENRY_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (HENRY_MODEL == null) {
            HENRY_MODEL = HBMResourceManager.henry;
        }
        return HENRY_MODEL;
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
        double[] sight = HbmAnimations.getRelevantTransformation("SIGHT", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER", 0);
        double[] lever = HbmAnimations.getRelevantTransformation("LEVER", 0);
        double[] turn = HbmAnimations.getRelevantTransformation("TURN", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] twist = HbmAnimations.getRelevantTransformation("TWIST", 0);
        double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", 0);
        double[] yeet = HbmAnimations.getRelevantTransformation("YEET", 0);
        double[] roll = HbmAnimations.getRelevantTransformation("ROLL", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate((float) (recoil[0] * 2), (float) recoil[1], (float) recoil[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (recoil[2] * 5)));
        }

        // Применяем анимацию TURN
        if (turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) turn[2]));
        }

        // Применяем анимацию YEET
        if (yeet != null && yeet.length >= 3) {
            poseStack.translate((float) yeet[0], (float) yeet[1], (float) yeet[2]);
        }

        // Применяем анимацию ROLL
        if (roll != null && roll.length >= 3) {
            poseStack.translate(0, 1, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) roll[2]));
            poseStack.translate(0, -1, 0);
        }

        // Применяем анимацию LIFT
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, -4, 4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 4, -4);
        }

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 2, -4);
            poseStack.mulPose(Axis.XN.rotationDegrees((float) equip[0]));
            poseStack.translate(0, -2, 4);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим прицел
        poseStack.pushPose();
        if (sight != null && sight.length >= 1) {
            poseStack.translate(0, 1.25f, -0.1875f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) sight[0]));
            poseStack.translate(0, -1.25f, 0.1875f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Sight", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим курок
        poseStack.pushPose();
        float hammerRotation = -30f;
        if (hammer != null && hammer.length >= 1) {
            hammerRotation += (float) hammer[0];
        }
        poseStack.translate(0, 0.625f, -3);
        poseStack.mulPose(Axis.XP.rotationDegrees(hammerRotation));
        poseStack.translate(0, -0.625f, 3);
        getWeaponModel().renderPart(poseStack, builder, "Hammer", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим рычаг
        poseStack.pushPose();
        if (lever != null && lever.length >= 1) {
            poseStack.translate(0, 0.25f, -2.3125f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lever[0]));
            poseStack.translate(0, -0.25f, 2.3125f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Lever", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим переднюю часть
        poseStack.pushPose();
        if (twist != null && twist.length >= 3) {
            poseStack.translate(0, 1, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) twist[2]));
            poseStack.translate(0, -1, 0);
        }
        getWeaponModel().renderPart(poseStack, builder, "Front", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим патрон
        poseStack.pushPose();
        if (bullet != null && bullet.length >= 3) {
            poseStack.translate((float) bullet[0], (float) bullet[1], (float) (bullet[2] - 1));
        }
        getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим дымовые узлы
        poseStack.pushPose();
        poseStack.translate(0, 1, 8);
        if (turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZN.rotationDegrees((float) turn[2]));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.25D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 1, 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        renderMuzzleFlash(poseStack, gun.lastShot[0], 5.0f, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.25F * offset;
        double startY = -1F * offset;
        double startZ = 1.75F * offset;

        double aimX = 0.06;
        double aimY = -3.5 / 8D;
        double aimZ = 0.75;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;
        z = z - 0.5;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.0f;
        }
        float yOffset = 0.35F;
        float scale = 0.29f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);

        // Дополнительное вращение при прицеливании
        double r = -2.5 * aimingProgress;
        poseStack.mulPose(Axis.XP.rotationDegrees((float) r));
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 0.25f, 1.5);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(9f, 3f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -7.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга всех частей винтовки Генри
     * Используется во всех режимах кроме first-person
     */
    private void renderAllParts(PoseStack poseStack, VertexConsumer builder,
                                int packedLight, int packedOverlay) {
        List<String> parts = Arrays.asList(
                "Gun",
                "Sight",
                "Hammer",
                "Lever",
                "Front",
                "Bullet"
        );

        // Рендерим все части по отдельности
        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }
    }
}