package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionThermo;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeKyiv extends EntityGrenadeBouncyBase {

    public EntityGrenadeKyiv(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    @Override
    public void explode() {
        //this.level().explode(this, this.getX(), this.getY(), this.getZ(), 5.0F, true, Level.ExplosionInteraction.MOB);
        if (!this.level().isClientSide) {
            this.discard();

            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());

            ExplosionLarge.jolt(this.level(), this.getX(), this.getY(), this.getZ(), 5, 200, 0.25);
            ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ(), 5, true, true, true);
            ExplosionThermo.setEntitiesOnFire(this.level(), pos, 8);
            ExplosionChaos.flameDeath(this.level(), pos, 15);
            ExplosionChaos.burn(this.level(), pos, 10);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_KYIV.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.0D;
    }
}
