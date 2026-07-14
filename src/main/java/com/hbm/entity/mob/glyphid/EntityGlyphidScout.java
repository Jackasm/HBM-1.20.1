package com.hbm.entity.mob.glyphid;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.MobConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.logic.EntityWaypoint;
import com.hbm.handler.pollution.PollutionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityGlyphidScout extends EntityGlyphid {
    private boolean hasTarget = false;
    private int timer;
    private final int scoutingRange = 45;
    private final int minDistanceToHive = 8;
    private boolean useLargeHive = false;
    private float largeHiveChance = MobConfig.largeHiveChance / 100.0F;

    public EntityGlyphidScout(EntityType<? extends EntityGlyphidScout> type, Level world) {
        super(type, world);
    }

    @Override
    public float getScale() { return 0.75F; }


    @Override
    public GlyphidStats.StatBundle getStats() {
        return GlyphidStats.getStats().getScout();
    }

    @Override
    public boolean isArmorBroken(float amount) {
        return this.random.nextInt(100) <= Math.min(Math.pow(amount, 2), 100);
    }

    @Override
    public boolean useExtendedTargeting() { return false; }

    @Override
    public boolean doHurtTarget(@NotNull Entity victim) {
        if (super.doHurtTarget(victim) && victim instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 3));
            return true;
        }
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.getTarget() != null && this.tickCount % 60 == 0) {
            Player nearest = findNearestPlayer();
            if (nearest != null) {
                this.setTarget(nearest);
            }
        }

        if (getCurrentTask() != TASK_BUILD_HIVE && getCurrentTask() != TASK_TERRAFORM && taskWaypoint == null) {
            if (MobConfig.rampantGlyphidGuidance && PollutionHandler.targetCoords != null) {
                if (!hasTarget) {
                    Vec3 targetPos = getPlayerTargetDirection();
                    if (targetPos != null) {
                        Vec3 dirVec = playerBaseDirFinder(this.position(), targetPos);

                        EntityWaypoint waypoint = new EntityWaypoint(ModEntities.WAYPOINT.get(), this.level());
                        waypoint.setPos(dirVec.x, dirVec.y, dirVec.z);
                        waypoint.maxAge = 300;
                        waypoint.radius = 6;
                        waypoint.setWaypointType(TASK_BUILD_HIVE);
                        this.level().addFreshEntity(waypoint);
                        hasTarget = true;

                        setCurrentTask(TASK_RETREAT_FOR_REINFORCEMENTS, waypoint);
                    }
                }

                if (hasTarget && super.isAtDestination()) {
                    setCurrentTask(TASK_BUILD_HIVE, null);
                    hasTarget = false;
                }
            } else {
                setCurrentTask(TASK_BUILD_HIVE, null);
            }
        }

        if (getCurrentTask() == TASK_BUILD_HIVE || getCurrentTask() == TASK_TERRAFORM) {
            if (!this.level().isClientSide && !hasTarget) {
                if (hasNuclearGlyphidNearby()) {
                    setCurrentTask(TASK_TERRAFORM, null);
                }

                if (expandHive()) {
                    this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600, 1));
                    hasTarget = true;
                }
            }

            if (taskWaypoint == null && hasTarget) {
                hasTarget = false;
            }

            if (getCurrentTask() == TASK_TERRAFORM && super.isAtDestination() && canBuildHiveHere()) {
                communicate(TASK_TERRAFORM, taskWaypoint);
            }

            if (this.tickCount % 10 == 0 && isAtDestination()) {
                timer++;

                if (!this.level().isClientSide && canBuildHiveHere()) {
                    if (timer == 1) {
                        EntityWaypoint additional = new EntityWaypoint(ModEntities.WAYPOINT.get(), this.level());
                        additional.setPos(this.getX(), this.getY(), this.getZ());
                        additional.setWaypointType(TASK_IDLE);

                        EntityWaypoint home = new EntityWaypoint(ModEntities.WAYPOINT.get(), this.level());
                        home.setWaypointType(TASK_RETREAT_FOR_REINFORCEMENTS);
                        home.setAdditionalWaypoint(additional);
                        home.setPos(homeX, homeY, homeZ);
                        home.maxAge = 1200;
                        home.radius = 6;
                        this.level().addFreshEntity(home);

                        this.taskWaypoint = home;
                        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 800, 10));
                        communicate(TASK_RETREAT_FOR_REINFORCEMENTS, taskWaypoint);
                    } else if (timer >= 5) {
                        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 5.0F, Level.ExplosionInteraction.MOB);
                        this.discard();
                    } else {
                        communicate(TASK_FOLLOW, taskWaypoint);
                    }
                }
            }
        }
    }

    @Override
    public boolean expandHive() {
        RandomSource rand = this.random;

        int nestX = rand.nextInt((homeX + scoutingRange) - (homeX - scoutingRange)) + (homeX - scoutingRange);
        int nestZ = rand.nextInt((homeZ + scoutingRange) - (homeZ - scoutingRange)) + (homeZ - scoutingRange);
        int nestY = this.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, nestX, nestZ);
        BlockPos pos = new BlockPos(nestX, nestY - 1, nestZ);
        BlockState state = this.level().getBlockState(pos);
        Block b = state.getBlock();

        // Проверка дистанции от дома
        Vec3 homeVec = new Vec3(homeX, homeY, homeZ);
        Vec3 nestVec = new Vec3(nestX, nestY, nestZ);
        boolean distanceCheck = homeVec.distanceTo(nestVec) > minDistanceToHive;

        if (distanceCheck && !state.isAir() && state.isSolid() && b != ModBlocks.GLYPHID_BASE.get()) {

            if (b == ModBlocks.BASALT.get()) {
                useLargeHive = true;
                largeHiveChance /= 2;
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 3));
            }

            if (!this.level().isClientSide) {
                EntityWaypoint nest = new EntityWaypoint(ModEntities.WAYPOINT.get(), this.level());
                nest.setWaypointType(getCurrentTask());
                nest.radius = 5;

                if (useLargeHive) {
                    nest.setHighPriority();
                }

                nest.setPos(nestX, nestY, nestZ);
                this.level().addFreshEntity(nest);

                taskWaypoint = nest;

                // updates the task coordinates
                setCurrentTask(getCurrentTask(), taskWaypoint);
                communicate(TASK_BUILD_HIVE, taskWaypoint);
            }

            return true;
        }

        return false;
    }

    /**
     * Returns true if the position is far enough away from other hives.
     * Also resets the task if unsuccessful.
     */
    private boolean canBuildHiveHere() {
        int length = useLargeHive ? 16 : 8;

        for (int i = 0; i < 8; i++) {
            float angle = (float) Math.toRadians(360D / 16 * i);
            Vec3 rot = new Vec3(0, 0, length);
            rot = rot.yRot(angle);
            Vec3 pos = new Vec3(this.getX(), this.getY() + 1, this.getZ());
            Vec3 nextPos = new Vec3(this.getX() + rot.x, this.getY() + 1, this.getZ() + rot.z);

            BlockHitResult hitResult = this.level().clip(
                    new net.minecraft.world.level.ClipContext(
                            pos, nextPos,
                            net.minecraft.world.level.ClipContext.Block.COLLIDER,
                            net.minecraft.world.level.ClipContext.Fluid.NONE,
                            this
                    )
            );

            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                BlockPos blockPos = hitResult.getBlockPos();
                Block block = this.level().getBlockState(blockPos).getBlock();

                if (block == ModBlocks.GLYPHID_BASE.get()) {
                    setCurrentTask(TASK_IDLE, null);
                    hasTarget = false;
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasNuclearGlyphidNearby() {
        int radius = 8;
        AABB bb = this.getBoundingBox().inflate(radius);
        List<Entity> bugs = this.level().getEntities(this, bb);
        for (Entity e : bugs) {
            if (e instanceof EntityGlyphidNuclear) return true;
        }
        return false;
    }

    private Player findNearestPlayer() {
        return this.level().getNearestPlayer(this, 10.0D);
    }

    private Vec3 getPlayerTargetDirection() {
        Player player = this.level().getNearestPlayer(this, 300.0D);
        if (player != null) {
            return player.position();
        }
        return PollutionHandler.targetCoords;
    }

    private Vec3 playerBaseDirFinder(Vec3 current, Vec3 target) {
        Vec3 dir = current.subtract(target).normalize();
        return new Vec3(
                current.x + dir.x * 10,
                current.y + dir.y * 10,
                current.z + dir.z * 10
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityGlyphid.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }
}