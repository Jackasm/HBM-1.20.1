package com.hbm.entity.projectile;

import com.hbm.blocks.bomb.NukeCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EntityFallingNuke extends ThrowableProjectile {

    private static final EntityDataAccessor<Byte> META = SynchedEntityData.defineId(EntityFallingNuke.class, EntityDataSerializers.BYTE);

    public float tnt;
    public float nuke;
    public float hydro;
    public float amat;
    public float dirty;
    public float schrab;
    public float euph;

    public EntityFallingNuke(EntityType<EntityFallingNuke> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public EntityFallingNuke(EntityType<EntityFallingNuke> type, Level level,
                             float tnt, float nuke, float hydro, float amat,
                             float dirty, float schrab, float euph) {
        super(type, level);
        this.noCulling = true;

        this.tnt = tnt;
        this.nuke = nuke;
        this.hydro = hydro;
        this.amat = amat;
        this.dirty = dirty;
        this.schrab = schrab;
        this.euph = euph;

        this.setYRot(90);
        this.setXRot(90);
        this.yRotO = 90;
        this.xRotO = 90;
    }

    public void setMeta(byte meta) {
        this.entityData.set(META, meta);
    }

    public byte getMeta() {
        return this.entityData.get(META);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(META, (byte) 0);
    }

    @Override
    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        this.setPos(this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z);

        this.setDeltaMovement(this.getDeltaMovement().x * 0.99,
                this.getDeltaMovement().y - 0.05,
                this.getDeltaMovement().z * 0.99);

        if (this.getDeltaMovement().y < -1) {
            this.setDeltaMovement(this.getDeltaMovement().x, -1, this.getDeltaMovement().z);
        }

        this.rotation();

        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        if (!this.level().getBlockState(pos).isAir()) {
            if (!this.level().isClientSide) {
                NukeCustom.explodeCustom(this.level(), this.getX(), this.getY(), this.getZ(),
                        tnt, nuke, hydro, amat, dirty, schrab, euph);
                this.discard();
            }
        }
    }

    public void rotation() {
        this.xRotO = this.getXRot();

        if (this.getXRot() > -75) {
            this.setXRot(this.getXRot() - 2);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        // Пустой, так как обработка в tick()
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        // Пустой, так как обработка в tick()
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("tnt", tnt);
        tag.putFloat("nuke", nuke);
        tag.putFloat("hydro", hydro);
        tag.putFloat("amat", amat);
        tag.putFloat("dirty", dirty);
        tag.putFloat("schrab", schrab);
        tag.putFloat("euph", euph);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        tnt = tag.getFloat("tnt");
        nuke = tag.getFloat("nuke");
        hydro = tag.getFloat("hydro");
        amat = tag.getFloat("amat");
        dirty = tag.getFloat("dirty");
        schrab = tag.getFloat("schrab");
        euph = tag.getFloat("euph");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 25000;
    }
}