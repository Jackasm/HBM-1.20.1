package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.uninos.GenNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * IFluidConductor with added node creation method
 * @author hbm
 */
public interface IFluidPipe extends IFluidConnector {

    default FluidNode createNode(FluidTypeHBM type) {
        BlockEntity tile = (BlockEntity) this;
        BlockPos pos = tile.getBlockPos();

        // Создаём массив ConnectionPoint
        GenNode.ConnectionPoint[] connections = new GenNode.ConnectionPoint[] {
                new GenNode.ConnectionPoint(pos.relative(Direction.EAST), Direction.EAST),
                new GenNode.ConnectionPoint(pos.relative(Direction.WEST), Direction.WEST),
                new GenNode.ConnectionPoint(pos.relative(Direction.UP), Direction.UP),
                new GenNode.ConnectionPoint(pos.relative(Direction.DOWN), Direction.DOWN),
                new GenNode.ConnectionPoint(pos.relative(Direction.SOUTH), Direction.SOUTH),
                new GenNode.ConnectionPoint(pos.relative(Direction.NORTH), Direction.NORTH)
        };

        return new FluidNode(type.getNetworkProvider(), pos).setConnections(connections);
    }
}
