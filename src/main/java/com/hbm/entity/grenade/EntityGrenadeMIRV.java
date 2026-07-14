package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityGrenadeMIRV extends EntityGrenadeBouncyBase {

    public EntityGrenadeMIRV(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeMIRV(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeMIRV(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            Vec3 motion = this.getDeltaMovement();

            for (int i = 0; i < 8; i++) {
                EntityGrenadeSmart grenade = new EntityGrenadeSmart(ModEntities.GRENADE_SMART.get(), this.level());
                grenade.setPos(this.getX(), this.getY(), this.getZ());
                grenade.setDeltaMovement(
                        motion.x + (random.nextGaussian() * 0.25),
                        motion.y + (random.nextGaussian() * 0.25),
                        motion.z + (random.nextGaussian() * 0.25)
                );
                grenade.tickCount = 10;

                this.level().addFreshEntity(grenade);
            }
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_MIRV.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}