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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GunAmatRenderer extends BaseGunRenderer {

    private final String textureName;
    private ResourceLocation AMAT_TEX = null;
    private ResourceLocation ATTACHMENTS_TEX = null;
    private HFRWavefrontObject AMAT_MODEL = null;
    private HFRWavefrontObject G3_MODEL = null;

    public GunAmatRenderer(String textureName) {
        this.textureName = textureName;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        return getWeaponTexture(null);
    }

    protected ResourceLocation getWeaponTexture(ItemStack stack) {
        if (AMAT_TEX == null) {
            // Определяем текстуру в зависимости от имени
            if (Objects.equals(textureName, "amat_subtlety")) {
                AMAT_TEX = HBMResourceManager.amat_subtlety_tex;
            } else if (Objects.equals(textureName, "amat_penance")) {
                AMAT_TEX = HBMResourceManager.amat_penance_tex;
            } else {
                AMAT_TEX = HBMResourceManager.amat_tex;
            }
        }
        return AMAT_TEX;
    }

    protected ResourceLocation getAttachmentsTexture() {
        if (ATTACHMENTS_TEX == null) {
            ATTACHMENTS_TEX = HBMResourceManager.g3_attachments_tex;
        }
        return ATTACHMENTS_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (AMAT_MODEL == null) {
            AMAT_MODEL = HBMResourceManager.amat;
        }
        return AMAT_MODEL;
    }

    protected HFRWavefrontObject getG3Model() {
        if (G3_MODEL == null) {
            G3_MODEL = HBMResourceManager.g3;
        }
        return G3_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
        }
        return -0.5F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(0);
        boolean isScoped = isScoped(stack);
        return fov * (1 - aimingProgress * (isScoped ? 0.8F : 0.33F));
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        boolean isScoped = this.isScoped(stack);
        float aimingProgress = getAimingProgress(partialTick);

        // Если полностью прицелились с прицелом, не рендерим оружие
        if (isScoped && aimingProgress == 1.0f) {
            return;
        }

        GunItem gun = (GunItem) stack.getItem();
        boolean isSilenced = isSilenced(stack);

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] bipod = HbmAnimations.getRelevantTransformation("BIPOD", 0);
        double[] lift = HbmAnimations.getRelevantTransformation("LIFT", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] boltTurn = HbmAnimations.getRelevantTransformation("BOLT_TURN", 0);
        double[] boltPull = HbmAnimations.getRelevantTransformation("BOLT_PULL", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] scopeThrow = HbmAnimations.getRelevantTransformation("SCOPE_THROW", 0);
        double[] scopeSpin = HbmAnimations.getRelevantTransformation("SCOPE_SPIN", 0);

        // Проверяем состояние сошек
        boolean deployed = HbmAnimations.getRelevantAnim(0) == null ||
                Objects.requireNonNull(HbmAnimations.getRelevantAnim(0)).animation.getBus("BIPOD") == null;

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));

        // Применяем анимацию RECOIL
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, (float) recoil[2]);
        }

        // Применяем анимации EQUIP и LIFT
        if (equip != null && equip.length >= 1 || lift != null && lift.length >= 1) {
            poseStack.translate(0, -3, -8);
            if (equip != null && equip.length >= 1) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            }
            if (lift != null && lift.length >= 1) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) lift[0]));
            }
            poseStack.translate(0, 3, 8);
        }

        // Рендерим основное тело
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим прицел если есть
        if (isScoped) {
            poseStack.pushPose();
            if (scopeThrow != null && scopeThrow.length >= 3) {
                poseStack.translate((float) scopeThrow[0], (float) scopeThrow[1], (float) scopeThrow[2]);
            }
            poseStack.translate(0, 1.5f, -4.5f);
            if (scopeSpin != null && scopeSpin.length >= 1) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) scopeSpin[0]));
            }
            poseStack.translate(0, -1.5f, 4.5f);
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Рендерим затвор
        poseStack.pushPose();
        poseStack.translate(0, 0.625f, 0);
        if (boltTurn != null && boltTurn.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) boltTurn[2]));
        }
        poseStack.translate(0, -0.625f, 0);
        if (boltPull != null && boltPull.length >= 3) {
            poseStack.translate(0, 0, (float) boltPull[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Bolt", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим магазин
        poseStack.pushPose();
        if (mag != null && mag.length >= 3) {
            poseStack.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим левую сошку
        poseStack.pushPose();
        poseStack.translate(0.3125f, -0.625f, -1);
        float hingeLeftRotation = deployed ? 25 : (bipod != null && bipod.length >= 2 ? (float) bipod[1] : 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(hingeLeftRotation));
        poseStack.translate(-0.3125f, 0.625f, 1);
        getWeaponModel().renderPart(poseStack, builder, "BipodHingeLeft", packedLight, packedOverlay);

        poseStack.translate(0.3125f, -0.625f, -1);
        float bipodLeftRotation = deployed ? 80 : (bipod != null && bipod.length >= 1 ? (float) bipod[0] : 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(bipodLeftRotation));
        poseStack.translate(-0.3125f, 0.625f, 1);
        getWeaponModel().renderPart(poseStack, builder, "BipodLeft", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим правую сошку
        poseStack.pushPose();
        poseStack.translate(-0.3125f, -0.625f, -1);
        float hingeRightRotation = deployed ? -25 : (bipod != null && bipod.length >= 2 ? -(float) bipod[1] : 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(hingeRightRotation));
        poseStack.translate(0.3125f, 0.625f, 1);
        getWeaponModel().renderPart(poseStack, builder, "BipodHingeRight", packedLight, packedOverlay);

        poseStack.translate(-0.3125f, -0.625f, -1);
        float bipodRightRotation = deployed ? 80 : (bipod != null && bipod.length >= 1 ? (float) bipod[0] : 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(bipodRightRotation));
        poseStack.translate(0.3125f, 0.625f, 1);
        getWeaponModel().renderPart(poseStack, builder, "BipodRight", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим глушитель или дульный тормоз
        if (isSilenced) {
            poseStack.pushPose();
            poseStack.translate(0, 0.625f, -4.3125f);
            poseStack.scale(1.25f, 1.25f, 1.25f);
            VertexConsumer attachmentsBuilder = buffer.getBuffer(RenderType.entityCutout(getAttachmentsTexture()));
            getG3Model().renderPart(poseStack, attachmentsBuilder, "Silencer", packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            getWeaponModel().renderPart(poseStack, builder, "MuzzleBrake", packedLight, packedOverlay);

            // Рендерим дымовые узлы
            double smokeScale = 0.5;
            poseStack.pushPose();
            poseStack.translate(0, 0.625f, 12);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale((float)smokeScale, (float)smokeScale, (float)smokeScale);
            renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 1.0D, packedLight, packedOverlay);
            poseStack.popPose();

            // Рендерим вспышку выстрела
            poseStack.pushPose();
            poseStack.translate(0, 0.5f, 11);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(0.75f, 0.75f, 0.75f);
            renderGapFlash(poseStack, gun.lastShot[0], partialTick);
            poseStack.popPose();
        }
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1F * offset;
        double startY = -1F * offset;
        double startZ = 3.25F * offset;

        double aimX = 0;
        double aimY = -4.875 / 8D;
        double aimZ = 1.875;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = 1f;
        }
        float yOffset = 0.35F;
        float scale = 0.29f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.25D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, 0.5f, 4.5f);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        boolean isSilenced = isSilenced(stack);

        if (isSilenced) {
            double scale = 0.045D;
            poseStack.scale((float)scale, (float)scale, (float)scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.translate(15f, 4f, -1);
        } else {
            double scale = 0.05D;
            poseStack.scale((float)scale, (float)scale, (float)scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.translate(15f, 4f, 0);
        }
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -5.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -0.25f, -1.5f);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture(stack)));
        renderStandardParts(poseStack, builder, stack, buffer, packedLight, packedOverlay);
    }

    /**
     * Метод для рендеринга стандартных частей AMAT
     */
    private void renderStandardParts(PoseStack poseStack, VertexConsumer builder,
                                     ItemStack stack, MultiBufferSource buffer,
                                     int packedLight, int packedOverlay) {

        List<String> parts = Arrays.asList(
                "Gun",
                "Bolt",
                "Magazine",
                "BipodLeft",
                "BipodHingeLeft",
                "BipodRight",
                "BipodHingeRight"
        );

        // Рендерим все стандартные части
        for (String part : parts) {
            getWeaponModel().renderPart(poseStack, builder, part, packedLight, packedOverlay);
        }

        // Рендерим прицел если есть
        if (isScoped(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "Scope", packedLight, packedOverlay);
        }

        // Рендерим глушитель или дульный тормоз
        boolean isSilenced = isSilenced(stack);
        if (isSilenced) {
            poseStack.pushPose();
            poseStack.translate(0, 0.625f, -4.3125f);
            poseStack.scale(1.25f, 1.25f, 1.25f);
            VertexConsumer attachmentsBuilder = buffer.getBuffer(RenderType.entityCutout(getAttachmentsTexture()));
            getG3Model().renderPart(poseStack, attachmentsBuilder, "Silencer", packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            getWeaponModel().renderPart(poseStack, builder, "MuzzleBrake", packedLight, packedOverlay);
        }
    }

    public boolean isScoped(ItemStack stack) {
        // AMAT всегда имеет прицел
        return true;
    }

    public boolean isSilenced(ItemStack stack) {
        // Проверяем, является ли это Penance версией или имеет глушитель
        return stack.getItem() == ModGunItems.GUN_AMAT_PENANCE.get() ||
                WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SILENCER);
    }
}