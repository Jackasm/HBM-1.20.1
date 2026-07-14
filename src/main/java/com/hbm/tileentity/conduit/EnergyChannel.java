package com.hbm.tileentity.conduit;

import com.hbm.api.energy.IEnergyConnector;
import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.EnergyNodespace;
import com.hbm.uninos.GenNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class EnergyChannel extends ConduitChannel implements IEnergyConnector {

    private EnergyNode energyNode;
    private EnumSet<Direction> lastKnownConnections = EnumSet.noneOf(Direction.class);

    public EnergyChannel() {
        super(ConduitEntry.energy());
    }

    @Override
    public void tick(ConduitTileEntity host) {
        if (Objects.requireNonNull(host.getLevel()).isClientSide) return;

        if (energyNode != null && !connections.equals(lastKnownConnections)) {
            EnergyNodespace.destroyNode(host.getLevel(), host.getBlockPos());
            energyNode = null;
        }
        this.lastKnownConnections = connections.clone();

        if (energyNode == null || energyNode.expired) {
            energyNode = EnergyNodespace.getNode(host.getLevel(), host.getBlockPos());
            if (energyNode == null || energyNode.expired) {
                energyNode = createNode(host);
                EnergyNodespace.createNode(host.getLevel(), energyNode);
            }
        }
    }

    private EnergyNode createNode(ConduitTileEntity host) {
        BlockPos pos = host.getBlockPos();
        EnergyNode node = new EnergyNode(pos);
        List<GenNode.ConnectionPoint> points = new ArrayList<>();
        for (Direction dir : connections) {
            points.add(new GenNode.ConnectionPoint(pos.relative(dir), dir));
        }
        return node.setConnections(points.toArray(new GenNode.ConnectionPoint[0]));
    }
}