package com.hbm.render.item.weapon.sedna;

import com.hbm.items.ModGunItems;
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

public class GunLaserPistolRenderer extends BaseGunRenderer {

    private final String textureName;
    private ResourceLocation LASER_PISTOL_TEX = null;
    private HFRWavefrontObject LASER_PISTOL_MODEL = null;

    public GunLaserPistolRenderer(String textureName) {
        this.textureName = textureName;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (LASER_PISTOL_TEX == null) {
            if (Objects.equals(textureName, "pew_pew")) {
                LASER_PISTOL_TEX = HBMResourceManager.laser_pistol_pew_pew_tex;
            } else if (Objects.equals(textureName, "morning_glory")) {
                LASER_PISTOL_TEX = HBMResourceManager.laser_pistol_morning_glory_tex;
            } else {
                LASER_PISTOL_TEX = HBMResourceManager.laser_pistol_tex;
            }
        }
        return LASER_PISTOL_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (LASER_PISTOL_MODEL == null) {
            LASER_PISTOL_MODEL = HBMResourceManager.laser_pistol;
        }
        return LASER_PISTOL_MODEL;
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

        float offset = 0.8F;
        float aimingProgress = getAimingProgress(partialTick);

        // Стартовые позиции (без прицеливания)
        double startX = -1.75F * offset;
        double startY = -2F * offset;
        double startZ = 2.75F * offset;

        // Целевые позиции (с прицеливанием)
        double aimX = 0.6;
        double aimY = -5.7 / 8D;
        double aimZ = 1.25;

        // Интерполяция между стартовыми и целевыми позициями
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        // Смещение для левой/правой руки
        float xOffset = 0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 2.5f;
        }

        float yOffset = 0.25F;
        float scale = 0.375f;

        z = z - 0.1;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] latch = HbmAnimations.getRelevantTransformation("LATCH", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] jolt = HbmAnimations.getRelevantTransformation("JOLT", 0);
        double[] battery = HbmAnimations.getRelevantTransformation("BATTERY", 0);
        double[] swirl = HbmAnimations.getRelevantTransformation("SWIRL", 0);

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -1, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 1, 6);
        }

        // Применяем анимацию LIFT
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, 2, -2);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, -2, 2);
        }

        // Применяем анимацию SWIRL
        if (swirl != null && swirl.length >= 1) {
            poseStack.translate(0, -1, -1);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) swirl[0]));
            poseStack.translate(0, 1, 1);
        }

        // Применяем анимации RECOIL и JOLT
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        if (jolt != null && jolt.length >= 3) {
            poseStack.translate((float) jolt[0], (float) jolt[1], (float) jolt[2]);
        }

        // Рендерим основные части
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим конденсаторы если есть
        if (hasCapacitors(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Capacitors", packedLight, packedOverlay);
        }

        // Рендерим ленту если есть
        if (hasTape(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Tape", packedLight, packedOverlay);
        }

        // Рендерим защелку и батарею
        poseStack.pushPose();

        // Применяем анимацию LATCH
        if (latch != null && latch.length >= 3) {
            poseStack.translate(1.125f, 0, -1.9125f);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) latch[1]));
            poseStack.translate(-1.125f, 0, 1.9125f);
        }

        getWeaponModel().renderPart(poseStack, builder, "Latch", packedLight, packedOverlay);

        // Применяем анимацию BATTERY
        if (battery != null && battery.length >= 3) {
            poseStack.translate((float) battery[0], (float) battery[1], (float) battery[2]);
        }

        getWeaponModel().renderPart(poseStack, builder, "Battery", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим лазерную вспышку
        poseStack.pushPose();
        poseStack.translate(0, 2, 4.75f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        boolean hasEmerald = hasEmerald(stack);
        renderLaserFlash(poseStack, gun.lastShot[0], 150, 1.5D,
                hasEmerald ? 0x008000 : 0xff0000);

        poseStack.translate(0, 0, -0.25f);
        renderLaserFlash(poseStack, gun.lastShot[0], 150, 0.75D,
                hasEmerald ? 0x80ff00 : 0xff8000);

        poseStack.popPose();
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        double scale = 1.25D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0, -0.5, -1);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.1D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(6, 3, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -10D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -0.5, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга стандартных частей лазерного пистолета
     */
    private void renderStandardParts(PoseStack poseStack, ItemStack stack,
                                     MultiBufferSource buffer, int packedLight,
                                     int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Рендерим основные части
        List<String> parts = Arrays.asList("Gun", "Latch");

        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }

        // Рендерим конденсаторы если есть
        if (hasCapacitors(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Capacitors", packedLight, packedOverlay);
        }

        // Рендерим ленту если есть
        if (hasTape(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Tape", packedLight, packedOverlay);
        }
    }

    // Метод для рендеринга на столе модификаций
    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);
        renderStandardParts(poseStack, stack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    // Проверка наличия конденсаторов
    public boolean hasCapacitors(ItemStack stack) {
        return stack.getItem() == ModGunItems.GUN_LASER_PISTOL_PEW_PEW.get();
    }

    // Проверка наличия ленты
    public boolean hasTape(ItemStack stack) {
        return stack.getItem() == ModGunItems.GUN_LASER_PISTOL_PEW_PEW.get();
    }

    // Проверка наличия изумруда (зеленый лазер)
    public boolean hasEmerald(ItemStack stack) {
        return stack.getItem() == ModGunItems.GUN_LASER_PISTOL_MORNING_GLORY.get();
    }

}