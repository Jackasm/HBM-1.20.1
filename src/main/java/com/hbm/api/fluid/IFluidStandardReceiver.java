package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.tank.FluidTankHBM;

/**
 * Standard implementation for fluid receivers with multiple tanks
 */
public interface IFluidStandardReceiver extends IFluidReceiver {

    FluidTankHBM[] getReceivingTanks();

    @Override
    default long getDemand(FluidTypeHBM type, int pressure) {
        long amount = 0;
        for (FluidTankHBM tank : getReceivingTanks()) {
            if (tank.getTankType() == type && tank.getPressure() == pressure) {
                amount += (tank.getMaxFill() - tank.getFill());
            }
        }
        return amount;
    }

    @Override
    default long transferFluid(FluidTypeHBM type, int pressure, long amount) {
        int tanks = 0;
        for (FluidTankHBM tank : getReceivingTanks()) {
            if (tank.getTankType() == type && tank.getPressure() == pressure) {
                tanks++;
            }
        }

        if (tanks > 1) {
            int firstRound = (int) Math.floor((double) amount / (double) tanks);
            for (FluidTankHBM tank : getReceivingTanks()) {
                if (tank.getTankType() == type && tank.getPressure() == pressure) {
                    int toAdd = Math.min(firstRound, tank.getMaxFill() - tank.getFill());
                    tank.setFill(tank.getFill() + toAdd);
                    amount -= toAdd;
                }
            }
        }

        if (amount > 0) {
            for (FluidTankHBM tank : getReceivingTanks()) {
                if (tank.getTankType() == type && tank.getPressure() == pressure) {
                    int toAdd = (int) Math.min(amount, tank.getMaxFill() - tank.getFill());
                    tank.setFill(tank.getFill() + toAdd);
                    amount -= toAdd;
                    if (amount <= 0) break;
                }
            }
        }

        return amount;
    }

    @Override
    default int[] getReceivingPressureRange(FluidTypeHBM type) {
        int lowest = HIGHEST_VALID_PRESSURE;
        int highest = 0;

        for (FluidTankHBM tank : getReceivingTanks()) {
            if (tank.getTankType() == type) {
                if (tank.getPressure() < lowest) lowest = tank.getPressure();
                if (tank.getPressure() > highest) highest = tank.getPressure();
            }
        }

        return lowest <= highest ? new int[]{lowest, highest} : DEFAULT_PRESSURE_RANGE;
    }

    @Override
    default long getReceiverSpeed(FluidTypeHBM type, int pressure) {
        return 1_000_000_000; // Default speed: 1B mB/t
    }
}
