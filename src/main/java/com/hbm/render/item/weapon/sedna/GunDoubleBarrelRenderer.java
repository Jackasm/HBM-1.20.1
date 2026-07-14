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

public class GunDoubleBarrelRenderer extends BaseGunRenderer {

    private ResourceLocation DOUBLE_BARREL_TEX = null;
    private HFRWavefrontObject DOUBLE_BARREL_MODEL = null;
    private final String texture;

    public GunDoubleBarrelRenderer(String texture) {
        this.texture = texture;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (DOUBLE_BARREL_TEX == null) {
            DOUBLE_BARREL_TEX = HBMResourceManager.double_barrel_tex;
            if (texture.equals("sacred_dragon")) DOUBLE_BARREL_TEX = HBMResourceManager.sacred_dragon_tex;
        }
        return DOUBLE_BARREL_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (DOUBLE_BARREL_MODEL == null) {
            DOUBLE_BARREL_MODEL = HBMResourceManager.double_barrel;
        }
        return DOUBLE_BARREL_MODEL;
    }

    private boolean isSawedOff(ItemStack stack) {
        return stack.getItem() == ModGunItems.GUN_DOUBLE_BARREL_SACRED_DRAGON.get() ||
                WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SAWED_OFF);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        boolean sawedOff = isSawedOff(stack);

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] turn = HbmAnimations.getRelevantTransformation("TURN", 0);
        double[] barrel = HbmAnimations.getRelevantTransformation("BARREL", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] shells = HbmAnimations.getRelevantTransformation("SHELLS", 0);
        double[] shellFlip = HbmAnimations.getRelevantTransformation("SHELL_FLIP", 0);
        double[] lever = HbmAnimations.getRelevantTransformation("LEVER", 0);
        double[] buckle = HbmAnimations.getRelevantTransformation("BUCKLE", 0);
        double[] noAmmo = HbmAnimations.getRelevantTransformation("NO_AMMO", 0);

        // Применяем анимации отдачи
        if (recoil != null) {
            poseStack.translate(recoil[0] * 3, recoil[1], recoil[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(recoil[2] * 10)));
        }

        // Анимация экипировки
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 0, -4);
            poseStack.mulPose(Axis.XN.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 0, 4);
        }

        // Анимация поворота
        if (turn != null && turn.length >= 2) {
            poseStack.translate(0, 0, -4);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) turn[1]));
            poseStack.translate(0, 0, 4);
        }

        // Анимация подъема
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, 0, -4);
            poseStack.mulPose(Axis.XN.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 0, 4);
        }

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Рендерим приклад
        getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);

        // Рендерим стволы
        poseStack.pushPose();

        // Анимация откидывания стволов
        if (barrel != null && barrel.length >= 1) {
            poseStack.translate(0, -0.4375, -0.875);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) barrel[0]));
            poseStack.translate(0, 0.4375, 0.875);
        }

        // Всегда рендерим короткий ствол
        getWeaponModel().renderPart(poseStack, builder, "BarrelShort", packedLight, packedOverlay);

        // Рендерим длинный ствол, если не обрезанный
        if (!sawedOff) {
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }

        // Рендерим застежку
        poseStack.pushPose();
        if (buckle != null && buckle.length >= 2) {
            poseStack.translate(0.75, 0, -0.6875);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) buckle[1]));
            poseStack.translate(-0.75, 0, 0.6875);
        }
        getWeaponModel().renderPart(poseStack, builder, "Buckle", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим рычаг
        poseStack.pushPose();
        if (lever != null && lever.length >= 3) {
            poseStack.translate(-0.3125, 0.3125, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) lever[2]));
            poseStack.translate(0.3125, -0.3125, 0);
        }
        getWeaponModel().renderPart(poseStack, builder, "Lever", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим гильзы, если есть патроны
        if (noAmmo == null || noAmmo[0] == 0) {
            poseStack.pushPose();
            if (shells != null && shells.length >= 3) {
                poseStack.translate(shells[0], shells[1], shells[2]);
            }
            if (shellFlip != null && shellFlip.length >= 1) {
                poseStack.translate(0, 0, -1);
                poseStack.mulPose(Axis.XP.rotationDegrees((float) shellFlip[0]));
                poseStack.translate(0, 0, 1);
            }
            getWeaponModel().renderPart(poseStack, builder, "Shells", packedLight, packedOverlay);
            poseStack.popPose();
        }

        poseStack.popPose();

        // Рендерим дым и вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 1, sawedOff ? 3.75f : 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        // Добавляем случайный поворот для вспышки
        if (Minecraft.getInstance().player != null) {
            ItemStack mainHand = Minecraft.getInstance().player.getMainHandItem();
            if (mainHand.getItem() instanceof GunItem gunItem) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gunItem.shotRand)));
            }
        }

        renderMuzzleFlash(poseStack, gun.lastShot[0], 5.0, partialTick);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.25, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        boolean sawedOff = isSawedOff(stack);
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8f;

        double startX = -1.25f * offset;
        double startY = -1f * offset;
        double startZ = 2f * offset;

        double aimX = 0.055;
        double aimY = -2.0 / 8.0;
        if (sawedOff) aimY += 0.14F;
        double aimZ = 1;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.0f;
        float yOffset = 0.35F;
        float scale = 0.39f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 1, 1);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        boolean sawedOff = isSawedOff(stack);
        float scale;
        int offsetX, offsetY;
        if (sawedOff) {
            scale = 0.09f;
            offsetX = 10;
            offsetY = 3;
        } else {
            scale = 0.08f;
            offsetX = 9;
            offsetY = 4;
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
        boolean sawedOff = isSawedOff(stack);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shells", "Barrel");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
        if (!sawedOff) {
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        boolean sawedOff = isSawedOff(stack);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shells", "Barrel");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
        if (!sawedOff) {
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        }
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        boolean sawedOff = isSawedOff(stack);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shells", "Barrel");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
        if (!sawedOff) {
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