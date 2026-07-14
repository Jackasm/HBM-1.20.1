package com.hbm.tileentity.deco;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityDeco extends BlockEntity {
    public TileEntityDeco(BlockPos pos, BlockState state) {
        super(ModTileEntity.DECO.get(), pos, state);
    }
}