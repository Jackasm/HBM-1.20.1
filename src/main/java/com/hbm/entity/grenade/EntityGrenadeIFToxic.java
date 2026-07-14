package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeIFToxic extends EntityGrenadeBouncyBase {

    public EntityGrenadeIFToxic(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeIFToxic(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeIFToxic(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());

            ExplosionLarge.jolt(this.level(), this.getX(), this.getY(), this.getZ(), 3, 200, 0.25);
            ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ(), 2, true, true, true);
            ExplosionChaos.poison(this.level(), pos.getX(), pos.getY(), pos.getZ(), 12);
            ExplosionNukeGeneric.waste(this.level(), pos, 12);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_IF_TOXIC.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}