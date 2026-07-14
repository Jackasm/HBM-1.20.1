package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionLarge;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeBreach extends EntityGrenadeBase {

    public EntityGrenadeBreach(EntityType<? extends EntityGrenadeBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeBreach(EntityType<? extends EntityGrenadeBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeBreach(EntityType<? extends EntityGrenadeBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            if (random.nextInt(10) == 0) {
                this.discard();
            }
            ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ(), 2.5F, false, false, false);
        }
    }
}