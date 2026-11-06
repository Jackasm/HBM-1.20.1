package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * 1.7.10: smoldering ore ставился только если сверху воздух, а под ним незерак.
 * На каждый чанк — 1/10, затем вертикальный отрезок y..y+5.
 */
public class SmolderingOreFeature extends Feature<NoneFeatureConfiguration> {

    public SmolderingOreFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        int x = origin.getX();
        int z = origin.getZ();
        int y = 16 + rand.nextInt(96);
        boolean placed = false;

        for (int cy = y - 5; cy <= y; cy++) {
            BlockPos pos = new BlockPos(x, cy, z);
            if (!level.getBlockState(pos).is(Blocks.NETHERRACK)) continue;
            if (!level.getBlockState(pos.above()).isAir()) continue;
            if (!level.ensureCanWrite(pos)) continue;

            level.setBlock(pos, ModBlocks.ORE_NETHER_SMOLDERING.get().defaultBlockState(), 2);
            placed = true;
        }

        return placed;
    }
}
