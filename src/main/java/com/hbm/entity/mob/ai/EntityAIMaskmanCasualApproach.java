package com.hbm.entity.mob.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EntityAIMaskmanCasualApproach extends Goal {

    private final Mob attacker;
    private final double speedTowardsTarget;
    private final boolean longMemory;
    private Class<? extends LivingEntity> classTarget;
    private int pathTimer;
    private double lastX;
    private double lastY;
    private double lastZ;
    private int failedPathFindingPenalty;
    private Path entityPathEntity;

    public EntityAIMaskmanCasualApproach(Mob owner, Class<? extends LivingEntity> target, double speed, boolean longMemory) {
        this(owner, speed, longMemory);
        this.classTarget = target;
    }

    public EntityAIMaskmanCasualApproach(Mob owner, double speed, boolean longMemory) {
        this.attacker = owner;
        this.speedTowardsTarget = speed;
        this.longMemory = longMemory;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.attacker.getTarget();

        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else if (this.classTarget != null && !this.classTarget.isAssignableFrom(target.getClass())) {
            return false;
        } else {
            if (--this.pathTimer <= 0) {
                double[] pos = getApproachPos();
                this.entityPathEntity = this.attacker.getNavigation().createPath(
                        BlockPos.containing(pos[0], pos[1], pos[2]), 0
                );
                this.pathTimer = 4 + this.attacker.getRandom().nextInt(7);
                return this.entityPathEntity != null;
            } else {
                return true;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.attacker.getTarget();
        return target != null &&
                target.isAlive() &&
                (this.longMemory ? this.attacker.isWithinRestriction(target.blockPosition()) :
                        !this.attacker.getNavigation().isDone());
    }

    @Override
    public void start() {
        this.attacker.getNavigation().moveTo(this.entityPathEntity, this.speedTowardsTarget);
        this.pathTimer = 0;
    }

    @Override
    public void stop() {
        this.attacker.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.attacker.getTarget();
        if (target == null) return;

        this.attacker.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double d0 = this.attacker.distanceToSqr(target);

        this.pathTimer--;

        if ((this.longMemory || this.attacker.getSensing().hasLineOfSight(target)) &&
                this.pathTimer <= 0 &&
                (this.lastX == 0.0D && this.lastY == 0.0D && this.lastZ == 0.0D ||
                        target.distanceToSqr(this.lastX, this.lastY, this.lastZ) >= 1.0D ||
                        this.attacker.getRandom().nextFloat() < 0.05F)) {

            this.lastX = target.getX();
            this.lastY = target.getY();
            this.lastZ = target.getZ();
            this.pathTimer = failedPathFindingPenalty + 4 + this.attacker.getRandom().nextInt(7);

            Path path = this.attacker.getNavigation().getPath();
            if (path != null) {
                Node finalPathPoint = path.getEndNode();
                if (finalPathPoint != null &&
                        target.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1) {
                    failedPathFindingPenalty = 0;
                } else {
                    failedPathFindingPenalty += 10;
                }
            } else {
                failedPathFindingPenalty += 10;
            }

            if (d0 > 1024.0D) {
                this.pathTimer += 10;
            } else if (d0 > 256.0D) {
                this.pathTimer += 5;
            }

            double[] pos = getApproachPos();
            if (!this.attacker.getNavigation().moveTo(pos[0], pos[1], pos[2], speedTowardsTarget)) {
                this.pathTimer += 15;
            }
        }
    }

    public double[] getApproachPos() {
        LivingEntity target = this.attacker.getTarget();
        if (target == null) return new double[]{this.attacker.getX(), this.attacker.getY(), this.attacker.getZ()};

        Vec3 vec = new Vec3(
                this.attacker.getX() - target.getX(),
                this.attacker.getY() - target.getY(),
                this.attacker.getZ() - target.getZ()
        );

        double range = Math.min(vec.length(), 20) - 10;
        vec = vec.normalize();

        double x = this.attacker.getX() + vec.x * range + this.attacker.getRandom().nextGaussian() * 2;
        double y = this.attacker.getY() + vec.y - 5 + this.attacker.getRandom().nextInt(11);
        double z = this.attacker.getZ() + vec.z * range + this.attacker.getRandom().nextGaussian() * 2;

        return new double[]{x, y, z};
    }
}