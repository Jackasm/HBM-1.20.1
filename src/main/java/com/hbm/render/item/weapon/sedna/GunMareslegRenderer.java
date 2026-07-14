package com.hbm.render.item.weapon.sedna;

import com.hbm.items.ModGunItems;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
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

import java.util.Arrays;
import java.util.List;

public class GunMareslegRenderer extends BaseGunRenderer {

    private ResourceLocation MARESLEG_TEX = null;
    private HFRWavefrontObject MARESLEG_MODEL = null;

    private final String texture;

    public GunMareslegRenderer(String texture) {
        this.texture = texture;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (MARESLEG_TEX == null) {
            MARESLEG_TEX = HBMResourceManager.maresleg_tex;
            if (texture.equals("maresleg_broken")) MARESLEG_TEX = HBMResourceManager.maresleg_broken_tex;
        }
        return MARESLEG_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (MARESLEG_MODEL == null) {
            MARESLEG_MODEL = HBMResourceManager.maresleg;
        }
        return MARESLEG_MODEL;
    }

    private boolean isShortened(ItemStack stack) {
        return stack.getItem() == ModGunItems.GUN_MARESLEG_BROKEN.get() ||
                WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SAWED_OFF);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        boolean shortened = isShortened(stack);

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] lever = HbmAnimations.getRelevantTransformation("LEVER", 0);
        double[] turn = HbmAnimations.getRelevantTransformation("TURN", 0);
        double[] flip = HbmAnimations.getRelevantTransformation("FLIP", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] shell = HbmAnimations.getRelevantTransformation("SHELL", 0);
        double[] flag = HbmAnimations.getRelevantTransformation("FLAG", 0);

        // Применяем анимации отдачи
        if (recoil != null) {
            poseStack.translate(recoil[0] * 2, recoil[1], recoil[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(recoil[2] * 5)));
        }

        if (turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) turn[2]));
        }

        // Анимация подъема
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, 0, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 0, 4);
        }

        // Анимация экипировки
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 0, -4);
            poseStack.mulPose(Axis.XN.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 0, 4);
        }

        // Анимация переворота
        if (flip != null && flip.length >= 1) {
            poseStack.translate(0, 0, -2);
            poseStack.mulPose(Axis.XN.rotationDegrees((float) flip[0]));
            poseStack.translate(0, 0, 2);
        }

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Основные части оружия
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        if (!shortened) {
            getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }

        // Рычаг перезарядки
        poseStack.pushPose();
        if (lever != null && lever.length >= 1) {
            poseStack.translate(0, 0.125f, -2.875f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lever[0]));
            poseStack.translate(0, -0.125f, 2.875f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Lever", packedLight, packedOverlay);
        poseStack.popPose();

        // Гильза
        if (shell != null && (shell[0] != 0 || shell[1] != 0 || shell[2] != 0)) {
            poseStack.pushPose();
            poseStack.translate(shell[0], shell[1] - 0.75f, shell[2]);
            getWeaponModel().renderPart(poseStack, builder, "Shell", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Флаг (дополнительная гильза)
        if (flag != null && flag[0] != 0) {
            poseStack.pushPose();
            poseStack.translate(0, -0.5f, 0);
            getWeaponModel().renderPart(poseStack, builder, "Shell", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Рендерим дым
        poseStack.pushPose();
        poseStack.translate(0, 1, shortened ? 3.75f : 8);
        if (turn != null && turn.length >= 3) {
            poseStack.mulPose(Axis.ZN.rotationDegrees((float) turn[2]));
        }
        if (flip != null && flip.length >= 1) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) flip[0]));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.25, packedLight, packedOverlay);
        poseStack.popPose();

        // Вспышка выстрела
        poseStack.pushPose();
        poseStack.translate(0, 1, shortened ? 3.75f : 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        if (Minecraft.getInstance().player != null) {
            ItemStack mainHand = Minecraft.getInstance().player.getMainHandItem();
            if (mainHand.getItem() instanceof GunItem gunItem) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gunItem.shotRand)));
            }
        }

        renderMuzzleFlash(poseStack, gun.lastShot[0], 5.0, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8f;

        double startX = -1.25f * offset;
        double startY = -1f * offset;
        double startZ = 2f * offset;

        double aimX = 0.055;
        double aimY = -3.875f / 8.0f;
        double aimZ = 1;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.0f;
        float yOffset = 0.25F;
        float scale = 0.49f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 0, 2);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        boolean sawedOff = isShortened(stack);
        float scale;
        int offsetX, offsetY;
        if (sawedOff) {
            scale = 0.12f;
            offsetX = 7;
            offsetY = 1;
        } else {
            scale = 0.08f;
            offsetX = 9;
            offsetY = 3;
        }
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(offsetX, offsetY, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -8.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        boolean shortened = isShortened(stack);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell", "Stock", "Barrel");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
        if (!shortened) {
            getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        boolean shortened = isShortened(stack);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell", "Stock", "Barrel");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
        if (!shortened) {
            getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        boolean shortened = isShortened(stack);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell", "Stock", "Barrel");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
        if (!shortened) {
            getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }
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

    // Метод для стола модификаций
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