package com.hbm.items.weapon.sedna.impl;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.HbmKeybinds.EnumKeybind;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;

import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.util.ArmorUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GunChargeThrower extends GunItem {

    public static final String KEY_LASTHOOK = "lasthook";
    public static final String KEY_LASTHOOK_UUID = "lasthook_uuid";

    public GunChargeThrower(WeaponQuality quality, GunConfig... cfg) {
        super(quality, cfg);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean isHeld) {
        super.inventoryTick(stack, world, entity, slot, isHeld);

        // Проверяем состояние перезарядки
        if (getState(stack, 0) == GunState.RELOADING) {
            setLastHookUUID(stack, null); // Очищаем UUID вместо ID
        }

        // Обработка крюка при удержании в руке
        if (isHeld && entity instanceof Player player) {
            UUID hookUUID = getLastHookUUID(stack);
            if (hookUUID != null) {
                Entity e = ((ServerLevel)world).getEntity(hookUUID);
                if (e != null && e.isAlive() && e instanceof EntityBulletBaseMK4 bullet &&
                        bullet.config == BulletConfigRegistry.ct_hook && bullet.velocity < 0.01) {

                    Vec3 vec = new Vec3(
                            e.getX() - player.getX(),
                            e.getY() - (player.getY() + player.getEyeHeight()),
                            e.getZ() - player.getZ()
                    );

                    double line = vec.length();

                    HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);

                    if (props.getKeyPressed(EnumKeybind.GUN_PRIMARY)) {
                        Vec3 normalized = vec.normalize().scale(0.1);
                        player.setDeltaMovement(
                                player.getDeltaMovement().add(
                                        normalized.x,
                                        normalized.y + 0.04,
                                        normalized.z
                                )
                        );

                        if (!world.isClientSide() && line < 2) {
                            e.discard();
                            setLastHookUUID(stack, null); // Очищаем после уничтожения
                        }
                    } else if (!props.getKeyPressed(EnumKeybind.GUN_SECONDARY)) {
                        // Автоматическое подтягивание
                        Vec3 nextPos = new Vec3(
                                player.getX() + player.getDeltaMovement().x,
                                player.getY() + player.getEyeHeight() + player.getDeltaMovement().y,
                                player.getZ() + player.getDeltaMovement().z
                        );

                        Vec3 delta = new Vec3(
                                e.getX() - nextPos.x,
                                e.getY() - nextPos.y,
                                e.getZ() - nextPos.z
                        );

                        if (delta.length() > line) {
                            Vec3 normalizedDelta = delta.normalize().scale(line);
                            Vec3 newNext = new Vec3(
                                    e.getX() - normalizedDelta.x,
                                    e.getY() - normalizedDelta.y,
                                    e.getZ() - normalizedDelta.z
                            );

                            Vec3 vel = new Vec3(
                                    newNext.x - player.getX(),
                                    newNext.y - (player.getY() + player.getEyeHeight()),
                                    newNext.z - player.getZ()
                            );

                            if (vel.length() < 3) {
                                player.setDeltaMovement(vel.x, vel.y, vel.z);
                            }
                        }
                    } else {
                        player.setDeltaMovement(
                                player.getDeltaMovement().scale(0.5)
                        );
                    }

                    if (player.getDeltaMovement().y > -0.1) {
                        player.fallDistance = 0;
                    }

                    ArmorUtil.resetFlightTime(player);
                } else {
                    // Крюк не найден или мёртв - очищаем
                    setLastHookUUID(stack, null);
                }
            }
        } else {
            // Сброс крюка, если предмет не в руке
            setLastHookUUID(stack, null);
        }
    }

    // Вспомогательные методы для работы с NBT
    public static int getLastHook(ItemStack stack) {
        return getInt(stack, KEY_LASTHOOK);
    }

    public static void setLastHook(ItemStack stack, int value) {
        setInt(stack, KEY_LASTHOOK, value);
    }
    public static UUID getLastHookUUID(ItemStack stack) {
        if (stack.getTag() == null) return null;
        String uuidStr = stack.getTag().getString(KEY_LASTHOOK_UUID);
        return uuidStr.isEmpty() ? null : UUID.fromString(uuidStr);
    }

    public static void setLastHookUUID(ItemStack stack, UUID uuid) {
        if (uuid == null) {
            stack.getOrCreateTag().remove(KEY_LASTHOOK_UUID);
        } else {
            stack.getOrCreateTag().putString(KEY_LASTHOOK_UUID, uuid.toString());
        }
    }
}