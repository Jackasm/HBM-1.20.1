package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeIFMystery extends EntityGrenadeBouncyBase {

    public EntityGrenadeIFMystery(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeIFMystery(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeIFMystery(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 10.0F, false, Level.ExplosionInteraction.MOB);

            ExplosionChaos.spawnVolley(this.level(), pos, 100, 1.0D);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_IF_MYSTERY.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}