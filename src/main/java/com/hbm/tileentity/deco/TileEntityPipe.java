package com.hbm.tileentity.deco;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPipe extends BlockEntity {

    public TileEntityPipe(BlockPos pos, BlockState state) {
        super(ModTileEntity.PIPE_TE.get(), pos, state);
    }
}