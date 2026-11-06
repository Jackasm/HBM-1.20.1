package com.hbm.entity.mob;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class EntityUndeadSoldier extends Monster {

    public static final EntityDataAccessor<Byte> DW_TYPE = SynchedEntityData.defineId(EntityUndeadSoldier.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Integer> ATTACK_TICKS = SynchedEntityData.defineId(EntityUndeadSoldier.class, EntityDataSerializers.INT);
    public static final byte TYPE_ZOMBIE = 0;
    public static final byte TYPE_SKELETON = 1;

    public int attackTicks = 0;

    public EntityUndeadSoldier(EntityType<? extends EntityUndeadSoldier> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MAX_HEALTH, 20.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (attackTicks > 0) {
            attackTicks--;
            if (attackTicks == 0) {
                this.entityData.set(ATTACK_TICKS, 0);
            }
        }
    }

    public void reassessWeaponGoal() {
        if (this.level().isClientSide) return;

        this.goalSelector.removeAllGoals(goal ->
                goal instanceof RangedGunAttackGoal ||
                        goal instanceof MeleeAttackGoal
        );

        if (this.getMainHandItem().getItem() instanceof GunItem) {
            this.goalSelector.addGoal(2, new RangedGunAttackGoal(this));
        } else {
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // После загрузки данных восстанавливаем цели
        if (!this.level().isClientSide) {
            this.reassessWeaponGoal();
            this.setAggressive(true);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DW_TYPE, (byte) 0);
        this.entityData.define(ATTACK_TICKS, 0);
    }

    public void triggerAttackAnimation() {
        this.attackTicks = 20;
        this.entityData.set(ATTACK_TICKS, 20);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficultyInstance, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag) {
        spawnData = super.finalizeSpawn(level, difficultyInstance, spawnType, spawnData, tag);
        this.addRandomArmor();
        this.entityData.set(DW_TYPE, random.nextBoolean() ? TYPE_ZOMBIE : TYPE_SKELETON);
        this.reassessWeaponGoal();
        this.setAggressive(true);
        return spawnData;
    }

    public void setType(byte type) {
        this.entityData.set(DW_TYPE, type);
    }

    public void addRandomArmor() {
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModArmorItems.TAURUN_HELMET.get()));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModArmorItems.TAURUN_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModArmorItems.TAURUN_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModArmorItems.TAURUN_BOOTS.get()));

        int gun = random.nextInt(5);
        if (gun == 0) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModGunItems.GUN_HEAVY_REVOLVER.get()));
        if (gun == 1) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModGunItems.GUN_LIGHT_REVOLVER.get()));
        if (gun == 2) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModGunItems.GUN_CARBINE.get()));
        if (gun == 3) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModGunItems.GUN_MARESLEG.get()));
        if (gun == 4) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModGunItems.GUN_GREASEGUN.get()));
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        byte type = this.entityData.get(DW_TYPE);
        if (type == TYPE_ZOMBIE) return SoundEvents.ZOMBIE_AMBIENT;
        if (type == TYPE_SKELETON) return SoundEvents.SKELETON_AMBIENT;
        return super.getAmbientSound();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource source) {
        byte type = this.entityData.get(DW_TYPE);
        if (type == TYPE_ZOMBIE) return SoundEvents.ZOMBIE_HURT;
        if (type == TYPE_SKELETON) return SoundEvents.SKELETON_HURT;
        return super.getHurtSound(source);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        byte type = this.entityData.get(DW_TYPE);
        if (type == TYPE_ZOMBIE) return SoundEvents.ZOMBIE_DEATH;
        if (type == TYPE_SKELETON) return SoundEvents.SKELETON_DEATH;
        return super.getDeathSound();
    }

    @Override
    protected void playStepSound(@NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.level.block.state.BlockState state) {
        byte type = this.entityData.get(DW_TYPE);
        if (type == TYPE_ZOMBIE) this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
        if (type == TYPE_SKELETON) this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor level, @NotNull MobSpawnType spawnType) {
        return level.getDifficulty() != Difficulty.PEACEFUL &&
                level.isUnobstructed(this) &&
                !level.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        // Не дропаем ничего
    }

    @Override
    protected void dropEquipment() {
        // Не дропаем экипировку
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    public static class RangedGunAttackGoal extends Goal {
        private final EntityUndeadSoldier mob;
        private final double speedModifier;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public RangedGunAttackGoal(EntityUndeadSoldier mob) {
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
                    // Задержка из ресивера
                    ItemStack gunStack = this.mob.getMainHandItem();
                    if (gunStack.getItem() instanceof GunItem gun) {
                        GunConfig config = gun.getConfig(gunStack, 0);
                        Receiver[] receivers = config.getReceivers(gunStack);
                        if (receivers != null && receivers.length > 0) {
                            Receiver receiver = receivers[0];
                            int delay = receiver.getDelayAfterFire(gunStack);
                            this.attackTime = Math.max(delay, 2);
                        } else {
                            this.attackTime = 20;
                        }
                    } else {
                        this.attackTime = 20;
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