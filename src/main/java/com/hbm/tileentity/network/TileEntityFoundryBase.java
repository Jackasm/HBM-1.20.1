package com.hbm.tileentity.network;

import com.hbm.api.block.ICrucibleAcceptor;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class TileEntityFoundryBase extends TileEntityLoadedBase implements ICrucibleAcceptor {

    public NTMMaterial type;
    protected NTMMaterial lastType;
    public int amount;
    protected int lastAmount;

    public TileEntityFoundryBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityFoundryBase te) {
        if (te.lastType != te.type || te.lastAmount != te.amount) {
            te.lastType = te.type;
            te.lastAmount = te.amount;
            te.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.type = Mats.matById.get(nbt.getInt("type"));
        this.amount = nbt.getInt("amount");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("type", this.type == null ? -1 : this.type.id);
        nbt.putInt("amount", this.amount);
    }

    public abstract int getCapacity();

    public boolean standardCheck(Level level, BlockPos pos, MaterialStack stack, Direction side) {
        if (this.type != null && this.type != stack.material && this.amount > 0) return false;
        return this.amount < this.getCapacity();
    }

    public MaterialStack standardAdd(Level level, BlockPos pos, MaterialStack stack, Direction side) {
        this.type = stack.material;
        int space = this.getCapacity() - this.amount;
        int toAdd = Math.min(stack.amount, space);
        this.amount += toAdd;
        this.setChanged();
        this.networkPackNT(20);

        int leftover = stack.amount - toAdd;
        if (leftover > 0) {
            return new MaterialStack(stack.material, leftover);
        }
        return null;
    }

    @Override
    public boolean canAcceptPartialFlow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        return this.standardCheck(level, pos, stack, side);
    }

    @Override
    public MaterialStack flow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        return standardAdd(level, pos, stack, side);
    }

    @Override
    public boolean canAcceptPartialPour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        if (side != Direction.UP) return false;
        return this.standardCheck(level, pos, stack, side);
    }

    @Override
    public MaterialStack pour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        return standardAdd(level, pos, stack, side);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(this.type != null ? this.type.id : -1);
        buf.writeInt(this.amount);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        int typeId = buf.readInt();
        this.type = typeId >= 0 ? Mats.matById.get(typeId) : null;
        this.amount = buf.readInt();
    }
}