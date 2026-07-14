package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeIFHopwire extends EntityGrenadeBouncyBase {

    public EntityGrenadeIFHopwire(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeIFHopwire(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeIFHopwire(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            EntityVortex vortex = new EntityVortex(ModEntities.VORTEX.get(), this.level(), 0.75F);
            vortex.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(vortex);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_IF_HOPWIRE.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}