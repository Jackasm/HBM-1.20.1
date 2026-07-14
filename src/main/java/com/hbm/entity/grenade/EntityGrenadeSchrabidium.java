package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeSchrabidium extends EntityGrenadeBouncyBase {

    public EntityGrenadeSchrabidium(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeSchrabidium(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeSchrabidium(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            ExplosionChaos.schrab(this.level(), pos, 50, 50);
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.5F, Level.ExplosionInteraction.MOB);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_SCHRABIDIUM.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}