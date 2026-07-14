package com.hbm.tileentity.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.energy.IEnergyProvider;
import com.hbm.api.fluid.IFluidCopiable;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.inventory.fluid.trait.FT_Coolable.CoolingType;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TileEntitySteamEngine extends TileEntityLoadedBase implements IEnergyProvider, IFluidStandardTransceiver, IBufPacketReceiver, IConfigurableMachine, IFluidCopiable {

    public long powerBuffer;
    public float rotor;
    public float lastRotor;
    private float syncRotor;
    public FluidTankHBM[] tanks;

    private int turnProgress;
    private float acceleration = 0F;

    public static int steamCap = 2_000;
    public static int ldsCap = 20;
    public static double efficiency = 0.85D;

    public TileEntitySteamEngine(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_STEAM_ENGINE.get(), pos, state);
        tanks = new FluidTankHBM[2];
        tanks[0] = new FluidTankHBM(Fluids.STEAM.get(), steamCap);
        tanks[1] = new FluidTankHBM(Fluids.SPENTSTEAM.get(), ldsCap);
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        // Паровая турбина принимает пар (tanks[0]) и отдаёт отработанный пар (tanks[1])
        if (dir != null && dir != Direction.UP && dir != Direction.DOWN) {
            return type == tanks[0].getTankType() || type == tanks[1].getTankType();
        }
        return false;
    }

    @Override
    public String getConfigName() {
        return "steamengine";
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        steamCap = IConfigurableMachine.grab(obj, "I:steamCap", steamCap);
        ldsCap = IConfigurableMachine.grab(obj, "I:ldsCap", ldsCap);
        efficiency = IConfigurableMachine.grab(obj, "D:efficiency", efficiency);
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("I:steamCap").value(steamCap);
        writer.name("I:ldsCap").value(ldsCap);
        writer.name("D:efficiency").value(efficiency);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            this.powerBuffer = 0;

            tanks[0].setType(Fluids.STEAM.get());
            tanks[1].setType(Fluids.SPENTSTEAM.get());

            FT_Coolable trait = tanks[0].getTankType().getTrait(FT_Coolable.class);
            if (trait != null) {
                double eff = trait.getEfficiency(CoolingType.TURBINE) * efficiency;

                int inputOps = tanks[0].getFill() / trait.amountReq;
                int outputOps = (tanks[1].getMaxFill() - tanks[1].getFill()) / trait.amountProduced;
                int ops = Math.min(inputOps, outputOps);
                tanks[0].setFill(tanks[0].getFill() - ops * trait.amountReq);
                tanks[1].setFill(tanks[1].getFill() + ops * trait.amountProduced);
                this.powerBuffer += (long) (ops * trait.heatEnergy * eff);
            }

            if (powerBuffer > 0) {
                this.acceleration += 0.1F;
            } else {
                this.acceleration -= 0.1F;
            }

            this.acceleration = Mth.clamp(this.acceleration, 0F, 40F);
            this.rotor += this.acceleration;

            if (this.rotor >= 360D) {
                this.rotor -= 360F;
            }

            for (PosDir pos : getConPos()) {
                if (this.powerBuffer > 0) this.tryProvide(level, pos.pos(), pos.dir());
                this.trySubscribe(tanks[0].getTankType(), level, pos.pos(), pos.dir());
                this.sendFluid(tanks[1], level, pos.pos(), pos.dir());
            }

            networkPackNT(150);
        } else {
            this.lastRotor = this.rotor;
            if (this.turnProgress > 0) {
                double d = Mth.wrapDegrees(this.syncRotor - (double) this.rotor);
                this.rotor = (float) ((double) this.rotor + d / (double) this.turnProgress);
                --this.turnProgress;
            } else {
                this.rotor = this.syncRotor;
            }
        }
    }

    protected PosDir[] getConPos() {
        Direction dir = getBlockState().getValue(BlockDummyable.FACING);
        Direction right = dir.getClockWise();
        Direction forward = dir.getOpposite();
        BlockPos basePos = worldPosition.relative(forward, 2);

        return new PosDir[]{
                new PosDir(basePos.relative(right, 1).above(1), forward),
                new PosDir(basePos.relative(right, 2).above(1), forward),
                new PosDir(basePos.relative(right, 3).above(1), forward)
        };
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.powerBuffer = nbt.getLong("powerBuffer");
        this.acceleration = nbt.getFloat("acceleration");
        tanks[0].readFromNBT(nbt, "s");
        tanks[1].readFromNBT(nbt, "w");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("powerBuffer", powerBuffer);
        nbt.putFloat("acceleration", acceleration);
        tanks[0].writeToNBT(nbt, "s");
        tanks[1].writeToNBT(nbt, "w");
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public boolean canConnect(Direction dir) {
        return dir != Direction.UP && dir != Direction.DOWN && dir != null;
    }

    @Override
    public long getPower() {
        return powerBuffer;
    }

    @Override
    public long getMaxPower() {
        return powerBuffer;
    }

    @Override
    public void setPower(long power) {
        this.powerBuffer = power;
    }

    @Override
    public long getProviderSpeed() {
        return powerBuffer;
    }

    @Override
    public void usePower(long power) {
        powerBuffer -= power;
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[]{tanks[1]};
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tanks[0]};
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return tanks;
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeLong(this.powerBuffer);
        buf.writeFloat(this.rotor);
        buf.writeInt(tanks[0].getFill());
        buf.writeInt(tanks[1].getFill());
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.powerBuffer = buf.readLong();
        this.syncRotor = buf.readFloat();
        tanks[0].setFill(buf.readInt());
        tanks[1].setFill(buf.readInt());
        this.rotor = this.syncRotor;
        this.turnProgress = 3;
    }

    public float getVolume(float base) {
        return base;
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return null;
    }
}