package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.uninos.GenNode;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.Compat;
import com.hbm.util.HBMEnums;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IFluidReceiver extends IFluidUser {

    /**
     * Передача жидкости приемнику
     */
    long transferFluid(FluidTypeHBM type, int pressure, long amount);


    /**
     * Получение текущего спроса на жидкость
     */
    long getDemand(FluidTypeHBM type, int pressure);

    default int[] getReceivingPressureRange(FluidTypeHBM type) { return DEFAULT_PRESSURE_RANGE; }

    default long getReceiverSpeed(FluidTypeHBM type, int pressure) { return 1_000_000_000; }

    default HBMEnums.ConnectionPriority getFluidPriority() {
        return HBMEnums.ConnectionPriority.NORMAL;
    }

    default void trySubscribe(FluidTypeHBM type, Level level, BlockPos pos, Direction dir) {

        BlockEntity te = Compat.getTileStandard(level, pos);
        boolean red = false;

        if(te instanceof IFluidConnector) {
            IFluidConnector con = (IFluidConnector) te;
            if(!con.canConnect(type, dir.getOpposite())) return;

            GenNode node = UniNodespace.getNode(level, pos.getX(), pos.getY(), pos.getZ(), type.getNetworkProvider());

            if(node != null && node.net != null) {
                node.net.addReceiver(this);
                red = true;
            }
        }

        if(particleDebug) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "network");
            data.putString("mode", "fluid");
            data.putInt("color", type.getColor());
            double posX = pos.getX() + 0.5 + dir.getStepX() * 0.5 + level.random.nextDouble() * 0.5 - 0.25;
            double posY = pos.getY() + 0.5 + dir.getStepY() * 0.5 + level.random.nextDouble() * 0.5 - 0.25;
            double posZ = pos.getZ() + 0.5 + dir.getStepZ() * 0.5 + level.random.nextDouble() * 0.5 - 0.25;
            data.putDouble("mX", -dir.getStepX() * (red ? 0.025 : 0.1));
            data.putDouble("mY", -dir.getStepY() * (red ? 0.025 : 0.1));
            data.putDouble("mZ", -dir.getStepZ() * (red ? 0.025 : 0.1));
            PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data, posX, posY, posZ), level, pos, 25);
        }
    }

    default void subscribeToAllAround(FluidTypeHBM type, BlockEntity tile) {
        subscribeToAllAround(type, tile.getLevel(), tile.getBlockPos());
    }

    default void subscribeToAllAround(FluidTypeHBM type, Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.relative(dir);
            trySubscribe(type, level, targetPos, dir);
        }
    }
}