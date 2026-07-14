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

public class GunLightRevolverRenderer extends BaseGunRenderer {

    private final String textureName;
    private ResourceLocation REVOLVER_TEX = null;
    private HFRWavefrontObject REVOLVER_MODEL = null;

    public GunLightRevolverRenderer(String textureName) {
        this.textureName = textureName;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (REVOLVER_TEX == null) {
            REVOLVER_TEX = HBMResourceManager.light_revolver_tex;
            if (Objects.equals(textureName, "light_revolver_atlas")) {
                REVOLVER_TEX = HBMResourceManager.light_revolver_atlas_tex;
            }
        }
        return REVOLVER_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (REVOLVER_MODEL == null) {
            REVOLVER_MODEL = HBMResourceManager.bio_revolver;
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
        return fov * (1 - aimingProgress * 0.33F);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        // Получаем анимации
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] reloadMove = HbmAnimations.getRelevantTransformation("RELOAD_MOVE", 0);
        double[] reloadRot = HbmAnimations.getRelevantTransformation("RELOAD_ROT", 0);
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] front = HbmAnimations.getRelevantTransformation("FRONT", 0);
        double[] latch = HbmAnimations.getRelevantTransformation("LATCH", 0);
        double[] drum = HbmAnimations.getRelevantTransformation("DRUM", 0);
        double[] drumPush = HbmAnimations.getRelevantTransformation("DRUM_PUSH", 0);
        double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate((float) recoil[0], (float) recoil[1], (float) recoil[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (recoil[2] * 10)));
        }

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 0, -7);
            poseStack.mulPose(Axis.XN.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 0, 7);
        }

        // Применяем анимации перезарядки
        if (reloadMove != null && reloadMove.length >= 3) {
            poseStack.translate((float) reloadMove[0], (float) reloadMove[1], (float) reloadMove[2]);
        }

        if (reloadRot != null && reloadRot.length >= 3) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) reloadRot[0]));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) reloadRot[2]));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) reloadRot[1]));
        }

        // Рендерим рукоятку
        getWeaponModel().renderPart(poseStack, builder, "Grip", packedLight, packedOverlay);

        // Рендерим переднюю часть с барабаном
        poseStack.pushPose();

        // Применяем анимацию FRONT
        if (front != null && front.length >= 3) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) front[2]));
        }

        // Рендерим ствол
        getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);

        // Рендерим защелку
        poseStack.pushPose();
        if (latch != null && latch.length >= 3) {
            poseStack.translate(0, 2.3125f, -0.875f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) latch[2]));
            poseStack.translate(0, -2.3125f, 0.875f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Latch", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим барабан
        poseStack.pushPose();
        poseStack.translate(0, 1, 0);

        // Применяем вращение барабана
        if (drum != null && drum.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (drum[2] * 60)));
        }
        poseStack.translate(0, -1, 0);

        // Применяем выталкивание барабана
        if (drumPush != null && drumPush.length >= 3) {
            poseStack.translate(0, 0, (float) drumPush[2]);
        }

        getWeaponModel().renderPart(poseStack, builder, "Drum", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose(); // Конец передней части

        // Рендерим курок
        poseStack.pushPose();
        poseStack.translate(0, 0, -4.5f);
        float hammerRotation = 0f;
        if (hammer != null && hammer.length >= 3) {
            hammerRotation = (float) (-45 + 45 * hammer[2]);
        }
        poseStack.mulPose(Axis.XP.rotationDegrees(hammerRotation));
        poseStack.translate(0, 0, 4.5f);
        getWeaponModel().renderPart(poseStack, builder, "Hammer", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 9.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();

        // Рендерим дымовые узлы
        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 9.25f);
        if (recoil != null && recoil.length >= 3) {
            poseStack.mulPose(Axis.XN.rotationDegrees((float) (recoil[2] * 10)));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5D, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.0F * offset;
        double startY = -0.75F * offset;

        double aimX = 0.16;
        double aimY = -3.5 / 8D;
        double aimZ = 0.25;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = (double) offset + (aimZ - (double) offset) * aimingProgress;

        float xOffset = -0.1F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 0.7f;
        }
        float yOffset = 0.35F;
        float scale = 0.15f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 0.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 1, 1);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.07D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(10f, 5f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 1.5f, 0);
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
     * Метод для рендеринга всех частей револьвера
     * Используется во всех режимах кроме first-person
     */
    private void renderAllParts(PoseStack poseStack, VertexConsumer builder,
                                int packedLight, int packedOverlay) {
        List<String> parts = Arrays.asList(
                "Grip",
                "Barrel",
                "Latch",
                "Drum",
                "Hammer"
        );

        // Рендерим все части по отдельности
        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }
    }
}