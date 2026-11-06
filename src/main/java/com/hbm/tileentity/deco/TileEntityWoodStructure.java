package com.hbm.tileentity.deco;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityWoodStructure extends BlockEntity {

    public TileEntityWoodStructure(BlockPos pos, BlockState state) {
        super(ModTileEntity.WOOD_STRUCTURE.get(), pos, state);
    }
}