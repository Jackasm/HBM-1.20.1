package com.hbm.inventory.container;

import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.tileentity.machine.TileEntityMachineArcWelder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineArcWelder extends AbstractContainerMenu {

    private final TileEntityMachineArcWelder welder;

    public ContainerMachineArcWelder(int windowId, Inventory playerInv, TileEntityMachineArcWelder tile) {
        super(ModContainers.MACHINE_ARC_WELDER.get(), windowId);
        this.welder = tile;

        IItemHandler handler = tile.getItemHandler();

        // Input slots (0-2)
        this.addSlot(new SlotItemHandler(handler, 0, 17, 36));
        this.addSlot(new SlotItemHandler(handler, 1, 35, 36));
        this.addSlot(new SlotItemHandler(handler, 2, 53, 36));

        // Output slot (3)
        this.addSlot(new SlotItemHandler(handler, 3, 107, 36) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // Battery slot (4)
        this.addSlot(new SlotItemHandler(handler, 4, 152, 72));

        // Fluid ID slot (5)
        this.addSlot(new SlotItemHandler(handler, 5, 17, 63) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof IItemFluidIdentifier;
            }
        });

        // Upgrade slots (6-7)
        this.addSlot(new SlotItemHandler(handler, 6, 89, 63) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof ItemMachineUpgrade;
            }
        });
        this.addSlot(new SlotItemHandler(handler, 7, 107, 63) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof ItemMachineUpgrade;
            }
        });

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 122 + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 180));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack rStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            rStack = stack.copy();

            // Если слот принадлежит машине (0-7)
            if (index <= 7) {
                if (!this.moveItemStackTo(stack, 8, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                boolean moved = false;

                // Батареи
                if (rStack.getMaxStackSize() == 1) {
                    if (this.moveItemStackTo(stack, 4, 5, false)) moved = true;
                }
                // Идентификаторы жидкостей
                else if (rStack.getItem() instanceof IItemFluidIdentifier) {
                    if (this.moveItemStackTo(stack, 5, 6, false)) moved = true;
                }
                // Апгрейды
                else if (rStack.getItem() instanceof ItemMachineUpgrade) {
                    if (this.moveItemStackTo(stack, 6, 8, false)) moved = true;
                }
                // Ингредиенты
                else {
                    if (this.moveItemStackTo(stack, 0, 3, false)) moved = true;
                }

                if (!moved) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == rStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return rStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return welder.isUsableByPlayer(player);
    }

    public TileEntityMachineArcWelder getWelder() {
        return welder;
    }
}