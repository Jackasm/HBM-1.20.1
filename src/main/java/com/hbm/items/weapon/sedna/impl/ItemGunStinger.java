package com.hbm.items.weapon.sedna.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.hbm.items.weapon.sedna.Crosshair;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.hud.AmmoCounterHUD;
import com.hbm.items.weapon.sedna.hud.DurabilityBarHUD;
import com.hbm.items.weapon.sedna.hud.IHUDComponent;

import com.hbm.sound.ModSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ItemGunStinger extends GunItem {

    public static final String KEY_LOCKINGON = "lockingon";
    public static final String KEY_LOCKONPROGRESS = "lockonprogress";
    public static final String KEY_LOCKONTARGET = "lockontarget";
    public static final String KEY_LOCKEDON = "lockedon";

    public static float prevLockon;
    public static float lockon;

    public ItemGunStinger(WeaponQuality quality, GunConfig... cfg) {
        super(quality, cfg);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
        super.inventoryTick(stack, level, entity, slot, isHeld);

        if (entity instanceof Player player) {

            // Синхронизация клиент/сервер
            if (!level.isClientSide) {
                // Серверная логика
                if (!isHeld && ItemGunStinger.getIsLockingOn(stack)) {
                    setIsLockingOn(stack, false);
                }

                int prevTarget = ItemGunStinger.getLockonTarget(stack);
                if (isHeld && getIsLockingOn(stack) && getIsAiming(stack) &&
                        Objects.requireNonNull(this.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, player.getInventory()) > 0) {

                    int newLockonTarget = getLockonTarget(player, 150D, 10D);

                    if (newLockonTarget == -1) {
                        if (!getIsLockedOn(stack)) resetLockon(stack);
                    } else {
                        if (!getIsLockedOn(stack) && newLockonTarget != prevTarget) {
                            resetLockon(stack);
                            setLockonTarget(stack, newLockonTarget);
                        }
                        progressLockon(stack);

                        if (getLockonProgress(stack) >= 60 && !getIsLockedOn(stack)) {
                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                    ModSounds.TECH_BLEEP.get(), player.getSoundSource(), 1F, 1F);
                            setIsLockedOn(stack, true);
                        }
                    }
                } else {
                    resetLockon(stack);
                }
            } else {
                // Клиентская логика для анимации HUD
                if (getLockonProgress(stack) > 1) {
                    prevLockon = lockon;
                    lockon += (1F / 60F);
                } else {
                    prevLockon = 0;
                    lockon = 0;
                }
            }
        }
    }

    public void resetLockon(ItemStack stack) {
        setLockonProgress(stack, 0);
        setIsLockedOn(stack, false);
    }

    public void progressLockon(ItemStack stack) {
        setLockonProgress(stack, getLockonProgress(stack) + 1);
    }

    public static int getLockonTarget(Player player, double distance, double angleThreshold) {

        if (player == null) return -1;

        double x = player.getX();
        double y = player.getY() + player.getEyeHeight();
        double z = player.getZ();

        // Вектор направления взгляда
        Vec3 lookVec = player.getLookAngle().scale(distance);

        // Точки для построения AABB
        Vec3 lookPoint = new Vec3(x + lookVec.x, y + lookVec.y, z + lookVec.z);
        Vec3 leftPoint = lookPoint.yRot((float) -Math.toRadians(angleThreshold)).add(0, 10, 0);
        Vec3 rightPoint = lookPoint.yRot((float) Math.toRadians(angleThreshold)).add(0, -10, 0);
        Vec3 startPoint = new Vec3(x, y, z);

        // Создаем AABB, охватывающий все точки
        double minX = Math.min(Math.min(lookPoint.x, leftPoint.x), Math.min(rightPoint.x, startPoint.x));
        double minY = Math.min(Math.min(lookPoint.y, leftPoint.y), Math.min(rightPoint.y, startPoint.y));
        double minZ = Math.min(Math.min(lookPoint.z, leftPoint.z), Math.min(rightPoint.z, startPoint.z));
        double maxX = Math.max(Math.max(lookPoint.x, leftPoint.x), Math.max(rightPoint.x, startPoint.x));
        double maxY = Math.max(Math.max(lookPoint.y, leftPoint.y), Math.max(rightPoint.y, startPoint.y));
        double maxZ = Math.max(Math.max(lookPoint.z, leftPoint.z), Math.max(rightPoint.z, startPoint.z));

        AABB aabb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        List<Entity> entities = player.level().getEntities(player, aabb,
                e -> e.isAlive() && e.getBbHeight() >= 0.5F && e.isPickable());

        Entity closestEntity = null;
        double closestAngle = 360D;

        for (Entity entity : entities) {
            Vec3 toEntity = new Vec3(
                    entity.getX() - x,
                    entity.getY() + entity.getBbHeight() / 2D - y,
                    entity.getZ() - z
            );

            double vecProd = toEntity.dot(lookVec);
            double bot = toEntity.length() * lookVec.length();
            double angle = Math.abs(Math.acos(vecProd / bot) * 180 / Math.PI);

            if (angle < closestAngle && angle < angleThreshold) {
                closestAngle = angle;
                closestEntity = entity;
            }
        }

        return closestEntity == null ? -1 : closestEntity.getId();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUD(GuiGraphics guiGraphics, float partialTick, Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        // Отменяем стандартный прицел и рисуем кастомный
        GunConfig config = getConfig(stack, 0);
        if (!(config.getHideCrosshair(stack) && !(aimingProgress >= 1F))) {
            Crosshair crosshair = config.getCrosshair(stack);
            if (crosshair != Crosshair.NONE) {
                renderCrosshair(guiGraphics, crosshair, width / 2, height / 2);
            }
        }

        // Рендерим индикатор захвата цели Stinger
        if (aimingProgress >= 1F) renderStingerLockon(guiGraphics, partialTick);

        // Получаем HUD компоненты из конфига
        int confNo = this.configs.length;
        List<IHUDComponent> allComponents = new ArrayList<>();

        for (int i = 0; i < confNo; i++) {
            IHUDComponent[] components = getConfig(stack, i).getHUDComponents(stack);
            if (components != null) {
                allComponents.addAll(Arrays.asList(components));
            }
        }

        // Если нет компонентов, добавляем дефолтные
        if (allComponents.isEmpty()) {
            allComponents.add(new AmmoCounterHUD(0));
            allComponents.add(new DurabilityBarHUD());
        }

        // Рендерим все компоненты
        int bottomOffset = 0;
        for (IHUDComponent component : allComponents) {
            component.renderHUDComponent(guiGraphics, player, stack, bottomOffset, 0);
            bottomOffset += component.getComponentHeight(player, stack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderStingerLockon(GuiGraphics guiGraphics, float partialTick) {
        float renderLockon = prevLockon + (lockon - prevLockon) * partialTick;
        int progress = (int) (renderLockon * 28);

        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        // Рендерим рамку
        guiGraphics.blit(overlay_misc,
                width / 2 - 15, height / 2 + 18,
                146, 18,
                30, 10);

        guiGraphics.blit(overlay_misc,
                width / 2 - 14, height / 2 + 19,
                147, 29,
                progress, 8);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static boolean getIsLockingOn(ItemStack stack) {
        return getBoolean(stack, KEY_LOCKINGON);
    }

    public static void setIsLockingOn(ItemStack stack, boolean value) {
        setBoolean(stack, KEY_LOCKINGON, value);
    }

    public static int getLockonProgress(ItemStack stack) {
        return getInt(stack, KEY_LOCKONPROGRESS);
    }

    public static void setLockonProgress(ItemStack stack, int value) {
        setInt(stack, KEY_LOCKONPROGRESS, value);
    }

    public static int getLockonTarget(ItemStack stack) {
        return getInt(stack, KEY_LOCKONTARGET);
    }

    public static void setLockonTarget(ItemStack stack, int value) {
        setInt(stack, KEY_LOCKONTARGET, value);
    }

    public static boolean getIsLockedOn(ItemStack stack) {
        return getBoolean(stack, KEY_LOCKEDON);
    }

    public static void setIsLockedOn(ItemStack stack, boolean value) {
        setBoolean(stack, KEY_LOCKEDON, value);
    }
}