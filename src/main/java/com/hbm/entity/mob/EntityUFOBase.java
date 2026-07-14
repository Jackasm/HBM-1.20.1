package com.hbm.entity.mob;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class EntityUFOBase extends FlyingMob implements Enemy {

    private static final EntityDataAccessor<Integer> WAYPOINT_X = SynchedEntityData.defineId(EntityUFOBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAYPOINT_Y = SynchedEntityData.defineId(EntityUFOBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAYPOINT_Z = SynchedEntityData.defineId(EntityUFOBase.class, EntityDataSerializers.INT);

    protected int scanCooldown;
    protected int courseChangeCooldown;
    protected Entity target;

    public EntityUFOBase(EntityType<? extends FlyingMob> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WAYPOINT_X, 0);
        this.entityData.define(WAYPOINT_Y, 0);
        this.entityData.define(WAYPOINT_Z, 0);
    }

    @Override
    protected void customServerAiStep() {
        if (!this.level().isClientSide) {
            if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
                this.discard();
                return;
            }
        }

        this.setDeltaMovement(0, 0, 0);

        if (this.target != null && !this.target.isAlive()) {
            this.target = null;
        }

        scanForTarget();

        if (this.courseChangeCooldown <= 0) {
            setCourse();
        }

        super.customServerAiStep();
    }

    protected void scanForTarget() {
        int range = getScanRange();

        if (this.scanCooldown <= 0) {
            AABB box = this.getBoundingBox().inflate(range, range / 2, range);
            List<Entity> entities = this.level().getEntities(this, box, e -> canAttackClass(e.getClass()));

            this.target = null;

            for (Entity entity : entities) {
                if (!entity.isAlive()) continue;

                if (entity instanceof Player player) {
                    if (player.getAbilities().instabuild) continue;
                    if (player.hasEffect(MobEffects.INVISIBILITY)) continue;

                    if (this.target == null) {
                        this.target = entity;
                    } else {
                        if (this.distanceToSqr(entity) < this.distanceToSqr(this.target)) {
                            this.target = entity;
                        }
                    }
                }
            }

            this.scanCooldown = getScanDelay();
        }
    }

    protected int getScanRange() {
        return 50;
    }

    protected int getScanDelay() {
        return 100;
    }

    protected boolean canAttackClass(Class<?> clazz) {
        return true;
    }

    protected boolean isCourseTraversable(double targetX, double targetY, double targetZ, double distance) {
        double d4 = (targetX - this.getX()) / distance;
        double d5 = (targetY - this.getY()) / distance;
        double d6 = (targetZ - this.getZ()) / distance;
        AABB aabb = this.getBoundingBox();

        for (int i = 1; i < distance; ++i) {
            aabb = aabb.move(d4, d5, d6);
            if (!this.level().getBlockCollisions(this, aabb).iterator().hasNext()) {
                return false;
            }
        }

        return true;
    }

    protected void approachPosition(double speed) {
        double deltaX = getWaypointX() - this.getX();
        double deltaY = getWaypointY() - this.getY();
        double deltaZ = getWaypointZ() - this.getZ();
        Vec3 delta = new Vec3(deltaX, deltaY, deltaZ);
        double len = delta.length();

        if (len > 5) {
            if (isCourseTraversable(getWaypointX(), getWaypointY(), getWaypointZ(), len)) {
                this.setDeltaMovement(
                        delta.x * speed / len,
                        delta.y * speed / len,
                        delta.z * speed / len
                );
            } else {
                this.courseChangeCooldown = 0;
            }
        }
    }

    protected void setCourse() {
        if (this.target != null) {
            setCourseForTarget();
            this.courseChangeCooldown = 20 + this.random.nextInt(20);
        } else {
            setCourseWithoutTarget();
            this.courseChangeCooldown = 60 + this.random.nextInt(20);
        }
    }

    protected void setCourseForTarget() {
        Vec3 vec = new Vec3(
                this.getX() - this.target.getX(),
                0,
                this.getZ() - this.target.getZ()
        );
        vec = vec.yRot((float) (Math.PI * 2 * this.random.nextFloat()));

        double length = vec.length();
        double overshoot = 10 + this.random.nextDouble() * 10;

        int wX = (int) Math.floor(this.target.getX() - vec.x / length * overshoot);
        int wZ = (int) Math.floor(this.target.getZ() - vec.z / length * overshoot);
        int wY = Math.max(this.level().getHeight(Heightmap.Types.WORLD_SURFACE, wX, wZ), (int) this.target.getY()) + targetHeightOffset();

        setWaypoint(wX, wY, wZ);
    }

    protected int targetHeightOffset() {
        return 2 + this.random.nextInt(2);
    }

    protected int wanderHeightOffset() {
        return 2 + this.random.nextInt(3);
    }

    protected void setCourseWithoutTarget() {
        int x = (int) Math.floor(this.getX() + this.random.nextGaussian() * 5);
        int z = (int) Math.floor(this.getZ() + this.random.nextGaussian() * 5);
        int y = this.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + wanderHeightOffset();
        setWaypoint(x, y, z);
    }

    public void setWaypoint(int x, int y, int z) {
        this.entityData.set(WAYPOINT_X, x);
        this.entityData.set(WAYPOINT_Y, y);
        this.entityData.set(WAYPOINT_Z, z);
    }

    public int getWaypointX() {
        return this.entityData.get(WAYPOINT_X);
    }

    public int getWaypointY() {
        return this.entityData.get(WAYPOINT_Y);
    }

    public int getWaypointZ() {
        return this.entityData.get(WAYPOINT_Z);
    }
}