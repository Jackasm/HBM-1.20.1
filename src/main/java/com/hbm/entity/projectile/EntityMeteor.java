package com.hbm.entity.projectile;

import com.hbm.config.WorldConfig;
import com.hbm.entity.ModEntities;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.main.MainRegistry;

import com.hbm.world.feature.Meteorite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EntityMeteor extends Entity {

    public boolean safe = false;

    public EntityMeteor(EntityType<? extends EntityMeteor> type, Level world) {
        super(type, world);
        this.setInvulnerable(true);
        this.setBoundingBox(this.getBoundingBox().inflate(4.0F));
    }

    public EntityMeteor(Level world) {
        this(ModEntities.METEOR.get(), world);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && !WorldConfig.ENABLE_METEOR_STIKES) {
            this.discard();
            return;
        }

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.03, 0));
        if (this.getDeltaMovement().y < -2.5) {
            this.setDeltaMovement(this.getDeltaMovement().x, -2.5, this.getDeltaMovement().z);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (!this.level().isClientSide && this.onGround() && this.getY() < 260) {
            boolean explosive = !safe;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 5.0F + this.random.nextFloat(),
                    explosive ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);

            if (WorldConfig.ENABLE_METEOR_TAILS) {
                ExplosionLarge.spawnParticles(this.level(), this.getX(), this.getY() + 5, this.getZ(), 75);
                ExplosionLarge.spawnParticles(this.level(), this.getX() + 5, this.getY(), this.getZ(), 75);
                ExplosionLarge.spawnParticles(this.level(), this.getX() - 5, this.getY(), this.getZ(), 75);
                ExplosionLarge.spawnParticles(this.level(), this.getX(), this.getY(), this.getZ() + 5, 75);
                ExplosionLarge.spawnParticles(this.level(), this.getX(), this.getY(), this.getZ() - 5, 75);
            }

            new Meteorite().generate(
                    this.level(),
                    this.random,
                    (int)Math.floor(this.getX() - 0.5),
                    (int)Math.floor(this.getY() - 0.5),
                    (int)Math.floor(this.getZ() - 0.5),
                    safe,
                    true,
                    true
            );

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                    net.minecraft.sounds.SoundSource.HOSTILE, 10000.0F, 0.5F + this.random.nextFloat() * 0.1F);

            this.discard();
        }

        if (WorldConfig.ENABLE_METEOR_TAILS && this.level().isClientSide) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "exhaust");
            data.putString("mode", "meteor");
            data.putInt("count", 10);
            data.putDouble("width", 1);
            data.putDouble("posX", this.getX() - this.getDeltaMovement().x);
            data.putDouble("posY", this.getY() - this.getDeltaMovement().y);
            data.putDouble("posZ", this.getZ() - this.getDeltaMovement().z);
            MainRegistry.proxy.effectNT(data);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        // Нет данных для синхронизации
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.safe = nbt.getBoolean("safe");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putBoolean("safe", safe);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}