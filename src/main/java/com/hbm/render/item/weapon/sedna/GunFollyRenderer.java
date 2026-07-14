package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.util.EntityDamageUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GunFollyRenderer extends BaseGunRenderer {

    private static long timeAiming;
    private static boolean jingle = false;
    private static boolean wasAiming = false;

    private ResourceLocation FOLLY_TEX = null;
    private HFRWavefrontObject FOLLY_MODEL = null;

    @Override
    protected ResourceLocation getWeaponTexture() {
        if (FOLLY_TEX == null) {
            FOLLY_TEX = HBMResourceManager.folly_tex;
        }
        return FOLLY_TEX;
    }

    @Override
    protected HFRWavefrontObject getWeaponModel() {
        if (FOLLY_MODEL == null) {
            FOLLY_MODEL = HBMResourceManager.folly;
        }
        return FOLLY_MODEL;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 2F : 2.5F;
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
        Player player = Minecraft.getInstance().player;

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));

        // Получаем анимации
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", 0);
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", 0);
        double[] load = HbmAnimations.getRelevantTransformation("LOAD", 0);
        double[] shell = HbmAnimations.getRelevantTransformation("SHELL", 0);
        double[] screw = HbmAnimations.getRelevantTransformation("SCREW", 0);
        double[] breech = HbmAnimations.getRelevantTransformation("BREECH", 0);

        // Применяем анимацию EQUIP
        if (equip != null && equip.length >= 1) {
            poseStack.translate(0, 1, -4);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) -equip[0]));
            poseStack.translate(0, -1, 4);
        }

        // Применяем анимацию LOAD
        if (load != null && load.length >= 1) {
            poseStack.translate(0, -2, -2);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) load[0]));
            poseStack.translate(0, 2, 2);
        }

        // Рендерим пушку
        getWeaponModel().renderPart(poseStack, builder, "Cannon", packedLight, packedOverlay);

        // Рендерим ствол с анимацией RECOIL
        poseStack.pushPose();
        if (recoil != null && recoil.length >= 3) {
            poseStack.translate(recoil[0], recoil[1], recoil[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Barrel", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим гильзу с анимацией
        poseStack.pushPose();
        if (shell != null && shell.length >= 3) {
            poseStack.translate(shell[0], shell[1], shell[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Shell", packedLight, packedOverlay);
        poseStack.popPose();

        // Рендерим затвор и шестерню
        poseStack.pushPose();
        if (breech != null && breech.length >= 3) {
            poseStack.translate(breech[0], breech[1], breech[2]);
        }
        getWeaponModel().renderPart(poseStack, builder, "Breech", packedLight, packedOverlay);

        poseStack.pushPose();
        poseStack.translate(0, 1, 0);
        if (screw != null && screw.length >= 3) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) screw[2]));
        }
        poseStack.translate(0, -1, 0);
        getWeaponModel().renderPart(poseStack, builder, "Cog", packedLight, packedOverlay);
        poseStack.popPose();
        poseStack.popPose();

        // Рендерим HUD и текст
        boolean isAiming = GunItem.prevAimingProgress >= 1F && GunItem.aimingProgress >= 1F;
        if (isAiming && !wasAiming) {
            timeAiming = System.currentTimeMillis();
        }

        if (isAiming && player != null) {
            renderHUD(poseStack, buffer, stack, gun, player, load, packedLight, packedOverlay);
        } else {
            jingle = false;
        }

        wasAiming = isAiming;
    }

    private void renderHUD(PoseStack poseStack, MultiBufferSource buffer, ItemStack stack,
                           GunItem gun, Player player, double[] load,
                           int packedLight, int packedOverlay) {

        String splash = getBootSplash();

        if (!jingle && !splash.isEmpty()) {
            // Воспроизводим звук - нужно будет добавить в SoundRegistry
            // MainRegistry.proxy.playSoundClient(player.getX(), player.getY(), player.getZ(), "hbm:weapon.fire.vstar", 0.5F, 1F);
            jingle = true;
        }

        Font font = Minecraft.getInstance().font;
        float variance = 0.85F + player.getRandom().nextFloat() * 0.15F;
        int color = new Color(variance, variance * 0.5F, 0F).getRGB();

        // Проверяем время для сообщения о боеприпасах
        if (System.currentTimeMillis() - timeAiming > 5000 && (load == null || load[0] == 0)) {
            IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
            String msg = Objects.requireNonNull(mag).getAmount(stack, player.getInventory()) > 0 ? "+" : ChatFormatting.RED + "No ammo";
            renderTextOnScreen(poseStack, buffer, font, msg, 0.01F, -2.75F, color, packedLight, packedOverlay);
        }

        // Рендерим загрузочный экран
        renderTextOnScreen(poseStack, buffer, font, splash, 0.02F, -2.75F, color, packedLight, packedOverlay);

        // Рендерим TTY вывод
        List<String> tty = getTTY(player);
        if (!tty.isEmpty()) {
            poseStack.pushPose();
            float fontSize = 0.005F;
            poseStack.translate(2.5F, 1.375F, -2.75F);
            poseStack.scale(fontSize, -fontSize, fontSize);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            for (String line : tty) {
                font.drawInBatch(line, 0, 0, color, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
                poseStack.translate(0, (font.lineHeight + 2), 0);
            }
            poseStack.popPose();
        }
    }

    private void renderTextOnScreen(PoseStack poseStack, MultiBufferSource buffer, Font font,
                                    String text, float size, float zOffset, int color,
                                    int packedLight, int packedOverlay) {
        if (text == null || text.isEmpty()) return;

        poseStack.pushPose();
        float textWidth = font.width(text) * size;
        float xOffset = textWidth / 2 + 2;
        float yOffset = 1F + font.lineHeight * size / 2F;

        poseStack.translate(xOffset, yOffset, zOffset);
        poseStack.scale(size, -size, size);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        font.drawInBatch(text, 0, 0, color, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }

    private String getBootSplash() {
        long now = System.currentTimeMillis();
        if (timeAiming + 5000 < now) return "";
        if (timeAiming + 3000 > now) return "";

        int splashIndex = (int) ((now - timeAiming - 3000) * 35 / 2000) - 10;
        char[] letters = "VStarOS".toCharArray();
        StringBuilder splash = new StringBuilder();

        for (int i = 0; i < letters.length; i++) {
            if (i < splashIndex - 1) splash.append(ChatFormatting.LIGHT_PURPLE);
            if (i == splashIndex - 1) splash.append(ChatFormatting.AQUA);
            if (i == splashIndex) splash.append(ChatFormatting.WHITE);
            if (i == splashIndex + 1) splash.append(ChatFormatting.AQUA);
            if (i == splashIndex + 2) splash.append(ChatFormatting.LIGHT_PURPLE);
            if (i > splashIndex + 2) splash.append(ChatFormatting.BLACK);
            splash.append(letters[i]);
        }
        return splash.toString();
    }

    private List<String> getTTY(Player player) {
        List<String> tty = new ArrayList<>();
        long now = System.currentTimeMillis();
        int time = (int) (now - timeAiming);

        if (time < 3000) {
            if (time > 250) tty.add(ChatFormatting.GREEN + "POST successful - Code 0");
            if (time > 500) tty.add(ChatFormatting.GREEN + "8,388,608 bytes of RAM installed");
            if (time > 500) tty.add(ChatFormatting.GREEN + "5,187,427 bytes available");
            if (time > 750) tty.add(ChatFormatting.GREEN + "Reticulating splines...");
            if (time > 1500) tty.add(ChatFormatting.GREEN + "No keyboard found!");
            if (time > 2000) tty.add(ChatFormatting.GREEN + "Booting from /dev/sda1...");
        }

        if (time > 5000 && player != null) {
            HitResult mop = EntityDamageUtil.getMouseOver(player, 250);
            String target = ChatFormatting.GREEN + "Target: ";

            if (mop == null || mop.getType() == HitResult.Type.MISS) {
                target += "N/A";
            } else if (mop.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) mop;
                BlockPos pos = blockHit.getBlockPos();
                target += pos.getX() + "/" + pos.getY() + "/" + pos.getZ();
            } else if (mop.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) mop;
                target += entityHit.getEntity().getName().getString();
            }

            tty.add(target);
            tty.add(ChatFormatting.GREEN + "Angle: " + (int) (-player.getXRot() * 100) / 100D);
        }

        return tty;
    }

    @Override
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {
        float aimingProgress = getAimingProgress(partialTick);
        float offset = 0.8F;
        float aim = 0.75F;

        double startX = -2.5F * offset;
        double startY = -1.5F * offset;
        double startZ = 2.75F * offset;

        double aimX = -2 * aim;
        double aimY = -1 * aim;
        double aimZ = 2.25F * offset;

        double x = startX + (aimX - startX) * aimingProgress;
        double y = startY + (aimY - startY) * aimingProgress;
        double z = startZ + (aimZ - startZ) * aimingProgress;

        float xOffset = 0.0F;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) xOffset = 1.5f;
        float yOffset = 0.0F;
        float scale = 0.875f;

        applyFirstPersonTransforms(poseStack, x, y, z, xOffset, yOffset, scale);
    }

    @Override
    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        super.setupThirdPersonTransforms(poseStack, stack);
        double scale = 3D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.translate(-0.25, 0.5, 3);
    }

    @Override
    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.07D;
        poseStack.translate(0.5, 0.4, 0);
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));

    }

    @Override
    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = -8.75D;
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -1, 0);
    }

    @Override
    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    @Override
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

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