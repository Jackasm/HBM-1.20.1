package com.hbm.inventory.fluid.tank;

import com.hbm.api.fluid.IFillableItem;
import com.hbm.handler.ArmorModHandler;
import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FluidLoaderFillableItem extends FluidLoadingHandler {

    @Override
    public boolean fillItem(ItemStackHandler handler, int in, int out, FluidTankHBM tank) {
        ItemStack stack = handler.getStackInSlot(in);

        if (stack.isEmpty()) return false;

        int oldFill = tank.getFill();

        if (fill(stack, tank)) {
            int newFill = tank.getFill();
            if (newFill < oldFill) {
                // Жидкость была потрачена

                // Проверяем, заполнен ли предмет полностью
                if (stack.getItem() instanceof IFillableItem fillable && fillable.isFull(stack)) {
                    // Если предмет полный, перемещаем его в выходной слот
                    if (handler.getStackInSlot(out).isEmpty()) {
                        handler.setStackInSlot(out, stack.copy());
                        handler.setStackInSlot(in, ItemStack.EMPTY);
                    }
                }
                return true;
            }
        }

        return false;
    }

    public boolean fill(ItemStack stack, FluidTankHBM tank) {

        if (tank.getPressure() != 0) return false;

        if (stack.isEmpty()) return false;

        FluidTypeHBM type = tank.getTankType();

        // Обработка арморных модов
        if (stack.getItem() instanceof ArmorItem) {
            ItemStack[] mods = ArmorModHandler.pryMods(stack);
            for (ItemStack mod : mods) {
                if (mod != null && !mod.isEmpty() && mod.getItem() instanceof IFillableItem) {
                    fill(mod, tank);
                }
            }
        }

        if (!(stack.getItem() instanceof IFillableItem fillable)) return false;

        if (fillable.acceptsFluid(type, stack)) {
            int oldFill = tank.getFill();
            int newFill = fillable.tryFill(type, tank.getFill(), stack);
            tank.setFill(newFill);
            return newFill < oldFill;
        }

        return false;
    }

    @Override
    public boolean emptyItem(ItemStackHandler handler, int in, int out, FluidTankHBM tank) {
        ItemStack stack = handler.getStackInSlot(in);
        return empty(stack, tank);
    }

    public boolean empty(ItemStack stack, FluidTankHBM tank) {

        FluidTypeHBM type = tank.getTankType();

        if (stack.getItem() instanceof ArmorItem) {
            ItemStack[] mods = ArmorModHandler.pryMods(stack);
            for (ItemStack mod : mods) {
                if (mod != null && !mod.isEmpty() && mod.getItem() instanceof IFillableItem) {
                    empty(mod, tank);
                }
            }
        }

        if (!(stack.getItem() instanceof IFillableItem fillable)) return false;

        if (fillable.providesFluid(type, stack)) {
            tank.setFill(tank.getFill() + fillable.tryEmpty(type, tank.getMaxFill() - tank.getFill(), stack));
        }

        return tank.getFill() == tank.getMaxFill();
    }
}