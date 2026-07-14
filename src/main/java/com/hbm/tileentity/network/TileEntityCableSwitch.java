package com.hbm.tileentity.network;

import com.hbm.blocks.network.CableSwitch;
import com.hbm.uninos.EnergyNodespace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityCableSwitch extends TileEntityCableBaseNT {

    public TileEntityCableSwitch(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public void updateState() {
        if (level != null && !level.isClientSide) {
            if (!level.getBlockState(worldPosition).getValue(CableSwitch.POWERED) && this.node != null) {
                EnergyNodespace.destroyNode(level, worldPosition);
                this.node = null;
            }
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }

    @Override
    public boolean shouldCreateNode() {
        return level != null && level.getBlockState(worldPosition).getValue(CableSwitch.POWERED);
    }
}