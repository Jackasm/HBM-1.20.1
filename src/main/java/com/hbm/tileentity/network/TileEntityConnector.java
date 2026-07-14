package com.hbm.tileentity.network;

import com.hbm.blocks.network.ConnectorRedWire;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.GenNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TileEntityConnector extends TileEntityPylonBase {

    public TileEntityConnector(BlockPos pos, BlockState state) {
        super(ModTileEntity.RED_CONNECTOR.get(), pos, state);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.SINGLE;
    }

    @Override
    public Vec3[] getMountPos() {
        return new Vec3[]{new Vec3(0.5, 0.5, 0.5)};
    }

    @Override
    public double getMaxWireLength() {
        return 10;
    }

    @Override
    public EnergyNode createNode() {
        Direction facing = getBlockState().getValue(ConnectorRedWire.FACING);
        Direction dir = facing.getOpposite();

        BlockPos pos = getBlockPos();
        BlockPos backPos = pos.relative(dir);

        GenNode.ConnectionPoint selfPoint = new GenNode.ConnectionPoint(pos, facing);
        GenNode.ConnectionPoint backPoint = new GenNode.ConnectionPoint(backPos, dir);

        return new EnergyNode(pos).setConnections(selfPoint, backPoint);
    }

    @Override
    public boolean canConnect(Direction dir) {
        return getBlockState().getValue(ConnectorRedWire.FACING).getOpposite() == dir;
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}