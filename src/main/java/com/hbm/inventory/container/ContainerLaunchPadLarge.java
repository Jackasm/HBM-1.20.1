package com.hbm.inventory.container;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.api.item.IDesignatorItem;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.items.ModItems;
import com.hbm.tileentity.bomb.TileEntityLaunchPadBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerLaunchPadLarge extends AbstractContainerMenu {

    private final TileEntityLaunchPadBase launchpad;
    private final IItemHandler handler;

    public ContainerLaunchPadLarge(int windowId, Inventory invPlayer, TileEntityLaunchPadBase tedf) {
        super(ModContainers.LAUNCH_PAD_LARGE.get(), windowId);

        this.launchpad = tedf;
        this.handler = tedf.getInventory();

        // Missile (0)
        this.addSlot(new SlotItemHandler(handler, 0, 26, 36));
        // Designator (1)
        this.addSlot(new SlotItemHandler(handler, 1, 26, 72));
        // Battery (2)
        this.addSlot(new SlotItemHandler(handler, 2, 107, 90));
        // Fuel in (3)
        this.addSlot(new SlotItemHandler(handler, 3, 125, 90));
        // Fuel out (4) - только извлечение
        this.addSlot(new SlotItemHandler(handler, 4, 125, 108) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
        // Oxidizer in (5)
        this.addSlot(new SlotItemHandler(handler, 5, 143, 90));
        // Oxidizer out (6) - только извлечение
        this.addSlot(new SlotItemHandler(handler, 6, 143, 108) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 154 + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 212));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack resultStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            resultStack = stack.copy();

            if (index <= 6) {
                // Из слотов тайла в инвентарь
                if (!this.moveItemStackTo(stack, 7, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Из инвентаря в слоты тайла
                if (stack.getItem() instanceof IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get()) {
                    if (!this.moveItemStackTo(stack, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (launchpad.isMissileValid(stack)) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() == ModItems.FLUID_BARREL_INFINITE.get()) {
                    if (!this.moveItemStackTo(stack, 3, 4, false)) {
                        if (!this.moveItemStackTo(stack, 5, 6, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (FluidContainerRegistry.getFluidContent(stack, launchpad.tanks[0].getTankType()) > 0) {
                    if (!this.moveItemStackTo(stack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (FluidContainerRegistry.getFluidContent(stack, launchpad.tanks[1].getTankType()) > 0) {
                    if (!this.moveItemStackTo(stack, 5, 6, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() instanceof IDesignatorItem) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return resultStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.launchpad.isUsableByPlayer(player);
    }

    public TileEntityLaunchPadBase getLaunchPad() {
        return launchpad;
    }
}