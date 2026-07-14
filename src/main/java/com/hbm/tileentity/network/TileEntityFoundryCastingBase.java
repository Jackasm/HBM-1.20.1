package com.hbm.tileentity.network;

import com.hbm.items.machine.ItemMold;
import com.hbm.inventory.material.Mats.MaterialStack;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TileEntityFoundryCastingBase extends TileEntityFoundryBase {

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public int cooloff = 100;

    public TileEntityFoundryCastingBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityFoundryCastingBase te) {
        TileEntityFoundryBase.serverTick(level, pos, state, te);

        if (te.amount > te.getCapacity()) te.amount = te.getCapacity();
        if (te.amount == 0) te.type = null;

        ItemMold.MoldType mold = te.getInstalledMold();
        ItemStack outSlot = te.inventory.getStackInSlot(1);
        if (mold != null && te.amount == te.getCapacity() && outSlot.isEmpty()) {
            te.cooloff--;
            if (te.cooloff <= 0) {
                te.amount = 0;
                ItemStack out = mold.getOutput(te.type);
                if (out != null) te.inventory.setStackInSlot(1, out.copy());
                te.cooloff = 200;
                te.setChanged();
                te.networkPackNT(20);
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.0F);
            }
        } else {
            te.cooloff = 200;
        }
    }

    public ItemMold.MoldType getInstalledMold() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMold)) return null;
        return ItemMold.getMoldType(stack);
    }

    @Override
    public int getCapacity() {
        ItemMold.MoldType mold = this.getInstalledMold();
        return mold == null ? 0 : mold.getCost();
    }

    @Override
    public boolean standardCheck(Level level, BlockPos pos, MaterialStack stack, Direction side) {
        boolean superCheck = super.standardCheck(level, pos, stack, side);
        ItemMold.MoldType mold = this.getInstalledMold();

        if (!superCheck) return false;
        if (!inventory.getStackInSlot(1).isEmpty()) return false;
        if (mold == null) return false;
        return mold.getOutput(stack.material) != null;
    }

    public abstract int getMoldSize();

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putInt("cooloff", cooloff);
        cooloff = nbt.getInt("cooloff");
        if (cooloff <= 0) cooloff = 200;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> inventory).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            buf.writeBoolean(!stack.isEmpty());
            if (!stack.isEmpty()) {
                CompoundTag tag = stack.save(new CompoundTag());
                byte[] bytes = tag.toString().getBytes();
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
            }
        }
        buf.writeInt(cooloff);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (buf.readBoolean()) {
                int length = buf.readInt();
                byte[] bytes = new byte[length];
                buf.readBytes(bytes);
                try {
                    CompoundTag tag = net.minecraft.nbt.TagParser.parseTag(new String(bytes));
                    inventory.setStackInSlot(i, ItemStack.of(tag));
                } catch (Exception e) {
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
            } else {
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        cooloff = buf.readInt();
    }
}