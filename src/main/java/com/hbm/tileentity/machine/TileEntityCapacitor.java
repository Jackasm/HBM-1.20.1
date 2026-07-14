package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyProvider;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.redstoneoverradio.IRORInfo;
import com.hbm.api.redstoneoverradio.IRORValueProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.MachineCapacitor;
import com.hbm.tileentity.IPersistentNBT;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityCapacitor extends TileEntityLoadedBase implements IEnergyProvider, IEnergyReceiver, IPersistentNBT, IRORValueProvider {

    private long power;
    private long maxPower;
    private long powerReceived;
    private long powerSent;

    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage((int) Math.min(maxPower, Integer.MAX_VALUE)) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            long received = Math.min(maxReceive, maxPower - power);
            if (!simulate) {
                power += received;
                powerReceived += received;
            }
            return (int) received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            long extracted = Math.min(maxExtract, power);
            if (!simulate) {
                power -= extracted;
                powerSent += extracted;
            }
            return (int) extracted;
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(power, Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(maxPower, Integer.MAX_VALUE);
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    });

    public TileEntityCapacitor(BlockPos pos, BlockState state) {
        super(ModTileEntity.CAPACITOR.get(), pos, state);
        this.maxPower = 0;
    }

    public TileEntityCapacitor(BlockPos pos, BlockState state, long maxPower) {
        super(ModTileEntity.CAPACITOR.get(), pos, state);
        this.maxPower = maxPower;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        Direction opp = getBlockState().getValue(MachineCapacitor.FACING);
        Direction dir = opp.getOpposite();

        BlockPos pos = worldPosition.relative(dir);

        boolean didStep = false;
        Direction last = null;

        while (level.getBlockState(pos).getBlock() == ModBlocks.CAPACITOR_BUS.get()) {
            Direction current = level.getBlockState(pos).getValue(MachineCapacitor.FACING);
            if (!didStep) last = current;
            didStep = true;

            if (last != current) {
                pos = null;
                break;
            }

            pos = pos.relative(current);
        }

        // Передаём энергию через capability
        if (pos != null && last != null) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te != null) {
                te.getCapability(ForgeCapabilities.ENERGY, last).ifPresent(handler -> {
                    if (handler.canReceive()) {
                        int toSend = (int) Math.min(getReceiverSpeed(), handler.receiveEnergy((int) getReceiverSpeed(), true));
                        int received = handler.receiveEnergy(toSend, false);
                        power -= received;
                        powerSent += received;
                    }
                    if (handler.canExtract()) {
                        int toExtract = (int) Math.min(getProviderSpeed(), handler.extractEnergy((int) getProviderSpeed(), true));
                        int extracted = handler.extractEnergy(toExtract, false);
                        power += extracted;
                        powerReceived += extracted;
                    }
                });
            }
        }

        // Подписка на соседние блоки
        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(opp));
        if (neighbor != null) {
            neighbor.getCapability(ForgeCapabilities.ENERGY, opp.getOpposite()).ifPresent(handler -> {
                if (handler.canReceive()) {
                    int toSend = (int) Math.min(getReceiverSpeed(), handler.receiveEnergy((int) getReceiverSpeed(), true));
                    int received = handler.receiveEnergy(toSend, false);
                    power -= received;
                    powerSent += received;
                }
                if (handler.canExtract()) {
                    int toExtract = (int) Math.min(getProviderSpeed(), handler.extractEnergy((int) getProviderSpeed(), true));
                    int extracted = handler.extractEnergy(toExtract, false);
                    power += extracted;
                    powerReceived += extracted;
                }
            });
        }

        powerSent = 0;
        powerReceived = 0;

        setChanged();
    }

    public long getPower() { return power; }
    public long getMaxPower() { return maxPower; }
    public long getProviderSpeed() { return maxPower / 300; }
    public long getReceiverSpeed() { return maxPower / 100; }

    public void setPower(long power) {
        this.power = Math.min(power, maxPower);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
        nbt.putLong("maxPower", maxPower);
        nbt.putLong("powerReceived", powerReceived);
        nbt.putLong("powerSent", powerSent);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.power = nbt.getLong("power");
        this.powerReceived = nbt.getLong("powerReceived");
        this.powerSent = nbt.getLong("powerSent");
        // maxPower не загружаем, берём из блока
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        CompoundTag data = new CompoundTag();
        data.putLong("power", power);
        data.putLong("maxPower", maxPower);
        nbt.put(NBT_PERSISTENT_KEY, data);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        CompoundTag data = nbt.getCompound(NBT_PERSISTENT_KEY);
        this.power = data.getLong("power");
        this.maxPower = data.getLong("maxPower");
    }

    @Override
    public String provideRORValue(String name) {
        if ((IRORInfo.PREFIX_VALUE + "fill").equals(name)) {
            return String.valueOf(this.power);
        }
        if ((IRORInfo.PREFIX_VALUE + "fillpercent").equals(name)) {
            return String.valueOf(this.power * 100 / this.maxPower);
        }
        return null;
    }

    @Override
    public String[] getFunctionInfo() {
        return new String[] {
                IRORInfo.PREFIX_VALUE + "fill",
                IRORInfo.PREFIX_VALUE + "fillpercent"
        };
    }
}