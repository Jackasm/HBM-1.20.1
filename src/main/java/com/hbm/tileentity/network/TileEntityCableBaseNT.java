package com.hbm.tileentity.network;

import com.hbm.api.energy.EnergyNet;
import com.hbm.api.energy.IEnergyConductor;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.EnergyNodespace;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityCableBaseNT extends TileEntityLoadedBase implements IEnergyConductor {

    protected EnergyNode node;

    public TileEntityCableBaseNT(BlockPos pos, BlockState state) {
        super(ModTileEntity.RED_CABLE.get(), pos, state);
    }

    public TileEntityCableBaseNT(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (this.node == null || this.node.expired) {
            this.node = EnergyNodespace.getNode(level, worldPosition);
            if (this.node == null || this.node.expired) {
                this.node = this.createNode(this);
                EnergyNodespace.createNode(level, this.node);
            }
        }

        if (this.node != null && this.node.hasValidNet() && this instanceof TileEntityPylonBase pylon) {
            for (int[] con : pylon.getConnected()) {
                BlockPos conPos = new BlockPos(con[0], con[1], con[2]);
                EnergyNode remoteNode = EnergyNodespace.getNode(level, conPos);
                if (remoteNode != null && remoteNode.hasValidNet()) {
                    EnergyNet localNet = this.node.getNet();
                    EnergyNet remoteNet = remoteNode.getNet();
                    if (localNet != null && remoteNet != null && !localNet.equals(remoteNet)) {
                        localNet.merge(remoteNet);
                    }
                }
            }
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        if (level != null && !level.isClientSide) {
            if (this.node != null) {
                EnergyNodespace.destroyNode(level, worldPosition);
            }
        }
    }

    public boolean shouldCreateNode() {
        return true;
    }
}