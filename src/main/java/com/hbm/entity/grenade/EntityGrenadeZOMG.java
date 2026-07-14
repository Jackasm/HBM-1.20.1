package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeZOMG extends EntityGrenadeBouncyBase {

    public EntityGrenadeZOMG(EntityType<? extends EntityGrenadeZOMG> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeZOMG(EntityType<? extends EntityGrenadeZOMG> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeZOMG(EntityType<? extends EntityGrenadeZOMG> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();
            ExplosionChaos.zomgMeSinPi(this.level(), this.getX(), this.getY(), this.getZ(), 20, this.getThrower(), this);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(ModItems.GRENADE_TAU.get());
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}