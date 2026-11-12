package com.hbm_addons.test;

import com.hbm.config.RadiationConfig;
import com.hbm.config.WorldConfig;
import com.hbm.datagen.worldgen.ModBiomes;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.EntityEffectHandler;
import com.hbm.util.ContaminationUtil;
import com.hbm.world.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.ChunkPos;

/**
 * Restores HBM 1.7.10 crater biome radiation for all living entities.
 * Uses the established, force-loaded test chunk (62,62), as crater_fallout
 * does. No explosions, entity spawns into the world or block placement.
 *
 * Client particle/Geiger visuals and long-term health damage still need
 * a local in-game acceptance test.
 */
public final class CraterBiomeMobRadiationTestRunner extends TestRunner {
    private static final float EPS = 0.001F;

    public CraterBiomeMobRadiationTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (tickCount++ != 0) return true;

        check("contamination_enabled", RadiationConfig.ENABLE_CONTAMINATION);
        check("outer_rate_config", near(EntityEffectHandler.craterBiomeRadiationPerSecond(
                ModBiomes.CRATER_OUTER), (float) WorldConfig.CRATER_BIOME_OUTER_RAD));
        check("main_rate_config", near(EntityEffectHandler.craterBiomeRadiationPerSecond(
                ModBiomes.CRATER), (float) WorldConfig.CRATER_BIOME_RAD));
        check("inner_rate_config", near(EntityEffectHandler.craterBiomeRadiationPerSecond(
                ModBiomes.CRATER_INNER), (float) WorldConfig.CRATER_BIOME_INNER_RAD));
        check("normal_biome_zero", EntityEffectHandler.craterBiomeRadiationPerSecond(
                net.minecraft.world.level.biome.Biomes.PLAINS) == 0F);
        check("null_biome_zero", EntityEffectHandler.craterBiomeRadiationPerSecond(null) == 0F);

        ChunkPos chunk = new ChunkPos(62, 62); // forced by TestBootstrap
        int x = chunk.getMiddleBlockX();
        int z = chunk.getMiddleBlockZ();
        BlockPos position = new BlockPos(x, 80, z);
        WorldUtil.applyCraterBiomes(level, chunk, x, z, 180);
        check("world_biome_inner", level.getBiome(position).is(ModBiomes.CRATER_INNER));

        Cow cow = EntityType.COW.create(level);
        Zombie zombie = EntityType.ZOMBIE.create(level);
        check("cow_constructed", cow != null);
        check("zombie_constructed", zombie != null);

        if (cow != null && zombie != null) {
            cow.setPos(x + 0.5D, 80D, z + 0.5D);
            zombie.setPos(x + 0.5D, 80D, z + 0.5D);

            check("cow_capability_attached", HbmLivingProps.getData(cow) != null);
            check("zombie_capability_attached", HbmLivingProps.getData(zombie) != null);
            check("cow_not_immune", !ContaminationUtil.isRadImmune(cow));
            check("zombie_immune", ContaminationUtil.isRadImmune(zombie));

            float before = HbmLivingProps.getRadiation(cow);
            EntityEffectHandler.applyCraterBiomeRadiation(cow);
            float first = HbmLivingProps.getRadiation(cow);
            float dose = (float) WorldConfig.CRATER_BIOME_INNER_RAD / 20F;
            check("cow_receives_biome_rad", first > before);
            check("cow_rad_equals_one_tick", near(first - before, dose));
            check("cow_environment_counter", HbmLivingProps.getRadEnv(cow) > 0F);

            EntityEffectHandler.applyCraterBiomeRadiation(cow);
            check("cow_accumulates_each_tick", near(HbmLivingProps.getRadiation(cow) - before, dose * 2F));

            float immuneBefore = HbmLivingProps.getRadiation(zombie);
            EntityEffectHandler.applyCraterBiomeRadiation(zombie);
            check("immune_mob_unchanged", near(HbmLivingProps.getRadiation(zombie), immuneBefore));

            metric("cow_rad_two_ticks", HbmLivingProps.getRadiation(cow));
            metric("zombie_rad", HbmLivingProps.getRadiation(zombie));
        } else {
            for (int i = 0; i < 9; i++) {
                check("mob_integration_unavailable_" + i, false);
            }
        }

        metric("checks", 18);
        finish(checks.stream().allMatch(result -> result.pass));
        return true;
    }

    private static boolean near(float actual, float expected) {
        return Math.abs(actual - expected) <= EPS;
    }
}
