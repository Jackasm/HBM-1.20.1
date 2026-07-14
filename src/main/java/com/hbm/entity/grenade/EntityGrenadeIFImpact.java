package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionLarge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeIFImpact extends EntityGrenadeBase {

    public EntityGrenadeIFImpact(EntityType<? extends EntityGrenadeBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeIFImpact(EntityType<? extends EntityGrenadeBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeIFImpact(EntityType<? extends EntityGrenadeBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());

            ExplosionLarge.jolt(this.level(), this.getX(), this.getY(), this.getZ(), 5, 200, 0.25);
            ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ(), 5, true, true, true);
        }
    }
}