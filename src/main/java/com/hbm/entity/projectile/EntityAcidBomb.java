package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityAcidBomb extends ThrowableProjectile  implements ItemSupplier {

    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(EntityAcidBomb.class, EntityDataSerializers.FLOAT);

    public EntityAcidBomb(EntityType<? extends EntityAcidBomb> type, Level world) {
        super(type, world);
        this.setNoGravity(false);
    }

    public EntityAcidBomb(Level world, LivingEntity thrower) {
        super(ModEntities.ACID_BOMB.get(), world);
        this.setOwner(thrower);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        this.setNoGravity(false);
    }

    public EntityAcidBomb(Level world, double x, double y, double z) {
        super(ModEntities.ACID_BOMB.get(), world);
        this.setPos(x, y, z);
        this.setNoGravity(false);
    }

    public EntityAcidBomb(Level world) {
        super(ModEntities.ACID_BOMB.get(), world);
        this.setNoGravity(false);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 1.5F);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (this.level().isClientSide) return;

        if (result.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) result).getEntity();

            if (!(target instanceof EntityGlyphid)) {
                DamageSource source = ModDamageSource.createDamageSource(
                        ModDamageSource.ACID,
                        this,
                        this.getOwner() instanceof LivingEntity ? this.getOwner() : null,
                        this.level()
                );
                target.hurt(source, this.getDamage());
                this.discard();
                return;
            }
        }

        if (result.getType() == HitResult.Type.BLOCK) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Добавляем частицы следа
        if (this.level().isClientSide && this.tickCount % 2 == 0) {
            Vec3 motion = this.getDeltaMovement();
            this.level().addParticle(ParticleTypes.ITEM_SLIME,
                    this.getX() - motion.x * 0.5,
                    this.getY() - motion.y * 0.5,
                    this.getZ() - motion.z * 0.5,
                    motion.x * 0.1, motion.y * 0.1, motion.z * 0.1);
        }
    }

    @Override
    protected float getGravity() {
        return 0.04F;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("damage", this.getDamage());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setDamage(nbt.getFloat("damage"));
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Items.SLIME_BALL);
    }
}