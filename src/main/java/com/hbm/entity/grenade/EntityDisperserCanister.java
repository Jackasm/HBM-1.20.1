package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityMist;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityDisperserCanister extends EntityGrenadeBase {

    private static final EntityDataAccessor<Integer> FLUID_ID = SynchedEntityData.defineId(EntityDisperserCanister.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ITEM_ID = SynchedEntityData.defineId(EntityDisperserCanister.class, EntityDataSerializers.INT);

    public EntityDisperserCanister(EntityType<? extends EntityDisperserCanister> type, Level world) {
        super(type, world);
    }

    public EntityDisperserCanister(EntityType<? extends EntityDisperserCanister> type, Level world, LivingEntity living) {
        super(type, world, living);
    }

    public EntityDisperserCanister(EntityType<? extends EntityDisperserCanister> type, Level world, double x, double y, double z) {
        super(type, world, x, y, z);
    }

    // Конструктор для упрощенного создания
    public EntityDisperserCanister(Level world, LivingEntity living) {
        this(ModEntities.DISPERSER_CANISTER.get(), world, living);
    }

    public EntityDisperserCanister(Level world, double x, double y, double z) {
        this(ModEntities.DISPERSER_CANISTER.get(), world, x, y, z);
    }

    public EntityDisperserCanister setFluid(int id) {
        this.entityData.set(FLUID_ID, id);
        return this;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLUID_ID, 0);
        this.entityData.define(ITEM_ID, 0);
    }

    public EntityDisperserCanister setType(int id) {
        this.entityData.set(ITEM_ID, id);
        return this;
    }

    public FluidTypeHBM getFluid() {
        return Fluids.fromID(this.entityData.get(FLUID_ID));
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            EntityMist mist = new EntityMist(this.level());
            mist.setFluidType(getFluid());
            mist.setPos(this.getX(), this.getY(), this.getZ());
            mist.setArea(10, 5);
            mist.setDuration(80);
            this.level().addFreshEntity(mist);
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("fluid", this.entityData.get(FLUID_ID));
        nbt.putInt("item", this.entityData.get(ITEM_ID));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(FLUID_ID, nbt.getInt("fluid"));
        this.entityData.set(ITEM_ID, nbt.getInt("item"));
    }
}