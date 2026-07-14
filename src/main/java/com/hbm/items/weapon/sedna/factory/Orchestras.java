package com.hbm.items.weapon.sedna.factory;

import java.util.Objects;
import java.util.function.BiConsumer;

import com.hbm.config.ClientConfig;
import com.hbm.items.ModGunItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.AudioWrapper;
import com.hbm.sound.ModSounds;
import com.hbm.sound.SoundHelper;
import com.hbm.util.ModDamageSource;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.impl.GunChargeThrower;
import com.hbm.items.weapon.sedna.impl.ItemGunStinger;
import com.hbm.items.weapon.sedna.GunItem.LambdaContext;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
import com.hbm.particle.SpentCasing;
import com.hbm.particle.helper.CasingCreator;
import com.hbm.render.anim.AnimationEnums.GunAnimation;
import com.hbm.sound.ClientAudioWrapper;

import com.hbm.util.EntityDamageUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;

import static com.hbm.items.weapon.sedna.factory.BulletConfigRegistry.g12_equestrian_bj;
import static com.hbm.sound.ModSounds.*;

/** Orchestras are server-side components that run along client-side animations.
 * The orchestra only knows what animation is or was playing and how long it started, but not if it is still active.
 * Orchestras are useful for things like playing server-side sound, spawning casings or sending particle packets.*/
public class Orchestras {

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_PEPPERBOX = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 24) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
            if(timer == 55) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERSPIN.get(), 1F, 1F);
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 21) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.6F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.8F);
            if(timer == 11) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.6F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 3) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERSPIN.get(), 1F, 1F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 28) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.75F);
            if(timer == 45) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.6F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_ATLAS = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 36) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
            if(timer == 44) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 5) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.9F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 5) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.9F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 24) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 34) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_HENRY = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.RELOAD) {
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_CYCLE) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_END) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 0.9F);
            if(timer == 12 && Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getAmountBeforeReload(stack) <= 0) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 0.9F);
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 1F);
            if(timer == 36) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 1F);
            if(timer == 44) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 14) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.5, -0.125, aiming ? -0.125 : -0.375D, 0, 0.12, -0.12, 0.01, -7.5F + (float)entity.getRandom().nextGaussian() * 5F, (float)entity.getRandom().nextGaussian() * 1.5F, casing.getName(), true, 60, 0.5D, 20);
            }
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_GREASEGUN = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.EQUIP) {
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_OPENLATCH.get(), 1F, 1F);
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 2) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.55, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, -7.5F + (float)entity.getRandom().nextGaussian() * 5F, 12F + (float)entity.getRandom().nextGaussian() * 5F, casing.getName());
            }
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.8F);
            if(timer == 11) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);

        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 24) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 36) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 5) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.8F);
            if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1.25F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 11) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
            if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MARESLEG = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) {
                IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
                boolean empty = Objects.requireNonNull(mag).getAmount(stack, ctx.inventory) == 0;

                CompoundTag tag = stack.getOrCreateTag();
                tag.putBoolean("maresleg_reload_empty", empty);
            }
            if(timer == 8) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.8F);
            if(timer == 16) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNRELOAD.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_CYCLE) {
            if(timer == 0) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNRELOAD.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_END) {
            if(timer == 0) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("maresleg_reload_empty")) {
                    tag.remove("maresleg_reload_empty");
                }
            }
            if(timer == 2) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.7F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 2) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.7F);
            if(timer == 17) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_LEVERCOCK.get(), 1F, 0.8F);
            if(timer == 29) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_LEVERCOCK.get(), 1F, 0.8F);
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 14) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) {
                    CasingCreator.composeEffect(entity.level(), entity,
                            0.3125, -0.125, aiming ? -0.125 : -0.375D,
                            0, 0.18, -0.12, 0.01,
                            -10F + (float)entity.getRandom().nextGaussian() * 5F,
                            (float)entity.getRandom().nextGaussian() * 2.5F,
                            casing.getName(), true, 60, 0.5D, 20);
                }
            }
            if(timer == 8) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_LEVERCOCK.get(), 1F, 0.8F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 8) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_LEVERCOCK.get(), 1F, 0.8F);
        }
        if ((type == GunAnimation.CYCLE || type == GunAnimation.CYCLE_DRY) && timer == 0) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("maresleg_reload_empty")) {
                tag.remove("maresleg_reload_empty");
            }
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MARESLEG_SHORT = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.RELOAD) {
            if(timer == 8) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.8F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_SHOTGUNRELOAD.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_CYCLE) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_SHOTGUNRELOAD.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_END) {
            if(timer == 2) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.7F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 2) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.7F);
            if(timer == 17) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 0.8F);
            if(timer == 29) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 0.8F);
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 14) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.3125, -0.125, 
                        aiming ? -0.125 : -0.375D, 0, -0.08, 0, 0.01, 
                        -15F + (float)entity.getRandom().nextGaussian() * 5F,
                        (float)entity.getRandom().nextGaussian() * 2.5F, casing.getName(),
                        true, 60, 0.5D, 20);
            }
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 0.8F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_LEVERCOCK.get(), 1F, 0.8F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_FLAREGUN = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 0.8F);
            if(timer == 4) {
                IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
                if(Objects.requireNonNull(mag).getAmountAfterReload(stack) > 0) {
                    SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                    if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.625, -0.125, aiming ? -0.125 : -0.375D, -0.12, 0.18, 0, 0.01, -15F + (float)entity.getRandom().nextGaussian() * 7.5F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName(), true, 60, 0.5D, 20);
                    mag.setAmountBeforeReload(stack, 0);
                }
            }
            if(timer == 16) SoundHelper.playSound(entity, RELOAD_INSERT_CANISTER.get(), 1F, 1F);
            if(timer == 24) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 0.8F);
            if(timer == 29) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_NOPIP = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 3) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
            if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 34) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
            if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);

            if(timer == 16) {
                Receiver rec = ctx.config.getReceivers(stack)[0];
                IMagazine mag = rec.getMagazine(stack);
                SpentCasing casing = Objects.requireNonNull(mag).getCasing(stack, ctx.inventory);
                if(casing != null) for(int i = 0; i < mag.getCapacity(stack); i++) CasingCreator.composeEffect(entity.level(), entity, 0.25, -0.125, -0.125, -0.05, 0, 0, 0.01, -6.5F + (float)entity.getRandom().nextGaussian() * 3F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName());
            }
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 11) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 11) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 3) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_CARBINE = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.CYCLE) {
            if(timer == 1) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.3125, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.21, -0.06, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 2.5F, 2.5F + (float)entity.getRandom().nextGaussian() * 2F, casing.getName(), true, 60, 0.5D, 20);
            }
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_END) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
            if(timer == 31) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 6) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            if(timer == 30) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_AM180 = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(ClientConfig.GUN_ANIMS_LEGACY) {
            if(type == GunAnimation.CYCLE) {
                if(timer == 0) {
                    SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                    if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.4375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, -0.06, 0, 0.01, (float)entity.getRandom().nextGaussian() * 10F, (float)entity.getRandom().nextGaussian() * 10F, casing.getName());
                }
            }
            if(type == GunAnimation.CYCLE_DRY) {
                if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
                if(timer == 6) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.9F);
            }
            if(type == GunAnimation.RELOAD) {
                if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
                if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.25F, 1F);
                if(timer == 32) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
                if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.9F);
            }
            if(type == GunAnimation.JAMMED) {
                if(timer == 15) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
            }
            if(type == GunAnimation.INSPECT) {
                if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
                if(timer == 35) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            }
        } else {
            if(type == GunAnimation.CYCLE) {
                if(timer == 0) {
                    SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                    if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.4375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, -0.06, 0, 0.01, (float)entity.getRandom().nextGaussian() * 10F, (float)entity.getRandom().nextGaussian() * 10F, casing.getName());
                }
            }
            if(type == GunAnimation.CYCLE_DRY) {
                if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
                if(timer == 6) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.9F);
            }
            if(type == GunAnimation.RELOAD) {
                if(timer == 6) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
                if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.25F, 1F);
                if(timer == 48) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
                if(timer == 54) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.9F);
            }
            if(type == GunAnimation.JAMMED) {
                if(timer == 6) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.8F);
                if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 1.0F);
            }
            if(type == GunAnimation.INSPECT) {
                if(timer == 6) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
                if(timer == 53) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            }
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_LIBERATOR = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.75F);
            if(timer == 4) {
                IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
                int toEject = Objects.requireNonNull(mag).getAmountAfterReload(stack) - mag.getAmount(stack, ctx.inventory);
                SpentCasing casing = mag.getCasing(stack, ctx.inventory);
                if(casing != null) for(int i = 0; i < toEject; i++) CasingCreator.composeEffect(entity.level(), entity, 0.625, -0.1875, -0.375D, -0.12, 0.18, 0, 0.01, -15F + (float)entity.getRandom().nextGaussian() * 7.5F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName(), true, 60, 0.5D, 20);
            }
            if(timer == 15) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_CYCLE) {
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_END) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.75F);
            if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.75F);
            IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
            int toEject = Objects.requireNonNull(mag).getAmountAfterReload(stack) - mag.getAmount(stack, ctx.inventory);
            if(timer == 4 && toEject > 0) {
                SpentCasing casing = mag.getCasing(stack, ctx.inventory);
                if(casing != null) for(int i = 0; i < toEject; i++) CasingCreator.composeEffect(entity.level(), entity, 0.625, -0.1875, -0.375D, -0.12, 0.18, 0, 0.01, -15F * (float)entity.getRandom().nextGaussian() * 7.5F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName(), true, 60, 0.5D, 20);
                mag.setAmountAfterReload(stack, 0);
            }
            if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_CONGOLAKE = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.CYCLE) {
            if(timer == 15) {
                IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
                SpentCasing casing = Objects.requireNonNull(mag).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.625, aiming ? -0.0625 : -0.25, aiming ? 0 : -0.375D, 0, 0.18, 0.12, 0.01, -5F + (float)entity.getRandom().nextGaussian() * 3.5F, -10F + entity.getRandom().nextFloat() * 5F, casing.getName(), true, 60, 0.5D, 20);
            }
        }
        if(type == GunAnimation.RELOAD || type == GunAnimation.RELOAD_CYCLE) {
            if(timer == 0) SoundHelper.playSound(entity, RELOAD_GLRELOAD.get(), 1F, 1F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 9) SoundHelper.playSound(entity, INSPECT_GLOPEN.get(), 1F, 1F);
            if(timer == 27) SoundHelper.playSound(entity, INSPECT_GLCLOSE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_FLAMER = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE && entity.level().isClientSide()) {
            ClientAudioWrapper runningAudio = (ClientAudioWrapper) GunItem.loopedSounds.get(entity);

            if(timer < 5) {
                //start sound
                if(runningAudio == null || !runningAudio.isPlaying()) {
                    AudioWrapper audio = SoundHelper.createLoopedSound(WEAPON_FIRE_FLAMELOOP.get(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), 1F, 15F, 1F, 10);
                    GunItem.loopedSounds.put(entity, audio);
                    audio.startSound();
                }
                //keepalive
                if(runningAudio != null && runningAudio.isPlaying()) {
                    runningAudio.keepAlive();
                    runningAudio.updatePosition((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
                }
            } else {
                //stop sound due to timeout
                if(runningAudio != null && runningAudio.isPlaying()) runningAudio.stopSound();
            }
        }
        //stop sound due to state change
        if(type != GunAnimation.CYCLE && entity.level().isClientSide()) {
            ClientAudioWrapper runningAudio = (ClientAudioWrapper) GunItem.loopedSounds.get(entity);
            if(runningAudio != null && runningAudio.isPlaying()) runningAudio.stopSound();
        }
        if(entity.level().isClientSide()) return;

        if(type == GunAnimation.RELOAD) {
            if(timer == 15) SoundHelper.playSound(entity, ModSounds.RELOAD_OPENLATCH.get(), 1F, 1F);
            if(timer == 35) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.5F, 1F);
            if(timer == 60) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.75F);
            if(timer == 70) SoundHelper.playSound(entity, RELOAD_INSERT_CANISTER.get(), 1F, 1F);
            if(timer == 85) SoundHelper.playSound(entity, WEAPON_RELOAD_PRESSUREVALVE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_FLAMER_DAYBREAKER = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 15) SoundHelper.playSound(entity, ModSounds.RELOAD_OPENLATCH.get(), 1F, 1F);
            if(timer == 35) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.5F, 1F);
            if(timer == 60) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.75F);
            if(timer == 70) SoundHelper.playSound(entity, RELOAD_INSERT_CANISTER.get(), 1F, 1F);
            if(timer == 85) SoundHelper.playSound(entity, WEAPON_RELOAD_PRESSUREVALVE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_LAG = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.CYCLE) {
            if(timer == 1) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.375, aiming ? 0 : -0.0625, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 5F, 10F + entity.getRandom().nextFloat() * 10F, casing.getName());
            }
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);

        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.5F, 1.6F);
            if(timer == 36) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_UZI = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.EQUIP) {
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_OPENLATCH.get(), 1F, 1.25F);
        }
        if(type == GunAnimation.CYCLE) {
            if(timer == 1) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, -2.5F + (float)entity.getRandom().nextGaussian() * 5F, 10F + entity.getRandom().nextFloat() * 15F, casing.getName());
            }
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 8) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 1F);

        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 4) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 36) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 17) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 1F);
            if(timer == 31) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_SPAS = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.CYCLE || type == GunAnimation.ALT_CYCLE) {
            if(timer == 8) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNCOCK.get(), 1F, 1F);
            if(timer == 10) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) {
                    CasingCreator.composeEffect(entity.level(), entity,
                            0.375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D,
                            0, 0.18, -0.12, 0.01,
                            -3F + (float)entity.getRandom().nextGaussian() * 2.5F,
                            -15F + entity.getRandom().nextFloat() * -5F,
                            casing.getName());
                }
            }
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 8) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNCOCK.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD) {
            IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
            boolean empty = Objects.requireNonNull(mag).getAmount(stack, ctx.inventory) == 0;

            CompoundTag tag = stack.getOrCreateTag();
            tag.putBoolean("spas_reload_empty", empty);

            if(empty) {
                if(timer == 0) SoundHelper.playSound(entity,
                        ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
                if(timer == 7) SoundHelper.playSound(entity,
                        ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            }
            if(timer == 5) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNRELOAD.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD_CYCLE) {
            if(timer == 5) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNRELOAD.get(), 1F, 1F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 5) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNCOCKOPEN.get(), 1F, 1F);
            if(timer == 18) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNCOCKCLOSE.get(), 1F, 1F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 18) SoundHelper.playSound(entity,
                    ModSounds.FOLEY_GUNWHACK.get(), 1F, 1F);
            if(timer == 25) SoundHelper.playSound(entity,
                    ModSounds.FOLEY_GUNWHACK.get(), 1F, 1F);
            if(timer == 29) SoundHelper.playSound(entity,
                    ModSounds.RELOAD_SHOTGUNCOCKCLOSE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_PANERSCHRECK = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 30) SoundHelper.playSound(entity, RELOAD_INSERT_CANISTER.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_G3 = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean scoped = stack.getItem() == ModGunItems.GUN_G3_ZEBRA.get() || WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SCOPE);
        boolean aiming = GunItem.getIsAiming(stack) && !scoped;

        if(type == GunAnimation.CYCLE) {
            if(timer == 0) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.5, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 5F, 12.5F + entity.getRandom().nextFloat() * 5F, casing.getName());
            }
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.8F);
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.9F);

        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 4) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
            if(timer == 32) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 36) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 28) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.JAMMED) {
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
            if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
            if(timer == 30) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_STINGER = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(entity.level().isClientSide()) {
            ClientAudioWrapper runningAudio = (ClientAudioWrapper) GunItem.loopedSounds.get(entity);
            if(ItemGunStinger.getLockonProgress(stack) > 0 && !ItemGunStinger.getIsLockedOn(stack)) {
                //start sound
                if(runningAudio == null || !runningAudio.isPlaying()) {
                    AudioWrapper audio = SoundHelper.createLoopedSound(WEAPON_FIRE_LOCKON.get(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), 1F, 15F, 1F, 10);
                    GunItem.loopedSounds.put(entity, audio);
                    audio.startSound();
                }
                //keepalive
                if(runningAudio != null && runningAudio.isPlaying()) {
                    runningAudio.keepAlive();
                    runningAudio.updatePosition((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
                }
            } else {
                //stop sound due to timeout
                if(runningAudio != null && runningAudio.isPlaying()) runningAudio.stopSound();
            }
        }

        if(type == GunAnimation.RELOAD) {
            if(timer == 30) SoundHelper.playSound(entity, RELOAD_INSERT_CANISTER.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_CHEMTHROWER = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE && entity.level().isClientSide()) {
            ClientAudioWrapper runningAudio = (ClientAudioWrapper) GunItem.loopedSounds.get(entity);

            if(timer < 5) {
                //start sound
                if(runningAudio == null || !runningAudio.isPlaying()) {
                    AudioWrapper audio = SoundHelper.createLoopedSound(WEAPON_FIRE_FLAMELOOP.get(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), 1F, 15F, 1F, 10);
                    GunItem.loopedSounds.put(entity, audio);
                    audio.startSound();
                }
                //keepalive
                if(runningAudio != null && runningAudio.isPlaying()) {
                    runningAudio.keepAlive();
                    runningAudio.updatePosition((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
                }
            } else {
                //stop sound due to timeout
                if(runningAudio != null && runningAudio.isPlaying()) runningAudio.stopSound();
            }
        }
        //stop sound due to state change
        if(type != GunAnimation.CYCLE && entity.level().isClientSide()) {
            ClientAudioWrapper runningAudio = (ClientAudioWrapper) GunItem.loopedSounds.get(entity);
            if(runningAudio != null && runningAudio.isPlaying()) runningAudio.stopSound();
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_AMAT = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.EQUIP) {
            if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 0.5F, 1.25F);
            if(timer == 15) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 0.5F, 1.25F);
        }

        if(type == GunAnimation.CYCLE) {
            if(timer == 7) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
            if(timer == 12) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity,
                        0.375, aiming ? 0 : -0.125, -0.25D,
                        -0.05, 0.2, -0.025,
                        0.01, -10F + (float) entity.getRandom().nextGaussian() * 10F, (float) entity.getRandom().nextGaussian() * 12.5F, casing.getName(), true, 60, 0.5D, 10);
            }
        }

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.75F);
            if(timer == 7) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
        }

        if(type == GunAnimation.RELOAD) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 32) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 41) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
        }

        if(type == GunAnimation.JAMMED) {
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 23) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
        }

        if(type == GunAnimation.INSPECT) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 0.5F, 1F);
            if(timer == 45) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 0.5F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_M2 = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.EQUIP) {
            if(timer == 0) SoundHelper.playSound(entity, "hbm:turret.howard_reload", 1F, 1F);
        }

        if(type == GunAnimation.CYCLE) {
            if(timer == 0) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.375, aiming ? 0 : -0.125, aiming ? 0 : -0.3125D, 0, 0.06, -0.18, 0.01, (float)entity.getRandom().nextGaussian() * 20F, 12.5F + (float)entity.getRandom().nextGaussian() * 7.5F, casing.getName());
            }
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_SHREDDER = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.WEAPON_FIRE_SHREDDERCYCLE.get(), 0.25F, 1.5F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.WEAPON_FIRE_SHREDDERCYCLE.get(), 0.25F, 1.5F);
        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 32) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 28) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_SHREDDER_SEXY = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.CYCLE) {
            if(timer == 0 && Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getType(stack, null) == g12_equestrian_bj) {
                GunItem.setTimer(stack, 0, 20);
            }

            if(timer == 2) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.375, aiming ? -0.0625 : -0.125, aiming ? -0.125 : -0.25D, 0, 0.18, -0.12, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 2.5F, (float)entity.getRandom().nextGaussian() * -20F + 15F, casing.getName(), false, 60, 0.5D, 20);
            }
        }

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
            if(timer == 4) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.75F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 30) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 55) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.5F, 1F);
            if(timer == 65) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 74) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
            if(timer == 88) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.75F);
            if(timer == 100) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);

            if(timer == 55) Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).reloadAction(stack, ctx.inventory);
        }

        if(type == GunAnimation.INSPECT) {
            if(timer == 20) SoundHelper.playSound(entity, "hbm:player.gulp", 1F, 1F);
            if(timer == 25) SoundHelper.playSound(entity, "hbm:player.gulp", 1F, 1F);
            if(timer == 30) SoundHelper.playSound(entity, "hbm:player.gulp", 1F, 1F);
            if(timer == 35) SoundHelper.playSound(entity, "hbm:player.gulp", 1F, 1F);
            if(timer == 50) SoundHelper.playSound(entity, "hbm:player.groan", 1F, 1F);
            if(timer == 60) {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 2));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10 * 20, 0));
            }
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_QUADRO = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 30) SoundHelper.playSound(entity, RELOAD_INSERT_CANISTER.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MINIGUN = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.CYCLE) {
            if(timer == 0) {
                int rounds = WeaponModManager.hasUpgrade(stack, ctx.configIndex, WeaponModManager.MINIGUN_SPEED) ? 3 : 1;
                for(int i = 0; i < rounds; i++) {
                    SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                    if(casing != null) CasingCreator.composeEffect(entity.level(), entity, aiming ? 0.125 : 0.5, aiming ? -0.125 : -0.25, aiming ? -0.25 : -0.5D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 15F, (float)entity.getRandom().nextGaussian() * 15F, casing.getName());
                }
            }
            //if(timer == (WeaponModManager.hasUpgrade(stack, 0, 207) ? 3 : 1)) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERSPIN.get(), 1F, 0.75F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.75F);
            if(timer == 1) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERSPIN.get(), 1F, 0.75F);
        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERSPIN.get(), 1F, 0.75F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERSPIN.get(), 1F, 0.75F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MISSILE_LAUNCHER = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1.25F);
        }
        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 1F, 0.9F);
            if(timer == 30) SoundHelper.playSound(entity, RELOAD_INSERT_CANISTER.get(), 1F, 1F);
            if(timer == 42) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 1F, 0.9F);
        }

        if(type == GunAnimation.JAMMED || type == GunAnimation.INSPECT) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 1F, 0.9F);
            if(timer == 27) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 1F, 0.9F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_TESLA = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.WEAPON_FIRE_SHREDDERCYCLE.get(), 0.25F, 1.25F);
        }
        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.WEAPON_FIRE_SHREDDERCYCLE.get(), 0.25F, 1.25F);
        }
        if(type == GunAnimation.INSPECT) {
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.BLOCK_SQUEAKYTOY.get(), 0.25F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_LASER_PISTOL = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1.5F);
        }

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
            if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1.25F);
            if(timer == 34) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1.25F);
            if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1.25F);
        }

        if(type == GunAnimation.JAMMED) {
            if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 1F);
            if(timer == 15) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1.25F);
            if(timer == 30) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.25F, 1.5F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_STG77 = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(ClientConfig.GUN_ANIMS_LEGACY) {
            if(type == GunAnimation.CYCLE) {
                if(timer == 0) {
                    SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                    if(casing != null) CasingCreator.composeEffect(entity.level(), entity, aiming ? 0.125 : 0.125, aiming ? -0.125 : -0.25, aiming ? -0.125 : -0.25D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 5F, 7.5F + entity.getRandom().nextFloat() * 5F, casing.getName());
                }
                if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 0.25F, 1.25F);
            }
            if(type == GunAnimation.CYCLE_DRY) {
                if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.8F);
                if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.9F);
                if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 0.25F, 1.25F);
            }
            if(type == GunAnimation.RELOAD) {
                if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
                if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
                if(timer == 24) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
                if(timer == 34) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            }
            if(type == GunAnimation.INSPECT) {
                if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
                if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);

                if(timer == 114) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
                if(timer == 124) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            }
        } else {
            if(type == GunAnimation.CYCLE) {
                if(timer == 0) {
                    SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                    if(casing != null) CasingCreator.composeEffect(entity.level(), entity, aiming ? 0.125 : 0.25, aiming ? -0.125 : -0.25, aiming ? -0.125 : -0.25D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 5F, 7.5F + entity.getRandom().nextFloat() * 5F, casing.getName());
                }
                if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 0.25F, 1.25F);
            }
            if(type == GunAnimation.CYCLE_DRY) {
                if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.8F);
                if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.9F);
                if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 0.25F, 1.25F);
            }
            if(type == GunAnimation.RELOAD) {
                if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
                if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
                if(timer == 32) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.25F, 1.25F);
                if(timer == 38) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
                if(timer == 43) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            }
            if(type == GunAnimation.INSPECT) {
                if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.9F);
                if(timer == 11) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);

                if(timer == 72) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 1F);
                if(timer == 84) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            }
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_TAU = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.SPINUP && entity.level().isClientSide()) {
            ClientAudioWrapper runningAudio = (ClientAudioWrapper) GunItem.loopedSounds.get(entity);

            if(timer < 300) {
                if(runningAudio == null || !runningAudio.isPlaying()) {
                    AudioWrapper audio = SoundHelper.createLoopedSound(WEAPON_FIRE_TAULOOP.get(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), 1F, 15F, 0.75F, 10);
                    audio.updatePitch(0.75F);
                    GunItem.loopedSounds.put(entity, audio);
                    audio.startSound();
                }
                if(runningAudio != null && runningAudio.isPlaying()) {
                    runningAudio.keepAlive();
                    runningAudio.updatePosition((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
                    runningAudio.updatePitch(0.75F + timer * 0.01F);
                }
            } else {
                if(runningAudio != null && runningAudio.isPlaying()) runningAudio.stopSound();
            }
        }
        //stop sound due to state change
        if(type != GunAnimation.SPINUP && entity.level().isClientSide()) {
            ClientAudioWrapper runningAudio = (ClientAudioWrapper) GunItem.loopedSounds.get(entity);
            if(runningAudio != null && runningAudio.isPlaying()) runningAudio.stopSound();
        }
        if(entity.level().isClientSide()) return;

        if(type == GunAnimation.CYCLE) {
            if(timer == 0) SoundHelper.playSound(entity, WEAPON_FIRE_TAU.get(), 0.5F, 0.9F + entity.getRandom().nextFloat() * 0.2F);
        }

        if(type == GunAnimation.ALT_CYCLE) {
            if(timer == 0) SoundHelper.playSound(entity, WEAPON_FIRE_TAU.get(), 0.5F, 0.7F + entity.getRandom().nextFloat() * 0.2F);
        }

        if(type == GunAnimation.SPINUP) {
            if(timer % 10 == 0 && timer < 130) {
                IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
                if(Objects.requireNonNull(mag).getAmount(stack, ctx.inventory) <= 0) {
                    GunItem.playAnimation(ctx.getPlayer(), stack, GunAnimation.CYCLE_DRY, ctx.configIndex);
                    return;
                }
                mag.useUpAmmo(stack, ctx.inventory, 1);
            }

            if(timer > 200) {
                GunItem.playAnimation(ctx.getPlayer(), stack, GunAnimation.CYCLE_DRY, ctx.configIndex);

                entity.hurt(ModDamageSource.causeTauDamage(entity), 1_000F);

                GunItem.setWear(stack, ctx.configIndex, Math.min(GunItem.getWear(stack, ctx.configIndex) + 10_000F, ctx.config.getDurability(stack)));

                SoundHelper.playSound(entity, ENTITY_UFOBLAST.get(), 5.0F, 0.9F);
                SoundHelper.playSound(entity, SoundEvents.FIREWORK_ROCKET_BLAST, 5.0F, 0.5F);

                float yaw = entity.level().getRandom().nextFloat() * 180F;
                for(int i = 0; i < 3; i++) {
                    CompoundTag data = new CompoundTag();
                    data.putString("type", "plasmablast");
                    data.putFloat("r", 1.0F);
                    data.putFloat("g", 0.8F);
                    data.putFloat("b", 0.5F);
                    data.putFloat("pitch", -60F + 60F * i);
                    data.putFloat("yaw", yaw);
                    data.putFloat("scale", 2F);
                    PacketDispatcher.sendAuxParticleNT(
                            data,
                            entity.getX(),
                            entity.getY() + entity.getEyeHeight(),
                            entity.getZ(),
                            entity
                    );
                }
            }
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_FATMAN = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.FATMAN_FULL.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_LASRIFLE = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1.5F);
        }

        if(type == GunAnimation.RELOAD) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 18) SoundHelper.playSound(entity, ModSounds.RELOAD_IMPACT.get(), 0.25F, 1F);
            if(timer == 30) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 38) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }

        if(type == GunAnimation.INSPECT) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }

        if(type == GunAnimation.JAMMED) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 1F);
            if(timer == 22) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
            if(timer == 30) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_COILGUN = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, WEAPON_RELOAD_COILGUN.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_HANGMAN = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
        }

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.8F);
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 0.8F);
            if(timer == 25) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            if(timer == 35) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.75F);

            if(timer == 10) {
                Receiver rec = ctx.config.getReceivers(stack)[0];
                IMagazine mag = rec.getMagazine(stack);
                SpentCasing casing = Objects.requireNonNull(mag).getCasing(stack, ctx.inventory);
                if(casing != null) for(int i = 0; i < mag.getCapacity(stack); i++)
                    CasingCreator.composeEffect(entity.level(), entity, 0.25, -0.25, -0.125, -0.05, 0, 0, 0.01,
                            -6.5F + (float)entity.getRandom().nextGaussian() * 3F,
                            (float)entity.getRandom().nextGaussian() * 5F, casing.getName());
            }
        }

        if(type == GunAnimation.INSPECT) {
            if(timer == 16 && ctx.getPlayer() != null) {
                HitResult hitResult = EntityDamageUtil.getMouseOver(ctx.getPlayer(), 3.0D);
                if(hitResult != null) {
                    // Проверяем тип попадания
                    if(hitResult.getType() == HitResult.Type.ENTITY) {
                        EntityHitResult entityHit = (EntityHitResult) hitResult;
                        float damage = 10F;

                        // Наносим урон
                        entityHit.getEntity().hurt(
                                entity.damageSources().playerAttack(ctx.getPlayer()),
                                damage
                        );

                        // Увеличиваем движение
                        entityHit.getEntity().setDeltaMovement(
                                entityHit.getEntity().getDeltaMovement().x * 2,
                                entityHit.getEntity().getDeltaMovement().y,
                                entityHit.getEntity().getDeltaMovement().z * 2
                        );

                        SoundHelper.playSound((LivingEntity) entityHit.getEntity(), ModSounds.SMACK.get(), 1F,
                                0.9F + entity.getRandom().nextFloat() * 0.2F);
                    }

                    if(hitResult.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult blockHit = (BlockHitResult) hitResult;
                        net.minecraft.world.level.block.state.BlockState state =
                                entity.level().getBlockState(blockHit.getBlockPos());

                        // Проигрываем звук шага блока
                        entity.level().playSound(
                                null,
                                blockHit.getLocation().x(),
                                blockHit.getLocation().y(),
                                blockHit.getLocation().z(),
                                state.getSoundType().getStepSound(),
                                entity.getSoundSource(),
                                2F,
                                0.9F + entity.getRandom().nextFloat() * 0.2F
                        );
                    }
                }
            }
        }

        if(type == GunAnimation.JAMMED) {
            if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.8F);
            if(timer == 15) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 0.8F);
            if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
            if(timer == 25) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.75F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_BOLTER = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.CYCLE) {
            if(timer == 1) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.5, aiming ? 0 : -0.125, aiming ? -0.0625 : -0.25D, 0, 0.18, -0.12, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 5F, 10F + entity.getRandom().nextFloat() * 10F, casing.getName());
            }
        }

        if(type == GunAnimation.RELOAD) {
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGREMOVE.get(), 1F, 1F);
            if(timer == 26) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGINSERT.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_FOLLY = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 20) SoundHelper.playSound(entity, ModSounds.SCREW.get(), 1F, 1F);
            if(timer == 80) SoundHelper.playSound(entity, ModSounds.INSERT_ROCKET.get(), 1F, 1F);
            if(timer == 120) SoundHelper.playSound(entity, ModSounds.SCREW.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_DOUBLE_BARREL = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.75F);
            if(timer == 19) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 0.9F);
            if(timer == 29) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.8F);

            if(timer == 12) {
                IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
                int toEject = Objects.requireNonNull(mag).getAmountAfterReload(stack) - mag.getAmount(stack, ctx.inventory);
                SpentCasing casing = mag.getCasing(stack, ctx.inventory);
                if(casing != null) for(int i = 0; i < toEject; i++) CasingCreator.composeEffect(entity.level(), entity, 0, -0.1875, -0.375D, -0.24, 0.18, 0, 0.01, -20F + (float)entity.getRandom().nextGaussian() * 5F, (float)entity.getRandom().nextGaussian() * 2.5F, casing.getName(), true, 60, 0.5D, 20);
            }
        }

        if(type == GunAnimation.INSPECT) {
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCOCK.get(), 1F, 0.75F);
            if(timer == 19) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 0.8F);
        }

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 2) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_ABERRATOR = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack);

        if(type == GunAnimation.RELOAD) {
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLREMOVE.get(), 1F, 0.75F);
            if(timer == 32) SoundHelper.playSound(entity, ModSounds.RELOAD_MAGSMALLINSERT.get(), 1F, 0.75F);
            if(timer == 42) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.75F);
        }

        if(type == GunAnimation.CYCLE) {
            if(timer == 1) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity, 0.5, aiming ? 0 : -0.125, aiming ? -0.0625 : -0.25D, -0.05, 0.25, -0.05, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 10F, (float)entity.getRandom().nextGaussian() * 12.5F, casing.getName());
            }
        }

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 1) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.75F);
            if(timer == 9) SoundHelper.playSound(entity, ModSounds.RELOAD_PISTOLCOCK.get(), 1F, 0.75F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MAS36 = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        boolean aiming = GunItem.getIsAiming(stack) && !WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SCOPE);

        if(type == GunAnimation.EQUIP) {
            if(timer == 10) SoundHelper.playSound(entity, ModSounds.RELOAD_OPENLATCH.get(), 1F, 1F);
            if(timer == 18) SoundHelper.playSound(entity, ModSounds.RELOAD_REVOLVERCLOSE.get(), 1F, 1F);
        }

        if(type == GunAnimation.CYCLE) {
            if(timer == 7) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
            if(timer == 12) {
                SpentCasing casing = Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getCasing(stack, ctx.inventory);
                if(casing != null) CasingCreator.composeEffect(entity.level(), entity,
                        0.375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D,
                        -0.05, 0.2, -0.025,
                        0.01, -10F + (float) entity.getRandom().nextGaussian() * 10F, (float) entity.getRandom().nextGaussian() * 12.5F, casing.getName(), true, 60, 0.5D, 10);
            }
        }

        if(type == GunAnimation.CYCLE_DRY) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.75F);
            if(timer == 7) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
        }

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 1F, 1F);
            if(timer == 20) SoundHelper.playSound(entity, ModSounds.RELOAD_RIFLECOCK.get(), 1F, 1F);
            if(timer == 36) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 1F, 1F);
        }

        if(type == GunAnimation.JAMMED) {
            if(timer == 5) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 12) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
            if(timer == 16) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 23) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
        }

        if(type == GunAnimation.INSPECT) {
            if(timer == 0) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_OPEN.get(), 0.5F, 1F);
            if(timer == 17) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 0.5F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_FIREEXT = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.RELOAD) {
            if(timer == 0) SoundHelper.playSound(entity, WEAPON_RELOAD_PRESSUREVALVE.get(), 1F, 1F);
        }
    };

    public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_CHARGE_THROWER = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        if(entity.level().isClientSide()) return;
        GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(type == GunAnimation.CYCLE_DRY) {

            int entityId = GunChargeThrower.getLastHook(stack);
            Entity e = entity.level().getEntity(entityId);
            if(timer == 0 && e == null) SoundHelper.playSound(entity, ModSounds.RELOAD_DRYFIRECLICK.get(), 1F, 0.75F);
        }

        if(type == GunAnimation.RELOAD) {
            if(timer == 30) SoundHelper.playSound(entity, INSERT_ROCKET.get(), 1F, 1F);
            if(timer == 40) SoundHelper.playSound(entity, ModSounds.RELOAD_BOLT_CLOSE.get(), 1F, 1F);
        }
    };
}
