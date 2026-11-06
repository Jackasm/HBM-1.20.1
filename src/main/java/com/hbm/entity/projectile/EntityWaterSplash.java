package com.hbm.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

public class EntityWaterSplash extends ThrowableProjectile {

    private int maxAge = 100;

    public EntityWaterSplash(EntityType<? extends EntityWaterSplash> entityType, Level level) {
        super(entityType, level);
    }

    // Правильный порядок: EntityType, LivingEntity, Level
    public EntityWaterSplash(EntityType<? extends EntityWaterSplash> entityType, LivingEntity thrower, Level level) {
        super(entityType, thrower, level);
    }

    // Правильный порядок: EntityType, double, double, double, Level
    public EntityWaterSplash(EntityType<? extends EntityWaterSplash> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    // Конструктор для спавна через Forge Network
    public EntityWaterSplash(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType<? extends EntityWaterSplash>) BuiltInRegistries.ENTITY_TYPE.byId(spawnEntity.getTypeId()), level);
    }

    public void setMaxAge(int age) {
        this.maxAge = age;
    }

    @Override
    protected void defineSynchedData() {
        // Никаких синхронизируемых данных
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (this.tickCount > maxAge) {
                this.discard();
            }
        } else {
            spawnWaterSplashParticles();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnWaterSplashParticles() {
        for (int i = 0; i < 5; i++) {
            double x = this.getX() + (random.nextDouble() - 0.5) * 0.5;
            double y = this.getY() + random.nextDouble() * 0.5;
            double z = this.getZ() + (random.nextDouble() - 0.5) * 0.5;
            this.level().addParticle(ParticleTypes.SPLASH, x, y, z,
                    (random.nextDouble() - 0.5) * 0.2,
                    random.nextDouble() * 0.2,
                    (random.nextDouble() - 0.5) * 0.2);
        }

        if (this.tickCount % 3 == 0) {
            this.level().addParticle(ParticleTypes.CLOUD,
                    this.getX() + (random.nextDouble() - 0.5) * 0.3,
                    this.getY() + random.nextDouble() * 0.3,
                    this.getZ() + (random.nextDouble() - 0.5) * 0.3,
                    0.0, 0.02, 0.0);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (this.tickCount > 5) {
            this.level().addParticle(ParticleTypes.SPLASH,
                    this.getX(), this.getY(), this.getZ(),
                    0.0, 0.0, 0.0);

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(ParticleTypes.SPLASH,
                        this.getX() + (random.nextDouble() - 0.5) * 0.8,
                        this.getY() + random.nextDouble() * 0.5,
                        this.getZ() + (random.nextDouble() - 0.5) * 0.8,
                        (random.nextDouble() - 0.5) * 0.3,
                        random.nextDouble() * 0.3,
                        (random.nextDouble() - 0.5) * 0.3);
            }

            this.discard();
        }
    }

    @Override
    public boolean save(@NotNull CompoundTag nbt) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 128.0 * 128.0;
    }
}