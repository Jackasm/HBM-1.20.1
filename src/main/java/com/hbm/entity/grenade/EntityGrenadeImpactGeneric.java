package com.hbm.entity.grenade;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGenericGrenade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityGrenadeImpactGeneric extends EntityGrenadeBase implements IGenericGrenade {

    private static final EntityDataAccessor<Integer> GRENADE_TYPE = SynchedEntityData.defineId(
            EntityGrenadeImpactGeneric.class, EntityDataSerializers.INT);

    public EntityGrenadeImpactGeneric(EntityType<? extends EntityGrenadeImpactGeneric> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeImpactGeneric(EntityType<? extends EntityGrenadeImpactGeneric> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeImpactGeneric(EntityType<? extends EntityGrenadeImpactGeneric> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    // Конструкторы для упрощённого создания
    public EntityGrenadeImpactGeneric(Level level) {
        this(ModEntities.GRENADE_IMPACT_GENERIC.get(), level);
    }

    public EntityGrenadeImpactGeneric(Level level, LivingEntity thrower) {
        this(ModEntities.GRENADE_IMPACT_GENERIC.get(), level, thrower);
    }

    public EntityGrenadeImpactGeneric(Level level, double x, double y, double z) {
        this(ModEntities.GRENADE_IMPACT_GENERIC.get(), level, x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GRENADE_TYPE, Item.getId(ModItems.GRENADE_KYIV.get()));
    }

    public EntityGrenadeImpactGeneric setType(ItemGenericGrenade grenade) {
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
        if (!this.level().isClientSide && getGrenade() != null) {
            // Проверяем, что владелец является LivingEntity
            Entity owner = this.getOwner();
            LivingEntity thrower = owner instanceof LivingEntity ? (LivingEntity) owner : null;
            getGrenade().explode(this, thrower, this.level(), this.getX(), this.getY(), this.getZ());
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("grenade", this.entityData.get(GRENADE_TYPE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(GRENADE_TYPE, compound.getInt("grenade"));
    }
}