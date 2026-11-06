package com.hbm_addons.handler;

import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.entity.mob.EntityEncounterVillager;
import com.hbm.util.RefStrings;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = RefStrings.MODID)
public class EncounterManager {

    private static final Map<Level, List<EncounterGroup>> GROUPS = new WeakHashMap<>();

    private static final int STUCK_CHECK_INTERVAL = 40;
    private static final int STUCK_MOVE_THRESHOLD_SQ = 64;
    private static final int STUCK_CHECKS_BEFORE_TELEPORT = 5;
    private static final int MAX_TELEPORTS = 2;

    /** Периодический бросок кубика: раньше вызывался из ModEventHandler ядра. */
    @SubscribeEvent
    public static void onLevelTickStart(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (event.level == null || event.level.isClientSide) return;
        EncounterSpawnHandler.rollTheDice(event.level);
    }

    public static void addGroup(Level level, EncounterGroup group) {
        GROUPS.computeIfAbsent(level, k -> new ArrayList<>()).add(group);
        debugGroup(group, "Group spawned");
    }

    public static int getActiveGroupCount(Level level) {
        List<EncounterGroup> list = GROUPS.get(level);
        if (list == null) return 0;
        list.removeIf(EncounterGroup::isEmpty);
        return list.size();
    }

    public static List<EncounterGroup> getGroupsSnapshot(Level level) {
        List<EncounterGroup> list = GROUPS.get(level);
        if (list == null) return Collections.emptyList();
        return new ArrayList<>(list);
    }

    public static void clearGroups(Level level) {
        List<EncounterGroup> list = GROUPS.get(level);
        if (list == null) return;
        for (EncounterGroup g : list) {
            for (EntityEncounterVillager v : g.members) if (v.isAlive()) v.discard();
        }
        list.clear();
        GROUPS.remove(level);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level.isClientSide) return;

        Level level = event.level;
        List<EncounterGroup> groups = GROUPS.get(level);
        if (groups == null || groups.isEmpty()) return;

        Iterator<EncounterGroup> it = groups.iterator();
        while (it.hasNext()) {
            EncounterGroup group = it.next();
            if (group.isEmpty()) {
                debugGroup(group, "Group emptied (all dead or removed)");
                it.remove();
                continue;
            }
            tickGroup(level, group);
        }
        if (groups.isEmpty()) GROUPS.remove(level);
    }

    private static void tickGroup(Level level, EncounterGroup group) {
        long now = level.getGameTime();
        group.age++;

        if (!group.testMode) {
            boolean seen = false;
            for (Player p : level.players()) {
                double r = AddonConfig.encounterViewRadius;
                if (p.distanceToSqr(group.spawnPos) < r * r) {
                    seen = true;
                    break;
                }
            }

            if (seen) {
                group.lastSeenTick = now;
            } else if (now - group.lastSeenTick > AddonConfig.encounterDespawnTicks) {
                debugGroup(group, "Despawn timeout (not seen for "
                        + (now - group.lastSeenTick) + "t)");
                despawnGroup(group, "timeout");
                return;
            }
        }

        Player target = group.getEffectiveTarget(level);
        if (group.hostile && target == null && group.primaryPlayer != null
                && !group.primaryPlayer.isAlive()) {
            debugGroup(group, "Orphan hostile group, despawning");
            despawnGroup(group, "orphan");
            return;
        }

        if (updateStuckDetection(level, group, target)) {
            return;
        }

        switch (group.goal) {
            case TRADER    -> tickTrader(group, target);
            case TRAVELER  -> { if (tickTraveler(group)) return; }
            case SCOUT     -> tickScout(group, target);
            case RAIDER    -> tickRaider(group, target);
            case AMBUSHER  -> tickAmbusher(group, target);
            case DESERTER  -> tickDeserter(group, target);
        }
    }

    /**
     * Определяет, что цель "стоит по определению" — не считается застреванием.
     * TRADER ждёт у игрока, SCOUT стоит 5 секунд, AMBUSHER всегда в укрытии.
     */
    private static boolean isStandingGoal(EncounterGroup group) {
        return switch (group.goal) {
            case TRADER -> group.traderWaitingTicks > 0;
            case SCOUT -> group.scoutPhase == 1;
            case AMBUSHER -> true;
            default -> false;
        };
    }

    private static boolean updateStuckDetection(Level level, EncounterGroup group, @Nullable Player target) {
        long now = level.getGameTime();
        boolean inCombat = target != null && group.hostile;
        boolean standing = isStandingGoal(group);

        if (now - group.lastStuckCheckTick < STUCK_CHECK_INTERVAL) {
            return false;
        }
        group.lastStuckCheckTick = now;

        if (inCombat || standing) {
            group.stuckChecks = 0;
            group.lastStuckCheckPos = null;
            return false;
        }

        Vec3 center = group.getCenter();
        if (center == null) return false;

        if (group.lastStuckCheckPos == null) {
            group.lastStuckCheckPos = center;
            group.stuckChecks = 0;
            return false;
        }

        double movedSq = center.distanceToSqr(group.lastStuckCheckPos);
        if (movedSq < STUCK_MOVE_THRESHOLD_SQ) {
            group.stuckChecks++;
        } else {
            group.stuckChecks = 0;
        }
        group.lastStuckCheckPos = center;

        if (group.stuckChecks >= STUCK_CHECKS_BEFORE_TELEPORT) {
            if (group.teleportCount >= MAX_TELEPORTS) {
                debugGroup(group, "Stuck too many times (" + group.teleportCount
                        + "/" + MAX_TELEPORTS + "), despawning");
                despawnGroup(group, "stuck_despawn");
                return true;
            }
            group.teleportCount++;
            debugGroup(group, "Stuck, teleporting (count=" + group.teleportCount
                    + "/" + MAX_TELEPORTS + ")");
            teleportGroupToViewRadius(level, group);
            group.stuckChecks = 0;
            group.lastStuckCheckPos = null;
        }
        return false;
    }

    private static void teleportGroupToViewRadius(Level level, EncounterGroup group) {
        Player target = group.getEffectiveTarget(level);
        double cx, cz;
        if (target != null) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double dist = AddonConfig.encounterViewRadius * 0.8;
            cx = target.getX() + Math.cos(angle) * dist;
            cz = target.getZ() + Math.sin(angle) * dist;
        } else {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double dist = 32;
            cx = group.spawnPos.x + Math.cos(angle) * dist;
            cz = group.spawnPos.z + Math.sin(angle) * dist;
        }

        for (EntityEncounterVillager v : group.members) {
            if (!v.isAlive()) continue;
            double sx = cx + (level.random.nextDouble() - 0.5) * 4;
            double sz = cz + (level.random.nextDouble() - 0.5) * 4;
            double sy = level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) sx, (int) sz);

            if (level instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.POOF, v.getX(), v.getY() + 1, v.getZ(),
                        5, 0.3, 0.3, 0.3, 0.01);
            }
            v.teleportTo(sx, sy, sz);
        }
    }

    private static void tickTrader(EncounterGroup group, @Nullable Player target) {
        if (target == null) return;
        for (EntityEncounterVillager v : group.members) {
            if (!v.isAlive()) continue;
            double distSq = v.distanceToSqr(target);
            if (distSq > 9.0) {
                v.getNavigation().moveTo(target, 0.8D);
                v.getLookControl().setLookAt(target, 30.0F, 30.0F);
                group.traderWaitingTicks = 0;
                continue;
            }
            v.getNavigation().stop();
            v.getLookControl().setLookAt(target, 30.0F, 30.0F);
            group.traderWaitingTicks++;
            if (group.traderWaitingTicks > 1200) {
                debugGroup(group, "TRADER waiting finished, leaving");
                despawnGroup(group, "trader_left");
                return;
            }
        }
    }

    private static boolean tickTraveler(EncounterGroup group) {
        for (EntityEncounterVillager v : group.members) {
            if (!v.isAlive()) continue;
            double tx = group.targetPoint.x;
            double tz = group.targetPoint.z;
            double ty = v.getY();
            v.getNavigation().moveTo(tx, ty, tz, 0.7D);
            double dx = v.getX() - tx;
            double dz = v.getZ() - tz;
            if (dx * dx + dz * dz < 16.0) {
                debugGroup(group, "TRAVELER reached target");
                despawnGroup(group, "reached_target");
                return true;
            }
        }
        return false;
    }

    private static void tickScout(EncounterGroup group, @Nullable Player target) {
        if (target == null) return;
        for (EntityEncounterVillager v : group.members) {
            if (!v.isAlive()) continue;
            double distSq = v.distanceToSqr(target);

            if (distSq > 1024.0 && group.scoutPhase == 0) {
                v.getNavigation().moveTo(target, 0.9D);
                v.getLookControl().setLookAt(target, 30.0F, 30.0F);
                continue;
            }
            if (group.scoutPhase == 0) {
                group.scoutPhase = 1;
                group.scoutWaitTicks = 0;
                debugGroup(group, "SCOUT reached 32 blocks, waiting 5s");
            }
            if (group.scoutPhase == 1) {
                v.getNavigation().stop();
                v.getLookControl().setLookAt(target, 30.0F, 30.0F);
                group.scoutWaitTicks++;
                if (group.scoutWaitTicks > 100) {
                    group.scoutPhase = 2;
                    debugGroup(group, "SCOUT waiting done, retreating");
                }
                continue;
            }
            if (group.scoutPhase == 2) {
                double angle = Math.atan2(v.getZ() - target.getZ(), v.getX() - target.getX());
                double rx = target.getX() + Math.cos(angle) * 60;
                double rz = target.getZ() + Math.sin(angle) * 60;
                v.getNavigation().moveTo(rx, v.getY(), rz, 1.0D);
                if (v.distanceToSqr(target) > 3600.0) {
                    despawnGroup(group, "scout_retreated");
                    return;
                }
            }
        }
    }

    private static void tickRaider(EncounterGroup group, @Nullable Player target) {
        if (target == null) {
            for (EntityEncounterVillager v : group.members) {
                if (!v.isAlive()) continue;
                if (group.lastKnownPlayerPos != null) {
                    v.getNavigation().moveTo(
                            group.lastKnownPlayerPos.x, v.getY(),
                            group.lastKnownPlayerPos.z, 1.0D);
                } else {
                    v.getNavigation().stop();
                }
            }
            return;
        }
        for (EntityEncounterVillager v : group.members) {
            if (!v.isAlive()) continue;
            boolean hasLos = v.getSensing().hasLineOfSight(target);
            double distSq = v.distanceToSqr(target);
            if (hasLos) {
                v.setTarget(target);
                group.lastKnownPlayerPos = target.position();
                if (distSq > 4.0) {
                    v.getNavigation().moveTo(target, 1.0D);
                }
            } else {
                if (group.lastKnownPlayerPos != null) {
                    v.getNavigation().moveTo(
                            group.lastKnownPlayerPos.x, v.getY(),
                            group.lastKnownPlayerPos.z, 1.0D);
                }
            }
        }
    }

    private static void tickAmbusher(EncounterGroup group, @Nullable Player target) {
        if (target == null) {
            for (EntityEncounterVillager v : group.members) {
                if (v.isAlive()) v.getNavigation().stop();
            }
            return;
        }
        for (EntityEncounterVillager v : group.members) {
            if (!v.isAlive()) continue;
            double distSq = v.distanceToSqr(target);
            if (distSq < 256.0) {
                if (v.getTarget() == null) {
                    debugGroup(group, "AMBUSHER revealed at " + (int) Math.sqrt(distSq) + "m");
                }
                v.setTarget(target);
                if (distSq > 4.0) {
                    v.getNavigation().moveTo(target, 1.0D);
                }
            } else {
                v.getNavigation().stop();
            }
        }
    }

    private static void tickDeserter(EncounterGroup group, @Nullable Player target) {
        if (target == null) return;
        for (EntityEncounterVillager v : group.members) {
            if (!v.isAlive()) continue;
            double distSq = v.distanceToSqr(target);
            if (distSq < 64.0) {
                if (v.getTarget() == null) {
                    debugGroup(group, "DESERTER turned hostile at "
                            + (int) Math.sqrt(distSq) + "m");
                }
                v.setTarget(target);
            }
        }
    }

    private static void despawnGroup(EncounterGroup group, String reason) {
        debugGroup(group, "Group despawned: " + reason);
        for (EntityEncounterVillager v : group.members) {
            if (!v.isAlive()) continue;
            if (v.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.POOF, v.getX(), v.getY() + 1, v.getZ(),
                        8, 0.3, 0.3, 0.3, 0.01);
            }
            v.discard();
        }
        group.members.clear();
    }

    private static void debugGroup(EncounterGroup group, String msg) {
        System.out.println("HBM_ENCOUNTER: [" + group.goal + "/"
                + (group.hostile ? "HOSTILE" : "FRIENDLY")
                + "] " + msg);

        Player primary = group.primaryPlayer;
        if (primary instanceof ServerPlayer sp && sp.isAlive()) {
            sp.sendSystemMessage(Component.literal(
                    "[Encounter] " + group.goal + " "
                            + (group.hostile ? "HOSTILE" : "FRIENDLY")
                            + ": " + msg));
        }
    }
}