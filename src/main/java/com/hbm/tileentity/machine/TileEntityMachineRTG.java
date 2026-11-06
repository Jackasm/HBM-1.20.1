package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyProvider;
import com.hbm.config.VersatileConfig;
import com.hbm.inventory.container.ContainerMachineRTG;
import com.hbm.items.machine.ItemRTGPellet;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.RTGUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityMachineRTG extends TileEntityMachineBase implements MenuProvider, IEnergyProvider {

    public int heat;
    public final int heatMax = VersatileConfig.rtgDecay() ? 600 : 200;
    public long power;
    public final long powerMax = 100000;

    public static final int[] SLOT_IO = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };

    private final ItemStackHandler inventory = new ItemStackHandler(15) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof ItemRTGPellet;
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMachineRTG(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_RTG.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        for (Direction dir : Direction.values()) {
            this.tryProvide(level, worldPosition.relative(dir), dir);
        }

        heat = RTGUtil.updateRTGs(inventory, SLOT_IO);

        if (heat > heatMax) heat = heatMax;

        power += heat * 5L;
        if (power > powerMax) power = powerMax;

        networkPackNT(50);
    }

    public void drops() {
        if (level == null || level.isClientSide) return;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        nbt.putInt("heat", heat);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        heat = nbt.getInt("heat");
    }

    public long getPowerScaled(long i) { return (power * i) / powerMax; }
    public int getHeatScaled(int i) { return (heat * i) / heatMax; }
    public boolean hasPower() { return power > 0; }
    public boolean hasHeat() { return RTGUtil.hasHeat(inventory, SLOT_IO); }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return inventoryCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCap.invalidate();
    }

    // IEnergyProvider

    @Override public long getPower() { return power; }
    @Override public long getMaxPower() { return powerMax; }
    @Override public void setPower(long i) { this.power = i; }

    // MenuProvider

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.rtg");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerMachineRTG(id, inv, this);
    }

    public IItemHandler getInventory() {
        return inventory;
    }
}