package com.hbm.datagen.worldgen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Утилита для генерации данжей (замена 1.7.10 DungeonToolbox).
 * Использует BlockState вместо MetaBlock.
 */
public class DungeonToolbox {

    /** Ставит блоки в кубе, случайно выбирая из списка состояний. */
    public static void generateBox(WorldGenLevel level, int x, int y, int z,
                                   int sx, int sy, int sz, List<BlockState> states) {
        if (states.isEmpty()) return;
        RandomSource rand = level.getRandom();

        for (int i = x; i < x + sx; i++) {
            for (int j = y; j < y + sy; j++) {
                for (int k = z; k < z + sz; k++) {
                    BlockState state = states.get(rand.nextInt(states.size()));
                    set(level, i, j, k, state);
                }
            }
        }
    }

    /** Ставит один блок-состояние в кубе. */
    public static void generateBox(WorldGenLevel level, int x, int y, int z,
                                   int sx, int sy, int sz, BlockState state) {
        for (int i = x; i < x + sx; i++) {
            for (int j = y; j < y + sy; j++) {
                for (int k = z; k < z + sz; k++) {
                    set(level, i, j, k, state);
                }
            }
        }
    }

    /** Случайный элемент из списка. */
    public static <T> T getRandom(List<T> list, RandomSource rand) {
        if (list.isEmpty()) return null;
        return list.get(rand.nextInt(list.size()));
    }

    private static void set(WorldGenLevel level, int x, int y, int z, BlockState state) {
        if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
            level.setBlock(new BlockPos(x, y, z), state, 2);
        }
    }
}