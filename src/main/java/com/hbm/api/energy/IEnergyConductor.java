package com.hbm.api.energy;

import com.hbm.uninos.EnergyNode;
import com.hbm.util.Library;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEnergyConductor extends IEnergyConnector {

    /**
     * Создаёт узел сети в позиции тайла, со стандартными шестью соединениями.
     * @param tile тайл-проводник
     * @return узел EnergyNode
     */
    default EnergyNode createNode(BlockEntity tile) {
        BlockPos pos = tile.getBlockPos();
        return new EnergyNode(
                new BlockPos(pos.getX(), pos.getY(), pos.getZ())
        ).setConnections(
                new Library.PosDir(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()), Direction.EAST),
                new Library.PosDir(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()), Direction.WEST),
                new Library.PosDir(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), Direction.UP),
                new Library.PosDir(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()), Direction.DOWN),
                new Library.PosDir(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1), Direction.SOUTH),
                new Library.PosDir(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1), Direction.NORTH)
        );
    }
}