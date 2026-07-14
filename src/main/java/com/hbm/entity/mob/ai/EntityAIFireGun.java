package com.hbm.entity.mob.ai;

import com.hbm.handler.HbmKeybinds.EnumKeybind;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.GunItem.GunState;
import com.hbm.items.weapon.sedna.mags.IMagazine;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class EntityAIFireGun extends Goal {

    private final Mob host;
    private LivingEntity target;

    private double attackMoveSpeed = 1.0D;
    private double maxRange = 20.0D;
    private int burstTime = 10;
    private int minWait = 10;
    private int maxWait = 40;
    private float inaccuracy = 30.0F;

    private int attackTimer = 0;
    private FireState state = FireState.IDLE;
    private int stateTimer = 0;

    private enum FireState {
        IDLE,
        WAIT,
        FIRING,
        RELOADING
    }

    public EntityAIFireGun(Mob host) {
        this.host = host;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return host.getTarget() != null && getGun() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        super.start();
        attackTimer = 0;
        state = FireState.IDLE;
        stateTimer = 0;
    }

    @Override
    public void tick() {
        target = host.getTarget();
        if (target == null) return;

        ItemStack stack = host.getMainHandItem();
        GunItem gun = getGun();
        if (gun == null) return;

        double distanceSq = host.distanceToSqr(target);
        boolean canSee = host.getSensing().hasLineOfSight(target);

        if (canSee) {
            attackTimer++;
        } else {
            attackTimer = 0;
        }

        // Движение к цели или остановка для стрельбы
        PathNavigation nav = host.getNavigation();
        if (distanceSq < maxRange * maxRange && attackTimer > 20) {
            nav.stop();
        } else {
            nav.moveTo(target, attackMoveSpeed);
        }

        // Поворот к цели
        host.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // Обновление состояния таймера
        stateTimer--;
        if (stateTimer < 0) {
            stateTimer = 0;

            if (state == FireState.WAIT) {
                updateState(FireState.IDLE, 0, gun, stack);
            } else if (state != FireState.IDLE) {
                int waitTime = host.getRandom().nextInt(maxWait - minWait) + minWait;
                updateState(FireState.WAIT, waitTime, gun, stack);
            }
        } else if (state == FireState.FIRING) {
            // Удерживаем нажатой кнопку стрельбы
            handleKeybind(gun, stack, EnumKeybind.GUN_PRIMARY, true);
        }

        // Проверка возможности стрельбы
        if (canSee && distanceSq < maxRange * maxRange) {
            if (state == FireState.IDLE) {
                GunConfig config = gun.getConfig(stack, 0);
                if (config != null && config.getReceivers(stack).length > 0) {
                    Receiver rec = config.getReceivers(stack)[0];
                    IMagazine mag = rec.getMagazine(stack);
                    int ammoCount = mag.getAmount(stack, null);

                    if (ammoCount <= 0) {
                        updateState(FireState.RELOADING, 20, gun, stack);
                    } else if (GunItem.getState(stack, 0) == GunState.IDLE) {
                        int burst = host.getRandom().nextInt(burstTime);
                        updateState(FireState.FIRING, burst, gun, stack);
                    }
                }
            }
        }
    }

    private void updateState(FireState newState, int time, GunItem gun, ItemStack stack) {
        FireState oldState = state;
        state = newState;
        stateTimer = time;

        switch (newState) {
            case FIRING:
                handleKeybind(gun, stack, EnumKeybind.GUN_PRIMARY, true);
                break;
            case RELOADING:
                handleKeybind(gun, stack, EnumKeybind.RELOAD, true);
                break;
            default:
                // Отпускаем все кнопки
                handleKeybind(gun, stack, EnumKeybind.GUN_PRIMARY, false);
                handleKeybind(gun, stack, EnumKeybind.RELOAD, false);
                handleKeybind(gun, stack, EnumKeybind.GUN_SECONDARY, false);
                handleKeybind(gun, stack, EnumKeybind.GUN_TERTIARY, false);
                break;
        }
    }

    private void handleKeybind(GunItem gun, ItemStack stack, EnumKeybind key, boolean pressed) {
        if (key == null) return;

        GunItem.KeybindType keyType = GunItem.convertToKeybindType(key);
        if (keyType == null) return;

        // Добавляем разброс при стрельбе
        if (key == EnumKeybind.GUN_PRIMARY && pressed) {
            host.setYRot(host.getYRot() + (host.getRandom().nextFloat() - 0.5F) * inaccuracy);
            host.setXRot(host.getXRot() + (host.getRandom().nextFloat() - 0.5F) * inaccuracy);
            host.yHeadRot = host.getYRot();
        }

        gun.handleKeybind(host, null, stack, keyType, pressed);
    }

    private GunItem getGun() {
        ItemStack stack = host.getMainHandItem();
        if (stack.isEmpty()) return null;
        if (stack.getItem() instanceof GunItem gun) return gun;
        return null;
    }
}