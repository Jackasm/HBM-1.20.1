package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeBurst extends EntityGrenadeBouncyBase {

    public EntityGrenadeBurst(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeBurst(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeBurst(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            for (int i = 0; i < 8; i++) {
                EntityGrenadeBreach grenade = new EntityGrenadeBreach(ModEntities.GRENADE_BREACH.get(), this.level());
                grenade.setPos(this.getX(), this.getY(), this.getZ());
                grenade.setDeltaMovement(
                        random.nextGaussian() * 0.1,
                        -0.25,
                        random.nextGaussian() * 0.1
                );

                this.level().addFreshEntity(grenade);
            }
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_BURST.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}