package com.hbm.explosion;

import com.hbm.datagen.worldgen.ModBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;

/**
 * Pure, bounded post-blast fallout rules. Keep the original HBM crater
 * thresholds, but respect the full 1.20.1 signed chunk coordinates.
 */
public final class CraterFalloutRules {

    private CraterFalloutRules() {}

    public static int chunkX(long packed) {
        return ChunkPos.getX(packed);
    }

    public static int chunkZ(long packed) {
        return ChunkPos.getZ(packed);
    }

    public static ResourceKey<Biome> targetBiome(double percent, int scale, ResourceKey<Biome> existing) {
        if (!Double.isFinite(percent) || percent < 0D || percent > 100D || scale < 25) return null;

        if (scale >= 150 && percent < 15D) return ModBiomes.CRATER_INNER;
        if (scale >= 100 && percent < 55D && !ModBiomes.CRATER_INNER.equals(existing)) {
            return ModBiomes.CRATER;
        }
        if (!ModBiomes.CRATER_INNER.equals(existing) && !ModBiomes.CRATER.equals(existing)) {
            return ModBiomes.CRATER_OUTER;
        }
        return null;
    }

    /**
     * Initial contamination of the soil, sampled once per PRISM 4x4 horizontal
     * cell by the fallout worker. Persistent biome RAD/s is independent of this
     * finite deposit and continues after the initial field has decayed.
     */
    public static float initialSurfaceRadiation(double percent, int scale) {
        if (!Double.isFinite(percent) || percent < 0D || percent > 100D || scale < 25) return 0F;
        if (scale >= 150 && percent < 15D) return 40F;
        if (scale >= 100 && percent < 55D) return 12F;
        return 2F;
    }
}
