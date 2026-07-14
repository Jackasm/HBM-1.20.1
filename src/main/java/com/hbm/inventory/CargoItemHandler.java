package com.hbm.inventory;

import com.hbm.tileentity.machine.TileEntityCargoContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CargoItemHandler implements IItemHandlerModifiable {

    private final TileEntityCargoContainer tile;
    private final ItemStack[] items; // иконки, количество всегда 1
    private final long[] counts;     // реальное количество
    private static final int SLOT_COUNT = 100;
    private static final int MAX_UNIQUE_TYPES = 100;

    public CargoItemHandler(TileEntityCargoContainer tile) {
        this.tile = tile;
        this.items = new ItemStack[SLOT_COUNT];
        this.counts = new long[SLOT_COUNT];
        Arrays.fill(items, ItemStack.EMPTY);
        Arrays.fill(counts, 0);
    }

    private void markDirty() {
        tile.setChanged();
        if (tile.getLevel() != null && !tile.getLevel().isClientSide) {
            tile.networkPackNT(150);
        }
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (slot < 0 || slot >= SLOT_COUNT) return;
        if (stack.isEmpty()) {
            items[slot] = ItemStack.EMPTY;
            counts[slot] = 0;
        } else {
            ItemStack iconStack = stack.copy();
            iconStack.setCount(1); // для иконки всегда 1
            items[slot] = iconStack;
            counts[slot] = stack.getCount();
        }
        markDirty();
    }

    @Override
    public int getSlots() {
        return SLOT_COUNT;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return ItemStack.EMPTY;
        return items[slot];
    }

    public long getRealCount(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return 0;
        return counts[slot];
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        // упрощённая логика – ищем первый пустой слот или совместимый
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (items[i].isEmpty()) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    counts[i] = copy.getCount();
                    copy.setCount(1);
                    items[i] = copy;
                    markDirty();
                }
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameTags(items[i], stack) && counts[i] + stack.getCount() <= Integer.MAX_VALUE) {
                if (!simulate) {
                    counts[i] += stack.getCount();
                    markDirty();
                }
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < 0 || slot >= SLOT_COUNT || amount <= 0) return ItemStack.EMPTY;
        long current = counts[slot];
        if (current <= 0) return ItemStack.EMPTY;
        int toExtract = (int) Math.min(amount, current);
        ItemStack result = items[slot].copy();
        result.setCount(toExtract);
        if (!simulate) {
            counts[slot] -= toExtract;
            if (counts[slot] <= 0) {
                items[slot] = ItemStack.EMPTY;
            }
            markDirty();
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !stack.isEmpty();
    }

    public void setSlotData(int slot, ItemStack iconStack, long realCount) {
        if (slot < 0 || slot >= SLOT_COUNT) return;
        items[slot] = iconStack.isEmpty() ? ItemStack.EMPTY : iconStack.copy();
        if (!items[slot].isEmpty()) items[slot].setCount(1);
        counts[slot] = realCount;
        markDirty();
    }
}