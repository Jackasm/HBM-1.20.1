package com.hbm.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IBlowable { //sloppy toppy

    /** Called server-side when a fan blows on an IBlowable in range every tick. */
    void applyFan(Level level, BlockPos pos, Direction dir, int dist);
}

