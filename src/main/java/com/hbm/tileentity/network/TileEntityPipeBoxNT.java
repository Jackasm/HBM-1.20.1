package com.hbm.tileentity.network;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPipeBoxNT extends TileEntityPipeBaseNT {

    public TileEntityPipeBoxNT(BlockPos pos, BlockState state) {
        super(ModTileEntity.FLUID_DUCT_BOX.get(), pos, state);
    }
}