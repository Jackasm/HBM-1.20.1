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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;

public class GunQuadroRenderer extends BaseGunRenderer {

    private ResourceLocation QUADRO_TEX = null;
    private ResourceLocation QUADRO_ROCKET_TEX = null;
    private HFRWavefrontObject QUADRO_MODEL = null;

    private static final String LABEL = ">> <<";

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (QUADRO_TEX == null) {
            QUADRO_TEX = HBMResourceManager.quadro_tex;
        }
        return QUADRO_TEX;
    }

    private ResourceLocation getRocketTexture() {
        if (QUADRO_ROCKET_TEX == null) {
            QUADRO_ROCKET_TEX = HBMResourceManager.quadro_rocket_tex;
        }
        return QUADRO_ROCKET_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (QUADRO_MODEL == null) {
            QUADRO_MODEL = HBMResourceManager.quadro;
        }
        return QUADRO_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {


        float offset = 0.8F;
        float aimingProgress = getAimingProgress(partialTick);

        double startX = -2.5F * offset;
        double startY = -3.5F * offset;
        double startZ = 2.5F * offset;

        double aimX = -1.5F * offset;
        double aimY = -3F * offset;
        double aimZ = 2.5F * offset;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? 1.5f : 0.0f;
        float yOffset = 0.4F;
        float scale = 1.75f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        Player player = Minecraft.getInstance().player;
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] reloadPush = HbmAnimations.getRelevantTransformation("RELOAD_PUSH", 0);
        double[] reloadRotate = HbmAnimations.getRelevantTransformation("RELOAD_ROTATE", 0);

        // Базовая анимация EQUIP
        poseStack.translate(0, -1, -1);
        if (equip != null) poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
        poseStack.translate(0, 1, 1);

        // Анимация отдачи
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, recoil[2]);
        }

        // Анимация перезарядки вращение
        poseStack.translate(0, -1, -1);
        if (reloadRotate != null && reloadRotate.length >= 3) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) reloadRotate[2]));
        }
        poseStack.translate(0, 1, 1);

        poseStack.pushPose();
        {
            // Рендерим пусковую установку
            getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);

            // Рендерим ракеты с отдельной текстурой и анимациями
            poseStack.pushPose();
            {
                // Применяем анимации для ракет
                poseStack.translate(0, -1, 0);
                poseStack.translate(0, 3, 0);
                if (reloadPush != null && reloadPush.length >= 2) {
                    poseStack.mulPose(Axis.XP.rotationDegrees((float) (reloadPush[1] * 30)));
                }
                poseStack.translate(0, -3, 0);
                if (reloadPush != null) {
                    poseStack.translate(0, 0, reloadPush[0] * 3);
                }

                VertexConsumer rocketBuilder = buffer.getBuffer(RenderType.entityCutout(getRocketTexture()));
                getWeaponModel().renderPart(poseStack, rocketBuilder, "Rockets", packedLight, packedOverlay);
            }
            poseStack.popPose();
        }
        poseStack.popPose();

        // Текст при прицеливании
        if (getAimingProgress(partialTick) >= 1.0F) {
            renderRotatingLabel(poseStack, buffer, player, packedLight);
        }

        // Вспышка выстрела
        poseStack.pushPose();
        {
            poseStack.translate(-1, 0.75, 6.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (90 * gun.shotRand)));
            poseStack.scale(0.75f, 0.75f, 0.75f);
            renderMuzzleFlash(poseStack, gun.lastShot[0], 15, partialTick);
        }
        poseStack.popPose();
    }

    private void renderRotatingLabel(PoseStack poseStack, MultiBufferSource buffer, Player player, int packedLight) {
        poseStack.pushPose();

        // Временно используем максимальный свет для текста
        int lightmap = 0xF000F0;

        poseStack.translate(-0.375F, 2.25F, 0.875F);

        // Вращающийся текст
        float rotation = (float) ((System.currentTimeMillis() / 2) % 360);
        poseStack.mulPose(Axis.YN.rotationDegrees(rotation + 180));

        float scale = 0.04f;
        float textWidth = Minecraft.getInstance().font.width(LABEL) * scale;
        poseStack.translate(-textWidth / 2, 0, 0);
        poseStack.scale(scale, -scale, scale);

        int color = new Color(0F, 1F, 1F).getRGB();

        Minecraft.getInstance().font.drawInBatch(
                LABEL,
                0, 0,
                color,
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
        double scale = 7.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, -0.5, -0.25);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.25D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(3, 0.5, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -30D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -1.125, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);
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
        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);

        poseStack.popPose();
    }
}