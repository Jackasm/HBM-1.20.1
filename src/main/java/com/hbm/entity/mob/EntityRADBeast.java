package com.hbm.entity.mob;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.entity.ModEntities;
import com.hbm.extprop.IRadiationImmune;
import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.items.ModItems;
import com.hbm.sound.ModSounds;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class EntityRADBeast extends Monster implements IRadiationImmune {

    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(EntityRADBeast.class, EntityDataSerializers.INT);

    private float heightOffset = 0.5F;
    private int heightOffsetUpdateTime;

    public EntityRADBeast(EntityType<? extends EntityRADBeast> type, Level world) {
        super(type, world);
        this.setInvulnerable(true);
        this.xpReward = 30;
    }

    public EntityRADBeast(Level world) {
        this(ModEntities.RADBEAST.get(), world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    public EntityRADBeast makeLeader() {
        this.setDropChance(EquipmentSlot.MAINHAND, 1.0F);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.COIN_RADIATION.get()));
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(360.0D);
        this.setHealth(this.getMaxHealth());
        return this;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_ID, 0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GEIGER_1.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.BLAZE_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DAMAGE;
    }

    @Override
    public int getArmorValue() {
        return 8;
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide) {
            if (this.isInWater()) {
                this.hurt(this.damageSources().drown(), 1.0F);
            }

            --this.heightOffsetUpdateTime;
            if (this.heightOffsetUpdateTime <= 0) {
                this.heightOffsetUpdateTime = 100;
                this.heightOffset = 0.5F + (float) this.random.nextGaussian() * 3.0F;
            }

            LivingEntity target = this.getTarget();
            if (target != null && target.getY() + target.getEyeHeight() > this.getY() + this.getEyeHeight() + this.heightOffset) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.3, 0));
            }

            if (target != null && this.hurtTime < 10) {
                this.entityData.set(TARGET_ID, target.getId());
            } else {
                this.entityData.set(TARGET_ID, 0);
            }
        }

        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.6, 1));
        }

        // Частицы
        if (this.getMaxHealth() <= 150) {
            for (int i = 0; i < 6; i++) {
                this.level().addParticle(ParticleTypes.END_ROD,
                        this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 1.5,
                        this.getY() + this.random.nextDouble() * this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 1.5,
                        0.0D, 0.0D, 0.0D);
            }
            if (this.random.nextInt(6) == 0) {
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * this.getBbHeight() * 0.75,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        0.0D, 0.0D, 0.0D);
            }
        } else {
            this.level().addParticle(ParticleTypes.LAVA,
                    this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                    this.getY() + this.random.nextDouble() * this.getBbHeight() * 0.75,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                    0.0D, 0.0D, 0.0D);
        }

        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = this.getTarget();
        if (target != null) {
            double dist = this.distanceTo(target);

            if (this.hurtTime <= 0 && dist < 2.0F &&
                    target.getBoundingBox().maxY > this.getBoundingBox().minY &&
                    target.getBoundingBox().minY < this.getBoundingBox().maxY) {
                this.hurtTime = 20;
                this.doHurtTarget(target);
            } else if (dist < 30.0F) {
                double deltaX = target.getX() - this.getX();
                double deltaZ = target.getZ() - this.getZ();

                if (this.hurtTime == 0) {
                    RadiationEvents.incrementRadiation(this.level(),
                            BlockPos.containing(this.getX(), this.getY(), this.getZ()), 100);
                    target.hurt(ModDamageSource.causeRadiation(this.level(), this, this), 16.0F);
                    this.swing(this.getUsedItemHand());
                    this.playAmbientSound();
                    this.hurtTime = 20;
                }

                this.setYRot((float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F);
                this.hurtTime = 20;
            }
        }
    }

    public Entity getUnfortunateSoul() {
        int id = this.entityData.get(TARGET_ID);
        return this.level().getEntity(id);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
        // Игнорируем урон от падения
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);

        if (this.getMaxHealth() > 150) {
            // Достижения для всех игроков в радиусе 50 блоков
            if (!this.level().isClientSide) {
                AABB box = this.getBoundingBox().inflate(50, 50, 50);
                List<Player> players = this.level().getEntitiesOfClass(Player.class, box);

                for (Player player : players) {
                    if (player instanceof ServerPlayer sp) {
                        ModCriteriaTriggers.BOSS_MELTDOWN.trigger(sp);
                    }
                }
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        if (looting > 0) {
            this.spawnAtLocation(ModItems.NUGGET_POLONIUM.get(), looting);
        }

        int count = this.random.nextInt(3) + 1;
        for (int i = 0; i < count; i++) {
            int r = this.random.nextInt(3);
            if (r == 0) {
                this.spawnAtLocation(this.isInWater() ? ModItems.WASTE_URANIUM.get() : ModItems.ROD_ZIRNOX_URANIUM_FUEL_DEPLETED.get(),
                        this.isInWater() ? 2 : 1);
            } else if (r == 1) {
                this.spawnAtLocation(this.isInWater() ? ModItems.WASTE_MOX.get() : ModItems.ROD_ZIRNOX_MOX_FUEL_DEPLETED.get(),
                        this.isInWater() ? 2 : 1);
            } else if (r == 2) {
                this.spawnAtLocation(this.isInWater() ? ModItems.WASTE_PLUTONIUM.get() : ModItems.ROD_ZIRNOX_PLUTONIUM_FUEL_DEPLETED.get(),
                        this.isInWater() ? 2 : 1);
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}