package com.hbm.tileentity.conduit;

import com.hbm.api.fluid.FluidNode;
import com.hbm.api.fluid.IFluidConnector;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.uninos.UniNodespace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class FluidChannel extends ConduitChannel implements IFluidConnector {

    private EnumSet<Direction> lastKnownConnections = EnumSet.noneOf(Direction.class);

    private FluidNode fluidNode;

    public FluidChannel(ConduitEntry entry) {
        super(entry);
    }

    @Override
    public void tick(ConduitTileEntity host) {
        if (Objects.requireNonNull(host.getLevel()).isClientSide) return;

        if (fluidNode != null && !connections.equals(lastKnownConnections)) {
            UniNodespace.destroyNode(host.getLevel(), host.getBlockPos(),
                    Objects.requireNonNull(entry.fluid).getNetworkProvider());
            fluidNode = null;
        }
        this.lastKnownConnections = connections.clone();

        if (fluidNode == null || fluidNode.expired) {
            fluidNode = (FluidNode) UniNodespace.getNode(host.getLevel(), host.getBlockPos(), Objects.requireNonNull(entry.fluid).getNetworkProvider());
            if (fluidNode == null || fluidNode.expired) {
                fluidNode = createNode(host);
                UniNodespace.createNode(host.getLevel(), fluidNode);
            }
        }
    }

    private FluidNode createNode(ConduitTileEntity host) {
        BlockPos pos = host.getBlockPos();
        FluidNode node = new FluidNode(Objects.requireNonNull(entry.fluid).getNetworkProvider(), pos);
        List<com.hbm.uninos.GenNode.ConnectionPoint> points = new ArrayList<>();
        for (Direction dir : connections) {
            points.add(new com.hbm.uninos.GenNode.ConnectionPoint(pos.relative(dir), dir));
        }
        return node.setConnections(points.toArray(new com.hbm.uninos.GenNode.ConnectionPoint[0]));
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return entry.fluid == type;
    }
}