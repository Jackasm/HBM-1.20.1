package com.hbm.entity.mob;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.items.weapon.sedna.mags.MagazineSingleTypeBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

public class CustomSkeleton extends Skeleton {

    public int attackTicks = 0;

    public CustomSkeleton(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (attackTicks > 0) {
            attackTicks--;
        }
    }

    public void triggerAttackAnimation() {
        this.attackTicks = 20;
    }

    @Override
    protected void registerGoals() {
        // Сначала вызываем super, чтобы добавить все стандартные цели
        super.registerGoals();

        // Удаляем стандартную цель стрельбы из лука
        this.goalSelector.removeAllGoals(goal -> goal instanceof RangedBowAttackGoal);

        // Проверяем оружие и добавляем соответствующую цель
        if (this.getMainHandItem().getItem() instanceof GunItem) {
            this.goalSelector.addGoal(4, new RangedGunAttackGoal(this));
        } else {
            this.goalSelector.addGoal(4, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));
        }
    }

    @Override
    public void reassessWeaponGoal() {
        if (!this.level().isClientSide) {
            // Удаляем старую цель стрельбы
            this.goalSelector.removeAllGoals(goal ->
                    goal instanceof RangedBowAttackGoal ||
                            goal instanceof RangedGunAttackGoal
            );

            // Добавляем актуальную в зависимости от оружия
            if (this.getMainHandItem().getItem() instanceof GunItem) {
                this.goalSelector.addGoal(4, new RangedGunAttackGoal(this));
            } else {
                this.goalSelector.addGoal(4, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));
            }
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        ItemStack mainHand = this.getMainHandItem();
        if (mainHand.getItem() instanceof GunItem) {
            BulletConfig ammo = getFirstAmmoForGun(mainHand);
            setInfiniteAmmo(mainHand, ammo);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty,
                                        @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData groupData,
                                        @Nullable CompoundTag tag) {
        groupData = super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
        Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(35.0D);

        ItemStack mainHand = this.getMainHandItem();
        if (mainHand.getItem() instanceof GunItem) {
            BulletConfig ammo = getFirstAmmoForGun(mainHand);
            setInfiniteAmmo(mainHand, ammo);
        }

        return groupData;
    }

    private BulletConfig getFirstAmmoForGun(ItemStack gun) {
        if (!(gun.getItem() instanceof GunItem gunItem)) return null;

        GunConfig config = gunItem.getConfig(gun, 0);
        Receiver[] receivers = config.getReceivers(gun);
        if (receivers == null || receivers.length == 0) return null;

        IMagazine mag = receivers[0].getMagazine(gun);
        if (mag instanceof MagazineSingleTypeBase singleMag && !singleMag.acceptedBullets.isEmpty()) {
            return singleMag.acceptedBullets.get(0);
        }

        return null;
    }

    private void setInfiniteAmmo(ItemStack gun, BulletConfig ammoType) {
        CompoundTag nbt = gun.getOrCreateTag();
        nbt.putBoolean("infiniteMag", true);
        if (ammoType != null) {
            nbt.putInt("ammoTypeId", ammoType.id);
        }
    }

    @Override
    protected @NotNull SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    public static class RangedGunAttackGoal extends Goal {
        private final CustomSkeleton mob;
        private final double speedModifier;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public RangedGunAttackGoal(CustomSkeleton mob) {
            this.mob = mob;
            this.speedModifier = 1.0D;
            this.attackRadiusSqr = 15.0F * 15.0F;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive() && isHoldingGun();
        }

        private boolean isHoldingGun() {
            return this.mob.getMainHandItem().getItem() instanceof GunItem;
        }

        @Override
        public boolean canContinueToUse() {
            return (canUse() || !this.mob.getNavigation().isDone()) && isHoldingGun();
        }

        @Override
        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
            this.seeTime = 0;
            this.attackTime = -1;
            this.strafingTime = -1;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target == null) return;

            double distSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(target);

            if (hasLineOfSight != (this.seeTime > 0)) {
                this.seeTime = 0;
            }

            if (hasLineOfSight) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(distSqr > (double) this.attackRadiusSqr) && this.seeTime >= 20) {
                this.mob.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.mob.getNavigation().moveTo(target, this.speedModifier);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if (this.mob.getRandom().nextFloat() < 0.3F) {
                    this.strafingClockwise = !this.strafingClockwise;
                }
                if (this.mob.getRandom().nextFloat() < 0.3F) {
                    this.strafingBackwards = !this.strafingBackwards;
                }
                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (distSqr > (double) (this.attackRadiusSqr * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (distSqr < (double) (this.attackRadiusSqr * 0.25F)) {
                    this.strafingBackwards = true;
                }
                this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.mob.lookAt(target, 30.0F, 30.0F);
            } else {
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            if (--this.attackTime <= 0) {
                if (this.seeTime >= 20) {
                    performRangedAttack(target);
                    // Получаем задержку из ресивера
                    ItemStack gunStack = this.mob.getMainHandItem();
                    if (gunStack.getItem() instanceof GunItem gun) {
                        GunConfig config = gun.getConfig(gunStack, 0);
                        Receiver receiver = config.getReceivers(gunStack)[0];
                        int delay = receiver.getDelayAfterFire(gunStack);
                        this.attackTime = Math.max(delay, 2); // минимум 2 тика, чтобы не стрелять каждый тик
                    } else {
                        this.attackTime = 20; // fallback
                    }
                } else {
                    this.attackTime = 5;
                }
            }
        }

        private void performRangedAttack(LivingEntity target) {
            ItemStack gunStack = this.mob.getMainHandItem();
            if (!(gunStack.getItem() instanceof GunItem gun)) return;

            GunConfig config = gun.getConfig(gunStack, 0);
            Receiver[] receivers = config.getReceivers(gunStack);
            if (receivers == null || receivers.length == 0) return;

            Receiver receiver = receivers[0];
            IMagazine mag = receiver.getMagazine(gunStack);
            if (mag == null) return;

            Container dummyInv = new SimpleContainer(0);
            Object typeObj = mag.getType(gunStack, dummyInv);
            if (!(typeObj instanceof BulletConfig bulletConfig)) return;

            float damage = receiver.getBaseDamage(gunStack);
            float spread = receiver.getInnateSpread(gunStack);
            if (!GunItem.getIsAiming(gunStack)) {
                spread += receiver.getHipfireSpread(gunStack);
            }
            spread *= 5;

            Vec3 from = this.mob.getEyePosition();
            Vec3 to = target.getEyePosition();
            Vec3 direction = to.subtract(from).normalize();

            EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(
                    this.mob.level(), this.mob, bulletConfig, damage, spread, 0, 0, 0
            );
            bullet.setPos(from.x, from.y, from.z);
            bullet.setDeltaMovement(direction.scale(bulletConfig.velocity));

            double randomYaw = (this.mob.getRandom().nextDouble() - 0.5) * spread * 2;
            double randomPitch = (this.mob.getRandom().nextDouble() - 0.5) * spread * 2;
            bullet.setDeltaMovement(bullet.getDeltaMovement().yRot((float) randomYaw).xRot((float) randomPitch));

            this.mob.level().addFreshEntity(bullet);
            this.mob.triggerAttackAnimation();

            SoundEvent sound = receiver.getFireSound(gunStack);
            if (sound != null) {
                this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                        sound, this.mob.getSoundSource(), receiver.getFireVolume(gunStack), receiver.getFirePitch(gunStack));
            }
        }
    }
}