package com.hbm.pawn;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Обёртка для {@link Container} в виде {@link IItemHandler}.
 */
public record ContainerWrapper(Container container) implements IItemHandler {

    @Override
    public int getSlots() {
        return container.getContainerSize();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return container.getItem(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        ItemStack current = container.getItem(slot);
        if (!current.isEmpty() && !ItemStack.isSameItem(current, stack)) {
            return stack;
        }

        int space = current.getMaxStackSize() - current.getCount();
        int toInsert = Math.min(stack.getCount(), space);

        if (!simulate) {
            if (current.isEmpty()) {
                container.setItem(slot, stack.split(toInsert));
            } else {
                current.grow(toInsert);
                stack.shrink(toInsert);
            }
        }

        return stack.getCount() > toInsert ? stack.split(stack.getCount() - toInsert) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack current = container.getItem(slot);
        if (current.isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, current.getCount());
        if (!simulate) {
            ItemStack result = current.split(toExtract);
            container.setItem(slot, current);
            return result;
        }
        return current.copy().split(toExtract);
    }

    @Override
    public int getSlotLimit(int slot) {
        return container.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
}