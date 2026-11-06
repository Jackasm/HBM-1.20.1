package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.worldgen.feature.config.GeyserConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class GeyserLargeFeature extends Feature<GeyserConfiguration> {

    public GeyserLargeFeature(com.mojang.serialization.Codec<GeyserConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GeyserConfiguration> context) {
        GeyserConfiguration cfg = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        if (!canPlaceGeyser(level, origin, cfg.geyserBlock())) {
            return false;
        }

        int x = origin.getX() - 4;
        int y = origin.getY() - 10;
        int z = origin.getZ() - 4;

        // === Слой y+5 ===
        set(level, x + 3, y + 5, z + 2, Blocks.STONE);
        set(level, x + 4, y + 5, z + 2, Blocks.STONE);
        set(level, x + 5, y + 5, z + 2, Blocks.STONE);
        set(level, x + 2, y + 5, z + 3, Blocks.STONE);
        set(level, x + 3, y + 5, z + 3, Blocks.STONE);
        set(level, x + 4, y + 5, z + 3, Blocks.STONE);
        set(level, x + 5, y + 5, z + 3, Blocks.STONE);
        set(level, x + 6, y + 5, z + 3, Blocks.STONE);
        set(level, x + 2, y + 5, z + 4, Blocks.STONE);
        set(level, x + 3, y + 5, z + 4, Blocks.STONE);
        set(level, x + 4, y + 5, z + 4, Blocks.STONE);
        set(level, x + 5, y + 5, z + 4, Blocks.STONE);
        set(level, x + 6, y + 5, z + 4, Blocks.STONE);
        set(level, x + 2, y + 5, z + 5, Blocks.STONE);
        set(level, x + 3, y + 5, z + 5, Blocks.STONE);
        set(level, x + 4, y + 5, z + 5, Blocks.STONE);
        set(level, x + 5, y + 5, z + 5, Blocks.STONE);
        set(level, x + 6, y + 5, z + 5, Blocks.STONE);
        set(level, x + 3, y + 5, z + 6, Blocks.STONE);
        set(level, x + 4, y + 5, z + 6, Blocks.STONE);
        set(level, x + 5, y + 5, z + 6, Blocks.STONE);

        // === Слой y+6 ===
        set(level, x + 3, y + 6, z + 1, Blocks.STONE);
        set(level, x + 4, y + 6, z + 1, Blocks.STONE);
        set(level, x + 5, y + 6, z + 1, Blocks.STONE);
        set(level, x + 2, y + 6, z + 2, Blocks.STONE);
        set(level, x + 3, y + 6, z + 2, Blocks.STONE);
        set(level, x + 4, y + 6, z + 2, Blocks.STONE);
        set(level, x + 5, y + 6, z + 2, Blocks.STONE);
        set(level, x + 6, y + 6, z + 2, Blocks.STONE);
        set(level, x + 1, y + 6, z + 3, Blocks.STONE);
        set(level, x + 2, y + 6, z + 3, Blocks.STONE);
        set(level, x + 3, y + 6, z + 3, Blocks.STONE);
        set(level, x + 4, y + 6, z + 3, Blocks.STONE);
        set(level, x + 5, y + 6, z + 3, Blocks.STONE);
        set(level, x + 6, y + 6, z + 3, Blocks.STONE);
        set(level, x + 7, y + 6, z + 3, Blocks.STONE);
        set(level, x + 1, y + 6, z + 4, Blocks.STONE);
        set(level, x + 2, y + 6, z + 4, Blocks.STONE);
        set(level, x + 3, y + 6, z + 4, Blocks.STONE);
        set(level, x + 4, y + 6, z + 4, Blocks.STONE);
        set(level, x + 5, y + 6, z + 4, Blocks.STONE);
        set(level, x + 6, y + 6, z + 4, Blocks.STONE);
        set(level, x + 7, y + 6, z + 4, Blocks.STONE);
        set(level, x + 1, y + 6, z + 5, Blocks.STONE);
        set(level, x + 2, y + 6, z + 5, Blocks.STONE);
        set(level, x + 3, y + 6, z + 5, Blocks.STONE);
        set(level, x + 4, y + 6, z + 5, Blocks.STONE);
        set(level, x + 5, y + 6, z + 5, Blocks.STONE);
        set(level, x + 6, y + 6, z + 5, Blocks.STONE);
        set(level, x + 7, y + 6, z + 5, Blocks.STONE);
        set(level, x + 2, y + 6, z + 6, Blocks.STONE);
        set(level, x + 3, y + 6, z + 6, Blocks.STONE);
        set(level, x + 4, y + 6, z + 6, Blocks.STONE);
        set(level, x + 5, y + 6, z + 6, Blocks.STONE);
        set(level, x + 6, y + 6, z + 6, Blocks.STONE);
        set(level, x + 3, y + 6, z + 7, Blocks.STONE);
        set(level, x + 4, y + 6, z + 7, Blocks.STONE);
        set(level, x + 5, y + 6, z + 7, Blocks.STONE);

        // === Слой y+7 — вода в центре ===
        set(level, x + 3, y + 7, z + 0, Blocks.STONE);
        set(level, x + 4, y + 7, z + 0, Blocks.STONE);
        set(level, x + 5, y + 7, z + 0, Blocks.STONE);
        set(level, x + 2, y + 7, z + 1, Blocks.STONE);
        set(level, x + 3, y + 7, z + 1, Blocks.STONE);
        set(level, x + 4, y + 7, z + 1, Blocks.STONE);
        set(level, x + 5, y + 7, z + 1, Blocks.STONE);
        set(level, x + 6, y + 7, z + 1, Blocks.STONE);
        set(level, x + 1, y + 7, z + 2, Blocks.STONE);
        set(level, x + 2, y + 7, z + 2, Blocks.STONE);
        set(level, x + 3, y + 7, z + 2, Blocks.WATER);
        set(level, x + 4, y + 7, z + 2, Blocks.WATER);
        set(level, x + 5, y + 7, z + 2, Blocks.WATER);
        set(level, x + 6, y + 7, z + 2, Blocks.STONE);
        set(level, x + 7, y + 7, z + 2, Blocks.STONE);
        set(level, x + 0, y + 7, z + 3, Blocks.STONE);
        set(level, x + 1, y + 7, z + 3, Blocks.STONE);
        set(level, x + 2, y + 7, z + 3, Blocks.WATER);
        set(level, x + 3, y + 7, z + 3, Blocks.WATER);
        set(level, x + 4, y + 7, z + 3, Blocks.WATER);
        set(level, x + 5, y + 7, z + 3, Blocks.WATER);
        set(level, x + 6, y + 7, z + 3, Blocks.WATER);
        set(level, x + 7, y + 7, z + 3, Blocks.STONE);
        set(level, x + 8, y + 7, z + 3, Blocks.STONE);
        set(level, x + 0, y + 7, z + 4, Blocks.STONE);
        set(level, x + 1, y + 7, z + 4, Blocks.STONE);
        set(level, x + 2, y + 7, z + 4, Blocks.WATER);
        set(level, x + 3, y + 7, z + 4, Blocks.WATER);
        set(level, x + 4, y + 7, z + 4, Blocks.WATER);
        set(level, x + 5, y + 7, z + 4, Blocks.WATER);
        set(level, x + 6, y + 7, z + 4, Blocks.WATER);
        set(level, x + 7, y + 7, z + 4, Blocks.STONE);
        set(level, x + 8, y + 7, z + 4, Blocks.STONE);
        set(level, x + 0, y + 7, z + 5, Blocks.STONE);
        set(level, x + 1, y + 7, z + 5, Blocks.STONE);
        set(level, x + 2, y + 7, z + 5, Blocks.WATER);
        set(level, x + 3, y + 7, z + 5, Blocks.WATER);
        set(level, x + 4, y + 7, z + 5, Blocks.WATER);
        set(level, x + 5, y + 7, z + 5, Blocks.WATER);
        set(level, x + 6, y + 7, z + 5, Blocks.WATER);
        set(level, x + 7, y + 7, z + 5, Blocks.STONE);
        set(level, x + 8, y + 7, z + 5, Blocks.STONE);
        set(level, x + 1, y + 7, z + 6, Blocks.STONE);
        set(level, x + 2, y + 7, z + 6, Blocks.STONE);
        set(level, x + 3, y + 7, z + 6, Blocks.WATER);
        set(level, x + 4, y + 7, z + 6, Blocks.WATER);
        set(level, x + 5, y + 7, z + 6, Blocks.WATER);
        set(level, x + 6, y + 7, z + 6, Blocks.STONE);
        set(level, x + 7, y + 7, z + 6, Blocks.STONE);
        set(level, x + 2, y + 7, z + 7, Blocks.STONE);
        set(level, x + 3, y + 7, z + 7, Blocks.STONE);
        set(level, x + 4, y + 7, z + 7, Blocks.STONE);
        set(level, x + 5, y + 7, z + 7, Blocks.STONE);
        set(level, x + 6, y + 7, z + 7, Blocks.STONE);
        set(level, x + 3, y + 7, z + 8, Blocks.STONE);
        set(level, x + 4, y + 7, z + 8, Blocks.STONE);
        set(level, x + 5, y + 7, z + 8, Blocks.STONE);

        // === Слой y+8 — полости ===
        set(level, x + 2, y + 8, z + 0, Blocks.STONE);
        set(level, x + 3, y + 8, z + 0, Blocks.STONE);
        set(level, x + 4, y + 8, z + 0, Blocks.STONE);
        set(level, x + 5, y + 8, z + 0, Blocks.STONE);
        set(level, x + 6, y + 8, z + 0, Blocks.STONE);
        set(level, x + 1, y + 8, z + 1, Blocks.STONE);
        set(level, x + 2, y + 8, z + 1, Blocks.STONE);
        set(level, x + 3, y + 8, z + 1, Blocks.STONE);
        set(level, x + 4, y + 8, z + 1, Blocks.STONE);
        set(level, x + 5, y + 8, z + 1, Blocks.STONE);
        set(level, x + 6, y + 8, z + 1, Blocks.STONE);
        set(level, x + 7, y + 8, z + 1, Blocks.STONE);
        set(level, x + 0, y + 8, z + 2, Blocks.STONE);
        set(level, x + 1, y + 8, z + 2, Blocks.STONE);
        set(level, x + 2, y + 8, z + 2, Blocks.STONE);
        set(level, x + 3, y + 8, z + 2, Blocks.AIR);
        set(level, x + 4, y + 8, z + 2, Blocks.AIR);
        set(level, x + 5, y + 8, z + 2, Blocks.AIR);
        set(level, x + 6, y + 8, z + 2, Blocks.STONE);
        set(level, x + 7, y + 8, z + 2, Blocks.STONE);
        set(level, x + 8, y + 8, z + 2, Blocks.STONE);
        set(level, x + 0, y + 8, z + 3, Blocks.STONE);
        set(level, x + 1, y + 8, z + 3, Blocks.STONE);
        set(level, x + 2, y + 8, z + 3, Blocks.AIR);
        set(level, x + 3, y + 8, z + 3, Blocks.AIR);
        set(level, x + 4, y + 8, z + 3, Blocks.AIR);
        set(level, x + 5, y + 8, z + 3, Blocks.AIR);
        set(level, x + 6, y + 8, z + 3, Blocks.AIR);
        set(level, x + 7, y + 8, z + 3, Blocks.STONE);
        set(level, x + 8, y + 8, z + 3, Blocks.STONE);
        set(level, x + 0, y + 8, z + 4, Blocks.STONE);
        set(level, x + 1, y + 8, z + 4, Blocks.STONE);
        set(level, x + 2, y + 8, z + 4, Blocks.AIR);
        set(level, x + 3, y + 8, z + 4, Blocks.AIR);
        set(level, x + 4, y + 8, z + 4, Blocks.AIR);
        set(level, x + 5, y + 8, z + 4, Blocks.AIR);
        set(level, x + 6, y + 8, z + 4, Blocks.AIR);
        set(level, x + 7, y + 8, z + 4, Blocks.STONE);
        set(level, x + 8, y + 8, z + 4, Blocks.STONE);
        set(level, x + 0, y + 8, z + 5, Blocks.STONE);
        set(level, x + 1, y + 8, z + 5, Blocks.STONE);
        set(level, x + 2, y + 8, z + 5, Blocks.AIR);
        set(level, x + 3, y + 8, z + 5, Blocks.AIR);
        set(level, x + 4, y + 8, z + 5, Blocks.AIR);
        set(level, x + 5, y + 8, z + 5, Blocks.AIR);
        set(level, x + 6, y + 8, z + 5, Blocks.AIR);
        set(level, x + 7, y + 8, z + 5, Blocks.STONE);
        set(level, x + 8, y + 8, z + 5, Blocks.STONE);
        set(level, x + 0, y + 8, z + 6, Blocks.STONE);
        set(level, x + 1, y + 8, z + 6, Blocks.STONE);
        set(level, x + 2, y + 8, z + 6, Blocks.STONE);
        set(level, x + 3, y + 8, z + 6, Blocks.AIR);
        set(level, x + 4, y + 8, z + 6, Blocks.AIR);
        set(level, x + 5, y + 8, z + 6, Blocks.AIR);
        set(level, x + 6, y + 8, z + 6, Blocks.STONE);
        set(level, x + 7, y + 8, z + 6, Blocks.STONE);
        set(level, x + 8, y + 8, z + 6, Blocks.STONE);
        set(level, x + 1, y + 8, z + 7, Blocks.STONE);
        set(level, x + 2, y + 8, z + 7, Blocks.STONE);
        set(level, x + 3, y + 8, z + 7, Blocks.STONE);
        set(level, x + 4, y + 8, z + 7, Blocks.STONE);
        set(level, x + 5, y + 8, z + 7, Blocks.STONE);
        set(level, x + 6, y + 8, z + 7, Blocks.STONE);
        set(level, x + 7, y + 8, z + 7, Blocks.STONE);
        set(level, x + 2, y + 8, z + 8, Blocks.STONE);
        set(level, x + 3, y + 8, z + 8, Blocks.STONE);
        set(level, x + 4, y + 8, z + 8, Blocks.STONE);
        set(level, x + 5, y + 8, z + 8, Blocks.STONE);
        set(level, x + 6, y + 8, z + 8, Blocks.STONE);

        // === Слой y+9 — узкая шахта ===
        set(level, x + 2, y + 9, z + 0, Blocks.STONE);
        set(level, x + 3, y + 9, z + 0, Blocks.STONE);
        set(level, x + 4, y + 9, z + 0, Blocks.STONE);
        set(level, x + 5, y + 9, z + 0, Blocks.STONE);
        set(level, x + 6, y + 9, z + 0, Blocks.STONE);
        set(level, x + 1, y + 9, z + 1, Blocks.STONE);
        set(level, x + 2, y + 9, z + 1, Blocks.STONE);
        set(level, x + 3, y + 9, z + 1, Blocks.STONE);
        set(level, x + 4, y + 9, z + 1, Blocks.STONE);
        set(level, x + 5, y + 9, z + 1, Blocks.STONE);
        set(level, x + 6, y + 9, z + 1, Blocks.STONE);
        set(level, x + 7, y + 9, z + 1, Blocks.STONE);
        set(level, x + 0, y + 9, z + 2, Blocks.STONE);
        set(level, x + 1, y + 9, z + 2, Blocks.STONE);
        set(level, x + 2, y + 9, z + 2, Blocks.STONE);
        set(level, x + 3, y + 9, z + 2, Blocks.AIR);
        set(level, x + 4, y + 9, z + 2, Blocks.AIR);
        set(level, x + 5, y + 9, z + 2, Blocks.AIR);
        set(level, x + 6, y + 9, z + 2, Blocks.STONE);
        set(level, x + 7, y + 9, z + 2, Blocks.STONE);
        set(level, x + 8, y + 9, z + 2, Blocks.STONE);
        set(level, x + 0, y + 9, z + 3, Blocks.STONE);
        set(level, x + 1, y + 9, z + 3, Blocks.STONE);
        set(level, x + 2, y + 9, z + 3, Blocks.AIR);
        set(level, x + 3, y + 9, z + 3, Blocks.AIR);
        set(level, x + 4, y + 9, z + 3, Blocks.AIR);
        set(level, x + 5, y + 9, z + 3, Blocks.AIR);
        set(level, x + 6, y + 9, z + 3, Blocks.AIR);
        set(level, x + 7, y + 9, z + 3, Blocks.STONE);
        set(level, x + 8, y + 9, z + 3, Blocks.STONE);
        set(level, x + 0, y + 9, z + 4, Blocks.STONE);
        set(level, x + 1, y + 9, z + 4, Blocks.STONE);
        set(level, x + 2, y + 9, z + 4, Blocks.AIR);
        set(level, x + 3, y + 9, z + 4, Blocks.AIR);
        set(level, x + 4, y + 9, z + 4, Blocks.AIR);
        set(level, x + 5, y + 9, z + 4, Blocks.AIR);
        set(level, x + 6, y + 9, z + 4, Blocks.AIR);
        set(level, x + 7, y + 9, z + 4, Blocks.STONE);
        set(level, x + 8, y + 9, z + 4, Blocks.STONE);
        set(level, x + 0, y + 9, z + 5, Blocks.STONE);
        set(level, x + 1, y + 9, z + 5, Blocks.STONE);
        set(level, x + 2, y + 9, z + 5, Blocks.AIR);
        set(level, x + 3, y + 9, z + 5, Blocks.AIR);
        set(level, x + 4, y + 9, z + 5, Blocks.AIR);
        set(level, x + 5, y + 9, z + 5, Blocks.AIR);
        set(level, x + 6, y + 9, z + 5, Blocks.AIR);
        set(level, x + 7, y + 9, z + 5, Blocks.STONE);
        set(level, x + 8, y + 9, z + 5, Blocks.STONE);
        set(level, x + 0, y + 9, z + 6, Blocks.STONE);
        set(level, x + 1, y + 9, z + 6, Blocks.STONE);
        set(level, x + 2, y + 9, z + 6, Blocks.STONE);
        set(level, x + 3, y + 9, z + 6, Blocks.AIR);
        set(level, x + 4, y + 9, z + 6, Blocks.AIR);
        set(level, x + 5, y + 9, z + 6, Blocks.AIR);
        set(level, x + 6, y + 9, z + 6, Blocks.STONE);
        set(level, x + 7, y + 9, z + 6, Blocks.STONE);
        set(level, x + 8, y + 9, z + 6, Blocks.STONE);
        set(level, x + 1, y + 9, z + 7, Blocks.STONE);
        set(level, x + 2, y + 9, z + 7, Blocks.STONE);
        set(level, x + 3, y + 9, z + 7, Blocks.STONE);
        set(level, x + 4, y + 9, z + 7, Blocks.STONE);
        set(level, x + 5, y + 9, z + 7, Blocks.STONE);
        set(level, x + 6, y + 9, z + 7, Blocks.STONE);
        set(level, x + 7, y + 9, z + 7, Blocks.STONE);
        set(level, x + 2, y + 9, z + 8, Blocks.STONE);
        set(level, x + 3, y + 9, z + 8, Blocks.STONE);
        set(level, x + 4, y + 9, z + 8, Blocks.STONE);
        set(level, x + 5, y + 9, z + 8, Blocks.STONE);
        set(level, x + 6, y + 9, z + 8, Blocks.STONE);

        // === Слой y+10 — смешанный гравий/камень ===
        set(level, x + 2, y + 10, z + 0, Blocks.STONE);
        set(level, x + 3, y + 10, z + 0, Blocks.GRAVEL);
        set(level, x + 4, y + 10, z + 0, Blocks.STONE);
        set(level, x + 5, y + 10, z + 0, Blocks.GRAVEL);
        set(level, x + 6, y + 10, z + 0, Blocks.STONE);
        set(level, x + 1, y + 10, z + 1, Blocks.GRAVEL);
        set(level, x + 2, y + 10, z + 1, Blocks.STONE);
        set(level, x + 3, y + 10, z + 1, Blocks.STONE);
        set(level, x + 4, y + 10, z + 1, Blocks.STONE);
        set(level, x + 5, y + 10, z + 1, Blocks.STONE);
        set(level, x + 6, y + 10, z + 1, Blocks.STONE);
        set(level, x + 7, y + 10, z + 1, Blocks.STONE);
        set(level, x + 0, y + 10, z + 2, Blocks.STONE);
        set(level, x + 1, y + 10, z + 2, Blocks.STONE);
        set(level, x + 2, y + 10, z + 2, Blocks.STONE);
        set(level, x + 3, y + 10, z + 2, Blocks.STONE);
        set(level, x + 4, y + 10, z + 2, Blocks.AIR);
        set(level, x + 5, y + 10, z + 2, Blocks.STONE);
        set(level, x + 6, y + 10, z + 2, Blocks.STONE);
        set(level, x + 7, y + 10, z + 2, Blocks.STONE);
        set(level, x + 8, y + 10, z + 2, Blocks.GRAVEL);
        set(level, x + 0, y + 10, z + 3, Blocks.GRAVEL);
        set(level, x + 1, y + 10, z + 3, Blocks.STONE);
        set(level, x + 2, y + 10, z + 3, Blocks.STONE);
        set(level, x + 3, y + 10, z + 3, Blocks.AIR);
        set(level, x + 4, y + 10, z + 3, Blocks.AIR);
        set(level, x + 5, y + 10, z + 3, Blocks.AIR);
        set(level, x + 6, y + 10, z + 3, Blocks.STONE);
        set(level, x + 7, y + 10, z + 3, Blocks.STONE);
        set(level, x + 8, y + 10, z + 3, Blocks.STONE);
        set(level, x + 0, y + 10, z + 4, Blocks.GRAVEL);
        set(level, x + 1, y + 10, z + 4, Blocks.STONE);
        set(level, x + 2, y + 10, z + 4, Blocks.AIR);
        set(level, x + 3, y + 10, z + 4, Blocks.AIR);
        set(level, x + 4, y + 10, z + 4, Blocks.AIR);
        set(level, x + 5, y + 10, z + 4, Blocks.AIR);
        set(level, x + 6, y + 10, z + 4, Blocks.AIR);
        set(level, x + 7, y + 10, z + 4, Blocks.STONE);
        set(level, x + 8, y + 10, z + 4, Blocks.STONE);
        set(level, x + 0, y + 10, z + 5, Blocks.STONE);
        set(level, x + 1, y + 10, z + 5, Blocks.STONE);
        set(level, x + 2, y + 10, z + 5, Blocks.STONE);
        set(level, x + 3, y + 10, z + 5, Blocks.AIR);
        set(level, x + 4, y + 10, z + 5, Blocks.AIR);
        set(level, x + 5, y + 10, z + 5, Blocks.AIR);
        set(level, x + 6, y + 10, z + 5, Blocks.STONE);
        set(level, x + 7, y + 10, z + 5, Blocks.STONE);
        set(level, x + 8, y + 10, z + 5, Blocks.STONE);
        set(level, x + 0, y + 10, z + 6, Blocks.STONE);
        set(level, x + 1, y + 10, z + 6, Blocks.STONE);
        set(level, x + 2, y + 10, z + 6, Blocks.STONE);
        set(level, x + 3, y + 10, z + 6, Blocks.STONE);
        set(level, x + 4, y + 10, z + 6, Blocks.AIR);
        set(level, x + 5, y + 10, z + 6, Blocks.STONE);
        set(level, x + 6, y + 10, z + 6, Blocks.STONE);
        set(level, x + 7, y + 10, z + 6, Blocks.STONE);
        set(level, x + 8, y + 10, z + 6, Blocks.STONE);
        set(level, x + 1, y + 10, z + 7, Blocks.STONE);
        set(level, x + 2, y + 10, z + 7, Blocks.STONE);
        set(level, x + 3, y + 10, z + 7, Blocks.STONE);
        set(level, x + 4, y + 10, z + 7, Blocks.STONE);
        set(level, x + 5, y + 10, z + 7, Blocks.STONE);
        set(level, x + 6, y + 10, z + 7, Blocks.STONE);
        set(level, x + 7, y + 10, z + 7, Blocks.STONE);
        set(level, x + 2, y + 10, z + 8, Blocks.STONE);
        set(level, x + 3, y + 10, z + 8, Blocks.GRAVEL);
        set(level, x + 4, y + 10, z + 8, Blocks.STONE);
        set(level, x + 5, y + 10, z + 8, Blocks.STONE);
        set(level, x + 6, y + 10, z + 8, Blocks.STONE);

        // === Слой y+11 ===
        set(level, x + 2, y + 11, z + 1, Blocks.STONE);
        set(level, x + 3, y + 11, z + 1, Blocks.STONE);
        set(level, x + 4, y + 11, z + 1, Blocks.STONE);
        set(level, x + 5, y + 11, z + 1, Blocks.STONE);
        set(level, x + 6, y + 11, z + 1, Blocks.GRAVEL);
        set(level, x + 1, y + 11, z + 2, Blocks.STONE);
        set(level, x + 2, y + 11, z + 2, Blocks.STONE);
        set(level, x + 3, y + 11, z + 2, Blocks.STONE);
        set(level, x + 4, y + 11, z + 2, Blocks.AIR);
        set(level, x + 5, y + 11, z + 2, Blocks.STONE);
        set(level, x + 6, y + 11, z + 2, Blocks.STONE);
        set(level, x + 7, y + 11, z + 2, Blocks.STONE);
        set(level, x + 1, y + 11, z + 3, Blocks.STONE);
        set(level, x + 2, y + 11, z + 3, Blocks.STONE);
        set(level, x + 3, y + 11, z + 3, Blocks.AIR);
        set(level, x + 4, y + 11, z + 3, Blocks.AIR);
        set(level, x + 5, y + 11, z + 3, Blocks.AIR);
        set(level, x + 6, y + 11, z + 3, Blocks.STONE);
        set(level, x + 7, y + 11, z + 3, Blocks.GRAVEL);
        set(level, x + 1, y + 11, z + 4, Blocks.STONE);
        set(level, x + 2, y + 11, z + 4, Blocks.AIR);
        set(level, x + 3, y + 11, z + 4, Blocks.AIR);
        set(level, x + 4, y + 11, z + 4, Blocks.AIR);
        set(level, x + 5, y + 11, z + 4, Blocks.AIR);
        set(level, x + 6, y + 11, z + 4, Blocks.AIR);
        set(level, x + 7, y + 11, z + 4, Blocks.STONE);
        set(level, x + 1, y + 11, z + 5, Blocks.STONE);
        set(level, x + 2, y + 11, z + 5, Blocks.STONE);
        set(level, x + 3, y + 11, z + 5, Blocks.AIR);
        set(level, x + 4, y + 11, z + 5, Blocks.AIR);
        set(level, x + 5, y + 11, z + 5, Blocks.AIR);
        set(level, x + 6, y + 11, z + 5, Blocks.STONE);
        set(level, x + 7, y + 11, z + 5, Blocks.STONE);
        set(level, x + 1, y + 11, z + 6, Blocks.STONE);
        set(level, x + 2, y + 11, z + 6, Blocks.STONE);
        set(level, x + 3, y + 11, z + 6, Blocks.STONE);
        set(level, x + 4, y + 11, z + 6, Blocks.AIR);
        set(level, x + 5, y + 11, z + 6, Blocks.STONE);
        set(level, x + 6, y + 11, z + 6, Blocks.STONE);
        set(level, x + 7, y + 11, z + 6, Blocks.STONE);
        set(level, x + 2, y + 11, z + 7, Blocks.GRAVEL);
        set(level, x + 3, y + 11, z + 7, Blocks.STONE);
        set(level, x + 4, y + 11, z + 7, Blocks.STONE);
        set(level, x + 5, y + 11, z + 7, Blocks.GRAVEL);
        set(level, x + 6, y + 11, z + 7, Blocks.STONE);

        // === Слой y+12 ===
        set(level, x + 3, y + 12, z + 1, Blocks.STONE);
        set(level, x + 4, y + 12, z + 1, Blocks.STONE);
        set(level, x + 5, y + 12, z + 1, Blocks.STONE);
        set(level, x + 2, y + 12, z + 2, Blocks.STONE);
        set(level, x + 3, y + 12, z + 2, Blocks.STONE);
        set(level, x + 4, y + 12, z + 2, Blocks.STONE);
        set(level, x + 5, y + 12, z + 2, Blocks.STONE);
        set(level, x + 6, y + 12, z + 2, Blocks.GRAVEL);
        set(level, x + 1, y + 12, z + 3, Blocks.GRAVEL);
        set(level, x + 2, y + 12, z + 3, Blocks.STONE);
        set(level, x + 3, y + 12, z + 3, Blocks.AIR);
        set(level, x + 4, y + 12, z + 3, Blocks.AIR);
        set(level, x + 5, y + 12, z + 3, Blocks.AIR);
        set(level, x + 6, y + 12, z + 3, Blocks.STONE);
        set(level, x + 7, y + 12, z + 3, Blocks.STONE);
        set(level, x + 1, y + 12, z + 4, Blocks.STONE);
        set(level, x + 2, y + 12, z + 4, Blocks.STONE);
        set(level, x + 3, y + 12, z + 4, Blocks.AIR);
        set(level, x + 4, y + 12, z + 4, Blocks.AIR);
        set(level, x + 5, y + 12, z + 4, Blocks.AIR);
        set(level, x + 6, y + 12, z + 4, Blocks.STONE);
        set(level, x + 7, y + 12, z + 4, Blocks.STONE);
        set(level, x + 1, y + 12, z + 5, Blocks.STONE);
        set(level, x + 2, y + 12, z + 5, Blocks.STONE);
        set(level, x + 3, y + 12, z + 5, Blocks.AIR);
        set(level, x + 4, y + 12, z + 5, Blocks.AIR);
        set(level, x + 5, y + 12, z + 5, Blocks.AIR);
        set(level, x + 6, y + 12, z + 5, Blocks.STONE);
        set(level, x + 7, y + 12, z + 5, Blocks.GRAVEL);
        set(level, x + 2, y + 12, z + 6, Blocks.STONE);
        set(level, x + 3, y + 12, z + 6, Blocks.STONE);
        set(level, x + 4, y + 12, z + 6, Blocks.STONE);
        set(level, x + 5, y + 12, z + 6, Blocks.STONE);
        set(level, x + 6, y + 12, z + 6, Blocks.STONE);
        set(level, x + 3, y + 12, z + 7, Blocks.STONE);
        set(level, x + 4, y + 12, z + 7, Blocks.STONE);
        set(level, x + 5, y + 12, z + 7, Blocks.STONE);

        // === Слой y+13 ===
        set(level, x + 4, y + 13, z + 1, Blocks.GRAVEL);
        set(level, x + 3, y + 13, z + 2, Blocks.STONE);
        set(level, x + 4, y + 13, z + 2, Blocks.STONE);
        set(level, x + 5, y + 13, z + 2, Blocks.STONE);
        set(level, x + 2, y + 13, z + 3, Blocks.STONE);
        set(level, x + 3, y + 13, z + 3, Blocks.STONE);
        set(level, x + 4, y + 13, z + 3, Blocks.AIR);
        set(level, x + 5, y + 13, z + 3, Blocks.STONE);
        set(level, x + 6, y + 13, z + 3, Blocks.STONE);
        set(level, x + 1, y + 13, z + 4, Blocks.STONE);
        set(level, x + 2, y + 13, z + 4, Blocks.STONE);
        set(level, x + 3, y + 13, z + 4, Blocks.AIR);
        set(level, x + 4, y + 13, z + 4, Blocks.AIR);
        set(level, x + 5, y + 13, z + 4, Blocks.AIR);
        set(level, x + 6, y + 13, z + 4, Blocks.STONE);
        set(level, x + 7, y + 13, z + 4, Blocks.STONE);
        set(level, x + 2, y + 13, z + 5, Blocks.STONE);
        set(level, x + 3, y + 13, z + 5, Blocks.STONE);
        set(level, x + 4, y + 13, z + 5, Blocks.AIR);
        set(level, x + 5, y + 13, z + 5, Blocks.STONE);
        set(level, x + 6, y + 13, z + 5, Blocks.GRAVEL);
        set(level, x + 3, y + 13, z + 6, Blocks.STONE);
        set(level, x + 4, y + 13, z + 6, Blocks.STONE);
        set(level, x + 5, y + 13, z + 6, Blocks.STONE);
        set(level, x + 4, y + 13, z + 7, Blocks.GRAVEL);

        // === Слой y+14 ===
        set(level, x + 3, y + 14, z + 2, Blocks.GRAVEL);
        set(level, x + 4, y + 14, z + 2, Blocks.GRAVEL);
        set(level, x + 5, y + 14, z + 2, Blocks.STONE);
        set(level, x + 2, y + 14, z + 3, Blocks.STONE);
        set(level, x + 3, y + 14, z + 3, Blocks.STONE);
        set(level, x + 4, y + 14, z + 3, Blocks.AIR);
        set(level, x + 5, y + 14, z + 3, Blocks.STONE);
        set(level, x + 6, y + 14, z + 3, Blocks.STONE);
        set(level, x + 2, y + 14, z + 4, Blocks.STONE);
        set(level, x + 3, y + 14, z + 4, Blocks.AIR);
        set(level, x + 4, y + 14, z + 4, Blocks.AIR);
        set(level, x + 5, y + 14, z + 4, Blocks.AIR);
        set(level, x + 6, y + 14, z + 4, Blocks.STONE);
        set(level, x + 2, y + 14, z + 5, Blocks.GRAVEL);
        set(level, x + 3, y + 14, z + 5, Blocks.STONE);
        set(level, x + 4, y + 14, z + 5, Blocks.AIR);
        set(level, x + 5, y + 14, z + 5, Blocks.STONE);
        set(level, x + 6, y + 14, z + 5, Blocks.STONE);
        set(level, x + 3, y + 14, z + 6, Blocks.STONE);
        set(level, x + 4, y + 14, z + 6, Blocks.STONE);
        set(level, x + 5, y + 14, z + 6, Blocks.GRAVEL);

        // === Слой y+15 ===
        set(level, x + 3, y + 15, z + 3, Blocks.GRAVEL);
        set(level, x + 4, y + 15, z + 3, Blocks.STONE);
        set(level, x + 5, y + 15, z + 3, Blocks.GRAVEL);
        set(level, x + 3, y + 15, z + 4, Blocks.STONE);
        set(level, x + 4, y + 15, z + 4, Blocks.AIR);
        set(level, x + 5, y + 15, z + 4, Blocks.STONE);
        set(level, x + 3, y + 15, z + 5, Blocks.GRAVEL);
        set(level, x + 4, y + 15, z + 5, Blocks.STONE);
        set(level, x + 5, y + 15, z + 5, Blocks.STONE);

        // === Слой y+16 — гейзер ===
        set(level, x + 4, y + 16, z + 3, Blocks.STONE);
        set(level, x + 3, y + 16, z + 4, Blocks.STONE);
        set(level, x + 4, y + 16, z + 4, cfg.geyserBlock().defaultBlockState());
        set(level, x + 5, y + 16, z + 4, Blocks.STONE);
        set(level, x + 4, y + 16, z + 5, Blocks.STONE);

        // === Слой y+17 ===
        set(level, x + 4, y + 17, z + 3, Blocks.STONE);
        set(level, x + 3, y + 17, z + 4, Blocks.STONE);
        set(level, x + 4, y + 17, z + 4, Blocks.AIR);
        set(level, x + 5, y + 17, z + 4, Blocks.STONE);
        set(level, x + 4, y + 17, z + 5, Blocks.STONE);

        return true;
    }

    private boolean canPlaceGeyser(WorldGenLevel level, BlockPos pos, Block geyserBlock) {
        BlockState below = level.getBlockState(pos.below());

        if (geyserBlock == ModBlocks.GEYSIR_CHLORINE.get()) {
            return below.is(Blocks.GRASS_BLOCK);
        }
        if (geyserBlock == ModBlocks.GEYSIR_WATER.get()) {
            return below.isSolid();
        }
        if (geyserBlock == ModBlocks.GEYSIR_VAPOR.get()) {
            return below.is(BlockTags.BASE_STONE_OVERWORLD)
                    || below.is(Blocks.SNOW_BLOCK)
                    || below.is(Blocks.POWDER_SNOW);
        }
        if (geyserBlock == ModBlocks.GEYSIR_NETHER.get()) {
            return below.is(Blocks.NETHERRACK);
        }
        return below.isSolid();
    }

    private void set(WorldGenLevel level, int x, int y, int z, Block block) {
        if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) return;
        level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 3);
    }

    private void set(WorldGenLevel level, int x, int y, int z, BlockState state) {
        if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) return;
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}