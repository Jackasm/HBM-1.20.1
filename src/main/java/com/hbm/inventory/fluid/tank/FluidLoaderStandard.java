package com.hbm.inventory.fluid.tank;

import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FluidLoaderStandard extends FluidLoadingHandler {

    @Override
    public boolean fillItem(ItemStackHandler handler, int in, int out, FluidTankHBM tank) {
        if (tank.getPressure() != 0) return false;

        ItemStack inputStack = handler.getStackInSlot(in);
        if (inputStack.isEmpty()) return true;

        FluidTypeHBM type = tank.getTankType();
        ItemStack fullContainer = FluidContainerRegistry.getFullContainer(inputStack, type);

        if (fullContainer != null && !fullContainer.isEmpty() && tank.getFill() >= FluidContainerRegistry.getFluidContent(fullContainer, type)) {
            ItemStack outputStack = handler.getStackInSlot(out);

            if (outputStack.isEmpty()) {
                // Выходной слот пуст - просто перемещаем полный контейнер
                tank.setFill(tank.getFill() - FluidContainerRegistry.getFluidContent(fullContainer, type));
                handler.setStackInSlot(out, fullContainer.copy());
                inputStack.shrink(1);
                if (inputStack.isEmpty()) {
                    handler.setStackInSlot(in, ItemStack.EMPTY);
                }
                return true;

            } else if (ItemStack.isSameItemSameTags(outputStack, fullContainer) &&
                    outputStack.getCount() < outputStack.getMaxStackSize()) {
                // Выходной слот содержит такой же предмет и есть место
                tank.setFill(tank.getFill() - FluidContainerRegistry.getFluidContent(fullContainer, type));
                inputStack.shrink(1);
                if (inputStack.isEmpty()) {
                    handler.setStackInSlot(in, ItemStack.EMPTY);
                }
                outputStack.grow(1);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean emptyItem(ItemStackHandler handler, int in, int out, FluidTankHBM tank) {
        ItemStack inputStack = handler.getStackInSlot(in);
        if (inputStack.isEmpty()) return true;

        FluidTypeHBM type = tank.getTankType();
        int amount = FluidContainerRegistry.getFluidContent(inputStack, type);

        if (amount > 0 && tank.getFill() + amount <= tank.getMaxFill()) {
            ItemStack emptyContainer = FluidContainerRegistry.getEmptyContainer(inputStack);
            ItemStack outputStack = handler.getStackInSlot(out);

            if (outputStack.isEmpty()) {
                // Выходной слот пуст - просто перемещаем пустой контейнер
                tank.setFill(tank.getFill() + amount);
                if (emptyContainer != null && !emptyContainer.isEmpty()) {
                    handler.setStackInSlot(out, emptyContainer.copy());
                }
                inputStack.shrink(1);
                if (inputStack.isEmpty()) {
                    handler.setStackInSlot(in, ItemStack.EMPTY);
                }
                return true;

            } else if (emptyContainer != null && !emptyContainer.isEmpty() &&
                    ItemStack.isSameItemSameTags(outputStack, emptyContainer) &&
                    outputStack.getCount() < outputStack.getMaxStackSize()) {
                // Выходной слот содержит такой же пустой контейнер
                tank.setFill(tank.getFill() + amount);
                inputStack.shrink(1);
                if (inputStack.isEmpty()) {
                    handler.setStackInSlot(in, ItemStack.EMPTY);
                }
                outputStack.grow(1);
                return true;

            } else if (emptyContainer == null || emptyContainer.isEmpty()) {
                // Нет пустого контейнера (например, ведро исчезает)
                tank.setFill(tank.getFill() + amount);
                inputStack.shrink(1);
                if (inputStack.isEmpty()) {
                    handler.setStackInSlot(in, ItemStack.EMPTY);
                }
                return true;
            }
        }

        return false;
    }
}