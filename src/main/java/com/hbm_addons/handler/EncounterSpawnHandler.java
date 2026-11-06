package com.hbm_addons.handler;

import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.main.AddonRegistry;
import com.hbm_addons.entity.ai.EncounterGoal;
import com.hbm_addons.entity.mob.EntityEncounterVillager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;

public class EncounterSpawnHandler {

    private static final int LOS_MAX_ATTEMPTS = 5;

    public static void rollTheDice(Level level) {
        if (level.isClientSide || !AddonConfig.enableEncounters) return;
        if (level.players().isEmpty()) return;

        long time = level.getGameTime();
        if (time % AddonConfig.encounterDelay != 0) return;
        if (level.random.nextInt(AddonConfig.encounterChance) != 0) return;

        if (EncounterManager.getActiveGroupCount(level) >= AddonConfig.encounterMaxGroups) return;

        Player player = level.players().get(level.random.nextInt(level.players().size()));
        spawnForPlayer(level, player, null, null);
    }

    @Nullable
    public static EncounterGroup spawnForPlayer(Level level, Player player,
                                                @Nullable EncounterGoal forcedGoal,
                                                @Nullable Boolean forcedHostile) {
        if (AddonConfig.encounterDayOnly && !level.isDay()) return null;
        if (AddonConfig.encounterSurfaceOnly) {
            int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE,
                    (int) player.getX(), (int) player.getZ());
            if (surface > player.getY() + 3) return null;
        }

        // === Разрешение фракции ===
        // Если задан forcedGoal — фракция выводится из него.
        // Если forcedHostile явно задан и goal не задан — используется он.
        // Иначе — ролл по репутации.
        boolean hostile;
        if (forcedGoal != null) {
            hostile = isHostileGoal(forcedGoal);
        } else if (forcedHostile != null) {
            hostile = forcedHostile;
        } else {
            double hostileChance = EncounterReputation.getHostilityChance(player);
            hostile = level.random.nextDouble() < hostileChance;
        }

        // === Разрешение цели ===
        EncounterGoal goal;
        if (forcedGoal != null) {
            goal = forcedGoal;
        } else {
            goal = hostile
                    ? (level.random.nextBoolean() ? EncounterGoal.RAIDER : EncounterGoal.SCOUT)
                    : (level.random.nextBoolean() ? EncounterGoal.TRADER : EncounterGoal.TRAVELER);
        }

        // Согласованность: если goal friendly, а фракция hostile — форсим friendly
        // (наоборот тоже работает). Это защита от вызова вида hostile + trader.
        if (hostile != isHostileGoal(goal)) {
            hostile = isHostileGoal(goal);
        }

        Vec3 spawnPos = pickSpawnPos(level, player);
        if (spawnPos == null) {
            System.out.println("HBM_ENCOUNTER: Spawn skipped: no valid position after "
                    + LOS_MAX_ATTEMPTS + " LOS attempts");
            return null;
        }

        int min = AddonConfig.encounterGroupMin;
        int max = AddonConfig.encounterGroupMax;
        if (hostile && max > min) max += 1;
        int count = min + level.random.nextInt(Math.max(1, max - min + 1));

        Vec3 targetPoint;
        if (!hostile) {
            double angleToPlayer = Math.atan2(spawnPos.z - player.getZ(), spawnPos.x - player.getX());
            double travelDist = AddonConfig.encounterViewRadius * 2.0;
            double tx = spawnPos.x + Math.cos(angleToPlayer) * travelDist;
            double tz = spawnPos.z + Math.sin(angleToPlayer) * travelDist;
            targetPoint = new Vec3(tx, spawnPos.y, tz);
        } else {
            targetPoint = player.position();
        }

        EncounterGroup group = new EncounterGroup(level, player, goal, hostile, spawnPos, targetPoint);
        spawnMembers(level, group, spawnPos.x, spawnPos.z, spawnPos.y, count, hostile, goal);

        if (!group.members.isEmpty()) {
            EncounterManager.addGroup(level, group);
            return group;
        }
        return null;
    }

    /** Определяет, является ли цель hostile по своей природе. */
    public static boolean isHostileGoal(EncounterGoal goal) {
        return switch (goal) {
            case RAIDER, SCOUT, AMBUSHER -> true;
            case TRADER, TRAVELER, DESERTER -> false;
        };
    }

    /** Форсированный спавн для команды /hbm encounter. */
    @Nullable
    public static EncounterGroup forceSpawn(Level level, Player player,
                                            @Nullable EncounterGoal goal,
                                            @Nullable Boolean hostile,
                                            @Nullable Integer countOverride) {
        boolean hostileResolved;
        if (goal != null) {
            hostileResolved = isHostileGoal(goal);
        } else if (hostile != null) {
            hostileResolved = hostile;
        } else {
            double hostileChance = EncounterReputation.getHostilityChance(player);
            hostileResolved = level.random.nextDouble() < hostileChance;
        }

        EncounterGoal goalResolved;
        goalResolved = Objects.requireNonNullElseGet(goal, () -> hostileResolved
                ? (level.random.nextBoolean() ? EncounterGoal.RAIDER : EncounterGoal.SCOUT)
                : (level.random.nextBoolean() ? EncounterGoal.TRADER : EncounterGoal.TRAVELER));

        Vec3 spawnPos = pickSpawnPos(level, player);
        if (spawnPos == null) {
            System.out.println("HBM_ENCOUNTER: forceSpawn skipped: no valid LOS position");
            return null;
        }

        int count;
        if (countOverride != null) {
            count = Math.max(1, countOverride);
        } else {
            int min = AddonConfig.encounterGroupMin;
            int max = AddonConfig.encounterGroupMax;
            if (hostileResolved && max > min) max += 1;
            count = min + level.random.nextInt(Math.max(1, max - min + 1));
        }

        Vec3 targetPoint;
        if (!hostileResolved) {
            double angleToPlayer = Math.atan2(spawnPos.z - player.getZ(), spawnPos.x - player.getX());
            double travelDist = AddonConfig.encounterViewRadius * 2.0;
            double tx = spawnPos.x + Math.cos(angleToPlayer) * travelDist;
            double tz = spawnPos.z + Math.sin(angleToPlayer) * travelDist;
            targetPoint = new Vec3(tx, spawnPos.y, tz);
        } else {
            targetPoint = player.position();
        }

        EncounterGroup group = new EncounterGroup(level, player, goalResolved, hostileResolved,
                spawnPos, targetPoint);
        spawnMembers(level, group, spawnPos.x, spawnPos.z, spawnPos.y, count, hostileResolved, goalResolved);

        if (!group.members.isEmpty()) {
            EncounterManager.addGroup(level, group);
            return group;
        }
        return null;
    }

    @Nullable
    private static Vec3 pickSpawnPos(Level level, Player player) {
        Vec3 motion = player.getDeltaMovement();
        boolean moving = motion.horizontalDistanceSqr() > 0.0001;
        double motionAngle = moving ? Math.atan2(motion.z, motion.x) : 0;

        for (int attempt = 0; attempt < LOS_MAX_ATTEMPTS; attempt++) {
            double angle;
            if (moving) {
                double roll = level.random.nextDouble();
                if (roll < 0.5) {
                    angle = motionAngle + (level.random.nextDouble() - 0.5) * Math.PI * 0.5;
                } else if (roll < 0.8) {
                    angle = motionAngle + (level.random.nextBoolean() ? 1 : -1) * Math.PI * 0.5
                            + (level.random.nextDouble() - 0.5) * Math.PI * 0.25;
                } else {
                    angle = motionAngle + Math.PI
                            + (level.random.nextDouble() - 0.5) * Math.PI * 0.5;
                }
            } else {
                angle = level.random.nextDouble() * Math.PI * 2;
            }

            double dist = AddonConfig.encounterMinDist
                    + level.random.nextDouble()
                    * (AddonConfig.encounterMaxDist - AddonConfig.encounterMinDist);

            double cx = player.getX() + Math.cos(angle) * dist;
            double cz = player.getZ() + Math.sin(angle) * dist;

            // MOTION_BLOCKING_NO_LEAVES — игнорирует листву
            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (int) Math.floor(cx), (int) Math.floor(cz));

            Vec3 candidate = new Vec3(cx, surfaceY, cz);

            if (isValidSpawnPoint(level, player, candidate)) {

                return candidate;
            }

            System.out.println("HBM_ENCOUNTER: attempt " + (attempt + 1)
                    + " rejected at "
                    + String.format(Locale.ROOT, "%.1f,%d,%.1f", cx, surfaceY, cz));
        }
        return null;
    }

    private static boolean isValidSpawnPoint(Level level, Player player, Vec3 pos) {
        // 1. Проверка LOS — только если точка в поле зрения игрока
        Vec3 look = player.getLookAngle();
        Vec3 dirToPoint = new Vec3(pos.x - player.getX(), 0, pos.z - player.getZ()).normalize();
        double dot = look.x * dirToPoint.x + look.z * dirToPoint.z;

        if (dot > 0.5) {
            Vec3 eye = player.getEyePosition();
            Vec3 target = new Vec3(pos.x, pos.y + 1.0, pos.z);
            BlockHitResult hit = level.clip(new ClipContext(
                    eye, target,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player));
            if (hit.getType() != HitResult.Type.MISS) {
                return false;
            }
        }

        // 2. Проверка границ мира
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        BlockPos below = BlockPos.containing(pos.x, pos.y - 1, pos.z);
        BlockPos at = BlockPos.containing(pos.x, pos.y, pos.z);

        // Если точка вне вертикальных границ мира — скипаем
        if (at.getY() < minY || at.getY() >= maxY - 1) return false;
        if (below.getY() < minY || below.getY() >= maxY) return false;

        // 3. Под точкой должна быть поверхность, на которую можно встать
        if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
            return false;
        }

        // 4. Сама точка и блок над ней — воздух
        //if (!level.getBlockState(at).isAir()) return false;
        if (!level.getBlockState(at.above()).isAir()) return false;

        // 5. Не в воде/лаве
        if (level.getFluidState(at).isSource()) return false;

        return true;
    }

    public static EncounterGroup debugSpawnGroup(ServerLevel level, Vec3 center,
                                                 EncounterGoal goal, boolean hostile, int count) {
        Vec3 targetPoint = hostile
                ? new Vec3(center.x, level.getHeight(Heightmap.Types.WORLD_SURFACE,
                (int) center.x, (int) center.z), center.z)
                : new Vec3(center.x + 50,
                level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) center.x, (int) center.z),
                center.z + 50);
        return debugSpawnGroupWithTarget(level, center, goal, hostile, count, targetPoint);
    }

    public static EncounterGroup debugSpawnGroupWithTarget(ServerLevel level, Vec3 center,
                                                           EncounterGoal goal, boolean hostile,
                                                           int count, Vec3 targetPoint) {
        double ty = level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) center.x, (int) center.z);
        Vec3 spawnPos = new Vec3(center.x, ty, center.z);
        Vec3 adjustedTarget = new Vec3(targetPoint.x, spawnPos.y, targetPoint.z);

        EncounterGroup group = new EncounterGroup(level, null, goal, hostile, spawnPos, adjustedTarget);
        group.testMode = true;
        spawnMembers(level, group, center.x, center.z, ty, count, hostile, goal);

        if (!group.members.isEmpty()) {
            EncounterManager.addGroup(level, group);
        }
        return group;
    }

    @Nullable
    public static EncounterGroup debugTrySpawnGroup(Level level, Player player,
                                                    EncounterGoal goal, boolean hostile) {
        return spawnForPlayer(level, player, goal, hostile);
    }

    private static void spawnMembers(Level level, EncounterGroup group,
                                     double cx, double cz, double cy,
                                     int count, boolean hostile, EncounterGoal goal) {
        for (int i = 0; i < count; i++) {
            double sx = cx + level.random.nextGaussian() * 2.5;
            double sz = cz + level.random.nextGaussian() * 2.5;
            double sy = level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) sx, (int) sz);

            EntityEncounterVillager v = new EntityEncounterVillager(
                    AddonRegistry.ENCOUNTER_VILLAGER.get(), level);
            v.setPos(sx, sy, sz);
            v.setYRot(level.random.nextFloat() * 360.0F);
            v.setXRot(0);
            v.setFaction(hostile
                    ? EntityEncounterVillager.FACTION_HOSTILE
                    : EntityEncounterVillager.FACTION_FRIENDLY);
            v.setGoal(goal);
            v.equipForSpawn();

            if (v.checkSpawnRules(level, MobSpawnType.EVENT)) {
                level.addFreshEntity(v);
                group.members.add(v);
            }
        }
    }
}