package com.hbm.entity.grenade;

import com.hbm.entity.projectile.EntityBullet;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityGrenadeIFBrimstone extends EntityGrenadeBouncyBase {

    public EntityGrenadeIFBrimstone(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeIFBrimstone(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeIFBrimstone(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.timer > (this.getMaxTimer() * 0.65)) {
            if (!this.level().isClientSide && this.thrower instanceof Player player) {
                EntityBullet fragment;

                fragment = new EntityBullet(this.level(), player, 3.0F, 35, 45, false, "tauDay");
                fragment.setDamage(random.nextInt(301) + 100);

                fragment.setDeltaMovement(
                        random.nextGaussian(),
                        random.nextGaussian(),
                        random.nextGaussian()
                );
                fragment.setOwner(this.thrower);
                fragment.setPos(this.getX(), this.getY(), this.getZ());
                fragment.setCritical(true);

                this.level().addFreshEntity(fragment);
            }
        }
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide && this.thrower instanceof Player player) {
            this.discard();

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 5.0F, false, Level.ExplosionInteraction.MOB);

            for (int i = 0; i < 100; i++) {
                EntityBullet fragment;

                fragment = new EntityBullet(this.level(), player, 3.0F, 35, 45, false, "tauDay");
                fragment.setDamage(random.nextInt(301) + 100);

                fragment.setDeltaMovement(
                        random.nextGaussian() * 0.25,
                        random.nextGaussian() * 0.25,
                        random.nextGaussian() * 0.25
                );
                fragment.setOwner(this.thrower);
                fragment.setPos(this.getX(), this.getY(), this.getZ());
                fragment.setCritical(true);

                this.level().addFreshEntity(fragment);
            }
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_IF_BRIMSTONE.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}