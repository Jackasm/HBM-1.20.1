package com.hbm.tileentity.network;

import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityFoundryBasin extends TileEntityFoundryCastingBase implements IRenderFoundry {

    public TileEntityFoundryBasin(BlockPos pos, BlockState state) {
        super(ModTileEntity.FOUNDRY_BASIN.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityFoundryBasin te) {
        TileEntityFoundryCastingBase.serverTick(level, pos, state, te);
    }

    @Override
    public int getMoldSize() {
        return 1; // большая форма
    }

    @Override
    public boolean canAcceptPartialFlow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        return false;
    }

    @Override
    public MaterialStack flow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        return stack;
    }

    @Override
    public boolean shouldRender() {
        return this.type != null && this.amount > 0;
    }

    @Override
    public double getFillLevel() {
        return 0.125 + this.amount * 0.75D / this.getCapacity();
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
    @Override public double outHeight() { return 0.875D; }
}