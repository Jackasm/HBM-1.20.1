package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GunSPAS12Renderer extends BaseGunRenderer {

    private ResourceLocation SPAS12_TEX = null;
    private ResourceLocation CASINGS_TEX = null;
    private ResourceLocation CASINGS_BASE_TEX = null;
    private HFRWavefrontObject SPAS12_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (SPAS12_TEX == null) {
            SPAS12_TEX = HBMResourceManager.spas_12_tex;
        }
        return SPAS12_TEX;
    }

    protected ResourceLocation getCasingsTexture() {
        if (CASINGS_TEX == null) {
            CASINGS_TEX = HBMResourceManager.casings_tex;
        }
        return CASINGS_TEX;
    }

    protected ResourceLocation getCasingsBaseTexture() {
        if (CASINGS_BASE_TEX == null) {
            CASINGS_BASE_TEX = HBMResourceManager.casings_base_tex;
        }
        return CASINGS_BASE_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (SPAS12_MODEL == null) {
            SPAS12_MODEL = HBMResourceManager.spas_12;
        }
        return SPAS12_MODEL;
    }

    @Override
    protected void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay, float partialTick) {

        GunItem gun = (GunItem) stack.getItem();

        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        HbmAnimations.applyRelevantTransformation(poseStack, "MainBody");
        getWeaponModel().renderPart(poseStack, builder, "MainBody", packedLight, packedOverlay);

        poseStack.pushPose();
        HbmAnimations.applyRelevantTransformation(poseStack, "PumpGrip");
        getWeaponModel().renderPart(poseStack, builder, "PumpGrip", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        HbmAnimations.applyRelevantTransformation(poseStack, "Shell");

        int color0 = SpentCasing.COLOR_CASE_BRASS;
        int color1 = SpentCasing.COLOR_CASE_BRASS;

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (gun.getConfig(stack, 0).getReceivers(stack) != null &&
                    gun.getConfig(stack, 0).getReceivers(stack).length > 0) {

                SpentCasing casing = Objects.requireNonNull(gun.getConfig(stack, 0).getReceivers(stack)[0]
                        .getMagazine(stack)).getCasing(stack, player.getInventory());

                if (casing != null) {
                    int[] colors = casing.getColors();
                    color0 = colors[0];
                    color1 = colors.length > 1 ? colors[1] : color0;
                }
            }
        }

        VertexConsumer casingBuilder1 = buffer.getBuffer(RenderType.entityCutout(getCasingsTexture()));
        VertexConsumer casingBuilder0 = buffer.getBuffer(RenderType.entityCutout(getCasingsBaseTexture()));

        float shellR0 = (color0 >> 16 & 255) / 255.0F;
        float shellG0 = (color0 >> 8 & 255) / 255.0F;
        float shellB0 = (color0 & 255) / 255.0F;

        float shellR1 = (color1 >> 16 & 255) / 255.0F;
        float shellG1 = (color1 >> 8 & 255) / 255.0F;
        float shellB1 = (color1 & 255) / 255.0F;

        getWeaponModel().renderPartColored(poseStack, casingBuilder1, "Shell",
                packedLight, packedOverlay, shellR1, shellG1, shellB1, 1.0F);
        getWeaponModel().renderPartColored(poseStack, casingBuilder0, "ShellFore",
                packedLight, packedOverlay, shellR0, shellG0, shellB0, 1.0F);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 1.5f, -11);
        poseStack.mulPose(Axis.YN.rotationDegrees(90));
        double smokeScale = 0.25;
        poseStack.scale((float)smokeScale, (float)smokeScale, (float)smokeScale);
        renderSmokeNodes(poseStack, buffer, gun.getConfig(stack, 0).smokeNodes, 0.75, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 1.5f, -11);
        poseStack.mulPose(Axis.YN.rotationDegrees(90));
        if (Minecraft.getInstance().player != null) {
            ItemStack mainHand = Minecraft.getInstance().player.getMainHandItem();
            if (mainHand.getItem() instanceof GunItem gunItem) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gunItem.shotRand)));
            }
        }
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8f;

        double startX = -1.25f * offset;
        double startY = -1.75f * offset;
        double startZ = -0.5f * offset;

        double aimX = 0;
        double aimY = 0;
        double aimZ = 0;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        poseStack.translate(x, y, z);

        float xOffset;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = -6.0f;
        } else {
            xOffset = 9.0f;
        }
        poseStack.translate(xOffset, -10, -6);

        float scale = 4f;
        poseStack.scale(scale, scale, scale);


    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(0, -1, -3);
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        float scale = 0.1f;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(11f, 0f, 0);
    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        // Как в оригинале: GL11.glTranslated(0, -0.5, -4.25);
        double scale = -10D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -0.5f, -4.25f);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        // Для GUI рендерим без гильз (как в оригинале renderOther)
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell", "ShellFore");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        // Для инвентаря рендерим без гильз
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell", "ShellFore");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        List<String> excludedParts = Arrays.asList("Shell", "ShellFore");
        getWeaponModel().renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        // Как в оригинале: return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F;
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.5F;
        }
        return 2.75F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        // Как в оригинале: fov * (1 - aimingProgress * 0.33F)
        float aimingProgress = getAimingProgress(0);
        return fov * (1 - aimingProgress * 0.33F);
    }

    @Override
    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight,
                               int packedOverlay) {
        // Для стола модификаций рендерим все части
        poseStack.pushPose();
        setupModTableTransforms(poseStack, stack);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);

        poseStack.popPose();
    }
}