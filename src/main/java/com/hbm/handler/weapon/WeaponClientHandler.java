package com.hbm.handler.weapon;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.items.armor.ArmorFSB;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.render.item.weapon.sedna.BaseGunRenderer;
import com.hbm.render.item.weapon.sedna.RegistryGunRender;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.Objects;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class WeaponClientHandler {

    @SubscribeEvent
    public static void preRenderEvent(RenderLivingEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        if (ArmorFSB.hasFSBArmor(player) && HbmPlayerProps.getData(player).isHudEnabled()) {
            ItemStack plate = player.getInventory().armor.get(2);
            if (plate.getItem() instanceof ArmorFSB chestplate && chestplate.hasVATS()) {

                LivingEntity entity = event.getEntity();
                if (entity == player) return;

                float maxHealth = entity.getMaxHealth();
                float currentHealth = entity.getHealth();

                int count = (int) Math.min(maxHealth, 100);
                int bars = (int) Math.ceil(currentHealth * count / maxHealth);

                StringBuilder bar = new StringBuilder(ChatFormatting.RED.toString());

                for (int i = 0; i < count; i++) {
                    if (i == bars) {
                        bar.append(ChatFormatting.RESET);
                    }
                    bar.append("|");
                }

                MultiBufferSource.BufferSource immediateBuffer = mc.renderBuffers().bufferSource();
                renderTag(entity, event.getPoseStack(), immediateBuffer, event.getRenderer(), bar.toString(), chestplate.hasThermal());
                immediateBuffer.endBatch();
            }
        }
    }

    private static void renderTag(LivingEntity entity, PoseStack poseStack, MultiBufferSource buffer,
                                  EntityRenderer<?> renderer, String text, boolean thermal) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        double distSq = entity.distanceToSqr(player);
        float range = 64;

        if (distSq < range * range && entity != player && !entity.isInvisibleTo(player)) {
            renderText(entity, poseStack, buffer, text, entity.getY() + entity.getBbHeight() + 0.5);
        }
    }

    private static void renderText(LivingEntity entity, PoseStack poseStack, MultiBufferSource buffer, String text, double yOffset) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();

        // Позиция текста в мировых координатах (над головой моба)
        double x = entity.getX();
        double y = yOffset;
        double z = entity.getZ();

        // Расстояние до камеры
        double dist = Math.sqrt(
                (x - cameraPos.x) * (x - cameraPos.x) +
                        (y - cameraPos.y) * (y - cameraPos.y) +
                        (z - cameraPos.z) * (z - cameraPos.z)
        );

        // Масштаб зависит от расстояния
        float scale = 0.025f;
        float adjustedScale = scale * (float) Math.min(dist, 32);

        // Перемещаемся в позицию текста (относительно камеры)
        poseStack.pushPose();
        poseStack.translate(x - cameraPos.x, y - cameraPos.y, z - cameraPos.z);

        // Поворачиваем текст к камере (как billboard)
        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());

        // Масштабируем
        poseStack.scale(-adjustedScale, -adjustedScale, adjustedScale);

        // Смещаем текст так, чтобы его центр был в точке вращения
        int width = mc.font.width(text);
        poseStack.translate(-width / 2f, -10f, 0);

        // Рисуем текст
        mc.font.drawInBatch(text, 0, 0, 0xFFFFFF, false, poseStack.last().pose(), buffer,
                Font.DisplayMode.NORMAL, 0x88000000, 15728880);

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        ItemStack heldItem = player.getMainHandItem();

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof GunItem) {
            HumanoidModel<AbstractClientPlayer> model = event.getRenderer().getModel();

            model.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;

        }
    }

    @SubscribeEvent
    public static void onPostRenderPlayer(RenderPlayerEvent.Post event) {
        HumanoidModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        model.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        model.leftArmPose = HumanoidModel.ArmPose.EMPTY;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            Player player = mc.player;

            if (GunItem.recoilVertical != 0 || GunItem.recoilHorizontal != 0) {

                GunItem.offsetVertical += GunItem.recoilVertical;
                GunItem.offsetHorizontal += GunItem.recoilHorizontal;

                player.setXRot(player.getXRot() - GunItem.recoilVertical);
                player.setYRot(player.getYRot() - GunItem.recoilHorizontal);

                GunItem.recoilVertical *= GunItem.recoilDecay;
                GunItem.recoilHorizontal *= GunItem.recoilDecay;

                float dV = GunItem.offsetVertical * GunItem.recoilRebound;
                float dH = GunItem.offsetHorizontal * GunItem.recoilRebound;

                GunItem.offsetVertical -= dV;
                GunItem.offsetHorizontal -= dH;

                player.setXRot(player.getXRot() + dV);
                player.setYRot(player.getYRot() + dH);

                if (Math.abs(GunItem.recoilVertical) < 0.001f) GunItem.recoilVertical = 0;
                if (Math.abs(GunItem.recoilHorizontal) < 0.001f) GunItem.recoilHorizontal = 0;
                if (Math.abs(GunItem.offsetVertical) < 0.001f) GunItem.offsetVertical = 0;
                if (Math.abs(GunItem.offsetHorizontal) < 0.001f) GunItem.offsetHorizontal = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = event.getPlayer();
        if (mc.player != player) return;

        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && held.getItem() instanceof GunItem) {
            BaseGunRenderer gunRenderer = RegistryGunRender.getRenderer(held.getItem());

            float newFov = gunRenderer.getViewFOV(held, event.getFovModifier());
            event.setNewFovModifier(newFov);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        ItemStack mainHand = mc.player.getMainHandItem();


        if (mainHand.getItem() instanceof GunItem gunItem) {
            gunItem.renderHUD(event.getGuiGraphics(), event.getPartialTick(), mc.player, mainHand);
        }
    }

    @SubscribeEvent
    public static void onRenderCrosshair(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player != null) {
                ItemStack mainHand = player.getMainHandItem();
                if (mainHand.getItem() instanceof GunItem gun) {
                    GunConfig config = gun.getConfig(mainHand, 0);

                    if (config.getHideCrosshair(mainHand)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        ItemStack held = mc.player.getMainHandItem();

        if (!held.isEmpty() && held.getItem() instanceof GunItem gun) {
            if (GunItem.prevAimingProgress == 1F && GunItem.aimingProgress == 1F) {
                GunConfig cfg = gun.getConfig(held, 0);
                ResourceLocation scopeTexture = cfg.getScopeTexture(held);

                if (scopeTexture != null) {
                    event.setCanceled(true);
                    renderScopeOverlay(event.getGuiGraphics(), scopeTexture);
                }
            }
        }
    }



    private static void renderScopeOverlay(GuiGraphics guiGraphics, ResourceLocation scopeTexture) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        float alpha = 0.15f;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        double smallest = Math.min(screenWidth, screenHeight);
        double divisor = smallest / (9D / 16D);
        smallest = 9D / 16D;
        double largest = Math.max(screenWidth, screenHeight) / divisor;

        double hMin = (double) screenHeight < screenWidth ? 0.5 - smallest / 2D : 0.5 - largest / 2D;
        double hMax = (double) screenHeight < screenWidth ? 0.5 + smallest / 2D : 0.5 + largest / 2D;
        double wMin = screenWidth < (double) screenHeight ? 0.5 - smallest / 2D : 0.5 - largest / 2D;
        double wMax = screenWidth < (double) screenHeight ? 0.5 + smallest / 2D : 0.5 + largest / 2D;

        int texWidth = 320;
        int texHeight = 320;

        int u1 = (int)(wMin * texWidth);
        int v1 = (int)(hMin * texHeight);
        int u2 = (int)(wMax * texWidth);
        int v2 = (int)(hMax * texHeight);

        guiGraphics.blit(scopeTexture,
                0, 0,
                screenWidth, screenHeight,
                u1, v1,
                u2 - u1, v2 - v1,
                texWidth, texHeight);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        boolean hasThermalSight = false;

        // Проверка тепловизора на оружии
        ItemStack held = mc.player.getMainHandItem();
        if (!held.isEmpty() && held.getItem() instanceof GunItem gun) {
            if (GunItem.prevAimingProgress == 1F && GunItem.aimingProgress == 1F) {
                for (int i = 0; i < gun.getConfigCount(); i++) {
                    if (gun.getConfig(held, i).hasThermalSights(held)) {
                        hasThermalSight = true;
                        break;
                    }
                }
            }
        }

        // Проверка тепловизора на FSB броне (только если нет тепловизора на оружии)
        if (!hasThermalSight) {
            if (ArmorFSB.hasFSBArmor(mc.player)) {
                ItemStack chestplate = mc.player.getInventory().armor.get(2);
                if (chestplate.getItem() instanceof ArmorFSB armor) {
                    if (armor.hasThermal()) {
                        hasThermalSight = true;
                    }
                }
            }
        }

        if (hasThermalSight) {
            renderThermalSight3D(event.getPoseStack(), event.getProjectionMatrix(), mc);
        }
    }

    private static void renderThermalSight3D(PoseStack poseStack, Matrix4f projectionMatrix, Minecraft mc) {
        Player player = mc.player;
        if (player == null) return;

        int renderDistance = mc.options.renderDistance().get();

        int thermalRenderDistance = renderDistance * 16;
        int thermalRenderDistanceSq = thermalRenderDistance * thermalRenderDistance;

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        for (Entity ent : player.level().getEntities(player, player.getBoundingBox().inflate(thermalRenderDistance))) {
            if (ent == player) continue;
            double distanceSq = ent.distanceToSqr(player);
            if (distanceSq > thermalRenderDistanceSq) continue;

            float r, g, b;
            if (ent instanceof EnderDragon || ent.getType().equals(EntityType.WITHER)) {
                r = 1.0F; g = 0.5F; b = 0.0F;
            } else if (ent instanceof Enemy) {
                r = 1.0F; g = 0.0F; b = 0.0F;
            } else if (ent instanceof Player) {
                r = 1.0F; g = 0.0F; b = 1.0F;
            } else if (ent instanceof LivingEntity) {
                r = 0.0F; g = 1.0F; b = 0.0F;
            } else if (ent instanceof ItemEntity) {
                r = 1.0F; g = 1.0F; b = 0.5F;
            } else if (ent instanceof ThrownExperienceBottle) {
                r = (player.tickCount % 10 < 5) ? 1.0F : 0.5F;
                g = 1.0F;
                b = 0.5F;
            } else {
                continue;
            }

            if (ent instanceof LivingEntity living && !living.isAlive()) {
                r = 0.0F; g = 0.0F; b = 0.0F;
            }

            AABB bb = ent.getBoundingBox();
            addLine(buffer, bb.minX, bb.maxY, bb.minZ, bb.minX, bb.minY, bb.minZ, r, g, b);
            addLine(buffer, bb.minX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, r, g, b);
            addLine(buffer, bb.maxX, bb.maxY, bb.minZ, bb.maxX, bb.minY, bb.minZ, r, g, b);
            addLine(buffer, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.minZ, r, g, b);
            addLine(buffer, bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, r, g, b);
            addLine(buffer, bb.maxX, bb.maxY, bb.maxZ, bb.maxX, bb.maxY, bb.minZ, r, g, b);
            addLine(buffer, bb.maxX, bb.maxY, bb.maxZ, bb.maxX, bb.minY, bb.maxZ, r, g, b);
            addLine(buffer, bb.minX, bb.maxY, bb.minZ, bb.minX, bb.maxY, bb.maxZ, r, g, b);
            addLine(buffer, bb.minX, bb.maxY, bb.maxZ, bb.minX, bb.minY, bb.maxZ, r, g, b);
            addLine(buffer, bb.minX, bb.maxY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b);
            addLine(buffer, bb.minX, bb.minY, bb.maxZ, bb.maxX, bb.minY, bb.maxZ, r, g, b);
            addLine(buffer, bb.minX, bb.minY, bb.minZ, bb.minX, bb.minY, bb.maxZ, r, g, b);
        }

        vertexBuffer.bind();
        vertexBuffer.upload(buffer.end());

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        var shader = GameRenderer.getPositionColorShader();
        vertexBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, Objects.requireNonNull(shader));

        poseStack.popPose();

        VertexBuffer.unbind();
        vertexBuffer.close();
    }

    private static void addLine(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b) {
        buffer.vertex((float)x1, (float)y1, (float)z1).color(r, g, b, 1.0F).endVertex();
        buffer.vertex((float)x2, (float)y2, (float)z2).color(r, g, b, 1.0F).endVertex();
    }
}