package com.hbm.render.item.weapon.sedna;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.items.weapon.sedna.GunItem;
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

public class GunPepperboxRenderer extends BaseGunRenderer {

    private ResourceLocation PEPPERBOX_TEX = null;
    private HFRWavefrontObject PEPPERBOX_MODEL = null;


    @Override
    protected ResourceLocation getWeaponTexture() {
        if (PEPPERBOX_TEX == null) {
            PEPPERBOX_TEX = HBMResourceManager.pepperbox_tex;
        }
        return PEPPERBOX_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (PEPPERBOX_MODEL == null) {
            PEPPERBOX_MODEL = HBMResourceManager.pepperbox;
        }
        return PEPPERBOX_MODEL;
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        // Получаем анимации
        double[] translate = HbmAnimations.getRelevantTransformation("TRANSLATE", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] cylinder = HbmAnimations.getRelevantTransformation("ROTATE", 0);
        double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER", 0);
        double[] trigger = HbmAnimations.getRelevantTransformation("TRIGGER", 0);
        double[] loader = HbmAnimations.getRelevantTransformation("LOADER", 0);
        double[] shot = HbmAnimations.getRelevantTransformation("SHOT", 0);

        double rec = -recoil[0];


        // Применяем анимации
        if (translate != null) {
            poseStack.translate(translate[0], translate[1], translate[2]);
        }

        poseStack.translate(0, 5, -5);
        poseStack.mulPose(Axis.XP.rotationDegrees((float)rec));
        poseStack.translate(0, -5, 5);



        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Рендерим спидлоадер если есть анимация
        if (loader != null && (loader[0] != 0 || loader[1] != 0 || loader[2] != 0)) {
            poseStack.pushPose();
            poseStack.translate(loader[0], loader[1], loader[2]);
            getWeaponModel().renderPart(poseStack, builder, "Speedloader", packedLight, packedOverlay);

            if (shot != null && shot[0] != 0) {
                getWeaponModel().renderPart(poseStack, builder, "Shot", packedLight, packedOverlay);
            }
            poseStack.popPose();
        }

        // 1. Рендерим рукоять
        getWeaponModel().renderPart(poseStack, builder, "Grip", packedLight, packedOverlay);

        // 2. Рендерим барабан с вращением
        poseStack.pushPose();
        if (cylinder != null && cylinder.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) cylinder[0]));
        }
        getWeaponModel().renderPart(poseStack, builder, "Cylinder", packedLight, packedOverlay);
        poseStack.popPose();

        // 3. Рендерим курок
        poseStack.pushPose();
        if (hammer != null && hammer.length >= 1) {
            poseStack.translate(0, 0.375f, -1.875f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) hammer[0]));
            poseStack.translate(0, -0.375f, 1.875f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Hammer", packedLight, packedOverlay);
        poseStack.popPose();

        // 4. Рендерим спусковой крючок
        poseStack.pushPose();
        if (trigger != null && trigger.length >= 1) {
            poseStack.translate(0, 0, (float) (-trigger[0] * 0.5));
        }
        getWeaponModel().renderPart(poseStack, builder, "Trigger", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 0.5, 7);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 0.5f, 7);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.pushPose();
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(45));
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8f;

        double startX = -1.25f * offset;
        double startY = -0.75f * offset;

        double aimX = 0.055;
        double aimY = -2.5 / 8.0;
        double aimZ = 0.5;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = (double) offset + (aimZ - (double) offset) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.0f;
        float yOffset = 0.35F;
        float scale = 0.29f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        float scale = 0.08f;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(9, 4, 0);
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
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Speedloader", "Shot");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Speedloader", "Shot");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Speedloader", "Shot");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
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