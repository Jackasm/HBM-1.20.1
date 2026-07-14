package com.hbm.tileentity.deco;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntitySimpleOBJ extends BlockEntity {
    public TileEntitySimpleOBJ(BlockPos pos, BlockState state) {
        super(ModTileEntity.SIMPLE_OBJ_BLOCK.get(), pos, state);
    }
}
