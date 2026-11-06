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

public class GeyserFeature extends Feature<GeyserConfiguration> {

    public GeyserFeature(com.mojang.serialization.Codec<GeyserConfiguration> codec) {
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

        int x = origin.getX() - 2;
        int y = origin.getY() - 11;
        int z = origin.getZ() - 2;

        // y+5 — слой stone (плоский)
        for (int dx = 0; dx <= 4; dx++)
            for (int dz = 0; dz <= 4; dz++)
                if (dx == 0 || dx == 4 || dz == 0 || dz == 4 || (dx >= 1 && dx <= 3 && dz >= 1 && dz <= 3))
                    set(level, x + dx, y + 5, z + dz, Blocks.STONE);

        // y+6 — stone
        fillRect(level, x, y + 6, z, 5, 5, Blocks.STONE);

        // y+7 — вода + yellowcake
        set(level, x + 1, y + 7, z + 1, Blocks.WATER);
        set(level, x + 2, y + 7, z + 1, ModBlocks.BLOCK_YELLOWCAKE.get().defaultBlockState());
        set(level, x + 3, y + 7, z + 1, Blocks.WATER);
        set(level, x + 1, y + 7, z + 2, ModBlocks.BLOCK_YELLOWCAKE.get().defaultBlockState());
        set(level, x + 2, y + 7, z + 2, Blocks.WATER);
        set(level, x + 3, y + 7, z + 2, Blocks.WATER);
        set(level, x + 1, y + 7, z + 3, Blocks.WATER);
        set(level, x + 2, y + 7, z + 3, ModBlocks.BLOCK_YELLOWCAKE.get().defaultBlockState());
        set(level, x + 3, y + 7, z + 3, ModBlocks.BLOCK_YELLOWCAKE.get().defaultBlockState());
        // периметр stone
        for (int dx = 0; dx <= 4; dx++) {
            set(level, x + dx, y + 7, z, Blocks.STONE);
            set(level, x + dx, y + 7, z + 4, Blocks.STONE);
        }
        for (int dz = 0; dz <= 4; dz++) {
            set(level, x, y + 7, z + dz, Blocks.STONE);
            set(level, x + 4, y + 7, z + dz, Blocks.STONE);
        }

        // y+8 — air сверху, периметр stone
        for (int dx = 0; dx <= 4; dx++) {
            set(level, x + dx, y + 8, z, Blocks.STONE);
            set(level, x + dx, y + 8, z + 4, Blocks.STONE);
        }
        for (int dz = 0; dz <= 4; dz++) {
            set(level, x, y + 8, z + dz, Blocks.STONE);
            set(level, x + 4, y + 8, z + dz, Blocks.STONE);
        }

        // y+9 — периметр stone
        for (int dx = 0; dx <= 4; dx++) {
            set(level, x + dx, y + 9, z, Blocks.STONE);
            set(level, x + dx, y + 9, z + 4, Blocks.STONE);
        }
        for (int dz = 0; dz <= 4; dz++) {
            set(level, x, y + 9, z + dz, Blocks.STONE);
            set(level, x + 4, y + 9, z + dz, Blocks.STONE);
        }
        set(level, x + 1, y + 9, z + 1, Blocks.STONE);
        set(level, x + 3, y + 9, z + 1, Blocks.STONE);
        set(level, x + 1, y + 9, z + 3, Blocks.STONE);
        set(level, x + 3, y + 9, z + 3, Blocks.STONE);

        // y+10 — верхний слой с гейзером
        fillRect(level, x, y + 10, z, 5, 5, Blocks.STONE);
        set(level, x + 1, y + 10, z + 1, Blocks.GRASS_BLOCK);
        set(level, x + 2, y + 10, z + 1, Blocks.STONE);
        set(level, x + 1, y + 10, z + 2, Blocks.GRASS_BLOCK);
        set(level, x + 2, y + 10, z + 2, cfg.geyserBlock().defaultBlockState());
        set(level, x + 3, y + 10, z + 2, Blocks.GRASS_BLOCK);
        set(level, x + 2, y + 10, z + 3, Blocks.GRASS_BLOCK);
        set(level, x + 0, y + 10, z + 4, Blocks.GRAVEL);
        set(level, x + 4, y + 10, z + 2, Blocks.GRAVEL);

        return true;
    }

    /**
     * Проверяет, можно ли поставить гейзер в этой точке.
     * Условие зависит от типа гейзера.
     */
    private boolean canPlaceGeyser(WorldGenLevel level, BlockPos pos, Block geyserBlock) {
        BlockState below = level.getBlockState(pos.below());

        // ХЛОРНЫЙ — только на траве
        if (geyserBlock == ModBlocks.GEYSIR_CHLORINE.get()) {
            return below.is(Blocks.GRASS_BLOCK);
        }

        // ВОДНЫЙ — на solid (песок, земля, камень)
        if (geyserBlock == ModBlocks.GEYSIR_WATER.get()) {
            return below.isSolid();
        }

        // ПАРОВОЙ — на камне или снегу
        if (geyserBlock == ModBlocks.GEYSIR_VAPOR.get()) {
            return below.is(BlockTags.BASE_STONE_OVERWORLD)
                    || below.is(Blocks.SNOW_BLOCK)
                    || below.is(Blocks.POWDER_SNOW);
        }

        // АДСКИЙ — только на адском камне
        if (geyserBlock == ModBlocks.GEYSIR_NETHER.get()) {
            return below.is(Blocks.NETHERRACK);
        }

        // По умолчанию — на solid
        return below.isSolid();
    }

    private void set(WorldGenLevel level, int x, int y, int z, net.minecraft.world.level.block.Block block) {
        level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 3);
    }

    private void set(WorldGenLevel level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }

    private void fillRect(WorldGenLevel level, int x, int y, int z, int sx, int sz, net.minecraft.world.level.block.Block block) {
        for (int dx = 0; dx < sx; dx++)
            for (int dz = 0; dz < sz; dz++)
                set(level, x + dx, y, z + dz, block);
    }
}