package com.hbm.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EntityCloudTom extends Entity {

    private int maxAge = 100;
    public int age;

    public EntityCloudTom(EntityType<? extends EntityCloudTom> type, Level level) {
        super(type, level);
        this.age = 0;
    }

    public EntityCloudTom(EntityType<? extends EntityCloudTom> type, Level level, int maxAge) {
        this(type, level);
        this.setMaxAge(maxAge);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(MAX_AGE, 0);
    }

    @Override
    public void tick() {
        this.age++;
        this.level().setThunderLevel(2.0F);

        if (this.age >= this.getMaxAge()) {
            this.discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        age = tag.getShort("age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort("age", (short) age);
    }

    public void setMaxAge(int i) {
        this.getEntityData().set(MAX_AGE, i);
    }

    public int getMaxAge() {
        return this.getEntityData().get(MAX_AGE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    @NotNull
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // Synched data keys
    public static final EntityDataAccessor<Integer> MAX_AGE =
            SynchedEntityData.defineId(EntityCloudTom.class,
                    EntityDataSerializers.INT);
}