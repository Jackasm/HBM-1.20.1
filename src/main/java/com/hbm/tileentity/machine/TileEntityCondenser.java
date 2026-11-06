package com.hbm.tileentity.machine;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.tileentity.*;



import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityCondenser extends TileEntityLoadedBase
        implements IFluidStandardTransceiver,
        IConfigurableMachine, IBufPacketReceiver, IFluidCopiable {

    public int age = 0;
    public FluidTankHBM[] tanks;

    public int waterTimer = 0;
    protected int throughput;

    // Configurable values
    public static int inputTankSize = 100;
    public static int outputTankSize = 100;

    public TileEntityCondenser(BlockPos pos, BlockState state) {
        super(ModTileEntity.CONDENSER.get(), pos, state);
        tanks = new FluidTankHBM[2];
        tanks[0] = new FluidTankHBM(Fluids.SPENTSTEAM.get(), inputTankSize);
        tanks[1] = new FluidTankHBM(Fluids.WATER.get(), outputTankSize);
    }

    // === IConfigurableMachine ===

    @Override
    public String getConfigName() {
        return "condenser";
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return true;
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        inputTankSize = IConfigurableMachine.grab(obj, "I:inputTankSize", inputTankSize);
        outputTankSize = IConfigurableMachine.grab(obj, "I:outputTankSize", outputTankSize);
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("I:inputTankSize").value(inputTankSize);
        writer.name("I:outputTankSize").value(outputTankSize);
    }

    // === Tick ===

    public void tick() {
        if (level == null || level.isClientSide) return;

        age++;
        if (age >= 2) age = 0;

        if (this.waterTimer > 0) this.waterTimer--;

        int convert = Math.min(tanks[0].getFill(), tanks[1].getMaxFill() - tanks[1].getFill());
        this.throughput = convert;

        if (extraCondition(convert)) {
            tanks[0].setFill(tanks[0].getFill() - convert);
            if (convert > 0) this.waterTimer = 20;
            tanks[1].setFill(tanks[1].getFill() + convert);
            postConvert(convert);
        }

        this.subscribeToAllAround(tanks[0].getTankType(), this);
        for (Direction dir : Direction.values()) {
            this.tryProvide(tanks[1], level, worldPosition.relative(dir), dir);
        }

        networkPackNT(150);
    }

    // === Хуки для наследников ===

    public void packExtra(CompoundTag data) { }
    public boolean extraCondition(int convert) { return true; }
    public void postConvert(int convert) { }

    // === Синхронизация ===

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        this.tanks[0].serialize(buf);
        this.tanks[1].serialize(buf);
        buf.writeByte(this.waterTimer);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.tanks[0].deserialize(buf);
        this.tanks[1].deserialize(buf);
        this.waterTimer = buf.readByte();
    }

    // === NBT ===

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        tanks[0].readFromNBT(nbt, "water");
        tanks[1].readFromNBT(nbt, "steam");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        tanks[0].writeToNBT(nbt, "water");
        tanks[1].writeToNBT(nbt, "steam");
    }

    // === IFluidStandardTransceiverMK2 ===

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[] { tanks[1] };
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[] { tanks[0] };
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return tanks;
    }


    // === IFluidCopiable ===

    @Override
    public FluidTankHBM getTankToPaste() {
        return null;
    }
}