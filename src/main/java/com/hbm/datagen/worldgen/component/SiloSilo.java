package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.deco.BlockSteelGrate;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SiloSilo {

    public SiloSilo() { }

    public static void build(SiloComponent c, WorldGenLevel level, RandomSource rand, BoundingBox box) {
        BlockState concreteSmooth = ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState();
        BlockState concrete = ModBlocks.CONCRETE.get().defaultBlockState();
        BlockState steelGrate = ModBlocks.STEEL_GRATE.get().defaultBlockState().setValue(BlockSteelGrate.LAYER, 7);
        BlockState fenceMetal = ModBlocks.FENCE_METAL.get().defaultBlockState();
        var bricks = new NTMComponent.ConcreteBricks();

        // ===== TOP =====
        c.fillWithAir(level, box, 17, 21, 6, 21, 23, 6);
        c.fillWithAir(level, box, 15, 21, 7, 23, 23, 10);
        c.fillWithAir(level, box, 24, 21, 8, 24, 23, 10);
        c.fillWithAir(level, box, 25, 21, 9, 25, 23, 10);
        c.fillWithAir(level, box, 26, 21, 10, 26, 23, 10);
        c.fillWithAir(level, box, 23, 21, 11, 26, 23, 17);
        c.fillWithAir(level, box, 27, 21, 12, 27, 23, 16);
        c.fillWithAir(level, box, 26, 21, 18, 26, 23, 18);
        c.fillWithAir(level, box, 25, 21, 18, 25, 23, 19);
        c.fillWithAir(level, box, 24, 21, 18, 24, 23, 20);
        c.fillWithAir(level, box, 15, 21, 18, 23, 23, 21);
        c.fillWithAir(level, box, 17, 21, 22, 21, 23, 22);
        c.fillWithAir(level, box, 14, 21, 18, 14, 23, 20);
        c.fillWithAir(level, box, 13, 21, 18, 13, 23, 19);
        c.fillWithAir(level, box, 12, 21, 18, 12, 23, 18);
        c.fillWithAir(level, box, 12, 21, 11, 15, 23, 17);
        c.fillWithAir(level, box, 11, 21, 12, 11, 23, 16);
        c.fillWithAir(level, box, 12, 21, 10, 12, 23, 10);
        c.fillWithAir(level, box, 13, 21, 9, 13, 23, 10);
        c.fillWithAir(level, box, 14, 21, 8, 14, 23, 10);

        // Floor
        c.fillWithBlocks(level, box, 13, 20, 9, 13, 20, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 14, 20, 8, 14, 20, 9, concreteSmooth);
        c.fillWithBlocks(level, box, 15, 20, 7, 16, 20, 8, concreteSmooth);
        c.fillWithBlocks(level, box, 17, 20, 6, 21, 20, 7, concreteSmooth);
        c.fillWithBlocks(level, box, 22, 20, 7, 23, 20, 8, concreteSmooth);
        c.fillWithBlocks(level, box, 24, 20, 8, 24, 20, 9, concreteSmooth);
        c.placeBlockAtCurrentPosition(level, concreteSmooth, 25, 20, 9, box);
        c.fillWithBlocks(level, box, 25, 20, 10, 26, 20, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 26, 20, 12, 27, 20, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 25, 20, 17, 26, 20, 18, concreteSmooth);
        c.fillWithBlocks(level, box, 24, 20, 19, 25, 20, 19, concreteSmooth);
        c.placeBlockAtCurrentPosition(level, concreteSmooth, 24, 20, 20, box);
        c.fillWithBlocks(level, box, 22, 20, 20, 23, 20, 21, concreteSmooth);
        c.fillWithBlocks(level, box, 17, 20, 21, 21, 20, 22, concreteSmooth);
        c.fillWithBlocks(level, box, 15, 20, 20, 16, 20, 21, concreteSmooth);
        c.fillWithBlocks(level, box, 14, 20, 19, 14, 20, 20, concreteSmooth);

        // Grates
        c.fillWithBlocks(level, box, 14, 20, 10, 15, 20, 18, steelGrate);
        c.fillWithBlocks(level, box, 13, 20, 12, 13, 20, 16, steelGrate);
        c.fillWithBlocks(level, box, 17, 20, 8, 21, 20, 8, steelGrate);
        c.fillWithBlocks(level, box, 15, 20, 9, 23, 20, 9, steelGrate);
        c.fillWithBlocks(level, box, 16, 20, 10, 22, 20, 10, steelGrate);
        c.fillWithBlocks(level, box, 23, 20, 10, 24, 20, 18, steelGrate);
        c.fillWithBlocks(level, box, 25, 20, 12, 25, 20, 16, steelGrate);
        c.fillWithBlocks(level, box, 22, 20, 19, 23, 20, 19, steelGrate);
        c.fillWithBlocks(level, box, 15, 20, 19, 16, 20, 19, steelGrate);
        c.fillWithBlocks(level, box, 16, 20, 18, 22, 20, 18, steelGrate);

        // Ceiling
        c.fillWithBlocks(level, box, 11, 24, 12, 11, 24, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 12, 24, 10, 15, 24, 18, concreteSmooth);
        c.fillWithBlocks(level, box, 13, 24, 9, 15, 24, 9, concreteSmooth);
        c.fillWithBlocks(level, box, 14, 24, 8, 15, 24, 8, concreteSmooth);
        c.fillWithBlocks(level, box, 13, 24, 19, 15, 24, 19, concreteSmooth);
        c.fillWithBlocks(level, box, 14, 24, 20, 15, 24, 20, concreteSmooth);
        c.fillWithBlocks(level, box, 17, 24, 6, 21, 24, 6, concreteSmooth);
        c.fillWithBlocks(level, box, 15, 24, 7, 23, 24, 7, concreteSmooth);
        c.fillWithBlocks(level, box, 16, 24, 8, 22, 24, 10, concreteSmooth);
        c.fillWithBlocks(level, box, 27, 24, 12, 27, 24, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 23, 24, 10, 26, 24, 18, concreteSmooth);
        c.fillWithBlocks(level, box, 23, 24, 9, 25, 24, 9, concreteSmooth);
        c.fillWithBlocks(level, box, 23, 24, 8, 24, 24, 8, concreteSmooth);
        c.fillWithBlocks(level, box, 23, 24, 19, 25, 24, 19, concreteSmooth);
        c.fillWithBlocks(level, box, 23, 24, 20, 24, 24, 20, concreteSmooth);
        c.fillWithBlocks(level, box, 17, 24, 22, 21, 24, 22, concreteSmooth);
        c.fillWithBlocks(level, box, 15, 24, 21, 23, 24, 21, concreteSmooth);
        c.fillWithBlocks(level, box, 16, 24, 18, 22, 24, 20, concreteSmooth);

        // Walls
        c.fillWithRandomizedBlocks(level, box, 14, 20, 7, 14, 24, 7, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 13, 20, 8, 13, 24, 8, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 12, 21, 9, 12, 24, 9, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 11, 21, 10, 11, 24, 11, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 10, 21, 12, 10, 24, 16, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 11, 21, 17, 11, 24, 18, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 12, 21, 19, 12, 24, 19, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 13, 21, 20, 13, 24, 20, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 14, 20, 21, 14, 24, 21, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 15, 20, 22, 16, 24, 22, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 17, 20, 23, 21, 24, 23, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 22, 20, 22, 23, 24, 22, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 24, 20, 21, 24, 24, 21, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 25, 20, 20, 25, 24, 20, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 26, 20, 19, 26, 24, 19, rand, bricks);

        // ===== CENTER =====
        c.fillWithAir(level, box, 17, 2, 12, 21, 25, 16);
        for (int i = 5; i <= 17; i += 4) {
            if (((i - 5) / 4) % 2 == 0) {
                c.fillWithAir(level, box, 17, i, 8, 20, i + 3, 9);
                c.fillWithAir(level, box, 17, i, 10, 21, i + 2, 10);
                c.fillWithAir(level, box, 17, i, 18, 21, i + 2, 20);
            } else {
                c.fillWithAir(level, box, 18, i, 19, 21, i + 3, 20);
                c.fillWithAir(level, box, 17, i, 18, 21, i + 2, 18);
                c.fillWithAir(level, box, 17, i, 8, 21, i + 2, 10);
            }
            c.fillWithAir(level, box, 22, i, 10, 22, i + 2, 10);
            c.fillWithAir(level, box, 22, i, 9, 23, i + 2, 9);
            c.fillWithAir(level, box, 23, i, 10, 24, i + 2, 18);
            c.fillWithAir(level, box, 25, i, 12, 25, i + 2, 16);
            c.fillWithAir(level, box, 22, i, 19, 23, i + 2, 19);
            c.fillWithAir(level, box, 22, i, 18, 22, i + 2, 18);
            c.fillWithAir(level, box, 16, i, 18, 16, i + 2, 18);
            c.fillWithAir(level, box, 15, i, 19, 16, i + 2, 19);
            c.fillWithAir(level, box, 14, i, 10, 15, i + 2, 18);
            c.fillWithAir(level, box, 13, i, 12, 13, i + 2, 16);
            c.fillWithAir(level, box, 15, i, 9, 16, i + 2, 9);
            c.fillWithAir(level, box, 16, i, 10, 16, i + 2, 10);
        }
        for (int i = 6; i <= 22; i += 4) {
            c.fillWithAir(level, box, 16, i, 11, 18, i + 1, 11);
            c.fillWithAir(level, box, 16, i, 12, 16, i + 1, 13);
            c.fillWithAir(level, box, 16, i, 15, 16, i + 1, 16);
            c.fillWithAir(level, box, 16, i, 17, 18, i + 1, 17);
            c.fillWithAir(level, box, 20, i, 17, 22, i + 1, 17);
            c.fillWithAir(level, box, 22, i, 15, 22, i + 1, 16);
            c.fillWithAir(level, box, 22, i, 12, 22, i + 1, 13);
            c.fillWithAir(level, box, 20, i, 11, 22, i + 1, 11);
        }

        // Supports
        c.fillWithRandomizedBlocks(level, box, 22, 24, 17, 22, 24, 17, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 17, 24, 17, 21, 25, 17, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 16, 24, 17, 16, 24, 17, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 16, 24, 12, 16, 25, 16, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 16, 24, 11, 16, 24, 11, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 17, 24, 11, 21, 25, 11, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 22, 24, 11, 22, 24, 11, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 22, 24, 12, 22, 25, 16, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 19, 5, 11, 19, 23, 11, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 22, 5, 14, 22, 23, 14, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 19, 5, 17, 19, 23, 17, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 16, 5, 14, 16, 23, 14, rand, bricks);

        // Grates + railing
        for (int j = 8; j <= 20; j += 4) {
            for (int i = 16; i <= 22; i += 6) {
                c.fillWithBlocks(level, box, i, j, 15, i, j, 16, steelGrate);
                c.fillWithBlocks(level, box, i, j, 12, i, j, 13, steelGrate);
                c.fillWithBlocks(level, box, i, j + 1, 15, i, j + 1, 16, fenceMetal);
                c.fillWithBlocks(level, box, i, j + 1, 12, i, j + 1, 13, fenceMetal);
            }
            for (int k = 11; k <= 17; k += 6) {
                c.fillWithBlocks(level, box, 16, j, k, 18, j, k, steelGrate);
                c.fillWithBlocks(level, box, 20, j, k, 22, j, k, steelGrate);
                c.fillWithBlocks(level, box, 16, j + 1, k, 18, j + 1, k, fenceMetal);
                c.fillWithBlocks(level, box, 20, j + 1, k, 22, j + 1, k, fenceMetal);
            }
        }

        // Floors
        for (int j = 8; j <= 16; j += 4) {
            c.fillWithBlocks(level, box, 15, j, 11, 15, j, 17, concrete);
            c.fillWithBlocks(level, box, 16, j, 10, 22, j, 10, concrete);
            c.fillWithBlocks(level, box, 23, j, 11, 23, j, 17, concrete);
            c.fillWithBlocks(level, box, 16, j, 18, 22, j, 18, concrete);
            c.fillWithBlocks(level, box, 15, j, 9, 16, j, 9, concreteSmooth);
            c.fillWithBlocks(level, box, 14, j, 10, 15, j, 10, concreteSmooth);
            c.fillWithBlocks(level, box, 14, j, 11, 14, j, 17, concreteSmooth);
            c.fillWithBlocks(level, box, 13, j, 12, 13, j, 16, concreteSmooth);
            c.fillWithBlocks(level, box, 14, j, 18, 15, j, 18, concreteSmooth);
            c.fillWithBlocks(level, box, 15, j, 19, 16, j, 19, concreteSmooth);

            if ((j / 4) % 2 == 0) {
                c.fillWithBlocks(level, box, 20, j, 19, 21, j, 20, concreteSmooth);
                c.fillWithBlocks(level, box, 19, j, 19, 19, j + 1, 20, concreteSmooth);
                c.fillWithBlocks(level, box, 18, j, 19, 18, j + 2, 20, concreteSmooth);
                c.fillWithBlocks(level, box, 17, j, 19, 17, j + 3, 20, concreteSmooth);
                for (int i = 0; i < 4; i++) {
                    c.fillWithStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.EAST, false,
                            20 - i, j + 1 + i, 19, 20 - i, j + 1 + i, 20);
                }
            } else {
                c.fillWithBlocks(level, box, 17, j, 8, 18, j, 9, concreteSmooth);
                c.fillWithBlocks(level, box, 19, j, 8, 19, j + 1, 9, concreteSmooth);
                c.fillWithBlocks(level, box, 20, j, 8, 20, j + 2, 9, concreteSmooth);
                c.fillWithBlocks(level, box, 21, j, 8, 21, j + 3, 9, concreteSmooth);
                for (int i = 0; i < 4; i++) {
                    c.fillWithStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.WEST, false,
                            18 + i, j + 1 + i, 8, 18 + i, j + 1 + i, 9);
                }
            }

            c.fillWithBlocks(level, box, 22, j, 9, 23, j, 9, concreteSmooth);
            c.fillWithBlocks(level, box, 23, j, 10, 24, j, 10, concreteSmooth);
            c.fillWithBlocks(level, box, 24, j, 11, 24, j, 17, concreteSmooth);
            c.fillWithBlocks(level, box, 25, j, 12, 25, j, 16, concreteSmooth);
            c.fillWithBlocks(level, box, 23, j, 18, 24, j, 18, concreteSmooth);
            c.fillWithBlocks(level, box, 22, j, 19, 23, j, 19, concreteSmooth);
        }

        // Walls
        c.fillWithRandomizedBlocks(level, box, 17, 5, 7, 21, 19, 7, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 15, 4, 8, 16, 19, 8, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 14, 4, 9, 14, 19, 9, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 13, 4, 10, 13, 19, 11, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 12, 5, 12, 12, 19, 16, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 13, 4, 17, 13, 19, 18, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 14, 4, 19, 14, 19, 19, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 15, 4, 20, 16, 19, 20, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 17, 5, 21, 21, 19, 21, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 22, 4, 20, 23, 19, 20, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 24, 4, 19, 24, 19, 19, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 25, 4, 17, 25, 19, 18, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 26, 5, 12, 26, 19, 16, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 25, 4, 10, 25, 19, 11, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 24, 4, 9, 24, 19, 9, rand, bricks);
        c.fillWithRandomizedBlocks(level, box, 22, 4, 8, 23, 19, 8, rand, bricks);

        // ===== EXHAUST =====
        BlockState colored7 = ModBlocks.CONCRETE_COLORED_GRAY.get().defaultBlockState();

        c.fillWithBlocks(level, box, 17, 0, 7, 21, 0, 21, colored7);
        c.fillWithBlocks(level, box, 18, 1, 7, 20, 1, 7, colored7);
        c.fillWithBlocks(level, box, 17, 1, 7, 17, 1, 12, colored7);
        c.fillWithBlocks(level, box, 21, 1, 7, 21, 1, 12, colored7);
        c.fillWithBlocks(level, box, 18, 1, 11, 18, 1, 12, colored7);
        c.fillWithBlocks(level, box, 20, 1, 11, 20, 1, 12, colored7);
        c.fillWithBlocks(level, box, 18, 1, 21, 20, 1, 21, colored7);
        c.fillWithBlocks(level, box, 17, 1, 16, 17, 1, 21, colored7);
        c.fillWithBlocks(level, box, 21, 1, 16, 21, 1, 21, colored7);
        c.fillWithBlocks(level, box, 18, 1, 16, 18, 1, 17, colored7);
        c.fillWithBlocks(level, box, 20, 1, 16, 20, 1, 17, colored7);

        c.fillWithBlocks(level, box, 12, 0, 12, 16, 0, 16, colored7);
        c.fillWithBlocks(level, box, 22, 0, 12, 26, 0, 16, colored7);
        c.fillWithBlocks(level, box, 12, 1, 13, 12, 1, 15, colored7);
        c.fillWithBlocks(level, box, 12, 1, 16, 16, 1, 16, colored7);
        c.fillWithBlocks(level, box, 12, 1, 12, 16, 1, 12, colored7);
        c.fillWithBlocks(level, box, 16, 1, 15, 17, 1, 15, colored7);
        c.fillWithBlocks(level, box, 16, 1, 13, 17, 1, 13, colored7);
        c.fillWithBlocks(level, box, 26, 1, 13, 26, 1, 15, colored7);
        c.fillWithBlocks(level, box, 22, 1, 16, 26, 1, 16, colored7);
        c.fillWithBlocks(level, box, 22, 1, 12, 26, 1, 12, colored7);
        c.fillWithBlocks(level, box, 21, 1, 15, 22, 1, 15, colored7);
        c.fillWithBlocks(level, box, 21, 1, 13, 22, 1, 13, colored7);

        // Gray walls / floor
        c.fillWithBlocks(level, box, 18, 2, 21, 20, 3, 21, concreteSmooth);
        c.fillWithBlocks(level, box, 21, 2, 17, 21, 3, 21, concreteSmooth);
        c.fillWithBlocks(level, box, 22, 2, 16, 26, 3, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 26, 2, 13, 26, 3, 15, concreteSmooth);
        c.fillWithBlocks(level, box, 22, 2, 12, 26, 3, 12, concreteSmooth);
        c.fillWithBlocks(level, box, 21, 2, 7, 21, 3, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 18, 2, 7, 20, 3, 7, concreteSmooth);
        c.fillWithBlocks(level, box, 17, 2, 7, 17, 3, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 12, 2, 12, 16, 3, 12, concreteSmooth);
        c.fillWithBlocks(level, box, 12, 2, 13, 12, 3, 15, concreteSmooth);
        c.fillWithBlocks(level, box, 12, 2, 16, 16, 3, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 17, 2, 17, 17, 2, 21, concreteSmooth);
        c.fillWithBlocks(level, box, 17, 4, 17, 21, 4, 21, concreteSmooth);
        c.fillWithBlocks(level, box, 22, 4, 17, 23, 4, 19, concreteSmooth);
        c.fillWithBlocks(level, box, 24, 4, 17, 24, 4, 18, concreteSmooth);
        c.fillWithBlocks(level, box, 22, 4, 12, 26, 4, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 24, 4, 10, 24, 4, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 22, 4, 9, 23, 4, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 17, 4, 7, 21, 4, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 15, 4, 9, 16, 4, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 14, 4, 10, 14, 4, 11, concreteSmooth);
        c.fillWithBlocks(level, box, 12, 4, 12, 16, 4, 16, concreteSmooth);
        c.fillWithBlocks(level, box, 14, 4, 17, 14, 4, 18, concreteSmooth);
        c.fillWithBlocks(level, box, 15, 4, 17, 16, 4, 19, concreteSmooth);

        // Small stairs
        c.fillWithBlocks(level, box, 19, 5, 8, 19, 5, 9, concreteSmooth);
        c.fillWithBlocks(level, box, 20, 5, 8, 20, 6, 9, concreteSmooth);
        c.fillWithBlocks(level, box, 21, 5, 8, 21, 7, 9, concreteSmooth);
        for (int i = 0; i < 4; i++) {
            c.fillWithStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.WEST, false,
                    18 + i, 5 + i, 8, 18 + i, 5 + i, 9);
        }

        // Railing + air pockets
        c.placeBlockAtCurrentPosition(level, fenceMetal, 18, 5, 11, box);
        c.fillWithBlocks(level, box, 20, 5, 11, 22, 5, 11, fenceMetal);
        c.fillWithBlocks(level, box, 22, 5, 12, 22, 5, 13, fenceMetal);
        c.fillWithBlocks(level, box, 22, 5, 15, 22, 5, 17, fenceMetal);
        c.placeBlockAtCurrentPosition(level, fenceMetal, 20, 5, 17, box);
        c.fillWithBlocks(level, box, 16, 5, 17, 18, 5, 17, fenceMetal);
        c.fillWithBlocks(level, box, 16, 5, 15, 16, 5, 16, fenceMetal);
        c.fillWithBlocks(level, box, 16, 5, 11, 16, 5, 13, fenceMetal);
        c.placeBlockAtCurrentPosition(level, Blocks.AIR.defaultBlockState(), 21, 5, 17, box);
        c.placeBlockAtCurrentPosition(level, Blocks.AIR.defaultBlockState(), 17, 5, 11, box);

        // Ladders
        c.fillWithLadders(level, box, ModBlocks.LADDER_STEEL.get(), Direction.NORTH, 17, 2, 12, 17, 4, 12);
        c.fillWithLadders(level, box, ModBlocks.LADDER_STEEL.get(), Direction.SOUTH, 21, 2, 16, 21, 4, 16);

        // Launch pad
        c.placeCoreLaunchpad(level, box, ModBlocks.LAUNCH_PAD_RUSTED.get(), Direction.SOUTH, 19, 1, 14);
        c.fillSpace(level, box, 19, 1, 14, new int[] { 0, 0, 1, 1, 1, 1 },
                ModBlocks.LAUNCH_PAD_RUSTED.get(), Direction.SOUTH);
        for (int i = 0; i <= 2; i += 2)
            for (int k = 0; k <= 2; k += 2)
                c.makeExtra(level, box, ModBlocks.LAUNCH_PAD_RUSTED.get(), 18 + i, 1, 13 + k);

        c.placeBlockAtCurrentPosition(level, ModBlocks.RADIO_TORCH_RECEIVER.get().defaultBlockState(), 19, 0, 14, box);
        c.setRTTYFreq(level, box, 19, 0, 14, c.freq);

        // Air pockets
        c.fillWithAir(level, box, 18, 1, 8, 20, 3, 10);
        c.fillWithAir(level, box, 18, 2, 11, 20, 3, 11);
        c.fillWithAir(level, box, 19, 1, 11, 19, 1, 12);
        c.fillWithAir(level, box, 19, 1, 16, 19, 1, 17);
        c.fillWithAir(level, box, 18, 2, 17, 20, 3, 17);
        c.fillWithAir(level, box, 18, 1, 18, 20, 3, 20);
        c.fillWithAir(level, box, 13, 1, 13, 15, 3, 15);
        c.fillWithAir(level, box, 16, 2, 13, 16, 3, 15);
        c.fillWithAir(level, box, 16, 1, 14, 17, 1, 14);
        c.fillWithAir(level, box, 21, 1, 14, 22, 1, 14);
        c.fillWithAir(level, box, 22, 2, 13, 22, 3, 15);
        c.fillWithAir(level, box, 23, 1, 13, 25, 3, 15);
    }
                                   }
