package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.bomb.TileEntityLandmine;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class LandmineFeature extends Feature<LandmineConfiguration> {

    public LandmineFeature(Codec<LandmineConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<LandmineConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource rand = context.random();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int x = pos.getX() + i;
                int z = pos.getZ() + j;
                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

                // Мина ставится на y (на поверхность), а проверяем блок под ней на y-1
                BlockPos placePos = new BlockPos(x, y, z);
                BlockPos belowPos = placePos.below();
                BlockState belowState = level.getBlockState(belowPos);

                // Выбираем тип мины в зависимости от блока под ней
                Block mineBlock;
                if (belowState.getBlock() == Blocks.WATER || belowState.liquid()) {
                    mineBlock = ModBlocks.MINE_NAVAL.get();

                    // Проверяем, есть ли вода на два блока вниз (глубина 2)
                    BlockPos deeperPos = belowPos.below();
                    if (level.getBlockState(deeperPos).liquid()) {
                        // Ставим мину на уровень ниже (глубина 2)
                        placePos = deeperPos;
                    } else {
                        // Ставим мину на первый уровень воды (глубина 1)
                        placePos = belowPos;
                    }

                    level.setBlock(placePos, mineBlock.defaultBlockState(), 2);

                    if (level.getBlockEntity(placePos) instanceof TileEntityLandmine landmine) {
                        landmine.waitingForPlayer = true;
                    }
                    return true;
                } else {
                    mineBlock = ModBlocks.MINE_AP.get();

                    // Проверяем, что место для мины свободно
                    if (level.getBlockState(placePos).isAir()) {
                        level.setBlock(placePos, mineBlock.defaultBlockState(), 2);

                        if (level.getBlockEntity(placePos) instanceof TileEntityLandmine landmine) {
                            landmine.waitingForPlayer = true;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
