package com.hbm.entity.mob.botprime;

import com.hbm.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityBOTPrimeBody extends EntityBOTPrimeBase {

    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(EntityBOTPrimeBody.class, EntityDataSerializers.BYTE);

    public EntityBOTPrimeBody(EntityType<? extends EntityBOTPrimeBody> type, Level level) {
        super(type, level);
        this.rangeForParts = 70.0D;
        this.segmentDistance = 3.5D;
        this.maxBodySpeed = 1.4D;

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, (byte) 0);
    }

    @Override
    public float getAttackStrength(Entity target) {
        if (target instanceof LivingEntity living) {
            return living.getHealth() * 0.75F;
        }
        return 100.0F;
    }

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effect) {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.updateEntityActionState();
        this.targetSelector.tick();

        updateMovement();

        if (this.didCheck) {
            if (this.targetedEntity == null || !this.targetedEntity.isAlive()) {
                this.setHealth(this.getHealth() - 1999.0F);
            }
            if ((this.followed == null || !this.followed.isAlive()) && this.random.nextInt(60) == 0) {
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.0F, Level.ExplosionInteraction.MOB);
            }
        }

        if (this.followed != null && this.followed.isAlive() && this.getTarget() != null) {
            if (canEntityBeSeenThroughNonSolids(this.getTarget())) {
                this.attackCounter++;
                if (this.attackCounter == 10) {
                    laserAttack(this.getTarget(), false);
                    this.attackCounter = -20;
                }
            } else if (this.attackCounter > 0) {
                this.attackCounter--;
            }
        } else if (this.attackCounter > 0) {
            this.attackCounter--;
        }

        updateRotationToTarget();
    }

    @Override
    public void tick() {
        super.tick();
        updateRotationToTarget();
    }

    private void updateRotationToTarget() {
        if (this.targetedEntity != null) {
            double dx = this.targetedEntity.getX() - this.getX();
            double dy = this.targetedEntity.getY() - this.getY();
            double dz = this.targetedEntity.getZ() - this.getZ();
            float f3 = (float) Math.sqrt(dx * dx + dz * dz);
            this.setYRot((float) (Math.atan2(dx, dz) * 180.0D / Math.PI));
            this.setXRot((float) (Math.atan2(dy, f3) * 180.0D / Math.PI));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("partID", this.getPartNumber());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setPartNumber(nbt.getInt("partID"));
    }
}