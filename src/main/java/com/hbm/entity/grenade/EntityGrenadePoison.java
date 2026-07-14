package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadePoison extends EntityGrenadeBouncyBase {

    public EntityGrenadePoison(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadePoison(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadePoison(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.0F, Level.ExplosionInteraction.MOB);

            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            ExplosionNukeGeneric.wasteNoSchrab(this.level(), pos, 10);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_POISON.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}