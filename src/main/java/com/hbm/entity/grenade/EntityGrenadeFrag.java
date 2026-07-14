package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeFrag extends EntityGrenadeBouncyBase {

    public Entity shooter;

    public EntityGrenadeFrag(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeFrag(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
        this.shooter = thrower;
    }

    public EntityGrenadeFrag(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            if (this.isOnFire()) {
                this.discard();

                ExplosionChaos.frag(this.level(), pos, 100, true, this.shooter);
                ExplosionChaos.burn(this.level(), pos, 5);
                ExplosionChaos.flameDeath(this.level(), pos, 15);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F,
                        (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            } else {
                this.discard();
                ExplosionChaos.frag(this.level(), pos, 100, false, this.shooter);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F,
                        (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            }
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_FRAG.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}