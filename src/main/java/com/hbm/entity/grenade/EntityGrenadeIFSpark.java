package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityRagingVortex;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeIFSpark extends EntityGrenadeBouncyBase {

    public EntityGrenadeIFSpark(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeIFSpark(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeIFSpark(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            EntityRagingVortex vortex = new EntityRagingVortex(ModEntities.RAGING_VORTEX.get(), this.level(), 1.5F);
            vortex.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(vortex);
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_IF_SPARK.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}