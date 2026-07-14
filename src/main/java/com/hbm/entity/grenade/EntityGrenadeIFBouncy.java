package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionLarge;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeIFBouncy extends EntityGrenadeBouncyBase {

    public EntityGrenadeIFBouncy(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeIFBouncy(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeIFBouncy(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
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

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_IF_BOUNCY.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.75D;
    }
}