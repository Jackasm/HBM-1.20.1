package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionLarge;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeMk2 extends EntityGrenadeBouncyBase {

    public EntityGrenadeMk2(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeMk2(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeMk2(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();
            ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ(), 7.5F, true, false, false);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_MK2.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}