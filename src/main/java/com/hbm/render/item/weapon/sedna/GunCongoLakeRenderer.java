package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.main.HBMResourceManager;
import com.hbm.particle.SpentCasing;
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

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class GunCongoLakeRenderer extends BaseGunRenderer {

    private ResourceLocation CONGOLAKE_TEX = null;
    private HFRWavefrontObject CONGOLAKE_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (CONGOLAKE_TEX == null) {
            CONGOLAKE_TEX = HBMResourceManager.congolake_tex;
        }
        return CONGOLAKE_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (CONGOLAKE_MODEL == null) {
            CONGOLAKE_MODEL = HBMResourceManager.congolake;
        }
        return CONGOLAKE_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
        }
        return 2.75F;
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

        HbmAnimations.applyRelevantTransformation(poseStack, "Gun");
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим насос с анимацией
        poseStack.pushPose();
            HbmAnimations.applyRelevantTransformation(poseStack,"Pump");
            getWeaponModel().renderPart(poseStack, builder, "Pump", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим прицел с анимацией прицеливания
        poseStack.pushPose();
            HbmAnimations.applyRelevantTransformation(poseStack,"Sight");
            float aimingProgress = getAimingProgress(partialTick);
            poseStack.translate(0, 2.125f, 3);
            poseStack.mulPose(Axis.XP.rotationDegrees(aimingProgress * -90));
            poseStack.translate(0, -2.125f, -3);
            getWeaponModel().renderPart(poseStack, builder, "Sight", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим петлю
        poseStack.pushPose();
            HbmAnimations.applyRelevantTransformation(poseStack,"Loop");
            getWeaponModel().renderPart(poseStack, builder, "Loop", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим внешнюю защиту
        poseStack.pushPose();
            HbmAnimations.applyRelevantTransformation(poseStack,"GuardOuter");
            getWeaponModel().renderPart(poseStack, builder, "GuardOuter", packedLight, packedOverlay);

            poseStack.pushPose();
                HbmAnimations.applyRelevantTransformation(poseStack,"GuardInner");
                getWeaponModel().renderPart(poseStack, builder, "GuardInner", packedLight, packedOverlay);
            poseStack.popPose();
        poseStack.popPose();

        // Рендерим снаряд
        poseStack.pushPose();
        HbmAnimations.applyRelevantTransformation(poseStack,"Shell");

        // Получаем информацию о снаряде
        IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
        if (gun.getLastAnim(stack, 0) != com.hbm.render.anim.AnimationEnums.GunAnimation.INSPECT ||
                mag.getAmount(stack, net.minecraft.client.Minecraft.getInstance().player.getInventory()) > 0) {

            VertexConsumer casingBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.casings_tex));

            SpentCasing casing = mag.getCasing(stack, net.minecraft.client.Minecraft.getInstance().player.getInventory());
            int[] colors = casing != null ? casing.getColors() : new int[] { SpentCasing.COLOR_CASE_40MM };

            Color shellColor = new Color(colors[0]);
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.001f); // Небольшое смещение чтобы избежать z-конфликта
            renderColoredPart(poseStack, casingBuilder, "Shell", shellColor, packedLight, packedOverlay);
            poseStack.popPose();

            Color shellForeColor = new Color(colors.length > 1 ? colors[1] : colors[0]);
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.002f); // Еще большее смещение для передней части
            renderColoredPart(poseStack, casingBuilder, "ShellFore", shellForeColor, packedLight, packedOverlay);
            poseStack.popPose();
        }
        poseStack.popPose();

        // Рендерим дым
        GunConfig cfg = gun.getConfig(stack, 0);
        double smokeScale = 0.25;

        poseStack.pushPose();
        poseStack.translate(0, 1.75f, 4.25f);
        double[] gunAnim = HbmAnimations.getRelevantTransformation("Gun", 0);
        if (gunAnim != null && gunAnim.length >= 6) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) -gunAnim[5]));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) -gunAnim[4]));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) -gunAnim[3]));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, cfg.smokeNodes, 1D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 1.75f, 4.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();
    }

    private void renderColoredPart(PoseStack poseStack, VertexConsumer builder, String partName, Color color,
                                   int packedLight, int packedOverlay) {
        float red = color.getRed() / 255.0f;
        float green = color.getGreen() / 255.0f;
        float blue = color.getBlue() / 255.0f;

        // Создаем временный builder с цветом
        getWeaponModel().renderPartColored(poseStack, builder, partName, packedLight, packedOverlay, red, green, blue, 1.0f);
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -2F * offset;
        double startZ = 1.25F * offset;

        double aimX = 0.07;
        double aimY = -12 / 8D;
        double aimZ = 0.25;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.7f;
        float scale = 0.5f;
        z = z - 0.5;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        poseStack.translate(0, -2.5f, 4);
        double scale = 2.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.15D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(4.5, 0.5f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -15D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -1.25f, 0);
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