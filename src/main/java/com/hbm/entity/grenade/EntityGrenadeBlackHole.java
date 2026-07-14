package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeBlackHole extends EntityGrenadeBouncyBase {

    public EntityGrenadeBlackHole(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeBlackHole(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeBlackHole(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.5F, Level.ExplosionInteraction.MOB);

            EntityBlackHole blackHole = new EntityBlackHole(ModEntities.BLACK_HOLE.get(), this.level(), 1.5F);
            blackHole.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(blackHole);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_BLACK_HOLE.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}