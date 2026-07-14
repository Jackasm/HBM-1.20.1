package com.hbm.entity.grenade;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeDynamite extends EntityGrenadeBouncyBase {

    public EntityGrenadeDynamite(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeDynamite(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeDynamite(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY() + 0.25D, this.getZ(), 3.0F, false, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    @Override
    protected int getMaxTimer() {
        return 60; // 3 секунды (60 тиков)
    }

    @Override
    protected double getBounceMod() {
        return 0.5D; // Динамит отскакивает сильнее обычных гранат
    }
}