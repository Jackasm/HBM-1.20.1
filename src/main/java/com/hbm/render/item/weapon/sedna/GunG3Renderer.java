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
import java.util.Objects;

public class GunG3Renderer extends BaseGunRenderer {

    private final String textureVariant;
    private ResourceLocation G3_TEX = null;
    private HFRWavefrontObject G3_MODEL = null;
    private ResourceLocation G3_ATTACHMENTS_TEX = null;

    public GunG3Renderer(String textureVariant) {
        this.textureVariant = textureVariant;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (G3_TEX == null) {
            // Определяем текстуру на основе варианта
            if (Objects.equals(textureVariant, "zebra")) {
                G3_TEX = HBMResourceManager.g3_zebra_tex;
            } else {
                G3_TEX = HBMResourceManager.g3_tex;
            }
        }
        return G3_TEX;
    }

    private ResourceLocation getWeaponTexture(ItemStack stack) {
        if (WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.FURNITURE_GREEN)) {
            return HBMResourceManager.g3_green_tex;
        }
        if (WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.FURNITURE_BLACK)) {
            return HBMResourceManager.g3_black_tex;
        }
        return getWeaponTexture();
    }

    private ResourceLocation getG3AttachmentTexture() {
        if (G3_ATTACHMENTS_TEX == null) {
            G3_ATTACHMENTS_TEX = HBMResourceManager.g3_attachments_tex;
        }
       return G3_ATTACHMENTS_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (G3_MODEL == null) {
            G3_MODEL = HBMResourceManager.g3;
        }
        return G3_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
        }
        return -0.25F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        boolean isScoped = isScoped(stack);
        return fov * (1 - aimingProgress * (isScoped ? 0.66F : 0.33F));
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();
        boolean isScoped = isScoped(stack);

        // Если прицел включен и полностью наведен - не рендерим оружие
        if(isScoped && getAimingProgress(partialTick) >= 1.0F) {
            return;
        }

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] speen = HbmAnimations.getRelevantTransformation("SPEEN", 0);
        double[] bolt = HbmAnimations.getRelevantTransformation("BOLT", 0);
        double[] plug = HbmAnimations.getRelevantTransformation("PLUG", 0);
        double[] handle = HbmAnimations.getRelevantTransformation("HANDLE", 0);
        double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", 0);
        double[] selector = HbmAnimations.getRelevantTransformation("SELECTOR", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -2, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 2, 6);
        }

        // Применяем анимацию LIFT
        if (lift != null && lift.length >= 1) {
            poseStack.translate(0, 0, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            poseStack.translate(0, 0, 4);
        }

        // Применяем анимацию RECOIL (отдача)
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Рендерим основную часть винтовки
        getWeaponModel().renderPart(poseStack, builder, "Rifle", packedLight, packedOverlay);

        // Рендерим приклад, если он есть
        if(hasStock(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Stock", packedLight, packedOverlay);
        }

        // Рендерим пламегаситель, если нет глушителя
        boolean silenced = hasSilencer(stack);
        if(!silenced) {
            getWeaponModel().renderPart(poseStack, builder, "Flash_Hider", packedLight, packedOverlay);
        }

        getWeaponModel().renderPart(poseStack, builder, "Trigger", packedLight, packedOverlay);

        poseStack.pushPose();
        if (mag != null && mag.length >= 3) {
            poseStack.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        }

        poseStack.translate(0, -1.75f, -0.5f);

        if (speen != null && speen.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) speen[2]));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) speen[1]));
        }

        poseStack.translate(0, 1.75f, 0.5f);

        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);

        // Рендерим патрон в магазине
        if (bullet != null && bullet[0] == 0) {
            getWeaponModel().renderPart(poseStack, builder, "Bullet", packedLight, packedOverlay);
        }
        poseStack.popPose();

        // Рендерим затвор
        poseStack.pushPose();
        if (bolt != null && bolt.length >= 3) {
            poseStack.translate(0, 0, (float) bolt[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Guide_And_Bolt", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим пробку и рукоятку взведения
        poseStack.pushPose();
        if (plug != null && plug.length >= 3) {
            poseStack.translate(0, 0.625f, (float) plug[2]);
        }

        if (handle != null && handle.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) handle[2]));
        }

        poseStack.translate(0, -0.625f, 0);
        getWeaponModel().renderPart(poseStack, builder, "Plug", packedLight, packedOverlay);

        // Анимация рукоятки взведения
        poseStack.translate(0, 0.625f, 5.25f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(22.5f));

        if (handle != null && handle.length >= 3) {
            poseStack.mulPose(Axis.YP.rotationDegrees((float) handle[1]));
        }

        poseStack.mulPose(Axis.ZN.rotationDegrees(22.5f));
        poseStack.translate(0, -0.625f, -5.25f);
        getWeaponModel().renderPart(poseStack, builder, "Handle", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим переключатель режима огня
        poseStack.pushPose();
        poseStack.translate(0, -0.875f, -3.5f);

        // Получаем текущий режим
        int mode = GunItem.getMode(stack, 0);
        float selectorRotation = -30 * (1 - mode);

        if (selector != null && selector.length >= 1) {
            selectorRotation += (float) selector[0];
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(selectorRotation));
        poseStack.translate(0, 0.875f, 3.5f);
        getWeaponModel().renderPart(poseStack, builder, "Selector", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим прицелы и глушители (текстура attachments)
        if(silenced || isScoped) {
            builder = buffer.getBuffer(RenderType.entityCutout(getG3AttachmentTexture()));

            if(silenced) {
                getWeaponModel().renderPart(poseStack, builder, "Silencer", packedLight, packedOverlay);
            }
            if(isScoped) {
                getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
            }
        }

        // Рендерим вспышку выстрела и дым, если нет глушителя
        if(!silenced) {
            // Рендерим вспышку выстрела
            poseStack.pushPose();
            poseStack.translate(0, 0, 13);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(0.75f, 0.75f, 0.75f);
            renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5f, partialTick);
            poseStack.popPose();

            // Рендерим дымовые узлы
            poseStack.pushPose();
            poseStack.translate(0, 0, 12);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(0.5f, 0.5f, 0.5f);

            // Добавляем небольшую случайную ротацию для дыма
            if (gun.shotRand != 0) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-25 + (float)(gun.shotRand * 10)));
            }

            renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.75D, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        boolean isScoped = isScoped(stack);
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.25F * offset;
        double startY = -1.0F * offset;
        double startZ = 2.75F * offset;

        double aimX = 0.56;
        double aimY = isScoped ? (-5.53125 / 8D) : (-2.7 / 8D);
        double aimZ = isScoped ? 1.46875 : 1.5;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = -0.5f;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1.5f;
        }

        float yOffset = 0.3f;
        float scale = 0.375f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.0D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 2, 1);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        if(hasStock(stack)) {
            double scale = 0.05D;
            poseStack.scale((float)scale, (float)scale, (float)scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));

            boolean silenced = hasSilencer(stack);
            poseStack.mulPose(Axis.YP.rotationDegrees(silenced ? 40 : 45));
            poseStack.translate(silenced ? 12f : 15f, silenced ? 6f : 5f, 0);
        } else {
            double scale = 0.07D;
            poseStack.scale((float)scale, (float)scale, (float)scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));

            boolean silenced = hasSilencer(stack);
            poseStack.mulPose(Axis.YP.rotationDegrees(silenced ? 55 : 45));
            poseStack.translate(8f, 4f, 0);
        }
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 0.5f, -0.5f);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderG3Weapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        renderG3Weapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderG3Weapon(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    /**
     * Универсальный метод рендеринга G3 для всех режимов кроме первого лица
     */
    private void renderG3Weapon(ItemStack stack, PoseStack poseStack,
                                MultiBufferSource buffer, int packedLight,
                                int packedOverlay) {
        boolean silenced = hasSilencer(stack);
        boolean isScoped = isScoped(stack);

        // Основная текстура
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Рендерим основные части
        List<String> mainParts = Arrays.asList(
                "Rifle",
                "Stock",
                "Magazine",
                "Guide_And_Bolt",
                "Handle",
                "Trigger"
        );

        for (String part : mainParts) {
            // Пропускаем приклад, если его нет
            if (part.equals("Stock") && !hasStock(stack)) continue;
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }

        // Пламегаситель (если нет глушителя)
        if(!silenced) {
            getWeaponModel().renderPart(poseStack, builder, "Flash_Hider", packedLight, packedOverlay);
        }

        // Переключатель режима огня
        poseStack.pushPose();
        poseStack.translate(0, -0.875f, -3.5f);
        poseStack.mulPose(Axis.XP.rotationDegrees(-30)); // Режим auto
        poseStack.translate(0, 0.875f, 3.5f);
        getWeaponModel().renderPart(poseStack, builder, "Selector", packedLight, packedOverlay);
        poseStack.popPose();

        // Прицелы и глушители
        if(silenced || isScoped) {
            builder = buffer.getBuffer(RenderType.entityCutout(getG3AttachmentTexture()));

            if(silenced) {
                getWeaponModel().renderPart(poseStack, builder, "Silencer", packedLight, packedOverlay);
            }
            if(isScoped) {
                getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
            }
        }
    }

    /**
     * Проверяет, есть ли приклад
     */
    public boolean hasStock(ItemStack stack) {
        return !WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.NO_STOCK);
    }

    /**
     * Проверяет, есть ли глушитель
     */
    public boolean hasSilencer(ItemStack stack) {
        // Zebra версия имеет встроенный глушитель
        if (Objects.equals(textureVariant, "zebra")) {
            return true;
        }
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SILENCER);
    }

    /**
     * Проверяет, есть ли прицел
     */
    public boolean isScoped(ItemStack stack) {
        // Zebra версия имеет встроенный прицел
        if (Objects.equals(textureVariant, "zebra")) {
            return true;
        }
        return WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SCOPE);
    }
}