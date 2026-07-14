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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GunChemthrowerRenderer extends BaseGunRenderer {

    private ResourceLocation CHEMTHROWER_TEX = null;
    private HFRWavefrontObject CHEMTHROWER_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (CHEMTHROWER_TEX == null) {
            CHEMTHROWER_TEX = HBMResourceManager.chemthrower_tex;
        }
        return CHEMTHROWER_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (CHEMTHROWER_MODEL == null) {
            CHEMTHROWER_MODEL = HBMResourceManager.chemthrower;
        }
        return CHEMTHROWER_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
        }
        return 2.5F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = getAimingProgress(Minecraft.getInstance().getPartialTick());
        return fov * (1 - aimingProgress * 0.33F);
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] gauge = HbmAnimations.getRelevantTransformation("GAUGE", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        poseStack.pushPose();

        // Анимация EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, -2, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 2, 4);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        // Рендерим основные части
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Hose", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Nozzle", packedLight, packedOverlay);

        // Рендерим манометр с анимацией
        poseStack.pushPose();
        {
            poseStack.translate(0, 0.875, 1.75);

            // Получаем количество жидкости в магазине
            IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
            Player player = Minecraft.getInstance().player;
            int amount = 0;
            if (player != null) {
                amount = mag.getAmount(stack, player.getInventory());
            }
            int capacity = mag.getCapacity(stack);
            double d = capacity > 0 ? (double) amount / (double) capacity : 0;

            // Анимация стрелки манометра
            float gaugeRotation = (float) (135 - d * 270);
            if (gauge != null && gauge.length >= 1) {
                gaugeRotation += (float) gauge[0];
            }

            poseStack.mulPose(Axis.XP.rotationDegrees(gaugeRotation));
            poseStack.translate(0, -0.875, -1.75);

            getWeaponModel().renderPart(poseStack, builder, "Gauge", packedLight, packedOverlay);
        }
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;

        double startX = -2.5F * offset;
        double startY = -2.5F * offset;
        double startZ = 2.5F * offset;

        double aimX = 0.0;
        double aimY = -4.375 / 8D;
        double aimZ = 1.0;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? 3.5f : -0.5f;
        float yOffset = 0.25F;
        float scale = 0.49f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }



    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 2D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(0, -2.5, 0.5);
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
        poseStack.translate(0, -0.5, -0.5);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        // Для GUI рендерим все части
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        // Для инвентаря/земли рендерим все части
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        // Для третьего лица рендерим все части
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    // Метод для рендеринга на столе модификаций
    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);

        poseStack.popPose();
    }

}