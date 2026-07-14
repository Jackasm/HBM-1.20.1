package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.HBMResourceManager;
import com.hbm.particle.SpentCasing;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.util.BobMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.Objects;

public class GunSexyRenderer extends BaseGunRenderer {

    private ResourceLocation SEXY_TEX = null;
    private ResourceLocation WHISKEY_TEX = null;
    private ResourceLocation SHELL_TEX = null;
    private HFRWavefrontObject SEXY_MODEL = null;
    private HFRWavefrontObject WHISKEY_MODEL = null;
    private HFRWavefrontObject SHELL_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (SEXY_TEX == null) {
            SEXY_TEX = HBMResourceManager.sexy_tex;
        }
        return SEXY_TEX;
    }

    protected ResourceLocation getWhiskeyTexture() {
        if (WHISKEY_TEX == null) {
            WHISKEY_TEX = HBMResourceManager.whiskey_tex;
        }
        return WHISKEY_TEX;
    }

    protected HFRWavefrontObject getWhiskeyModel() {
        if (WHISKEY_MODEL == null) {
            WHISKEY_MODEL = HBMResourceManager.whiskey;
        }
        return WHISKEY_MODEL;
    }

    protected HFRWavefrontObject getShellModel() {
        if (SHELL_MODEL == null) {
            SHELL_MODEL = HBMResourceManager.sexy; // предположим, что модель гильз в той же модели
        }
        return SHELL_MODEL;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (SEXY_MODEL == null) {
            SEXY_MODEL = HBMResourceManager.sexy;
        }
        return SEXY_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2.5F : -0.25F;
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

        // Получаем анимации
        boolean doesCycle = HbmAnimations.getRelevantAnim(0) != null && Objects.requireNonNull(HbmAnimations.getRelevantAnim(0)).animation.getBus("CYCLE") != null;
        boolean reloading = HbmAnimations.getRelevantAnim(0) != null && Objects.requireNonNull(HbmAnimations.getRelevantAnim(0)).animation.getBus("BELT") != null;
        boolean useShellCount = HbmAnimations.getRelevantAnim(0) != null && Objects.requireNonNull(HbmAnimations.getRelevantAnim(0)).animation.getBus("SHELLS") != null;
        boolean girldinner = HbmAnimations.getRelevantAnim(0) != null && Objects.requireNonNull(HbmAnimations.getRelevantAnim(0)).animation.getBus("BOTTLE") != null;

        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] lower = HbmAnimations.getRelevantTransformation("LOWER", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] cycle = HbmAnimations.getRelevantTransformation("CYCLE", 0);
        double[] barrel = HbmAnimations.getRelevantTransformation("BARREL", 0);
        double[] hood = HbmAnimations.getRelevantTransformation("HOOD", 0);
        double[] lever = HbmAnimations.getRelevantTransformation("LEVER", 0);
        double[] belt = HbmAnimations.getRelevantTransformation("BELT", 0);
        double[] mag = HbmAnimations.getRelevantTransformation("MAG", 0);
        double[] magRot = HbmAnimations.getRelevantTransformation("MAGROT", 0);
        double[] shellCount = HbmAnimations.getRelevantTransformation("SHELLS", 0);
        double[] bottle = HbmAnimations.getRelevantTransformation("BOTTLE", 0);
        double[] sippy = HbmAnimations.getRelevantTransformation("SIP", 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Рендерим виски если есть анимация
        if (girldinner && bottle != null && sippy != null) {
            poseStack.pushPose();
            poseStack.translate(bottle[0], bottle[1], bottle[2]);
            poseStack.translate(0, 2, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) sippy[0]));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.XP.rotationDegrees(-15));
            poseStack.translate(0, -2, 0);
            poseStack.scale(1.5f, 1.5f, 1.5f);

            VertexConsumer whiskeyBuilder = buffer.getBuffer(RenderType.entityCutout(getWhiskeyTexture()));
            getWhiskeyModel().renderAll(poseStack, whiskeyBuilder, packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Применяем анимации
        if (equip != null) {
            poseStack.translate(0, -1, -8);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) equip[0]));
            poseStack.translate(0, 1, 8);
        }

        if (lower != null) {
            poseStack.translate(0, 0, -6);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) lower[0]));
            poseStack.translate(0, 0, 6);
        }

        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(0, 0, recoil[2]);
        }

        // Рендерим основные части
        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);

        // Ствол
        if (barrel != null && barrel.length >= 3) {
            poseStack.pushPose();
            poseStack.translate(0, 0, barrel[2]);
            getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Пружина отдачи
        if (barrel != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, -0.375f);
            poseStack.scale(1, 1, (float) (1 + 0.457247371D * barrel[2]));
            poseStack.translate(0, 0, 0.375f);
            getWeaponModel().renderPart(poseStack, builder, "RecoilSpring", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Капюшон
        if (hood != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0.4375f, -2.875f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) hood[0]));
            poseStack.translate(0, -0.4375f, 2.875f);
            getWeaponModel().renderPart(poseStack, builder, "Hood", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Рычаг
        if (lever != null && lever.length >= 3) {
            poseStack.pushPose();
            poseStack.translate(0, 0.46875f, -6.875f);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (lever[2] * 60)));
            poseStack.translate(0, -0.46875f, 6.875f);
            getWeaponModel().renderPart(poseStack, builder, "Lever", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Пружина замка
        if (lever != null && lever.length >= 3) {
            poseStack.pushPose();
            poseStack.translate(0, 0, -6.75f);
            poseStack.scale(1, 1, (float) (1 - lever[2] * 0.25));
            poseStack.translate(0, 0, 6.75f);
            getWeaponModel().renderPart(poseStack, builder, "LockSpring", packedLight, packedOverlay);
            poseStack.popPose();
        }

        // Магазин с лентой
        if (mag != null && mag.length >= 3 && magRot != null) {
            poseStack.pushPose();
            poseStack.translate(mag[0], mag[1], mag[2]);
            poseStack.translate(0, -1, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) magRot[2]));
            poseStack.translate(0, 1, 0);
            getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);

            double p = 0.0625D;
            double x = p * 17;
            double y = p * -26;
            double angle = 0;
            Vector3f vec = new Vector3f(0, 0.4375f, 0);

            // Углы для ленты
            double[] anglesLoaded = new double[]   {0,   0,  20,  20,  50, 60, 70};
            double[] anglesUnloaded = new double[] {0, -10, -50, -60, -60,  0,  0};
            double reloadProgress = !reloading || belt == null ? 1D : belt[0];
            double cycleProgress = !doesCycle || cycle == null ? 1 : cycle[0];

            double[][] shells = new double[anglesLoaded.length][3];

            // Генерация позиций ленты
            for (int i = 0; i < anglesLoaded.length; i++) {
                shells[i][0] = x;
                shells[i][1] = y;
                shells[i][2] = angle - 90;
                double delta = BobMathUtil.interp(anglesUnloaded[i], anglesLoaded[i], reloadProgress);
                angle += delta;
                rotateVectorAroundZ(vec, (float)-delta);
                x += vec.x();
                y += vec.y();
            }

            int shellAmount = Objects.requireNonNull(gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, null);
            if (useShellCount && shellCount != null) {
                shellAmount = (int) shellCount[0];
            }

            // Рендерим ленту
            for (int i = 0; i < shells.length - 1; i++) {
                double[] prevShell = shells[i];
                double[] nextShell = shells[i + 1];
                boolean shouldRenderShell = shells.length - i < shellAmount + 2;
                renderShell(poseStack, buffer,
                        prevShell[0], nextShell[0],
                        prevShell[1], nextShell[1],
                        prevShell[2], nextShell[2],
                        shouldRenderShell, cycleProgress,
                        packedLight, packedOverlay);
            }

            poseStack.popPose();
        }

        // Рендерим вспышку выстрела
        poseStack.pushPose();
        poseStack.translate(0, 0, 8);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 * gun.shotRand)));
        renderMuzzleFlash(poseStack, gun.lastShot[0], 7.5, partialTick);
        poseStack.popPose();
    }

    // Метод для рендеринга гильзы
    private void renderShell(PoseStack poseStack, MultiBufferSource buffer,
                             double x0, double x1, double y0, double y1,
                             double rot0, double rot1, boolean shell,
                             double interp, int packedLight, int packedOverlay) {
        double x = BobMathUtil.interp(x0, x1, interp);
        double y = BobMathUtil.interp(y0, y1, interp);
        double rot = BobMathUtil.interp(rot0, rot1, interp);

        poseStack.pushPose();
        poseStack.translate(x, 0.375 + y, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rot));
        poseStack.translate(0, -0.375, 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderPart(poseStack, builder, "Belt", packedLight, packedOverlay);

        if (shell) {
            // Получаем цвет гильзы как в оригинале
            Player player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null) {
                GunItem gun = (GunItem) player.getMainHandItem().getItem();
                if (gun.getConfig(player.getMainHandItem(), 0).getReceivers(player.getMainHandItem()) != null &&
                        gun.getConfig(player.getMainHandItem(), 0).getReceivers(player.getMainHandItem()).length > 0) {

                    SpentCasing casing = Objects.requireNonNull(gun.getConfig(player.getMainHandItem(), 0)
                            .getReceivers(player.getMainHandItem())[0]
                            .getMagazine(player.getMainHandItem())).getCasing(player.getMainHandItem(), player.getInventory());

                    if (casing != null) {
                        int[] colors = casing.getColors();
                        int color = colors.length > 0 ? colors[0] : SpentCasing.COLOR_CASE_BRASS;
                        float r = (color >> 16 & 255) / 255.0F;
                        float g = (color >> 8 & 255) / 255.0F;
                        float b = (color & 255) / 255.0F;

                        getWeaponModel().renderPartColored(poseStack, builder, "Shell",
                                packedLight, packedOverlay, r, g, b, 1.0F);
                    }
                }
            }
        }
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
        double aimY = -6.25 / 8D + 0.1;
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
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 1.75D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(1, 1, 6);
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
        double scale = -9.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        renderOther(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        renderOther(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        renderOther(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    private void renderOther(ItemStack stack, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight,
                             int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        getWeaponModel().renderPart(poseStack, builder, "Gun", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "RecoilSpring", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Hood", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Lever", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "LockSpring", packedLight, packedOverlay);
        getWeaponModel().renderPart(poseStack, builder, "Magazine", packedLight, packedOverlay);

        double p = 0.0625D;
        renderStaticShell(poseStack, buffer, p * 0, p * -6, 90, true, packedLight, packedOverlay);
        renderStaticShell(poseStack, buffer, p * 5, p * 1, 30, true, packedLight, packedOverlay);
        renderStaticShell(poseStack, buffer, p * 12, p * -1, -30, true, packedLight, packedOverlay);
        renderStaticShell(poseStack, buffer, p * 17, p * -6, -60, true, packedLight, packedOverlay);
        renderStaticShell(poseStack, buffer, p * 17, p * -13, -90, true, packedLight, packedOverlay);
        renderStaticShell(poseStack, buffer, p * 17, p * -20, -90, true, packedLight, packedOverlay);
    }

    private void renderStaticShell(PoseStack poseStack, MultiBufferSource buffer,
                                   double x, double y, double rot, boolean shell,
                                   int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(x, 0.375 + y, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rot));
        poseStack.translate(0, -0.375, 0);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderPart(poseStack, builder, "Belt", packedLight, packedOverlay);

        if (shell) {
            getWeaponModel().renderPart(poseStack, builder, "Shell", packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private void rotateVectorAroundZ(Vector3f vec, float angleDeg) {
        float angleRad = (float) Math.toRadians(angleDeg);
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);
        float newX = vec.x() * cos - vec.y() * sin;
        float newY = vec.x() * sin + vec.y() * cos;
        vec.set(newX, newY, vec.z());
    }
}