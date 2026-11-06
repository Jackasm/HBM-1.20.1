package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityMachineMiniRTG extends TileEntityLoadedBase implements IEnergyProvider {

    public long power;

    public TileEntityMachineMiniRTG(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_MINI_RTG.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        power += getOutput();
        if (power > getMaxPower()) power = getMaxPower();

        for (Direction dir : Direction.values()) {
            this.tryProvide(level, worldPosition.relative(dir), dir);
        }

        setChanged();
    }

    public long getOutput() {
        return this.getBlockState().getBlock() == ModBlocks.MACHINE_POWERRTG.get() ? 2500 : 700;
    }

    @Override
    public long getMaxPower() {
        return this.getBlockState().getBlock() == ModBlocks.MACHINE_POWERRTG.get() ? 50000 : 1400;
    }

    @Override public long getPower() { return power; }
    @Override public void setPower(long i) { power = i; }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        power = nbt.getLong("power");
    }
}