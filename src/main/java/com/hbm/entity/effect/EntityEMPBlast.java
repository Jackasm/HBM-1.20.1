package com.hbm.entity.effect;

import com.hbm.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityEMPBlast extends Entity {

    public int maxAge = 100;
    public int age;
    public float scale = 0;

    public EntityEMPBlast(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.setSize(1.5F, 1.5F);
        this.blocksBuilding = true;
        this.age = 0;
        this.scale = 0;
    }

    public EntityEMPBlast(Level level, int maxAge) {
        this(ModEntities.EMP_BLAST.get(), level);
        this.setMaxAge(maxAge);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MAX_AGE, 100);
    }

    private static final EntityDataAccessor<Integer> MAX_AGE = SynchedEntityData.defineId(EntityEMPBlast.class, EntityDataSerializers.INT);


    @Override
    public void tick() {
        this.age++;

        if (this.age >= this.getMaxAge()) {
            this.age = 0;
            this.discard();
        }

        this.scale++;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.age = nbt.getShort("age");
        this.scale = nbt.getShort("scale");
        if (nbt.contains("maxAge")) {
            this.setMaxAge(nbt.getInt("maxAge"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putShort("age", (short) this.age);
        nbt.putShort("scale", (short) this.scale);
        nbt.putInt("maxAge", this.getMaxAge());
    }

    public void setMaxAge(int i) {
        this.entityData.set(MAX_AGE, i);
        this.maxAge = i;
    }

    public int getMaxAge() {
        return this.entityData.get(MAX_AGE);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }
}