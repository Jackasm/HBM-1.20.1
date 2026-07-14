package com.hbm.blocks.network;

import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBlockFluidDuct {

    void changeTypeRecursively(Level world, BlockPos pos, FluidTypeHBM prevType, FluidTypeHBM type, int loopsRemaining);
}
