package com.hbm.entity.grenade;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class EntityGrenadeFlare extends ThrowableItemProjectile {

    public Entity shooter;

    public EntityGrenadeFlare(EntityType<? extends EntityGrenadeFlare> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeFlare(EntityType<? extends EntityGrenadeFlare> type, Level level, LivingEntity thrower) {
        super(type, thrower, level);
        this.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 1.5F, 1.0F);
        this.shooter = thrower;
    }

    public EntityGrenadeFlare(EntityType<? extends EntityGrenadeFlare> type, Level level, double x, double y, double z) {
        super(type, x, y, z, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 250) {
            this.discard();
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        this.setDeltaMovement(0, 0, 0);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return null;
    }
}