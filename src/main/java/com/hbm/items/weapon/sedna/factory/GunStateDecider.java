package com.hbm.items.weapon.sedna.factory;

import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.render.anim.AnimationEnums.GunAnimation;
import com.hbm.items.weapon.sedna.GunItem.GunState;
import com.hbm.items.weapon.sedna.GunItem.LambdaContext;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

public class GunStateDecider {

    /**
     * The meat and bones of the gun system's state machine.
     * This standard decider can handle guns with an automatic primary receiver, as well as one receiver's reloading state.
     * It supports draw delays as well as semi and auto fire
     */
    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_DECIDER = (stack, ctx) -> {
        int index = ctx.configIndex;
        GunState lastState = GunItem.getState(stack, index);
        deciderStandardFinishDraw(stack, lastState, index);
        deciderStandardClearJam(stack, lastState, index);
        deciderStandardReload(stack, ctx, lastState, 0, index);
        deciderAutoRefire(stack, ctx, lastState, 0, index, () ->
               GunItem.getPrimary(stack, index) && GunItem.getMode(stack, ctx.configIndex) == 0);
    };

    /** Transitions the gun from DRAWING to IDLE */
    public static void deciderStandardFinishDraw(ItemStack stack, GunState lastState, int index) {
        // transition to idle
        if(lastState == GunState.DRAWING) {
            GunItem.setState(stack, index, GunState.IDLE);
            GunItem.setTimer(stack, index, 0);
        }
    }

    /** Transitions the gun from JAMMED to IDLE */
    public static void deciderStandardClearJam(ItemStack stack, GunState lastState, int index) {
        // transition to idle
        if(lastState == GunState.JAMMED) {
            GunItem.setState(stack, index, GunState.IDLE);
            GunItem.setTimer(stack, index, 0);
        }
    }

    /** Triggers a reload action on the first receiver. If the mag is not full and reloading is still possible, set to RELOADING, otherwise IDLE */
    public static void deciderStandardReload(ItemStack stack, LambdaContext ctx, GunState lastState, int recIndex, int gunIndex) {
        if(lastState == GunState.RELOADING) {
            LivingEntity entity = ctx.entity;
            Player player = ctx.getPlayer();
            GunConfig cfg = ctx.config;
            Receiver rec = cfg.getReceivers(stack)[recIndex];
            IMagazine mag = rec.getMagazine(stack);

            Objects.requireNonNull(mag).reloadAction(stack, ctx.inventory);
            boolean cancel = GunItem.getReloadCancel(stack);

            // if after reloading the gun can still reload, assume a tube mag and resume reloading
            if(!cancel && mag.canReload(stack, ctx.inventory)) {
                GunItem.setState(stack, gunIndex, GunState.RELOADING);
                GunItem.setTimer(stack, gunIndex, rec.getReloadCycleDuration(stack));
                GunItem.playAnimation(player, stack, GunAnimation.RELOAD_CYCLE, gunIndex);
            } else {
                // if no more reloading can be done, go idle
                if(getStandardJamChance(stack, cfg, gunIndex) > entity.getRandom().nextFloat()) {
                    GunItem.setState(stack, gunIndex, GunState.JAMMED);
                    GunItem.setTimer(stack, gunIndex, rec.getJamDuration(stack));
                    GunItem.playAnimation(player, stack, GunAnimation.JAMMED, gunIndex);
                } else {
                    GunItem.setState(stack, gunIndex, GunState.DRAWING);
                    int duration = rec.getReloadEndDuration(stack) +
                            (mag.getAmountBeforeReload(stack) <= 0 ? rec.getReloadCockOnEmptyPost(stack) : 0);
                    GunItem.setTimer(stack, gunIndex, duration);
                    GunItem.playAnimation(player, stack, GunAnimation.RELOAD_END, gunIndex);
                }

                GunItem.setReloadCancel(stack, false);
            }

            mag.setAmountAfterReload(stack, mag.getAmount(stack, ctx.inventory));
        }
    }

    public static float getStandardJamChance(ItemStack stack, GunConfig config, int index) {
        float percent = GunItem.getWear(stack, index) / config.getDurability(stack);
        if(percent < 0.66F) return 0F;
        return Math.min((percent - 0.66F) * 4F, 1F);
    }

    /** Triggers a re-fire of the primary if the fire delay has expired, the left mouse button is down and re-firing is enabled, otherwise switches to IDLE */
    public static void deciderAutoRefire(ItemStack stack, LambdaContext ctx, GunState lastState, int recIndex, int gunIndex, BooleanSupplier refireCondition) {
        if(lastState == GunState.COOLDOWN) {
            LivingEntity entity = ctx.entity;
            Player player = ctx.getPlayer();
            GunConfig cfg = ctx.config;
            Receiver rec = cfg.getReceivers(stack)[recIndex];

            // if the gun supports re-fire (i.e. if it's an auto)
            if(rec.getRefireOnHold(stack) && refireCondition.getAsBoolean()) {
                // if there's a bullet loaded, fire again
                if(Objects.requireNonNull(rec.getCanFire(stack)).apply(stack, ctx)) {
                    Objects.requireNonNull(rec.getOnFire(stack)).accept(stack, ctx);
                    GunItem.setState(stack, gunIndex, GunState.COOLDOWN);
                    GunItem.setTimer(stack, gunIndex, rec.getDelayAfterFire(stack));

                    SoundEvent soundEvent = rec.getFireSound(stack);
                    if(soundEvent != null) {
                        entity.level().playSound(
                                null,
                                entity.getX(), entity.getY(), entity.getZ(),
                                soundEvent,
                                SoundSource.PLAYERS,
                                rec.getFireVolume(stack),
                                rec.getFirePitch(stack)
                        );
                    }

                    int remaining = rec.getRoundsPerCycle(stack) - 1;
                    for(int i = 0; i < remaining; i++) {
                        if(Objects.requireNonNull(rec.getCanFire(stack)).apply(stack, ctx)) {
                            Objects.requireNonNull(rec.getOnFire(stack)).accept(stack, ctx);
                        }
                    }
                } else if(rec.getDoesDryFireAfterAuto(stack)) {
                    // if refire after dry is allowed, switch to COOLDOWN which will trigger a refire, otherwise switch to DRAWING
                    GunItem.setState(stack, gunIndex,
                            rec.getRefireAfterDry(stack) ? GunState.COOLDOWN : GunState.DRAWING);
                    GunItem.setTimer(stack, gunIndex, rec.getDelayAfterDryFire(stack));
                    GunItem.playAnimation(player, stack, GunAnimation.CYCLE_DRY, gunIndex);
                } else {
                    // if not, revert to idle
                    GunItem.setState(stack, gunIndex, GunState.IDLE);
                    GunItem.setTimer(stack, gunIndex, 0);
                }
            } else {
                // reload on empty, only for non-refiring guns
                if(rec.getReloadOnEmpty(stack) && Objects.requireNonNull(rec.getMagazine(stack)).getAmount(stack, ctx.inventory) <= 0) {
                    GunItem.setIsAiming(stack, false);
                    IMagazine mag = rec.getMagazine(stack);

                    if(Objects.requireNonNull(mag).canReload(stack, ctx.inventory)) {
                        int loaded = mag.getAmount(stack, ctx.inventory);
                        mag.setAmountBeforeReload(stack, loaded);
                        GunItem.setState(stack, ctx.configIndex, GunState.RELOADING);
                        GunItem.setTimer(stack, ctx.configIndex,
                                rec.getReloadBeginDuration(stack) +
                                        (loaded <= 0 ? rec.getReloadCockOnEmptyPre(stack) : 0));
                        GunItem.playAnimation(player, stack, GunAnimation.RELOAD, ctx.configIndex);
                    } else {
                        GunItem.setState(stack, gunIndex, GunState.IDLE);
                        GunItem.setTimer(stack, gunIndex, 0);
                    }
                } else {
                    GunItem.setState(stack, gunIndex, GunState.IDLE);
                    GunItem.setTimer(stack, gunIndex, 0);
                }
            }
        }
    }

    /**
     * Advanced decider for multi-receiver weapons (e.g., double-barrel shotguns, underbarrel attachments)
     */
    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_MULTI_RECEIVER_DECIDER = (stack, ctx) -> {
        int index = ctx.configIndex;
        GunState lastState = GunItem.getState(stack, index);
        deciderStandardFinishDraw(stack, lastState, index);
        deciderStandardClearJam(stack, lastState, index);

        // Handle each receiver separately
        GunConfig cfg = ctx.config;
        Receiver[] receivers = cfg.getReceivers(stack);

        for (int recIndex = 0; recIndex < receivers.length; recIndex++) {
            deciderStandardReload(stack, ctx, lastState, recIndex, index);
            int finalRecIndex = recIndex;
            deciderAutoRefire(stack, ctx, lastState, recIndex, index,
                    () -> GunItem.getPrimary(stack, index) && GunItem.getMode(stack, ctx.configIndex) == finalRecIndex);
        }
    };

    /**
     * Decider for bolt-action weapons with manual cycling
     */
    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_BOLT_ACTION_DECIDER = (stack, ctx) -> {
        int index = ctx.configIndex;
        GunState lastState = GunItem.getState(stack, index);

        // Finish draw
        if(lastState == GunState.DRAWING) {
            GunItem.setState(stack, index, GunState.IDLE);
            GunItem.setTimer(stack, index, 0);
            return;
        }

        // Clear jam
        if(lastState == GunState.JAMMED) {
            GunItem.setState(stack, index, GunState.IDLE);
            GunItem.setTimer(stack, index, 0);
            return;
        }

        // Handle reload
        if(lastState == GunState.RELOADING) {
            deciderStandardReload(stack, ctx, lastState, 0, index);
            return;
        }

        // Handle cooldown (bolt cycling)
        if(lastState == GunState.COOLDOWN) {
            GunConfig cfg = ctx.config;
            Receiver rec = cfg.getReceivers(stack)[0];
            Player player = ctx.getPlayer();

            // After cooldown, cycle the bolt
            GunItem.setState(stack, index, GunState.CYCLING);
            GunItem.setTimer(stack, index, rec.getBoltCycleDuration(stack));
            GunItem.playAnimation(player, stack, GunAnimation.CYCLE_BOLT, index);
            return;
        }

        // Handle bolt cycling
        if(lastState == GunState.CYCLING) {
            GunItem.setState(stack, index, GunState.IDLE);
            GunItem.setTimer(stack, index, 0);
        }
    };

    /**
     * Utility method for creating custom deciders
     */
    public static BiConsumer<ItemStack, LambdaContext> createCustomDecider(
            boolean autoRefire,
            boolean reloadOnEmpty,
            boolean hasBoltAction) {

        return (stack, ctx) -> {
            int index = ctx.configIndex;
            GunState lastState = GunItem.getState(stack, index);

            // Common state transitions
            deciderStandardFinishDraw(stack, lastState, index);
            deciderStandardClearJam(stack, lastState, index);

            if(hasBoltAction && lastState == GunState.COOLDOWN) {
                // Bolt action handling
                GunConfig cfg = ctx.config;
                Receiver rec = cfg.getReceivers(stack)[0];
                Player player = ctx.getPlayer();

                GunItem.setState(stack, index, GunState.CYCLING);
                GunItem.setTimer(stack, index, rec.getBoltCycleDuration(stack));
                GunItem.playAnimation(player, stack, GunAnimation.CYCLE_BOLT, index);
            } else if(lastState == GunState.RELOADING) {
                deciderStandardReload(stack, ctx, lastState, 0, index);
            } else if(lastState == GunState.COOLDOWN && autoRefire) {
                deciderAutoRefire(stack, ctx, lastState, 0, index,
                        () -> GunItem.getPrimary(stack, index) && GunItem.getMode(stack, ctx.configIndex) == 0);
            } else if(lastState == GunState.COOLDOWN) {
                // Manual weapon cooldown
                GunItem.setState(stack, index, GunState.IDLE);
                GunItem.setTimer(stack, index, 0);
            }
        };
    }
}