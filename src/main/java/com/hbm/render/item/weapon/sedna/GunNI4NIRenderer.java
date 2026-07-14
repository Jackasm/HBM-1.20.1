package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.impl.ItemGunNI4NI;
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

public class GunNI4NIRenderer extends BaseGunRenderer {

    private ResourceLocation NI4NI_TEX = null;
    private ResourceLocation NI4NI_GREYSCALE_TEX = null;
    private HFRWavefrontObject NI4NI_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        int[] colors = ItemGunNI4NI.getColors(stack);
        if (colors != null) {
            if (NI4NI_GREYSCALE_TEX == null) {
                NI4NI_GREYSCALE_TEX = HBMResourceManager.n_i_4_n_i_greyscale_tex;
            }
            return NI4NI_GREYSCALE_TEX;
        } else {
            if (NI4NI_TEX == null) {
                NI4NI_TEX = HBMResourceManager.n_i_4_n_i_tex;
            }
            return NI4NI_TEX;
        }
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (NI4NI_MODEL == null) {
            NI4NI_MODEL = HBMResourceManager.n_i_4_n_i;
        }
        return NI4NI_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * 0.33F);
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float offset = 0.8F;
        float aimingProgress = getAimingProgress(partialTick);

        // Стартовые позиции
        double startX = -1.0F * offset;
        double startY = -1.0F * offset;
        double startZ = 1.0F * offset;

        // Целевые позиции при прицеливании
        double aimX = 0;
        double aimY = -5 / 8D;
        double aimZ = 0.125;

        // Интерполяция
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        // Смещение для рук
        float xOffset = 0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1f;
        }

        float yOffset = 0.3F;
        float scale = 0.35F;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        int[] colors = ItemGunNI4NI.getColors(stack);
        int dark = colors != null ? colors[0] : 0xffffff;
        int light = colors != null ? colors[1] : 0xffffff;
        int grip = colors != null ? colors[2] : 0xffffff;

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] drum = HbmAnimations.getRelevantTransformation("DRUM", 0);

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 0, -2.25f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 0, 2.25f);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 1) {
            poseStack.translate(0, -1, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) recoil[0]));
            poseStack.translate(0, 1, 6);
        }

        poseStack.pushPose();

        // Рендерим части модели с разными цветами
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Темные части
        setVertexColor(builder, dark);
        getWeaponModel().renderPart(poseStack, builder, "FrameDark", packedLight, packedOverlay);

        // Ручка
        setVertexColor(builder, grip);
        getWeaponModel().renderPart(poseStack, builder, "Grip", packedLight, packedOverlay);

        // Светлые части
        setVertexColor(builder, light);
        getWeaponModel().renderPart(poseStack, builder, "FrameLight", packedLight, packedOverlay);

        // Цилиндр с анимацией вращения
        poseStack.pushPose();
        if (drum != null && drum.length >= 3) {
            poseStack.translate(0, 1.1875f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) drum[2]));
            poseStack.translate(0, -1.1875f, 0);
        }
        getWeaponModel().renderPart(poseStack, builder, "Cylinder", packedLight, packedOverlay);

        // Яркие части цилиндра (fullbright)
        VertexConsumer fullbrightBuilder = buffer.getBuffer(RenderType.entityTranslucentEmissive(getWeaponTexture(stack)));
        fullbrightBuilder.color(255, 255, 255, 255);
        getWeaponModel().renderPart(poseStack, fullbrightBuilder, "CylinderHighlights", 15728880, packedOverlay);

        poseStack.popPose();

        // Ствол (fullbright)
        getWeaponModel().renderPart(poseStack, fullbrightBuilder, "Barrel", 15728880, packedOverlay);

        // Монеты (без текстуры)
        int coinCount = ItemGunNI4NI.getCoinCount(stack);
        VertexConsumer untexturedBuilder = buffer.getBuffer(RenderType.entityTranslucentEmissive(WHITE_TEXTURE));
        untexturedBuilder.color(0, 0, 255, 255);
        if (coinCount > 3) {
            int coinColor = coinCount > 7 ? 0xFF00FF00 : 0xFF0000FF; // Зеленый или синий
            float r = ((coinColor >> 16) & 0xFF) / 255.0F;
            float g = ((coinColor >> 8) & 0xFF) / 255.0F;
            float b = (coinColor & 0xFF) / 255.0F;
            getWeaponModel().renderPartColored(poseStack, untexturedBuilder, "Coin1", packedLight, packedOverlay, r, g, b, 1.0F);
        }

        if (coinCount > 2) {
            int coinColor = coinCount > 6 ? 0xFF00FF00 : 0xFF0000FF;
            float r = ((coinColor >> 16) & 0xFF) / 255.0F;
            float g = ((coinColor >> 8) & 0xFF) / 255.0F;
            float b = (coinColor & 0xFF) / 255.0F;
            getWeaponModel().renderPartColored(poseStack, builder, "Coin2", packedLight, packedOverlay, r, g, b, 1.0F);
        }

        if (coinCount > 1) {
            int coinColor = coinCount > 5 ? 0xFF00FF00 : 0xFF0000FF;
            float r = ((coinColor >> 16) & 0xFF) / 255.0F;
            float g = ((coinColor >> 8) & 0xFF) / 255.0F;
            float b = (coinColor & 0xFF) / 255.0F;
            getWeaponModel().renderPartColored(poseStack, builder, "Coin3", packedLight, packedOverlay, r, g, b, 1.0F);
        }

        if (coinCount > 0) {
            int coinColor = coinCount > 4 ? 0xFF00FF00 : 0xFF0000FF;
            float r = ((coinColor >> 16) & 0xFF) / 255.0F;
            float g = ((coinColor >> 8) & 0xFF) / 255.0F;
            float b = (coinColor & 0xFF) / 255.0F;
            getWeaponModel().renderPartColored(poseStack, builder, "Coin4", packedLight, packedOverlay, r, g, b, 1.0F);
        }

        // Восстанавливаем цвет по умолчанию
        untexturedBuilder.color(255, 255, 255, 255);

        // Рендерим лазерную вспышку
        if (stack.getItem() instanceof GunItem gun) {
            poseStack.pushPose();
            poseStack.translate(0, 0.75f, 4);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
            poseStack.scale(0.03125f, 0.03125f, 0.03125f);
            renderLaserFlash(poseStack, gun.lastShot[0], 75, 7.5, 0xFFFFFF);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void setVertexColor(VertexConsumer builder, int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        builder.color(r, g, b, 1.0f);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        poseStack.translate(0, 0.25, 3);
        double scale = 1.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.125D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(5, 2, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -3.75D; // -15D * 0.25
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -0.5f, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        renderColoredParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderColoredParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderColoredParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга цветных частей NI4NI
     */
    private void renderColoredParts(PoseStack poseStack, ItemStack stack,
                                    MultiBufferSource buffer, int packedLight,
                                    int packedOverlay) {

        int[] colors = ItemGunNI4NI.getColors(stack);
        int dark = colors != null ? colors[0] : 0xffffff;
        int light = colors != null ? colors[1] : 0xffffff;
        int grip = colors != null ? colors[2] : 0xffffff;

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Светлые части
        setVertexColor(builder, light);
        getWeaponModel().renderPart(poseStack, builder, "FrameLight", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Cylinder", packedLight, packedOverlay);

        // Ручка
        setVertexColor(builder, grip);
        getWeaponModel().renderPart(poseStack, builder, "Grip", packedLight, packedOverlay);

        // Темные части
        setVertexColor(builder, dark);
        getWeaponModel().renderPart(poseStack, builder, "FrameDark", packedLight, packedOverlay);

        // Яркие части (fullbright)
        VertexConsumer fullbrightBuilder = buffer.getBuffer(RenderType.entityTranslucentEmissive(getWeaponTexture(stack)));
        fullbrightBuilder.color(255, 255, 255, 255);
        getWeaponModel().renderPart(poseStack, fullbrightBuilder, "CylinderHighlights", 15728880, packedOverlay);
        getWeaponModel().renderPart(poseStack, fullbrightBuilder, "Barrel", 15728880, packedOverlay);

        // Монеты (зеленые, fullbright)
        VertexConsumer coinBuilder = buffer.getBuffer(RenderType.entityTranslucentEmissive(getWeaponTexture(stack)));
        coinBuilder.color(0, 255, 0, 255);
        getWeaponModel().renderPart(poseStack, coinBuilder, "Coin1", 15728880, packedOverlay);
        getWeaponModel().renderPart(poseStack, coinBuilder, "Coin2", 15728880, packedOverlay);
        getWeaponModel().renderPart(poseStack, coinBuilder, "Coin3", 15728880, packedOverlay);
        getWeaponModel().renderPart(poseStack, coinBuilder, "Coin4", 15728880, packedOverlay);

        // Восстанавливаем цвет по умолчанию
        coinBuilder.color(255, 255, 255, 255);
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);
        renderColoredParts(poseStack, stack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}