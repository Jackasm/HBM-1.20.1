package com.hbm.entity.mob.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EntityAIBreaking extends Goal {

    private LivingEntity target;
    private BlockPos markedPos;
    private final Mob entityDigger;
    private int digTick = 0;
    private int scanTick = 0;

    public EntityAIBreaking(Mob entity) {
        this.entityDigger = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        target = entityDigger.getTarget();

        if (target != null && entityDigger.getNavigation().isDone() &&
                entityDigger.distanceTo(target) > 1.0D &&
                (target.onGround() || !entityDigger.hasLineOfSight(target))) {

            BlockHitResult hit = getNextObstacle(entityDigger, 2.0D);

            if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
                return false;
            }

            BlockPos pos = hit.getBlockPos();
            BlockState state = entityDigger.level().getBlockState(pos);
            float hardness = state.getDestroySpeed(entityDigger.level(), pos);

            if (hardness >= 0) {
                markedPos = pos;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (markedPos == null) return false;

        Vec3 vector = new Vec3(
                markedPos.getX() - entityDigger.getX(),
                markedPos.getY() - (entityDigger.getY() + entityDigger.getEyeHeight()),
                markedPos.getZ() - entityDigger.getZ()
        );

        return entityDigger != null && entityDigger.isAlive() && vector.length() <= 4.0D;
    }

    @Override
    public void tick() {
        BlockHitResult hit = null;

        if (entityDigger.tickCount % 10 == 0) {
            hit = getNextObstacle(entityDigger, 2.0D);
        }

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            markedPos = hit.getBlockPos();
        }

        if (markedPos == null || entityDigger.level().getBlockState(markedPos).isAir()) {
            digTick = 0;
            return;
        }

        BlockState state = entityDigger.level().getBlockState(markedPos);
        digTick++;

        float hardness = state.getDestroySpeed(entityDigger.level(), markedPos);
        if (hardness < 0) {
            markedPos = null;
            return;
        }

        int health = (int) (hardness / 3);
        if (health < 1) health = 1;

        float progress = (digTick * 0.05F) / (float) health;

        if (progress >= 1.0F) {
            digTick = 0;

            entityDigger.level().destroyBlock(markedPos, false);
            markedPos = null;

            if (target != null) {
                entityDigger.getNavigation().moveTo(target, 1.0D);
            }
        } else {
            if (digTick % 5 == 0) {
                // Воспроизводим звук
                entityDigger.level().playSound(null, entityDigger.getX(), entityDigger.getY(), entityDigger.getZ(),
                        state.getBlock().getSoundType(state).getBreakSound(),
                        entityDigger.getSoundSource(),
                        state.getBlock().getSoundType(state).getVolume() + 1.0F,
                        state.getBlock().getSoundType(state).getPitch()
                );

                entityDigger.swing(entityDigger.getUsedItemHand());

                // Отображаем прогресс разрушения
                int progressLevel = (int) (progress * 10.0F);
                entityDigger.level().destroyBlockProgress(entityDigger.getId(), markedPos, progressLevel);
            }
        }
    }

    @Override
    public void stop() {
        markedPos = null;
        digTick = 0;
    }

    /**
     * Rolls through all the points in the bounding box of the entity and raycasts them toward its current heading
     */
    private BlockHitResult getNextObstacle(LivingEntity entity, double distance) {
        float yaw = entity.getYRot();
        float pitch = entity.getXRot();

        int width = (int) Math.ceil(entity.getBbWidth());
        int height = (int) Math.ceil(entity.getBbHeight());

        int passMax = width * width * height;
        if (passMax <= 0) passMax = 1;

        int x = scanTick % width - (width / 2);
        int y = scanTick / (width * width);
        int z = (scanTick % (width * width)) / width - (width / 2);

        double rayX = x + entity.getX();
        double rayY = y + entity.getY();
        double rayZ = z + entity.getZ();

        BlockHitResult hit = rayCastBlocks(entity.level(), rayX, rayY, rayZ, yaw, pitch, distance);

        scanTick = (scanTick + 1) % passMax;

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            float hardness = entity.level().getBlockState(pos).getDestroySpeed(entity.level(), pos);

            if (hardness >= 0) {
                scanTick = 0;
                return hit;
            }
        }

        return null;
    }

    private BlockHitResult rayCastBlocks(Level level, double x, double y, double z, float yaw, float pitch, double distance) {
        Vec3 start = new Vec3(x, y, z);

        float f3 = (float) Math.cos(Math.toRadians(-yaw) - Math.PI);
        float f4 = (float) Math.sin(Math.toRadians(-yaw) - Math.PI);
        float f5 = (float) -Math.cos(Math.toRadians(-pitch));
        float f6 = (float) Math.sin(Math.toRadians(-pitch));
        float f7 = f4 * f5;
        float f8 = f3 * f5;

        Vec3 end = start.add(
                f7 * distance,
                f6 * distance,
                f8 * distance
        );

        return level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
    }
}