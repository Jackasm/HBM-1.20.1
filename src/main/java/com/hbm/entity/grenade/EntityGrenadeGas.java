package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityMist;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

public class EntityGrenadeGas extends EntityGrenadeBouncyBase {

    private final Random rand = new Random();

    public EntityGrenadeGas(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeGas(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeGas(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            // Взрыв без разрушений (радиус 0)
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, Level.ExplosionInteraction.NONE);

            EntityMist mist = new EntityMist(ModEntities.MIST.get(), this.level());
            mist.setFluidType(Fluids.CHLORINE.get());
            mist.setPos(this.getX(), this.getY() - 5, this.getZ());
            mist.setArea(15, 10);
            this.level().addFreshEntity(mist);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_GAS.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}