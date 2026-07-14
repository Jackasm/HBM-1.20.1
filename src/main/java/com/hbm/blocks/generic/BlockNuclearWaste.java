package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public class BlockNuclearWaste extends BlockHazard {

    public BlockNuclearWaste(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        Direction dir = Direction.values()[random.nextInt(6)];
        BlockPos targetPos = pos.relative(dir);

        if (random.nextInt(2) == 0 && level.getBlockState(targetPos).isAir()) {
            level.setBlock(targetPos, ModBlocks.GAS_RADON_DENSE.get().defaultBlockState(), 3);
        }

        super.randomTick(state, level, pos, random);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .strength(5.0F, 10.0F)
                .randomTicks()
                .noOcclusion();
    }
}