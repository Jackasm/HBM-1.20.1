package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeNuclear extends EntityGrenadeBouncyBase {

    public EntityGrenadeNuclear(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeNuclear(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeNuclear(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            ExplosionNukeSmall.explode(this.level(), this.getX(), this.getY() + 0.5, this.getZ(),
                    ExplosionNukeSmall.PARAMS_TOTS);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_NUCLEAR.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}