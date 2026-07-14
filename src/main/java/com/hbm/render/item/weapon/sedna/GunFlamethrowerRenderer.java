package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mags.IMagazine;
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

public class GunFlamethrowerRenderer extends BaseGunRenderer {

    private ResourceLocation FLAMETHROWER_TEX = null;
    private HFRWavefrontObject FLAMETHROWER_MODEL = null;
    private final String texture;

    public GunFlamethrowerRenderer(String texture) {
        this.texture = texture;
    }

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (FLAMETHROWER_TEX == null) {
            if (texture.equals("flamethrower_topaz")) {
                FLAMETHROWER_TEX = HBMResourceManager.flamethrower_topaz_tex;
            } else if (texture.equals("flamethrower_daybreaker")) {
                FLAMETHROWER_TEX = HBMResourceManager.flamethrower_daybreaker_tex;
            } else FLAMETHROWER_TEX = HBMResourceManager.flamethrower_tex;
        }
        return FLAMETHROWER_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (FLAMETHROWER_MODEL == null) {
            FLAMETHROWER_MODEL = HBMResourceManager.flamethrower;
        }
        return FLAMETHROWER_MODEL;
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
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] rotate = HbmAnimations.getRelevantTransformation("ROTATE", 0);
        double[] gunAnim = HbmAnimations.getRelevantTransformation("Gun", 0);
        double[] tankAnim = HbmAnimations.getRelevantTransformation("Tank", 0);
        double[] gaugeAnim = HbmAnimations.getRelevantTransformation("Gauge", 0);

        // Применяем анимацию экипировки
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 2, -6);
            poseStack.mulPose(Axis.XN.rotationDegrees((float) equip[0]));
            poseStack.translate(0, -2, 6);
        }

        // Применяем анимацию поворота
        if (rotate != null && rotate.length >= 3) {
            poseStack.translate(0, 1, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotate[2]));
            poseStack.translate(0, -1, 0);
        }

        // Рендерим основное тело огнемета
        poseStack.pushPose();
        if (gunAnim != null && gunAnim.length >= 6) {
            poseStack.translate(gunAnim[0], gunAnim[1], gunAnim[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) gunAnim[3]));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) gunAnim[4]));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) gunAnim[5]));
        }
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Рендерим тепловой щит, если есть
        if (hasShield(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "HeatShield", packedLight, packedOverlay);
        }
        poseStack.popPose();

        // Рендерим бак
        poseStack.pushPose();
        if (tankAnim != null && tankAnim.length >= 6) {
            poseStack.translate(tankAnim[0], tankAnim[1], tankAnim[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) tankAnim[3]));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) tankAnim[4]));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) tankAnim[5]));
        }
        getWeaponModel().renderPart(poseStack, builder, "Tank", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим манометр с анимацией
        poseStack.pushPose();
        if (gaugeAnim != null && gaugeAnim.length >= 6) {
            poseStack.translate(gaugeAnim[0], gaugeAnim[1], gaugeAnim[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) gaugeAnim[3]));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) gaugeAnim[4]));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) gaugeAnim[5]));
        }

        // Анимация стрелки манометра на основе уровня топлива
        poseStack.translate(1.25f, 1.25f, 0);
        IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
        if (Minecraft.getInstance().player != null) {
            float fuelLevel = mag.getAmount(stack, Minecraft.getInstance().player.getInventory());
            float capacity = mag.getCapacity(stack);
            float rotation = -135 + (fuelLevel * 270f / capacity);
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        }
        poseStack.translate(-1.25f, -1.25f, 0);

        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -1.5F * offset;
        double startY = -1.5F * offset;
        double startZ = 2.75F * offset;

        double aimX = 0;
        double aimY = -4.625 / 8D;
        double aimZ = 0.25;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.875f; // Из исходного кода: GL11.glTranslated(0, 0, 0.875)
        float scale = 0.375f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, -3f, 4);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.08D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(10f, 4f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -7.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, 0, 1);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Tank", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);

        if (hasShield(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "HeatShield", packedLight, packedOverlay);
        }
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Tank", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);

        if (hasShield(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "HeatShield", packedLight, packedOverlay);
        }
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Tank", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);

        if (hasShield(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "HeatShield", packedLight, packedOverlay);
        }
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Tank", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);

        if (hasShield(stack)) {
            getWeaponModel().renderPart(poseStack, builder, "HeatShield", packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private boolean hasShield(ItemStack stack) {
        // Проверяем, является ли это огнеметом Daybreaker по текстуре
        // (Предполагается, что вы будете использовать разные текстуры для разных типов огнеметов)
        ResourceLocation texture = getWeaponTexture();
        String texturePath = texture.toString();

        // Проверяем по пути текстуры
        return texturePath.contains("daybreaker") ||
                texturePath.contains("flamethrower_daybreaker") ||
                // Или можете добавить другую логику определения
                (stack.getItem() instanceof GunItem &&
                        stack.getDisplayName().getString().toLowerCase().contains("daybreaker"));
    }
}