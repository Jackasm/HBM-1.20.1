package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGenericGrenade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class EntityGrenadeBouncyGeneric extends EntityGrenadeBouncyBase implements IGenericGrenade {

    private static final EntityDataAccessor<Integer> GRENADE_TYPE = SynchedEntityData.defineId(
            EntityGrenadeBouncyGeneric.class, EntityDataSerializers.INT);

    public EntityGrenadeBouncyGeneric(EntityType<? extends EntityGrenadeBouncyGeneric> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeBouncyGeneric(EntityType<? extends EntityGrenadeBouncyGeneric> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeBouncyGeneric(EntityType<? extends EntityGrenadeBouncyGeneric> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    // Конструкторы для создания сущности (обязательны)
    public EntityGrenadeBouncyGeneric(Level level) {
        this(ModEntities.GRENADE_BOUNCY_GENERIC.get(), level);
    }

    public EntityGrenadeBouncyGeneric(Level level, LivingEntity thrower) {
        this(ModEntities.GRENADE_BOUNCY_GENERIC.get(), level, thrower);
    }

    public EntityGrenadeBouncyGeneric(Level level, double x, double y, double z) {
        this(ModEntities.GRENADE_BOUNCY_GENERIC.get(), level, x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GRENADE_TYPE, Item.getId(ModItems.GRENADE_KYIV.get()));
    }

    public EntityGrenadeBouncyGeneric setType(ItemGenericGrenade grenade) {
        this.entityData.set(GRENADE_TYPE, Item.getId(grenade));
        return this;
    }

    public ItemGenericGrenade getGrenade() {
        Item item = Item.byId(this.entityData.get(GRENADE_TYPE));
        if (item instanceof ItemGenericGrenade grenade) {
            return grenade;
        }
        return (ItemGenericGrenade) ModItems.GRENADE_KYIV.get();
    }

    @Override
    public void explode() {
        getGrenade().explode(this, this.getThrower(), this.level(), this.getX(), this.getY(), this.getZ());
        this.discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("grenade", this.entityData.get(GRENADE_TYPE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(GRENADE_TYPE, compound.getInt("grenade"));
    }

    @Override
    protected int getMaxTimer() {
        return getGrenade().getMaxTimer();
    }

    @Override
    protected double getBounceMod() {
        return getGrenade().getBounceMod();
    }
}