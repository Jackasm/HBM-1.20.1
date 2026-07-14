package com.hbm.entity.mob.botprime;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class EntityWormBaseNT extends EntityBurrowingNT {

    public int aggroCooldown = 0;
    public int courseChangeCooldown = 0;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    protected Entity targetedEntity = null;
    protected boolean canFly = false;
    protected int dmgCooldown = 0;
    protected boolean wasNearGround;
    protected BlockPos spawnPoint = BlockPos.ZERO;
    protected double attackRange;
    protected double maxSpeed;
    protected double fallSpeed;
    protected double rangeForParts;
    protected LivingEntity followed;
    protected int surfaceY;
    private int headID;
    private int partNum;
    protected boolean didCheck;
    protected double maxBodySpeed;
    protected double segmentDistance;
    protected double knockbackDivider;

    public EntityWormBaseNT(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setSize(1.0F, 1.0F);
        this.surfaceY = 60;
        this.noPhysics = true;
    }

    public int getPartNumber() {
        return this.partNum;
    }

    public void setPartNumber(int num) {
        this.partNum = num;
    }

    public Entity getHead() {
        return this.level().getEntity(this.headID);
    }

    public int getHeadID() {
        return this.headID;
    }

    public void setHeadID(int id) {
        this.headID = id;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) ||
                source == this.damageSources().drown() ||
                source == this.damageSources().inWall() ||
                (source.getEntity() instanceof EntityWormBaseNT worm && worm.getHeadID() == this.getHeadID())) {
            return false;
        }

        this.markHurt();

        if (this.getIsHead()) {
            return super.hurt(source, amount);
        }

        Entity head = this.getHead();
        if (head != null) {
            return head.hurt(source, amount);
        }
        return super.hurt(source, amount);
    }

    protected void updateEntityActionState() {
        if (!this.level().isClientSide && this.level().getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
            this.discard();
            return;
        }

        if (this.targetedEntity != null && this.targetedEntity.isRemoved()) {
            this.targetedEntity = null;
        }

        if (this.getY() < -10.0D) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 1.0D, 0));
        } else if (this.getY() < 3.0D) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.3D, 0));
        }

        if (this.tickCount % 5 == 0) {
            attackEntitiesInList(this.level().getEntities(this, this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D)));
        }
    }

    protected void attackEntitiesInList(List<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof LivingEntity && canAttackClass(target.getClass()) &&
                    (!(target instanceof EntityWormBaseNT worm) || worm.getHeadID() != this.getHeadID())) {
                this.doHurtTarget(target);
            }
        }
    }

    public boolean canAttackClass(Class<?> clazz) {
        return true;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean success = target.hurt(this.damageSources().mobAttack(this), getAttackStrength(target));

        if (success) {
            this.tickCount = 0;
            double tx = (this.getBoundingBox().minX + this.getBoundingBox().maxX) / 2.0D;
            double tz = (this.getBoundingBox().minZ + this.getBoundingBox().maxZ) / 2.0D;
            double ty = (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D;
            double deltaX = target.getX() - tx;
            double deltaZ = target.getZ() - tz;
            double deltaY = target.getY() - ty;
            double knockback = this.knockbackDivider * (deltaX * deltaX + deltaZ * deltaZ + deltaY * deltaY + 0.1D);
            target.push(deltaX / knockback, deltaY / knockback, deltaZ / knockback);
        }

        return success;
    }

    public abstract float getAttackStrength(Entity target);

    @Override
    public void push(@NotNull Entity entity) {
        // Отключаем стандартный пуш
    }

    @Override
    public void lookAt(@NotNull Entity entity, float yaw, float pitch) {
        // Отключаем поворот
    }

    protected boolean isCourseTraversable() {
        return this.canFly || this.isInWall();
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    @Override
    public void onRemovedFromWorld() {
        this.playSound(this.getDeathSound(), this.getSoundVolume(), 1.0F);
        super.onRemovedFromWorld();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("wormID", this.getHeadID());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setHeadID(nbt.getInt("wormID"));
    }

    protected void updateMovement() {
        double targetingRange = 128.0D;

        if (this.targetedEntity != null && this.targetedEntity.distanceToSqr(this) < targetingRange * targetingRange) {
            this.waypointX = this.targetedEntity.getX();
            this.waypointY = this.targetedEntity.getY();
            this.waypointZ = this.targetedEntity.getZ();
        }

        if ((this.tickCount % 60 == 0 || this.tickCount == 1) && (this.targetedEntity == null || this.followed == null)) {
            findEntityToFollow(this.level().getEntitiesOfClass(EntityWormBaseNT.class,
                    this.getBoundingBox().inflate(this.rangeForParts, this.rangeForParts, this.rangeForParts)));
        }

        double deltaX = this.waypointX - this.getX();
        double deltaY = this.waypointY - this.getY();
        double deltaZ = this.waypointZ - this.getZ();
        double deltaDist = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        if (this.targetedEntity != null) {
            this.lookAt(this.targetedEntity, 180.0F, 180.0F);
        }

        double speed = Math.max(0.0D, Math.min(deltaDist - this.segmentDistance, this.maxBodySpeed));

        if (deltaDist < this.segmentDistance * 0.895D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8D, 0.8D, 0.8D));
        } else if (deltaDist > 0) {
            this.setDeltaMovement(
                    deltaX / deltaDist * speed,
                    deltaY / deltaDist * speed,
                    deltaZ / deltaDist * speed
            );
        }
    }

    protected void findEntityToFollow(List<EntityWormBaseNT> segments) {
        for (EntityWormBaseNT segment : segments) {
            if (segment.getHeadID() == this.getHeadID()) {
                if (segment.getIsHead()) {
                    if (this.getPartNumber() == 0) {
                        this.targetedEntity = segment;
                    }
                    this.followed = segment;
                } else if (segment.getPartNumber() == this.getPartNumber() - 1) {
                    this.targetedEntity = segment;
                }
            }
        }
        this.didCheck = true;
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return false;
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D);
    }
}