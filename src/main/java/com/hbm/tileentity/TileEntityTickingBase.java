package com.hbm.tileentity;

import com.hbm.inventory.fluid.tank.FluidTankHBM;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileEntityTickingBase extends TileEntityLoadedBase {

    public TileEntityTickingBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract String getInventoryName();

    public int getGaugeScaled(int i, FluidTankHBM tank) {
        if (tank == null || tank.getMaxFill() == 0) return 0;
        return tank.getFill() * i / tank.getMaxFill();
    }


    public abstract void tick();

    @Deprecated
    public void handleButtonPacket(int value, int meta) { }
}