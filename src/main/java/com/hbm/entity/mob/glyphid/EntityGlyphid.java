package com.hbm.entity.mob.glyphid;

import com.hbm.config.MobConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.logic.EntityWaypoint;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorGlyphidDig;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntityGlyphid extends Monster {

    // Data watcher keys
    private static final EntityDataAccessor<Byte> DW_WALL =
            SynchedEntityData.defineId(EntityGlyphid.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> DW_ARMOR =
            SynchedEntityData.defineId(EntityGlyphid.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> DW_SUBTYPE =
            SynchedEntityData.defineId(EntityGlyphid.class, EntityDataSerializers.BYTE);

    // Home
    public boolean hasHome = false;
    public int homeX, homeY, homeZ;

    // Task system
    protected int currentTask = 0;
    protected int previousTask;
    protected EntityWaypoint previousWaypoint;
    public int taskX, taskY, taskZ;
    protected boolean hasWaypoint = false;
    protected EntityWaypoint taskWaypoint = null;

    // Digging
    public int blastSize = 3;
    public int blastResToDig = 50;
    public boolean shouldDig;

    // Task constants
    public static final int TASK_IDLE = 0;
    public static final int TASK_RETREAT_FOR_REINFORCEMENTS = 1;
    public static final int TASK_BUILD_HIVE = 2;
    public static final int TASK_INITIATE_RETREAT = 3;
    public static final int TASK_FOLLOW = 4;
    public static final int TASK_TERRAFORM = 5;
    public static final int TASK_DIG = 6;

    // Subtypes
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_INFECTED = 1;
    public static final int TYPE_RADIOACTIVE = 2;

    public EntityGlyphid(EntityType<? extends EntityGlyphid> type, Level world) {
        super(type, world);
        this.xpReward = 5;
    }


    public GlyphidStats.StatBundle getStats() {
        return GlyphidStats.getStats().getGrunt();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DW_WALL, (byte) 0);
        this.entityData.define(DW_ARMOR, (byte) 0b11111);
        this.entityData.define(DW_SUBTYPE, (byte) 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        // Атаковать при получении урона
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // Атаковать всех живых существ, кроме глифидов
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                target -> !(target instanceof EntityGlyphid) && target.isAlive()));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            if (!hasHome) {
                homeX = (int) this.getX();
                homeY = (int) this.getY();
                homeZ = (int) this.getZ();
                hasHome = true;
            }

            if (this.hasEffect(MobEffects.BLINDNESS)) {
                onBlinded();
            }

            if (getCurrentTask() == TASK_FOLLOW && isAtDestination() && !hasWaypoint) {
                setCurrentTask(TASK_IDLE, null);
            }

            if (getCurrentTask() == TASK_DIG && this.tickCount % 20 == 0 && isAtDestination()) {
                swing(this.getUsedItemHand());

                ExplosionVNT vnt = new ExplosionVNT(
                        this.level(),
                        taskX, taskY + 2, taskZ,
                        blastSize, this
                );
                vnt.setBlockAllocator(new BlockAllocatorGlyphidDig(blastResToDig));
                vnt.setBlockProcessor(new BlockProcessorStandard().setNoDrop());
                vnt.setEntityProcessor(null);
                vnt.setPlayerProcessor(null);
                vnt.explode();

                setCurrentTask(previousTask, previousWaypoint);
            }

            setBesideClimbableBlock(this.horizontalCollision);

            if (this.tickCount % 100 == 0) {
                swing(this.getUsedItemHand());
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.getTarget() == null && getCurrentTask() == TASK_IDLE) {
            double range = useExtendedTargeting() ? 128.0D : 16.0D;

            // Находим ближайшую цель
            LivingEntity nearest = null;
            double nearestDist = Double.MAX_VALUE;

            // Получаем всех живых существ в радиусе
            for (LivingEntity entity : this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(range),
                    e -> e != this && e.isAlive() && !(e instanceof EntityGlyphid)
            )) {
                double dist = this.distanceTo(entity);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = entity;
                }
            }

            if (nearest != null) {
                this.setTarget(nearest);
            }
        }

        if (getCurrentTask() == TASK_IDLE && this.getTarget() != null) {
            this.getNavigation().moveTo(this.getTarget(), 1.0D);
        }

        if (getCurrentTask() != TASK_IDLE && !isAtDestination() && taskWaypoint != null) {
            this.getNavigation().moveTo(taskX, taskY, taskZ, 1.0D);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof EntityGlyphid) return false;
        return GlyphidStats.getStats().handleAttack(this, source, amount);
    }

    public boolean attackSuperclass(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity victim) {
        if (this.swinging) return false;
        this.swing(this.getUsedItemHand());

        if (this.entityData.get(DW_SUBTYPE) == TYPE_INFECTED && victim instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2));
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
        }

        return super.doHurtTarget(victim);
    }


    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(DW_WALL) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbable) {
        byte watchable = this.entityData.get(DW_WALL);
        if (climbable) {
            watchable = (byte) (watchable | 1);
        } else {
            watchable &= -2;
        }
        this.entityData.set(DW_WALL, watchable);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return this.getTarget() == null && getCurrentTask() == TASK_IDLE && this.tickCount > 100;
    }

    @Override
    public boolean fireImmune() {
        return this.entityData.get(DW_SUBTYPE) == TYPE_RADIOACTIVE;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DW_ARMOR, tag.getByte("armor"));
        this.entityData.set(DW_SUBTYPE, tag.getByte("subtype"));

        this.hasHome = tag.getBoolean("hasHome");
        this.homeX = tag.getInt("homeX");
        this.homeY = tag.getInt("homeY");
        this.homeZ = tag.getInt("homeZ");

        this.hasWaypoint = tag.getBoolean("hasWaypoint");
        this.taskX = tag.getInt("taskX");
        this.taskY = tag.getInt("taskY");
        this.taskZ = tag.getInt("taskZ");

        this.currentTask = tag.getInt("task");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("armor", this.entityData.get(DW_ARMOR));
        tag.putByte("subtype", this.entityData.get(DW_SUBTYPE));

        tag.putBoolean("hasHome", hasHome);
        tag.putInt("homeX", homeX);
        tag.putInt("homeY", homeY);
        tag.putInt("homeZ", homeZ);

        tag.putBoolean("hasWaypoint", hasWaypoint);
        tag.putInt("taskX", taskX);
        tag.putInt("taskY", taskY);
        tag.putInt("taskZ", taskZ);

        tag.putInt("task", currentTask);
    }

    // ============ TASK SYSTEM ==========

    public int getCurrentTask() { return currentTask; }
    public EntityWaypoint getWaypoint() { return taskWaypoint; }

    public void setCurrentTask(int task, @Nullable EntityWaypoint waypoint) {
        this.currentTask = task;
        this.taskWaypoint = waypoint;
        this.hasWaypoint = waypoint != null;

        if (taskWaypoint != null) {
            taskX = (int) taskWaypoint.getX();
            taskY = (int) taskWaypoint.getY();
            taskZ = (int) taskWaypoint.getZ();

            if (taskWaypoint.highPriority) {
                this.setTarget(null);
                this.getNavigation().stop();
            }
        }
        carryOutTask();
    }

    public void carryOutTask() {
        switch (getCurrentTask()) {
            case TASK_RETREAT_FOR_REINFORCEMENTS:
                if (taskWaypoint != null) {
                    communicate(TASK_FOLLOW, taskWaypoint);
                    setCurrentTask(TASK_FOLLOW, taskWaypoint);
                }
                break;
            case TASK_INITIATE_RETREAT:
                if (!this.level().isClientSide && taskWaypoint == null) {
                    EntityWaypoint additional = new EntityWaypoint(ModEntities.WAYPOINT.get(), this.level());
                    additional.setPos(this.getX(), this.getY(), this.getZ());
                    additional.setWaypointType(TASK_IDLE);

                    EntityWaypoint home = new EntityWaypoint(ModEntities.WAYPOINT.get(), this.level());
                    home.setWaypointType(TASK_RETREAT_FOR_REINFORCEMENTS);
                    home.setAdditionalWaypoint(additional);
                    home.setHighPriority();
                    home.setPos(homeX, homeY, homeZ);
                    home.radius = 6;
                    this.level().addFreshEntity(home);

                    this.taskWaypoint = home;
                    communicate(TASK_FOLLOW, home);
                    setCurrentTask(TASK_FOLLOW, taskWaypoint);
                }
                break;
            case TASK_DIG:
                shouldDig = true;
                break;
            default:
                break;
        }
    }

    public void communicate(int task, @Nullable EntityWaypoint waypoint) {
        int radius = waypoint != null ? waypoint.radius : 4;
        AABB bb = this.getBoundingBox().inflate(radius);

        List<Entity> bugs = this.level().getEntities(this, bb);
        for (Entity e : bugs) {
            if (e instanceof EntityGlyphid && !(e instanceof EntityGlyphidScout)) {
                if (((EntityGlyphid) e).getCurrentTask() != task) {
                    ((EntityGlyphid) e).setCurrentTask(task, waypoint);
                }
            }
        }
    }

    public boolean isAtDestination() {
        int destinationRadius = taskWaypoint != null ? (int) Math.pow(taskWaypoint.radius, 2) : 25;
        return this.distanceToSqr(taskX, taskY, taskZ) <= destinationRadius;
    }

    public boolean useExtendedTargeting() {
        return MobConfig.RAMPANT_EXTENDED_TARGETING.get() ||
                PollutionHandler.getPollution(this.level(), BlockPos.containing(this.getX(), this.getY(), this.getZ()),
                        PollutionType.SOOT) >= MobConfig.targetingThreshold;
    }

    public boolean canDig() {
        return MobConfig.rampantDig;
    }

    public void onBlinded() {
        this.setTarget(null);
        this.getNavigation().stop();
    }

    public boolean doesInfectedSpawnMaggots() {
        return true;
    }

    // ============ ARMOR SYSTEM ==========

    public boolean isArmorBroken(float amount) {
        return this.random.nextInt(100) <= Math.min(Math.pow(amount * 0.6, 2), 100);
    }

    public void breakOffArmor() {
        byte armor = this.entityData.get(DW_ARMOR);
        List<Integer> indices = Arrays.asList(0, 1, 2, 3, 4);
        Collections.shuffle(indices);

        for (Integer i : indices) {
            byte bit = (byte) (1 << i);
            if ((armor & bit) > 0) {
                armor &= ~bit;
                armor = (byte) (armor & 0b11111);
                this.entityData.set(DW_ARMOR, armor);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, this.getSoundSource(), 1.0F, 1.25F);
                break;
            }
        }
    }

    public int getGlyphidArmor() {
        int total = 0;
        byte armor = this.entityData.get(DW_ARMOR);
        for (int i = 0; i < 5; i++) {
            total += (armor & (1 << i)) != 0 ? 1 : 0;
        }
        return total;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.fixed(1.75F, 1.0F);
    }

    public boolean expandHive(){
        return false;
    }

    public float getScale() {
        return 1.0F;
    }
}