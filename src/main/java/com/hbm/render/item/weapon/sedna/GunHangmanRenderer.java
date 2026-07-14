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

public class GunHangmanRenderer extends BaseGunRenderer {

    private ResourceLocation HANGMAN_TEX = null;
    private HFRWavefrontObject HANGMAN_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (HANGMAN_TEX == null) {
            HANGMAN_TEX = HBMResourceManager.hangman_tex;
        }
        return HANGMAN_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (HANGMAN_MODEL == null) {
            HANGMAN_MODEL = HBMResourceManager.hangman;
        }
        return HANGMAN_MODEL;
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
        float offset = 0.8F;

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] roll = HbmAnimations.getRelevantTransformation("ROLL", 0);
        double[] turn = HbmAnimations.getRelevantTransformation("TURN", 0);
        double[] smack = HbmAnimations.getRelevantTransformation("SMACK", 0);
        double[] lid = HbmAnimations.getRelevantTransformation("LID", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] bullets = HbmAnimations.getRelevantTransformation("BULLETS", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Применяем анимацию TURN
        if (turn != null && turn.length >= 2) {
            poseStack.translate(1.5F * offset, 0, -1);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) turn[1]));
            poseStack.translate(-1.5F * offset, 0, 1);
        }

        // Применяем анимацию ROLL
        if (roll != null && roll.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) roll[2]));
        }

        // Применяем анимацию SMACK
        if (smack != null && smack.length >= 3) {
            poseStack.translate((float) smack[0], (float) smack[1], (float) smack[2]);
        }



        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -4, -10);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 4, 10);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Rifle", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Internals", packedLight, packedOverlay);

        // Рендерим крышку
        poseStack.pushPose();
        if (lid != null && lid.length >= 3) {
            poseStack.translate(-2.1875f, -1.75f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) lid[2]));
            poseStack.translate(2.1875f, 1.75f, 0);
        }
        getWeaponModel().renderPart(poseStack, builder, "Lid", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим магазин и патроны
        poseStack.pushPose();
        if (mag != null && mag.length >= 3) {
            poseStack.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);

        // Рендерим патроны только если нужно
        if (bullets != null && bullets.length >= 1 && bullets[0] == 0) {
            getWeaponModel().renderPart(poseStack, builder, "Bullets", packedLight, packedOverlay);
        }
        poseStack.popPose();

        // Рендерим дымовые узлы
        double smokeScale = 1.5;
        poseStack.pushPose();
        poseStack.translate(0, 0, 29);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float)smokeScale, (float)smokeScale, (float)smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 0, 29);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        poseStack.scale(2.0f, 2.0f, 2.0f);
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -0.875F * offset;
        double startZ = 1.75F * offset;

        double aimX = 0.06;
        double aimY = -0.8 / 8D;
        double aimZ = 1.25;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.5f;
        }
        float yOffset = 0.35F;
        float scale = 0.1f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 0.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 4.25f, 5);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.023D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(30f, 15f, 0);
    }


    protected void setupEntityTransforms(PoseStack poseStack, ItemStack stack) {
        // Для entity рендеринга
        double scale = 0.0625D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -2.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        renderAllParts(poseStack, builder, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга всех частей Hangman
     */
    private void renderAllParts(PoseStack poseStack, VertexConsumer builder,
                                int packedLight, int packedOverlay) {
        List<String> parts = Arrays.asList(
                "Rifle",
                "Internals",
                "Lid",
                "Magazine",
                "Bullets"
        );

        // Рендерим все части
        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }
    }
}