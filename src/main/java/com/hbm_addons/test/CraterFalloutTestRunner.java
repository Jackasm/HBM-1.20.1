package com.hbm_addons.test;

import com.hbm.datagen.worldgen.ModBiomes;
import com.hbm.explosion.CraterFalloutRules;
import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.world.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/**
 * Non-destructive crater test: mutate only the isolated, force-loaded
 * test chunk (62,62). No explosion, no terrain destruction, no CI.
 */
public class CraterFalloutTestRunner extends TestRunner {

    public CraterFalloutTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        long packed = ChunkPos.asLong(-3, -7);
        check("negative_chunk_x", CraterFalloutRules.chunkX(packed) == -3);
        check("negative_chunk_z", CraterFalloutRules.chunkZ(packed) == -7);
        check("positive_chunk_x", CraterFalloutRules.chunkX(ChunkPos.asLong(3, 7)) == 3);

        check("inner_zone", ModBiomes.CRATER_INNER.equals(CraterFalloutRules.targetBiome(10D, 180, null)));
        check("main_zone", ModBiomes.CRATER.equals(CraterFalloutRules.targetBiome(30D, 180, null)));
        check("outer_zone", ModBiomes.CRATER_OUTER.equals(CraterFalloutRules.targetBiome(85D, 180, null)));
        check("outside_untouched", CraterFalloutRules.targetBiome(101D, 180, null) == null);
        check("small_yield_untouched", CraterFalloutRules.targetBiome(50D, 10, null) == null);
        check("inner_not_overwritten", CraterFalloutRules.targetBiome(50D, 180, ModBiomes.CRATER_INNER) == null);
        check("surface_rads", CraterFalloutRules.initialSurfaceRadiation(10D, 180) >
                CraterFalloutRules.initialSurfaceRadiation(80D, 180));

        ChunkPos target = new ChunkPos(62, 62); // forced by TestBootstrap
        int x = target.getMiddleBlockX();
        int z = target.getMiddleBlockZ();
        WorldUtil.applyCraterBiomes(level, target, x, z, 180);
        check("biome_below_zero", level.getBiome(new BlockPos(x, -32, z)).is(ModBiomes.CRATER_INNER));
        check("biome_player_height", level.getBiome(new BlockPos(x, 80, z)).is(ModBiomes.CRATER_INNER));
        check("biome_high_altitude", level.getBiome(new BlockPos(x, 220, z)).is(ModBiomes.CRATER_INNER));

        BlockPos rads = new BlockPos(x + 2, 80, z + 2);
        float before = RadiationEvents.getRadiation(level, rads);
        RadiationEvents.incrementRadiation(level, rads,
                CraterFalloutRules.initialSurfaceRadiation(0D, 180));
        float after = RadiationEvents.getRadiation(level, rads);
        check("radiation_field_receives_deposit", after > before);

        metric("checks", 14);
        metric("rad_before", before);
        metric("rad_after", after);
        finish(checks.stream().allMatch(result -> result.pass));
        return true;
    }
}
