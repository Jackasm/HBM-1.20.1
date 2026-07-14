package com.hbm.tileentity.storage;

import com.hbm.tileentity.TileEntityLockableBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TileEntityCrateBase extends TileEntityLockableBase implements Container, MenuProvider {

    protected ItemStack[] slots;
    public String customName;
    public boolean hasSpiders = false;

    public TileEntityCrateBase(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state);
        slots = new ItemStack[size];
        for (int i = 0; i < size; i++) slots[i] = ItemStack.EMPTY;
    }

    // ========== Container ==========
    @Override
    public int getContainerSize() {
        return slots.length;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : slots) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    public IItemHandler getInventory() {
        return new InvWrapper(this);
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        if (slot < 0 || slot >= slots.length) return ItemStack.EMPTY;
        return slots[slot];
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack result = stack.split(amount);
            if (stack.isEmpty()) slots[slot] = ItemStack.EMPTY;
            setChanged();
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            slots[slot] = ItemStack.EMPTY;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (slot >= 0 && slot < slots.length) {
            slots[slot] = stack;
            setChanged();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < slots.length; i++) slots[i] = ItemStack.EMPTY;
        setChanged();
    }

    // ========== IItemHandler capability ==========
    private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    // ========== NBT ==========
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        ListTag list = tag.getList("items", 10);
        slots = new ItemStack[getContainerSize()];
        for (int i = 0; i < slots.length; i++) slots[i] = ItemStack.EMPTY;
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int slot = itemTag.getByte("slot") & 0xFF;
            if (slot >= 0 && slot < slots.length) {
                slots[slot] = ItemStack.of(itemTag);
            }
        }
        hasSpiders = tag.getBoolean("spiders");
        if (tag.contains("name")) customName = tag.getString("name");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag list = new ListTag();
        for (int i = 0; i < slots.length; i++) {
            if (!slots[i].isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("slot", (byte) i);
                slots[i].save(itemTag);
                list.add(itemTag);
            }
        }
        tag.put("items", list);
        tag.putBoolean("spiders", hasSpiders);
        if (customName != null) tag.putString("name", customName);
    }

    // ========== Методы для звуков и пауков ==========
    public void openInventory(Player player) {
        if (level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_OPEN,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        spawnSpiders(player);
    }

    public void closeInventory(Player player) {
        if (level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_CLOSE,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public void fillWithSpiders() {
        this.hasSpiders = true;
    }

    private void spawnSpiders(Player player) {
        if (hasSpiders && level != null && !level.isClientSide) {
            // Спавн пауков – портировать нужно отдельно, можно оставить заглушку или реализовать
            // Для простоты пока пропустим
            hasSpiders = false;
            setChanged();
        }
    }

    // ========== MenuProvider ==========
    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.crate");
    }

    public boolean canExtractItem(int i, ItemStack itemStack, int j) {
        return !this.isLocked();
    }
}
