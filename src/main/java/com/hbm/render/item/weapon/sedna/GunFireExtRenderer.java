package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.main.HBMResourceManager;
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

public class GunFireExtRenderer extends BaseGunRenderer {

    private ResourceLocation FIREEXT_TEX = null;
    private HFRWavefrontObject FIREEXT_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (FIREEXT_TEX == null) {
            FIREEXT_TEX = HBMResourceManager.fireext_tex;
        }
        return FIREEXT_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (FIREEXT_MODEL == null) {
            FIREEXT_MODEL = HBMResourceManager.fireext;
        }
        return FIREEXT_MODEL;
    }

    private ResourceLocation getCurrentTexture(ItemStack stack) {
        GunItem gun = (GunItem) stack.getItem();
        IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);

        if (Objects.requireNonNull(mag).getType(stack, null) == BulletConfigRegistry.fext_foam) {
            return HBMResourceManager.fireext_foam_tex;
        }
        if (mag.getType(stack, null) == BulletConfigRegistry.fext_sand) {
            return HBMResourceManager.fireext_sand_tex;
        }
        return HBMResourceManager.fireext_tex;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return 0F; // Огнетушитель не поворачивается при прицеливании
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        // Огнетушитель не меняет FOV
        return fov;
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getCurrentTexture(stack)));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);

        double startX = -0.3;
        double startY = -0.5;
        double startZ = -0.5;

        double aimX = 0.0;
        double aimY = -0.25;
        double aimZ = 0.0;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.0F;
        float scale = 0.25f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 0.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(20));
        poseStack.mulPose(Axis.XP.rotationDegrees(15));
        poseStack.mulPose(Axis.YP.rotationDegrees(-5));
        poseStack.mulPose(Axis.YP.rotationDegrees(10));
        poseStack.translate(0.75, -2.75, 0.5);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.25D;
        poseStack.translate(0.2, 0.2, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        poseStack.mulPose(Axis.XP.rotationDegrees(-45));
        poseStack.mulPose(Axis.YP.rotationDegrees((System.currentTimeMillis() / 10) % 360));
        poseStack.scale((float) scale, (float) scale, (float) -scale);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -6.0D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -1, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getCurrentTexture(stack)));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getCurrentTexture(stack)));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getCurrentTexture(stack)));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getCurrentTexture(stack)));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);

        poseStack.popPose();
    }
}