package com.hbm.inventory;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class FluidStackHBM {

    public FluidTypeHBM type;
    public int fill;
    public int pressure;

    public FluidStackHBM(int fill, FluidTypeHBM type) {
        this.fill = fill;
        this.type = type;
    }

    public FluidStackHBM(FluidTypeHBM type, int fill) {
        this(type, fill, 0);
    }

    public FluidStackHBM(FluidTypeHBM type, int fill, int pressure) {
        this.fill = fill;
        this.type = type;
        this.pressure = pressure;
    }

    public FluidStackHBM copy() {
        return new FluidStackHBM(type, fill, pressure);
    }

    public boolean isFluidEqual(FluidStackHBM other) {
        return other != null && type == other.type;
    }

    public boolean containsFluid(FluidStackHBM other) {
        return isFluidEqual(other) && fill >= other.fill;
    }

    public void writeToNBT(CompoundTag nbt, String key) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", type.getName());
        tag.putInt("fill", fill);
        tag.putInt("pressure", pressure);
        nbt.put(key, tag);
    }

    public static FluidStackHBM readFromNBT(CompoundTag nbt, String key) {
        if (!nbt.contains(key)) return null;
        CompoundTag tag = nbt.getCompound(key);
        FluidTypeHBM type = Fluids.fromName(tag.getString("type"));
        int fill = tag.getInt("fill");
        int pressure = tag.getInt("pressure");
        return new FluidStackHBM(type, fill, pressure);
    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeInt(Fluids.getID(type));
        buf.writeInt(fill);
        buf.writeInt(pressure);
    }

    public static FluidStackHBM deserialize(FriendlyByteBuf buf) {
        int typeId = buf.readInt();
        int fill = buf.readInt();
        int pressure = buf.readInt();
        return new FluidStackHBM(Fluids.fromID(typeId), fill, pressure);
    }

    @Override
    public String toString() {
        return String.format("FluidStackHBM[%s, %d mB, %d PU]", type.getName(), fill, pressure);
    }
}