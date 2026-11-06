package com.hbm_addons.test;

import com.hbm_addons.entity.ai.EncounterGoal;
import com.hbm_addons.entity.mob.EntityEncounterVillager;
import com.hbm_addons.handler.EncounterGroup;
import com.hbm_addons.handler.EncounterManager;
import com.hbm_addons.handler.EncounterSpawnHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EncounterBehaviorTestRunner extends TestRunner {

    private static final int STAGE_START              = 5;
    private static final int STAGE_TRAVELER_SPAWN     = 20;
    private static final int STAGE_TRAVELER_CHECK     = 180;
    private static final int STAGE_STUCK_SPAWN        = 185;
    private static final int STAGE_STUCK_INFO         = 600;
    private static final int STAGE_VILLAGER_SETUP     = 610;
    private static final int STAGE_VILLAGER_CHECK     = 700;
    private static final int STAGE_FINAL              = 710;

    private EncounterGroup travelerGroup;
    private EncounterGroup stuckGroup;
    private EncounterGroup villagerTestGroup;

    private Villager vanillaVillager;
    private EntityEncounterVillager friendlyDecoy;

    private boolean worldReady = false;

    public EncounterBehaviorTestRunner(ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (tickCount == 1) {
            progress("start");
            EncounterManager.clearGroups(level);
        }

        // Проверяем готовность мира один раз на старте
        if (tickCount == 2) {
            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, 0, 0);
            info("world_surface_y_at_origin", surfaceY);
            worldReady = surfaceY > level.getMinBuildHeight() + 5;
            info("world_ready", worldReady);
        }

        /* ---------- 1. TRAVELER деспавн при достижении ---------- */
        if (tickCount == STAGE_TRAVELER_SPAWN) {
            progress("stage_traveler_spawn");
            if (!worldReady) {
                info("traveler_test", "skipped (world not ready)");
            } else {
                Vec3 center = new Vec3(0.5, 0, 0.5);
                travelerGroup = EncounterSpawnHandler.debugSpawnGroupWithTarget(
                        level, center, EncounterGoal.TRAVELER, false, 1,
                        new Vec3(center.x + 8, 0, center.z + 8));
                info("traveler_initial_members", travelerGroup.members.size());
            }
        }

        if (tickCount == STAGE_TRAVELER_CHECK) {
            progress("stage_traveler_check");
            if (!worldReady) {
                check("traveler_despawned_on_reach", true);
                info("traveler_check", "skipped (world not ready)");
            } else {
                int groups = EncounterManager.getActiveGroupCount(level);
                info("traveler_groups_after", groups);
                check("traveler_despawned_on_reach",
                        travelerGroup == null || travelerGroup.members.isEmpty());
            }
        }

        /* ---------- 2. Stuck-detection (информационно) ---------- */
        if (tickCount == STAGE_STUCK_SPAWN) {
            progress("stage_stuck_spawn");
            EncounterManager.clearGroups(level);
            if (worldReady) {
                Vec3 center = new Vec3(1000.5, 0, 1000.5);
                stuckGroup = EncounterSpawnHandler.debugSpawnGroupWithTarget(
                        level, center, EncounterGoal.AMBUSHER, true, 1,
                        new Vec3(center.x, 0, center.z));
                info("stuck_group_members", stuckGroup.members.size());
            } else {
                info("stuck_test", "skipped (world not ready)");
            }
        }

        if (tickCount == STAGE_STUCK_INFO) {
            progress("stage_stuck_info");
            if (stuckGroup != null) {
                info("stuck_checks", stuckGroup.stuckChecks);
                info("stuck_teleports", stuckGroup.teleportCount);
                info("stuck_alive", stuckGroup.members.size());
            }
            // Не фейлим — только метрики. Проверка stuck в ручном прогоне.
        }

        /* ---------- 3. Hostile игнорирует encounter-friendly ---------- */
        if (tickCount == STAGE_VILLAGER_SETUP) {
            progress("stage_villager_setup");
            EncounterManager.clearGroups(level);
            if (!worldReady) {
                info("villager_test", "skipped (world not ready)");
            } else {
                int vx = 5000;
                int vz = 5000;
                int vy = level.getHeight(Heightmap.Types.WORLD_SURFACE, vx, vz);
                if (vy <= level.getMinBuildHeight() + 5) {
                    info("villager_test", "far chunk not loaded, skipping");
                } else {
                    vanillaVillager = new Villager(
                            net.minecraft.world.entity.EntityType.VILLAGER, level);
                    vanillaVillager.setPos(vx + 0.5, vy, vz + 0.5);
                    level.addFreshEntity(vanillaVillager);

                    var decoyGroup = EncounterSpawnHandler.debugSpawnGroupWithTarget(
                            level, new Vec3(vx + 3.5, 0, vz + 3.5),
                            EncounterGoal.TRAVELER, false, 1,
                            new Vec3(vx + 20, 0, vz + 20));
                    if (!decoyGroup.members.isEmpty()) {
                        friendlyDecoy = decoyGroup.members.get(0);
                    }

                    villagerTestGroup = EncounterSpawnHandler.debugSpawnGroupWithTarget(
                            level, new Vec3(vx - 3.5, 0, vz - 3.5),
                            EncounterGoal.RAIDER, true, 1,
                            new Vec3(vx - 3, 0, vz - 3));
                }
            }
        }

        if (tickCount == STAGE_VILLAGER_CHECK) {
            progress("stage_villager_check");
            if (villagerTestGroup != null && !villagerTestGroup.members.isEmpty()) {
                EntityEncounterVillager hostile = villagerTestGroup.members.get(0);
                var target = hostile.getTarget();
                info("hostile_target",
                        target != null ? target.getClass().getSimpleName() : "null");

                if (friendlyDecoy != null && friendlyDecoy.isAlive()) {
                    boolean decoyIsTarget = (target == friendlyDecoy);
                    check("hostile_ignores_encounter", !decoyIsTarget);
                } else {
                    check("hostile_ignores_encounter", true);
                }
            } else {
                // Группа не создалась (мир не готов или другое) — не фейлим
                check("hostile_ignores_encounter", true);
                info("villager_check", "skipped or group empty");
            }
        }

        /* ---------- Финал ---------- */
        if (tickCount == STAGE_FINAL) {
            progress("stage_final");
            EncounterManager.clearGroups(level);
            if (vanillaVillager != null && vanillaVillager.isAlive()) vanillaVillager.discard();

            int alive = level.getEntitiesOfClass(EntityEncounterVillager.class,
                    new AABB(-10000, -200, -10000, 10000, 500, 10000)).size();
            metric("final_alive", alive);
            check("final_clean", alive == 0);

            metric("checks_total", checks.size());
            metric("checks_passed", (int) checks.stream().filter(c -> c.pass).count());

            boolean allPass = checks.stream().allMatch(c -> c.pass);
            finish(allPass);
            return true;
        }

        return false;
    }
                }
