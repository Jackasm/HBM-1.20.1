package com.hbm.blocks.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

public interface IDetConnectible {

    default boolean canConnectToDetCord(BlockGetter level, BlockPos pos, Direction dir) {
        return true;
    }

    static boolean isConnectible(BlockGetter level, BlockPos pos, Direction dir) {
        Block block = level.getBlockState(pos).getBlock();
        if (block instanceof IDetConnectible connectible) {
            return connectible.canConnectToDetCord(level, pos, dir);
        }
        return false;
    }
}