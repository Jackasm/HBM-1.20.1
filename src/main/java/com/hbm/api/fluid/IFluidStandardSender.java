package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.uninos.GenNode;
import com.hbm.uninos.UniNodespace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * IFluidProvider with standard implementation for fluid provision and fluid removal.
 */
public interface IFluidStandardSender extends IFluidProvider {

    default void tryProvide(FluidTankHBM tank, Level world, BlockPos pos, Direction dir) {
        tryProvide(tank.getTankType(), tank.getPressure(), world, pos, dir);
    }

    default void sendFluid(FluidTankHBM tank, Level world, BlockPos pos, Direction dir) {
        tryProvide(tank.getTankType(), tank.getPressure(), world, pos, dir);
    }

    default void tryProvide(FluidTypeHBM type, Level world, BlockPos pos, Direction dir) {
        tryProvide(type, 0, world, pos, dir);
    }

    default void tryProvide(FluidTypeHBM type, int pressure, Level world, BlockPos pos, Direction dir) {

        BlockEntity te = world.getBlockEntity(pos);
        boolean red = false;

        if (te instanceof IFluidConnector con) {
            if (con.canConnect(type, dir.getOpposite())) {

                GenNode<FluidNet> node = null;
                GenNode<?> rawNode = UniNodespace.getNode(world, pos, type.getNetworkProvider());
                if (rawNode != null && rawNode.getNet() instanceof FluidNet) {
                    node = (GenNode<FluidNet>) rawNode; // безопасно, так как мы проверили тип сети
                }

                if (node != null && node.getNet() != null) {
                    node.getNet().addProvider(this);
                    red = true;
                }
            }
        }

        if (te != this && te instanceof IFluidReceiver receiver) {
            if (receiver.canConnect(type, dir.getOpposite())) {
                long provides = Math.min(this.getFluidAvailable(type, pressure), this.getProviderSpeed(type, pressure));
                long receives = Math.min(receiver.getDemand(type, pressure), receiver.getReceiverSpeed(type, pressure));
                long toTransfer = Math.min(provides, receives);
                toTransfer -= receiver.transferFluid(type, pressure, toTransfer);
                this.useUpFluid(type, pressure, toTransfer);
            }
        }

        if (particleDebug) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "network");
            data.putString("mode", "fluid");
            data.putInt("color", type.getColor());
            double posX = pos.getX() + 0.5 - dir.getStepX() * 0.5 + world.random.nextDouble() * 0.5 - 0.25;
            double posY = pos.getY() + 0.5 - dir.getStepY() * 0.5 + world.random.nextDouble() * 0.5 - 0.25;
            double posZ = pos.getZ() + 0.5 - dir.getStepZ() * 0.5 + world.random.nextDouble() * 0.5 - 0.25;
            data.putDouble("mX", dir.getStepX() * (red ? 0.025 : 0.1));
            data.putDouble("mY", dir.getStepY() * (red ? 0.025 : 0.1));
            data.putDouble("mZ", dir.getStepZ() * (red ? 0.025 : 0.1));
            PacketDispatcher.sendToAllAround(
                    new AuxParticlePacketNT(data, posX, posY, posZ),
                    world,
                    new BlockPos((int)posX, (int)posY, (int)posZ),
                    25
            );
        }
    }

    FluidTankHBM[] getSendingTanks();

    @Override
    default long getFluidAvailable(FluidTypeHBM type, int pressure) {
        long amount = 0;
        for (FluidTankHBM tank : getSendingTanks()) {
            if (tank.getTankType() == type && tank.getPressure() == pressure) {
                amount += tank.getFill();
            }
        }
        return amount;
    }

    @Override
    default void useUpFluid(FluidTypeHBM type, int pressure, long amount) {
        int tanks = 0;
        for (FluidTankHBM tank : getSendingTanks()) {
            if (tank.getTankType() == type && tank.getPressure() == pressure) {
                tanks++;
            }
        }

        if (tanks > 1) {
            int firstRound = (int) Math.floor((double) amount / (double) tanks);
            for (FluidTankHBM tank : getSendingTanks()) {
                if (tank.getTankType() == type && tank.getPressure() == pressure) {
                    int toRem = Math.min(firstRound, tank.getFill());
                    tank.setFill(tank.getFill() - toRem);
                    amount -= toRem;
                }
            }
        }

        if (amount > 0) {
            for (FluidTankHBM tank : getSendingTanks()) {
                if (tank.getTankType() == type && tank.getPressure() == pressure) {
                    int toRem = (int) Math.min(amount, tank.getFill());
                    tank.setFill(tank.getFill() - toRem);
                    amount -= toRem;
                    if (amount <= 0) break;
                }
            }
        }
    }

    @Override
    default int[] getProvidingPressureRange(FluidTypeHBM type) {
        int lowest = HIGHEST_VALID_PRESSURE;
        int highest = 0;

        for (FluidTankHBM tank : getSendingTanks()) {
            if (tank.getTankType() == type) {
                if (tank.getPressure() < lowest) lowest = tank.getPressure();
                if (tank.getPressure() > highest) highest = tank.getPressure();
            }
        }

        return lowest <= highest ? new int[]{lowest, highest} : DEFAULT_PRESSURE_RANGE;
    }

    @Override
    default long getProviderSpeed(FluidTypeHBM type, int pressure) {
        return 1_000_000_000;
    }

}