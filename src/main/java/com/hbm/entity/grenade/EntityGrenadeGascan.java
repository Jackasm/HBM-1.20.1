package com.hbm.entity.grenade;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeGascan extends EntityGrenadeBase {

    public EntityGrenadeGascan(EntityType<? extends EntityGrenadeBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeGascan(EntityType<? extends EntityGrenadeBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeGascan(EntityType<? extends EntityGrenadeBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();
            this.level().explode(null, this.getX(), this.getY(), this.getZ(), 5.0F, true, Level.ExplosionInteraction.MOB);
        }
    }
}