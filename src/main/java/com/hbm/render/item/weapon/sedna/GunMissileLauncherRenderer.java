package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.main.HBMResourceManager;
import com.hbm.particle.SpentCasing;
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

public class GunMissileLauncherRenderer extends BaseGunRenderer {

    private ResourceLocation MISSILE_LAUNCHER_TEX = null;
    private HFRWavefrontObject MISSILE_LAUNCHER_MODEL = null;

    private static final String LABEL = "AUTO";

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (MISSILE_LAUNCHER_TEX == null) {
            MISSILE_LAUNCHER_TEX = HBMResourceManager.missile_launcher_tex;
        }
        return MISSILE_LAUNCHER_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (MISSILE_LAUNCHER_MODEL == null) {
            MISSILE_LAUNCHER_MODEL = HBMResourceManager.missile_launcher;
        }
        return MISSILE_LAUNCHER_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * 0.33F);
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        poseStack.translate(0, 0, 0.875);

        float offset = 0.8F;
        float aimingProgress = getAimingProgress(partialTick);

        double startX = -1.5F * offset;
        double startY = -1.25F * offset;
        double startZ = 0.5F * offset;

        double aimX = -1F * offset;
        double aimY = -1.25F * offset;
        double aimZ = 0F * offset;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? 1.5f : 0.0f;
        float yOffset = 0.4F;
        float scale = 0.5f;

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
        double[] barrel = HbmAnimations.getRelevantTransformation("BARREL", 0);
        double[] open = HbmAnimations.getRelevantTransformation("OPEN", 0);
        double[] missile = HbmAnimations.getRelevantTransformation("MISSILE", 0);

        // Базовая анимация EQUIP
        poseStack.translate(0, -2, -2);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) (equip != null ? equip[0] : 0)));
        poseStack.translate(0, 2, 2);

        poseStack.pushPose();
        {
            // Основная часть
            getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);

            // Передняя часть с анимацией OPEN
            poseStack.pushPose();
            {
                poseStack.translate(0, 0.25f, 1.6875f);
                poseStack.mulPose(Axis.XP.rotationDegrees((float) (open != null ? open[0] : 0)));
                poseStack.translate(0, -0.25f, -1.6875f);

                getWeaponModel().renderPart(poseStack, builder, "Front", packedLight, packedOverlay);

                // Ствол с анимацией BARREL
                poseStack.pushPose();
                {
                    poseStack.translate(0, 0, barrel != null ? barrel[2] : 0);
                    getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
                }
                poseStack.popPose();

                // Ракета с анимацией MISSILE
                poseStack.pushPose();
                {
                    if (missile != null && missile.length >= 3) {
                        poseStack.translate(missile[0], missile[1], missile[2]);
                    }

                    // Проверяем, есть ли патроны
                    GunConfig config = gun.getConfig(stack, 0);
                    if (config != null) {
                        IMagazine mag = config.getReceivers(stack)[0].getMagazine(stack);
                        if (mag != null && mag.getAmount(stack, player.getInventory()) > 0) {
                            getWeaponModel().renderPart(poseStack, builder, "Missile", packedLight, packedOverlay);
                        }
                    }
                }
                poseStack.popPose();
            }
            poseStack.popPose();
        }
        poseStack.popPose();

        // Текст "AUTO" при прицеливании
        if (getAimingProgress(partialTick) >= 1.0F) {
            renderAutoLabel(poseStack, buffer, player, packedLight);
        }

        // Вспышка выстрела
        poseStack.pushPose();
        {
            poseStack.translate(0, 1, 6.75);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (gun.shotRand * 90)));
            poseStack.scale(0.75f, 0.75f, 0.75f);
            renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5, partialTick);
        }
        poseStack.popPose();
    }

    private void renderAutoLabel(PoseStack poseStack, MultiBufferSource buffer, Player player, int packedLight) {
        poseStack.pushPose();

        // Сохраняем текущий lightmap и временно ставим максимальный свет
        int lightmap = 0xF000F0;

        poseStack.translate(0.9375f, 2.25f, -0.5625f);
        float textWidth = Minecraft.getInstance().font.width(LABEL) * 0.04f;
        poseStack.translate(textWidth / 2, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        float scale = 0.04f;
        poseStack.scale(scale, -scale, scale);

        float variance = 0.7F + player.getRandom().nextFloat() * 0.3F;
        int color = new Color(variance, 0F, 0F).getRGB();

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
        double scale = 2.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, -0.5, -2);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.075D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(8, 4, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -10D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -1, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        GunItem gun = (GunItem) stack.getItem();

        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Front", packedLight, packedOverlay);

        GunConfig config = gun.getConfig(stack, 0);
        if (config != null) {
            IMagazine mag = config.getReceivers(stack)[0].getMagazine(stack);
            if (mag != null && mag.getAmount(stack, null) > 0) {
                getWeaponModel().renderPart(poseStack, builder, "Missile", packedLight, packedOverlay);
            }
        }
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
        GunItem gun = (GunItem) stack.getItem();

        getWeaponModel().renderPart(poseStack, builder, "Launcher", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Front", packedLight, packedOverlay);

        GunConfig config = gun.getConfig(stack, 0);
        if (config != null) {
            IMagazine mag = config.getReceivers(stack)[0].getMagazine(stack);
            if (mag != null && mag.getAmount(stack, null) > 0) {
                getWeaponModel().renderPart(poseStack, builder, "Missile", packedLight, packedOverlay);
            }
        }

        poseStack.popPose();
    }
}