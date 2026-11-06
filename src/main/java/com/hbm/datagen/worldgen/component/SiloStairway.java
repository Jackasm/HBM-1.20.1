package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SiloStairway {

    public SiloStairway() { }

    public static void build(SiloComponent c, WorldGenLevel level, RandomSource rand, BoundingBox box) {
        BlockState concreteColored = ModBlocks.CONCRETE_COLORED_BLUE.get().defaultBlockState();
        BlockState concreteSmooth = ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState();
        var bricks = new NTMComponent.ConcreteBricks();

        c.fillWithAir(level, box, 37, 26, 9, 37, 27, 10);
        c.placeBlockAtCurrentPosition(level, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 37, 25, 10, box);
        c.fillWithAir(level, box, 37, 24, 11, 37, 26, 11);
        c.fillWithAir(level, box, 37, 23, 12, 37, 25, 12);
        c.fillWithAir(level, box, 37, 21, 13, 37, 24, 14);

        for (int i = 0; i < 5; i++) {
            c.fillWithRandomizedBlocks(level, box, 36, 24 - i, 9 + i, 38, 24 - i, 9 + i, rand, bricks);
            c.placeStairs(level, box, ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, false, 37, 25 - i, 9 + i);
        }

        for (int i = 36; i <= 38; i += 2) {
            c.fillWithRandomizedBlocks(level, box, i, 26, 11, i, 26, 11, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, i, 25, 10, i, 25, 12, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, i, 24, 10, i, 24, 15, rand, bricks);
            c.fillWithRandomizedBlocks(level, box, i, 23, 11, i, 23, 15, rand, bricks);
            c.fillWithBlocks(level, box, i, 22, 12, i, 22, 15, concreteColored);
            c.fillWithRandomizedBlocks(level, box, i, 21, 13, i, 21, 15, rand, bricks);
        }

        c.fillWithBlocks(level, box, 36, 20, 14, 38, 20, 15, concreteSmooth);
        c.fillWithAir(level, box, 36, 21, 14, 36, 22, 14);
    }
}
