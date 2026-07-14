package com.hbm.entity.mob;

import com.hbm.entity.projectile.EntityBullet;
import com.hbm.extprop.IRadiationImmune;
import com.hbm.util.ModDamageSource;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class EntityCyberCrab extends Monster implements RangedAttackMob, IRadiationImmune {

    private static final Predicate<LivingEntity> SELECTOR = (entity) ->
            !(entity instanceof EntityCyberCrab || entity instanceof Creeper);

    public EntityCyberCrab(EntityType<? extends EntityCyberCrab> type, Level level) {
        super(type, level);
        this.getNavigation().setCanFloat(false);
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        if (!(this instanceof EntityTaintCrab)) {
            this.goalSelector.addGoal(0, new PanicGoal(this, 0.75D));
        }

        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.5F));
        this.goalSelector.addGoal(4, createArrowAttackGoal());

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, true, true, SELECTOR));
    }

    protected RangedAttackGoal createArrowAttackGoal() {
        return new RangedAttackGoal(this, 0.5D, 60, 80, 15.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.75D)
                .add(Attributes.FOLLOW_RANGE, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (ModDamageSource.getIsTau(source)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isInWater() || this.isInWaterOrRain() || this.isOnFire()) {
            this.hurt(this.damageSources().generic(), 10F);
        }

        if (this.getHealth() <= 0) {
            this.discard();

            if (this instanceof EntityTaintCrab) {
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3F, Level.ExplosionInteraction.MOB);
            } else {
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.1F, Level.ExplosionInteraction.MOB);
            }
        }
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR; // Замени на свой звук
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR; // Замени на свой звук
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        return true;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        // Используем кастомную пулю
        EntityBullet bullet = new EntityBullet(level(), this, 1.6F, 5, 10, false, false);

        // Устанавливаем позицию
        bullet.setPos(this.getX(), this.getEyeY(), this.getZ());

        // Вычисляем направление
        double dX = target.getX() - this.getX();
        double dY = target.getY(0.333D) - bullet.getY();
        double dZ = target.getZ() - this.getZ();
        double d = Math.sqrt(dX * dX + dZ * dZ);

        // Стреляем
        bullet.shoot(dX, dY + d * 0.2F, dZ, 1.6F, 2F);

        // Настройка параметров пули
        bullet.setCritical(true);
        bullet.setDamage(3);

        // Добавляем в мир
        this.level().addFreshEntity(bullet);
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 2.0F);
    }
}