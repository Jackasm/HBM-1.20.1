package com.hbm_addons.test;

import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.entity.ai.EncounterGoal;
import com.hbm_addons.entity.mob.EntityEncounterVillager;
import com.hbm_addons.handler.EncounterGroup;
import com.hbm_addons.handler.EncounterManager;
import com.hbm_addons.handler.EncounterReputation;
import com.hbm_addons.handler.EncounterSpawnHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

/**
 * Комбинированный gameplay-тест энкаунтеров.
 * Стадии:
 *   1. Спавн TRAVELER → проверка сущности
 *   2. Спавн RAIDER + TRAVELER → проверка фракций
 *   3. Репутация через FakePlayer
 *   4. Движение TRAVELER к targetPoint
 *   5. Деспавн группы без игроков рядом
 *   6. Финальная проверка чистоты состояния
 */
public class EncounterFullTestRunner extends TestRunner {

    /* Стадии по тикам (относительно tickCount) */
    private static final int STAGE_SPAWN_FRIENDLY   = 5;
    private static final int STAGE_CHECK_FRIENDLY   = 40;
    private static final int STAGE_SPAWN_HOSTILE    = 45;
    private static final int STAGE_CHECK_HOSTILE    = 80;
    private static final int STAGE_REPUTATION       = 85;
    private static final int STAGE_MOVEMENT_START   = 100;
    private static final int STAGE_MOVEMENT_CHECK   = 320;
    private static final int STAGE_DESPAWN_SETUP    = 325;
    private static final int STAGE_DESPAWN_CHECK    = 500;
    private static final int STAGE_FINAL            = 510;

    private EncounterGroup friendlyGroup;
    private EncounterGroup hostileGroup;
    private EncounterGroup travelGroup;
    private EncounterGroup despawnGroup;

    private Vec3 travelerStartPos;
    private double travelerStartDist;

    private FakePlayer fakePlayer;
    private FakePlayer sentinelPlayer;
    private double baselineHostility;

    private int stageDespawnStartTick;

    public EncounterFullTestRunner(ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (tickCount == 1) {
            progress("start");
            EncounterManager.clearGroups(level);
        }

        if (tickCount == STAGE_SPAWN_FRIENDLY) {
            progress("stage_spawn_friendly");
            friendlyGroup = EncounterSpawnHandler.debugSpawnGroup(level,
                    new Vec3(0.5, 0, 0.5), EncounterGoal.TRAVELER, false, 2);
            info("friendly_spawned", friendlyGroup.members.size());
        }

        if (tickCount == STAGE_CHECK_FRIENDLY) {
            progress("stage_check_friendly");
            check("friendly_group_not_empty", !friendlyGroup.members.isEmpty());
            if (!friendlyGroup.members.isEmpty()) {
                EntityEncounterVillager v = friendlyGroup.members.get(0);
                check("friendly_not_hostile", !v.isHostile());
                check("friendly_faction", v.getFaction() == EntityEncounterVillager.FACTION_FRIENDLY);
                check("friendly_goal", v.getGoal() == EncounterGoal.TRAVELER);
                check("friendly_health", v.getHealth() > 0);
                check("friendly_target_null", v.getTarget() == null);
            }
        }

        if (tickCount == STAGE_SPAWN_HOSTILE) {
            progress("stage_spawn_hostile");
            hostileGroup = EncounterSpawnHandler.debugSpawnGroup(level,
                    new Vec3(60.5, 0, 60.5), EncounterGoal.RAIDER, true, 2);
            info("hostile_spawned", hostileGroup.members.size());
        }

        if (tickCount == STAGE_CHECK_HOSTILE) {
            progress("stage_check_hostile");
            check("hostile_group_not_empty", !hostileGroup.members.isEmpty());
            if (!hostileGroup.members.isEmpty()) {
                EntityEncounterVillager v = hostileGroup.members.get(0);
                check("hostile_is_hostile", v.isHostile());
                check("hostile_faction", v.getFaction() == EntityEncounterVillager.FACTION_HOSTILE);
                check("hostile_goal", v.getGoal() == EncounterGoal.RAIDER);
            }
            if (!friendlyGroup.members.isEmpty() && !hostileGroup.members.isEmpty()) {
                check("factions_differ",
                        friendlyGroup.members.get(0).getFaction()
                                != hostileGroup.members.get(0).getFaction());
            }
        }

        if (tickCount == STAGE_REPUTATION) {
            progress("stage_reputation");
            fakePlayer = new FakePlayer(level,
                    new GameProfile(UUID.randomUUID(), "enc_test_player"));
            baselineHostility = EncounterReputation.getHostilityChance(fakePlayer);
            info("baseline_hostility", baselineHostility);

            EncounterReputation.onFriendlyKilled(fakePlayer);
            EncounterReputation.onFriendlyKilled(fakePlayer);
            EncounterReputation.onFriendlyKilled(fakePlayer);

            int kills = fakePlayer.getPersistentData().getInt(EncounterReputation.KEY_KILLS);
            double after = EncounterReputation.getHostilityChance(fakePlayer);
            info("after_hostility", after);
            metric("kills_recorded", kills);
            metric("baseline_hostility", baselineHostility);
            metric("after_hostility", after);

            check("rep_kills_3", kills == 3);
            check("rep_hostility_up", after > baselineHostility);
            double expected = baselineHostility + 3 * AddonConfig.encounterHostilePerKill;
            check("rep_formula", after >= expected - 0.01);

            for (int i = 0; i < 100; i++) EncounterReputation.onFriendlyKilled(fakePlayer);
            double capped = EncounterReputation.getHostilityChance(fakePlayer);
            check("rep_capped", capped <= AddonConfig.encounterHostileMax + 0.001);
        }

        if (tickCount == STAGE_MOVEMENT_START) {
            progress("stage_movement_start");
            travelGroup = EncounterSpawnHandler.debugSpawnGroup(level,
                    new Vec3(-40.5, 0, -40.5), EncounterGoal.TRAVELER, false, 1);
            if (!travelGroup.members.isEmpty()) {
                travelerStartPos = travelGroup.members.get(0).position();
                travelerStartDist = travelerStartPos.distanceTo(travelGroup.targetPoint);
                info("traveler_start_dist", travelerStartDist);
            }
            // Sentinel-игрок рядом со spawnPos, чтобы группа была "видима"
            sentinelPlayer = new FakePlayer(level,
                    new GameProfile(UUID.randomUUID(), "enc_sentinel"));
            sentinelPlayer.setPos(travelGroup.spawnPos.x, travelGroup.spawnPos.y, travelGroup.spawnPos.z);
            level.addFreshEntity(sentinelPlayer);
            info("sentinel_spawned", sentinelPlayer.position());
        }

        if (tickCount == STAGE_MOVEMENT_CHECK) {
            progress("stage_movement_check");

            // Убираем sentinel — больше не нужен
            if (sentinelPlayer != null && sentinelPlayer.isAlive()) {
                sentinelPlayer.discard();
            }

            if (travelGroup == null) {
                check("traveler_group_exists", false);
            } else if (travelGroup.members.isEmpty()) {
                check("traveler_group_exists", false);
                info("traveler_empty_reason", "members emptied by despawn or death");
            } else {
                check("traveler_alive", true);
                EntityEncounterVillager v = travelGroup.members.get(0);
                double finalDist = v.position().distanceTo(travelGroup.targetPoint);
                double moved = travelerStartPos.distanceTo(v.position());

                info("traveler_final_dist", finalDist);
                info("traveler_moved", moved);
                metric("traveler_start_dist", travelerStartDist);
                metric("traveler_final_dist", finalDist);
                metric("traveler_moved", moved);

                check("traveler_moved", moved > 1.0);
                check("traveler_toward_target", finalDist < travelerStartDist - 1.0);
            }
        }

        if (tickCount == STAGE_DESPAWN_SETUP) {
            progress("stage_despawn_setup");
            EncounterManager.clearGroups(level);
            despawnGroup = EncounterSpawnHandler.debugSpawnGroup(level,
                    new Vec3(200.5, 0, 200.5), EncounterGoal.SCOUT, true, 2);
            // ВАЖНО: сбрасываем testMode, чтобы проверить деспавн по таймауту
            despawnGroup.testMode = false;
            stageDespawnStartTick = tickCount;
            info("despawn_initial", despawnGroup.members.size());
        }

        if (tickCount == STAGE_DESPAWN_CHECK) {
            progress("stage_despawn_check");
            int elapsed = tickCount - stageDespawnStartTick;
            int alive = level.getEntitiesOfClass(EntityEncounterVillager.class,
                    new AABB(-500, -50, -500, 500, 400, 500)).size();
            int groups = EncounterManager.getActiveGroupCount(level);

            info("despawn_elapsed", elapsed);
            info("despawn_alive", alive);
            info("despawn_groups", groups);
            metric("despawn_elapsed", elapsed);
            metric("despawn_alive", alive);
            metric("despawn_groups", groups);

            check("despawn_all_gone", alive == 0);
            check("despawn_groups_cleared", groups == 0);
        }

        if (tickCount == STAGE_FINAL) {
            progress("stage_final");
            int alive = level.getEntitiesOfClass(EntityEncounterVillager.class,
                    new AABB(-1000, -100, -1000, 1000, 400, 1000)).size();
            metric("final_alive", alive);
            check("final_clean", alive == 0);
            check("final_groups_zero", EncounterManager.getActiveGroupCount(level) == 0);

            metric("checks_total", checks.size());
            metric("checks_passed", (int) checks.stream().filter(c -> c.pass).count());

            boolean allPass = checks.stream().allMatch(c -> c.pass);
            finish(allPass);
            return true;
        }

        return false;
    }
                             }
