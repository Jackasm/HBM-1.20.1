package com.hbm.tileentity.machine;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityFloodlightBeam extends BlockEntity {

    public TileEntityFloodlight cache;
    public BlockPos sourcePos;
    public int index;

    public TileEntityFloodlightBeam(BlockPos pos, BlockState state) {
        super(ModTileEntity.FLOODLIGHT_BEAM.get(), pos, state);
    }

    public void setSource(TileEntityFloodlight floodlight, BlockPos sourcePos, int index) {
        this.cache = floodlight;
        this.sourcePos = sourcePos;
        this.index = index;
    }
}