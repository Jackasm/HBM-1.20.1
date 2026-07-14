package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class BroadcasterFeature extends Feature<BroadcasterConfiguration> {

    public BroadcasterFeature(Codec<BroadcasterConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BroadcasterConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource rand = context.random();
        BroadcasterConfiguration config = context.config();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int x = pos.getX() + i;
                int z = pos.getZ() + j;
                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

                mutablePos.set(x, y, z);

                if (level.getBlockState(mutablePos.below()).canBeReplaced() &&
                        level.getBlockState(mutablePos).isAir()) {

                    BlockState state = config.broadcasterBlock().defaultBlockState();
                    state = state.setValue(BlockStateProperties.HORIZONTAL_FACING,
                            Direction.from2DDataValue(rand.nextInt(4)));

                    level.setBlock(mutablePos, state, 2);
                    return true;
                }
            }
        }

        return false;
    }
}
