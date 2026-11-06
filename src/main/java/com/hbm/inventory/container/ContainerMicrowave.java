package com.hbm.inventory.container;

import com.hbm.tileentity.machine.TileEntityMicrowave;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMicrowave extends AbstractContainerMenu {

    private final TileEntityMicrowave microwave;

    public ContainerMicrowave(int windowId, Inventory playerInv, TileEntityMicrowave tile) {
        super(ModContainers.MICROWAVE.get(), windowId);
        this.microwave = tile;
        this.addSlot(new SlotItemHandler(tile.getInventory(), 0, 80, 35));
        this.addSlot(new SlotItemHandler(tile.getInventory(), 1, 140, 35) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(tile.getInventory(), 2, 8, 53));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if (index <= 2) {
                // из машины → в игрока
                if (!this.moveItemStackTo(stack, 3, this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                // из игрока → в input (0), иначе в батарею (2)
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    if (!this.moveItemStackTo(stack, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return microwave.getBlockPos().distSqr(player.blockPosition()) <= 64.0;
    }

    public  TileEntityMicrowave getMicrowave() {
        return microwave;
    }
}