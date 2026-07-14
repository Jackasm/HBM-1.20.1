package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionLarge;
import com.hbm.items.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeSmart extends EntityGrenadeBase {

    public EntityGrenadeSmart(EntityType<? extends EntityGrenadeBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeSmart(EntityType<? extends EntityGrenadeBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeSmart(EntityType<? extends EntityGrenadeBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            if (this.tickCount > 10) {
                ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ(), 5.0F, true, false, false);
            } else {
                ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(),
                        new ItemStack(ModItems.GRENADE_SMART.get()));
                this.level().addFreshEntity(item);
            }
        }
    }
}