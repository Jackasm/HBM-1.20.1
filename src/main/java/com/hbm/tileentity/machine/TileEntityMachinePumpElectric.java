package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TileEntityMachinePumpElectric extends TileEntityMachinePumpBase implements IEnergyReceiver {

    public long power;
    public static final long maxPower = 10_000;

    public TileEntityMachinePumpElectric(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_PUMP_ELECTRIC.get(), pos, state);
        this.water = new FluidTankHBM(Fluids.WATER.get(), electricSpeed * 100);
    }

    @Override
    public void tick() {
        if (!Objects.requireNonNull(level).isClientSide) {
            if (level.getGameTime() % 20 == 0) {
                for (PosDir pos : getConPos()) {
                    this.trySubscribe(level, pos.pos(), pos.dir());
                }
            }
        }
        super.tick();
    }

    @Override
    protected boolean canOperate() {
        return power >= 1_000 && water.getFill() < water.getMaxFill();
    }

    @Override
    protected void operate() {
        this.power -= 1_000;
        water.setFill(Math.min(water.getFill() + electricSpeed, water.getMaxFill()));
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(this.power);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.power = buf.readLong();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.power = nbt.getLong("power");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
    }

    @Override
    public long getPower() {
        return power;
    }

    @Override
    public long getMaxPower() {
        return maxPower;
    }

    @Override
    public void setPower(long power) {
        this.power = power;
    }

    @Override
    public long getReceiverSpeed() {
        return 10_000;
    }

    @Override
    public long transferPower(long power) {
        long newPower = this.power + power;
        if (newPower <= maxPower) {
            this.power = newPower;
            return 0;
        }
        long received = maxPower - this.power;
        this.power = maxPower;
        return power - received;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void playClientSound() {
        if (level == null) return;
        level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                net.minecraft.sounds.SoundEvents.PLAYER_SPLASH, net.minecraft.sounds.SoundSource.BLOCKS,
                1.0F, 0.5F, false);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }
}