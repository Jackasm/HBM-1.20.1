package com.hbm.api.fluid;

import com.hbm.uninos.GenNode;
import com.hbm.uninos.INetworkProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class FluidNode extends GenNode<FluidNet> {

    public FluidNode(INetworkProvider<FluidNet> provider, BlockPos... positions) {
        super(provider, positions);
    }

    @Override
    public FluidNode setConnections(ConnectionPoint... connections) {
        super.setConnections(connections);
        return this;
    }

    public FluidNode setConnections(BlockPos[] positions, Direction[] directions) {
        super.setConnections(positions, directions);
        return this;
    }

    public FluidNode addConnection(BlockPos pos, Direction dir) {
        super.addConnection(pos, dir);
        return this;
    }

    public FluidNode addConnection(ConnectionPoint point) {
        super.addConnection(point);
        return this;
    }
}

