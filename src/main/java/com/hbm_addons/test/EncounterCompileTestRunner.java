package com.hbm_addons.test;

import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.main.AddonRegistry;
import com.hbm_addons.entity.mob.EntityEncounterVillager;
import com.hbm_addons.handler.EncounterManager;
import com.hbm_addons.handler.EncounterReputation;
import com.hbm_addons.handler.EncounterSpawnHandler;
import com.hbm_addons.util.EncounterPools;
import com.hbm.util.ResLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Smoke-тест: загрузка классов, регистрация сущности, конфиг, пулы.
 * Живёт ~20 тиков. Ничего не спавнит.
 */
public class EncounterCompileTestRunner extends TestRunner {

    public EncounterCompileTestRunner(ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (tickCount < 5) return false;

        progress("checking_classes");

        check("feature_loaded", AddonRegistry.ENCOUNTER_VILLAGER != null);
        check("entity_type_present", AddonRegistry.ENCOUNTER_VILLAGER.isPresent());

        check("config_loaded", AddonConfig.encounterViewRadius > 0
                && AddonConfig.encounterMaxGroups > 0);

        check("pools_loaded",
                EncounterPools.ENCOUNTER_HOSTILE_GUN_POOLS != null
                        && EncounterPools.ENCOUNTER_FRIENDLY_GUN_POOLS != null
                        && EncounterPools.ENCOUNTER_LOOT_POOLS != null
                        && !EncounterPools.ENCOUNTER_FRIENDLY_ARMOR_POOLS.isEmpty()
                        && !EncounterPools.ENCOUNTER_HOSTILE_ARMOR_POOLS.isEmpty());

        boolean entityRegistered = ForgeRegistries.ENTITY_TYPES
                .containsKey(ResLocation("hbm", "encounter_villager"));
        check("entity_registered", entityRegistered);

        check("nt_target_class", classPresent("com.hbm.entity.mob.ai.EntityAINearestAttackableTargetNT"));
        check("fire_gun_class", classPresent("com.hbm.entity.mob.ai.EntityAIFireGun"));
        check("spawn_handler_class", classPresent("com.hbm_addons.handler.EncounterSpawnHandler"));
        check("manager_class", classPresent("com.hbm_addons.handler.EncounterManager"));
        check("group_class", classPresent("com.hbm_addons.handler.EncounterGroup"));
        check("reputation_class", classPresent("com.hbm_addons.handler.EncounterReputation"));
        check("goal_enum", classPresent("com.hbm_addons.entity.ai.EncounterGoal"));

        // Инстанцирование
        check("entity_instantiable", tryInstantiate());

        // API репутации
        try {
            EncounterReputation.class.getMethod("getHostilityChance",
                    net.minecraft.world.entity.player.Player.class);
            EncounterReputation.class.getMethod("onFriendlyKilled",
                    net.minecraft.world.entity.player.Player.class);
            check("reputation_api_ok", true);
        } catch (Throwable t) {
            check("reputation_api_ok", false);
            info("reputation_api_error", t.toString());
        }

        // Менеджер не падает на пустом уровне
        try {
            int n = EncounterManager.getActiveGroupCount(level);
            check("manager_safe_empty", n >= 0);
        } catch (Throwable t) {
            check("manager_safe_empty", false);
            info("manager_error", t.toString());
        }

        metric("checks_total", checks.size());
        metric("checks_passed", (int) checks.stream().filter(c -> c.pass).count());

        boolean allPass = checks.stream().allMatch(c -> c.pass);
        finish(allPass);
        return true;
    }

    private static boolean classPresent(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private boolean tryInstantiate() {
        try {
            EntityEncounterVillager v = new EntityEncounterVillager(
                    AddonRegistry.ENCOUNTER_VILLAGER.get(), level);
            return v.getFaction() == EntityEncounterVillager.FACTION_FRIENDLY;
        } catch (Throwable t) {
            info("instantiate_error", t.toString());
            return false;
        }
    }

    private static net.minecraft.resources.ResourceLocation ResLocation(String ns, String path) {
        return new net.minecraft.resources.ResourceLocation(ns, path);
    }
}
