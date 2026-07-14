package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mags.IMagazine;
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

import java.util.Objects;

public class GunFatManRenderer extends BaseGunRenderer {

    private static final String LABEL = "AUTO";

    private ResourceLocation FATMAN_TEX = null;
    private HFRWavefrontObject FATMAN_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (FATMAN_TEX == null) {
            FATMAN_TEX = HBMResourceManager.fatman_tex;
        }
        return FATMAN_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (FATMAN_MODEL == null) {
            FATMAN_MODEL = HBMResourceManager.fatman;
        }
        return FATMAN_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
        }
        return 2.5F;
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
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] lid = HbmAnimations.getRelevantTransformation("LID", 0);
        double[] nuke = HbmAnimations.getRelevantTransformation("NUKE", 0);
        double[] piston = HbmAnimations.getRelevantTransformation("PISTON", 0);
        double[] handle = HbmAnimations.getRelevantTransformation("HANDLE", 0);
        double[] gauge = HbmAnimations.getRelevantTransformation("GAUGE", 0);

        // Применяем анимацию экипировки
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 1, -2);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, -1, 2);
        }

        // Рендерим пусковую установку
        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);

        // Рендерим рукоятку с анимацией
        poseStack.pushPose();
        if (handle != null && handle.length >= 3) {
            poseStack.translate(0, 0, handle[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Handle", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим манометр с анимацией
        poseStack.pushPose();
        if (gauge != null && gauge.length >= 3) {
            poseStack.translate(0.4375, -0.875, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) gauge[2]));
            poseStack.translate(-0.4375, 0.875, 0);
        }
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим крышку с анимацией
        poseStack.pushPose();
        if (lid != null && lid.length >= 3) {
            poseStack.translate(0.25, 0.125, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) lid[2]));
            poseStack.translate(-0.25, -0.125, 0);
        }
        getWeaponModel().renderPart(poseStack, builder, "Lid", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим поршень с анимацией
        poseStack.pushPose();
        if (piston != null && piston.length >= 3) {
            poseStack.translate(0, 0, piston[2]);
        }
        boolean isLoaded = isWeaponLoaded(stack);
        if (!isLoaded && (piston == null || piston[2] == 0)) {
            poseStack.translate(0, 0, 3);
        }
        getWeaponModel().renderPart(poseStack, builder, "Piston", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим мини-ядерный заряд
        if (isLoaded || (nuke != null && (nuke[0] != 0 || nuke[1] != 0 || nuke[2] != 0))) {
            poseStack.pushPose();
            if (nuke != null && nuke.length >= 3) {
                poseStack.translate(nuke[0], nuke[1], nuke[2]);
            }
            renderNuke(stack, poseStack, buffer, packedLight, packedOverlay, partialTick);
            poseStack.popPose();
        }

    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -1.25F * offset;
        double startZ = 0.5F * offset;

        double aimX = -1F * offset;
        double aimY = -1.25F * offset;
        double aimZ = 0F * offset;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.875f;
        float scale = 0.5f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 2.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(-0.5, 0.5, -3);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.translate(0.5, 0.5, 0);
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));

    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -8.75D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Handle", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Lid", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Piston", packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Handle", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Lid", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Piston", packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Handle", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Lid", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Piston", packedLight, packedOverlay);
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Handle", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Lid", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Piston", packedLight, packedOverlay);

        poseStack.popPose();
    }

    private boolean isWeaponLoaded(ItemStack stack) {
        if (stack.getItem() instanceof GunItem gun) {
            IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
            return Objects.requireNonNull(mag).getAmount(stack, Objects.requireNonNull(Minecraft.getInstance().player).getInventory()) > 0;
        }
        return false;
    }

    private void renderNuke(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                            int packedLight, int packedOverlay, float partialTick) {
        GunItem gun = (GunItem) stack.getItem();
        IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
        Object ammoType = Objects.requireNonNull(mag).getType(stack, null);

        if (ammoType != null && ammoType.toString().contains("balefire")) {
            renderBalefire(poseStack, buffer, packedLight, packedOverlay, partialTick);
        } else {
            VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.fatman_mininuke_tex));
            getWeaponModel().renderPart(poseStack, builder, "MiniNuke", packedLight, packedOverlay);
        }
    }

    public void renderBalefire(PoseStack poseStack, MultiBufferSource buffer,
                                      int packedLight, int packedOverlay, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.fatman_balefire_tex));
        getWeaponModel().renderPart(poseStack, builder, "MiniNuke", packedLight, packedOverlay);

        // Glint effect for balefire
        VertexConsumer glintBuilder = buffer.getBuffer(RenderType.glint());
        float offset = Objects.requireNonNull(mc.player).tickCount + partialTick;
        float scale = 2F;
        float speed = -6F;
        int layers = 3;
        float r = 0F;
        float g = 0.8F;
        float b = 0.15F;
        float glintColor = 0.76F;

        poseStack.pushPose();

        for (int k = 0; k < layers; ++k) {
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.ZP.rotationDegrees(30.0F - k * 60.0F));

            float movement = offset * (0.001F + k * 0.003F) * speed;
            poseStack.translate(0, movement, 0);

            getWeaponModel().renderPart(poseStack, glintBuilder, "MiniNuke", packedLight, packedOverlay);
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}