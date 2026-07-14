package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionLarge;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeStrong extends EntityGrenadeBouncyBase {


    public EntityGrenadeStrong(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeStrong(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeStrong(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();
            ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ(), 5.0F, true, false, false);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(ModItems.GRENADE_STRONG.get());
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}