package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
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

public class GunMAS36Renderer extends BaseGunRenderer {

    private ResourceLocation MAS36_TEX = null;
    private HFRWavefrontObject MAS36_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (MAS36_TEX == null) {
            MAS36_TEX = HBMResourceManager.mas36_tex;
        }
        return MAS36_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (MAS36_MODEL == null) {
            MAS36_MODEL = HBMResourceManager.mas36;
        }
        return MAS36_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        boolean scoped = isScoped(stack);
        return fov * (1 - aimingProgress * (scoped ? 0.66F : 0.33F));
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        boolean scoped = isScoped(stack);

        // Проверяем, полностью ли прицелились для режима снайперского прицела
        if (scoped && getAimingProgress(0) >= 1.0f) {
            return; // Не рендерим оружие при полностью включенном прицеле
        }

        // Получаем анимации как в оригинале
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] stock = HbmAnimations.getRelevantTransformation("STOCK", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] boltTurn = HbmAnimations.getRelevantTransformation("BOLT_TURN", 0);
        double[] boltPull = HbmAnimations.getRelevantTransformation("BOLT_PULL", 0);
        double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", 0);
        double[] showClip = HbmAnimations.getRelevantTransformation("SHOW_CLIP", 0);
        double[] clip = HbmAnimations.getRelevantTransformation("CLIP", 0);
        double[] bullets = HbmAnimations.getRelevantTransformation("BULLETS", 0);
        double[] stab = HbmAnimations.getRelevantTransformation("STAB", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Применяем анимацию EQUIP и LIFT
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -3, -3);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            if (lift != null && lift.length >= 1) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            }
            poseStack.translate(0, 3, 3);
        }

        // Применяем анимацию STAB
        if (stab != null && stab.length >= 3) {
            poseStack.translate((float) stab[0], (float) stab[1], (float) stab[2]);
        }

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим штык если есть
        if (hasBayonet(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Bayonet", packedLight, packedOverlay);
        }

        // Рендерим приклад с анимацией STOCK
        poseStack.pushPose();
        if (stock != null && stock.length >= 1) {
            poseStack.translate(0, 0.3125f, -2.125f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) stock[0]));
            poseStack.translate(0, -0.3125f, 2.125f);
        }
        getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим затвор с анимациями BOLT_TURN и BOLT_PULL
        poseStack.pushPose();
        if (boltTurn != null && boltTurn.length >= 3) {
            poseStack.translate(0, 0.0625f * 18.5f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) boltTurn[2]));
            poseStack.translate(0, -0.0625f * 18.5f, 0);
        }
        if (boltPull != null && boltPull.length >= 3) {
            poseStack.translate(0, 0, (float) boltPull[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Bolt", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим патрон
        poseStack.pushPose();
        if (bullet != null && bullet.length >= 3) {
            poseStack.translate((float) bullet[0], (float) bullet[1], (float) bullet[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим прицел если есть
        if (scoped) {
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
        }

        // Рендерим обойму и патроны если показывается
        if (showClip != null && showClip.length >= 1 && showClip[0] != 0) {
            // Обойма
            poseStack.pushPose();
            if (clip != null && clip.length >= 3) {
                poseStack.translate((float) clip[0], (float) clip[1], (float) clip[2]);
            }
            getWeaponModel().renderPart(poseStack, builder, "Clip", packedLight, packedOverlay);
            poseStack.popPose();

            // Патроны в обойме (с отсечением как в оригинале)
            poseStack.pushPose();
            if (bullets != null && bullets.length >= 3) {
                poseStack.translate((float) bullets[0], (float) bullets[1], (float) bullets[2]);

                // В оригинале используется GL_CLIP_PLANE для отсечения части модели
                // В 1.20.1 нет прямой замены, рендерим всю модель
                getWeaponModel().renderPart(poseStack, builder, "Bullets", packedLight, packedOverlay);
            }
            poseStack.popPose();
        }

        // Рендерим дымовые узлы
        double smokeScale = 0.25;
        poseStack.pushPose();
        poseStack.translate(0, 1.125f, 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale((float) smokeScale, (float) smokeScale, (float) smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 1.0D, packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 1, 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        if (gun.shotRand != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        }
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;
        boolean scoped = isScoped(stack);

        // Определяем целевые позиции в зависимости от наличия прицела
        double startX = -1.5F * offset;
        double startY = -1.25F * offset;
        double startZ = 1.75F * offset;

        double aimX, aimY, aimZ;
        if (scoped) {
            // С прицелом
            aimX = -0.2;
            aimY = -5.875 / 8D;
            aimZ = 1.125;
        } else {
            // Без прицела
            aimX = 0.06;
            aimY = -3.45 / 8D;
            aimZ = 0.75;
        }

        // Интерполяция
        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        // Смещение для левой/правой руки
        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.5f;
        }

        float yOffset = 0.25F;
        float scale = 0.375f; // Масштаб как в оригинале

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);

        // Масштаб и смещение как в оригинале
        double scale = 2;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0, 0.5f, 1.5);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        // Реализация для GUI (инвентаря)
        double scale = 0.08D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(9f, 2f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        // Для стола модификаций
        double scale = -7.5D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -0.25f, -2.5f);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        renderAllParts(poseStack, builder, stack, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        renderAllParts(poseStack, builder, stack, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        renderAllParts(poseStack, builder, stack, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга всех частей MAS36
     * Используется во всех режимах кроме first-person
     */
    private void renderAllParts(PoseStack poseStack, VertexConsumer builder,
                                ItemStack stack, int packedLight, int packedOverlay) {
        List<String> baseParts = Arrays.asList("Gun", "Stock", "Bolt");

        // Основные части
        for (String part : baseParts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }

        // Дополнительные части в зависимости от модификаций
        if (isScoped(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
        }

        if (hasBayonet(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Bayonet", packedLight, packedOverlay);
        }
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Рендерим все основные части
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Bolt", packedLight, packedOverlay);

        // Рендерим дополнительные части
        if (isScoped(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
        }

        if (hasBayonet(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Bayonet", packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    /**
     * Проверка наличия прицела (из оригинала)
     */
    public boolean isScoped(ItemStack stack) {
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SCOPE);
    }

    /**
     * Проверка наличия штыка (из оригинала)
     */
    public boolean hasBayonet(ItemStack stack) {
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.MAS_BAYONET);
    }

}