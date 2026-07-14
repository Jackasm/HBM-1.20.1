package com.hbm.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EntityCloudFleija extends Entity {

    private static final EntityDataAccessor<Integer> MAX_AGE = SynchedEntityData.defineId(EntityCloudFleija.class, EntityDataSerializers.INT);

    public int age;
    public float scale = 0;

    public EntityCloudFleija(EntityType<?> type, Level level) {
        super(type, level);
        this.setBoundingBox(this.getBoundingBox().inflate(1, 4, 1));
        this.setInvulnerable(true);
        this.age = 0;
        this.scale = 0;
    }

    public EntityCloudFleija(EntityType<?> type, Level level, int maxAge) {
        super(type, level);
        this.setBoundingBox(this.getBoundingBox().inflate(20, 40, 20));
        this.setInvulnerable(true);
        this.setMaxAge(maxAge);
        this.age = 0;
        this.scale = 0;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MAX_AGE, 100);
    }

    @Override
    public void tick() {
        this.age++;

        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.setPos(this.getX(), this.getY() + 200, this.getZ());
                this.level().addFreshEntity(lightning);
            }
        }

        if (this.age >= this.getMaxAge()) {
            this.discard();
        }

        this.scale++;
    }

    public void setMaxAge(int age) {
        this.entityData.set(MAX_AGE, age);
    }

    public int getMaxAge() {
        return this.entityData.get(MAX_AGE);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getInt("age");
        this.scale = tag.getFloat("scale");
        this.setMaxAge(tag.getInt("maxAge"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("age", this.age);
        tag.putFloat("scale", this.scale);
        tag.putInt("maxAge", this.getMaxAge());
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 25000;
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}