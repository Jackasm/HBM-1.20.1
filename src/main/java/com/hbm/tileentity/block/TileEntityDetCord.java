package com.hbm.tileentity.block;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityDetCord extends BlockEntity {
    public TileEntityDetCord(BlockPos pos, BlockState state) {
        super(ModTileEntity.DET_CORD.get(), pos, state);
    }
}
