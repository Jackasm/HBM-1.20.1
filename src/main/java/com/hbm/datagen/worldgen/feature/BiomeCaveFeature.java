package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.worldgen.feature.config.BiomeCaveConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class BiomeCaveFeature extends Feature<BiomeCaveConfiguration> {

    public BiomeCaveFeature(com.mojang.serialization.Codec<BiomeCaveConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BiomeCaveConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        BiomeCaveConfiguration cfg = context.config();

        Holder<Biome> biomeHolder = level.getBiome(origin);
        BlockState biomeStone = getBiomeStone(biomeHolder);
        if (biomeStone == null) return false;   // биом не подходит

        int yLevel = cfg.yLevel();
        int threshold = cfg.threshold();
        int rangeMult = cfg.rangeMult();
        int maxRange = cfg.maxRange();

        int x = origin.getX();
        int z = origin.getZ();

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                double n = getPseudoNoise(x + dx, z + dz);
                if (n <= threshold) continue;

                int range = (int) ((n - threshold) * rangeMult);
                if (range > maxRange) range = (maxRange * 2) - range;
                if (range < 0) continue;

                for (int y = yLevel - range; y <= yLevel + range; y++) {
                    BlockPos pos = new BlockPos(x + dx, y, z + dz);
                    handleBiome(level, pos, biomeStone);
                }
            }
        }
        return true;
    }

    /** Возвращает нужный блок stone_biome или null, если биом не подходит. */
    private BlockState getBiomeStone(Holder<Biome> biome) {
        float temp = biome.value().getBaseTemperature();
        boolean dry = biome.value().getModifiedClimateSettings().downfall() < 0.25f;

        // Пустыня — жарко и сухо
        if (temp >= 1.0f && dry) {
            return ModBlocks.STONE_BIOME_DESERT.get().defaultBlockState();
        }
        // Лес — тепло и влажно
        if (temp >= 0.5f && !dry) {
            return ModBlocks.STONE_BIOME_WOODLAND.get().defaultBlockState();
        }
        return null;
    }

    private void handleBiome(WorldGenLevel level, BlockPos pos, BlockState biomeStone) {
        BlockState current = level.getBlockState(pos);

        if (!current.is(BlockTags.STONE_ORE_REPLACEABLES)) return;

        // Проверка: рядом воздух (в радиусе 2)
        boolean nearAir = false;
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).isAir()) { nearAir = true; break; }
            if (level.getBlockState(pos.relative(dir, 2)).isAir()) { nearAir = true; break; }
        }

        if (nearAir) {
            level.setBlock(pos, biomeStone, 2);
        }
    }

    /** Простой псевдо-шум. */
    private double getPseudoNoise(int x, int z) {
        long hash = (long) x * 341873128712L + (long) z * 132897987541L;
        hash ^= (hash >>> 33);
        hash *= 0xff51afd7ed558ccdL;
        hash ^= (hash >>> 33);
        return ((hash & 0xFFFF) / (double) 0xFFFF) * 6.0;
    }
}