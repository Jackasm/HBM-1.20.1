package com.hbm.blocks.light;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockTorchLight extends BlockLightBase {

    private static final int MAX_TICKS_TO_LIVE = 30;

    public BlockTorchLight() {
        super(14); // уровень света как у факела
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            RandomSource rand = level.getRandom();
            int lifetime = 20 + rand.nextInt(MAX_TICKS_TO_LIVE);
            level.scheduleTick(pos, this, lifetime);
        }
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.removeBlock(pos, false);
    }
}