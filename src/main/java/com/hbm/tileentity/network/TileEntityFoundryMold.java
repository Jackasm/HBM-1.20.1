package com.hbm.tileentity.network;

import com.hbm.inventory.material.NTMMaterial;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityFoundryMold extends TileEntityFoundryCastingBase implements IRenderFoundry {

    public TileEntityFoundryMold(BlockPos pos, BlockState state) {
        super(ModTileEntity.FOUNDRY_MOLD.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityFoundryMold te) {
        TileEntityFoundryCastingBase.serverTick(level, pos, state, te);
    }

    @Override
    public int getMoldSize() {
        return 0; // маленькая форма
    }

    @Override
    public boolean shouldRender() {
        return this.type != null && this.amount > 0;
    }

    @Override
    public double getFillLevel() {
        return 0.125 + this.amount * 0.25D / this.getCapacity();
    }

    @Override
    public NTMMaterial getMat() {
        return this.type;
    }

    @Override public double minX() { return 0.125D; }
    @Override public double maxX() { return 0.875D; }
    @Override public double minZ() { return 0.125D; }
    @Override public double maxZ() { return 0.875D; }
    @Override public double moldHeight() { return 0.13D; }
    @Override public double outHeight() { return 0.25D; }
}