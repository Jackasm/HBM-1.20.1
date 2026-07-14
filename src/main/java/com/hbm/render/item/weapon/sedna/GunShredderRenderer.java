package com.hbm.render.item.weapon.sedna;

import com.hbm.items.ModGunItems;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;
import java.util.Objects;

public class GunShredderRenderer extends BaseGunRenderer {

    private ResourceLocation SHREDDER_TEX = null;
    private HFRWavefrontObject SHREDDER_MODEL = null;

    private final String texture;
    private static final String LABEL = "[> <]";

    public GunShredderRenderer(String texture) {
        this.texture = texture;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (SHREDDER_TEX == null) {
            SHREDDER_TEX = HBMResourceManager.shredder_tex;
            if (Objects.equals(texture, "autoshotgun_shredder"))
            {
                SHREDDER_TEX = HBMResourceManager.autoshotgun_shredder_tex;
            }
        }
        return SHREDDER_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (SHREDDER_MODEL == null) {
            SHREDDER_MODEL = HBMResourceManager.shredder;
        }
        return SHREDDER_MODEL;
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
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        Player player = Minecraft.getInstance().player;

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] speen = HbmAnimations.getRelevantTransformation("SPEEN", 0);
        double[] cycle = HbmAnimations.getRelevantTransformation("CYCLE", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Применяем анимации
        if (equip != null) {
            poseStack.translate(0, -2, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 2, 6);
        }

        if (lift != null) {
            poseStack.translate(0, 0, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 0, 4);
        }

        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, recoil[2]);
        }

        boolean sexy = stack.getItem() == ModGunItems.GUN_AUTOSHOTGUN_SEXY.get();
        boolean isAiming = false;

        if (stack.getItem() instanceof GunItem) {
            isAiming = GunItem.prevAimingProgress >= 1F && GunItem.aimingProgress >= 1F;
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим магазин с анимациями
        if (mag != null && mag.length >= 3 && speen != null && cycle != null) {
            poseStack.pushPose();
            poseStack.translate(mag[0], mag[1], mag[2]);
            poseStack.translate(0, -1, -0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) speen[0]));
            poseStack.translate(0, 1, 0.5f);
            getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);

            poseStack.translate(0, -1, -0.5f);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) cycle[2]));
            poseStack.translate(0, 1, 0.5f);
            getWeaponModel().renderPart(poseStack, builder, "Shells", packedLight, packedOverlay);

            poseStack.popPose();
        }

        // Рендерим дым от выстрела
        double smokeScale = 0.75;
        poseStack.pushPose();
        poseStack.translate(0, 1, 7.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.5, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 1, 7.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XP.rotationDegrees((float)(gun.shotRand * 90)));
        poseStack.scale(0.75f, 0.75f, 0.75f);
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5, partialTick);
        poseStack.popPose();

        // Рендерим текстовую метку если нужно
        if (sexy || isAiming) {
            renderLabel(poseStack, buffer, player, sexy, packedLight);
        }
    }

    private void renderLabel(PoseStack poseStack, MultiBufferSource buffer, Player player, boolean sexy, int packedLight) {
        poseStack.pushPose();

        Font font = Minecraft.getInstance().font;
        float scale = 0.04F;

        // Позиционирование как в оригинале
        poseStack.translate((font.width(LABEL) / 2.0F) * scale - 0.02, 3.14F, -1.75F);
        poseStack.scale(scale, -scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        // Цвет как в оригинале
        float variance = 0.9F + player.getRandom().nextFloat() * 0.1F;
        int color = new Color(sexy ? variance : 0F, sexy ? 0F : variance, 0F).getRGB();

        // Рендерим текст
        font.drawInBatch(LABEL, 0, 0, color, false,
                poseStack.last().pose(), buffer,
                Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -1.25F * offset;
        double startZ = 1.5F * offset;

        double aimX = 0.065;
        double aimY = -6.25 / 8D + 0.05;
        double aimZ = 0.5;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.3f;
        float yOffset = 0.25F;
        float scale = 0.29f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }



    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.07D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(11, 3, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -7.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 0, 1.5f);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
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
}