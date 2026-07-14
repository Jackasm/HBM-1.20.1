package com.hbm.entity.grenade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public abstract class EntityGrenadeBase extends ThrowableItemProjectile {

    public EntityGrenadeBase(EntityType<? extends EntityGrenadeBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeBase(EntityType<? extends EntityGrenadeBase> type, Level level, LivingEntity thrower) {
        super(type, thrower, level);

        this.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 1.5F, 1.0F);
    }

    public EntityGrenadeBase(EntityType<? extends EntityGrenadeBase> type, Level level, double x, double y, double z) {
        super(type, x, y, z, level);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() > 0.01) {
            this.setYRot((float) (Math.atan2(motion.x, motion.z) * 180.0D / Math.PI));
            this.setXRot(this.getXRot() - (float) motion.length() * 25);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level().isClientSide) {
            if (result.getType() != HitResult.Type.MISS) {
                this.explode();
            }
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return null; // Должен быть переопределён
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public abstract void explode();

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }
}