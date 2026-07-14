package com.hbm.tileentity.machine;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TileEntityMachinePumpSteam extends TileEntityMachinePumpBase {

    public FluidTankHBM steam;
    public FluidTankHBM lps;

    public TileEntityMachinePumpSteam(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_PUMP_STEAM.get(), pos, state);
        this.water = new FluidTankHBM(Fluids.WATER.get(), steamSpeed * 100);
        this.steam = new FluidTankHBM(Fluids.STEAM.get(), 1_000);
        this.lps = new FluidTankHBM(Fluids.SPENTSTEAM.get(), 10);
    }

    @Override
    public void tick() {
        if (!Objects.requireNonNull(level).isClientSide) {
            for (PosDir pos : getConPos()) {
                this.trySubscribe(steam.getTankType(), level, pos.pos(), pos.dir());
                if (lps.getFill() > 0) {
                    this.sendFluid(lps, level, pos.pos(), pos.dir());
                }
            }
        }
        super.tick();
    }

    @Override
    protected boolean canOperate() {
        return steam.getFill() >= 100 && lps.getMaxFill() - lps.getFill() > 0 && water.getFill() < water.getMaxFill();
    }

    @Override
    protected void operate() {
        steam.setFill(steam.getFill() - 100);
        lps.setFill(lps.getFill() + 1);
        water.setFill(Math.min(water.getFill() + steamSpeed, water.getMaxFill()));
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        steam.serialize(buf);
        lps.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        steam.deserialize(buf);
        lps.deserialize(buf);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (steam == null) {
            steam = new FluidTankHBM(Fluids.STEAM.get(), 1_000);
        }
        if (lps == null) {
            lps = new FluidTankHBM(Fluids.SPENTSTEAM.get(), 10);
        }
        steam.readFromNBT(nbt, "steam");
        lps.readFromNBT(nbt, "lps");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        steam.writeToNBT(nbt, "steam");
        lps.writeToNBT(nbt, "lps");
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{water, steam, lps};
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[]{water, lps};
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{steam};
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return (dir != null && type == water.getTankType()) || (dir != null && type == steam.getTankType()) || (dir != null && type == lps.getTankType());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void playClientSound() {
        if (level == null) return;
        level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                ModSounds.STEAM_ENGINE_OPERATE.get(), SoundSource.BLOCKS,
                0.5F, 0.75F, false);
        level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                net.minecraft.sounds.SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS,
                1.0F, 0.5F, false);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return LazyOptional.of(() -> new CombinedFluidHandler(water, steam, lps)).cast();
        }
        return super.getCapability(cap, side);
    }

    private static class CombinedFluidHandler implements IFluidHandler {
        private final FluidTankHBM[] tanks;

        public CombinedFluidHandler(FluidTankHBM... tanks) {
            this.tanks = tanks;
        }

        @Override
        public int getTanks() {
            return tanks.length;
        }

        @Override
        public net.minecraftforge.fluids.@NotNull FluidStack getFluidInTank(int tank) {
            return tanks[tank].getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return tanks[tank].getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, net.minecraftforge.fluids.@NotNull FluidStack stack) {
            return tanks[tank].isFluidValid(stack);
        }

        @Override
        public int fill(net.minecraftforge.fluids.FluidStack resource, FluidAction action) {
            for (FluidTankHBM tank : tanks) {
                if (tank.isFluidValid(resource) && tank.getFluidAmount() < tank.getCapacity()) {
                    return tank.fill(resource, action);
                }
            }
            return 0;
        }

        @Override
        public @NotNull net.minecraftforge.fluids.FluidStack drain(net.minecraftforge.fluids.FluidStack resource, FluidAction action) {
            for (FluidTankHBM tank : tanks) {
                if (tank.getFluid().getFluid() == resource.getFluid() && tank.getFluidAmount() > 0) {
                    return tank.drain(resource.getAmount(), action);
                }
            }
            return net.minecraftforge.fluids.FluidStack.EMPTY;
        }

        @Override
        public @NotNull net.minecraftforge.fluids.FluidStack drain(int maxDrain, FluidAction action) {
            for (FluidTankHBM tank : tanks) {
                if (tank.getFluidAmount() > 0) {
                    return tank.drain(maxDrain, action);
                }
            }
            return net.minecraftforge.fluids.FluidStack.EMPTY;
        }
    }
}