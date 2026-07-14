package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

public interface IFluidConnectorBlock {

    /** dir is the face that is connected to, the direction going outwards from the block */
    boolean canConnect(FluidTypeHBM type, BlockGetter level, BlockPos pos, Direction dir);
}
