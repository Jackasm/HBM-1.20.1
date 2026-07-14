package com.hbm.items.weapon.sedna.factory;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.ExplosionEffectTiny;
import com.hbm.explosion.vanillant.standard.ExplosionEffectWeapon;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.BulletConfig.ProjectileType;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.GunItem.GunState;
import com.hbm.items.weapon.sedna.GunItem.LambdaContext;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.particle.helper.BlackPowderCreator;
import com.hbm.render.anim.AnimationEnums.GunAnimation;

import com.hbm.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;

public class Lego {

    public static final Random ANIM_RAND = new Random();

    /**
     * If IDLE and the mag of receiver 0 can be loaded, set state to RELOADING. Used by keybinds. */
    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_RELOAD = (stack, ctx) -> {

        Player player = ctx.getPlayer();
        Receiver rec = ctx.config.getReceivers(stack)[0];
        GunState state = GunItem.getState(stack, ctx.configIndex);

        if(state == GunState.IDLE) {

            GunItem.setIsAiming(stack, false);
            IMagazine mag = rec.getMagazine(stack);

            if(Objects.requireNonNull(mag).canReload(stack, ctx.inventory)) {
                int loaded = mag.getAmount(stack, ctx.inventory);
                mag.setAmountBeforeReload(stack, loaded);
                GunItem.setState(stack, ctx.configIndex, GunState.RELOADING);
                GunItem.setTimer(stack, ctx.configIndex, rec.getReloadBeginDuration(stack) + (loaded <= 0 ? rec.getReloadCockOnEmptyPre(stack) : 0));
                GunItem.playAnimation(player, stack, GunAnimation.RELOAD, ctx.configIndex);
                if(ctx.config.getReloadChangesType(stack)) mag.initNewType(stack, ctx.inventory);
            } else {
                GunItem.playAnimation(player, stack, GunAnimation.INSPECT, ctx.configIndex);
                if(!ctx.config.getInspectCancel(stack)) {
                    GunItem.setState(stack, ctx.configIndex, GunState.DRAWING);
                    GunItem.setTimer(stack, ctx.configIndex, ctx.config.getInspectDuration(stack));
                }
            }
        }
    };

    /** If IDLE and ammo is loaded, fire and set to JUST_FIRED. */
    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_CLICK_PRIMARY = (stack, ctx) -> clickReceiver(stack, ctx, 0);

    public static void clickReceiver(ItemStack stack, LambdaContext ctx, int receiver) {
        LivingEntity entity = ctx.entity;
        Player player = ctx.getPlayer();
        Receiver rec = ctx.config.getReceivers(stack)[receiver];
        int index = ctx.configIndex;
        GunState state = GunItem.getState(stack, index);

        if(state == GunState.IDLE) {

            if(Objects.requireNonNull(rec.getCanFire(stack)).apply(stack, ctx)) {
                Objects.requireNonNull(rec.getOnFire(stack)).accept(stack, ctx);

                SoundEvent sound = rec.getFireSound(stack);
                if(sound != null) {
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            sound, entity.getSoundSource(), rec.getFireVolume(stack), rec.getFirePitch(stack));
                }

                int remaining = rec.getRoundsPerCycle(stack) - 1;
                for(int i = 0; i < remaining; i++) if(Objects.requireNonNull(rec.getCanFire(stack)).apply(stack, ctx)) Objects.requireNonNull(rec.getOnFire(stack)).accept(stack, ctx);

                GunItem.setState(stack, index, GunState.COOLDOWN);
                GunItem.setTimer(stack, index, rec.getDelayAfterFire(stack));
            } else {

                if(rec.getDoesDryFire(stack)) {
                    GunItem.playAnimation(player, stack, GunAnimation.CYCLE_DRY, index);
                    GunItem.setState(stack, index, rec.getRefireAfterDry(stack) ? GunState.COOLDOWN : GunState.DRAWING);
                    GunItem.setTimer(stack, index, rec.getDelayAfterDryFire(stack));
                }
            }
        }

        if(state == GunState.RELOADING) {
            GunItem.setReloadCancel(stack, true);
        }
    }

    /** If IDLE, switch mode between 0 and 1. */
    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_CLICK_SECONDARY = (stack, ctx) -> {

        LivingEntity entity = ctx.entity;
        int index = ctx.configIndex;
        GunState state = GunItem.getState(stack, index);

        if(state == GunState.IDLE) {
            int mode = GunItem.getMode(stack, 0);
            GunItem.setMode(stack, index, 1 - mode);
            SoundEvent sound1 = ModSounds.getCachedSound("hbm:weapon.switchmode1");
            SoundEvent sound2 = ModSounds.getCachedSound("hbm:weapon.switchmode2");
            if(mode == 0 && sound1 != null) {
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        sound1, entity.getSoundSource(), 1F, 1F);
            } else if(sound2 != null) {
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        sound2, entity.getSoundSource(), 1F, 1F);
            }
        }
    };

    /** Default smoke. */
    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_SMOKE = (stack, ctx) ->
            handleStandardSmoke(ctx.entity, stack, 2000, 0.025D, 1.15D, ctx.configIndex);

    public static void handleStandardSmoke(LivingEntity entity, ItemStack stack, int smokeDuration, double alphaDecay, double widthGrowth, int index) {
        GunItem gun = (GunItem) stack.getItem();
        long lastShot = gun.lastShot[index];

        // Используем GunConfig.SmokeNode
        List<GunItem.SmokeNode> smokeNodes = gun.getConfig(stack, index).smokeNodes;

        boolean smoking = lastShot + smokeDuration > System.currentTimeMillis();
        if(!smoking && !smokeNodes.isEmpty()) smokeNodes.clear();


        if(smoking) {
            Vec3 prev = new Vec3(-entity.getDeltaMovement().x, -entity.getDeltaMovement().y, -entity.getDeltaMovement().z);
            prev = prev.yRot((float) (entity.getYRot() * Math.PI / 180D));
            double accel = 15D;
            double side = (entity.getYRot() - entity.yHeadRotO) * 0.1D;
            double waggle = 0.025D;

            for(GunItem.SmokeNode node : smokeNodes) {
                node.forward += -prev.z * accel + entity.level().random.nextGaussian() * waggle;
                node.lift += prev.y + 1.5D;
                node.side += prev.x * accel + entity.level().random.nextGaussian() * waggle + side;
                if(node.alpha > 0) node.alpha -= alphaDecay;
                node.width *= widthGrowth;
            }

            double alpha = (System.currentTimeMillis() - lastShot) / (double) smokeDuration;
            alpha = (1 - alpha) * 0.5D;

            if(gun.getState(stack, index) == GunItem.GunState.RELOADING || smokeNodes.isEmpty()) alpha = 0;
            smokeNodes.removeIf(node -> node.alpha <= 0.001);
            // Создаем новый SmokeNode из GunConfig
            smokeNodes.add(new GunItem.SmokeNode(alpha));
        }
    }

    /** Toggles isAiming. Used by keybinds. */
    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_TOGGLE_AIM = (stack, ctx) -> GunItem.setIsAiming(stack, !GunItem.getIsAiming(stack));

    /** Returns true if the mag has ammo in it. Used by keybind functions on whether to fire, and deciders on whether to trigger a refire. */
    public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_STANDARD_CAN_FIRE = (stack, ctx) -> Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, ctx.inventory) > 0;

    /** Returns true if the mag has ammo in it, and the gun is in the locked on state */
    public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_LOCKON_CAN_FIRE = (stack, ctx) -> Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, ctx.inventory) > 0 && GunItem.getIsLockedOn(stack);

    /** JUMPER - bypasses mag testing and just allows constant fire */
    public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_DEBUG_CAN_FIRE = (stack, ctx) -> true;

    /** Spawns an EntityBulletBaseMK4 with the loaded bulletcfg */
    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_FIRE = (stack, ctx) -> doStandardFire(stack, ctx, GunAnimation.CYCLE, true);

    /** Spawns an EntityBulletBaseMK4 with the loaded bulletcfg, ignores wear */
    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_NOWEAR_FIRE = (stack, ctx) -> doStandardFire(stack, ctx, GunAnimation.CYCLE, false);

    /** Spawns an EntityBulletBaseMK4 with the loaded bulletcfg, then resets lockon progress */
    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_LOCKON_FIRE = (stack, ctx) -> {
        doStandardFire(stack, ctx, GunAnimation.CYCLE, true);
        GunItem.setIsLockedOn(stack, false);
    };

    public static void doStandardFire(ItemStack stack, LambdaContext ctx, GunAnimation anim, boolean calcWear) {
        LivingEntity entity = ctx.entity;
        Player player = ctx.getPlayer();
        int index = ctx.configIndex;
        long shotTime = System.currentTimeMillis();

        if(anim != null) GunItem.playAnimation(Objects.requireNonNull(player), stack, anim, ctx.configIndex, true, shotTime);

        boolean aim = GunItem.getIsAiming(stack);
        Receiver primary = ctx.config.getReceivers(stack)[0];
        IMagazine mag = primary.getMagazine(stack);

        if(mag == null) return;

        Object typeObj = mag.getType(stack, ctx.inventory);
        if(!(typeObj instanceof BulletConfig config)) return;

        if(config.blackPowder && !entity.level().isClientSide()) {
            BlackPowderCreator.doEffect(entity);
        }

        Vec3 offset = GunItem.getIsAiming(stack) ? primary.getProjectileOffsetScoped(stack) : primary.getProjectileOffset(stack);
        double sideOffset = offset.x;
        double heightOffset = offset.y;
        double forwardOffset = offset.z;

        int projectiles = config.projectilesMin;
        if(config.projectilesMax > config.projectilesMin) projectiles += entity.getRandom().nextInt(config.projectilesMax - config.projectilesMin + 1);
        projectiles = (int) (projectiles * primary.getSplitProjectiles(stack));

        for(int i = 0; i < projectiles; i++) {
            float damage = calcDamage(ctx, stack, primary, calcWear, index);
            float spread = calcSpread(ctx, stack, primary, config, calcWear, index, aim);

            if(config.pType == ProjectileType.BULLET) {
                EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(entity.level(), entity, config, damage, spread, sideOffset, heightOffset, forwardOffset);
                if(GunItem.getIsLockedOn(stack)) bullet.setLockonTarget(entity.level().getEntity(GunItem.getLockonTarget(stack)));

                entity.level().addFreshEntity(bullet);
            } else if(config.pType == ProjectileType.BULLET_CHUNKLOADING) {
                // Вам нужно будет создать аналогичный класс для чанклоадинга
                // EntityBulletBaseMK4CL bullet = new EntityBulletBaseMK4CL(entity, config, damage, spread, sideOffset, heightOffset, forwardOffset);
                // if(GunItem.getIsLockedOn(stack)) bullet.lockonTarget = entity.level().getEntity(GunItem.getLockonTarget(stack));
                // entity.level().addFreshEntity(bullet);
            } else if(config.pType == ProjectileType.BEAM) {
                EntityBulletBeamBase beam = new EntityBulletBeamBase(
                        entity.level(),
                        entity,
                        config,
                        damage,
                        spread,
                        sideOffset,
                        heightOffset,
                        forwardOffset
                );
                entity.level().addFreshEntity(beam);
            }
        }

        //if(player != null) player.awardStat(MainRegistry.statBullets, 1);
        mag.useUpAmmo(stack, ctx.inventory, 1);

        if(calcWear) {
            float currentWear = GunItem.getWear(stack, index);
            float maxDura = ctx.config.getDurability(stack);
            float newWear = currentWear + config.wear;

            if(newWear >= maxDura) {
                // Сломали оружие ПОСЛЕ выстрела
                GunItem.setWear(stack, index, maxDura);

                // Уничтожаем на следующем тике
                if(ctx.entity instanceof Player) {
                    scheduleGunBreak(player, stack);
                }
            } else {
                GunItem.setWear(stack, index, newWear);
            }
        }

    }

    private static void scheduleGunBreak(Player player, ItemStack stack) {
        // Устанавливаем флаг для уничтожения в inventoryTick
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("shouldBreak", true);
    }

    public static float getStandardWearSpread(ItemStack stack, GunConfig config, int index) {
        float percent = GunItem.getWear(stack, index) / config.getDurability(stack);
        if(percent < 0.5F) return 0F;
        return (percent - 0.5F) * 2F;
    }

    /** Returns the standard multiplier for damage based on wear */
    public static float getStandardWearDamage(ItemStack stack, GunConfig config, int index) {
        float percent = GunItem.getWear(stack, index) / config.getDurability(stack);
        if(percent < 0.75F) return 1F;
        return 1F - (percent - 0.75F) * 2F;
    }

    /** Returns the full calculated damage based on guncfg and wear */
    public static float calcDamage(LambdaContext ctx, ItemStack stack, Receiver primary, boolean calcWear, int index) {
        return primary.getBaseDamage(stack) * (calcWear ? getStandardWearDamage(stack, ctx.config, index) : 1);
    }

    public static float calcSpread(LambdaContext ctx, ItemStack stack, Receiver primary, BulletConfig config, boolean calcWear, int index, boolean aim) {
        float spreadInnate = primary.getInnateSpread(stack);
        float spreadAmmo = config.spread * primary.getAmmoSpread(stack);
        float spreadHipfire = aim ? 0F : primary.getHipfireSpread(stack);
        float spreadWear = !calcWear ? 0F : (getStandardWearSpread(stack, ctx.config, index) * primary.getDurabilitySpread(stack));

        return spreadInnate + spreadAmmo + spreadHipfire + spreadWear;
    }

    public static void standardExplode(EntityBulletBaseMK4 bullet, HitResult mop, float range) {
        standardExplode(bullet, mop, range, 1F);
    }

    public static void standardExplode(EntityBulletBaseMK4 bullet, HitResult mop, float range, float damageMod) {
        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), mop.getLocation().x, mop.getLocation().y, mop.getLocation().z, range, bullet.getOwner());
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.getDamage() * damageMod)
                .setupPiercing(bullet.getConfig().armorThresholdNegation, bullet.getConfig().armorPiercingPercent));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
        vnt.explode();
    }

    public static void tinyExplode(EntityBulletBaseMK4 bullet, HitResult mop, float range) {
        tinyExplode(bullet, mop, range, 1F);
    }

    public static void tinyExplode(EntityBulletBaseMK4 bullet, HitResult mop, float range, float damageMod) {
        Direction dir = Direction.UP; // Значение по умолчанию

        if(mop.getType() == HitResult.Type.BLOCK && mop instanceof BlockHitResult blockHit) {
            dir = blockHit.getDirection();
        }

        double x = mop.getLocation().x + dir.getStepX() * 0.25D;
        double y = mop.getLocation().y + dir.getStepY() * 0.25D;
        double z = mop.getLocation().z + dir.getStepZ() * 0.25D;

        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), x, y, z, range, bullet.getOwner());
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.getDamage() * damageMod)
                .setupPiercing(bullet.getConfig().armorThresholdNegation, bullet.getConfig().armorPiercingPercent)
                .setKnockback(0.25D));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.setSFX(new ExplosionEffectTiny());
        vnt.explode();
    }

}